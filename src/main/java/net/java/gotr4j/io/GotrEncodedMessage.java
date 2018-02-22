package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrException;

import java.io.*;
import java.util.regex.Pattern;

public abstract class GotrEncodedMessage implements GotrMessage {
    public final static Pattern HEADER_PATTERN = Pattern.compile(GOTR_HEADER+GOTR_ENCODED);

    private final byte type;
    private final SessionID sessionID;

    public GotrEncodedMessage(byte type, SessionID sessionID){
        this.type = type;
        this.sessionID = sessionID;
    }

    public String encode() throws IOException, GotrException {
        return String.format("%s%s%s", GOTR_HEADER, GOTR_ENCODED, getEncodedMessage());
    }

    public GotrOutputStream buildOutputStream() throws IOException, GotrException {
        GotrOutputStream out = new GotrOutputStream();
        out.writeSessionID(sessionID);
        out.writeByte(type);

        return out;
    }

    public SessionID getSessionID(){
        return sessionID;
    }

    public byte getMessageType(){
        return type;
    }

    protected abstract String getEncodedMessage() throws IOException, GotrException;

}
