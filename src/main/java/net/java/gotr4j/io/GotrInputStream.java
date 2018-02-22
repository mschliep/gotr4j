package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.*;
import net.java.gotr4j.util.GotrUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public class GotrInputStream {

    private final ByteArrayInputStream stream;

    public GotrInputStream(byte[] bytes) {
        this.stream = new ByteArrayInputStream(bytes);
    }

    public GotrInputStream(String input) {
        this(GotrUtil.fromBase64(input));
    }

    private byte[] readNumber(int length) throws IOException {
        byte[] value = new byte[length];
        stream.read(value);

        return value;

    }

    public short readShort() throws IOException {
        byte[] value = readNumber(2);

        return ByteBuffer.wrap(value).order(ByteOrder.BIG_ENDIAN)
                .asShortBuffer().get();
    }

    public int readInt() throws IOException {
        byte[] value = readNumber(4);

        return ByteBuffer.wrap(value).order(ByteOrder.BIG_ENDIAN)
                .asIntBuffer().get();
    }

    public BigInteger readBigInteger() throws IOException {
        int size = readInt();

        byte[] bytes = readNumber(size);

        return BigIntegers.fromUnsignedByteArray(bytes);
    }

    public byte readByte() {
        return (byte)stream.read();
    }

    public void read(byte[] bytes) throws IOException {
        stream.read(bytes);
    }

    public byte[] readRemaining() throws IOException {
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return bytes;
    }

    public int available(){
        return stream.available();
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] value = new byte[length];
        stream.read(value);
        return value;
    }

    public void reset() {
        stream.reset();
    }

    public Counter readCounter() throws IOException {
        return new Counter(readBytes(8));
    }

    public BigInteger[] readBigIntegers(int size) throws IOException {
        BigInteger results[] = new BigInteger[size];
        for(int i=0; i<size; i++){
            results[i] = readBigInteger();
        }
        return results;
    }

    public ECPoint[] readPoints(int size, ECCurve curve) throws IOException {
        ECPoint[] points = new ECPoint[size];

        for(int i=0; i<size; i++){
            points[i] = readPoint(curve);
        }

        return points;
    }

    public ECPoint readPoint(ECCurve curve) throws IOException {
        int length = readInt();
        return curve.decodePoint(readBytes(length));
    }

    public SessionID readSessionID() throws IOException {
        return new SessionID(readBytes(GotrCrypto.DIGEST_SIZE));
    }

    public KeySpec readPubKeySpec() throws IOException {
        int length = readInt();
        return new X509EncodedKeySpec(readBytes(length));
    }

    public byte[] readSignature() throws IOException {
        int length = readInt();
        return readBytes(length);

    }

    public byte[] readSignatures() throws IOException {
        int length = readInt();
        return readBytes(length);
    }

    public String readString() throws IOException {
        int length = readInt();
        return GotrUtil.decodeUTF8(readBytes(length));
    }
}
