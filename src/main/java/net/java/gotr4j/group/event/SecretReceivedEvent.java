package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

import java.security.spec.KeySpec;

public class SecretReceivedEvent implements HSMEvent{

    private final GotrUser remoteUser;
    private final byte[] secret;
    private final KeySpec signingPubKey;
    private final byte[] groupVerificationKey;

    public SecretReceivedEvent(GotrUser remoteUser, byte[] secret, KeySpec publicSigningKey, byte[] groupVerificationKey) {
        this.remoteUser = remoteUser;
        this.secret = secret;
        this.signingPubKey = publicSigningKey;
        this.groupVerificationKey = groupVerificationKey;
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

    public byte[] getGroupVerificationKey() {
        return groupVerificationKey;
    }
}
