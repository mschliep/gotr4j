package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.group.event.AddUserEvent;
import net.java.gotr4j.group.event.FinishEvent;
import net.java.gotr4j.group.event.RemoveUserEvent;
import net.java.gotr4j.p2p.gotr.event.*;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.hsm.HSMEvent;
import net.java.hsm.SimpleHSMState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class GotrP2pState extends SimpleHSMState<GotrP2pContext> {
    private static final Logger logger = LoggerFactory.getLogger(GotrP2pState.class);

    @Override
    public void handleEvent(GotrP2pContext context, HSMEvent event) throws Exception{
        if(event instanceof SendSecretEvent){
            handleSendSecretEvent(context, (SendSecretEvent) event);
        }
        else if(event instanceof ReceivedSecretEvent){
            handleReceivedSecretEvent(context, (ReceivedSecretEvent) event);
        }
        else if(event instanceof SendVerifySecretEvent){
            handleSendVerifySecretEvent(context, (SendVerifySecretEvent) event);
        }
        else if(event instanceof ReceivedSecretVerificationEvent){
            handleReceivedSecretVerificationEvent(context, (ReceivedSecretVerificationEvent) event);
        }
        else if(event instanceof SendDigestEvent){
            handleSendDigestEvent(context, (SendDigestEvent) event);
        }
        else if(event instanceof ReceivedDigestEvent){
            handleReceivedDigestEvent(context, (ReceivedDigestEvent) event);
        }
        else if(event instanceof SendVerifyDigestEvent){
            handleSendVerifyDigestEvent(context, (SendVerifyDigestEvent) event);
        }
        else if(event instanceof ReceivedDigestVerificationEvent){
            handleReceivedDigestVerificationEvent(context, (ReceivedDigestVerificationEvent) event);
        }
        else if(event instanceof AddUserEvent){
            handleAddUserEvent(context, (AddUserEvent) event);
        }
        else if(event instanceof RemoveUserEvent){
            handleRemoveUserEvent(context, (RemoveUserEvent) event);
        }
        else if(event instanceof FinishEvent){
            handleFinishEvent(context, (FinishEvent) event);
        }

    }

    protected void handleFinishEvent(GotrP2pContext context, FinishEvent event) {
        context.setNextState(new SendSecretState());
    }

    protected void handleRemoveUserEvent(GotrP2pContext context, RemoveUserEvent event) {
        context.setNextState(new SendSecretState());
    }

    protected void handleAddUserEvent(GotrP2pContext context, AddUserEvent event) {
        context.setNextState(new SendSecretState());
    }

    protected void handleReceivedDigestVerificationEvent(GotrP2pContext context, ReceivedDigestVerificationEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleSendVerifyDigestEvent(GotrP2pContext context, SendVerifyDigestEvent event) throws GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleReceivedDigestEvent(GotrP2pContext context, ReceivedDigestEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleSendDigestEvent(GotrP2pContext context, SendDigestEvent event) throws GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleReceivedSecretVerificationEvent(GotrP2pContext context, ReceivedSecretVerificationEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleSendVerifySecretEvent(GotrP2pContext context, SendVerifySecretEvent event) throws GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleReceivedSecretEvent(GotrP2pContext context, ReceivedSecretEvent event) {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleSendSecretEvent(GotrP2pContext context, SendSecretEvent event) throws GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }
}
