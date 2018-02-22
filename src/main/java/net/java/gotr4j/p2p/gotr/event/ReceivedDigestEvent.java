package net.java.gotr4j.p2p.gotr.event;

import java.security.spec.KeySpec;

public class ReceivedDigestEvent implements GotrP2pEvent{

    private final byte[] digest;
    private final byte[] signature;
    private final byte[] secret;
    private final KeySpec verificationKey;

    public ReceivedDigestEvent(byte[] digest, byte[] signature, byte[] secret, KeySpec verificationKey) {
        this.digest = digest;
        this.signature = signature;
        this.secret = secret;
        this.verificationKey = verificationKey;
    }

    public KeySpec getVerificationKey() {
        return verificationKey;
    }

    public byte[] getSecret() {
        return secret;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getSignature() {
        return signature;
    }
}
