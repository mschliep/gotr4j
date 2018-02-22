package net.java.gotr4j.p2p.gotr.event;

import net.java.hsm.HSMEvent;

public class SendVerifyDigestEvent implements GotrP2pEvent{

    private final byte[] digestVerification;

    public SendVerifyDigestEvent(byte[] digestVerification) {
        this.digestVerification = digestVerification;
    }

    public byte[] getDigestVerification() {
        return digestVerification;
    }
}
