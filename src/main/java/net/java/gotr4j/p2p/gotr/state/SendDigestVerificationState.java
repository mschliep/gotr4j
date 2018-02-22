package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestVerificationEvent;
import net.java.gotr4j.p2p.gotr.event.SendVerifyDigestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendDigestVerificationState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(SendDigestVerificationState.class);

    @Override
    protected void handleSendVerifyDigestEvent(GotrP2pContext context, SendVerifyDigestEvent event) throws GotrException {
        try {
            GotrOutputStream out = new GotrOutputStream();
            out.writeByte(EncryptedMessageType.DIGEST_VERIFCATION.id);
            out.write(event.getDigestVerification());
            //out.write(event.getSecretVerification());

            context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

            context.setNextState(new ReceiveDigestVerificationState());

        } catch (Exception e){
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleReceivedDigestVerificationEvent(GotrP2pContext context, ReceivedDigestVerificationEvent event) {
        context.deferEvent(event);
    }
}
