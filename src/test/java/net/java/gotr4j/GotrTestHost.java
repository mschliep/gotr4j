package net.java.gotr4j;

import java.security.KeyPair;
import java.util.*;

import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.crypto.GotrException;
import net.java.hsm.HSMException;

public class GotrTestHost implements GotrSessionHost {
	
	private final List<String> broadcasts = new ArrayList<String>();

	private GotrSessionManager session;
	private final GotrTestRoom room;
	private final GotrUser user;
    private final KeyPair keyPair;

    private final Set<GotrUser> verified = new HashSet<GotrUser>();

    private GotrSmpListener smpListener;

    private final Object stateWaiter = new Object();

	public GotrTestHost(GotrTestRoom room, GotrUser user){
		this.room = room;
		this.user = user;
        try {
            session = new GotrSessionManager(this, user, this.room.getRoomName());
            keyPair = new GotrCrypto().generateKeyPair();
        } catch (GotrException e) {
            throw new RuntimeException(e);
        }
    }
	
	public void start(){
		try {
			session.start();
		} catch (GotrException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void receiveMessage(GotrUser source, String message){
        try {
            session.handleMessage(source, message);
        } catch (GotrException e) {
            e.printStackTrace();
        }
    }
	
	protected void receiveBroadcast(GotrUser source, String broadcast){
        try {
            session.handleBroadcast(source, broadcast);
        } catch (GotrException e) {
            e.printStackTrace();
        }
    }

    @Override
	public void sendMessage(GotrUser target, String message) {
		room.sendMessage(user, target, message);
	}

	@Override
	public void sendBroadcast(String broadcast) {
		room.sendBroadcast(user, broadcast);
	}

	@Override
	public void handleBroadcast(GotrUser sender, String broadcast) {
	}

    @Override
    public void broadcastConfirmed(GotrUser user, String broadcast) {
        synchronized (broadcasts) {
            broadcasts.add(broadcast);
            broadcasts.notifyAll();
        }
    }

    @Override
    public KeyPair getLocalKeyPair() {
        return keyPair;
    }

    @Override
    public void verify(GotrUser user, String fingerprint) {
        synchronized (verified){
            verified.add(user);
            verified.notifyAll();
        }
    }

    @Override
    public void stateChanged(GotrSessionState state) {
        synchronized (stateWaiter){
            stateWaiter.notifyAll();
        }
    }

    @Override
    public void askForSecret(GotrUser remoteUser, String question) {
        if(smpListener != null){
            String response = smpListener.getResponse(remoteUser, question);
            try{
                session.respondSmp(remoteUser, question, response);
            } catch (GotrException e){
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sessionFinished(GotrUser source) {

    }

    @Override
    public void unrecoverableError(GotrUser source) {

    }

    @Override
    public void recoverableError(GotrUser source) {

    }

    @Override
    public void receivedUnsentMessage(GotrUser source) {

    }

    @Override
    public void broadcastToEmptySecureRoom(String message) {

    }

    @Override
    public void smpAborted(GotrUser remoteUser) {

    }

    @Override
    public void smpError(GotrUser user) {

    }

    @Override
    public void unverify(GotrUser user, String fingerprint) {

    }

    public List<String> getBroadcasts(){
        synchronized (broadcasts){
            List<String> result = new ArrayList<String>(broadcasts);
            return result;
        }
	}
	
	public GotrUser getUser(){
		return user;
	}
	
	public void broadcastMessage(String message) throws GotrException {
		session.broadcastMessage(message);
	}

    public void addUser(GotrUser user) {
        try {
            session.addUser(user);
        } catch (GotrException e) {
            throw new RuntimeException(e);
        }
    }

    public GotrSessionManager getSession() {
        return session;
    }

    public void setSmpListener(GotrSmpListener smpListener){
        this.smpListener = smpListener;
    }

    public boolean isVerified(GotrUser user) {
        return verified.contains(user);
    }

    public void removeUser(GotrUser user) {
        try {
            session.removeUser(user);
        } catch (GotrException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleException(HSMException e) {
        e.printStackTrace();
    }

    public void awaitSecure() throws InterruptedException {
        awaitState(GotrSessionState.SECURE);
    }

    public void awaitPlaintext() throws InterruptedException {
        awaitState(GotrSessionState.PLAINTEXT);
    }

    public void awaitState(GotrSessionState goal) throws InterruptedException {
        synchronized (stateWaiter){
            while(true){
                GotrSessionState state = session.getState();
                if(state == null || !state.equals(goal)) {
                    stateWaiter.wait();
                }
                else{
                    return;
                }
            }
        }
    }

    public void awaitBroadcasts(int count) throws InterruptedException {
        synchronized (broadcasts){
            while(true){
                if(broadcasts.size() != count) {
                    broadcasts.wait();
                }
                else{
                    return;
                }
            }
        }
    }

    public void awaitSMP(GotrUser user) throws InterruptedException {
        synchronized (verified){
            while(true){
                if(!verified.contains(user)) {
                    verified.wait();
                }
                else{
                    return;
                }
            }
        }
    }

    public void shutdown() {
        session.shutdownStateMachine();
    }

    public void stop() throws GotrException {
        session.end();
    }

    public void startStateMachine() throws Exception {
        session.startStateMachie();
    }
}
