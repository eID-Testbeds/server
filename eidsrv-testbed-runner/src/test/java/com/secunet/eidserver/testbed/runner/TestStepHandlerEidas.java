package com.secunet.eidserver.testbed.runner;

import com.secunet.eidserver.testbed.common.classes.EidCards;
import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.eidas.EidasAuthnRequestHandler;
import com.secunet.eidserver.testbed.common.eidas.EidasUtils;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CryptoFile;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCandidateTestingPojo;
import com.secunet.testbedutils.utilities.Base64Util;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.XMLParserException;
import net.shibboleth.shared.xml.impl.BasicParserPool;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.Streams;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.*;

@SuppressWarnings("HttpUrlsUsage")
public class TestStepHandlerEidas  extends RunnerTestBase
{
	private LogMessageDAO logMessageDAO;

	@BeforeClass
	private void initMocks() throws InitializationException, IOException {
		// mocks
		logMessageDAO = mock(LogMessageDAO.class);

		// empty POJOs
		LogMessage emptyLogMessage = new LogMessageTestingPojo();
		when(logMessageDAO.createNew()).thenReturn(emptyLogMessage);

		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
		EidasAuthnRequestHandler.Init();
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded as an HTTP redirect
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationRedirect() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH][[CREATE_SAML]] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"</p:Step>");
		assertNotNull(request);
        assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /(\\S*) HTTP");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(os, inflater);
		inflaterOutputStream.write(b64);
		inflaterOutputStream.close();
		os.close();
		request = os.toString(StandardCharsets.UTF_8);
		//System.out.println(request);
		X509Certificate cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F.toString(), testCandidate);
		InputStream is = IOUtils.toInputStream(request, StandardCharsets.UTF_8);
		AuthnRequest r = unmarshalAndValidateAuthnRequest(is, cert);
		Assert.assertNotNull(r);
		Assert.assertEquals(r.getDestination(), "http://some.url");
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded as an HTTP redirect with an added "random" attribute
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationRedirectRandomAttribute() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH][[CREATE_SAML]] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"</p:Step>", new KnownValue("ADD_RANDOM_ATTRIBUTE"));
		assertNotNull(request);
		assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /(\\S*) HTTP");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(os, inflater);
		inflaterOutputStream.write(b64);
		inflaterOutputStream.close();
		os.close();
		request = os.toString(StandardCharsets.UTF_8);
		//System.out.println(request);
		X509Certificate cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F.toString(), testCandidate);
		InputStream is = IOUtils.toInputStream(request, StandardCharsets.UTF_8);
		AuthnRequest r = unmarshalAndValidateAuthnRequest(is, cert);
		Assert.assertNotNull(r);
		Assert.assertEquals(r.getDestination(), "http://some.url");
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded as an HTTP redirect with a missing CREATE_SAML placeholder
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationRedirectWrongTestcase() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"</p:Step>");
		assertNotNull(request);
		assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /(\\S*) HTTP");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(os, inflater);
		inflaterOutputStream.write(b64);
		inflaterOutputStream.close();
		os.close();
		request = os.toString(StandardCharsets.UTF_8);
		System.out.println(request);
		assertTrue(request.isBlank());
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded as an HTTP redirect with an invalid (e.g. empty message) testcase
	 */
	@Test
	public void testRequestGenerationRedirectInvalidTestcase()
	{
		Assert.assertThrows(InvalidTestCaseDescriptionException.class, () -> getRequest(""));
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded in an HTTP body
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationNonRedirect() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"<p:ProtocolStepToken>" + //
				"<p:name>body</p:name>" + //
				"<p:value>[[CREATE_SAML]]</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:ProtocolStepToken>" + //
				"</p:Step>");
		assertNotNull(request);
		assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /\\S* HTTP[^\\n]+\\R\\R(\\S+)");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		request = new String(b64, StandardCharsets.UTF_8);
		//System.out.println(request);
		X509Certificate cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F.toString(), testCandidate);
		InputStream is = IOUtils.toInputStream(request, StandardCharsets.UTF_8);
		AuthnRequest r = unmarshalAndValidateAuthnRequest(is, cert);
		Assert.assertNotNull(r);
		Assert.assertEquals(r.getDestination(), "http://some.url");
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded in an HTTP body with a defect XML signature
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationWrongSignature() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH][[CREATE_SAML]] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"</p:Step>", new KnownValue("BREAK_XML_SIGNATURE"));
		assertNotNull(request);
		assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /(\\S*) HTTP");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(os, inflater);
		inflaterOutputStream.write(b64);
		inflaterOutputStream.close();
		os.close();
		request = os.toString(StandardCharsets.UTF_8);
		//System.out.println(request);
		X509Certificate cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F.toString(), testCandidate);
		InputStream is = IOUtils.toInputStream(request, StandardCharsets.UTF_8);
		Assert.assertThrows(SignatureException.class, () -> unmarshalAndValidateAuthnRequest(is,cert));
	}

	/**
	 * Test the generation of an eIDAS AuthnRequest encoded in an HTTP body with a missing XML signature
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testRequestGenerationNoSignature() throws Exception
	{
		String request = getRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
				"<p:Step xmlns:p=\"http://www.secunet.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //
				"xsi:schemaLocation=\"http://www.secunet.com ../xmlScheme/sn_TestcaseDefinition.xsd\" >" + //
				"<p:TargetInterface>Attached</p:TargetInterface>" + //
				"<p:HttpStepToken>" + //
				"<p:name>header</p:name>" + //
				"<p:value>GET [ATTACHED_WEBPAGE_PATH][[CREATE_SAML_NO_SIGNATURE]] HTTP/1.1" + //
				"Host: [ATTACHED_WEBPAGE_HOSTNAME]" + //
				"User-Agent: [USER_AGENT]" + //
				"Content-Type: text/xml;charset=utf-8</p:value>" + //
				"<p:isMandatory>true</p:isMandatory>" + //
				"</p:HttpStepToken>" + //
				"</p:Step>");
		assertNotNull(request);
		assertFalse(request.isEmpty());
		Pattern patternContent = Pattern.compile("GET /(\\S*) HTTP");
		Matcher matcherContent = patternContent.matcher(request);
		Assert.assertTrue(matcherContent.find());
		Assert.assertEquals(matcherContent.groupCount(), 1);
		request = URLDecoder.decode(matcherContent.group(1), StandardCharsets.UTF_8);
		byte[] b64 = Base64Util.decode(request);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Inflater inflater = new Inflater(true);
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(os, inflater);
		inflaterOutputStream.write(b64);
		inflaterOutputStream.close();
		os.close();
		request = os.toString(StandardCharsets.UTF_8);
		//System.out.println(request);
		X509Certificate cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F.toString(), testCandidate);
		InputStream is = IOUtils.toInputStream(request, StandardCharsets.UTF_8);
		Source source = new StreamSource(is);
		DOMResult domResult = new DOMResult();
		TransformerFactory.newInstance().newTransformer().transform(source, domResult);
		Document document = (Document) domResult.getNode();
		Assert.assertEquals (document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").getLength(),0);
		is.reset();
		Assert.assertThrows(() -> unmarshalAndValidateAuthnRequest(is,cert));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirect() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("PERSONAL_FirstName","ERIKA"), new KnownValue("EIDAS_ATTR_FirstName", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		// logMessages.forEach(logMessage -> assertTrue(logMessage.getSuccess())); todo: change the assertion to not validate against a static html where certs will have mismatches
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with an invalid test step name
	 */
	@Test
	public void testResponseValidationNoRedirectWrongTestcaseType()
	{
		Assert.assertThrows(() -> validateStep("IN_GARBAGE", "", new KnownValue("NONE")));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with an invalid expected Attribute content
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectWrongValue() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("PERSONAL_FirstName","MAX"), new KnownValue("EIDAS_ATTR_FirstName", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with an invalid attribute request
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectNonExistentAttribute() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("PERSONAL_Blubb","XXX"), new KnownValue("EIDAS_ATTR_Blubb", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with missing reference data for a requested attribute
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectMissingReferenceData() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("EIDAS_ATTR_FirstName", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		// logMessages.forEach(logMessage -> assertTrue(logMessage.getSuccess())); todo: change the assertion to not validate against a static html where certs will have mismatches
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with a required attribute
	 * not requested in the saml request (encoded and encrypted in the XML file)
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectMissingValue() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("EIDAS_ATTR_CurrentAddress", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body with an "allowed" value
	 * (which by the means of the testbed means, it should be denied for the test to succeed)
	 * which is present nonetheless
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectNotAllowedValue() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponse.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("PERSONAL_FirstName","ERIKA"), new KnownValue("EIDAS_ATTR_FirstName", "ALLOWED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an expected eIDAS Error Response encoded in an HTTP body
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectErrorExpected() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseError.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue(GeneralConstants.EIDAS_ERROR_SAMLSTATUS,"urn:oasis:names:tc:SAML:2.0:status:Responder"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		// logMessages.forEach(logMessage -> assertTrue(logMessage.getSuccess())); todo: change the assertion to not validate against a static html where certs will have mismatches
	}

	/**
	 * Test the validation of an expected eIDAS Error Response encoded in an HTTP body, while there is
	 * another error received
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectErrorExpectedWrong() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseError.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue(GeneralConstants.EIDAS_ERROR_SAMLSTATUS,"urn:oasis:names:tc:SAML:2.0:status:Sender"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded in an HTTP body, where there is an error returned instead
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationNoRedirectErrorUnexpected() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseError.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE_POST.toString(), message, new KnownValue("PERSONAL_FirstName","MAX"), new KnownValue("EIDAS_ATTR_FirstName", "REQUIRED"));
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded as an HTTP redirect
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationRedirect() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseRedirect.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE.toString(), message);
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		// logMessages.forEach(logMessage -> assertTrue(logMessage.getSuccess())); todo: change the assertion to not validate against a static html where certs will have mismatches
	}

	/**
	 * Test the validation of an eIDAS Response encoded as an HTTP redirect, where there is
	 * invalid content in the XML structure after base64 decoding and inflate
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationRedirectDefective() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseRedirectDefective.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE.toString(), message);
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Test the validation of an eIDAS Response encoded as an HTTP redirect, where there is
	 * invalid content in the deflate/inflate binary data after base64 decoding
	 *
	 * @throws Exception If there was an error during the test
	 */
	@Test
	public void testResponseValidationRedirectInflateBroken() throws Exception
	{
		String message;
		try (InputStream eidasResponseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("eidasResponseRedirectInflateBroken.html")) {
            assert eidasResponseInputStream != null; message = new String(Streams.readAll(eidasResponseInputStream), StandardCharsets.UTF_8);
		}
		assertNotNull(message);
		List<LogMessage> logMessages = validateStep(TestStepType.IN_EIDAS_RESPONSE.toString(), message);
		assertNotNull(logMessages);
		assertFalse(logMessages.isEmpty());
		Assert.assertTrue(logMessages.stream().anyMatch(logMessage -> !logMessage.getSuccess()));
	}

	/**
	 * Prepare and call the function under test for generating an eIDAS AuthnRequest
	 *
	 * @param message Message content, XML
	 * @param knownValues List of {@link KnownValue} to use for request generation
	 * @return Base64 representation of generated AuthnRequest
	 */
	private String getRequest(String message, KnownValue ... knownValues) {
		try {
			KnownValues values = new KnownValues();
			values.addAll(List.of(knownValues));
			TestCandidate candidate = new TestCandidateTestingPojo();
			candidate.setCandidateName("TEST");
			candidate.setEcardapiUrl(new URL("http://some.url"));
			candidate.setAttachedTcTokenUrl(new URL("http://some.url"));
			candidate.setCryptoFiles(cryptoFiles);
			StepHandlerEidas stepHandler = new StepHandlerEidas(candidate, values, logMessageDAO, EService.F, EidCard.EIDCARD_1, 3);
			return stepHandler.generateMsg(message);
		} catch (MalformedURLException u) {
			throw new InternalError(u); // This should really never happen. The URL is hardcoded...
		}
	}

	/**
	 * Prepare and call the function under test for validating an eIDAS Response
	 *
	 * @param type TestStepType as String
	 * @param message Content of Response to parse
	 * @param knownValues List of {@link KnownValue} to use for request generation
	 * @return List of {@link LogMessage}. Normally only one item in this case.
	 */
	private List<LogMessage> validateStep(String type, String message, KnownValue ... knownValues) {
		try {
			KnownValues values = new KnownValues();
			values.addAll(List.of(knownValues));
			TestCaseStep step = getStep(type, message);
			TestCandidate candidate = new TestCandidateTestingPojo();
			candidate.setCandidateName("TEST");
			candidate.setEcardapiUrl(new URL("http://some.url"));
			candidate.setAttachedTcTokenUrl(new URL("http://some.url"));
			StepHandlerEidas stepHandler = new StepHandlerEidas(candidate, values, logMessageDAO, EService.F, EidCard.EIDCARD_1, 3);
			return stepHandler.validateStep(message, List.of(step));
		} catch (MalformedURLException u) {
			throw new InternalError(u); // This should really never happen. The URL is hardcoded...
		}
	}

	/**
	 * Helper function to generate a TestCaseStep with custom name and message
	 *
	 * @param name name of test step
	 * @param message message encoded in test step
	 * @return TestCaseStep mock
	 */
	private static TestCaseStep getStep(String name, String message) {
		return new TestCaseStep() {
			@Override
			public String getName() {return name;}

			@Override
			public void setName(String name) {}

			@Override
			public boolean getInbound() {return true;}

			@Override
			public void setInbound(boolean inbound) {}

			@Override
			public boolean isDefault() {return true;}

			@Override
			public void setDefault(boolean isDefault) {}

			@Override
			public String getMessage() {return message;}

			@Override
			public void setMessage(String message) {}

			@Override
			public boolean isOptional() {return false;}

			@Override
			public void setOptional(boolean optional) {}

			@Override
			public void setSuffix(String newSuffix) {}

			@Override
			public String getSuffix() {return null;}

			@Override
			public String getXsd() {return null;}

			@Override
			public void setXsd(String xsd) {}

			@Override
			public TargetInterfaceType getTarget() {return null;}

			@Override
			public void setTarget(TargetInterfaceType target) {}
		};
	}

	/**
	 * Helper function which unmarshalls an AuthnRequest and validates its signature
	 *
	 * @param is {@link InputStream} of the binary AuthnRequest
	 * @param cert {@link X509Certificate} to validate the AuthnRequest against
	 * @return unmarshalled AuthnRequest
	 * @throws ComponentInitializationException Means an internal error with a crucial helper class. Should never happen.
	 * @throws XMLParserException If there is a problem parsing the XML of the AuthnRequest
	 * @throws UnmarshallingException If either no unmarshaller could be created or the data unmarshalling produces an error
	 * @throws SecurityException If there is an internal error regarding the chain of trust or the certificate itself
	 * @throws SignatureException If the signature could not be validated
	 */
	private static AuthnRequest unmarshalAndValidateAuthnRequest(InputStream is, X509Certificate cert)
			throws ComponentInitializationException, XMLParserException, UnmarshallingException, SecurityException, SignatureException {
		BasicParserPool ppMgr = EidasUtils.getBasicParserPool();
		Document inCommonMDDoc = ppMgr.parse(is);
		Element metadataRoot = inCommonMDDoc.getDocumentElement();
		// Get appropriate unmarshaller
		UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
		if (unmarshaller != null) {
			AuthnRequest r = (AuthnRequest) unmarshaller.unmarshall(metadataRoot);
			EidasAuthnRequestHandler.validate(r, cert);
			return r;
		} else {
			throw new UnmarshallingException("Could not create unmarshaller for AuthnRequest");
		}
	}
}
