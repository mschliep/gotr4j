package net.java.gotr4j.p2p.gotr.event;

import java.security.PublicKey;

public class SendSecretEvent implements GotrP2pEvent{

    private final byte[] secret;
    private final PublicKey signingPublicKey;
    private final byte[] groupVerificationKey;

    public SendSecretEvent(byte[] secret, PublicKey signingPublicKey, byte[] groupVerificationKey) {
        this.secret = secret;
        this.signingPublicKey = signingPublicKey;
        this.groupVerificationKey = groupVerificationKey;
    }

    public byte[] getSecret() {
        return secret;
    }

    public PublicKey getSigningPublicKey() {
        return signingPublicKey;
    }

    public byte[] getGroupVerificationKey() {
        return groupVerificationKey;
    }
}
