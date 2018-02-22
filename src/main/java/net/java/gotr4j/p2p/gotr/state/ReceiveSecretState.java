package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.group.event.SecretReceivedEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveSecretState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveSecretState.class);

    @Override
    protected void handleReceivedSecretEvent(GotrP2pContext context, ReceivedSecretEvent event) {
        context.getGroupContext().handelEvent(new SecretReceivedEvent(context.getRemoteUser(), event.getSecret(), event.getVerificationKey(), event.getGroupVerificationKey()));

        context.setNextState(new SendDigestState());
    }
}
