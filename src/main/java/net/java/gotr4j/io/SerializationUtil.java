package net.java.gotr4j.io;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.GotrCrypto;
import net.java.gotr4j.p2p.event.ReceivedEncryptedP2pMessageEvent;
import net.java.gotr4j.p2p.event.ReceivedP2PKeysEvent;
import net.java.gotr4j.group.event.*;
import net.java.gotr4j.p2p.io.P2pMessageType;
import net.java.gotr4j.util.GotrUtil;
import net.java.hsm.HSMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;

public class SerializationUtil {

    private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

    private final GotrCrypto crypto;

    public SerializationUtil(GotrCrypto crypto) {
        this.crypto = crypto;
    }

    public HSMEvent broadcastToEvent(GotrUser source, String broadcast) throws IOException {

        final Matcher queryMatch = QueryMessage.QUERY_PATTERN.matcher(broadcast);
        if(queryMatch.matches()){
            return new GotrQueryEvent(source, queryMatch.group(2));
        }

        final Matcher headerMatch = GotrEncodedMessage.HEADER_PATTERN.matcher(broadcast);

        if(!headerMatch.lookingAt()){
            return new PlaintextMessageEvent(source, broadcast);
        }

        final byte[] content = GotrUtil.fromBase64(broadcast.substring(headerMatch.end()));

        GotrInputStream input = new GotrInputStream(content);

        SessionID sessionID = input.readSessionID();

        byte type = input.readByte();

        if(type == GroupMessageType.FINISH.id){
            return new FinishEvent(sessionID, source);
        }
        if(type == GroupMessageType.REFRESH_KEYS.id){
            return new RefreshKeysEvent(sessionID, source);
        }
        else if(type == GroupMessageType.UNRECOVERABLE_ERROR.id){
            return new UnrecoverableErrorEvent(sessionID, source);
        }
        else if(type == GroupMessageType.ENCRYPTED.id){
            return new ReceivedEncryptedGroupMessageEvent(content, broadcast, source, crypto);
        }
        logger.debug("Weird, no event.");
        return null;
    }

    public HSMEvent p2pMessageToEvent(GotrUser source, String message) throws IOException {
        final Matcher headerMatch = GotrEncodedMessage.HEADER_PATTERN.matcher(message);
        if(!headerMatch.lookingAt()){
            return null;
        }

        byte[] content = GotrUtil.fromBase64(message.substring(headerMatch.end()));

        GotrInputStream input = new GotrInputStream(content);

        SessionID sessionID = input.readSessionID();
        byte type = input.readByte();

        if(type == P2pMessageType.ENCRYPTED.id){
            return new ReceivedEncryptedP2pMessageEvent(content, message, source, crypto);
        }
        else if(type == P2pMessageType.P2P_KEYS.id){
            return new ReceivedP2PKeysEvent(sessionID, input.readPubKeySpec(), input.readPubKeySpec());
        }
        logger.debug("Weird, no event. {} {}", sessionID.toString(), type);
        return null;
    }
}
