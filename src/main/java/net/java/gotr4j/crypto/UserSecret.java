package net.java.gotr4j.crypto;

import net.java.gotr4j.GotrUser;

import java.security.PublicKey;

public class UserSecret {

    private final GotrUser user;
    private final byte[] secret;
    private final PublicKey signingPublicKey;

    public UserSecret(GotrUser user, byte[] secret, PublicKey signingPublicKey) {
        this.user = user;
        this.secret = secret;
        this.signingPublicKey = signingPublicKey;
    }

    public GotrUser getUser() {
        return user;
    }

    public byte[] getSecret() {
        return secret;
    }

    public PublicKey getSigningPublicKey() {
        return signingPublicKey;
    }
}
