package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.io.GotrOutputStream;
import net.java.gotr4j.p2p.event.SendEncryptedMessageEvent;
import net.java.gotr4j.p2p.io.EncryptedMessageType;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.ReceivedInitFinalSmpEvent;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class AwaitInitFinalSmpState extends SmpState {
    private static final Logger logger = LoggerFactory.getLogger(AwaitInitFinalSmpState.class);

    private final ECPoint ga3;
    private final ECPoint g2;
    private final ECPoint g3;
    private final BigInteger b3;
    private final ECPoint pb;
    private final ECPoint qb;


    public AwaitInitFinalSmpState(ECPoint ga3, ECPoint g2, ECPoint g3, BigInteger b3, ECPoint pb, ECPoint qb) {
        this.ga3 = ga3;
        this.g2 = g2;
        this.g3 = g3;
        this.b3 = b3;
        this.pb = pb;
        this.qb = qb;
    }

    @Override
    protected void handleReceivedInitFinalSmpEvent(SmpContext context, ReceivedInitFinalSmpEvent event) throws Exception{

        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        BigInteger capBigInt = new BigInteger(event.getCap());

        digest.update("6".getBytes());

        digest.update(g3.multiply(event.getDa5()).add(event.getPa().multiply(capBigInt)).getEncoded());
        digest.update(g.multiply(event.getDa5()).add(g2.multiply(event.getDa6()).add(event.getQa().multiply(capBigInt))).getEncoded());

        byte[] tempCap = digest.digest();

        BigInteger carBigInt = new BigInteger(event.getCr());

        digest.reset();
        digest.update("7".getBytes());
        digest.update(g.multiply(event.getDa7()).add(ga3.multiply(carBigInt)).getEncoded());
        digest.update(event.getQa().subtract(qb).multiply(event.getDa7()).add(event.getRa().multiply(carBigInt)).getEncoded());
        byte[] tempCar = digest.digest();

        if(!Arrays.equals(tempCap, event.getCap()) || !Arrays.equals(tempCar, event.getCr())){
            throw new GotrException("Invalid SMP final message.");
        }

        BigInteger r7 = context.getCrypto().getRandomBigInteger(order);

        ECPoint rb = event.getQa().subtract(qb).multiply(b3);

        digest.reset();
        digest.update("8".getBytes());
        digest.update(g.multiply(r7).getEncoded());
        digest.update(event.getQa().subtract(qb).multiply(r7).getEncoded());
        byte[] cbr = digest.digest();

        BigInteger db7 = r7.subtract(b3.multiply(new BigInteger(cbr))).mod(order);

        GotrOutputStream out = new GotrOutputStream();
        out.writeByte(EncryptedMessageType.SMP_RESPONSE_FINAL.id);
        out.writeECPoint(rb);
        out.write(cbr);
        out.writeBigInteger(db7);

        context.getP2pContext().handelEvent(new SendEncryptedMessageEvent(out.getBytes()));

        ECPoint rab = event.getRa().multiply(b3);

        if(event.getPa().subtract(pb).equals(rab)){
            context.getSessionHost().verify(context.getRemoteUser(), context.getCrypto().getFingerprint(context.getRemotePublicKey()));
        }
        else{
            context.getSessionHost().unverify(context.getRemoteUser(), context.getCrypto().getFingerprint(context.getRemotePublicKey()));
        }

        context.setNextState(new InitSmpState());
    }
}
