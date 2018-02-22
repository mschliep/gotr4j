package net.java.gotr4j.crypto;

import net.java.gotr4j.GotrUser;

public class Broadcast {

    private final String text;
    private final GotrUser sender;

    private final byte[] digest;


    public Broadcast(String text, GotrUser sender, byte[] digest) {
        this.text = text;
        this.sender = sender;
        this.digest = digest;
    }

    public String getText() {
        return text;
    }

    public GotrUser getSender() {
        return sender;
    }

    public byte[] getDigest() {
        return digest;
    }
}
