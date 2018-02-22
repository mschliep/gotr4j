package net.java.gotr4j.crypto;

import net.java.gotr4j.GotrUser;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyRatchet {
    private final String KEY_AGREEMENT_ALGORITHM = "ECDH";

    private final List<LocalKeyRatchetShare> shares = new ArrayList<>();
    private final KeyFactory keyFactory;
    private final KeyAgreement keyAgreement;
    private final GotrCrypto crypto;

    private RemoteKeyRachetShare remoteShare;

    public KeyRatchet(GotrCrypto crypto) throws NoSuchAlgorithmException {
        this.crypto = crypto;
        keyFactory = KeyFactory.getInstance(KEY_AGREEMENT_ALGORITHM);
        keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM);
    }

    private byte nextLocalId(){
        byte id = 0;
        if(!shares.isEmpty()){
            id = shares.get(shares.size()-1).getId();
            id++;
        }
        return id;
    }

    public LocalKeyRatchetShare newShare(){
        LocalKeyRatchetShare currentShare;
        KeyPair pair = crypto.generateKeyPair();
        byte id = nextLocalId();
        currentShare = new LocalKeyRatchetShare(id, pair.getPrivate(), pair.getPublic());
        shares.add(currentShare);

        return currentShare;
    }

    public byte[][] generateKeys(PrivateKey privateKey, PublicKey publicKey, GotrUser send, GotrUser recv) throws NoSuchAlgorithmException, GotrException, InvalidKeyException {
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);

        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update("KDF2".getBytes());
        digest.update(send.getUsername().getBytes());
        digest.update(recv.getUsername().getBytes());

        return crypto.getKeys(digest.digest(keyAgreement.generateSecret()));
    }

    public byte[][] generateReceivingKeys(byte id, GotrUser localUser, GotrUser remoteUser) throws NoSuchAlgorithmException, GotrException, InvalidKeyException {
        if(shares.isEmpty() || remoteUser == null){
            throw new GotrException("Empty Key Ratchet.");
        }

        LocalKeyRatchetShare share = null;
        Iterator<LocalKeyRatchetShare> iterator = shares.iterator();
        while(iterator.hasNext()){
            share = iterator.next();
            if(share.getId() != id){
                iterator.remove();
            }
            else{
                break;
            }
        }
        if(share == null){
            throw new GotrException("Invalid key ratchet id.");
        }

        return generateKeys(share.getPrivateKey(), remoteShare.getPublicKey(), remoteUser, localUser);
    }

    public byte[][] generateSendingKeys(GotrUser localUser, GotrUser remoteUser) throws GotrException, InvalidKeyException, NoSuchAlgorithmException {
        if(shares.isEmpty() || remoteShare == null){
            throw new GotrException("Empty Key Ratchet.");
        }

        return generateKeys(shares.get(shares.size()-1).getPrivateKey(), remoteShare.getPublicKey(), localUser, remoteUser);
    }

    public void setRemoteShare(byte id, KeySpec keySpec) throws InvalidKeySpecException {
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        remoteShare = new RemoteKeyRachetShare(id, publicKey);
    }

    public byte getRemoteShareId() {
        return remoteShare.getId();
    }
}
