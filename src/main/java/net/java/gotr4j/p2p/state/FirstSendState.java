package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.LocalKeyRatchetShare;
import net.java.gotr4j.crypto.KeyRatchet;
import net.java.gotr4j.io.EncryptedGroupMessage;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedEncryptedP2pMessageEvent;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedP2pMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstSendState extends P2pState {
    private static final Logger logger = LoggerFactory.getLogger(FirstSendState.class);

    private final byte[][] sendingKeys;
    private final byte[][] receivingKeys;
    private KeyRatchet keyRatchet;

    public FirstSendState(byte[][] sendingKeys, byte[][] receivingKeys) {
        this.sendingKeys = sendingKeys;
        this.receivingKeys = receivingKeys;
    }

    @Override
    protected void handleSendEncryptedMessageEvent(P2pContext context, SendEncryptedMessageEvent event) throws GotrException {
        try {
            LocalKeyRatchetShare share = keyRatchet.newShare();

            GotrOutputStream out = new GotrOutputStream();
            out.write(event.getData());
            out.writeByte(share.getId());
            out.writePublicKey(share.getPublicKey());

            EncryptedP2pMessage message = new EncryptedP2pMessage(context.getSessionID(), (byte) 0, out.getBytes(),
                    sendingKeys[0], sendingKeys[1], context.getCrypto());

            context.getSessionHost().sendMessage(context.getRemoteUser(), message.encode());
            context.setNextState(new FirstRecvState(receivingKeys, keyRatchet));

        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleReceivedEncryptedMessageEvent(P2pContext context, ReceivedEncryptedP2pMessageEvent event) throws GotrException {
        context.deferEvent(event);
    }

    @Override
    public void onEntry(P2pContext context) throws Exception {
        keyRatchet = new KeyRatchet(context.getCrypto());
    }
}
