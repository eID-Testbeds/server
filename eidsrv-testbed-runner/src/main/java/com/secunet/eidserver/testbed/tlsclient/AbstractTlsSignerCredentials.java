package com.secunet.eidserver.testbed.tlsclient;

import org.bouncycastle.tls.Certificate;
import org.bouncycastle.tls.SignatureAndHashAlgorithm;

import java.io.IOException;

public abstract class AbstractTlsSignerCredentials
    extends AbstractTlsCredentials
    implements TlsSignerCredentials
{
    @Override
    public byte[] generateCertificateSignature(byte[] hash) throws IOException {
        return new byte[0];
    }

    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm()
    {
        throw new IllegalStateException("TlsSignerCredentials implementation does not support (D)TLS 1.2+");
    }

    @Override
    public Certificate getCertificate() {
        return null;
    }
}
