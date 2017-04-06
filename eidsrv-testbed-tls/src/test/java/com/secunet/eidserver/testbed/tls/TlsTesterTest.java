package com.secunet.eidserver.testbed.tls;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.TlsServer;
import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.bouncycastle.crypto.tls.TlsUtils;
import org.bouncycastle.util.io.Streams;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.interfaces.DefaultTlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;

public class TlsTesterTest
{

	@Mock
	private LogMessageDAO logMessageDAO;
	@InjectMocks
	private final TlsTesterBean tlsTesterBean = new TlsTesterBean();

	@BeforeClass
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test(enabled = false)
	public void testPublicInterface() throws UnknownHostException, IOException
	{
		String host = null;
		int port = -1;
		byte[] inData = null;
		byte[] outData = null;

		TlsTestParameters parameters = new DefaultTlsTestParameters();

		TlsTesterBean tti = new TlsTesterBean();
		List<LogMessage> result = tti.runTlsTest(host, port, parameters, inData, outData);

		for (LogMessage msg : result)
		{
			System.out.println(msg.toString());
		}

	}


	public void testInternalInterface(TlsTestParameters parameters) throws Exception
	{
		when(logMessageDAO.createNew()).thenAnswer(new Answer<LogMessage>() {

			@Override
			public LogMessage answer(InvocationOnMock invocation) throws Throwable
			{
				return new LogMessageTestingPojo();
			}

		});

		SecureRandom secureRandom = new SecureRandom();

		int length = 1000;

		byte[] data = new byte[length];
		secureRandom.nextBytes(data);

		PipedInputStream clientRead = new PipedInputStream();
		PipedInputStream serverRead = new PipedInputStream();
		PipedOutputStream clientWrite = new PipedOutputStream(serverRead);
		PipedOutputStream serverWrite = new PipedOutputStream(clientRead);

		TlsServerProtocol serverProtocol = new TlsServerProtocol(serverRead, serverWrite, secureRandom);
		TlsServer server = (parameters.getPSK() == null) ? new MockTlsServer(parameters) : new MockPSKTlsServer(parameters);

		ServerThread serverThread = new ServerThread(serverProtocol, server);
		serverThread.start();

		List<LogMessage> results = new ArrayList<LogMessage>();
		tlsTesterBean.runTlsTest(results, clientRead, clientWrite, parameters, data, data);

		serverThread.join();

		List<LogMessage> expectedResults = new ArrayList<LogMessage>();
		LogMessage msg = new LogMessageTestingPojo();
		msg.setTestStepName(TlsTesterSteps.notifyServerVersion.toString());
		msg.setSuccess(true);
		msg.setMessage("Server selected expected TLS version " + parameters.getSelectedVersion() + ".");
		expectedResults.add(msg);
		msg = new LogMessageTestingPojo();
		msg.setTestStepName(TlsTesterSteps.notifySelectedCipherSuite.toString());
		msg.setSuccess(true);
		msg.setMessage("Server selected expected cipher suite " + parameters.getSelectedCipherSuite() + ".");
		expectedResults.add(msg);
		if (parameters.getEncryptThenMACExtension())
		{
			msg = new LogMessageTestingPojo();
			msg.setTestStepName(TlsTesterSteps.processServerExtensionEncryptThenMAC.toString());
			msg.setSuccess(true);
			msg.setMessage("Server responded as expected and" + (TlsUtils.isBlockCipherSuite(parameters.getSelectedCipherSuite()) ? " " : " did not ") + "set encrypt-then-MAC extension.");
			expectedResults.add(msg);
		}
		msg = new LogMessageTestingPojo();
		msg.setTestStepName(TlsTesterSteps.transmitAndVerifyData.toString());
		msg.setSuccess(true);
		msg.setMessage("Server returned expected application data.");
		expectedResults.add(msg);

		for (int i = 0; i < results.size(); i++)
		{
			System.out.println(results.get(i).toString());
			assertEquals(results.get(i).toString(), expectedResults.get(i).toString());
		}
	}


	// EncryptThenMACExtension disabled
	@Test
	public void testInternalInterfaceEncryptThenMACExtensionDisabled() throws Exception
	{
		DefaultTlsTestParameters parameters = new DefaultTlsTestParameters();
		// EncryptThenMACExtension(Received) are both disabled by default
		testInternalInterface(parameters);
		assertEquals(false, parameters.getEncryptThenMACExtensionEnabled());
	}


	// EncryptThenMACExtension enabled, but no block cipher suite (see org.bouncycastle.crypto.tls.AbstractTlsServer.getServerExtensions())
	/*
	 * RFC 7366 3. If a server receives an encrypt-then-MAC request extension from a client
	 * and then selects a stream or Authenticated Encryption with Associated Data (AEAD)
	 * ciphersuite, it MUST NOT send an encrypt-then-MAC response extension back to the
	 * client.
	 */
	@Test
	public void testInternalInterfaceEncryptThenMACExtensionEnabledNoBlockCipher() throws Exception
	{
		DefaultTlsTestParameters parameters = new DefaultTlsTestParameters();
		parameters.setEncryptThenMACExtension(true);
		parameters.setEncryptThenMACExtensionEnabled(false);
		testInternalInterface(parameters);
	}


	// EncryptThenMACExtension enabled with block cipher suite
	@Test
	public void testInternalInterfaceEncryptThenMACExtensionEnabledBlockCipher() throws Exception
	{
		DefaultTlsTestParameters parameters = new DefaultTlsTestParameters();
		parameters.setCipherSuites(new int[] { CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256 });
		parameters.setSelectedCipherSuite(CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256);
		parameters.setEncryptThenMACExtension(true);
		parameters.setEncryptThenMACExtensionEnabled(true);
		testInternalInterface(parameters);
	}

	@Test
	public void testInternalInterfacePSK() throws Exception
	{
		byte[] pskId = "4b83e859-f84f-44db-8cdb-693bfb4a6203".getBytes();
		byte[] pskKey = Hex.decodeHex("E17F3C0ACF70502482F2835DB3432725CD8D55C7E5F17BF4D5EBB1ECBE64662CD2F69545754ED8B4509E00B92FEE74A3451DBA1C4FD09E52BFDA9D59BCF9BEEE".toCharArray());
		DefaultTlsTestParameters parameters = new DefaultTlsTestParameters();
		parameters.setCipherSuites(new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA });
		parameters.setSelectedCipherSuite(CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA);
		parameters.setPSK(pskId, pskKey);
		testInternalInterface(parameters);
	}

	@Test
	public void testInternalInterfaceClientAuthentication() throws Exception
	{
		DefaultTlsTestParameters parameters = new DefaultTlsTestParameters();
		parameters.setClientAuthentication(true);
		parameters.setClientCertificate(TlsTestUtils.loadCertificateChain(new String[] { "x509-client.pem", "x509-ca.pem" }));
		parameters.setClientPrivateKey(TlsTestUtils.loadPrivateKeyResource("x509-client-key.pem"));
		// EncryptThenMACExtension(Received) are both disabled by default
		testInternalInterface(parameters);
		assertEquals(false, parameters.getEncryptThenMACExtensionEnabled());
	}

	static class ServerThread extends Thread
	{
		private final TlsServerProtocol serverProtocol;
		private final TlsServer server;

		ServerThread(TlsServerProtocol serverProtocol, TlsServer server)
		{
			this.serverProtocol = serverProtocol;
			this.server = server;
		}

		@Override
		public void run()
		{
			try
			{
				serverProtocol.accept(server);
				Streams.pipeAll(serverProtocol.getInputStream(), serverProtocol.getOutputStream());
				serverProtocol.close();
			}
			catch (Exception e)
			{
				// throw new RuntimeException(e);
			}
		}
	}
}
