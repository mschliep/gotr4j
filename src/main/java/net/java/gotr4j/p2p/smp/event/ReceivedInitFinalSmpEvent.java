package net.java.gotr4j.p2p.smp.event;

import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class ReceivedInitFinalSmpEvent implements SmpEvent {

    private final ECPoint pa;
    private final ECPoint qa;

    private final byte[] cap;

    private final BigInteger da5;
    private final BigInteger da6;

    private final ECPoint ra;

    private final byte[] cr;

    private final BigInteger da7;

    public ReceivedInitFinalSmpEvent(ECPoint pa, ECPoint qa, byte[] cap, BigInteger da5, BigInteger da6, ECPoint ra, byte[] cr, BigInteger da7) {
        this.pa = pa;
        this.qa = qa;
        this.cap = cap;
        this.da5 = da5;
        this.da6 = da6;
        this.ra = ra;
        this.cr = cr;
        this.da7 = da7;
    }

    public ECPoint getPa() {
        return pa;
    }

    public ECPoint getQa() {
        return qa;
    }

    public byte[] getCap() {
        return cap;
    }

    public BigInteger getDa5() {
        return da5;
    }

    public BigInteger getDa6() {
        return da6;
    }

    public ECPoint getRa() {
        return ra;
    }

    public byte[] getCr() {
        return cr;
    }

    public BigInteger getDa7() {
        return da7;
    }
}
