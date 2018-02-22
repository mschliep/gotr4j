package net.java.gotr4j;

import net.java.gotr4j.util.GotrUtil;

import java.util.Arrays;

public class SessionID {
    public static final int size = 32; //32 bytes

    private final byte[] sessionID;

    public SessionID(byte[] sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionID sessionID1 = (SessionID) o;

        if (!Arrays.equals(sessionID, sessionID1.sessionID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(sessionID);
    }

    @Override
    public String toString() {
        return GotrUtil.toBase64(sessionID);
    }

    public byte[] getBytes() {
        return sessionID;
    }
}
