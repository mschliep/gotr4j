package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;

import java.util.regex.*;

public class QueryMessage implements GotrMessage{
    private static final String QUERY_STRING = "?";
    public static final Pattern QUERY_PATTERN = Pattern.compile(
            "\\"+QUERY_STRING + GOTR_HEADER+"v(\\d+)\\"+QUERY_STRING+"(.*)");


    private final String message;

    public QueryMessage(){
        this("");
    }

    public QueryMessage(String message){
        this.message = message;
    }

    @Override
    public String encode() {
        return String.format("%s%sv%d%s%s", QUERY_STRING, GOTR_HEADER, 1, QUERY_STRING, message);
    }

    public static boolean isQueryMessage(String message){
        return QUERY_PATTERN.matcher(message).matches();
    }
}
