package com.secunet.eidserver.testbed.common.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;


/**
 * Helper class
 */
public class CryptoHelper
{
	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public enum Protocol
	{
		SAML_PROT_ID("SAML"), SOAP_PROT_ID("SOAP");

		private final String value;

		private Protocol(String value)
		{
			this.value = value;
		}

		public String getProtocolName()
		{
			return value;
		}
	}

	public enum Algorithm
	{
		RSA_ALG_ID("RSA"), ECDSA_ALG_ID("ECDSA"), DSA_ALG_ID("DSA"), HMAC_ALG_ID("HMAC");

		private final String value;

		private Algorithm(String value)
		{
			this.value = value;
		}

		public String getAlgorithmName()
		{
			return value;
		}

		/**
		 * Returns the enum value that represents the algorithm with the given name
		 * 
		 * @param name
		 *            Name
		 */
		public static Algorithm getFromEnumName(String name)
		{
			for (Algorithm x : Algorithm.values())
			{
				if (name != null && name.equals(x.toString()))
				{
					return x;
				}
			}
			return null;
		}

		/**
		 * Returns the enum value that represents the algorithm with the given algorithm name
		 * 
		 * @param name
		 *            Name
		 */
		public static Algorithm getFromAlgorithmName(String name)
		{
			for (Algorithm x : Algorithm.values())
			{
				if (name != null && name.equals(x.getAlgorithmName()))
				{
					return x;
				}
			}
			return null;
		}

	}


	private static final Logger logger = LogManager.getLogger(CryptoHelper.class);

	/*
	 * Determine algorithm from a given signature URL
	 * 
	 * @param url Signature URL
	 */
	public static String getAlgorithmForSignatureUrl(String url)
	{
		String returnValue = null;

		if (url == null)
			return null;

		if (url.contains("sha1"))
		{
			returnValue = "SHA1";
		}
		else if (url.endsWith("ripemd160"))
		{
			returnValue = "RIPEMD160";
		}
		// shaXXX
		else
		{
			returnValue = url.substring(url.length() - "shaXXX".length(), url.length()).toUpperCase();
		}

		returnValue += "with";
		if (url.contains("rsa"))
		{
			returnValue += "RSA";
		}
		else if (url.contains("ecdsa"))
		{
			returnValue += "ECDSA";
		}
		else if (url.contains("dsa"))
		{
			returnValue += "DSA";
		}
		else
		{
			returnValue += "HMAC";
		}

		return returnValue;
	}


	/**
	 * Determine algorithm from a given string
	 * 
	 * @param toCheck
	 *            String to check
	 * 
	 * @param toUpperCase
	 *            If true returned algorithm is in upper case, if false in lower case
	 * 
	 * @return Algorithm identifier or null if nothing valid has been found
	 */
	public static String getAlgorithm(String toCheck, boolean toUpperCase)
	{
		String returnValue = null;

		if (toCheck == null)
			return null;

		String lowerToCheck = toCheck.toLowerCase();

		if (lowerToCheck.contains("rsa"))
		{
			returnValue = "rsa";
		}
		else if (lowerToCheck.contains("ecdsa"))
		{
			returnValue = "ecdsa";
		}
		else if (lowerToCheck.contains("dsa"))
		{
			returnValue = "dsa";
		}
		else if (lowerToCheck.contains("hmac"))
		{
			returnValue = "hmac";
		}
		if ((returnValue != null) && (toUpperCase))
		{
			returnValue = returnValue.toUpperCase();
		}

		return returnValue;
	}

	/**
	 * Get a list of all supported signature algorithms regarding a given crypto algorithm
	 * 
	 * @param algorithm
	 *            Algorithm
	 * @return Supported signature algorithms
	 */
	public static List<String> getSupportedSignatureAlgorithms(String algorithm)
	{
		List<String> listURI = new LinkedList<String>();
		if (CryptoHelper.Algorithm.RSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			// listURIRSA.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_MD_5.value()); // Not supported
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_RSA_SHA_1.value());
			// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_224.value()); // Not supported
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512.value());
			// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_RIPEMD_160.value()); // Not supported
		}
		else if (CryptoHelper.Algorithm.DSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_DSA_SHA_1.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256.value());
		}
		else if (CryptoHelper.Algorithm.DSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_1.value());
			// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_224.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512.value());
		}
		else if (CryptoHelper.Algorithm.HMAC_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_HMAC_SHA_1.value());
			// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_224.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_256.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_384.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_HMAC_SHA_512.value());
		}
		return listURI;
	}

	/**
	 * Load the certificate with the given name from the file system. For valid parameter values:
	 * 
	 * @see com.secunet.eidserver.testbed.common.constants.GeneralConstants for valid parameter values
	 * 
	 * @param protocol
	 *            Protocol identifier
	 * @param alg
	 *            Algorithm identifier
	 * @param eservice
	 *            {@link EService} The eService for which the certificate is to be loaded.
	 * @return {@link X509Certificate}
	 */
	public static X509Certificate loadSignatureCertificate(Protocol protocol, Algorithm alg, EService eservice)
	{
		return loadCertificate(protocol, alg, eservice.toString());
	}

	/**
	 * Returns the algorithm ID that needs to be used for a given {@link EService}.
	 * 
	 * @param service
	 *            {@link EService}
	 * @return {@link CryptoHelper.Algorithm} The algorithm to be used
	 */
	public static Algorithm getAlgorithmFromService(EService service)
	{
		switch (service)
		{
			case EDSA:
				return CryptoHelper.Algorithm.DSA_ALG_ID;
			case EECDSA:
				return CryptoHelper.Algorithm.ECDSA_ALG_ID;
			default:
				return CryptoHelper.Algorithm.RSA_ALG_ID;
		}
	}


	/**
	 * Load the certificate with the given name from the file system. For valid parameter values:
	 * 
	 * @see com.secunet.eidserver.testbed.common.constants.GeneralConstants for valid parameter values
	 * 
	 * @param protocol
	 *            Protocol identifier
	 * @param alg
	 *            Algorithm identifier
	 * @param name
	 *            {@link String} The name of the certificate to load.
	 * @return {@link X509Certificate}
	 */
	public static X509Certificate loadCertificate(Protocol protocol, Algorithm alg, String name)
	{
		CertificateFactory certFactory;
		try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("certificates/" + alg.getAlgorithmName() + "/" + name + ".crt");)
		{
			if (input != null)
			{
				certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
				X509Certificate cert = (X509Certificate) certFactory.generateCertificate(input);
				return cert;
			}
		}
		catch (CertificateException | NoSuchProviderException | IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load " + protocol.getProtocolName() + " x509 certificate " + name + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	/**
	 * Load the key with the given name from the file system.
	 * 
	 * @see com.secunet.eidserver.testbed.common.constants.GeneralConstants for valid parameter values
	 *
	 * @param protocol
	 *            Protocol identifier
	 * @param alg
	 *            Algorithm identifier
	 * @param eservice
	 *            {@link EService} The eService for which the key is to be loaded.
	 * @return {@link PrivateKey}
	 */
	public static PrivateKey loadSignatureKey(Protocol protocol, Algorithm alg, EService eservice)
	{
		return loadKey(protocol, alg, eservice.toString());
	}

	/**
	 * Load the key with the given name from the file system.
	 * 
	 * @see com.secunet.eidserver.testbed.common.constants.GeneralConstants for valid parameter values
	 *
	 * @param protocol
	 *            Protocol identifier
	 * @param alg
	 *            Algorithm identifier
	 * @param name
	 *            {@link String} The name of the key to load.
	 * @return {@link PrivateKey}
	 */
	public static PrivateKey loadKey(Protocol protocol, Algorithm alg, String name)
	{
		try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("keys/" + alg.getAlgorithmName() + "/" + name + ".der");)
		{
			if (input != null)
			{
				KeyFactory factory = KeyFactory.getInstance(alg.getAlgorithmName());
				byte[] keyBytes = IOUtils.toByteArray(input);
				KeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
				return factory.generatePrivate(spec);
			}
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load " + protocol.getProtocolName() + " key " + name + " due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	/**
	 * Loads a CV certificate from the file with the given relative path
	 * 
	 * @param path
	 * @return {@link CVCertificate} if the certificate was successfully loaded, or <i>null</i> otherwise
	 */
	public static CVCertificate loadCvFromFile(String path)
	{
		try
		{
			File expectedDvFile = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
			DataBuffer cvBuffer = new DataBuffer(IOUtils.toByteArray(new FileInputStream(expectedDvFile)));
			return new CVCertificate(cvBuffer);
		}
		catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException | IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load expected DV certificate: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

}
