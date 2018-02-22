package net.java.gotr4j.p2p.gotr.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretEvent;
import net.java.gotr4j.p2p.gotr.event.SendSecretEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendSecretState extends GotrP2pState {
    private static final Logger logger = LoggerFactory.getLogger(SendSecretState.class);

    @Override
    protected void handleSendSecretEvent(GotrP2pContext context, SendSecretEvent event) throws GotrException {
        try {
            GotrOutputStream out = new GotrOutputStream();
            out.writeByte(EncryptedMessageType.SECRET_KEY.id);
            out.write(event.getSecret());
            out.writePublicKey(event.getSigningPublicKey());
            out.write(event.getGroupVerificationKey());

            context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

            context.setNextState(new ReceiveSecretState());

        } catch (Exception e){
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleReceivedSecretEvent(GotrP2pContext context, ReceivedSecretEvent event) {
        context.deferEvent(event);
    }

    @Override
    public void onEntry(GotrP2pContext context) throws Exception {
    }
}
