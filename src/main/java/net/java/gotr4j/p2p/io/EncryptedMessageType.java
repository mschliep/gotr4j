package net.java.gotr4j.p2p.io;

public enum EncryptedMessageType {
    SECRET_KEY(0x01),
    SECRET_VERIFICATION(0x02),
    DIGEST(0x03),
    DIGEST_VERIFCATION(0x04),
    SMP_INIT(0x05),
    SMP_RESPONSE(0x06),
    SMP_INIT_FINAL(0x07),
    SMP_RESPONSE_FINAL(0x08),
    SMP_ABORT(0x09);

    public byte id;

    EncryptedMessageType(int id){
        this.id = (byte)id;
    }
}
