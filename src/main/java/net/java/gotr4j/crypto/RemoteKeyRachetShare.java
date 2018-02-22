package net.java.gotr4j.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public class RemoteKeyRachetShare {

    private final byte id;
    private final PublicKey publicKey;

    public RemoteKeyRachetShare(byte id, PublicKey publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    public byte getId() {
        return id;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
