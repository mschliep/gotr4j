package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

import java.security.spec.KeySpec;

public class DigestReceivedEvent implements HSMEvent{

    private final GotrUser remoteUser;

    private final byte[] digest;
    private final byte[] signature;

    private final byte[] secret;
    private final KeySpec signingPubKey;



    public DigestReceivedEvent(GotrUser remoteUser, byte[] digest, byte[] signature, byte[] secret, KeySpec publicSigningKey) {
        this.remoteUser = remoteUser;
        this.digest = digest;
        this.signature = signature;
        this.secret = secret;
        this.signingPubKey = publicSigningKey;
    }

    public GotrUser getRemoteUser() {
        return remoteUser;
    }

    public byte[] getSecret() {
        return secret;
    }

    public KeySpec getSigningPubKey() {
        return signingPubKey;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getSignature() {
        return signature;
    }
}
