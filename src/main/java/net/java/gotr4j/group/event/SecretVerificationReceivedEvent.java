package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

public class SecretVerificationReceivedEvent implements HSMEvent {

    public final GotrUser remoteUser;

    public final byte[] secretVerification;

    public SecretVerificationReceivedEvent(GotrUser remoteUser, byte[] secretVerification) {
        this.remoteUser = remoteUser;
        this.secretVerification = secretVerification;
    }

    public GotrUser getRemoteUser() {
        return remoteUser;
    }

    public byte[] getSecretVerification() {
        return secretVerification;
    }
}
