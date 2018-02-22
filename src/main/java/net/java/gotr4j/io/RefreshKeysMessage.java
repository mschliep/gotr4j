package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrException;

import java.io.IOException;

public class RefreshKeysMessage extends GotrEncodedMessage{
    public RefreshKeysMessage(SessionID sessionID)
    {
        super(GroupMessageType.REFRESH_KEYS.id, sessionID);
    }

    @Override
    protected String getEncodedMessage() throws IOException, GotrException {
        return buildOutputStream().encode();
    }
}
