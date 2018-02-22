package net.java.gotr4j.p2p.smp.event;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ReceivedSmpResponseEvent implements SmpEvent {

    private final ECPoint gb2;
    private final ECPoint gb3;

    private final BigInteger db2;
    private final BigInteger db3;

    private final byte[] cb2;
    private final byte[] cb3;

    private final ECPoint pb;
    private final ECPoint qb;

    private final byte[] cp;

    private final BigInteger db5;
    private final BigInteger db6;

    public ReceivedSmpResponseEvent(ECPoint gb2, ECPoint gb3, BigInteger db2, BigInteger db3, byte[] cb2, byte[] cb3, ECPoint pb, ECPoint qb, byte[] cb, BigInteger db5, BigInteger db6) {
        this.gb2 = gb2;
        this.gb3 = gb3;
        this.db2 = db2;
        this.db3 = db3;
        this.cb2 = cb2;
        this.cb3 = cb3;
        this.pb = pb;
        this.qb = qb;
        this.cp = cb;
        this.db5 = db5;
        this.db6 = db6;
    }

    public ECPoint getGb2() {
        return gb2;
    }

    public ECPoint getGb3() {
        return gb3;
    }

    public BigInteger getDb2() {
        return db2;
    }

    public BigInteger getDb3() {
        return db3;
    }

    public byte[] getCb2() {
        return cb2;
    }

    public byte[] getCb3() {
        return cb3;
    }

    public ECPoint getPb() {
        return pb;
    }

    public ECPoint getQb() {
        return qb;
    }

    public byte[] getCp() {
        return cp;
    }

    public BigInteger getDb5() {
        return db5;
    }

    public BigInteger getDb6() {
        return db6;
    }
}
