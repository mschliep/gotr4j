package net.java.gotr4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class GotrTestRoom {
    private static final Logger logger = LoggerFactory.getLogger(GotrTestRoom.class);

	private final Map<GotrUser, GotrTestHost> users = 
			new HashMap<GotrUser, GotrTestHost>();

    private final String roomName;

    private final ExecutorService singleExecutor;

    public GotrTestRoom(String roomName) {
        this.roomName = roomName;
        this.singleExecutor = Executors.newSingleThreadExecutor();
    }

    protected void sendMessage(final GotrUser source, final GotrUser target, final String message){
        singleExecutor.submit(new Runnable() {
            @Override
            public void run() {

                synchronized (users) {
                    GotrTestHost host = users.get(target);
                    if (host != null) {
                        host.receiveMessage(source, message);
                    }
                }
            }
        });
	}
	
	protected void sendBroadcast(final GotrUser source, final String broadcast){
        singleExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    for (Map.Entry<GotrUser, GotrTestHost> entry : users.entrySet()) {
                        entry.getValue().receiveBroadcast(source, broadcast);
                    }
                }
            }
        });
	}

	public void addHost(final GotrTestHost host) throws ExecutionException, InterruptedException {
	    Future future = singleExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    for (Map.Entry<GotrUser, GotrTestHost> entry : users.entrySet()) {
                        host.addUser(entry.getValue().getUser());
                    }
                    users.put(host.getUser(), host);
                    for (Map.Entry<GotrUser, GotrTestHost> entry : users.entrySet()) {
                        if (!host.getUser().equals(entry.getKey())) {
                            entry.getValue().addUser(host.getUser());
                        }
                    }
                }
            }
        });
	    future.get();
	}

    public Collection<GotrTestHost> getHosts() {
        return users.values();
    }

    public void removeHost(final GotrTestHost host) {
	    Future future = singleExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (users) {
                    users.remove(host.getUser());
                    for (Map.Entry<GotrUser, GotrTestHost> entry : users.entrySet()) {
                        entry.getValue().removeUser(host.getUser());
                    }
                }
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public String getRoomName(){
        return roomName;
    }
}
