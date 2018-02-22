package net.java.gotr4j.group.state;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.group.event.*;
import net.java.gotr4j.io.FinishMessage;
import net.java.gotr4j.p2p.P2pContext;
import net.java.hsm.HSMEvent;
import net.java.hsm.SimpleHSMState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public abstract class GotrState extends SimpleHSMState<GotrContext> {

    private static final Logger logger = LoggerFactory.getLogger(GotrState.class);

    public abstract GotrSessionState getSessionState();

    @Override
    public void handleEvent(GotrContext context, HSMEvent event) throws Exception {
        if(event instanceof UserBroadcastEvent){
            handleUserBroadcastEvent(context, (UserBroadcastEvent) event);
        }
        else if(event instanceof RemoveUserEvent){
            handleRemoveUserEvent(context, (RemoveUserEvent) event);
        }
        else if(event instanceof AddUserEvent){
            handleAddUserEvent(context, (AddUserEvent) event);
        }
        else if(event instanceof PlaintextMessageEvent){
            handlePlaintextMessageEvent(context, (PlaintextMessageEvent) event);
        }
        else if(event instanceof UserStartEvent){
            handleUserStartEvent(context, (UserStartEvent) event);
        }
        else if(event instanceof GotrQueryEvent){
            handleGotrQueryEvent(context, (GotrQueryEvent) event);
        }
        else if(event instanceof SecretReceivedEvent){
            handleSecretReceivedEvent(context, (SecretReceivedEvent) event);
        }
        else if(event instanceof SecretVerificationReceivedEvent){
            handleSecretVerificationReceivedEvent(context, (SecretVerificationReceivedEvent) event);
        }
        else if(event instanceof UserFinishEvent){
            handleUserFinishEvent(context, (UserFinishEvent) event);
        }
        else if(event instanceof FinishEvent){
            handleFinishEvent(context, (FinishEvent) event);
        }
        else if(event instanceof UnrecoverableErrorEvent){
            handleUnrecoverableErrorEvent(context, (UnrecoverableErrorEvent) event);
        }
        else if(event instanceof ReceivedEncryptedGroupMessageEvent){
            handleReceivedEncryptedGroupMessageEvent(context, (ReceivedEncryptedGroupMessageEvent) event);
        }
        else if(event instanceof DigestReceivedEvent){
            handleDigestReceivedEvent(context, (DigestReceivedEvent) event);
        }
        else if(event instanceof DigestVerificationReceivedEvent){
            handleDigestVerificationReceivedEvent(context, (DigestVerificationReceivedEvent) event);
        }
        else if(event instanceof UserRefreshKeysEvent){
            handleUserRefreshKeysEvent(context, (UserRefreshKeysEvent) event);
        }
        else if(event instanceof RefreshKeysEvent){
            handleRefreshKeysEvent(context, (RefreshKeysEvent) event);
        }
    }

    protected void handleRefreshKeysEvent(GotrContext context, RefreshKeysEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleUserRefreshKeysEvent(GotrContext context, UserRefreshKeysEvent event) throws IOException, GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleDigestVerificationReceivedEvent(GotrContext context, DigestVerificationReceivedEvent event) throws NoSuchAlgorithmException, GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleDigestReceivedEvent(GotrContext context, DigestReceivedEvent event) throws InvalidKeySpecException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleReceivedEncryptedGroupMessageEvent(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException, NoSuchAlgorithmException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleSecretVerificationReceivedEvent(GotrContext context, SecretVerificationReceivedEvent event) throws GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleUserFinishEvent(GotrContext context, UserFinishEvent event) throws Exception {
        context.getOutingMessages().clear();
        context.setNextState(new PlaintextState());

        context.getSessionHost().sendBroadcast(new FinishMessage(context.getSessionID()).encode());

        FinishEvent finishEvent = new FinishEvent(context.getSessionID(), context.getLocalUser());
        for(P2pContext p2pContext: context.getP2pContexts()){
            p2pContext.handelEvent(finishEvent);
        }
    }

    protected void handleUnrecoverableErrorEvent(GotrContext context, UnrecoverableErrorEvent event) throws Exception {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleFinishEvent(GotrContext context, FinishEvent event) throws Exception{
        context.getOutingMessages().clear();
        context.setNextState(new PlaintextState());
        for(P2pContext p2pContext: context.getP2pContexts()){
            p2pContext.handelEvent(event);
        }
    }

    protected void handleSecretReceivedEvent(GotrContext context, SecretReceivedEvent event)  throws Exception{
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleGotrQueryEvent(GotrContext context, GotrQueryEvent event) throws Exception {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleUserStartEvent(GotrContext context, UserStartEvent event) throws Exception {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handlePlaintextMessageEvent(GotrContext context, PlaintextMessageEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleAddUserEvent(GotrContext context, AddUserEvent event) throws Exception {
        context.addP2pContext(event.getUser(), event.getP2pContext());
        context.getOutingMessages().clear();
        context.setNextState(new ShareSecretState());
        for(P2pContext p2pContext: context.getP2pContexts()){
            if(!event.getUser().equals(p2pContext.getRemoteUser())){
                p2pContext.handelEvent(event);
            }
        }
    }

    protected void handleRemoveUserEvent(GotrContext context, RemoveUserEvent event) throws Exception {
        context.removeP2pContext(event.getUser());
        context.getOutingMessages().clear();
        if(context.getGroupSize() == 1){
            byte[] secret = context.getCrypto().generateSecret();
            KeyPair keyPair = context.getCrypto().generateSigningKeyPair();
            UserKeys userKeys = new UserKeys();
            userKeys.put(context.getLocalUser(), secret, keyPair.getPublic());

            context.setNextState(new AwaitingBroadcastState(userKeys, keyPair, true));
        }
        else {
            context.setNextState(new ShareSecretState());
            for (P2pContext p2pContext : context.getP2pContexts()) {
                p2pContext.handelEvent(event);
            }
        }
    }

    protected void handleUserBroadcastEvent(GotrContext context, UserBroadcastEvent event) throws Exception {
        context.getOutingMessages().add(event.getBroadcast());
    }
}
