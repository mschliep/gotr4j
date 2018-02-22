package net.java.gotr4j.p2p.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrEncodedMessage;
import net.java.gotr4j.io.GotrOutputStream;

import java.io.IOException;
import java.security.PublicKey;

public class P2PKeysMessage extends GotrEncodedMessage {

    private final PublicKey longtermKey;
    private final PublicKey ephemeralKey;

    public P2PKeysMessage(SessionID sessionID, PublicKey longtermKey, PublicKey ephemeralKey) {
        super(P2pMessageType.P2P_KEYS.id, sessionID);
        this.longtermKey = longtermKey;
        this.ephemeralKey = ephemeralKey;
    }

    @Override
    protected String getEncodedMessage() throws IOException, GotrException {
        GotrOutputStream out = buildOutputStream();
        out.writePublicKey(longtermKey);
        out.writePublicKey(ephemeralKey);
        return out.encode();
    }
}
