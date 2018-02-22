package net.java.gotr4j.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public class LocalKeyRatchetShare {

    private final byte id;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public LocalKeyRatchetShare(byte id, PrivateKey privateKey, PublicKey publicKey) {
        this.id = id;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public byte getId() {
        return id;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
