package net.java.gotr4j.group.state;

import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.crypto.Broadcast;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.group.event.*;
import net.java.gotr4j.io.EncryptedGroupMessage;
import net.java.gotr4j.io.GotrInputStream;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.io.RefreshKeysMessage;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedEncryptedP2pMessageEvent;
import net.java.gotr4j.p2p.gotr.state.SendSecretState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class AwaitingBroadcastState extends GotrState{
    private static final Logger logger = LoggerFactory.getLogger(AwaitingBroadcastState.class);

    private final UserKeys secrets;
    private final KeyPair signingKeyPair;
    private final boolean sessionStateChanged;

    private String sentMessage = null;

    public AwaitingBroadcastState(UserKeys secrets, KeyPair signingKeyPair, boolean sessionStateChanged) {
        this.secrets = secrets;
        this.signingKeyPair = signingKeyPair;
        this.sessionStateChanged = sessionStateChanged;
    }

    @Override
    protected void handleReceivedEncryptedGroupMessageEvent(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException {
        if(context.getLocalUser().equals(event.getUser())){
            handleSentMessage(context, event);
        }
        else{
            handleReceivedMessage(context, event);
        }
    }

    private void handleSentMessage(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException {
        Broadcast broadcast = null;
        byte[] digest = context.getCrypto().hash(event.getBytes());
        if(sentMessage != null && sentMessage.equals(event.getEncodedMessage())){
            broadcast = new Broadcast(context.getOutingMessages().peek(), context.getLocalUser(), digest);
        }
        else{
            broadcast = new Broadcast(null, event.getUser(), digest);
        }

        if(context.getGroupSize() == 1) {
            if(broadcast.getText() != null){
                context.getSessionHost().handleBroadcast(broadcast.getSender(), broadcast.getText());
                context.getSessionHost().broadcastConfirmed(broadcast.getSender(), broadcast.getText());
                context.getOutingMessages().poll();
            }

            byte[] secret = context.getCrypto().generateSecret();
            KeyPair keyPair = context.getCrypto().generateSigningKeyPair();
            UserKeys userKeys = new UserKeys();
            userKeys.put(context.getLocalUser(), secret, keyPair.getPublic());

            context.setNextState(new AwaitingBroadcastState(userKeys, keyPair, false));
        }
        else{
            context.setNextState(new ShareDigestState(broadcast, signingKeyPair, secrets));
        }
    }

    private void handleReceivedMessage(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException {
        try {
            byte[][] keys = context.getCrypto().generateKeys(event.getUser(), secrets);

            if(!event.checkMac(keys[1])){
                throw new GotrException("Invalid MAC.");
            }

            event.decrypt(keys[0]);

            GotrInputStream input = new GotrInputStream(event.getPlaintext());
            String plaintext = input.readString();

            byte[] digest = context.getCrypto().hash(event.getBytes());

            context.setNextState(new ShareDigestState(new Broadcast(plaintext, event.getUser(), digest), signingKeyPair, secrets));
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleUserRefreshKeysEvent(GotrContext context, UserRefreshKeysEvent event) throws IOException, GotrException {
        context.getSessionHost().sendBroadcast(new RefreshKeysMessage(context.getSessionID()).encode());
    }

    @Override
    protected void handleRefreshKeysEvent(GotrContext context, RefreshKeysEvent event) {
        context.setNextState(new SendSecretState());
        for(P2pContext p2pContext: context.getP2pContexts()){
            p2pContext.handelEvent(event);
        }
    }

    @Override
    protected void handleUserBroadcastEvent(GotrContext context, UserBroadcastEvent event) throws Exception {
        context.getOutingMessages().add(event.getBroadcast());
        send(event.getBroadcast(), context);
    }

    @Override
    public void onEntry(GotrContext context) throws Exception {
        if(sessionStateChanged) {
            context.getSessionHost().stateChanged(GotrSessionState.SECURE);
        }
        String message = context.getOutingMessages().peek();
        if(message != null){
            send(message, context);
        }
    }

    private void send(String message, GotrContext context) throws GotrException, NoSuchAlgorithmException, IOException {
        if(sentMessage != null){
            return;
        }
        byte[][] keys = context.getCrypto().generateKeys(context.getLocalUser(), secrets);

        GotrOutputStream out = new GotrOutputStream();
        out.writeString(message);

        EncryptedGroupMessage msg = new EncryptedGroupMessage(context.getSessionID(), out.getBytes(), keys[0], keys[1],
                context.getCrypto());

        sentMessage = msg.encode();
        context.getSessionHost().sendBroadcast(sentMessage);
    }

    @Override
    public GotrSessionState getSessionState() {
        return GotrSessionState.SECURE;
    }
}
