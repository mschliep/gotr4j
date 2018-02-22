package net.java.gotr4j;

import net.java.hsm.HSMExceptionHandler;

import java.security.KeyPair;

public interface GotrSessionHost extends HSMExceptionHandler {

    /**
     * Send a message to the target.
     * @param target target
     * @param message message
     */
	public void sendMessage(GotrUser target, String message);

    /**
     * Send a broadcast.
     * @param broadcast message
     */
	public void sendBroadcast(String broadcast);

    /**
     * Handle a decrypted broadcast message.
     * @param source sender
     * @param broadcast message
     */
	public void handleBroadcast(GotrUser source, String broadcast);

	public void broadcastConfirmed(GotrUser user, String broadcast);

    /**
     * Get the {@link java.security.KeyPair} for this session.
     * @return keypair of the user
     */
    public KeyPair getLocalKeyPair();

    /**
     * {@link net.java.gotr4j.GotrUser} has been verified.
     */
    public void verify(GotrUser user, String fingerprint);

    public void stateChanged(GotrSessionState state);

    public void askForSecret(GotrUser remoteUser, String question);

    public void sessionFinished(GotrUser source);

    public void unrecoverableError(GotrUser source);

    public void recoverableError(GotrUser source);

    public void receivedUnsentMessage(GotrUser source);

    public void broadcastToEmptySecureRoom(String message);

    public void smpAborted(GotrUser user);

    public void smpError(GotrUser user);

    public void unverify(GotrUser user, String fingerprint);
}
