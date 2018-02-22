package net.java.gotr4j.p2p.event;

import net.java.gotr4j.SessionID;
import net.java.hsm.HSMEvent;

import java.security.spec.KeySpec;

public class ReceivedP2PKeysEvent implements HSMEvent {

    private final SessionID sessionID;
    private final KeySpec longtermKeySpec;
    private final KeySpec ephemeralKeySpec;

    public ReceivedP2PKeysEvent(SessionID sessionID, KeySpec longtermKeySpec, KeySpec ephemeralKey) {
        this.sessionID = sessionID;
        this.longtermKeySpec = longtermKeySpec;
        this.ephemeralKeySpec = ephemeralKey;
    }

    public KeySpec getEphemeralKeySpec() {
        return ephemeralKeySpec;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public KeySpec getLongtermKeySpec() {
        return longtermKeySpec;
    }
}
