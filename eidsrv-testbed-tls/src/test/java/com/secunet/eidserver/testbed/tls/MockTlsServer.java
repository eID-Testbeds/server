package com.secunet.eidserver.testbed.tls;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.ClientCertificateType;
import org.bouncycastle.crypto.tls.DefaultTlsServer;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.tls.TlsEncryptionCredentials;
import org.bouncycastle.crypto.tls.TlsSignerCredentials;
import org.bouncycastle.crypto.tls.TlsUtils;

import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;

class MockTlsServer extends DefaultTlsServer
{
	private final TlsTestParameters tlsParameters;

	MockTlsServer(TlsTestParameters tlsParameters)
	{
		super();
		this.tlsParameters = tlsParameters;
	}

	@Override
	public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
	{
		PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
		out.println("TLS server raised alert: " + AlertLevel.getText(alertLevel) + ", " + AlertDescription.getText(alertDescription));
		if (message != null)
		{
			out.println("> " + message);
		}
		if (cause != null)
		{
			cause.printStackTrace(out);
		}
	}

	@Override
	public void notifyAlertReceived(short alertLevel, short alertDescription)
	{
		PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
		out.println("TLS server received alert: " + AlertLevel.getText(alertLevel) + ", " + AlertDescription.getText(alertDescription));
	}

	@Override
	protected ProtocolVersion getMaximumVersion()
	{
		return ProtocolVersion.TLSv12;
	}

	@Override
	public ProtocolVersion getServerVersion() throws IOException
	{
		ProtocolVersion serverVersion = super.getServerVersion();

		System.out.println("TLS server negotiated " + serverVersion);

		return serverVersion;
	}

	@Override
	public CertificateRequest getCertificateRequest() throws IOException
	{
		CertificateRequest certReq = null;

		if (tlsParameters.isClientAuthentication())
		{
			short[] certificateTypes = new short[] { ClientCertificateType.rsa_sign, ClientCertificateType.dss_sign, ClientCertificateType.ecdsa_sign };

			Vector serverSigAlgs = null;
			if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(serverVersion))
			{
				serverSigAlgs = TlsUtils.getDefaultSupportedSignatureAlgorithms();
			}

			Vector certificateAuthorities = new Vector();
			certificateAuthorities.add(TlsTestUtils.loadCertificateResource("x509-ca.pem").getSubject());

			certReq = new CertificateRequest(certificateTypes, serverSigAlgs, certificateAuthorities);
		}

		return certReq;
	}

	@Override
	public void notifyClientCertificate(org.bouncycastle.crypto.tls.Certificate clientCertificate) throws IOException
	{
		X500Name caName = TlsTestUtils.loadCertificateResource("x509-ca.pem").getSubject();
		Certificate[] chain = clientCertificate.getCertificateList();
		System.out.println("TLS server received client certificate chain of length " + chain.length);
		for (int i = 0; i != chain.length; i++)
		{
			Certificate entry = chain[i];
			// TODO Create fingerprint based on certificate signature algorithm digest
			System.out.println("    fingerprint:SHA-256 " + TlsTestUtils.fingerprint(entry) + " (" + entry.getSubject() + ")");
			System.out.println("Issued by CA: " + caName.equals(entry.getIssuer()));
		}
	}

	@Override
	protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException
	{
		return TlsTestUtils.loadEncryptionCredentials(context, new String[] { "x509-server.pem", "x509-ca.pem" }, "x509-server-key.pem");
	}

	@Override
	protected TlsSignerCredentials getRSASignerCredentials() throws IOException
	{
		return TlsTestUtils.loadSignerCredentials(context, supportedSignatureAlgorithms, SignatureAlgorithm.rsa, "x509-server.pem", "x509-server-key.pem");
	}
}