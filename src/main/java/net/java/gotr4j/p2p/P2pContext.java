package net.java.gotr4j.p2p;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionHost;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.p2p.gotr.GotrP2pContext;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.state.PlaintextState;
import net.java.hsm.HSMContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.concurrent.ThreadFactory;

public class P2pContext extends HSMContext {

    private static final Logger logger = LoggerFactory.getLogger(P2pContext.class);

    private final GotrUser localUser;
    private final GotrUser remoteUser;
    private final GotrContext groupContext;
    private final GotrSessionHost sessionHost;
    private final GotrCrypto crypto;
    private SessionID sessionID;

    private final GotrP2pContext gotrP2pContext;
    private final SmpContext smpContext;

    private PublicKey remoteLongtermKey;

    public P2pContext(GotrUser localUser, GotrUser remoteUser, GotrContext groupContext, GotrSessionHost sessionHost, SessionID sessionID, GotrCrypto crypto, ThreadFactory threadFactory) {
        super(sessionHost, String.format("P2P HSM: %s -> %s", localUser.toString(), remoteUser.toString()), threadFactory);
        this.localUser = localUser;
        this.remoteUser = remoteUser;
        this.groupContext = groupContext;
        this.sessionHost = sessionHost;
        this.sessionID = sessionID;
        this.crypto = crypto;

        this.gotrP2pContext = new GotrP2pContext(this, crypto, threadFactory);

        this.smpContext = new SmpContext(this, threadFactory);

        this.setNextState(new PlaintextState());
    }

    public GotrUser getLocalUser() {
        return localUser;
    }

    public GotrUser getRemoteUser() {
        return remoteUser;
    }

    public GotrContext getGroupContext() {
        return groupContext;
    }

    public GotrSessionHost getSessionHost() {
        return sessionHost;
    }

    public GotrCrypto getCrypto() {
        return crypto;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public GotrP2pContext getGotrP2pContext() {
        return gotrP2pContext;
    }

    public SmpContext getSmpContext() {
        return smpContext;
    }

    public PublicKey getRemoteLongtermKey() {
        return remoteLongtermKey;
    }

    public void setRemoteLongtermKey(PublicKey remoteLongtermKey) {
        this.remoteLongtermKey = remoteLongtermKey;
    }

    @Override
    public void start() throws Exception {
        super.start();
        this.gotrP2pContext.start();
        this.smpContext.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.gotrP2pContext.shutdown();
        this.smpContext.shutdown();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.getClass().getName(), this.getCurrentState());
    }
}
