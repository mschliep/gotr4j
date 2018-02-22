package net.java.gotr4j.group.event;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.p2p.P2pContext;
import net.java.hsm.HSMEvent;

public class AddUserEvent implements HSMEvent {

    private final GotrUser user;
    private final P2pContext p2pContext;

    public AddUserEvent(GotrUser user, P2pContext p2pContext) {
        this.user = user;
        this.p2pContext = p2pContext;
    }

    public GotrUser getUser(){
        return user;
    }

    public P2pContext getP2pContext() {
        return p2pContext;
    }
}
