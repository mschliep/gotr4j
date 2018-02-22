package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretVerificationEvent;
import net.java.gotr4j.p2p.gotr.event.SendVerifySecretEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SendSecretVerificationState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(SendSecretVerificationState.class);

    @Override
    protected void handleSendVerifySecretEvent(GotrP2pContext context, SendVerifySecretEvent event) throws GotrException {
        try {
            GotrOutputStream out = new GotrOutputStream();
            out.writeByte(EncryptedMessageType.SECRET_VERIFICATION.id);
            out.write(event.getSecretVerification());

            context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));
            context.setNextState(new ReceiveSecretVerificationState());
        } catch (IOException e) {
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleReceivedSecretVerificationEvent(GotrP2pContext context, ReceivedSecretVerificationEvent event) {
        context.deferEvent(event);
    }
}
