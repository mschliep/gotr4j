package net.java.gotr4j.group.state;

import net.java.gotr4j.group.GotrContext;
import net.java.gotr4j.GotrSessionState;
import net.java.gotr4j.GotrUser;
import net.java.gotr4j.crypto.Broadcast;
import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.crypto.UserKeys;
import net.java.gotr4j.crypto.UserSecret;
import net.java.gotr4j.group.event.DigestVerificationReceivedEvent;
import net.java.gotr4j.group.event.ReceivedEncryptedGroupMessageEvent;
import net.java.gotr4j.group.event.UserBroadcastEvent;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.gotr.event.SendVerifyDigestEvent;
import net.java.gotr4j.p2p.state.ShareKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VerifyDigestState extends GotrState {
    private static final Logger logger = LoggerFactory.getLogger(ShareKeys.class);

    private final Broadcast broadcast;
    private final Map<GotrUser, byte[]> signatures;
    private final Map<GotrUser, byte[]> signatureVerifications = new HashMap<GotrUser, byte[]>();
    private final UserKeys secrets;

    private final UserKeys nextSecrets;
    private final KeyPair nextKeyPair;

    //private final Map<GotrUser, byte[]> secretVerifications = new HashMap<GotrUser, byte[]>();

    public VerifyDigestState(Broadcast broadcast, Map<GotrUser, byte[]> signatures, UserKeys secrets, UserKeys nextSecrets, KeyPair nextKeyPair) {
        this.broadcast = broadcast;
        this.signatures = signatures;
        this.secrets = secrets;
        this.nextSecrets = nextSecrets;
        this.nextKeyPair = nextKeyPair;
    }

    @Override
    protected void handleDigestVerificationReceivedEvent(GotrContext context, DigestVerificationReceivedEvent event) throws NoSuchAlgorithmException, GotrException {
        signatureVerifications.put(event.getRemoteUser(), event.getDigestVerification());
        //secretVerifications.put(event.getRemoteUser(), event.getSecretVerification());

        if(signatureVerifications.size() == context.getGroupSize()){
            if(!verifyDigests(context)){
                throw new GotrException("Invalid digest signature.");
            }
//            else if(!verifySecrets(context)){
//                throw new GotrException("Group Consistency check failed.");
//            }
            else{
                if (context.getLocalUser().equals(broadcast.getSender())) {
                    context.getOutingMessages().poll();
                }
                context.getSessionHost().broadcastConfirmed(broadcast.getSender(), broadcast.getText());
                context.setNextState(new AwaitingBroadcastState(nextSecrets, nextKeyPair, false));
            }
        }
    }

    private boolean verifyDigests(GotrContext context){
        byte[] mySig = signatureVerifications.get(context.getLocalUser());
        for(Map.Entry<GotrUser, byte[]> entry: signatureVerifications.entrySet()) {
            if(!Arrays.equals(mySig, entry.getValue())){
                return false;
            }
        }
        return true;
    }

//    private boolean verifySecrets(GotrContext context) throws NoSuchAlgorithmException {
//        for(Map.Entry<GotrUser, byte[]> entry: secretVerifications.entrySet()){
//            if(!entry.getKey().equals(context.getLocalUser())){
//                byte[] computed = context.getCrypto().computeGroupVerification(entry.getKey(), secrets);
//                if(!Arrays.equals(computed, entry.getValue())){
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    @Override
    protected void handleReceivedEncryptedGroupMessageEvent(GotrContext context, ReceivedEncryptedGroupMessageEvent event) throws GotrException, NoSuchAlgorithmException {
        context.deferEvent(event);
    }

    @Override
    protected void handleUserBroadcastEvent(GotrContext context, UserBroadcastEvent event) throws Exception {
        context.getOutingMessages().add(event.getBroadcast());
    }

    @Override
    public void onEntry(GotrContext context) throws Exception {

        //byte[] secretVerification = context.getCrypto().computeGroupVerification(context.getLocalUser(), secrets);

        //secretVerifications.put(context.getLocalUser(), secretVerification);

        MessageDigest hash = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);

        for(UserSecret secret: secrets.getOrderedSecrets()){
            hash.update(signatures.get(secret.getUser()));
            hash.update(secret.getUser().toString().getBytes());
        }

        byte[] sigHash = hash.digest();

        signatureVerifications.put(context.getLocalUser(), sigHash);

        SendVerifyDigestEvent event = new SendVerifyDigestEvent(sigHash);
        for(P2pContext p2pContext: context.getP2pContexts()){
            p2pContext.handelEvent(event);
        }
    }

    @Override
    public GotrSessionState getSessionState() {
        return GotrSessionState.SECURE;
    }
}
