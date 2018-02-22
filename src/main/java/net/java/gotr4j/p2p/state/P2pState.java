package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.group.event.AddUserEvent;
import net.java.gotr4j.group.event.FinishEvent;
import net.java.gotr4j.group.event.GotrQueryEvent;
import net.java.gotr4j.group.event.RemoveUserEvent;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.*;
import net.java.gotr4j.p2p.gotr.event.GotrP2pEvent;
import net.java.gotr4j.p2p.smp.event.SmpEvent;
import net.java.hsm.HSMEvent;
import net.java.hsm.SimpleHSMState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class P2pState extends SimpleHSMState<P2pContext> {
    private static final Logger logger = LoggerFactory.getLogger(P2pState.class);

    @Override
    public void handleEvent(P2pContext context, HSMEvent event) throws Exception{
        if(event instanceof GotrQueryEvent){
            handleGotrQueryEvent(context, (GotrQueryEvent) event);
        }
        else if(event instanceof ReceivedP2PKeysEvent){
            handleReceivedEphemeralKeyEvent(context, (ReceivedP2PKeysEvent) event);
        }
        else if(event instanceof ReceivedEncryptedP2pMessageEvent){
            handleReceivedEncryptedMessageEvent(context, (ReceivedEncryptedP2pMessageEvent) event);
        }
        else if(event instanceof SendEncryptedMessageEvent){
            handleSendEncryptedMessageEvent(context, (SendEncryptedMessageEvent) event);
        }
        else if(event instanceof GotrP2pEvent){
            handleGotrP2pEvent(context, (GotrP2pEvent) event);
        }
        else if(event instanceof SmpEvent){
            handleSmpEvent(context, (SmpEvent) event);
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



    private void handleSmpEvent(P2pContext context, SmpEvent event) {
        context.getSmpContext().handelEvent(event);
    }

    private void handleGotrP2pEvent(P2pContext context, GotrP2pEvent event) {
        context.getGotrP2pContext().handelEvent(event);
    }

    protected void handleFinishEvent(P2pContext context, FinishEvent event) {
        context.setNextState(new PlaintextState());
        context.getGotrP2pContext().handelEvent(event);
        context.getSmpContext().handelEvent(event);
    }

    protected void handleRemoveUserEvent(P2pContext context, RemoveUserEvent event) {
        context.getGotrP2pContext().handelEvent(event);
    }

    protected void handleAddUserEvent(P2pContext context, AddUserEvent event) {
        context.getGotrP2pContext().handelEvent(event);
    }

    protected void handleSendEncryptedMessageEvent(P2pContext context, SendEncryptedMessageEvent event) throws NoSuchAlgorithmException, GotrException {
        context.deferEvent(event);
    }

    protected void handleReceivedEncryptedMessageEvent(P2pContext context, ReceivedEncryptedP2pMessageEvent event) throws GotrException {
        context.deferEvent(event);
    }

    protected void handleReceivedEphemeralKeyEvent(P2pContext context, ReceivedP2PKeysEvent event) throws InvalidKeySpecException, InvalidKeyException, GotrException {
        logger.info("{} is dropping message {} !!!!!!!!!!!!!!!!!! {}", context.getLocalUser(), event, context.getCurrentState());
    }

    protected void handleGotrQueryEvent(P2pContext context, GotrQueryEvent event) throws GotrException {
        context.getGotrP2pContext().handelEvent(event);
    }

}
