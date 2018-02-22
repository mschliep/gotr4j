package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.hsm.HSMEvent;

public class UnrecoverableErrorEvent implements HSMEvent {

    private final SessionID sessionID;
    private final GotrUser user;

    public UnrecoverableErrorEvent(SessionID sessionID, GotrUser user) {
        this.sessionID = sessionID;
        this.user = user;
    }

    public GotrUser getUser(){
        return user;
    }

    public SessionID getSessionID(){
        return sessionID;
    }
}
