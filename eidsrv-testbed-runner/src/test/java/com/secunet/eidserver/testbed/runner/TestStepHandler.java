package com.secunet.eidserver.testbed.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.ObjectFactory;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCaseStepTestingPojo;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class TestStepHandler
{
	private LogMessageDAO logMessageDAO;

	@BeforeClass
	private void initMocks()
	{
		// mocks
		logMessageDAO = mock(LogMessageDAO.class);

		// empty POJOs
		LogMessage emptyLogMessage = new LogMessageTestingPojo();
		when(logMessageDAO.createNew()).thenReturn(emptyLogMessage);
	}

	@Test
	public void validatePositive() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Positive");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), true);
	}

	@Test
	public void validateWrongHttpHeader() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		/*
		 * CHANGE IN THIS LINE, ARG Content-Type
		 */
		incomingMessageSample += "Content-Type: unknown";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Wrong HTTP header");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), false);
	}

	@Test
	public void validateMissingHttpHeader() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		/*
		 * CHANGE IN THIS LINE, ARG charset
		 */
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Missing HTTP header");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), false);
	}

	@Test
	public void validateWrongProtocolMessage() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		/*
		 * CHANGE IN THIS LINE, ARG RefreshAddress
		 */
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://wrong.de",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Wrong protocol message");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), false);
	}

	@Test
	public void validateMissingProcotolMessage() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		/*
		 * CHANGE IN THIS LINE, ARG RefreshAddress
		 */
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Missing protocol message");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), false);
	}

	@Test
	public void validateWrongOptionalProtocolMessage() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		incomingMessageSample += "<CommunicationErrorAddress>https://eservice-idp-test.secunet.de:443/communicationerror</CommunicationErrorAddress>\r\n";
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		/*
		 * CHANGE IN THIS LINE, ARG CommunicationErrorAddress
		 */
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh", new URL("https://wrong.de"),
				null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Wrong optional protocol message");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), true);
	}

	@Test(enabled = false)
	public void validateMissingOptionalProtocolMessage() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<TCTokenType>\r\n";
		incomingMessageSample += "<ServerAddress>https://eidserver-idp-test.secunet.de:8443/test</ServerAddress>\r\n";
		incomingMessageSample += "<SessionIdentifier>D90D37779F3149FEA0C02D10A495EC0D</SessionIdentifier>\r\n";
		incomingMessageSample += "<RefreshAddress>https://eservice-idp-test.secunet.de:443/refresh</RefreshAddress>\r\n";
		/*
		 * CHANGE IN THIS LINE, ARG CommunicationErrorAddress
		 */
		incomingMessageSample += "<Binding>urn:liberty:paos:2006-08</Binding>\r\n";
		incomingMessageSample += "<PathSecurity-Protocol>urn:ietf:rfc:4279</PathSecurity-Protocol>\r\n";
		incomingMessageSample += "<PathSecurity-Parameters>\r\n";
		incomingMessageSample += "<PSK>6D96CA084F7BC5D27C580DD8A6EC24FC2F049A735A0C9E0BCBBB54B7F0464133D7257ADF87</PSK>\r\n";
		incomingMessageSample += "</PathSecurity-Parameters>\r\n";
		incomingMessageSample += "</TCTokenType>\r\n";
		// create the object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step step = factory.createStep();
		// xsd
		step.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		step.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		step.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		step.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		step.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		step.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		step.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		step.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		step.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		/*
		 * CHANGE IN THIS LINE, ARG CommunicationErrorAddress
		 */
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh", new URL("https://wrong.de"),
				null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);
		// validate the data
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcStep = new TestCaseStepTestingPojo();
		tcStep.setName("Missing optional protocol message");
		tcStep.setInbound(true);
		tcStep.setDefault(true);
		tcStep.setOptional(true);
		tcStep.setMessage(JaxBUtil.marshallWithoutReplacing(step));
		steps.add(tcStep);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(0).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(0).getSuccess(), true);
	}


	// the following test validates the possibility to use different verification paths. for some of the received messages, the testbed is able to process different alternatives
	@Test(enabled = false)
	public void validateOptionalMessage() throws Exception
	{
		// create the test message
		String incomingMessageSample = "HTTP/1.1 200 OK";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-language: de-DE,en,*";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "host: https://eidserver-idp-test.secunet.de:8443";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "Content-Type: text/xml";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "charset: utf-8";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "connection: Keep-Alive";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "accept-encoding: gzip, deflate";
		incomingMessageSample += "\r\n";
		incomingMessageSample += "user-agent: " + GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "."
				+ GeneralConstants.USER_AGENT_SUBMINOR;
		incomingMessageSample += "\r\n\r\n";
		incomingMessageSample += "<ns1:Envelope xmlns:ns2=\"urn:liberty:paos:2003-08\"\r\n";
		incomingMessageSample += "xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n";
		incomingMessageSample += "xmlns:ns3=\"urn:liberty:paos:2006-08\"\r\n";
		incomingMessageSample += "xmlns:ns5=\"http://www.w3.org/2005/03/addressing\">\r\n";
		incomingMessageSample += "<ns1:Header>\r\n";
		incomingMessageSample += "<ns5:MessageID>urn:uuidC978A04207384B79B71FDB8C9CF36457</ns5:MessageID>\r\n";
		incomingMessageSample += "<ns5:ReplyTo>\r\n";
		incomingMessageSample += "<ns5:Address>http://www.projectliberty.org/2006/02/role/paos</ns5:Address>\r\n";
		incomingMessageSample += "</ns5:ReplyTo>\r\n";
		incomingMessageSample += "<ns5:Action>http://www.bsi.bund.de/ecard/api/1.0/PAOS/GetNextCommand</ns5:Action>\r\n";
		incomingMessageSample += "</ns1:Header>\r\n";
		incomingMessageSample += "<ns1:Body>\r\n";
		incomingMessageSample += "<InitializeFramework:InitializeFramework\r\n";
		incomingMessageSample += "xsi:type=\"iso:RequestType\" xmlns=\"http://www.bsi.bund.de/ecard/api/1.1\"\r\n";
		incomingMessageSample += "xmlns:tsl2=\"http://uri.etsi.org/02231/v2.1.1#\" xmlns:ecdsa=\"http://www.w3.org/2001/04/xmldsig-more#\"\r\n";
		incomingMessageSample += "xmlns:olsc=\"http://www.openlimit.com/ecard/api/ext/acbc\" xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\"\r\n";
		incomingMessageSample += "xmlns:iso=\"urn:iso:std:iso-iec:24727:tech:schema\" xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\"\r\n";
		incomingMessageSample += "xmlns:vr=\"urn:oasis:names:tc:dss-x:1.0:profiles:verificationreport:schema#\"\r\n";
		incomingMessageSample += "xmlns:InitializeFramework=\"http://www.bsi.bund.de/ecard/api/1.1\"\r\n";
		incomingMessageSample += "xmlns:dss=\"urn:oasis:names:tc:dss:1.0:core:schema\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\r\n";
		incomingMessageSample += "xmlns:dsse=\"urn:oasis:names:tc:dss-x:1.0:profiles:encryption:schema#\"\r\n";
		incomingMessageSample += "xmlns:ec=\"http://www.bsi.bund.de/ecard/api/1.1\" xmlns:tsl=\"http://uri.etsi.org/02231/v3.1.2#\"\r\n";
		incomingMessageSample += "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n";
		incomingMessageSample += "xmlns:XAdES=\"http://uri.etsi.org/01903/v1.3.2#\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\r\n";
		incomingMessageSample += "xmlns:dssades=\"urn:oasis:names:tc:dss:1.0:profiles:AdES:schema#\"\r\n";
		incomingMessageSample += "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\r\n";
		incomingMessageSample += "xmlns:dssx=\"urn:oasis:names:tc:dss-x:1.0:profiles:SignaturePolicy:schema#\"\r\n";
		incomingMessageSample += "xmlns:ers=\"http://www.setcce.org/schemas/ers\" xmlns:tslg=\"http://uri.etsi.org/02231/v2.x#\"\r\n";
		incomingMessageSample += "xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" />\r\n";
		incomingMessageSample += "</ns1:Body>\r\n";
		incomingMessageSample += "</ns1:Envelope>\r\n";
		// create the wrong object to validate against
		ObjectFactory factory = new ObjectFactory();
		Step wrongStep = factory.createStep();
		// xsd
		wrongStep.setSchema("bsi_TCToken.xml");
		// http content type
		StepToken contentToken = factory.createStepToken();
		contentToken.setName("Content-Type");
		contentToken.setValue("text/xml");
		contentToken.setIsMandatory(true);
		wrongStep.getHttpStepToken().add(contentToken);
		// http charset
		StepToken charsetToken = factory.createStepToken();
		charsetToken.setName("charset");
		charsetToken.setValue("utf-8");
		charsetToken.setIsMandatory(true);
		wrongStep.getHttpStepToken().add(charsetToken);
		// type name
		StepToken typeToken = factory.createStepToken();
		typeToken.setIsMandatory(true);
		typeToken.setName("TCTokenType");
		wrongStep.getProtocolStepToken().add(typeToken);
		// server address
		StepToken serverAddressToken = factory.createStepToken();
		serverAddressToken.setIsMandatory(true);
		serverAddressToken.setName("ServerAddress");
		serverAddressToken.setValue("[CANDIDATE_URL]");
		wrongStep.getProtocolStepToken().add(serverAddressToken);
		// refresh url
		StepToken refreshAddressToken = factory.createStepToken();
		refreshAddressToken.setIsMandatory(true);
		refreshAddressToken.setName("RefreshAddress");
		refreshAddressToken.setValue("[TESTBED_REFRESH_ADDRESS]");
		wrongStep.getProtocolStepToken().add(refreshAddressToken);
		// communication error url
		StepToken communicationErrorAddressToken = factory.createStepToken();
		communicationErrorAddressToken.setIsMandatory(false);
		communicationErrorAddressToken.setName("CommunicationErrorAddress");
		communicationErrorAddressToken.setValue("[COMMUNICATION_ERROR_ADDRESS]");
		wrongStep.getProtocolStepToken().add(communicationErrorAddressToken);
		// binding
		StepToken bindingToken = factory.createStepToken();
		bindingToken.setIsMandatory(true);
		bindingToken.setName("Binding");
		bindingToken.setValue("urn:liberty:paos:2006-08");
		wrongStep.getProtocolStepToken().add(bindingToken);
		// pathsecurity-protocol
		StepToken pathSecProtocolToken = factory.createStepToken();
		pathSecProtocolToken.setIsMandatory(true);
		pathSecProtocolToken.setName("PathSecurity-Protocol");
		pathSecProtocolToken.setValue("urn:ietf:rfc:4279");
		wrongStep.getProtocolStepToken().add(pathSecProtocolToken);
		// create a handler
		KnownValues values = new KnownValues();
		StepHandler stepHandler = new StepHandlerSAML(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), null, values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);

		// now create the correct step
		Step correctStep = factory.createStep();
		// http content type
		StepToken contentToken2 = factory.createStepToken();
		contentToken2.setName("Content-Type");
		contentToken2.setValue("text/xml");
		contentToken2.setIsMandatory(true);
		correctStep.getHttpStepToken().add(contentToken2);
		// http charset
		StepToken charsetToken2 = factory.createStepToken();
		charsetToken2.setName("charset");
		charsetToken2.setValue("utf-8");
		charsetToken2.setIsMandatory(true);
		correctStep.getHttpStepToken().add(charsetToken2);
		// type name
		StepToken typeToken2 = factory.createStepToken();
		typeToken2.setIsMandatory(true);
		typeToken2.setName("InitializeFramework");
		correctStep.getProtocolStepToken().add(typeToken2);

		// add the steps in the correct order, such that the message will first be validated as TCToken, then as InitializeFramework
		List<TestCaseStep> steps = new ArrayList<TestCaseStep>();
		TestCaseStep tcWrong = new TestCaseStepTestingPojo();
		tcWrong.setName("Wrong step");
		tcWrong.setInbound(true);
		tcWrong.setDefault(true);
		tcWrong.setOptional(true);
		tcWrong.setMessage(JaxBUtil.marshallWithoutReplacing(wrongStep));
		steps.add(tcWrong);
		TestCaseStep tcCorrect = new TestCaseStepTestingPojo();
		tcCorrect.setName("Correct step");
		tcCorrect.setInbound(true);
		tcCorrect.setDefault(true);
		tcCorrect.setOptional(true);
		tcCorrect.setMessage(JaxBUtil.marshallWithoutReplacing(correctStep));
		steps.add(tcCorrect);
		List<LogMessage> result = stepHandler.validateStep(incomingMessageSample, steps);
		Assert.assertEquals(stepHandler.getAbortState(), false);
		Assert.assertNotNull(result);
		Assert.assertEquals((result.get(1).getMessage().length() > 0), true);
		Assert.assertEquals(result.get(1).getSuccess(), true);
		Assert.assertEquals(stepHandler.getStepsToSkip(), 1);
	}

	@Test(enabled = true)
	public void testPositiveApduValidation() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.FamilyNames.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.IssuingState.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandler handler = new StepHandlerSOAP(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);

		// init the computationHelper
		ComputationHelper computationHelper = mock(ComputationHelper.class);
		Set<RequestAttribute> mockAttributes = new HashSet<>();
		mockAttributes.add(RequestAttribute.FamilyNames);
		mockAttributes.add(RequestAttribute.IssuingState);
		Set<SpecialFunction> mockFunctions = new HashSet<>();
		mockFunctions.add(SpecialFunction.AgeVerification);
		when(computationHelper.getReceivedAttribues()).thenReturn(mockAttributes);
		when(computationHelper.getReceivedFunctions()).thenReturn(mockFunctions);
		handler.setComputationHelper(computationHelper);

		Result res = handler.checkReceivedApdus();
		Assert.assertEquals(res.wasSuccessful(), true);
		// nothing to report, string has to be empty
		Assert.assertTrue(res.getMessage().length() == 0);
	}

	@Test(enabled = true)
	public void testApduValidationMissingElements() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.FamilyNames.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.IssuingState.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.DocumentType.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + SpecialFunction.PlaceVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandler handler = new StepHandlerSOAP(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);

		// init the computationHelper
		ComputationHelper computationHelper = mock(ComputationHelper.class);
		Set<RequestAttribute> mockAttributes = new HashSet<>();
		mockAttributes.add(RequestAttribute.FamilyNames);
		mockAttributes.add(RequestAttribute.IssuingState);
		Set<SpecialFunction> mockFunctions = new HashSet<>();
		mockFunctions.add(SpecialFunction.AgeVerification);
		when(computationHelper.getReceivedAttribues()).thenReturn(mockAttributes);
		when(computationHelper.getReceivedFunctions()).thenReturn(mockFunctions);
		handler.setComputationHelper(computationHelper);

		Result res = handler.checkReceivedApdus();
		Assert.assertEquals(res.wasSuccessful(), false);
		Assert.assertEquals(res.getMessage(),
				"The function PlaceVerification was requested by the user, but the server did not send a request for it to the card." + System.getProperty("line.separator")
						+ "The attribute DocumentType was requested by the user, but the server did not send a request for it to the card." + System.getProperty("line.separator"));
	}

	@Test(enabled = true)
	public void testApduValidationTooManyElements() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.FamilyNames.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.IssuingState.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandler handler = new StepHandlerSOAP(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);


		// init the computationHelper
		ComputationHelper evilHelper = mock(ComputationHelper.class);
		Set<RequestAttribute> mockAttributes = new HashSet<>();
		mockAttributes.add(RequestAttribute.FamilyNames);
		mockAttributes.add(RequestAttribute.IssuingState);
		mockAttributes.add(RequestAttribute.DocumentType);
		Set<SpecialFunction> mockFunctions = new HashSet<>();
		mockFunctions.add(SpecialFunction.AgeVerification);
		mockFunctions.add(SpecialFunction.PlaceVerification);
		when(evilHelper.getReceivedAttribues()).thenReturn(mockAttributes);
		when(evilHelper.getReceivedFunctions()).thenReturn(mockFunctions);

		handler.setComputationHelper(evilHelper);
		Result res = handler.checkReceivedApdus();
		Assert.assertEquals(res.wasSuccessful(), false);
		Assert.assertEquals(res.getMessage(), "The server requested the additional attributes: DocumentType." + System.getProperty("line.separator")
				+ "The server requested the additional functions: PlaceVerification." + System.getProperty("line.separator"));
	}

	@Test(enabled = true)
	public void validateCvSaving() throws Exception
	{
		KnownValues values = new KnownValues();
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.FamilyNames.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + RequestAttribute.IssuingState.toString(), GeneralConstants.PERMISSION_ALLOWED));
		values.add(new KnownValue(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString() + SpecialFunction.AgeVerification.toString(), GeneralConstants.PERMISSION_ALLOWED));

		List<String> certificateNames = new ArrayList<>();
		certificateNames.add("CERT_EID_XMLSIG_ESERVICE_1_A");
		StepHandler handler = new StepHandlerSOAP(null, new URL("https://eidserver-idp-test.secunet.de:8443/test"), "https://eservice-idp-test.secunet.de:443/refresh",
				new URL("https://eservice-idp-test.secunet.de:443/communicationerror"), values, logMessageDAO, EService.A, EidCard.EIDCARD_1, 0);

		String eac1input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\"\r\n";
		eac1input += "<soap:Header>\r\n";
		eac1input += "<sb:Correlation xmlns:sb=\"http://urn:liberty:sb:2003-08\" messageID=\"Id34915\" refToMessageID=\"1f5929cb-fd30-430d-8c96-37d7ad636137\"/>\r\n";
		eac1input += "<RelatesTo xmlns=\"http://www.w3.org/2005/03/addressing\">urn:uuid:2d735155-0efa-44b0-9ab0-754e124e7aea</RelatesTo>\r\n";
		eac1input += "<MessageID xmlns=\"http://www.w3.org/2005/03/addressing\">urn:uuid:352ed2b3-0cb4-4fa0-bc14-4de2d47bfb2e</MessageID>\r\n";
		eac1input += "</soap:Header>\r\n";
		eac1input += "<soap:Body>\r\n";
		eac1input += "<ns2:DIDAuthenticate xmlns:ns2=\"urn:iso:std:iso-iec:24727:tech:schema\" xmlns:ns3=\"urn:oasis:names:tc:dss:1.0:core:schema\" xmlns:ns4=\"http://www.w3.org/2000/09/xmldsig#\">\r\n";
		eac1input += "<ns2:ConnectionHandle>\r\n";
		eac1input += "<ns2:CardApplication>E80704007F00070302</ns2:CardApplication>\r\n";
		eac1input += "<ns2:SlotHandle>36333334</ns2:SlotHandle>\r\n";
		eac1input += "</ns2:ConnectionHandle>\r\n";
		eac1input += "<ns2:DIDName>PIN</ns2:DIDName>\r\n";
		eac1input += "<ns2:AuthenticationProtocolData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Protocol=\"urn:oid:1.3.162.15480.3.0.14.2\" xsi:type=\"ns2:EAC1InputType\">\r\n";
		eac1input += "<ns2:Certificate xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f218201467f4e81ff5f29010042104445445674494447564e4b30303031327f494f060a04007f00070202020203864104290590b5e2a394101d9daafaf7f74a555f33b20de8314dba0f2f51f294f3c9b128f5afddc0becc9f574b763037b2fa3ad4ce073294c4071981a85be45d12bd765f200d444553454354535430303031377f4c12060904007f0007030102025305000513ff875f25060106000700095f2406010600080008655e732d060904007f00070301030180203887d15f2fed0da95f310e433df43b27acb6dbe2cfcbb7b822c7dc088ed2b18a732d060904007f0007030103028020f1fbb30ae3a9bf2b34054b5e1c9a3f9d07ab32fb2f8f959a77953c332fb5d5b35f3740496b951e6f431a517963a0c712ad7a5efe85178eb57ab4246f066c9a95dc3f7e67a3f86b1031130e427dec2670e7ebc9b4acadb6e4b32cc35650630853c0dcb3</ns2:Certificate>\r\n";
		eac1input += "<ns2:Certificate xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f218201b67f4e82016e5f290100420e44455445535465494430303030317f4982011d060a04007f000702020202038120a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e537782207d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9832026dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b68441048bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f0469978520a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a7864104096eb58bfd86252238ec2652185c43c3a56c320681a21e37a8e69ddc387c0c5f5513856efe2fdc656e604893212e29449b365e304605ac5413e75be31e641f128701015f200e44455445535465494430303030327f4c12060904007f0007030102025305fe0f01ffff5f25060100000902015f24060103000902015f3740141120a0fdfc011a52f3f72b387a3dc7aca88b4868d5ae9741780b6ff8a0b49e5f55169a2d298ef5cf95935dca0c3df3e9d42dc45f74f2066317154961e6c746</ns2:Certificate>\r\n";
		eac1input += "<ns2:Certificate xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f218201b67f4e82016e5f290100420e44455445535465494430303030327f4982011d060a04007f000702020202038120a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e537782207d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9832026dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b68441048bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f0469978520a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a786410474ff63ab838c73c303ac003dfee95cf8bf55f91e8febcb7395d942036e47cf1845ec786ec95bb453aac288ad023b6067913cf9b63f908f49304e5cfc8b3050dd8701015f200e44455445535465494430303030347f4c12060904007f0007030102025305fc0f13ffff5f25060102000501015f24060105000501015f37405c035a0611b6c58f0b5261fdd009decab7dc7a79482d5248cca119059b7d82b2157cf0c4a499bcf441efdd35e294a58c0af19a34a0762159533285acf170a505</ns2:Certificate>\r\n";
		eac1input += "<ns2:Certificate xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f218201b67f4e82016e5f290100420e44455445535465494430303030347f4982011d060a04007f000702020202038120a9fb57dba1eea9bc3e660a909d838d726e3bf623d52620282013481d1f6e537782207d5a0975fc2c3057eef67530417affe7fb8055c126dc5c6ce94a4b44f330b5d9832026dc5c6ce94a4b44f330b5d9bbd77cbf958416295cf7e1ce6bccdc18ff8c07b68441048bd2aeb9cb7e57cb2c4b482ffc81b7afb9de27e1e3bd23c23a4453bd9ace3262547ef835c3dac4fd97f8461a14611dc9c27745132ded8e545c1d54c72f0469978520a9fb57dba1eea9bc3e660a909d838d718c397aa3b561a6f7901e0e82974856a78641049bfeba8dc7faab6e3bdeb3ff794dbb800848fe4f6940a4cc7eecb5159c87da5395505892026d420a22596cd014ed1fd872dada597db0f8d64441041198f62d448701015f200e44455445535465494430303030357f4c12060904007f0007030102025305fc0f13ffff5f25060105000500045f24060108000500045f37402d2468416d66bcbe259b9b907a73395bc1ef94ed75f9c17615210246e9efb06e6753e9055ce76623b7699b9efb1a7d3a9dd83f6e6e09e55a33ea0a5f62a1c719</ns2:Certificate>\r\n";
		eac1input += "<ns2:Certificate xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f2181e77f4e81a05f290100420e44455445535465494430303030357f494f060a04007f000702020202038641045a2af2be146c184ddb05335ed662ce54f714af1193a56ef16e7bc1e6acb968dc28ac040930e4550abcf7c5c478324b2ba9ee32f39aa8380a6397c25a4cc291075f20104445445674494447564e4b30303031327f4c12060904007f0007030102025305400513ff875f25060106000501085f24060106000801065f374035c04a634ee73e57e9990c3241bfcfd255b14772abdcb78e614b26257df77f196696f2ad62f1c3fca9cb7a30579397d03cbf9fd596119e72f4d88980f2944063</ns2:Certificate>\r\n";
		eac1input += "<ns2:CertificateDescription xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">30820107060a04007f00070301030101a1160c14476f7665726e696b757320546573742044564341a21a1318687474703a2f2f7777772e676f7665726e696b75732e6465a31e0c1c736563756e6574205365637572697479204e6574776f726b73204147a41e131c68747470733a2f2f652d736572766963652e736563756e65742e6465a5150c13536563756e657420546573746d616e64616e74a626132468747470733a2f2f652d736572766963652e736563756e65742e64652f72656672657368a7463144042071b89f97689425ccad573f4754d74baafdbb95980d9135605eed4bf29781674c04205c6fcac6857d69b469b8e8e523b656338bdda1ac43dea739e0510c862901d06d</ns2:CertificateDescription>\r\n";
		eac1input += "<ns2:RequiredCHAT xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f4c12060904007f00070301020253050000000000</ns2:RequiredCHAT>\r\n";
		eac1input += "<ns2:OptionalCHAT xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">7f4c12060904007f00070301020253050000001800</ns2:OptionalCHAT>\r\n";
		eac1input += "<ns2:AuthenticatedAuxiliaryData xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\">67177315060904007f00070301040253083230313630373133</ns2:AuthenticatedAuxiliaryData>\r\n";
		eac1input += "<ns2:TransactionInfo xmlns=\"urn:iso:std:iso-iec:24727:tech:schema\"/>\r\n";
		eac1input += "</ns2:AuthenticationProtocolData>\r\n";
		eac1input += "</ns2:DIDAuthenticate>\r\n";
		eac1input += "</soap:Body>\r\n";
		eac1input += "</soap:Envelope>\r\n";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(eac1input)));
		handler.saveCVcertificates(document);
		Map<String, CVCertificate> cvCerts = handler.getKnownCvCertificates();
		Assert.assertEquals(cvCerts.size(), 5);
	}

}
