package net.java.gotr4j.io;

public class PlaintextMessage implements GotrMessage {

    private final String message;
    public PlaintextMessage(String message){
        this.message = message;
    }

    public String toString(){
        return message;
    }

    @Override
    public String encode() {
        return message;
    }
}
