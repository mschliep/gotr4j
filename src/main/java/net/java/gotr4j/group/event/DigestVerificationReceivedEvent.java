package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

public class DigestVerificationReceivedEvent implements HSMEvent {

    private final GotrUser remoteUser;

    private final byte[] digestVerification;

    public DigestVerificationReceivedEvent(GotrUser remoteUser, byte[] digestVerification) {
        this.remoteUser = remoteUser;
        this.digestVerification = digestVerification;
    }

    public GotrUser getRemoteUser() {
        return remoteUser;
    }

    public byte[] getDigestVerification() {
        return digestVerification;
    }
}
