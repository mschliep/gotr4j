package net.java.gotr4j.crypto;

import net.java.gotr4j.GotrUser;
import net.java.gotr4j.SessionID;
import net.java.gotr4j.util.GotrUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GotrCrypto {

    public static final ECNamedCurveParameterSpec NAMED_CURVE = ECNamedCurveTable.getParameterSpec("P-256");
	
	public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final int DIGEST_SIZE = 32; // hash size in bytes
    public static final String CIPHER_ALGORITHM = "AES/CTR/NoPadding";
    public static final int KEY_SIZE = 16; // half of the hash size in bytes
	public static final String MAC_ALGORITHM = "HmacSHA256";
    public static final int MAC_SIZE = 32; // mac size in bytes
	public static final String KEY_PAIR_ALGORITHM = "ECDH";
    public static final String SIGNING_ALGORITHM = "ECDSA";
    public static final int SECRET_SIZE = 256/8;

    private final ECParameterSpec parameterSpec;

    private final KeyPairGenerator generator;
    private final KeyPairGenerator signingKeyGenerator;

    private final KeyFactory encKeyFactory;
    private final KeyFactory signingKeyFactory;

    private final SecureRandom random = new SecureRandom();

    public GotrCrypto() throws GotrException {
        Security.addProvider(new BouncyCastleProvider());

        parameterSpec=new ECNamedCurveSpec(NAMED_CURVE.getName(), NAMED_CURVE.getCurve(), NAMED_CURVE.getG(), NAMED_CURVE.getN(), NAMED_CURVE.getH(), NAMED_CURVE.getSeed());

        try {
            generator = KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM, "BC");
            generator.initialize(parameterSpec, random);

            signingKeyGenerator = KeyPairGenerator.getInstance(SIGNING_ALGORITHM, "BC");
            signingKeyGenerator.initialize(parameterSpec, random);

            encKeyFactory = KeyFactory.getInstance(KEY_PAIR_ALGORITHM);
            signingKeyFactory = KeyFactory.getInstance(SIGNING_ALGORITHM);

        } catch (Exception e) {
            throw new GotrException(e);
        }

    }

    public P2pKeyAgreement generateP2pKeyAgreement() throws GotrException {
        byte[] secret = new byte[SECRET_SIZE];
        random.nextBytes(secret);
        try {
            return new P2pKeyAgreement(secret, parameterSpec, this);
        } catch (NoSuchAlgorithmException e) {
            throw new GotrException(e);
        }
    }

	public KeyPair generateKeyPair(){
		return generator.generateKeyPair();
	}

    public KeyPair generateSigningKeyPair() {
        return signingKeyGenerator.generateKeyPair();
    }

    public byte[][] getKeys(byte[] key) throws GotrException {
        byte[][] keys = {Arrays.copyOfRange(key, 0, KEY_SIZE),
                Arrays.copyOfRange(key, KEY_SIZE, key.length)};

        return keys;
    }

    public byte[] hash(byte[] bytes) throws GotrException {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            return digest.digest(bytes);
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public byte[] mac(byte[] key, byte[] message) throws GotrException {
        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, MAC_ALGORITHM));
            return mac.doFinal(message);
        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public byte[] aesCtrEncrypt(byte[] key, Counter ctr, byte[] plaintext)
            throws GotrException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(ctr.toByteArray()));

            return cipher.doFinal(plaintext);

        } catch (Exception e) {
           throw new GotrException(e);
        }
    }

    public byte[] aesCtrDecrypt(byte[] key, Counter ctr, byte[] ciphertext)
            throws GotrException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(ctr.toByteArray()));

            return cipher.doFinal(ciphertext);

        } catch (Exception e) {
            throw new GotrException(e);
        }
    }

    public SessionID generateSessionID(String roomName) throws GotrException {
        return new SessionID(hash(GotrUtil.encodeUTF8(roomName)));
    }

    public byte[] generateSecret() {
        byte bytes[] = new byte[SECRET_SIZE];
        random.nextBytes(bytes);
        return bytes;
    }

    public boolean checkMac(byte[] macKey, byte[] msgAndMac) throws GotrException {
        byte[] ct = Arrays.copyOfRange(msgAndMac, 0, msgAndMac.length-MAC_SIZE);
        byte[] mac = Arrays.copyOfRange(msgAndMac, ct.length, msgAndMac.length);

        byte[] computedMac = mac(macKey, ct);
        return Arrays.equals(computedMac, mac);
    }

    public byte[] decrypt(byte[] key, byte[] msgAndMac, Counter counter) throws GotrException {
        byte[] ct = Arrays.copyOfRange(msgAndMac, 0, msgAndMac.length-MAC_SIZE);

        return aesCtrDecrypt(key, counter, ct);
    }

    public byte[][] generateKeys(GotrUser user, UserKeys secrets) throws NoSuchAlgorithmException, GotrException {
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        digest.update("KDF3".getBytes());

        for(UserSecret secret: secrets.getOrderedSecrets()){
            digest.update(secret.getSecret());
            digest.update(secret.getUser().toString().getBytes());
        }
        return getKeys(digest.digest(user.toString().getBytes()));
    }

    public KeyFactory getEncKeyFactory() {
        return encKeyFactory;
    }

    public KeyFactory getSigningKeyFactory() {
        return signingKeyFactory;
    }

    public byte[] computeGroupVerification(SessionID sessionID, List<GotrUser> users) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

        digest.update(sessionID.getBytes());
        Collections.sort(users, new Comparator<GotrUser>() {
            @Override
            public int compare(GotrUser user, GotrUser other) {
                return user.compareTo(other);
            }
        });

        for(GotrUser user: users){
            digest.update(user.toString().getBytes());
        }
        return digest.digest();
    }

    public String getFingerprint(PublicKey publicKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        return DatatypeConverter.printHexBinary(digest.digest(publicKey.getEncoded()));
    }

    public BigInteger getRandomBigInteger(BigInteger modulus){
        int bits = modulus.bitLength();
        BigInteger result = new BigInteger(bits, random);
        while(result.compareTo(modulus) >= 0){
            result = new BigInteger(bits, random);
        }
        return result;
    }
}
