package net.java.gotr4j.crypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;

public class P2pKeyAgreementTest {

    private GotrCrypto crypto;

    @Before
    public void before() throws GotrException {
        crypto = new GotrCrypto();
    }

    @Test
    public void testP2pKeyAgreement() throws Exception {
        byte[] localName = "local".getBytes();
        byte[] remoteName = "remote".getBytes();

        P2pKeyAgreement local = crypto.generateP2pKeyAgreement();
        P2pKeyAgreement remote = crypto.generateP2pKeyAgreement();

        KeyPair localPair = crypto.generateKeyPair();
        KeyPair remotePair = crypto.generateKeyPair();

        local.initHosts(localName, remoteName);
        local.initLongtermKey((ECPrivateKey) localPair.getPrivate(), (ECPublicKey)localPair.getPublic());

        remote.initHosts(remoteName, localName);
        remote.initLongtermKey((ECPrivateKey) remotePair.getPrivate(), (ECPublicKey)remotePair.getPublic());

        local.setRemoteEphemeralKey(new X509EncodedKeySpec(remote.getEphemeralPublicKey().getEncoded()));
        remote.setRemoteEphemeralKey(new X509EncodedKeySpec(local.getEphemeralPublicKey().getEncoded()));

        local.setRemoteLongtermPublicKey(remote.getLongtermPublicKey().getEncoded());
        remote.setRemoteLongtermPublicKey(local.getLongtermPublicKey().getEncoded());

        Assert.assertArrayEquals(local.getSendingSharedSecret(), remote.getReceivingSharedSecret());
        Assert.assertArrayEquals(remote.getSendingSharedSecret(), local.getReceivingSharedSecret());

    }

}
