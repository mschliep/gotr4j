package net.java.gotr4j.io;

import net.java.gotr4j.crypto.GotrException;

import java.io.IOException;

public interface GotrMessage {

    public static final String GOTR_HEADER = "GOTR";
    public static final String GOTR_ENCODED = ":";

    public String encode() throws IOException, GotrException;

}
