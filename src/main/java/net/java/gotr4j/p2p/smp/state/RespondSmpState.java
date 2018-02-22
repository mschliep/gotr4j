package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.UserRespondSmpEvent;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class RespondSmpState extends SmpState {

    private static final Logger logger = LoggerFactory.getLogger(RespondSmpState.class);

    private final ECPoint ga2;
    private final ECPoint ga3;

    private final BigInteger da2;
    private final BigInteger da3;

    private final byte[] ca2;
    private final byte[] ca3;

    private final String question;

    public RespondSmpState(ECPoint ga2, ECPoint ga3, BigInteger da2, BigInteger da3, byte[] ca2, byte[] ca3, String question) {
        this.ga2 = ga2;
        this.ga3 = ga3;
        this.da2 = da2;
        this.da3 = da3;
        this.ca2 = ca2;
        this.ca3 = ca3;
        this.question = question;
    }

    @Override
    protected void handleUserRespondSmpEvent(SmpContext context, UserRespondSmpEvent event) throws Exception {

        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);
        digest.update(context.getRemotePublicKey().getEncoded());
        digest.update(context.getLocalPublicKey().getEncoded());
        byte[] y = digest.digest(event.getAnswer().getBytes());

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        BigInteger b2 = context.getCrypto().getRandomBigInteger(order);
        BigInteger b3 = context.getCrypto().getRandomBigInteger(order);

        BigInteger r2 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r3 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r4 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r5 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r6 = context.getCrypto().getRandomBigInteger(order);

        ECPoint gb2 = g.multiply(b2);
        ECPoint gb3 = g.multiply(b3);

        ECPoint gr2 = g.multiply(r2);
        ECPoint gr3 = g.multiply(r3);

        digest.reset();
        digest.update("3".getBytes());
        byte[] cb2 = digest.digest(gr2.getEncoded());

        digest.reset();
        digest.update("4".getBytes());
        byte[] cb3 = digest.digest(gr3.getEncoded());

        BigInteger db2 = r2.subtract(b2.multiply(new BigInteger(cb2))).mod(order);
        BigInteger db3 = r3.subtract(b3.multiply(new BigInteger(cb3))).mod(order);

        ECPoint g2 = ga2.multiply(b2);
        ECPoint g3 = ga3.multiply(b3);

        ECPoint pb = g3.multiply(r4);
        ECPoint qb = g.multiply(r4).add(g2.multiply(new BigInteger(y)));


        digest.reset();
        digest.update("5".getBytes());
        digest.update(g3.multiply(r5).getEncoded());
        byte[] cp = digest.digest(g.multiply(r5).add(g2.multiply(r6)).getEncoded());

        BigInteger cpBigInt = new BigInteger(cp);

        BigInteger d5 = r5.subtract(r4.multiply(cpBigInt)).mod(order);
        BigInteger d6 = r6.subtract(new BigInteger(y).multiply(cpBigInt)).mod(order);
        


        GotrOutputStream out = new GotrOutputStream();
        out.writeByte(EncryptedMessageType.SMP_RESPONSE.id);
        out.writeECPoint(gb2);
        out.writeECPoint(gb3);
        out.writeBigInteger(db2);
        out.writeBigInteger(db3);
        out.write(cb2);
        out.write(cb3);
        out.writeECPoint(pb);
        out.writeECPoint(qb);
        out.write(cp);
        out.writeBigInteger(d5);
        out.writeBigInteger(d6);

        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

        context.setNextState(new AwaitInitFinalSmpState(ga3, g2, g3, b3, pb, qb));

    }
}
