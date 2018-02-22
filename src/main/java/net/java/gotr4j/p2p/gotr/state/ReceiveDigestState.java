package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.group.event.DigestReceivedEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveDigestState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveDigestState.class);

    @Override
    protected void handleReceivedDigestEvent(GotrP2pContext context, ReceivedDigestEvent event) {
        context.getGroupContext().handelEvent(new DigestReceivedEvent(context.getRemoteUser(), event.getDigest(),
                event.getSignature(), event.getSecret(), event.getVerificationKey()));
        context.setNextState(new SendDigestVerificationState());
    }
}
