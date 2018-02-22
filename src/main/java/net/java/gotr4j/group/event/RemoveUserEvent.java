package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

public class RemoveUserEvent implements HSMEvent {

    private final GotrUser user;


    public RemoveUserEvent(GotrUser user) {
        this.user = user;
    }

    public GotrUser getUser(){
        return user;
    }
}
