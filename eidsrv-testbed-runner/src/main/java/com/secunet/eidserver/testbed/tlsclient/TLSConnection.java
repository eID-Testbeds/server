package com.secunet.eidserver.testbed.tlsclient;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class TLSConnection extends DefaultTlsClient
{
	private static final Logger logger = LogManager.getLogger(TLSConnection.class);

	private final int[] supported_suites;
	private final Certificate client_cert;
	private final AsymmetricKeyParameter client_key;
	private final boolean clientAuthentication;

	// Assume that client authentication is required if a certificate with key
	// is supplied
	TLSConnection(Certificate cert, AsymmetricKeyParameter keys, int[] supported_cipher_suites)
	{
        super(new BcTlsCrypto(new SecureRandom()));
        supported_suites = supported_cipher_suites;
		client_cert = cert;
		client_key = keys;
		clientAuthentication = true;
	}

	// Perform a Server-Only Authentication if client certificate is not
	// supplied
	TLSConnection(int[] supported_cipher_suites)
	{
		super(new BcTlsCrypto(new SecureRandom()));
		supported_suites = supported_cipher_suites;
		client_cert = null;
		client_key = null;
		clientAuthentication = false;
	}

	// Returns the cipher suites that can be used for the connection
	@Override
	public int[] getCipherSuites()
	{
		return supported_suites;
	}

	@Override
	public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException
	{
		// do not terminate
	}

	@Override
	public void notifyAlertRaised(short level, short description, String message, Throwable cause)
	{
		if (level == AlertLevel.fatal)
		{
			logger.fatal("TLS error during sending: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description) + System.lineSeparator()
					+ message);
		}
		else
		{
			logger.warn("TLS warning during sending: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description) + System.lineSeparator()
					+ message);
		}
		super.notifyAlertRaised(level, description, message, cause);
	}

	@Override
	public void notifyAlertReceived(short level, short description)
	{
		if (level == AlertLevel.fatal)
		{
			logger.info("TLS error during reading: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description));
		}
		else
		{
			logger.debug("TLS warning during reading: " + AlertLevel.getName(description) + System.lineSeparator() + AlertLevel.getText(description));
		}
		super.notifyAlertReceived(level, description);
	}

	// This function is called when establishing the connection to verify/supply
	// certificates
	@Override
	public TlsAuthentication getAuthentication() throws IOException
	{
		if (clientAuthentication)
		{
			logger.debug("Using client and server certificates");
			return new TlsAuthentication() {

				@Override
				public void notifyServerCertificate(TlsServerCertificate serverCertificate) throws IOException {
					// we do not need to perform any checks here
				}

				@Override
				public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException
				{
					logger.debug("Answering request for client certificates");

					// for TLS 1.2 there MUST be this object
					SignatureAndHashAlgorithm saha = null;
					if (TlsUtils.isTLSv12(context))
					{
						// TODO replace static values
						saha = new SignatureAndHashAlgorithm(HashAlgorithm.sha256, SignatureAlgorithm.rsa);
					}
					return new DefaultTlsSignerCredentials(context, client_cert, client_key, saha) {
					};
				}

			};

		}
		else
		{
			logger.debug("Using only server certificates");
			return new ServerOnlyTlsAuthentication() {

				@Override
				public void notifyServerCertificate(TlsServerCertificate serverCertificate) throws IOException {
					// we do not need to perform any checks here
				}
			};
		}
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Hashtable getClientExtensions() throws IOException {
		Hashtable clientExtensions = null;
		ProtocolVersion clientVersion = this.context.getClientVersion();
		if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(clientVersion)) {
			this.supportedSignatureAlgorithms = TlsUtils.getDefaultSupportedSignatureAlgorithms(this.context);
			clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
			TlsExtensionsUtils.addSignatureAlgorithmsExtension(clientExtensions, this.supportedSignatureAlgorithms);
		}

		if (containsECCCipherSuites(getCipherSuites())) {
			Vector namedCurves = new Vector(List.of(26, 27, 28, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 1, 2, 3, 6, 7, 8, 9, 10, 11, 13, 13, 14));
			short[] clientECPointFormats = new short[] { 0, 1, 2 };
			clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
			TlsExtensionsUtils.addSupportedGroupsExtension(clientExtensions, namedCurves);
			TlsExtensionsUtils.addSupportedPointFormatsExtension(clientExtensions, clientECPointFormats);
		}
		return clientExtensions;
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

}
