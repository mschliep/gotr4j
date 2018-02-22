package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.KeyRatchet;
import net.java.gotr4j.io.GotrInputStream;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedEncryptedP2pMessageEvent;
import net.java.gotr4j.p2p.gotr.event.GotrP2pEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageParser;
import net.java.gotr4j.p2p.smp.event.SmpEvent;
import net.java.hsm.HSMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.spec.KeySpec;

public class FirstRecvState extends P2pState {
    private static final Logger logger = LoggerFactory.getLogger(FirstRecvState.class);

    private final byte[][] receivingKeys;
    private final KeyRatchet keyRatchet;

    public FirstRecvState(byte[][] receivingKeys, KeyRatchet keyRatchet) {
        this.receivingKeys = receivingKeys;
        this.keyRatchet = keyRatchet;
    }

    @Override
    protected void handleReceivedEncryptedMessageEvent(P2pContext context, ReceivedEncryptedP2pMessageEvent event) throws GotrException {
        try {

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
            byte id = input.readByte();
            KeySpec remoteKeySpec = input.readPubKeySpec();
            keyRatchet.setRemoteShare(id, remoteKeySpec);


            context.setNextState(new EncryptedState(keyRatchet));
        } catch (Exception e){
            throw new GotrException(e);
        }
    }
}
