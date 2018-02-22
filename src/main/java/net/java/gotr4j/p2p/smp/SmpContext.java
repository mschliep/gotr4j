package net.java.gotr4j.p2p.smp;

import net.java.gotr4j.GotrSessionHost;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.smp.state.InitSmpState;
import net.java.hsm.HSMContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.concurrent.ThreadFactory;

public class SmpContext extends HSMContext {

    private static final Logger logger = LoggerFactory.getLogger(SmpContext.class);

    private final P2pContext p2pContext;

    @Override
    public String toString() {
        return String.format("%s: %s", this.getClass().getName(), this.getCurrentState());
    }

    public SmpContext(P2pContext p2pContext, ThreadFactory threadFactory) {
        super(p2pContext.getSessionHost(), String.format("SMP HSM: %s -> %s", p2pContext.getLocalUser().toString(), p2pContext.getRemoteUser().toString()), threadFactory);
        this.p2pContext = p2pContext;
        this.setNextState(new InitSmpState());
    }

    public P2pContext getP2pContext(){
        return p2pContext;
    }

    public GotrCrypto getCrypto() {
        return p2pContext.getCrypto();
    }

    public GotrUser getLocaluser(){
        return p2pContext.getLocalUser();
    }

    public GotrUser getRemoteUser(){
        return p2pContext.getRemoteUser();
    }

    public PublicKey getLocalPublicKey(){
        return p2pContext.getSessionHost().getLocalKeyPair().getPublic();
    }

    public PublicKey getRemotePublicKey(){
        return p2pContext.getRemoteLongtermKey();
    }

    public GotrSessionHost getSessionHost(){
        return p2pContext.getSessionHost();
    }
}
