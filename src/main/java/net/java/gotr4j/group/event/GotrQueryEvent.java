package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

public class GotrQueryEvent implements HSMEvent {

    private final GotrUser user;
    private final String query;

    public GotrQueryEvent(GotrUser source, String query) {
        this.user = source;
        this.query = query;
    }

    public GotrUser getUser(){
        return user;
    }
}
