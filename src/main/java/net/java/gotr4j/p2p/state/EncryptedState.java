package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.KeyRatchet;
import net.java.gotr4j.crypto.LocalKeyRatchetShare;
import net.java.gotr4j.io.GotrInputStream;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedEncryptedP2pMessageEvent;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.gotr.event.GotrP2pEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageParser;
import net.java.gotr4j.p2p.io.EncryptedP2pMessage;
import net.java.gotr4j.p2p.smp.event.SmpEvent;
import net.java.hsm.HSMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptedState extends P2pState {
    private static final Logger logger = LoggerFactory.getLogger(EncryptedState.class);

    private final KeyRatchet keyRatchet;

    public EncryptedState(KeyRatchet keyRatchet) {
        this.keyRatchet = keyRatchet;
    }

    @Override
    protected void handleReceivedEncryptedMessageEvent(P2pContext context, ReceivedEncryptedP2pMessageEvent event) throws GotrException {
        try {

            byte[][] receivingKeys = keyRatchet.generateReceivingKeys(event.getKeyId(), context.getLocalUser(), context.getRemoteUser());

            if(!event.checkMac(receivingKeys[1])){
                throw new GotrException("Invalid Mac.");
            }
            event.decrypt(receivingKeys[0]);

            GotrInputStream input = new GotrInputStream(event.getPlaintext());

            HSMEvent inner = EncryptedMessageParser.parseEncryptedMessage(input, context);

            if(inner instanceof GotrP2pEvent){
                context.getGotrP2pContext().handelEvent(inner);
            }
            else if(inner instanceof SmpEvent){
                context.getSmpContext().handelEvent(inner);
            }

            byte nextID = input.readByte();
            keyRatchet.setRemoteShare(nextID, input.readPubKeySpec());
        } catch (Exception e){
            throw new GotrException(e);
        }
    }

    @Override
    protected void handleSendEncryptedMessageEvent(P2pContext context, SendEncryptedMessageEvent event) throws GotrException {
        try {

            byte[][] sendingKeys = keyRatchet.generateSendingKeys(context.getLocalUser(), context.getRemoteUser());

            LocalKeyRatchetShare share = keyRatchet.newShare();

            GotrOutputStream out = new GotrOutputStream();
            out.write(event.getData());
            out.writeByte(share.getId());
            out.writePublicKey(share.getPublicKey());

            EncryptedP2pMessage message = new EncryptedP2pMessage(context.getSessionID(), keyRatchet.getRemoteShareId(), out.getBytes(),
                    sendingKeys[0], sendingKeys[1], context.getCrypto());

            context.getSessionHost().sendMessage(context.getRemoteUser(), message.encode());
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }
}
