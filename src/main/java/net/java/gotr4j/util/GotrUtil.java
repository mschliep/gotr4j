package net.java.gotr4j.util;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.io.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;

public class GotrUtil {

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static String decodeUTF8(byte[] bytes){
        return decodeUTF8(bytes, 0, bytes.length);
    }

    public static String decodeUTF8(byte[] bytes, int offset, int length){
        return new String(bytes, offset, length, UTF8_CHARSET);
    }

    public static byte[] encodeUTF8(String string){
        return string.getBytes(UTF8_CHARSET);
    }

    public static String toBase64(byte[] val){
        return DatatypeConverter.printBase64Binary(val);
    }

    public static byte[] fromBase64(String val){
        return DatatypeConverter.parseBase64Binary(val);
    }

    public static boolean isEncodedGotrMessage(String message){
        return (message != null && GotrEncodedMessage.HEADER_PATTERN.matcher(message).lookingAt());
    }

    /**
     * Get the {@link net.java.gotr4j.SessionID} of a GOTR message.
     *
     * @param message gotr message
     * @return SessionID, null if it is not a GOTR message
     */
    public static SessionID getEncodedMessageSessionID(String message) throws IOException
    {
        Matcher matcher = GotrEncodedMessage.HEADER_PATTERN.matcher(message);

        if(matcher.lookingAt()){
            final byte[] content = GotrUtil.fromBase64(message.substring(matcher.end()));

            GotrInputStream input = new GotrInputStream(content);

            SessionID sessionID =  input.readSessionID();

            return sessionID;
        }
        else{
            return null;
        }
    }

    public static boolean isGotrBroadcast(String broadcast){
        return isQueryMessage(broadcast) || isEncodedGotrMessage(broadcast);
    }

    public static boolean isQueryMessage(String broadcast) {
        return QueryMessage.QUERY_PATTERN.matcher(broadcast).matches();
    }
}
