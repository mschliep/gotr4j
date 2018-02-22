package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.hsm.HSMEvent;

public class PlaintextMessageEvent implements HSMEvent{

    private final GotrUser source;
    private final String content;

    public PlaintextMessageEvent(GotrUser source, String content) {
        this.source = source;
        this.content = content;
    }

    public GotrUser getSource(){
        return source;
    }

    public String getContent(){
        return content;
    }
}
