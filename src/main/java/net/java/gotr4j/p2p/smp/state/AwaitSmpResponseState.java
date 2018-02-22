package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.ReceivedSmpResponseEvent;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class AwaitSmpResponseState extends SmpState {

    private static final Logger logger = LoggerFactory.getLogger(RespondSmpState.class);

    private final BigInteger a2;
    private final BigInteger a3;
    private final byte[] x;

    public AwaitSmpResponseState(BigInteger a2, BigInteger a3, byte[] x) {
        this.a2 = a2;
        this.a3 = a3;
        this.x = x;
    }

    @Override
    protected void handleReceivedSmpResponseEvent(SmpContext context, ReceivedSmpResponseEvent event) throws  Exception{

        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        ECPoint g2 = event.getGb2().multiply(a2);
        ECPoint g3 = event.getGb3().multiply(a3);

        digest.update("3".getBytes());
        ECPoint temp = g.multiply(event.getDb2()).add(event.getGb2().multiply(new BigInteger(event.getCb2())));
        byte[] tempC2 = digest.digest(temp.getEncoded());

        digest.reset();
        digest.update("4".getBytes());
        temp = g.multiply(event.getDb3()).add(event.getGb3().multiply(new BigInteger(event.getCb3())));
        byte[] tempC3 = digest.digest(temp.getEncoded());

        digest.reset();
        digest.update("5".getBytes());
        BigInteger cbp = new BigInteger(event.getCp());
        temp = g3.multiply(event.getDb5()).add(event.getPb().multiply(cbp));
        digest.update(temp.getEncoded());
        temp = g.multiply(event.getDb5()).add(g2.multiply(event.getDb6())).add(event.getQb().multiply(cbp));
        byte[] tempCbp = digest.digest(temp.getEncoded());

        if(!Arrays.equals(tempC2, event.getCb2())
                || !Arrays.equals(tempC3, event.getCb3())
                || !Arrays.equals(tempCbp, event.getCp())){
            throw new GotrException("Invalid SMP response.");
        }

        BigInteger r4 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r5 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r6 = context.getCrypto().getRandomBigInteger(order);
        BigInteger r7 = context.getCrypto().getRandomBigInteger(order);

        ECPoint pa = g3.multiply(r4);
        ECPoint qa = g.multiply(r4).add(g2.multiply(new BigInteger(x)));

        digest.reset();
        digest.update("6".getBytes());
        digest.update(g3.multiply(r5).getEncoded());
        byte[] cap = digest.digest(g.multiply(r5).add(g2.multiply(r6)).getEncoded());

        BigInteger capBigInt = new BigInteger(cap);
        BigInteger da5 = r5.subtract(r4.multiply(capBigInt)).mod(order);
        BigInteger da6 = r6.subtract(new BigInteger(x).multiply(capBigInt)).mod(order);

        ECPoint ra = qa.subtract(event.getQb()).multiply(a3);

        digest.reset();
        digest.update("7".getBytes());
        digest.update(g.multiply(r7).getEncoded());
        byte[] cr = digest.digest(qa.subtract(event.getQb()).multiply(r7).getEncoded());

        BigInteger da7 = r7.subtract(a3.multiply(new BigInteger(cr))).mod(order);

        GotrOutputStream out = new GotrOutputStream();
        out.writeByte(EncryptedMessageType.SMP_INIT_FINAL.id);
        out.writeECPoint(pa);
        out.writeECPoint(qa);
        out.write(cap);
        out.writeBigInteger(da5);
        out.writeBigInteger(da6);
        out.writeECPoint(ra);
        out.write(cr);
        out.writeBigInteger(da7);

        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

        context.setNextState(new AwaitFinalSmpResponseState(event.getGb3(), pa.subtract(event.getPb()), qa.subtract(event.getQb()), a3));
    }
}
