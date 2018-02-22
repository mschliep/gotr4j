package net.java.gotr4j.p2p.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.Counter;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrEncodedMessage;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.io.GroupMessageType;

import java.io.IOException;

public class EncryptedP2pMessage extends GotrEncodedMessage{

    private final byte keyId;

    private final byte[] plaintext;

    private final byte[] encKey;
    private final byte[] macKey;
    private final GotrCrypto crypto;

    public EncryptedP2pMessage(SessionID sessionID, byte keyId, byte[] plaintext, byte[] encKey, byte[] macKey, GotrCrypto crypto)
    {
        super(P2pMessageType.ENCRYPTED.id, sessionID);
        this.keyId = keyId;
        this.plaintext = plaintext;
        this.encKey = encKey;
        this.macKey = macKey;
        this.crypto = crypto;
    }


    @Override
    public String getEncodedMessage() throws IOException, GotrException {
        GotrOutputStream tmp = new GotrOutputStream();
        tmp.write(plaintext);

        byte[] ct = crypto.aesCtrEncrypt(encKey, Counter.ZERO, tmp.getBytes());

        GotrOutputStream out = buildOutputStream();
        out.writeByte(keyId);
        out.write(ct);

        byte[] mac = crypto.mac(macKey, out.getBytes());
        out.write(mac);

        return out.encode();
    }
}
