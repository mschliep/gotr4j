package net.java.gotr4j.p2p.gotr.event;

public class ReceivedSecretVerificationEvent implements GotrP2pEvent{

    private final byte[] secretVerification;

    public ReceivedSecretVerificationEvent(byte[] secretVerification) {
        this.secretVerification = secretVerification;
    }

    public byte[] getSecretVerification() {
        return secretVerification;
    }
}
