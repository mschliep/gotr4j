package net.java.gotr4j.group.event;

import net.java.hsm.HSMEvent;

public class UserBroadcastEvent implements HSMEvent {

    private final String broadcast;

    public UserBroadcastEvent(String broadcast) {
        this.broadcast = broadcast;
    }

    public String getBroadcast(){
        return broadcast;
    }
}
