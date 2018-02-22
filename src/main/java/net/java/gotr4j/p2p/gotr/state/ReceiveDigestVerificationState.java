package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.group.event.DigestVerificationReceivedEvent;
import net.java.gotr4j.group.state.ShareDigestState;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestVerificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveDigestVerificationState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveDigestVerificationState.class);

    @Override
    protected void handleReceivedDigestVerificationEvent(GotrP2pContext context, ReceivedDigestVerificationEvent event) {

        context.getGroupContext().handelEvent(new DigestVerificationReceivedEvent(context.getRemoteUser(),
                event.getDigestVerification()));

        context.setNextState(new SendDigestState());
    }
}
