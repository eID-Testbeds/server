package com.secunet.eidserver.testbed.tlsclient;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.TlsSigner;
import org.bouncycastle.tls.crypto.impl.bc.*;

import java.io.IOException;
import java.security.cert.CertificateParsingException;

public class DefaultTlsSignerCredentials
    extends AbstractTlsSignerCredentials
{
    protected TlsContext context;
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    protected SignatureAndHashAlgorithm signatureAndHashAlgorithm;

    protected TlsSigner signer;

    public DefaultTlsSignerCredentials(TlsContext context, Certificate certificate, AsymmetricKeyParameter privateKey) throws CertificateParsingException {
        this(context, certificate, privateKey, null);
    }

    public DefaultTlsSignerCredentials(TlsContext context, Certificate certificate, AsymmetricKeyParameter privateKey,
        SignatureAndHashAlgorithm signatureAndHashAlgorithm) {
        if (certificate == null)
        {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        if (certificate.isEmpty())
        {
            throw new IllegalArgumentException("'certificate' cannot be empty");
        }
        if (privateKey == null)
        {
            throw new IllegalArgumentException("'privateKey' cannot be null");
        }
        if (!privateKey.isPrivate())
        {
            throw new IllegalArgumentException("'privateKey' must be private");
        }
        if (TlsUtils.isTLSv12(context) && signatureAndHashAlgorithm == null)
        {
            throw new IllegalArgumentException("'signatureAndHashAlgorithm' cannot be null for (D)TLS 1.2+");
        }

        if (privateKey instanceof RSAKeyParameters)
        {
            this.signer = new BcTlsRSASigner((BcTlsCrypto)(context.getCrypto()), (RSAKeyParameters)privateKey, null);
        }
        else if (privateKey instanceof DSAPrivateKeyParameters)
        {
            this.signer = new BcTlsDSASigner((BcTlsCrypto)(context.getCrypto()), (DSAPrivateKeyParameters)privateKey);
        }
        else if (privateKey instanceof ECPrivateKeyParameters)
        {
            this.signer = new BcTlsECDSASigner((BcTlsCrypto)(context.getCrypto()), (ECPrivateKeyParameters) privateKey);
        }
        else
        {
            throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
        }

        this.context = context;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.signatureAndHashAlgorithm = signatureAndHashAlgorithm;
    }

    public Certificate getCertificate()
    {
        return certificate;
    }

    public byte[] generateCertificateSignature(byte[] hash)
        throws IOException
    {
        return signer.generateRawSignature(signatureAndHashAlgorithm, hash);
    }

    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm()
    {
        return signatureAndHashAlgorithm;
    }
}
