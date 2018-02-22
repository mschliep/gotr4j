package net.java.gotr4j.p2p.gotr.event;

import net.java.hsm.HSMEvent;

import java.security.PublicKey;

public class SendDigestEvent implements GotrP2pEvent{


    private final byte[] digest;
    private final byte[] signature;

    private final byte[] secret;
    private final PublicKey signingPublicKey;

    public SendDigestEvent(byte[] digest, byte[] signature, byte[] secret, PublicKey signingPublicKey) {
        this.digest = digest;
        this.signature = signature;
        this.secret = secret;
        this.signingPublicKey = signingPublicKey;
    }

    public byte[] getSecret() {
        return secret;
    }

    public PublicKey getSigningPublicKey() {
        return signingPublicKey;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getSignature() {
        return signature;
    }
}
