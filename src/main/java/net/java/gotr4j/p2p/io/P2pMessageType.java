package net.java.gotr4j.p2p.io;

public enum P2pMessageType {
    P2P_KEYS(0x01),
    ENCRYPTED(0x02);

    public byte id;

    P2pMessageType(int id){
        this.id = (byte)id;
    }
}
