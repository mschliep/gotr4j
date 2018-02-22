package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.*;
import net.java.hsm.HSMEvent;
import net.java.hsm.SimpleHSMState;

import java.security.NoSuchAlgorithmException;

public class SmpState extends SimpleHSMState<SmpContext> {

    @Override
    public void handleEvent(SmpContext context, HSMEvent event) throws Exception{
        if(event instanceof UserStartSmpEvent){
            handleUserStartSmpEvent(context, (UserStartSmpEvent) event);
        }
        else if(event instanceof UserAbortSmpEvent){
            handleUserAbortSmpEvent(context, (UserAbortSmpEvent) event);
        }
        else if(event instanceof ReceivedInitSmpEvent){
            handleReceivedInitSmpEvent(context, (ReceivedInitSmpEvent) event);
        }
        else if(event instanceof UserRespondSmpEvent){
            handleUserRespondSmpEvent(context, (UserRespondSmpEvent) event);
        }
        else if(event instanceof ReceivedSmpResponseEvent){
            handleReceivedSmpResponseEvent(context, (ReceivedSmpResponseEvent) event);
        }
        else if(event instanceof ReceivedInitFinalSmpEvent){
            handleReceivedInitFinalSmpEvent(context, (ReceivedInitFinalSmpEvent) event);
        }
        else if(event instanceof ReceivedFinalSmpResponseEvent){
            handleReceivedFinalSmpResponseEvent(context, (ReceivedFinalSmpResponseEvent) event);
        }
        else if(event instanceof ReceivedSmpAbortEvent){
            handleReceivedSmpAbortEvent(context, (ReceivedSmpAbortEvent) event);
        }

    }

    private void handleReceivedSmpAbortEvent(SmpContext context, ReceivedSmpAbortEvent event) {
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleReceivedFinalSmpResponseEvent(SmpContext context, ReceivedFinalSmpResponseEvent event) throws Exception {
        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(new byte[] {EncryptedMessageType.SMP_ABORT.id}));
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleReceivedInitFinalSmpEvent(SmpContext context, ReceivedInitFinalSmpEvent event) throws Exception {
        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(new byte[] {EncryptedMessageType.SMP_ABORT.id}));
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleReceivedSmpResponseEvent(SmpContext context, ReceivedSmpResponseEvent event) throws Exception {
        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(new byte[] {EncryptedMessageType.SMP_ABORT.id}));
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleUserRespondSmpEvent(SmpContext context, UserRespondSmpEvent event) throws Exception {
    }

    protected void handleReceivedInitSmpEvent(SmpContext context, ReceivedInitSmpEvent event) throws Exception {
        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(new byte[] {EncryptedMessageType.SMP_ABORT.id}));
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleUserAbortSmpEvent(SmpContext context, UserAbortSmpEvent event) {
        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(new byte[] {EncryptedMessageType.SMP_ABORT.id}));
        context.getSessionHost().smpAborted(context.getRemoteUser());
        context.setNextState(new InitSmpState());
    }

    protected void handleUserStartSmpEvent(SmpContext context, UserStartSmpEvent event) throws NoSuchAlgorithmException, Exception {
    }
}
