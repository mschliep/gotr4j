package net.java.gotr4j.group.state;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.group.event.ReceivedEncryptedGroupMessageEvent;
import net.java.gotr4j.io.QueryMessage;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.gotr.event.SendSecretEvent;

import net.java.gotr4j.group.event.GotrQueryEvent;
import net.java.gotr4j.group.event.SecretReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareSecretState extends GotrState {
    private static final Logger logger = LoggerFactory.getLogger(ShareSecretState.class);

    private final UserKeys secrets = new UserKeys();
    private byte[] groupVerificationKey;
    private KeyPair signingKeyPair;

    @Override
    protected void handleSecretReceivedEvent(GotrContext context, SecretReceivedEvent event) throws Exception {
        //signingKeyPair = context.getCrypto().generateSigningKeyPair();

        if(!Arrays.equals(groupVerificationKey, event.getGroupVerificationKey())){
            throw new GotrException("Groups do not match!!!");
        };

        secrets.put(event.getRemoteUser(), event.getSecret(),
                context.getCrypto().getSigningKeyFactory().generatePublic(event.getSigningPubKey()));


        if(secrets.size() == context.getGroupSize()){
            context.setNextState(new AwaitingBroadcastState(secrets, signingKeyPair, true));
        }
    }

    @Override
    protected void handleGotrQueryEvent(GotrContext context, GotrQueryEvent event) throws Exception {
        if(!context.getLocalUser().equals(event.getUser())){
            P2pContext p2pContext = context.getP2pContext(event.getUser());
            p2pContext.handelEvent(event);
        }
        else if(context.getGroupSize() == 1){
            context.setNextState(new AwaitingBroadcastState(secrets, signingKeyPair, true));
        }
    }

    @Override
    protected void handleReceivedEncryptedGroupMessageEvent(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException, NoSuchAlgorithmException {
        context.deferEvent(event);
    }

    @Override
    public void onEntry(GotrContext context) throws Exception {
        context.getSessionHost().stateChanged(GotrSessionState.SETUP);
        context.getOutingMessages().clear();

        byte[] secret = context.getCrypto().generateSecret();
        signingKeyPair = context.getCrypto().generateSigningKeyPair();
        secrets.put(context.getLocalUser(), secret, signingKeyPair.getPublic());

        List<GotrUser> users = new ArrayList<>();
        users.add(context.getLocalUser());
        for(P2pContext p2pContext: context.getP2pContexts()){
            users.add(p2pContext.getRemoteUser());
        }

        groupVerificationKey = context.getCrypto().computeGroupVerification(context.getSessionID(), users);

        SendSecretEvent sendSecretEvent = new SendSecretEvent(secret, signingKeyPair.getPublic(), groupVerificationKey);
        for (P2pContext p2pContext : context.getP2pContexts()) {
            p2pContext.handelEvent(sendSecretEvent);
        }

        context.getSessionHost().sendBroadcast(new QueryMessage().encode());
    }

    @Override
    public GotrSessionState getSessionState() {
        return GotrSessionState.SETUP;
    }


}
