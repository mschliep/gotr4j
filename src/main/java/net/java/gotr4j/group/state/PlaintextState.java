package net.java.gotr4j.group.state;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.group.event.RemoveUserEvent;
import net.java.gotr4j.group.event.UserBroadcastEvent;
import net.java.gotr4j.group.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;

public class PlaintextState extends GotrState {

    private static final Logger logger = LoggerFactory.getLogger(PlaintextState.class);

    @Override
    protected void handleRemoveUserEvent(GotrContext context, RemoveUserEvent event) {
        context.removeP2pContext(event.getUser());
    }

    @Override
    protected void handleAddUserEvent(GotrContext context, AddUserEvent event) {
        context.addP2pContext(event.getUser(), event.getP2pContext());
    }

    @Override
    protected void handleUserStartEvent(GotrContext context, UserStartEvent event) throws Exception {
        if(context.getGroupSize() == 1){
            byte[] secret = context.getCrypto().generateSecret();
            KeyPair keyPair = context.getCrypto().generateSigningKeyPair();
            UserKeys userKeys = new UserKeys();
            userKeys.put(context.getLocalUser(), secret, keyPair.getPublic());

            context.setNextState(new AwaitingBroadcastState(userKeys, keyPair, true));
        }
        else {
            context.setNextState(new ShareSecretState());
        }
    }

    @Override
    protected void handlePlaintextMessageEvent(GotrContext context, PlaintextMessageEvent event){
        context.getSessionHost().handleBroadcast(event.getSource(), event.getContent());
    }

    @Override
    protected void handleUserBroadcastEvent(GotrContext context, UserBroadcastEvent event){
        context.getSessionHost().sendBroadcast(event.getBroadcast());
    }

    @Override
    protected void handleGotrQueryEvent(GotrContext context, GotrQueryEvent event) throws Exception {
        if(!context.getLocalUser().equals(event.getUser())) {
            P2pContext p2pContext = context.getOrCreateP2pContext(event.getUser());
            p2pContext.handelEvent(event);
            context.setNextState(new ShareSecretState());
        }
    }

    @Override
    protected void handleUserFinishEvent(GotrContext context, UserFinishEvent event) throws Exception {
    }

    @Override
    protected void handleFinishEvent(GotrContext context, FinishEvent event) throws Exception {
    }

    @Override
    public GotrSessionState getSessionState() {
        return GotrSessionState.PLAINTEXT;
    }

    @Override
    public void onEntry(GotrContext context) throws Exception {
        context.getSessionHost().stateChanged(GotrSessionState.PLAINTEXT);
    }
}
