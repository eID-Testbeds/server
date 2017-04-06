package com.secunet.eidserver.testbed.tlsclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.CompressionMethod;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsCredentials;

public class TLSConnection extends DefaultTlsClient
{
	private static final Logger logger = LogManager.getLogger(TLSConnection.class);

	private int[] supported_suites;
	private final Certificate client_cert;
	private final AsymmetricKeyParameter client_key;
	private final boolean clientAuthentication;
	private final TLSConnectionClient connectionClient;
	private String compressionMethod, cipherSuite, serverVersion;

	// Assume that client authentication is required if a certificate with key
	// is supplied
	TLSConnection(TLSConnectionClient connectionClient, Certificate cert, AsymmetricKeyParameter keys, int[] supported_cipher_suites)
	{
		this.connectionClient = connectionClient;
		supported_suites = supported_cipher_suites;
		client_cert = cert;
		client_key = keys;
		clientAuthentication = true;
	}

	// Assume that client authentication is required if a certificate with key
	// is supplied
	TLSConnection(TLSConnectionClient connectionClient, Certificate cert, AsymmetricKeyParameter keys)
	{
		this.connectionClient = connectionClient;
		supported_suites = new int[] { CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256,
				CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384 };
		client_cert = cert;
		client_key = keys;
		clientAuthentication = true;
	}

	// Perform a Server-Only Authentication if client certificate is not
	// supplied
	TLSConnection(TLSConnectionClient connectionClient, int[] supported_cipher_suites)
	{
		this.connectionClient = connectionClient;
		supported_suites = supported_cipher_suites;
		client_cert = null;
		client_key = null;
		clientAuthentication = false;
	}

	// Perform a Server-Only Authentication if client certificate is not
	// supplied
	TLSConnection(TLSConnectionClient connectionClient)
	{
		this.connectionClient = connectionClient;
		supported_suites = new int[] { CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256,
				CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA };
		client_cert = null;
		client_key = null;
		clientAuthentication = false;
	}

	// Allows the manipulation cipher suites that can be used for the connection
	public void setCipherSuites(int[] supported_cipher_suites)
	{
		supported_suites = supported_cipher_suites;
	}

	// Returns the cipher suites that can be used for the connection
	@Override
	public int[] getCipherSuites()
	{
		return supported_suites;
	}

	// We support only TLS1.2
	@Override
	public ProtocolVersion getMinimumVersion()
	{
		return ProtocolVersion.SSLv3;
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
			logger.fatal("TLS error during sending: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description) + System.getProperty("line.separator")
					+ message);
		}
		else
		{
			logger.warn("TLS warning during sending: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description) + System.getProperty("line.separator")
					+ message);
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
				public void notifyServerCertificate(Certificate arg0) throws IOException
				{
					connectionClient.verifyTLSServerCerts(arg0);
				}

				@Override
				public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException
				{
					logger.debug("Answering request for client certificates");
					return new DefaultTlsSignerCredentials(context, client_cert, client_key);
				}

			};

		}
		else
		{
			logger.debug("Using only server certificates");
			return new ServerOnlyTlsAuthentication() {

				@Override
				public void notifyServerCertificate(Certificate arg0) throws IOException
				{
					connectionClient.verifyTLSServerCerts(arg0);
				}
			};
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.secunet.bouncycastle.crypto.tls.AbstractTlsClient#notifyServerVersion(com.secunet.bouncycastle.crypto.tls.ProtocolVersion)
	 */
	@Override
	public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException
	{
		this.serverVersion = serverVersion.toString();
		super.notifyServerVersion(serverVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.secunet.bouncycastle.crypto.tls.AbstractTlsClient#notifySelectedCompressionMethod(short)
	 */
	@Override
	public void notifySelectedCompressionMethod(short selectedCompressionMethod)
	{
		for (Field f : CompressionMethod.class.getFields())
		{
			try
			{
				if (f.getShort(null) == selectedCompressionMethod)
				{
					this.compressionMethod = f.getName();
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not decode compression method:" + System.getProperty("line.separator") + trace.toString());
			}
		}
		super.notifySelectedCompressionMethod(selectedCompressionMethod);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.secunet.bouncycastle.crypto.tls.AbstractTlsPeer#notifyHandshakeComplete()
	 */
	@Override
	public void notifyHandshakeComplete() throws IOException
	{
		connectionClient.setHandshakeData("Protocol version: " + serverVersion + ", Ciphersuite: " + cipherSuite + " Compression method: " + compressionMethod);
		super.notifyHandshakeComplete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.secunet.bouncycastle.crypto.tls.AbstractTlsClient#notifySelectedCipherSuite(int)
	 */
	@Override
	public void notifySelectedCipherSuite(int selectedCipherSuite)
	{
		for (Field f : CipherSuite.class.getFields())
		{
			try
			{
				if (f.getInt(null) == selectedCipherSuite)
				{
					this.cipherSuite = f.getName();
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not decode ciphersuite:" + System.getProperty("line.separator") + trace.toString());
			}
		}
		super.notifySelectedCipherSuite(selectedCipherSuite);
	}

}
