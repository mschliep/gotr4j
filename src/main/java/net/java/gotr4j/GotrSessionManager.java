package net.java.gotr4j;

import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.group.event.*;
import net.java.gotr4j.group.state.GotrState;
import net.java.gotr4j.group.state.ShareSecretState;
import net.java.gotr4j.io.SerializationUtil;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.smp.event.UserAbortSmpEvent;
import net.java.gotr4j.p2p.smp.event.UserRespondSmpEvent;
import net.java.gotr4j.p2p.smp.event.UserStartSmpEvent;
import net.java.hsm.HSMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class GotrSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(GotrSessionManager.class);

    private static final String ROOM_TRIGGER = "_gotr";

    private final GotrSessionHost host;
    private final GotrUser user;
    private final GotrContext gotrStateMachine;
    private final GotrCrypto crypto;
    private final SerializationUtil serializationUtil;

    private final SessionID sessionID;

    public GotrSessionManager(GotrSessionHost host, GotrUser user, String roomName) throws GotrException{
        this(host, user, roomName, Executors.defaultThreadFactory());
    }

    public GotrSessionManager(GotrSessionHost host, GotrUser user, String roomName, ThreadFactory threadFactory) throws GotrException {
        this.host = host;
        this.user = user;
        this.crypto = new GotrCrypto();
        this.sessionID = crypto.generateSessionID(roomName);

        boolean gotrRequired = roomName.contains(ROOM_TRIGGER);

        gotrStateMachine = new GotrContext(user, host, sessionID, gotrRequired, crypto, threadFactory);
        serializationUtil = new SerializationUtil(crypto);

        if(gotrStateMachine.isGotrRequired()){
            gotrStateMachine.setNextState(new ShareSecretState());
        }
    }

    public void start() throws GotrException {
        try {
            gotrStateMachine.handelEvent(new UserStartEvent());
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public void handleBroadcast(GotrUser source, String broadcast) throws GotrException {
        try {
            HSMEvent event = serializationUtil.broadcastToEvent(source, broadcast);
            gotrStateMachine.handelEvent(event);
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public void handleMessage(GotrUser source, String message) throws GotrException {
        P2pContext p2pContext = gotrStateMachine.getOrCreateP2pContext(source);
        try {
            HSMEvent event = serializationUtil.p2pMessageToEvent(source, message);
            p2pContext.handelEvent(event);
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public void broadcastMessage(String broadcast) throws GotrException {
        UserBroadcastEvent event = new UserBroadcastEvent(broadcast);
        try {
            gotrStateMachine.handelEvent(event);
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public void addUser(GotrUser user) throws GotrException{
        try {
            synchronized (gotrStateMachine) {
                P2pContext p2pContext = gotrStateMachine.getOrCreateP2pContext(user);
                if(gotrStateMachine.isRunning()) {
                    gotrStateMachine.handelEvent(new AddUserEvent(user, p2pContext));
                }
            }
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public void removeUser(GotrUser user) throws GotrException {
        try {
            synchronized (gotrStateMachine) {
                if(gotrStateMachine.isRunning()) {
                    gotrStateMachine.handelEvent(new RemoveUserEvent(user));
                }
                else{
                    gotrStateMachine.removeP2pContext(user);
                }
            }
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public GotrSessionState getState(){
        GotrState state = (GotrState) gotrStateMachine.getCurrentState();
        if(state != null) {
            return state.getSessionState();
        }
        else{
            return null;
        }
    }

    public SessionID getSessionID(){
        return gotrStateMachine.getSessionID();
    }

    public void respondSmp(GotrUser user, String question, String answer)
            throws GotrException {
        P2pContext p2pContext = gotrStateMachine.getP2pContext(user);
        p2pContext.handelEvent(new UserRespondSmpEvent(question, answer));
    }

    public void initSmp(GotrUser user, String question, String answer)
            throws GotrException {

        P2pContext p2pContext = gotrStateMachine.getP2pContext(user);
        p2pContext.handelEvent(new UserStartSmpEvent(question, answer));

    }

    public void abortSmp(GotrUser user) throws GotrException {
        P2pContext p2pContext = gotrStateMachine.getP2pContext(user);
        p2pContext.handelEvent(new UserAbortSmpEvent());
    }

    public int getSize(){
        return gotrStateMachine.getGroupSize();
    }

    public PublicKey getRemotePublicKey(GotrUser user) throws GotrException {
        P2pContext userContext = gotrStateMachine.getOrCreateP2pContext(user);

        return userContext.getRemoteLongtermKey();
    }

    public void refreshKeys(){
        gotrStateMachine.handelEvent(new UserRefreshKeysEvent());
    }

    public void end() throws GotrException {
        if(gotrStateMachine.isGotrRequired()){
            return;
        }
        try {
            gotrStateMachine.handelEvent(new UserFinishEvent());
        } catch (Exception e)
        {
            throw new GotrException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s: StateMachine: %s", this.getClass().getName(), gotrStateMachine);
    }

    public void startStateMachie() throws Exception {
        synchronized (gotrStateMachine) {
            gotrStateMachine.start();
        }
    }

    public void shutdownStateMachine(){
        synchronized (gotrStateMachine) {
            gotrStateMachine.shutdown();
        }
    }


}
