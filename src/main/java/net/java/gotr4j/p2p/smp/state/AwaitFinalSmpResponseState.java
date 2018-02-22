package net.java.gotr4j.p2p.smp.state;

import net.java.gotr4j.crypto.GotrException;
import net.java.gotr4j.p2p.smp.SmpContext;
import net.java.gotr4j.p2p.smp.event.ReceivedFinalSmpResponseEvent;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class AwaitFinalSmpResponseState extends SmpState {

    private final ECPoint gb3;
    private final ECPoint p;
    private final ECPoint q;
    private final BigInteger a3;

    public AwaitFinalSmpResponseState(ECPoint gb3, ECPoint p, ECPoint q, BigInteger a3) {
        this.gb3 = gb3;
        this.p = p;
        this.q = q;
        this.a3 = a3;
    }

    @Override
    protected void handleReceivedFinalSmpResponseEvent(SmpContext context, ReceivedFinalSmpResponseEvent event) throws Exception{
        MessageDigest digest = MessageDigest.getInstance(context.getCrypto().DIGEST_ALGORITHM);

        ECPoint g = context.getCrypto().NAMED_CURVE.getG();
        BigInteger order = context.getCrypto().NAMED_CURVE.getN();

        BigInteger cbrBigInt = new BigInteger(event.getCbr());

        digest.update("8".getBytes());
        digest.update(g.multiply(event.getDb7()).add(gb3.multiply(cbrBigInt)).getEncoded());
        digest.update(q.multiply(event.getDb7()).add(event.getRb().multiply(cbrBigInt)).getEncoded());

        byte[] tempCbr = digest.digest();

        if(!Arrays.equals(tempCbr, event.getCbr())){
            throw new GotrException("Invalid SMP final response.");
        }

        ECPoint rab = event.getRb().multiply(a3);

        if(p.equals(rab)){
            context.getSessionHost().verify(context.getRemoteUser(), context.getCrypto().getFingerprint(context.getRemotePublicKey()));
        }
        else{
            context.getSessionHost().unverify(context.getRemoteUser(), context.getCrypto().getFingerprint(context.getRemotePublicKey()));
        }

        context.setNextState(new InitSmpState());
    }
}
