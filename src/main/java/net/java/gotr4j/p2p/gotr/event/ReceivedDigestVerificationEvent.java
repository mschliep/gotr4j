package net.java.gotr4j.p2p.gotr.event;

public class ReceivedDigestVerificationEvent implements GotrP2pEvent{

    private final byte[] digestVerification;

    public ReceivedDigestVerificationEvent(byte[] digestVerification) {
        this.digestVerification = digestVerification;
    }

    public byte[] getDigestVerification() {
        return digestVerification;
    }
}
