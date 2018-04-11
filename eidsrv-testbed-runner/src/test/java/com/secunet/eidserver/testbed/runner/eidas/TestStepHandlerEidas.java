package com.secunet.eidserver.testbed.runner.eidas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URL;
import java.security.Security;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.runner.KnownValues;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;

public class TestStepHandlerEidas
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

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@Test
	public void testRequestGeneration() throws Exception
	{
		KnownValues values = new KnownValues();
		StepHandlerEidas stepHandler = new StepHandlerEidas("TEST", new URL("http://some.url"), values, logMessageDAO, EService.F, EidCard.EIDCARD_1, 3, new URL("http://some.url"));
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //
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
				"</p:Step>";
		String request = stepHandler.generateMsg(message);
		assertNotNull(request);
		assertTrue(request.length() > 0);
	}
}
