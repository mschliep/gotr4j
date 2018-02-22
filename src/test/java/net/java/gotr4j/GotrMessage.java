package net.java.gotr4j;

public class GotrMessage {

	private final GotrUser source;
	private final String message;
    private final boolean broadcast;
	
	public GotrMessage(GotrUser source, String message, boolean broadcast) {
		super();
		this.source = source;
		this.message = message;
        this.broadcast = broadcast;
	}

	public GotrUser getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}

    public boolean isBroadcast(){
        return broadcast;
    }
	
}
