package net.java.gotr4j.crypto;

import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECPoint;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Arrays;

public class P2pKeyAgreement {
    private final String KEY_AGREEMENT_ALGORITHM = "ECDH";

    private final byte[] ephemeralSecret;

    private final ECParameterSpec parameterSpec;
    private final KeyAgreement firstKeyAgreement;
    private final KeyAgreement secondKeyAgreement;
    private final KeyAgreement thirdKeyAgreement;

    private final KeyFactory keyFactory;

    private ECPublicKey longtermPublicKey;
    private ECPublicKey remoteLongtermPublicKey;

    private ECPrivateKey ephemeralPrivateKey;
    private ECPublicKey ephemeralPublicKey;

    private ECPublicKey remoteEphemeralKey;

    private byte[] local;
    private byte[] remote;

    private final GotrCrypto crypto;

    public P2pKeyAgreement(byte[] ephemeralSecret, ECParameterSpec parameterSpec, GotrCrypto crypto) throws NoSuchAlgorithmException {
        this.ephemeralSecret = ephemeralSecret;
        this.parameterSpec = parameterSpec;
        this.crypto = crypto;
        firstKeyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM);
        secondKeyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM);
        thirdKeyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM);
        keyFactory = KeyFactory.getInstance(KEY_AGREEMENT_ALGORITHM);
    }

    public void initHosts(byte[] local, byte[] remote){
        this.local = local;
        this.remote = remote;
    }

    public void initLongtermKey(ECPrivateKey privateKey, ECPublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        longtermPublicKey = publicKey;

        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update(ephemeralSecret);
        byte[] out = digest.digest(privateKey.getS().toByteArray());

        BigInteger exp = new BigInteger(out).mod(parameterSpec.getOrder());
        ECPrivateKeySpec keySpec = new ECPrivateKeySpec(exp, parameterSpec);
        ephemeralPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);

        ECPoint point = crypto.NAMED_CURVE.getG().multiply(exp);

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(ECPointUtil.decodePoint(parameterSpec.getCurve(), point.getEncoded(true)), parameterSpec);

        ephemeralPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);

        firstKeyAgreement.init(privateKey);
        secondKeyAgreement.init(ephemeralPrivateKey);
        thirdKeyAgreement.init(ephemeralPrivateKey);
    }

    public PublicKey getEphemeralPublicKey(){
        return ephemeralPublicKey;
    }

    public void setRemoteEphemeralKey(KeySpec keySpec) throws InvalidKeySpecException, InvalidKeyException {
        remoteEphemeralKey = (ECPublicKey) keyFactory.generatePublic(keySpec);
        firstKeyAgreement.doPhase(remoteEphemeralKey, true);
        thirdKeyAgreement.doPhase(remoteEphemeralKey, true);
    }

    public void setFirstMessage(byte[] input) throws InvalidKeySpecException, InvalidKeyException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(input);
        setRemoteEphemeralKey(keySpec);
    }

    public void setRemoteLongtermPublicKey(byte[] msg) throws InvalidKeySpecException, InvalidKeyException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(msg);
        remoteLongtermPublicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

        secondKeyAgreement.doPhase(remoteLongtermPublicKey, true);

    }

    public void setRemoteLongtermPublicKey(KeySpec keySpec) throws InvalidKeySpecException, InvalidKeyException {
        remoteLongtermPublicKey = (ECPublicKey) keyFactory.generatePublic(keySpec);

        secondKeyAgreement.doPhase(remoteLongtermPublicKey, true);

    }

    public byte[] getSendingSharedSecret() throws NoSuchAlgorithmException {

        byte[] first = firstKeyAgreement.generateSecret();
        byte[] second = secondKeyAgreement.generateSecret();
        byte[] third = thirdKeyAgreement.generateSecret();

        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update("KDF1".getBytes());
        digest.update(first);
        digest.update(second);
        digest.update(third);
        digest.update(local);
        byte[] result = digest.digest(remote);

        return result;
    }

    public byte[] getReceivingSharedSecret() throws NoSuchAlgorithmException {

        byte[] first = firstKeyAgreement.generateSecret();
        byte[] second = secondKeyAgreement.generateSecret();
        byte[] third = thirdKeyAgreement.generateSecret();

        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update("KDF1".getBytes());
        digest.update(second);
        digest.update(first);
        digest.update(third);
        digest.update(remote);
        byte[] result = digest.digest(local);

        return result;
    }

    public ECPublicKey getLongtermPublicKey(){
        return longtermPublicKey;
    }

    public byte[] getTempSendingSecret() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update(thirdKeyAgreement.generateSecret());
        digest.update(local);
        byte[] keys = digest.digest(remote);
        return keys;
    }

    public byte[] getTempReceivingSecret() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(crypto.DIGEST_ALGORITHM);
        digest.update(thirdKeyAgreement.generateSecret());
        digest.update(remote);
        byte[] keys = digest.digest(local);
        return keys;
    }

    public ECPublicKey getRemoteLongtermPublicKey() {
        return remoteLongtermPublicKey;
    }
}
