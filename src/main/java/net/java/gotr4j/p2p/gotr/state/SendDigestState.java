package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretEvent;
import net.java.gotr4j.p2p.gotr.event.SendVerifyDigestEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestEvent;
import net.java.gotr4j.p2p.gotr.event.SendDigestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendDigestState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(SendDigestState.class);

    @Override
    protected void handleSendDigestEvent(GotrP2pContext context, SendDigestEvent event) throws GotrException {
        try {
            GotrOutputStream out = new GotrOutputStream();
            out.writeByte(EncryptedMessageType.DIGEST.id);
            out.write(event.getDigest());
            out.writeSignature(event.getSignature());
            out.write(event.getSecret());
            out.writePublicKey(event.getSigningPublicKey());

            context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

            context.setNextState(new ReceiveDigestState());
        } catch (Exception e){
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleReceivedDigestEvent(GotrP2pContext context, ReceivedDigestEvent event) {
        context.deferEvent(event);
    }

    @Override
    protected void handleReceivedSecretEvent(GotrP2pContext context, ReceivedSecretEvent event) {
        context.deferEvent(event);
    }
}
