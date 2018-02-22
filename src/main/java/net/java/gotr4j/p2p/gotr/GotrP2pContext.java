package net.java.gotr4j.p2p.gotr;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.gotr.state.SendSecretState;
import net.java.hsm.HSMContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

public class GotrP2pContext extends HSMContext {

    private static final Logger logger = LoggerFactory.getLogger(GotrP2pContext.class);

    private final P2pContext p2pContext;

    @Override
    public String toString() {
        return String.format("%s: %s", this.getClass().getName(), this.getCurrentState());
    }

    public GotrP2pContext(P2pContext p2pContext, GotrCrypto crypto, ThreadFactory threadFactory) {
        super(p2pContext.getSessionHost(), String.format("GotrP2P HSM: %s -> %s", p2pContext.getLocalUser().toString(), p2pContext.getRemoteUser().toString()), threadFactory);
        this.p2pContext = p2pContext;
        this.setNextState(new SendSecretState());
    }

    public P2pContext getP2pContext() {
        return p2pContext;
    }

    public GotrUser getLocalUser(){
        return p2pContext.getLocalUser();
    }

    public GotrUser getRemoteUser(){
        return p2pContext.getRemoteUser();
    }

    public GotrCrypto getCrypto() {
        return p2pContext.getCrypto();
    }

    public GotrContext getGroupContext() {
        return p2pContext.getGroupContext();
    }
}
