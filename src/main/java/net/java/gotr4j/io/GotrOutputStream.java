package net.java.gotr4j.io;

import net.java.gotr4j.SessionID;
import net.java.gotr4j.crypto.Counter;
import net.java.gotr4j.util.GotrUtil;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;

public class GotrOutputStream{

    private final ByteArrayOutputStream outputStream;

    public GotrOutputStream(){
        outputStream = new ByteArrayOutputStream();
    }

    public void writeInt(int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(value)
                .array();

        outputStream.write(bytes);
    }

    public void writeBigInteger(BigInteger value) throws IOException {

        byte[] bytes = BigIntegers.asUnsignedByteArray(value);

        writeInt(bytes.length);

        outputStream.write(bytes);
    }

    public void writeByte(byte b) throws IOException {
        outputStream.write(b);
    }

    public void write(byte[] data) throws IOException {
        outputStream.write(data);
    }

    public String encode(){
        return GotrUtil.toBase64(outputStream.toByteArray());
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }

    public void writeString(String string) throws IOException {
        byte[] bytes = GotrUtil.encodeUTF8(string);
        writeInt(bytes.length);
        write(GotrUtil.encodeUTF8(string));
    }

    public void writeShort(short value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(2)
                .order(ByteOrder.BIG_ENDIAN)
                .putShort(value)
                .array();

        outputStream.write(bytes);
    }

    public void writeCounter(Counter counter) {
        outputStream.write(counter.toByteArray(), 0, 8);
    }


    public void writeBigIntegers(BigInteger[] values) throws IOException {
        for(int i=0; i<values.length; i++){
            writeBigInteger(values[i]);
        }
    }

    public void writePoints(ECPoint[] values) throws IOException {
        for(int i=0; i<values.length; i++){
            byte[] bytes = values[i].getEncoded(true);
            writeInt(bytes.length);
            write(bytes);
        }
    }

    public void writeSessionID(SessionID sessionID) throws IOException {
        write(sessionID.getBytes());
    }

    public void writePublicKey(PublicKey publicKey) throws IOException {
        byte[] bytes = publicKey.getEncoded();
        writeInt(bytes.length);
        write(bytes);
    }

    public void writeSignature(byte[] signature) throws IOException {
        writeInt(signature.length);
        write(signature);
    }

    public void writeSignatures(byte[] signatures) throws IOException {
        writeInt(signatures.length);
        write(signatures);
    }

    public void writeECPoint(ECPoint point) throws IOException {
        byte[] bytes = point.getEncoded();
        writeInt(bytes.length);
        write(bytes);
    }
}
