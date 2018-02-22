package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.Counter;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.crypto.GotrException;

import java.io.IOException;

public class EncryptedGroupMessage extends GotrEncodedMessage{

    private final byte[] plaintext;

    private final byte[] encKey;
    private final byte[] macKey;
    private final GotrCrypto crypto;

    public EncryptedGroupMessage(SessionID sessionID, byte[] plaintext, byte[] encKey, byte[] macKey, GotrCrypto crypto)
    {
        super(GroupMessageType.ENCRYPTED.id, sessionID);
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
        out.write(ct);

        byte[] mac = crypto.mac(macKey, out.getBytes());
        out.write(mac);

        return out.encode();
    }
}
