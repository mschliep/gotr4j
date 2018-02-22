package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrException;

import java.io.IOException;

public class FinishMessage extends GotrEncodedMessage{
    public FinishMessage(SessionID sessionID)
    {
        super(GroupMessageType.FINISH.id, sessionID);
    }

    @Override
    protected String getEncodedMessage() throws IOException, GotrException {
        return buildOutputStream().encode();
    }
}
