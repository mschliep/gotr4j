package net.java.gotr4j.p2p.smp.event;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ReceivedInitSmpEvent implements SmpEvent {

    private final ECPoint ga2;
    private final ECPoint ga3;

    private final BigInteger d2;
    private final BigInteger d3;

    private final byte[] c2;
    private final byte[] c3;

    private final String question;

    public ReceivedInitSmpEvent(ECPoint ga2, ECPoint ga3, BigInteger d2, BigInteger d3, byte[] c2, byte[] c3, String question) {
        this.ga2 = ga2;
        this.ga3 = ga3;
        this.d2 = d2;
        this.d3 = d3;
        this.c2 = c2;
        this.c3 = c3;
        this.question = question;
    }

    public ECPoint getGa2() {
        return ga2;
    }

    public ECPoint getGa3() {
        return ga3;
    }

    public BigInteger getD2() {
        return d2;
    }

    public BigInteger getD3() {
        return d3;
    }

    public byte[] getC2() {
        return c2;
    }

    public byte[] getC3() {
        return c3;
    }

    public String getQuestion() {
        return question;
    }
}
