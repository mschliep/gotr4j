package net.java.gotr4j.p2p.io;

import net.java.gotr4j.io.GotrInputStream;
import net.java.gotr4j.p2p.P2pContext;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestEvent;
import net.java.gotr4j.p2p.gotr.event.ReceivedDigestVerificationEvent;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretEvent;
import net.java.gotr4j.p2p.gotr.event.ReceivedSecretVerificationEvent;
import net.java.gotr4j.p2p.smp.event.*;
import net.java.hsm.HSMEvent;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.KeySpec;

public class EncryptedMessageParser {

    public static HSMEvent parseEncryptedMessage(GotrInputStream input, P2pContext context) throws IOException {
        byte type = input.readByte();

        HSMEvent event = null;

        if(EncryptedMessageType.SECRET_KEY.id == type){
            event = parseSecretKey(input, context);
        }
        else if(EncryptedMessageType.SECRET_VERIFICATION.id == type){
            event = parseSecretVerification(input, context);
        }
        else if(EncryptedMessageType.DIGEST.id == type){
            event = parseDigest(input, context);
        }
        else if(EncryptedMessageType.DIGEST_VERIFCATION.id == type){
            event = parseDigestVerification(input, context);
        }
        else if(EncryptedMessageType.SMP_INIT.id == type){
            event = parseSmpInit(input, context);
        }
        else if(EncryptedMessageType.SMP_RESPONSE.id == type){
            event = parseSmpResponse(input, context);
        }
        else if(EncryptedMessageType.SMP_INIT_FINAL.id == type){
            event = parseSmpInitFinal(input, context);
        }
        else if(EncryptedMessageType.SMP_RESPONSE_FINAL.id == type){
            event = parseSmpResponseFinal(input, context);
        }
        else if(EncryptedMessageType.SMP_ABORT.id == type){
            event = new ReceivedSmpAbortEvent();
        }

        return event;
    }

    private static HSMEvent parseSmpResponseFinal(GotrInputStream input, P2pContext context) throws IOException {
        ECPoint rb = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        byte[] crb = input.readBytes(context.getCrypto().DIGEST_SIZE);
        BigInteger db7 = input.readBigInteger();

        return new ReceivedFinalSmpResponseEvent(rb, crb, db7);
    }

    private static HSMEvent parseSmpInitFinal(GotrInputStream input, P2pContext context) throws IOException {
        ECPoint pa = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        ECPoint qa = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());

        byte[] cap = input.readBytes(context.getCrypto().DIGEST_SIZE);

        BigInteger da5 = input.readBigInteger();
        BigInteger da6 = input.readBigInteger();

        ECPoint ra = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        byte[] cr = input.readBytes(context.getCrypto().DIGEST_SIZE);

        BigInteger da7 = input.readBigInteger();

        return new ReceivedInitFinalSmpEvent(pa, qa, cap, da5, da6, ra, cr, da7);
    }

    private static HSMEvent parseSmpResponse(GotrInputStream input, P2pContext context) throws IOException {
        ECPoint gb2 = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        ECPoint gb3 = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());

        BigInteger db2 = input.readBigInteger();
        BigInteger db3 = input.readBigInteger();

        byte[] cb2 = input.readBytes(context.getCrypto().DIGEST_SIZE);
        byte[] cb3 = input.readBytes(context.getCrypto().DIGEST_SIZE);

        ECPoint pb = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        ECPoint qb = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());

        byte[] cp = input.readBytes(context.getCrypto().DIGEST_SIZE);

        BigInteger db5 = input.readBigInteger();
        BigInteger db6 = input.readBigInteger();

        return new ReceivedSmpResponseEvent(gb2, gb3, db2, db3, cb2, cb3, pb, qb, cp, db5, db6);
    }

    private static HSMEvent parseSmpInit(GotrInputStream input, P2pContext context) throws IOException {
        ECPoint ga2 = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());
        ECPoint ga3 = input.readPoint(context.getCrypto().NAMED_CURVE.getCurve());

        BigInteger da2 = input.readBigInteger();
        BigInteger da3 = input.readBigInteger();

        byte[] ca2 = input.readBytes(context.getCrypto().DIGEST_SIZE);
        byte[] ca3 = input.readBytes(context.getCrypto().DIGEST_SIZE);

        String question = input.readString();

        return new ReceivedInitSmpEvent(ga2, ga3, da2, da3, ca2, ca3, question);
    }

    private static ReceivedDigestEvent parseDigest(GotrInputStream input, P2pContext context) throws IOException {
        byte[] digest = input.readBytes(context.getCrypto().DIGEST_SIZE);
        byte[] signature = input.readSignature();
        byte[] secret = input.readBytes(context.getCrypto().SECRET_SIZE);
        KeySpec verificationKeySpec = input.readPubKeySpec();

        return new ReceivedDigestEvent(digest, signature, secret, verificationKeySpec);
    }

    private static ReceivedDigestVerificationEvent parseDigestVerification(GotrInputStream input, P2pContext context) throws IOException {
        byte[] digestVerification = input.readBytes(context.getCrypto().DIGEST_SIZE);

        return new ReceivedDigestVerificationEvent(digestVerification);
    }

    private static ReceivedSecretVerificationEvent parseSecretVerification(GotrInputStream input, P2pContext context) throws IOException {
        byte[] verification = input.readBytes(context.getCrypto().DIGEST_SIZE);
        return new ReceivedSecretVerificationEvent(verification);
    }

    private static ReceivedSecretEvent parseSecretKey(GotrInputStream input, P2pContext context) throws IOException {
        byte[] secret = input.readBytes(context.getCrypto().SECRET_SIZE);
        KeySpec verificationKeySpec = input.readPubKeySpec();
        byte[] groupVerificationKey = input.readBytes(context.getCrypto().DIGEST_SIZE);

        return new ReceivedSecretEvent(secret, verificationKeySpec, groupVerificationKey);
    }

}
