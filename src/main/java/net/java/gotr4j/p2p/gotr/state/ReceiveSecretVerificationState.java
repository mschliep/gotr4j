package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.group.event.SecretVerificationReceivedEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretVerificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveSecretVerificationState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveSecretVerificationState.class);

    @Override
    protected void handleReceivedSecretVerificationEvent(GotrP2pContext context, ReceivedSecretVerificationEvent event) {
        context.getGroupContext().handelEvent(new SecretVerificationReceivedEvent(context.getRemoteUser(), event.getSecretVerification()));
        context.setNextState(new SendDigestState());
    }
}
