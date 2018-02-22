package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.ReceivedInitSmpEvent;
import net.java.gotr4j.p2p.smp.event.UserStartSmpEvent;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class InitSmpState extends SmpState {
    private static final Logger logger = LoggerFactory.getLogger(InitSmpState.class);

    @Override
    protected void handleReceivedInitSmpEvent(SmpContext context, ReceivedInitSmpEvent event) throws Exception {

        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        digest.update("1".getBytes());
        ECPoint temp = g.multiply(event.getD2()).add(event.getGa2().multiply(new BigInteger(event.getC2())));
        byte[] tempC2 = digest.digest(temp.getEncoded());

        digest.reset();
        digest.update("2".getBytes());
        temp = g.multiply(event.getD3()).add(event.getGa3().multiply(new BigInteger(event.getC3())));
        byte[] tempC3 = digest.digest(temp.getEncoded());

        if(!Arrays.equals(tempC2, event.getC2()) || !Arrays.equals(tempC3, event.getC3())){
            throw new GotrException("Invalid SMP generators.");
        }

        context.getSessionHost().askForSecret(context.getRemoteUser(), event.getQuestion());
        context.setNextState(new RespondSmpState(event.getGa2(), event.getGa3(), event.getD2(), event.getD3(),
                event.getC2(), event.getC3(), event.getQuestion()));
    }

    @Override
    protected void handleUserStartSmpEvent(SmpContext context, UserStartSmpEvent event) throws Exception {

        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);
        digest.update(context.getLocalPublicKey().getEncoded());
        digest.update(context.getRemotePublicKey().getEncoded());
        byte[] x = digest.digest(event.getAnswer().getBytes());

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        BigInteger a2 = context.getCrypto().getRandomBigInteger(order);
        BigInteger a3 = context.getCrypto().getRandomBigInteger(order);

        BigInteger r2 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r3 = context.getCrypto().getRandomBigInteger(order);

        ECPoint ga2 = g.multiply(a2);
        ECPoint ga3 = g.multiply(a3);

        ECPoint gr2 = g.multiply(r2);
        ECPoint gr3 = g.multiply(r3);

        digest.reset();
        digest.update("1".getBytes());
        byte[] c2 = digest.digest(gr2.getEncoded());

        digest.reset();
        digest.update("2".getBytes());
        byte[] c3 = digest.digest(gr3.getEncoded());

        BigInteger d2 = r2.subtract(a2.multiply(new BigInteger(c2))).mod(order);
        BigInteger d3 = r3.subtract(a3.multiply(new BigInteger(c3))).mod(order);

        GotrOutputStream out = new GotrOutputStream();
        out.writeByte(EncryptedMessageType.SMP_INIT.id);
        out.writeECPoint(ga2);
        out.writeECPoint(ga3);
        out.writeBigInteger(d2);
        out.writeBigInteger(d3);
        out.write(c2);
        out.write(c3);
        out.writeString(event.getQuestion());

        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

        context.setNextState(new AwaitSmpResponseState(a2, a3, x));

    }
}
