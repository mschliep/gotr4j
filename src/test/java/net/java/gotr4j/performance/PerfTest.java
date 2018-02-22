package net.java.gotr4j.performance;

import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.GotrTestHost;
import net.java.gotr4j.GotrTestRoom;
import net.java.gotr4j.GotrUser;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PerfTest {
    private final GotrUser[] users = {
            new GotrUser("test1"),
            new GotrUser("test2"),
            new GotrUser("test3"),
            new GotrUser("test4"),
            new GotrUser("test5"),
            new GotrUser("test6"),
            new GotrUser("test7"),
            new GotrUser("test8"),
            new GotrUser("test9"),
            new GotrUser("test10"),
            new GotrUser("test11"),
            new GotrUser("test12"),
            new GotrUser("test13"),
            new GotrUser("test14"),
            new GotrUser("test15"),
            new GotrUser("test16"),
            new GotrUser("test17"),
            new GotrUser("test18"),
            new GotrUser("test19"),
            new GotrUser("test20"),
            new GotrUser("test21"),
            new GotrUser("test22"),
            new GotrUser("test23"),
            new GotrUser("test24"),
            new GotrUser("test25"),
            new GotrUser("test26"),
            new GotrUser("test27"),
            new GotrUser("test28"),
            new GotrUser("test29"),
            new GotrUser("test30"),
    };


    /*//@Ignore
    @Test
    public void measureSetup() throws Exception {
        GotrTestRoom room = new GotrTestRoom("test_room");

        Map<GotrUser, GotrTestHost> hosts = new HashMap<>();

        //for(int i=0; i<users.length; i++) {
        for(int i=0; i<30; i++) {
            GotrTestHost host = new GotrTestHost(room, users[i]);
            hosts.put(users[i], host);
            room.addHost(host);
            host.startStateMachine();
        }
        GotrTestHost host = hosts.get(users[0]);

        Map<GotrUser, Long> cpuTimes = new HashMap<>();

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            //cpuTimes.put(entry.getKey(), entry.getValue().getCPUTime());
        }

        host.start();

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            entry.getValue().awaitSecure();
            long before = cpuTimes.get(entry.getKey());
            //cpuTimes.put(entry.getKey(), entry.getValue().getCPUTime() - before);
        }

        for(Map.Entry<GotrUser, Long> entry: cpuTimes.entrySet()){
            System.out.println(entry.getValue());
        }

        System.out.println("Sending...");

        host.broadcastMessage("Here");

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            entry.getValue().awaitBroadcasts(1);
            long before = cpuTimes.get(entry.getKey());
            //cpuTimes.put(entry.getKey(), entry.getValue().getCPUTime() - before);
        }

        for(Map.Entry<GotrUser, Long> entry: cpuTimes.entrySet()){
            System.out.println(entry.getValue());
        }

    }

    @Test
    public void measureAdd() throws Exception {
        GotrTestRoom room = new GotrTestRoom("test_room");

        Map<GotrUser, GotrTestHost> hosts = new HashMap<>();

        for(int i=0; i<29; i++) {
            GotrTestHost host = new GotrTestHost(room, users[i]);
            hosts.put(users[i], host);
            room.addHost(host);
            host.startStateMachine();
        }
        GotrTestHost host = hosts.get(users[0]);

        Map<GotrUser, Long> cpuTimes = new HashMap<>();

        System.out.println("before");
        host.start();

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            entry.getValue().awaitSecure();
        }
        System.out.println("after");
        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            //cpuTimes.put(entry.getKey(), entry.getValue().getCPUTime());
        }

        GotrUser lateUser = users[hosts.size()];
        cpuTimes.put(lateUser, 0l);

        GotrTestHost late = new GotrTestHost(room, lateUser);
        hosts.put(lateUser, late);
        room.addHost(late);
        late.startStateMachine();

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            entry.getValue().awaitSecure();
            long before = cpuTimes.get(entry.getKey());
            long current = entry.getValue().getCPUTime();
            cpuTimes.put(entry.getKey(), current);
            System.out.println(current - before);
        }

        System.out.println("leave...");

        hosts.remove(lateUser);
        room.removeHost(late);
        late.shutdown();

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            entry.getValue().awaitSecure();
        }

        for(Map.Entry<GotrUser, GotrTestHost> entry: hosts.entrySet()){
            long before = cpuTimes.get(entry.getKey());
            long current = entry.getValue().getCPUTime();
            cpuTimes.put(entry.getKey(), current);
            System.out.println(current - before);
        }
    }*/

}
