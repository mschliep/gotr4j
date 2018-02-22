package net.java.gotr4j.p2p.gotr.event;

import java.security.spec.KeySpec;

public class ReceivedSecretEvent implements GotrP2pEvent{

    private final byte[] secret;
    private final KeySpec verificationKey;
    private final byte[] groupVerificationKey;

    public ReceivedSecretEvent(byte[] secret, KeySpec verificationKey, byte[] groupVerificationKey) {
        this.secret = secret;
        this.verificationKey = verificationKey;
        this.groupVerificationKey = groupVerificationKey;
    }

    public KeySpec getVerificationKey() {
        return verificationKey;
    }

    public byte[] getSecret() {
        return secret;
    }

    public byte[] getGroupVerificationKey() {
        return groupVerificationKey;
    }
}
