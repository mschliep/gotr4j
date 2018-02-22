package net.java.gotr4j.crypto;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

public class Counter implements Comparable<Counter>{

    public static final Counter ZERO = new Counter(BigInteger.ZERO);
    public static final Counter ONE = new Counter(BigInteger.ONE);

    private final BigInteger head;

    private Counter(BigInteger head){
        this.head = head;
    }

    public Counter(byte[] head){
        this.head = BigIntegers.fromUnsignedByteArray(head);
    }

    public Counter next(){
        return new Counter(head.add(BigInteger.ONE));
    }

    public byte[] toByteArray(){
        byte[] head = BigIntegers.asUnsignedByteArray(8, this.head);
        return Arrays.concatenate(head, new byte[8]);
    }

    @Override
    public int compareTo(Counter counter) {
        return this.head.compareTo(counter.head);
    }

    @Override
    public int hashCode() {
        return head.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return head.equals(obj);
    }
}
