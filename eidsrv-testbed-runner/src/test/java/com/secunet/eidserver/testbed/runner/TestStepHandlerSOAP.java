package com.secunet.eidserver.testbed.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignatureMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;

public class TestStepHandlerSOAP
{
	private static final Logger logger = LogManager.getLogger(TestStepHandlerSOAP.class);
	private LogMessageDAO logMessageDAO;

	@BeforeClass
	private void initMocks()
	{
		// mocks
		logMessageDAO = mock(LogMessageDAO.class);

		// empty POJOs
		LogMessage emptyLogMessage = new LogMessageTestingPojo();
		when(logMessageDAO.createNew()).thenReturn(emptyLogMessage);

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@Test(enabled = true)
	public void testCreateSOAPrequest() throws Exception
	{
		String candidateName = "TEST_CANDIDATE";
		URL candidateUrl = new URL("https://some.eid.server.de:8445/ecardpaos/paosreceiver");
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL eidUrl = new URL("https://some.eid.server.de:8443/eID-Server-20/eID");
		KnownValues values = null;
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eid=\"http://bsi.bund.de/eID/\"><soapenv:Header /><soapenv:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"body-123\"><eid:getServerInfoRequest /></soapenv:Body></soapenv:Envelope>";

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, candidateUrl, testbedRefreshAdress, eidUrl, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		String request = shs.createSOAPrequest(message);
		assertNotNull(request);
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestRSAManipulated() throws Exception
	{
		String candidateName = "TEST_CANDIDATE";
		URL candidateUrl = new URL("https://some.eid.server.de:8445/ecardpaos/paosreceiver");
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL eidUrl = new URL("https://some.eid.server.de:8443/eID-Server-20/eID");
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, "RSA_MANIPULATED"));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, SignatureMethod.RSA_SHA1));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_DIGEST, DigestMethod.SHA1));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_CANONICALIZATION, CanonicalizationMethod.EXCLUSIVE));

		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eid=\"http://bsi.bund.de/eID/\"><soapenv:Header /><soapenv:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"body-123\"><eid:getServerInfoRequest /></soapenv:Body></soapenv:Envelope>";

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, candidateUrl, testbedRefreshAdress, eidUrl, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		String request = shs.createSOAPrequest(message);
		assertNotNull(request);
	}

	@Test(enabled = true)
	public void testRetreiveTcTokenUrlFromHTML() throws Exception
	{
		String candidateName = "TEST_CANDIDATE";
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL attachedUrl = new URL("https://some.eid.server.de:8443/getTCToken");
		KnownValues values = new KnownValues();

		String message = "<a href=\"http://127.0.0.1:24727/eID-Client?tcTokenURL=https%3a%2f%2fsome.eid.server.de%3a8443%2fgetTcToken.html%3bjsessionid%3d4242424242424242424242\">Start eID-Client</a><p>The requested data will be read from your document.</p>";

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, attachedUrl, testbedRefreshAdress, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		URL url = shs.parseTcTokenUrl(message);
		assertNotNull(url);
		assertEquals("https://some.eid.server.de:8443/getTcToken.html;jsessionid=4242424242424242424242", url.toString());
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestGetResult() throws Exception
	{
		String candidateName = "TEST_CANDIDATE";
		URL candidateUrl = new URL("https://some.eid.server.de:8445/ecardpaos/paosreceiver");
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL eidUrl = new URL("https://some.eid.server.de:8443/eID-Server-20/eID");
		KnownValues values = null;
		String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eid=\"http://bsi.bund.de/eID/\"><soapenv:Header /><soapenv:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"body-123\"><eid:getResultRequest><eid:Session><eid:ID>1234CAFEBABE</eid:ID></eid:Session><eid:RequestCounter>1</eid:RequestCounter></eid:getResultRequest></soapenv:Body></soapenv:Envelope>";

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, candidateUrl, testbedRefreshAdress, eidUrl, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		String request = shs.createSOAPrequest(message);
		assertNotNull(request);
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestRSA() throws Exception
	{
		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
		IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256;
		IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N;
		IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1;
		logger.debug("--- Testing : " + algorithm.getAlgorithmName() + " || " + uri.value() + " || " + canonicalization.value() + " || " + digest.value() + " ...");
		String request = createSOAPrequest(algorithm, uri, canonicalization, digest, EService.A);
		assertNotNull(request);
		logger.debug("Created request:\n" + request);
		assertTrue(request.contains(uri.value()));
		assertTrue(request.contains(canonicalization.value()));
		assertTrue(request.contains(digest.value()));
	}

	@Test(enabled = true)
	public void testCreateSOAPrequestRSAC_14_N_11WithComments() throws Exception
	{
		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
		IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256;
		IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2006_12_XML_C_14_N_11_WITH_COMMENTS;
		IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1;
		logger.debug("--- Testing : " + algorithm.getAlgorithmName() + " || " + uri.value() + " || " + canonicalization.value() + " || " + digest.value() + " ...");
		String request = createSOAPrequest(algorithm, uri, canonicalization, digest, EService.A);
		assertNotNull(request);
		logger.debug("Created request:\n" + request);
		assertTrue(request.contains(uri.value()));
		assertTrue(request.contains(canonicalization.value()));
		assertTrue(request.contains(digest.value()));
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestRSAUnsupportedCanonicalization() throws Exception
	{
		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
		IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_RSA_SHA_1;
		IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2010_10_XML_C_14_N_2;
		IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1;
		logger.debug("--- Testing : " + algorithm.getAlgorithmName() + " || " + uri.value() + " || " + canonicalization.value() + " || " + digest.value() + " ...");
		assertNull(createSOAPrequest(algorithm, uri, canonicalization, digest, EService.A));
	}

	// TODO disabled due to application server restrictions. re-enable once the xmlsig library is replaced
	// @Test(enabled = true)
	// public void testCreateSOAPrequestECDSA() throws Exception
	// {
	// CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.ECDSA_ALG_ID;
	// IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256;
	// IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N;
	// IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1;
	// logger.debug("--- Testing : " + algorithm.getAlgorithmName() + " || " + uri.value() + " || " + canonicalization.value() + " || " + digest.value() + " ...");
	// String request = createSOAPrequest(algorithm, uri, canonicalization, digest, EService.EECDSA);
	// assertNotNull(request);
	// logger.debug("Created request:\n" + request);
	// assertTrue(request.contains(uri.value()));
	// assertTrue(request.contains(canonicalization.value()));
	// assertTrue(request.contains(digest.value()));
	// }
	//
	// @Test(enabled = true)
	// public void testCreateSOAPrequestDSA() throws Exception
	// {
	// CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.DSA_ALG_ID;
	// IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256;
	// IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N;
	// IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1;
	// logger.debug("--- Testing : " + algorithm.getAlgorithmName() + " || " + uri.value() + " || " + canonicalization.value() + " || " + digest.value() + " ...");
	// String request = createSOAPrequest(algorithm, uri, canonicalization, digest, EService.EDSA);
	// assertNotNull(request);
	// logger.debug("Created request:\n" + request);
	// assertTrue(request.contains(uri.value()));
	// assertTrue(request.contains(canonicalization.value()));
	// assertTrue(request.contains(digest.value()));
	// }


	@Test(enabled = true)
	public void testCreateSOAPrequestRSAMultipleWorking() throws Exception
	{
		List<String> listURI = new LinkedList<String>();
		// listURIRSA.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_MD_5.value()); // Not supported
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_RSA_SHA_1.value());
		// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_224.value()); // Not supported
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512.value());
		// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_RIPEMD_160.value()); // Not supported

		LinkedList<String> listDigest = getAllSignatureDigests(true);
		LinkedList<String> listCanonicalization = getAllCanonicalizations(true);

		List<List<String>> allLists = new LinkedList<List<String>>();
		allLists.add(listURI);
		allLists.add(listDigest);
		allLists.add(listCanonicalization);

		Set<List<String>> allCombinations = getCombinations(allLists);

		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
		LinkedHashMap<String, String> mapTests = createSOAPrequestMultiple(allCombinations, algorithm, EService.A);

		Set<Entry<String, String>> entries = mapTests.entrySet();
		for (Map.Entry<String, String> entry : entries)
		{
			String value = entry.getValue();
			assertTrue(value.equals("OK"));
		}
	}

	@Test(enabled = true)
	public void testCreateSOAPrequestRSAMultipleAll() throws Exception
	{
		List<String> listURI = new LinkedList<String>();
		// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_MD_5.value()); // Not supported
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_RSA_SHA_1.value());
		// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_224.value()); // Not supported
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512.value());
		// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_RIPEMD_160.value()); // Not supported

		LinkedList<String> listDigest = getAllSignatureDigests(false);
		LinkedList<String> listCanonicalization = getAllCanonicalizations(false);

		List<List<String>> allLists = new LinkedList<List<String>>();
		allLists.add(listURI);
		allLists.add(listDigest);
		allLists.add(listCanonicalization);

		Set<List<String>> allCombinations = getCombinations(allLists);

		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
		createSOAPrequestMultiple(allCombinations, algorithm, EService.A);
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestDSAMultipleAll() throws Exception
	{
		List<String> listURI = new LinkedList<String>();
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_DSA_SHA_1.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256.value());

		LinkedList<String> listDigest = getAllSignatureDigests(false);
		LinkedList<String> listCanonicalization = getAllCanonicalizations(false);

		List<List<String>> allLists = new LinkedList<List<String>>();
		allLists.add(listURI);
		allLists.add(listDigest);
		allLists.add(listCanonicalization);

		Set<List<String>> allCombinations = getCombinations(allLists);

		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.DSA_ALG_ID;
		createSOAPrequestMultiple(allCombinations, algorithm, EService.EDSA);
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestDSAMultipleWorking() throws Exception
	{
		List<String> listURI = new LinkedList<String>();
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_DSA_SHA_1.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2009_XMLDSIG_11_DSA_SHA_256.value());

		LinkedList<String> listDigest = getAllSignatureDigests(true);
		LinkedList<String> listCanonicalization = getAllCanonicalizations(true);

		List<List<String>> allLists = new LinkedList<List<String>>();
		allLists.add(listURI);
		allLists.add(listDigest);
		allLists.add(listCanonicalization);

		Set<List<String>> allCombinations = getCombinations(allLists);

		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.DSA_ALG_ID;
		LinkedHashMap<String, String> mapTests = createSOAPrequestMultiple(allCombinations, algorithm, EService.EDSA);

		Set<Entry<String, String>> entries = mapTests.entrySet();
		for (Map.Entry<String, String> entry : entries)
		{
			String value = entry.getValue();
			assertTrue(value.equals("OK"));
		}
	}


	@Test(enabled = true)
	public void testCreateSOAPrequestECDSAMultipleAll() throws Exception
	{
		List<String> listURI = new LinkedList<String>();
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_1.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_224.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384.value());
		listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512.value());

		LinkedList<String> listDigest = getAllSignatureDigests(false);
		LinkedList<String> listCanonicalization = getAllCanonicalizations(false);

		List<List<String>> allLists = new LinkedList<List<String>>();
		allLists.add(listURI);
		allLists.add(listDigest);
		allLists.add(listCanonicalization);

		Set<List<String>> allCombinations = getCombinations(allLists);

		CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.ECDSA_ALG_ID;
		createSOAPrequestMultiple(allCombinations, algorithm, EService.EECDSA);
	}

	// TODO disabled due to application server restrictions. re-enable once the xmlsig library is replaced
	// @Test(enabled = true)
	// public void testCreateSOAPrequestECDSAMultipleWorking() throws Exception
	// {
	// List<String> listURI = new LinkedList<String>();
	// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_1.value());
	// // listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_224.value());
	// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256.value());
	// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384.value());
	// listURI.add(IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512.value());
	//
	// LinkedList<String> listDigest = getAllSignatureDigests(true);
	// LinkedList<String> listCanonicalization = getAllCanonicalizations(true);
	//
	// List<List<String>> allLists = new LinkedList<List<String>>();
	// allLists.add(listURI);
	// allLists.add(listDigest);
	// allLists.add(listCanonicalization);
	//
	// Set<List<String>> allCombinations = getCombinations(allLists);
	//
	// CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.ECDSA_ALG_ID;
	// LinkedHashMap<String, String> mapTests = createSOAPrequestMultiple(allCombinations, algorithm, EService.EECDSA);
	//
	// Set<Entry<String, String>> entries = mapTests.entrySet();
	// for (Map.Entry<String, String> entry : entries)
	// {
	// String value = entry.getValue();
	// assertTrue(value.equals("OK"));
	// }
	// }


	@Test(enabled = false)
	public void testGenerateMessageSaveUseId() throws Exception, SharedSecretNotYetReadyException
	{

		String candidateName = "TEST_CANDIDATE";
		URL candidateUrl = new URL("https://some.eid.server.de:8445/ecardpaos/paosreceiver");
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL eidUrl = new URL("https://some.eid.server.de:8443/eID-Server-20/eID");
		KnownValues values = null;

		String useIdAll = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_step.xsd\"> <p:HttpStepToken> <p:name>header</p:name> <p:value><![CDATA[POST [EID_INTERFACE_PATH] HTTP/1.1Accept-Encoding: gzip,deflateContent-Type: text/xml;charset=UTF-8SOAPAction: \"http://bsi.bund.de/eID/useID\"Content-Length: [[CHARLENGTH]]Host: [EID_INTERFACE_HOSTNAME]Connection: Keep-AliveUser-Agent: [TESTBED_USER_AGENT_NAME]/[TESTBED_USER_AGENT_MAJOR].[TESTBED_USER_AGENT_MINOR].[TESTBED_USER_AGENT_SUBMINOR]]]></p:value> <p:isMandatory>true</p:isMandatory> </p:HttpStepToken> <p:ProtocolStepToken> <p:name>message</p:name> <p:value><![CDATA[[[CREATE_SOAP_SECURITY_HEADER]]&lt;soapenv:Envelope xmlns:soapenv=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot; xmlns:eid=&quot;http://bsi.bund.de/eID/&quot;&gt;&lt;soapenv:Header /&gt;&lt;soapenv:Body xmlns:wsu=&quot;http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd&quot; wsu:Id=&quot;body-123&quot;&gt;&lt;eid:useIDRequest&gt;&lt;eid:UseOperations&gt;&lt;eid:DocumentType&gt;REQUIRED&lt;/eid:DocumentType&gt;&lt;eid:IssuingState&gt;REQUIRED&lt;/eid:IssuingState&gt;&lt;eid:DateOfExpiry&gt;REQUIRED&lt;/eid:DateOfExpiry&gt;&lt;eid:GivenNames&gt;REQUIRED&lt;/eid:GivenNames&gt;&lt;eid:FamilyNames&gt;REQUIRED&lt;/eid:FamilyNames&gt;&lt;eid:ArtisticName&gt;REQUIRED&lt;/eid:ArtisticName&gt;&lt;eid:AcademicTitle&gt;REQUIRED&lt;/eid:AcademicTitle&gt;&lt;eid:DateOfBirth&gt;REQUIRED&lt;/eid:DateOfBirth&gt;&lt;eid:PlaceOfBirth&gt;REQUIRED&lt;/eid:PlaceOfBirth&gt;&lt;eid:Nationality&gt;REQUIRED&lt;/eid:Nationality&gt;&lt;eid:BirthName&gt;REQUIRED&lt;/eid:BirthName&gt;&lt;eid:PlaceOfResidence&gt;REQUIRED&lt;/eid:PlaceOfResidence&gt;&lt;eid:ResidencePermitI&gt;REQUIRED&lt;/eid:ResidencePermitI&gt;&lt;eid:RestrictedID&gt;REQUIRED&lt;/eid:RestrictedID&gt;&lt;eid:AgeVerification&gt;REQUIRED&lt;/eid:AgeVerification&gt;&lt;eid:PlaceVerification&gt;REQUIRED&lt;/eid:PlaceVerification&gt;&lt;/eid:UseOperations&gt;&lt;/eid:useIDRequest&gt;&lt;/soapenv:Body&gt;&lt;/soapenv:Envelope&gt;]]></p:value> <p:isMandatory>true</p:isMandatory> </p:ProtocolStepToken></p:Step>";

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, candidateUrl, testbedRefreshAdress, eidUrl, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		shs.generateMsg(useIdAll);
	}

	// --------------------------
	// ----- Helper methods -----
	// --------------------------
	/** Helper method */
	private String createSOAPrequest(CryptoHelper.Algorithm algorithm, IcsXmlsecSignatureUri uri, IcsXmlsecSignatureCanonicalization canonicalization, IcsXmlsecSignatureDigest digest,
			EService service) throws Exception
	{
		String candidateName = "TEST_CANDIDATE";
		URL candidateUrl = new URL("https://some.eid.server.de:8445/ecardpaos/paosreceiver");
		String testbedRefreshAdress = GeneralConstants.TESTBED_REFRESH_URL;
		URL eidUrl = new URL("https://some.eid.server.de:8443/eID-Server-20/eID");
		// String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eid=\"http://bsi.bund.de/eID/\"><soapenv:Header /><soapenv:Body
		// xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"body-123\"><eid:getServerInfoRequest /></soapenv:Body></soapenv:Envelope>";
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eid=\"http://bsi.bund.de/eID/\"><soapenv:Header /><soapenv:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"body-123\"><eid:getServerInfoRequest /></soapenv:Body></soapenv:Envelope>";

		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE, algorithm.getAlgorithmName()));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_URI, uri.value()));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_CANONICALIZATION, canonicalization.value()));
		values.add(new KnownValue(GeneralConstants.XML_SIGNATURE_DIGEST, digest.value()));

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandlerSOAP shs = new StepHandlerSOAP(candidateName, candidateUrl, testbedRefreshAdress, eidUrl, values, logMessageDAO, service, EidCard.EIDCARD_1, 0);
		String request = shs.createSOAPrequest(message);
		// assertNotNull(request);
		return request;
	}


	/** Helper method */
	private static <T> Set<List<T>> getCombinations(List<List<T>> lists)
	{
		Set<List<T>> combinations = new LinkedHashSet<List<T>>();
		Set<List<T>> newCombinations;

		int index = 0;

		// extract each of the members in the first list
		// and add each to combinations as a new list
		for (T i : lists.get(0))
		{
			List<T> newList = new LinkedList<T>();
			newList.add(i);
			combinations.add(newList);
		}
		index++;
		while (index < lists.size())
		{
			List<T> nextList = lists.get(index);
			newCombinations = new LinkedHashSet<List<T>>();
			for (List<T> first : combinations)
			{
				for (T second : nextList)
				{
					List<T> newList = new LinkedList<T>();
					newList.addAll(first);
					newList.add(second);
					newCombinations.add(newList);
				}
			}
			combinations = newCombinations;

			index++;
		}

		return combinations;
	}


	/** Helper method */
	private LinkedHashMap<String, String> createSOAPrequestMultiple(Set<List<String>> combinationsToTest, CryptoHelper.Algorithm algorithm, EService service) throws Exception
	{
		LinkedHashMap<String, String> mapTests = new LinkedHashMap<String, String>();

		for (List<String> list : combinationsToTest)
		{
			// logger.debug(list.toString());
			String currentTest = list.toString();
			StringTokenizer tokenizer;
			tokenizer = new StringTokenizer(currentTest, ",");
			IcsXmlsecSignatureUri uri = IcsXmlsecSignatureUri.fromValue(tokenizer.nextToken().substring(1).trim());
			IcsXmlsecSignatureDigest digest = IcsXmlsecSignatureDigest.fromValue(tokenizer.nextToken().trim());
			String lastToken = tokenizer.nextToken().trim();
			IcsXmlsecSignatureCanonicalization canonicalization = IcsXmlsecSignatureCanonicalization.fromValue(lastToken.substring(0, lastToken.length() - 1));
			String request = createSOAPrequest(algorithm, uri, canonicalization, digest, service);
			mapTests.put(currentTest, (request != null) ? "OK" : "ERROR");
			if (request != null)
			{
				assertTrue(request.contains(uri.value()));
				assertTrue(request.contains(canonicalization.value()));
				assertTrue(request.contains(digest.value()));
			}
		}

		Set<Entry<String, String>> entries = mapTests.entrySet();
		for (Map.Entry<String, String> entry : entries)
		{
			String value = entry.getValue();
			if (value.equals("OK"))
				logger.debug("OK : " + entry.getKey());
			else
				logger.error("ERROR : " + entry.getKey());
		}
		return mapTests;
	}


	/** Helper method */
	private LinkedList<String> getAllSignatureDigests(boolean supportedOnly)
	{
		LinkedList<String> listDigest = new LinkedList<String>();
		listDigest.add(IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2000_09_XMLDSIG_SHA_1.value());
		listDigest.add(IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_SHA_384.value());
		if (!supportedOnly)
		{
			listDigest.add(IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_MD_5.value()); // Not supported
			listDigest.add(IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_SHA_224.value()); // Not supported
		}
		return listDigest;
	}


	/** Helper method */
	private LinkedList<String> getAllCanonicalizations(boolean supportedOnly)
	{
		LinkedList<String> listCanonicalization = new LinkedList<String>();
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_TR_2001_REC_XML_C_14_N_20010315.value());
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_TR_2001_REC_XML_C_14_N_20010315_WITH_COMMENTS.value());
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N.value());
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N_WITH_COMMENTS.value());
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2006_12_XML_C_14_N_11.value()); // No default Java constant in "javax.xml.crypto.dsig.CanonicalizationMethod" available, but supported
		listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2006_12_XML_C_14_N_11_WITH_COMMENTS.value()); // No default Java constant in "javax.xml.crypto.dsig.CanonicalizationMethod" available, but supported
		if (!supportedOnly)
		{
			listCanonicalization.add(IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2010_10_XML_C_14_N_2.value()); // "Canonical XML Version 2.0" (see "https://www.w3.org/TR/xml-c14n2/") not supported
		}
		return listCanonicalization;
	}


}
