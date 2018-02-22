package net.java.gotr4j.group;

import net.java.gotr4j.GotrSessionHost;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.*;
import net.java.gotr4j.group.state.PlaintextState;
import net.java.gotr4j.p2p.P2pContext;
import net.java.hsm.HSMContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

public class GotrContext extends HSMContext {

    private final GotrUser localUser;
    private final GotrSessionHost sessionHost;
    private final Map<GotrUser, P2pContext> p2pStateMachines;
    private final GotrCrypto crypto;

    private volatile SessionID sessionID;
    private final Queue<String> outingMessages = new LinkedList<String>();

    private final boolean gotrRequired;

    private final ThreadFactory threadFactory;

    public GotrContext(GotrUser localUser, GotrSessionHost sessionHost, SessionID sessionID, boolean gotrRequired, GotrCrypto crypto, ThreadFactory threadFactory) throws GotrException {
        super(sessionHost, String.format("GOTR HSM: %s", localUser.toString()), threadFactory);
        this.threadFactory = threadFactory;
        this.localUser = localUser;
        this.sessionHost = sessionHost;
        this.sessionID = sessionID;
        this.gotrRequired = gotrRequired;
        p2pStateMachines = new ConcurrentHashMap<GotrUser, P2pContext>();
        this.crypto = crypto;
        this.setNextState(new PlaintextState());
    }

    public GotrUser getLocalUser(){
        return localUser;
    }

    public GotrSessionHost getSessionHost(){
        return sessionHost;
    }

    public P2pContext getP2pContext(GotrUser user){
        return p2pStateMachines.get(user);
    }

    public void addP2pContext(GotrUser user, P2pContext p2pContext) {
        if(!p2pStateMachines.containsKey(user)){
            p2pStateMachines.put(user, p2pContext);
        }
    }

    public void removeP2pContext(GotrUser user){
        P2pContext temp = p2pStateMachines.remove(user);
        if(temp != null){
            temp.shutdown();
        }
    }

    public GotrCrypto getCrypto() {
        return crypto;
    }

    /**
     * Get the size of the group, counting the local user.
     * @return size of the group
     */
    public int getGroupSize(){
        return p2pStateMachines.size()+1;
    }

    public Queue<String> getOutingMessages(){
        return outingMessages;
    }

    public Collection<P2pContext> getP2pContexts() {
        return p2pStateMachines.values();
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    @Override
    public String toString() {
        return String.format("%s: State: %s", this.getClass().getName(), this.getCurrentState());
    }

    public boolean isGotrRequired() {
        return gotrRequired;
    }

    public P2pContext getOrCreateP2pContext(GotrUser remote) throws GotrException {
        synchronized (p2pStateMachines){
            P2pContext p2pContext = p2pStateMachines.get(remote);
            if (p2pContext == null) {
                p2pContext = new P2pContext(localUser, remote, this, sessionHost, sessionID, crypto, threadFactory);
                p2pStateMachines.put(remote, p2pContext);
                if(isRunning()) {
                    try {
                        p2pContext.start();
                    } catch (Exception e) {
                        throw new GotrException(e);
                    }
                }
            }
            return p2pContext;
        }
    }

    @Override
    public void start() throws Exception {
        synchronized (p2pStateMachines) {
            super.start();
            for (Map.Entry<GotrUser, P2pContext> p2pContext : p2pStateMachines.entrySet()) {
                p2pContext.getValue().start();
            }
        }
    }

    @Override
    public void shutdown() {
        synchronized (p2pStateMachines) {
            super.shutdown();
            for (Map.Entry<GotrUser, P2pContext> p2pContext : p2pStateMachines.entrySet()) {
                p2pContext.getValue().shutdown();
            }
        }
    }
}
