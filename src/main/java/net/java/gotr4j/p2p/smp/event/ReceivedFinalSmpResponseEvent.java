package net.java.gotr4j.p2p.smp.event;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ReceivedFinalSmpResponseEvent implements SmpEvent {

    private final ECPoint rb;

    private final byte[] cbr;

    private final BigInteger db7;

    public ReceivedFinalSmpResponseEvent(ECPoint rb, byte[] cbr, BigInteger db7) {
        this.rb = rb;
        this.cbr = cbr;
        this.db7 = db7;
    }

    public ECPoint getRb() {
        return rb;
    }

    public byte[] getCbr() {
        return cbr;
    }

    public BigInteger getDb7() {
        return db7;
    }
}
