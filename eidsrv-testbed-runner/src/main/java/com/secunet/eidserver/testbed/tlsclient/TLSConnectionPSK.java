package com.secunet.eidserver.testbed.tlsclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.CompressionMethod;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.bouncycastle.crypto.tls.NamedCurve;
import org.bouncycastle.crypto.tls.PSKTlsClient;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.crypto.tls.TlsECCUtils;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.crypto.tls.TlsUtils;

public class TLSConnectionPSK extends PSKTlsClient
{
	private static final Logger logger = LogManager.getLogger(TLSConnectionPSK.class);

	private int[] supported_suites, namedCurves;
	private final Certificate client_cert;
	private final AsymmetricKeyParameter client_key;
	private final boolean clientAuthentication;
	private short[] hashAlgorithms, signatureAlgorithms;
	private String compressionMethod, cipherSuite, serverVersion;
	private final TLSConnectionClient connectionClient;

	/**
	 * Create a TLS PSK client without a client certificate using the given
	 * parameters
	 * 
	 * @param connectionClient
	 * @param PSKID
	 * @param PSK
	 */
	TLSConnectionPSK(TLSConnectionClient connectionClient, byte[] PSKID, byte[] PSK)
	{
		super(new TLSPSKClientIdentity(PSKID, PSK));
		this.connectionClient = connectionClient;
		client_cert = null;
		client_key = null;
		clientAuthentication = false;
		setDefaultCryptography();
	}

	/**
	 * Create a TLS PSK client with a client certificate using the given
	 * parameters
	 * 
	 * @param connectionClient
	 * @param cert
	 * @param keys
	 * @param PSKID
	 * @param PSK
	 */
	TLSConnectionPSK(TLSConnectionClient connectionClient, Certificate cert, AsymmetricKeyParameter keys, byte[] PSKID, byte[] PSK)
	{
		super(new TLSPSKClientIdentity(PSKID, PSK));
		this.connectionClient = connectionClient;
		client_cert = cert;
		client_key = keys;
		clientAuthentication = true;
		setDefaultCryptography();
	}

	// set default crypto params
	private void setDefaultCryptography()
	{
		supported_suites = new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA };
		namedCurves = new int[] { NamedCurve.brainpoolP256r1, NamedCurve.brainpoolP384r1, NamedCurve.brainpoolP512r1, NamedCurve.secp224r1, NamedCurve.secp256r1, NamedCurve.secp384r1,
				NamedCurve.secp521r1 };
		hashAlgorithms = new short[] { HashAlgorithm.sha512, HashAlgorithm.sha384, HashAlgorithm.sha256, HashAlgorithm.sha224, HashAlgorithm.sha1 };
		signatureAlgorithms = new short[] { SignatureAlgorithm.rsa, SignatureAlgorithm.ecdsa };
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
		return ProtocolVersion.TLSv12;
	}

	// Do not terminate
	@Override
	public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException
	{
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
		if (TlsECCUtils.containsECCCipherSuites(getCipherSuites()))
		{
			TlsECCUtils.addSupportedEllipticCurvesExtension(clientExtensions, namedCurves);
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
		TlsUtils.addSignatureAlgorithmsExtension(clientExtensions, supportedSignatureAlgorithms);

		return clientExtensions;
	}

	@Override
	public void notifyAlertRaised(short level, short description, String message, Throwable cause)
	{
		if (level == AlertLevel.fatal)
		{
			logger.info("TLS error during sending: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description) + System.getProperty("line.separator")
					+ message);
		}
		else
		{
			logger.debug("TLS warning during sending: " + AlertLevel.getName(description) + System.getProperty("line.separator") + AlertLevel.getText(description)
					+ System.getProperty("line.separator") + message);
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

	/**
	 * @return the signatureAlgorithms
	 */
	public short[] getSignatureAlgorithms()
	{
		return signatureAlgorithms;
	}

	/**
	 * @param signatureAlgorithms
	 *            the signatureAlgorithms to set
	 */
	public void setSignatureAlgorithms(short[] signatureAlgorithms)
	{
		this.signatureAlgorithms = signatureAlgorithms;
	}

	/**
	 * @return the hashAlgorithms
	 */
	public short[] getHashAlgorithms()
	{
		return hashAlgorithms;
	}

	/**
	 * @param hashAlgorithms
	 *            the hashAlgorithms to set
	 */
	public void setHashAlgorithms(short[] hashAlgorithms)
	{
		this.hashAlgorithms = hashAlgorithms;
	}

	/**
	 * @return the namedCurves
	 */
	public int[] getNamedCurves()
	{
		return namedCurves;
	}

	/**
	 * @param namedCurves
	 *            the namedCurves to set
	 */
	public void setNamedCurves(int[] namedCurves)
	{
		this.namedCurves = namedCurves;
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
