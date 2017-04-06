package com.secunet.eidserver.testbed.model.entities;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.enumerations.BitLength;
import com.secunet.eidserver.testbed.common.enumerations.ChipAuthenticationAlgorithm;
import com.secunet.eidserver.testbed.common.enumerations.EllipticCurve;
import com.secunet.eidserver.testbed.common.enumerations.MandatoryProfile;
import com.secunet.eidserver.testbed.common.enumerations.OptionalProfile;
import com.secunet.eidserver.testbed.common.enumerations.TlsCiphersuite;
import com.secunet.eidserver.testbed.common.enumerations.TlsVersion;
import com.secunet.eidserver.testbed.common.enumerations.XMLSIGAlgorithm;
import com.secunet.eidserver.testbed.common.enumerations.XMLSIGCanonicalizationMethod;
import com.secunet.eidserver.testbed.common.enumerations.XMLSIGDigestMethod;
import com.secunet.eidserver.testbed.common.enumerations.XmlEncryptionAlgorithm;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestProfile;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.dao.CertificateCVDAO;
import com.secunet.eidserver.testbed.dao.CertificateX509DAO;
import com.secunet.eidserver.testbed.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.dao.TestProfileDAO;
import com.secunet.eidserver.testbed.dao.TestResultDAO;
import com.secunet.eidserver.testbed.dao.TlsDAO;
import com.secunet.eidserver.testbed.dao.XmlSignatureAlgorithmDAO;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;
import com.secunet.testbedutils.bouncycertgen.CertificateGenerator;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.utilities.HexUtil;


public class ModelTests {
	private final String CVC_NAME_TERMINAL = "TEST_CVC_TERMINAL";
	private final String CVC_NAME_DV = "TEST_CVC_DV";
	private final String CVC_NAME_CVCA = "TEST_CVC_CVCA";
	private final String X509_NAME = "TEST_X509";

	@BeforeClass
	public void setup() throws Exception {
		// nothing to do here
	}

	@AfterSuite
	public void teardown() throws Exception {
		// nothing to do here
	}

	@Test
	public void testSerializeCVC() throws Exception {		
		// Terminal certificate
		BufferedReader brTerminal = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/CERT_CV_TERM_1_A.cvcert.hex"), "UTF-8"));
		String dataTerminal = new String();
		try {
			String line;
			while ((line = brTerminal.readLine()) != null) {
				dataTerminal = dataTerminal.concat(line);
			}
		} finally {
			brTerminal.close();
		}
		assertTrue(dataTerminal.length() > 0);
		CVCertificate certTerminal = new CVCertificate(new DataBuffer(HexUtil.hexStringToByteArray(dataTerminal)));

		CertificateCV generatedTerminal = new CertificateCVEntity();
		generatedTerminal.setCertificateName(CVC_NAME_TERMINAL);
		generatedTerminal.setCvCertificate(certTerminal);

		// try persisting the certificate
		final CertificateCVDAO certDAO = (CertificateCVDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/CertificateCVDAO");
		certDAO.persist((CertificateCVEntity) generatedTerminal);

		// now try reading it back
		CertificateCV readCertTerminal = certDAO.findByName(CVC_NAME_TERMINAL);
		assertNotNull(readCertTerminal);
		assertNotNull(readCertTerminal.getCvCertificate());
		assertEquals(certTerminal.toString(), readCertTerminal.getCvCertificate().toString());
		
		// DV certificate
		BufferedReader brDV = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/CERT_CV_DV_1_A.cvcert.hex"), "UTF-8"));
		String dataDV = new String();
		try {
			String line;
			while ((line = brDV.readLine()) != null) {
				dataDV = dataDV.concat(line);
			}
		} finally {
			brDV.close();
		}
		assertTrue(dataDV.length() > 0);
		CVCertificate certDV = new CVCertificate(new DataBuffer(HexUtil.hexStringToByteArray(dataDV)));

		CertificateCV generatedDV = new CertificateCVEntity();
		generatedDV.setCertificateName(CVC_NAME_DV);
		generatedDV.setCvCertificate(certDV);
		certDAO.persist((CertificateCVEntity) generatedDV);

		// now try reading it back
		CertificateCV readCertDV = certDAO.findByName(CVC_NAME_DV);
		assertNotNull(readCertDV);
		assertNotNull(readCertDV.getCvCertificate());
		assertEquals(certDV.toString(), readCertDV.getCvCertificate().toString());
		
		// CVCA certificate
		BufferedReader brCVCA = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/CERT_CV_CVCA_1.cvcert.hex"), "UTF-8"));
		String dataCVCA = new String();
		try {
			String line;
			while ((line = brCVCA.readLine()) != null) {
				dataCVCA = dataCVCA.concat(line);
			}
		} finally {
			brCVCA.close();
		}
		assertTrue(dataCVCA.length() > 0);
		CVCertificate certCVCA = new CVCertificate(new DataBuffer(HexUtil.hexStringToByteArray(dataCVCA)));

		CertificateCV generatedCVCA = new CertificateCVEntity();
		generatedCVCA.setCertificateName(CVC_NAME_CVCA);
		generatedCVCA.setCvCertificate(certCVCA);
		certDAO.persist((CertificateCVEntity) generatedCVCA);

		// now try reading it back
		CertificateCV readCertCVCA = certDAO.findByName(CVC_NAME_CVCA);
		assertNotNull(readCertCVCA);
		assertNotNull(readCertCVCA.getCvCertificate());
		assertEquals(certCVCA.toString(), readCertCVCA.getCvCertificate().toString());
	}

	@Test
	public void testSerializeX509() throws Exception {
		// read a sample certificate
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/saml_enc_base64.txt"), "UTF-8"));
		String data = new String();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				data = data.concat(line);
			}
		} finally {
			br.close();
		}
		assertTrue(data.length() > 0);

		// create the certificate
		X509Certificate cert = CertificateGenerator.x509FromBase64String(data);

		// try persisting the certificate
		final CertificateX509DAO certDAO = (CertificateX509DAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/CertificateX509DAO");
		CertificateX509 certObject = new CertificateX509Entity();
		certObject.setCertificateName(X509_NAME);
		certObject.setX509certificate(cert);
		certDAO.persist((CertificateX509Entity) certObject);

		// now read the certificate back
		CertificateX509 readCert = certDAO.findByName(X509_NAME);
		assertNotNull(readCert);
		assertNotNull(readCert.getX509certificate());
		assertEquals(cert, readCert.getX509certificate());
	}

	@Test
	public void testCreateXMLSignature() throws Exception {
		final XmlSignatureEntity signatureAlgorithm = new XmlSignatureEntity();
		signatureAlgorithm.setId("42");
		// supported bitlengths
		Set<BitLength> bSet = new HashSet<BitLength>();
		bSet.add(BitLength.createUsingValue(1024));
		bSet.add(BitLength.createUsingValue(2048));
		bSet.add(BitLength.createUsingValue(4096));
		signatureAlgorithm.setBitLengths(bSet);
		assertEquals(3, signatureAlgorithm.getBitLengths().size());
		// supported elliptic curves
		Set<EllipticCurve> ecSet = new HashSet<EllipticCurve>();
		ecSet.add(EllipticCurve.secp160k1);
		ecSet.add(EllipticCurve.secp192k1);
		signatureAlgorithm.setEllipticCurves(ecSet);
		assertEquals(2, signatureAlgorithm.getEllipticCurves().size());
		// digest algorithm
		Set<XMLSIGDigestMethod> digestSet = new HashSet<XMLSIGDigestMethod>();
		digestSet.add(XMLSIGDigestMethod.SHA1);
		signatureAlgorithm.setDigestMethod(digestSet);
		// canonicalization method
		Set<XMLSIGCanonicalizationMethod> canSet = new HashSet<XMLSIGCanonicalizationMethod>();
		canSet.add(XMLSIGCanonicalizationMethod.C14N);
		signatureAlgorithm.setCanonicalizationMethod(canSet);
		// signature method
		signatureAlgorithm.setSignatureAlgorithm(XMLSIGAlgorithm.SHAwithDSA);

		// persist the result
		final XmlSignatureAlgorithmDAO sigDAO = (XmlSignatureAlgorithmDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/XmlSignatureAlgorithmDAO");
		assertNotNull(sigDAO);
		sigDAO.persist(signatureAlgorithm);
	}

	@Test
	public void testCreateTLS() throws Exception {
		final TlsEntity tls = new TlsEntity();
		tls.setId("24");
		// supported bitlengths
		Set<BitLength> bSet = new HashSet<BitLength>();
		bSet.add(BitLength.createUsingValue(1024));
		bSet.add(BitLength.createUsingValue(2048));
		bSet.add(BitLength.createUsingValue(4096));
		tls.setBitLengthsRsa(bSet);
		assertEquals(3, tls.getBitLengthsRsa().size());
		// supported elliptic curves
		Set<EllipticCurve> ecSet = new HashSet<EllipticCurve>();
		ecSet.add(EllipticCurve.secp160k1);
		ecSet.add(EllipticCurve.secp192k1);
		tls.setEllipticCurves(ecSet);
		assertEquals(2, tls.getEllipticCurves().size());
		// ciphersuites
		Set<TlsCiphersuite> ciphersuites = new HashSet<TlsCiphersuite>();
		ciphersuites.add(TlsCiphersuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA);
		tls.setTlsCiphersuite(ciphersuites);
		// tls version
		tls.setTlsVersion(TlsVersion.onetwo);

		// persist the result
		final TlsDAO tlsDAO = (TlsDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TlsDAO");
		assertNotNull(tlsDAO);
		tlsDAO.persist(tls);
	}

	@Test(dependsOnMethods = { "testCreateXMLSignature", "testCreateTLS" })
	public void testCreateProfile() throws Exception {
		final TestProfile testProfile = new TestProfileEntity();
		assertNotNull(testProfile);
		// id
		testProfile.setId("100");
		// candidate info
		testProfile.setName("secunet eIDServer-Mock");
		testProfile.setVersionMajor(1);
		testProfile.setVersionMinor(42);
		// optional and mandatory profiles
		Set<MandatoryProfile> mpSet = new HashSet<MandatoryProfile>();
		// sample of adding via string
		try {
			mpSet.add(MandatoryProfile.valueOf("EAC"));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		mpSet.add(MandatoryProfile.PAOS);
		testProfile.setMandatoryProfiles(mpSet);
		assertEquals(2, testProfile.getMandatoryProfiles().size());
		Set<OptionalProfile> opSet = new HashSet<OptionalProfile>();
		opSet.add(OptionalProfile.SAML);
		opSet.add(OptionalProfile.SOAP);
		testProfile.setOptionalProfiles(opSet);
		assertEquals(2, testProfile.getOptionalProfiles().size());
		// chip authentication
		Set<ChipAuthenticationAlgorithm> caSet = new HashSet<ChipAuthenticationAlgorithm>();
		caSet.add(ChipAuthenticationAlgorithm.CA_ECDH_AES_CBC_CMAC_128);
		testProfile.setChipAuthenticationAlgorithms(caSet);
		assertEquals(1, testProfile.getChipAuthenticationAlgorithms().size());
		// XML encryption algorithm
		Set<XmlEncryptionAlgorithm> xmleSet = new HashSet<XmlEncryptionAlgorithm>();
		XmlEncryptionAlgorithm x = XmlEncryptionAlgorithm.getAlgorithmFromURL("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
		if (x != null) {
			xmleSet.add(x);
		}
		x = XmlEncryptionAlgorithm.getAlgorithmFromURL("http://www.w3.org/2001/04/xmlenc#aes192-cbc");
		if (x != null) {
			xmleSet.add(x);
		}
		x = XmlEncryptionAlgorithm.getAlgorithmFromURL("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
		if (x != null) {
			xmleSet.add(x);
		}
		testProfile.setXmlEncryptionAlgorithms(xmleSet);
		assertEquals(3, testProfile.getXmlEncryptionAlgorithms().size());
		// XML signature
		final XmlSignatureAlgorithmDAO sigDAO = (XmlSignatureAlgorithmDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/XmlSignatureAlgorithmDAO");
		Collection<XmlSignatureEntity> xmlSigAlgs = sigDAO.findAll();
		assertNotNull(xmlSigAlgs);
		assertEquals(false, xmlSigAlgs.isEmpty());
		XmlSignature sigalg = null;
		// note: there is only one signature algorithm defined here
		for (XmlSignature s : xmlSigAlgs) {
			sigalg = s;
		}
		assertNotNull(sigalg);
		Set<XmlSignature> xmlsSet = new HashSet<XmlSignature>();
		xmlsSet.add(sigalg);
		testProfile.setXmlSignatureAlgorithms(xmlsSet);
		// tls
		final TlsDAO tlsDAO = (TlsDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TlsDAO");
		Collection<TlsEntity> tls = tlsDAO.findAll();
		assertNotNull(tls);
		assertEquals(false, tls.isEmpty());
		Tls tlselement = null;
		// note: there is only one signature algorithm defined here
		for (Tls t : tls) {
			tlselement = t;
		}
		assertNotNull(tlselement);
		Set<Tls> tlsSet = new HashSet<Tls>();
		tlsSet.add(tlselement);
		testProfile.setTls2(tlsSet);
		// persist
		final TestProfileDAO testProfileDAO = (TestProfileDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestProfileDAO");
		assertNotNull(testProfileDAO);
		testProfileDAO.persist((TestProfileEntity) testProfile);
	}

	@Test
	public void testCreateSingleTestCaseStep() throws Exception {
		final TestCaseStepEntity testCaseStep = new TestCaseStepEntity();
		assertNotNull(testCaseStep);
		testCaseStep.setName("TEST_STEP_IN");
		testCaseStep.setInbound(true);
		testCaseStep.setDefault(true);
		testCaseStep.setMessage("ClientVersion 3,1" + System.getProperty("line.separator") + "ClientRandom[32]" + System.getProperty("line.separator")
				+ "SessionID: None (new session)" + System.getProperty("line.separator") + "Suggested Cipher Suites:" + System.getProperty("line.separator")
				+ "TLS_RSA_WITH_3DES_EDE_CBC_SHA" + System.getProperty("line.separator") + "TLS_RSA_WITH_DES_CBC_SHA" + System.getProperty("line.separator")
				+ "Suggested Compression Algorithm: NONE");

		// persist
		final TestCaseStepDAO testCaseStepDAO = (TestCaseStepDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestCaseStepDAO");
		assertNotNull(testCaseStepDAO);
		testCaseStepDAO.persist(testCaseStep);
	}

	@Test(dependsOnMethods = { "testCreateSingleTestCaseStep" })
	public void testCreateTestCase() throws Exception {
		final TestCase testCase = new DefaultTestCaseEntity();
		assertNotNull(testCase);
		testCase.setName("EIDSERVER_A1_01");
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		// add first step
		final TestCaseStepDAO testCaseStepDAO = (TestCaseStepDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestCaseStepDAO");
		TestCaseStep testCaseStepOne = testCaseStepDAO.findByName("TEST_STEP_IN");
		assertNotNull(testCaseStepOne);
		steps.add(testCaseStepOne);
		// add second step
		TestCaseStep testCaseStepTwo = new TestCaseStepEntity();
		assertNotNull(testCaseStepTwo);
		testCaseStepTwo.setName("TEST_STEP_OUT");
		testCaseStepTwo.setInbound(false);
		testCaseStepTwo.setDefault(true);
		testCaseStepTwo.setMessage("Version 3,1" + System.getProperty("line.separator") + "ServerRandom[32]" + System.getProperty("line.separator")
				+ "SessionID: bd608869f0c629767ea7e3ebf7a63bdcffb0ef58b1b941e6b0c044acb6820a77" + System.getProperty("line.separator") + "Use Cipher Suite:"
				+ System.getProperty("line.separator") + "TLS_RSA_WITH_3DES_EDE_CBC_SHA" + System.getProperty("line.separator") + "Compression Algorithm: NONE");
		testCaseStepDAO.persist((TestCaseStepEntity) testCaseStepTwo);
		steps.add(testCaseStepTwo);
		testCase.setTestCaseSteps(steps);
		assertEquals(2, testCase.getTestCaseSteps().size());
		// persist
		final DefaultTestCaseDAO testCaseDAO = (DefaultTestCaseDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/DefaultTestCaseDAO");
		assertNotNull(testCaseDAO);
		testCaseDAO.persist((DefaultTestCaseEntity) testCase);
	}

	// these messages describe a test run that failed on the second step
	@Test
	public void testWriteLogMessages() throws Exception {
		// positive
		LogMessageEntity messageOne = new LogMessageEntity();
		assertNotNull(messageOne);
		messageOne.setId("200");
		messageOne.setSuccess(true);
		messageOne.setMessage("Everything went fine!");
		// failed
		LogMessageEntity messageTwo = new LogMessageEntity();
		assertNotNull(messageTwo);
		messageTwo.setId("201");
		messageTwo.setSuccess(false);
		messageTwo.setMessage("This failed, because of reasons.");
		// persist
		LogMessageDAO logMessageDAO = (LogMessageDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/LogMessageDAO");
		assertNotNull(logMessageDAO);
		logMessageDAO.persist(messageOne);
		logMessageDAO.persist(messageTwo);
	}

	@Test(dependsOnMethods = { "testWriteLogMessages", "testCreateTestCase", "testCreateProfile" })
	public void testResults() throws Exception {
		TestResultEntity result = new TestResultEntity();
		result.setId("798");
		// load test case
		final DefaultTestCaseDAO testCaseDAO = (DefaultTestCaseDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/DefaultTestCaseDAO");
		assertNotNull(testCaseDAO);
		result.setTestCase(testCaseDAO.findByName("EIDSERVER_A1_01"));
		// load profile
		final TestProfileDAO testProfileDAO = (TestProfileDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestProfileDAO");
		assertNotNull(testProfileDAO);
		result.setTestProfile(testProfileDAO.findById("100"));
		// load messages
		LogMessageDAO logMessageDAO = (LogMessageDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/LogMessageDAO");
		assertNotNull(logMessageDAO);
		List<LogMessage> logMessages = new ArrayList<LogMessage>();
		logMessages.add(logMessageDAO.findById("200"));
		logMessages.add(logMessageDAO.findById("201"));
		result.setLogMessages(logMessages);
		assertEquals(2, result.getLogMessages().size());
		// persist
		TestResultDAO resultDAO = (TestResultDAO) ContainerProvider.getContainer().getContext().lookup("java:global/classes/TestResultDAO");
		assertNotNull(resultDAO);
		resultDAO.persist(result);
		// test if cascading works
		resultDAO.delete(result);
		LogMessage logmessage = logMessageDAO.findById("200");
		assertEquals(null, logmessage);
	}
}
