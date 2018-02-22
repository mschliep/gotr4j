package net.java.gotr4j.p2p.event;

import net.java.hsm.HSMEvent;

public class SendEncryptedMessageEvent implements HSMEvent {

    private final byte[] data;

    public SendEncryptedMessageEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
