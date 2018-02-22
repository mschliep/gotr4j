package net.java.gotr4j.group.state;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.crypto.Broadcast;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.group.event.DigestReceivedEvent;
import net.java.gotr4j.group.event.UserBroadcastEvent;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.gotr.event.SendDigestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class ShareDigestState extends GotrState {
    private static final Logger logger = LoggerFactory.getLogger(ShareDigestState.class);

    private final Broadcast broadcast;

    private final UserKeys secrets;
    private final KeyPair signingKeyPair;

    private final Map<GotrUser, byte[]> signatures = new HashMap<GotrUser, byte[]>();

    private final UserKeys nextSecrets = new UserKeys();
    private KeyPair nextKeyPair;

    public ShareDigestState(Broadcast broadcast, KeyPair signingKeyPair, UserKeys secrets) {
        this.broadcast = broadcast;
        this.signingKeyPair = signingKeyPair;
        this.secrets = secrets;
    }

    @Override
    protected void handleDigestReceivedEvent(GotrContext context, DigestReceivedEvent event) throws InvalidKeySpecException {
        signatures.put(event.getRemoteUser(), event.getSignature());

        nextSecrets.put(event.getRemoteUser(), event.getSecret(),
                context.getCrypto().getSigningKeyFactory().generatePublic(event.getSigningPubKey()));

        if(nextSecrets.size() == context.getGroupSize()){
            context.getSessionHost().handleBroadcast(broadcast.getSender(), broadcast.getText());
            context.setNextState(new VerifyDigestState(broadcast, signatures, secrets, nextSecrets, nextKeyPair));
        }

    }

    @Override
    public void onEntry(GotrContext context) throws Exception {
        byte[] nextSecret = context.getCrypto().generateSecret();
        nextKeyPair = context.getCrypto().generateSigningKeyPair();
        nextSecrets.put(context.getLocalUser(), nextSecret, nextKeyPair.getPublic());

        Signature signature = Signature.getInstance(context.getCrypto().SIGNING_ALGORITHM);
        signature.initSign(signingKeyPair.getPrivate());
        signature.update(broadcast.getDigest());

        byte[] sign = signature.sign();

        signatures.put(context.getLocalUser(), sign);

        SendDigestEvent event = new SendDigestEvent(broadcast.getDigest(), sign, nextSecret, nextKeyPair.getPublic());
        for(P2pContext p2pContext: context.getP2pContexts()){
            p2pContext.handelEvent(event);
        }
    }

    @Override
    protected void handleUserBroadcastEvent(GotrContext context, UserBroadcastEvent event) throws Exception {
        context.getOutingMessages().add(event.getBroadcast());
    }

    @Override
    public GotrSessionState getSessionState() {
        return GotrSessionState.SECURE;
    }
}
