package net.java.gotr4j.p2p.event;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.Counter;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrInputStream;
import net.java.hsm.HSMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ReceivedEncryptedP2pMessageEvent implements HSMEvent {

    private static final Logger logger = LoggerFactory.getLogger(ReceivedEncryptedP2pMessageEvent.class);

    private final byte[] bytes;
    private final SessionID sessionID;
    private final byte keyId;
    private final byte[] ciphertext;
    private final byte[] mac;

    private final String encodedMessage;
    private final GotrUser user;

    private final GotrCrypto crypto;

    private byte[] plaintext;

    public ReceivedEncryptedP2pMessageEvent(byte[] bytes, String encodedMessage, GotrUser user, GotrCrypto crypto) throws IOException {
        this.bytes = bytes;
        this.encodedMessage = encodedMessage;
        this.user = user;
        this.crypto = crypto;

        GotrInputStream input = new GotrInputStream(bytes);
        sessionID = input.readSessionID();
        input.readByte();
        keyId = input.readByte();

        int length = input.available() - crypto.MAC_SIZE;

        ciphertext = input.readBytes(length);
        mac = input.readRemaining();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    public byte[] getMac() {
        return mac;
    }

    public byte[] getPlaintext(){
        return plaintext;
    }

    public boolean checkMac(byte[] key) throws GotrException {
        return crypto.checkMac(key, bytes);
    }

    public byte[] decrypt(byte[] key) throws GotrException, IOException {
        if(plaintext == null) {
            plaintext = crypto.aesCtrDecrypt(key, Counter.ZERO, ciphertext);
        }
        return plaintext;
    }

    public GotrUser getUser() {
        return user;
    }

    public String getEncodedMessage() {
        return encodedMessage;
    }

    public byte getKeyId() {
        return keyId;
    }
}
