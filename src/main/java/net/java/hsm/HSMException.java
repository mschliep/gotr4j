package net.java.hsm;

public class HSMException extends Exception {

    private final HSMState state;
    private final HSMEvent event;

    public HSMException(HSMState state, HSMEvent event, Throwable cause) {
        super(cause);
        this.state = state;
        this.event = event;
    }
}
