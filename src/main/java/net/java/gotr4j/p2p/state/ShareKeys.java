package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.P2pKeyAgreement;
import net.java.gotr4j.p2p.io.P2PKeysMessage;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedP2PKeysEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class ShareKeys extends P2pState {
    private static final Logger logger = LoggerFactory.getLogger(ShareKeys.class);

    private P2pKeyAgreement keyAgreement;

    @Override
    protected void handleReceivedEphemeralKeyEvent(P2pContext context, ReceivedP2PKeysEvent event) throws GotrException {
        try {
            keyAgreement.setRemoteEphemeralKey(event.getEphemeralKeySpec());
            keyAgreement.setRemoteLongtermPublicKey(event.getLongtermKeySpec());


            byte[][] sendingKeys = context.getCrypto().getKeys(keyAgreement.getSendingSharedSecret());
            byte[][] receivingKeys = context.getCrypto().getKeys(keyAgreement.getReceivingSharedSecret());

            context.setRemoteLongtermKey(keyAgreement.getRemoteLongtermPublicKey());

            context.setNextState(new FirstSendState(sendingKeys, receivingKeys));
        } catch (Exception e){
            throw new GotrException(e);
        }
    }

    @Override
    public void onEntry(P2pContext context) throws Exception {
        keyAgreement = context.getCrypto().generateP2pKeyAgreement();
        keyAgreement.initHosts(context.getLocalUser().toString().getBytes(), context.getRemoteUser().toString().getBytes());
        KeyPair localKeyPair = context.getSessionHost().getLocalKeyPair();

        keyAgreement.initLongtermKey((ECPrivateKey) localKeyPair.getPrivate(), (ECPublicKey) localKeyPair.getPublic());

        P2PKeysMessage message = new P2PKeysMessage(context.getSessionID(), localKeyPair.getPublic(), keyAgreement.getEphemeralPublicKey());

        context.getSessionHost().sendMessage(context.getRemoteUser(), message.encode());
    }
}
