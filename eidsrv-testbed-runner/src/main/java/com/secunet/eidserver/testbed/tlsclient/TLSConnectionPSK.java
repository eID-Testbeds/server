package com.secunet.eidserver.testbed.tlsclient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class TLSConnectionPSK extends PSKTlsClient
{
	private static final Logger logger = LogManager.getLogger(TLSConnectionPSK.class);

	private final int[] supported_suites;

	private final Vector namedCurves;
	private final short[] hashAlgorithms, signatureAlgorithms;
    private final String host;

	/**
	 * Create a TLS PSK client without a client certificate using the given
	 * parameters
	 * 
	 * @param host The host to connect to
	 * @param PSKID The PSK identity
	 * @param PSK The PSK
	 */
	TLSConnectionPSK(String host, byte[] PSKID, byte[] PSK)
	{
		super(new BcTlsCrypto(new SecureRandom()),new TLSPSKClientIdentity(PSKID, PSK));
		this.supported_suites = new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA };
		this.namedCurves = new Vector(List.of(NamedGroup.brainpoolP256r1, NamedGroup.brainpoolP384r1, NamedGroup.brainpoolP512r1, NamedGroup.secp224r1, NamedGroup.secp256r1, NamedGroup.secp384r1,
				NamedGroup.secp521r1));
		this.hashAlgorithms = new short[] { HashAlgorithm.sha512, HashAlgorithm.sha384, HashAlgorithm.sha256, HashAlgorithm.sha224, HashAlgorithm.sha1 };
		this.signatureAlgorithms = new short[] { SignatureAlgorithm.rsa, SignatureAlgorithm.ecdsa };
		this.host = host;
	}

	// Returns the cipher suites that can be used for the connection
	@Override
	public int[] getCipherSuites()
	{
		return supported_suites;
	}

	@Override
	public ProtocolVersion[] getProtocolVersions() {
		return ProtocolVersion.TLSv12.only();
	}

	@Override
	public ProtocolVersion[] getSupportedVersions()
	{
		return ProtocolVersion.TLSv12.only();
	}

	// Do not terminate
	@Override
	public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException
	{
	}

	private boolean containsECCCipherSuites(int[] cipherSuites)
	{
        for (int cipherSuite : cipherSuites) {
            if (TlsECCUtils.isECCCipherSuite(cipherSuite)) {
                return true;
            }
        }
		return false;
	}

	// set up the client extensions
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Hashtable getClientExtensions() throws IOException
	{
		Hashtable clientExtensions = super.getClientExtensions();
		clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);

		// add curves. if none have been specified, use the defaults according
		// to TR03116
		if (containsECCCipherSuites(getCipherSuites()))
		{
			TlsExtensionsUtils.addSupportedGroupsExtension(clientExtensions, namedCurves);
		}

		// add all combinations of hash and signature algorithms that have been
		// specified
		this.supportedSignatureAlgorithms = new Vector<SignatureAndHashAlgorithm>();
		for (Short hashAlg : hashAlgorithms)
		{
			for (Short sigAlg : signatureAlgorithms)
			{
				this.supportedSignatureAlgorithms.addElement(new SignatureAndHashAlgorithm(hashAlg, sigAlg));
			}
		}
		clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
		TlsExtensionsUtils.addSignatureAlgorithmsExtension(clientExtensions, supportedSignatureAlgorithms);

		Vector serverNames = new Vector(1);
		serverNames.addElement(new ServerName(NameType.host_name, host.getBytes(StandardCharsets.UTF_8)));
		TlsExtensionsUtils.addServerNameExtensionClient(clientExtensions, serverNames);

		return clientExtensions;
	}

	@Override
	public void notifyAlertRaised(short level, short description, String message, Throwable cause)
	{
		if (level == AlertLevel.fatal)
		{
			logger.info("TLS error during sending: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description) + System.lineSeparator()
					+ message);
		}
		else
		{
			logger.debug("TLS warning during sending: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description)
					+ System.lineSeparator() + message);
		}
		super.notifyAlertRaised(level, description, message, cause);
	}

	@Override
	public void notifyAlertReceived(short level, short description)
	{
		if (level == AlertLevel.fatal)
		{
			logger.info("TLS error during reading: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description));
		}
		else
		{
			logger.debug("TLS warning during reading: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description));
		}
		super.notifyAlertReceived(level, description);
	}

	@Override
	public TlsAuthentication getAuthentication() throws IOException
	{
		logger.debug("Using only server certificates");
		return new ServerOnlyTlsAuthentication() {
			@Override
			public void notifyServerCertificate(TlsServerCertificate serverCertificate) {
				System.out.println(serverCertificate.getCertificate().getCertificateList()[0].toString());
			}
		};
	}

}
