package net.java.gotr4j.p2p.smp.event;

public class UserStartSmpEvent implements SmpEvent {

    private final String question;
    private final String answer;

    public UserStartSmpEvent(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
