package net.java.gotr4j.p2p.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.group.event.GotrQueryEvent;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.event.ReceivedP2PKeysEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

public class PlaintextState extends P2pState {
    private static final Logger logger = LoggerFactory.getLogger(PlaintextState.class);

    @Override
    protected void handleGotrQueryEvent(P2pContext context, GotrQueryEvent event) throws GotrException {
        context.setNextState(new ShareKeys());
    }

    @Override
    protected void handleReceivedEphemeralKeyEvent(P2pContext context, ReceivedP2PKeysEvent event) throws InvalidKeySpecException, InvalidKeyException, GotrException {
        context.deferEvent(event);
    }
}
