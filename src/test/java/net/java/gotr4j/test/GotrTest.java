package net.java.gotr4j.test;

import net.java.gotr4j.*;
import net.java.gotr4j.crypto.GotrException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GotrTest {
	private final GotrUser[] users = {new GotrUser("bob"), 
		new GotrUser("john"),
        new GotrUser("max")};

    private static final Logger logger = LoggerFactory.getLogger(GotrTest.class);

	private final Set<GotrTestHost> hosts = new HashSet<GotrTestHost>();
	private GotrTestRoom room = null;

    private final Map<GotrUser, GotrTestHost> userToHostMap =
            new HashMap<GotrUser, GotrTestHost>();
	
	public GotrTest() throws GotrException {
		
	}
	
	@Before
	public void before() throws Exception {
        room = new GotrTestRoom("TestRoom");
		for(GotrUser user: users){
			GotrTestHost host = new GotrTestHost(room, user);
            userToHostMap.put(user, host);
			hosts.add(host);
			room.addHost(host);
		}

        for(GotrTestHost host: hosts){
            host.startStateMachine();
        }
	}

    @After
    public void after(){
        for(GotrTestHost host: hosts){
            host.shutdown();
        }
        hosts.clear();
        userToHostMap.clear();
    }

    @Test
    public void testSingleUser() throws Exception {
        GotrTestRoom room = new GotrTestRoom("SingleRoom");
        GotrUser user = new GotrUser("Mike");
        GotrTestHost host = new GotrTestHost(room, user);
        room.addHost(host);

        host.startStateMachine();

        host.start();
        host.awaitSecure();
        Assert.assertEquals(GotrSessionState.SECURE, host.getSession().getState());

    }

    @Test
    public void testSingleUserBroadcast() throws Exception {
        String plaintext = "Test Single Bcast$\n";
        GotrTestRoom room = new GotrTestRoom("SingleRoom");
        GotrUser user = new GotrUser("Mike");
        GotrTestHost host = new GotrTestHost(room, user);
        room.addHost(host);

        host.startStateMachine();


        host.start();
        host.awaitSecure();

        host.broadcastMessage(plaintext);
        host.awaitBroadcasts(1);
        Assert.assertEquals(1, host.getBroadcasts().size());
        Assert.assertEquals(plaintext, host.getBroadcasts().get(0));
    }
	
	@Test
	public void testStart() throws InterruptedException {
		hosts.iterator().next().start();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, host.getSession().getState());
        }
	}

    @Test
    public void testFinish() throws InterruptedException, GotrException {
        hosts.iterator().next().start();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
        }

        hosts.iterator().next().stop();
        for(GotrTestHost host: hosts){
            host.awaitPlaintext();
            Assert.assertEquals(GotrSessionState.PLAINTEXT, host.getSession().getState());
        }

    }

    @Test
    public void testRestart() throws InterruptedException, GotrException {
        hosts.iterator().next().start();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
        }

        hosts.iterator().next().stop();
        for(GotrTestHost host: hosts){
            host.awaitPlaintext();
        }

        hosts.iterator().next().start();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, host.getSession().getState());
        }
    }

    @Test
    public void testRefreshKeys() throws InterruptedException, GotrException {
        hosts.iterator().next().start();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
        }

        hosts.iterator().next().getSession().refreshKeys();
        for(GotrTestHost host: hosts){
            host.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, host.getSession().getState());
        }

    }

    @Test
    public void testSmp() throws GotrException, InterruptedException {
        final String question = "What the hell?";
        final String answer = "Good and you?";

        GotrTestHost host1 = userToHostMap.get(users[0]);
        GotrTestHost host2 = userToHostMap.get(users[1]);

        host2.setSmpListener(new GotrSmpListener()
        {
            @Override
            public String getResponse(GotrUser user, String question)
            {
                return answer;
            }
        });

        host1.start();
        host1.awaitSecure();

        host1.getSession().initSmp(host2.getUser(), question, answer);

        host1.awaitSMP(host2.getUser());
        Assert.assertTrue(host1.isVerified(host2.getUser()));
        host2.awaitSMP(host1.getUser());
        Assert.assertTrue(host2.isVerified(host1.getUser()));
    }

    @Test
    public void testSingleBroadcast() throws Exception{
        String plaintext = "Testing 123!@#!@\n";

        GotrTestHost host = hosts.iterator().next();

        host.start();

        host.broadcastMessage(plaintext);

        for(GotrTestHost temp: hosts){
            temp.awaitBroadcasts(1);
            Assert.assertEquals(1, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext, temp.getBroadcasts().get(0));
        }
    }

    @Test
    public void testLateJoin() throws Exception{
        String plaintext = "Testing 123!@#!@\n";
        GotrUser lateUser = new GotrUser("lateguy");

        GotrTestHost host = hosts.iterator().next();

        host.start();

        host.awaitSecure();

        GotrTestHost lateHost = new GotrTestHost(room, lateUser);
        userToHostMap.put(lateUser, lateHost);
        hosts.add(lateHost);
        room.addHost(lateHost);

        lateHost.startStateMachine();

        host.broadcastMessage(plaintext);

        for(GotrTestHost temp: hosts){
            temp.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, temp.getSession().getState());
            temp.awaitBroadcasts(1);
            Assert.assertEquals(1, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext, temp.getBroadcasts().get(0));
        }
    }

    @Test
    public void testLeave() throws Exception{
        String plaintext = "Testing 123!@#!@\n";
        GotrUser lateUser = new GotrUser("lateguy");

        GotrTestHost host = hosts.iterator().next();

        GotrTestHost lateHost = new GotrTestHost(room, lateUser);
        userToHostMap.put(lateUser, lateHost);
        hosts.add(lateHost);
        room.addHost(lateHost);

        lateHost.startStateMachine();

        host.start();

        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }

        room.removeHost(lateHost);
        hosts.remove(lateHost);
        lateHost.shutdown();

        lateHost.awaitSecure();
        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }
        host.broadcastMessage(plaintext);

        for(GotrTestHost temp: hosts){
            temp.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, temp.getSession().getState());
            temp.awaitBroadcasts(1);
            Assert.assertEquals(1, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext, temp.getBroadcasts().get(0));
            Assert.assertEquals(3, temp.getSession().getSize());
        }
    }

    @Test
    public void testJoinAfterFirstBroadcast() throws Exception{
        String plaintext = "Testing 123!@#!@\n";
        String plaintext2 = "Testing 123!@#!@NOW\n";
        GotrUser lateUser = new GotrUser("lateguy");

        GotrTestHost host = hosts.iterator().next();

        host.start();

        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }
        host.broadcastMessage(plaintext);
        for(GotrTestHost temp: hosts) {
            temp.awaitBroadcasts(1);
        }
        GotrTestHost lateHost = new GotrTestHost(room, lateUser);
        userToHostMap.put(lateUser, lateHost);
        hosts.add(lateHost);
        room.addHost(lateHost);

        lateHost.startStateMachine();

        lateHost.awaitSecure();
        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }
        host.broadcastMessage(plaintext2);

        for(GotrTestHost temp: hosts){
            temp.awaitSecure();
            Assert.assertEquals(GotrSessionState.SECURE, temp.getSession().getState());
            if(lateHost.equals(temp)){
                temp.awaitBroadcasts(1);
                Assert.assertEquals(plaintext2, temp.getBroadcasts().get(0));
            }
            else {
                temp.awaitBroadcasts(2);
                Assert.assertEquals(plaintext2, temp.getBroadcasts().get(1));
            }
        }
    }

    @Test
    public void testTwoBroadcasts() throws Exception{
        String plaintext1 = "Testing 1!@#!@";
        String plaintext2 = "Testing 12!@#!@";

        GotrTestHost host = hosts.iterator().next();

        host.start();

        host.awaitSecure();
        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }

        host.broadcastMessage(plaintext1);
        host.broadcastMessage(plaintext2);

        for(GotrTestHost temp: hosts){
            temp.awaitBroadcasts(2);
            Assert.assertEquals(2, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext1, temp.getBroadcasts().get(0));
            Assert.assertEquals(plaintext2, temp.getBroadcasts().get(1));
        }
    }

    @Test
    public void testThreeBroadcasts() throws Exception{
        String plaintext1 = "Testing 1!@#!@";
        String plaintext2 = "Testing 12!@#!@";
        String plaintext3 = "Testing 123!@#!@";

        GotrTestHost host = hosts.iterator().next();

        host.start();

        host.awaitSecure();
        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }

        host.broadcastMessage(plaintext1);
        host.broadcastMessage(plaintext2);
        host.broadcastMessage(plaintext3);

        for(GotrTestHost temp: hosts){
            temp.awaitBroadcasts(3);
            Assert.assertEquals(3, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext1, temp.getBroadcasts().get(0));
            Assert.assertEquals(plaintext2, temp.getBroadcasts().get(1));
            Assert.assertEquals(plaintext3, temp.getBroadcasts().get(2));
        }
    }

    @Test
    public void testFourBroadcasts() throws Exception{
        String plaintext1 = "Testing 1!@#!@";
        String plaintext2 = "Testing 12!@#!@";
        String plaintext3 = "Testing 123!@#!@";
        String plaintext4 = "Testing 1234!@#!@";

        GotrTestHost host = hosts.iterator().next();

        host.start();

        host.awaitSecure();
        for(GotrTestHost temp: hosts) {
            temp.awaitSecure();
        }

        host.broadcastMessage(plaintext1);
        host.broadcastMessage(plaintext2);
        host.broadcastMessage(plaintext3);
        host.broadcastMessage(plaintext4);

        for(GotrTestHost temp: hosts){
            temp.awaitBroadcasts(4);
            Assert.assertEquals(4, temp.getBroadcasts().size());
            Assert.assertEquals(plaintext1, temp.getBroadcasts().get(0));
            Assert.assertEquals(plaintext2, temp.getBroadcasts().get(1));
            Assert.assertEquals(plaintext3, temp.getBroadcasts().get(2));
            Assert.assertEquals(plaintext4, temp.getBroadcasts().get(3));
        }
    }
}
