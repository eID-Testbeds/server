package com.secunet.eidserver.testbed.common.helper;

import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.constants.Bitlengths;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.cv.RuntimeCVCertGen;
import com.secunet.testbedutils.bouncycertgen.x509.RuntimeX509CertificateFactory;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.exception.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import static com.secunet.eidserver.testbed.common.constants.GeneralConstants.TEMP_DIR;


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

		Protocol(String value)
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
		RSA_ALG_ID("RSA"), ECDSA_ALG_ID("ECDSA"), DSA_ALG_ID("DSA"), HMAC_ALG_ID("HMAC"), CSCA_ALG_ID("CSCA");

		private final String value;

		Algorithm(String value)
		{
			this.value = value;
		}

		public String getAlgorithmName()
		{
			return value;
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
		String returnValue;

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
			returnValue = url.substring(url.length() - "shaXXX".length()).toUpperCase();
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
		List<String> listURI = new LinkedList<>();
		if (CryptoHelper.Algorithm.RSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256_MGF1.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384_MGF1.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512_MGF1.value());
		}
		else if (CryptoHelper.Algorithm.DSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_DSA_SHA_1.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256.value());
		}
		else if (CryptoHelper.Algorithm.ECDSA_ALG_ID.getAlgorithmName().equals(algorithm))
		{
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384.value());
			listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512.value());
		}
		else if (CryptoHelper.Algorithm.HMAC_ALG_ID.getAlgorithmName().equals(algorithm))
		{
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
	public static X509Certificate loadSignatureCertificate(Protocol protocol, Algorithm alg, EService eservice, TestCandidate candidate)
	{
		return loadCertificate(protocol, alg, eservice.toString(), candidate);
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
        return switch (service) {
            case EDSA -> Algorithm.DSA_ALG_ID;
            case EECDSA -> Algorithm.ECDSA_ALG_ID;
            default -> Algorithm.RSA_ALG_ID;
        };
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
	 * @param extension
	 * 			{@link String} File extension
	 * @return {@link X509Certificate}
	 */
	public static X509Certificate loadCertificate(Protocol protocol, Algorithm alg, String name, String extension, TestCandidate candidate)
	{
		CertificateFactory certFactory;
		try
		{
			certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(getCryptoFileMap(candidate).get("certificates/" + alg.getAlgorithmName() + "/" + name + extension)));
		}
		catch (CertificateException | NoSuchProviderException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load " + protocol.getProtocolName() + " x509 certificate " + name + " due to: " + e.getMessage() + System.lineSeparator() + trace);
		}
		return null;
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
	public static X509Certificate loadCertificate(Protocol protocol, Algorithm alg, String name, TestCandidate candidate) {
		return loadCertificate(protocol, alg, name, ".crt", candidate);
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
	public static PrivateKey loadSignatureKey(Protocol protocol, Algorithm alg, EService eservice, TestCandidate candidate)
	{
		return loadKey(protocol, alg, eservice.toString(), candidate);
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
	public static PrivateKey loadKey(Protocol protocol, Algorithm alg, String name, TestCandidate candidate)
	{
		try
		{
			// handling for the case that the key is for CSCA, as CSCA is not a standard algorithm for key generation in KeyFactories but is needed for the correct dir
			String algorithm = Objects.equals(alg.getAlgorithmName(), "CSCA") ? "EC" : alg.getAlgorithmName();
			KeyFactory factory = KeyFactory.getInstance(algorithm);
			byte[] keyBytes = getCryptoFileMap(candidate).get("keys/" + alg.getAlgorithmName() + "/" + name + ".DER");
			KeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			return factory.generatePrivate(spec);
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load " + protocol.getProtocolName() + " key " + name + " due to: " + e.getMessage() + System.lineSeparator() + trace);
		}
		return null;
	}

	/**
	 * Loads a CV certificate from the file with the given relative path
	 * 
	 * @param path
	 * 			The relative path to the file
	 * @param candidate
	 * 			The test candidate
	 * @return {@link CVCertificate} if the certificate was successfully loaded, or <i>null</i> otherwise
	 */
	public static CVCertificate loadCvFromFile(String path, TestCandidate candidate)
	{
		try
		{
			DataBuffer cvBuffer = new DataBuffer(getCryptoFileMap(candidate).get(path));
			return new CVCertificate(cvBuffer);
		}
		catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load expected DV certificate: " + e.getMessage() + System.lineSeparator() + trace);
		}
		return null;
	}

	/**
	 * Generates and returns a map containing all certificates
	 *
	 * @param seed The seed which certificates will be generated based on
	 * @return {@link Map} Certificate map containing the names as keys of type {@link String} and the certificates themselves as {@link byte}[]
	 */
	public static GeneratedCertsContainer generateCertificatesAndKeys(byte[] seed, URL attachedUrl) throws IOException {
		HashMap<String, byte[]> certificatesAndKeysCV = new HashMap<>();
		HashMap<String, byte[]> certificatesAndKeysX509 = new HashMap<>();
		HashMap<String, byte[]> certificatesAndKeys = new HashMap<>();
		GeneratedCertsContainer generatedCertsContainer = null;


		InputStream termSector1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("staticfiles/TERM_SECTOR1.x509.cv");
		InputStream termSector2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("staticfiles/TERM_SECTOR2.x509.cv");

		if(termSector1 == null || termSector2 == null)
			return null;

		RuntimeX509CertificateFactory x509Gen = new RuntimeX509CertificateFactory(certificatesAndKeysX509,
				IOUtils.toByteArray(termSector1),
				IOUtils.toByteArray(termSector2));
		RuntimeCVCertGen cvGen = new RuntimeCVCertGen(certificatesAndKeysCV);

		InputStream x509XMLInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("X509CertificateGeneratorList.xml");
		// TODO: XML Validation
		InputStream cvCertXmlFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("CVCertificates.xml");

		try {
			Set<GeneratedCertificate> certs = x509Gen.generateFromXML(x509XMLInputStream, seed, attachedUrl.getHost());
			byte[] masterlist = x509Gen.generateMasterListAndStore();
			byte[] defectlist = x509Gen.generateDefectListAndStore();
			Map<Character, byte[]> blacklists = new HashMap<>();
			blacklists.put('X', x509Gen.generateBlackListAndStore('X'));
			blacklists.put('D', x509Gen.generateBlackListAndStore('D'));
			blacklists.put('E', x509Gen.generateBlackListAndStore('E'));
			Set<CVCertificate> cvCerts = cvGen.generateFromXML(cvCertXmlFile, attachedUrl.getProtocol() + "://" + attachedUrl.getHost() + "/");
			certificatesAndKeys.putAll(certificatesAndKeysX509);
			certificatesAndKeys.putAll(certificatesAndKeysCV);

			generatedCertsContainer = new GeneratedCertsContainer(certs, cvCerts, masterlist, defectlist, blacklists, certificatesAndKeys);

		}
		catch (NoSuchAlgorithmException | IOException | CertificateException | OperatorCreationException |
			   ParserConfigurationException | SAXException | KeyStoreException e) {
			logger.error("Error generating certificates and keys, Type: " +  e.getClass());
			logger.error("Error message: " + e.getMessage());
		}

		return generatedCertsContainer;
	}

	public static Map<String, byte[]> getCryptoFileMap(TestCandidate testCandidate) {
		Map<String, byte[]> cryptoFiles = new HashMap<>();
		for (CryptoFile entry : testCandidate.getCryptoFiles()) {
			cryptoFiles.put(entry.getFilename(), entry.getContent());
		}
		return cryptoFiles;
	}

	public static Set<CryptoFile> fileMapToCryptoFiles(Map<String, byte[]> fileMap) {
		Set<CryptoFile> cryptoFiles = new HashSet<>();
		for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
			CryptoFile cryptoFile = new CryptoFile() {
				@Override
				public byte[] getContent() {
					return entry.getValue();
				}

				@Override
				public void setContent(byte[] content) { throw new RuntimeException("Not implemented"); }

				@Override
				public String getFilename() {
					return entry.getKey();
				}

				@Override
				public void setFilename(String filename) {
					throw new RuntimeException("Not implemented");
				}

				@Override
				public String getId() {
					return null;
				}

				@Override
				public void setId(String id) {
					throw new RuntimeException("Not implemented");
				}
			};
			cryptoFiles.add(cryptoFile);
		}
		return cryptoFiles;
	}

	public static void writeCryptoFiles(TestCandidate testCandidate) {
		try {
			FileUtils.deleteDirectory(new File(TEMP_DIR));
		} catch (IOException e) {
			logger.error("Error deleting temp directory", e);
		}
		for (CryptoFile entry : testCandidate.getCryptoFiles()) {

			String fileName = entry.getFilename();
			byte[] content = entry.getContent();

			Path filePath = Paths.get(TEMP_DIR, fileName);
			try {
				Path directoryPath = filePath.getParent();
				if (!Files.exists(directoryPath)) {
					Files.createDirectories(directoryPath);
				}

				Files.write(filePath, content);

			} catch (IOException e) {
				logger.error("Error writing file for: " + fileName, e);
			} catch (NullPointerException npe) {
				logger.error("NPE for file: " + filePath);
			}
		}
	}

	/**
	 * This helper tests whether a given set of {@link BigInteger} only contains allowed bitlengths (eg. 2048, 3072, 4096, ...)
	 *
	 * @param toTest
	 *            The set of values to test
	 * @return <i>true</i> iff all items contain allowed values, else <i>false</>
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean checkOnlyAllowedBitlengths(Set<BigInteger> toTest)
	{
		if (null == toTest)
		{
			return false;
		}
		boolean overall = true;
		for (BigInteger c : toTest)
		{
			overall &= Bitlengths.ALLOWED_BIT_LENGTHS.contains(c);
		}
		return overall;
	}
}
