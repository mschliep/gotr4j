package net.java.gotr4j.io;

public enum GroupMessageType {
    ENCRYPTED(0x01),
    FINISH(0x02),
    REFRESH_KEYS(0x03),
    UNRECOVERABLE_ERROR(0x04);

    public byte id;

    GroupMessageType(int id){
        this.id = (byte)id;
    }
}
