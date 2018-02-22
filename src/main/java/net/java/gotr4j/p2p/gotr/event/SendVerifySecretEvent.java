package net.java.gotr4j.p2p.gotr.event;

import net.java.hsm.HSMEvent;

public class SendVerifySecretEvent implements GotrP2pEvent{

    private final byte[] secretVerification;

    public SendVerifySecretEvent(byte[] secretVerification) {
        this.secretVerification = secretVerification;
    }

    public byte[] getSecretVerification() {
        return secretVerification;
    }
}
