package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrException;

import java.io.IOException;

public class UnrecoverableErrorMessage extends GotrEncodedMessage{

    public UnrecoverableErrorMessage(SessionID sessionID) {
        super(GroupMessageType.UNRECOVERABLE_ERROR.id, sessionID);
    }

    @Override
    protected String getEncodedMessage() throws IOException, GotrException {
        return buildOutputStream().encode();
    }
}
