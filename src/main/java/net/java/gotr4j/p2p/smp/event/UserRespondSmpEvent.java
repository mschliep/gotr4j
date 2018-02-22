package net.java.gotr4j.p2p.smp.event;

public class UserRespondSmpEvent implements SmpEvent {
    private final String answer;

    public UserRespondSmpEvent(String question, String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
