package com.secunet.eidserver.testbed.tls;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.interfaces.DefaultTlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

public class TlsProtocolTest
{
	// TODO reenable

	@Test(enabled = false)
	public void testClientServer() throws Exception
	{
		SecureRandom secureRandom = new SecureRandom();

		PipedInputStream clientRead = new PipedInputStream();
		PipedInputStream serverRead = new PipedInputStream();
		PipedOutputStream clientWrite = new PipedOutputStream(serverRead);
		PipedOutputStream serverWrite = new PipedOutputStream(clientRead);

		TesterTlsClientProtocol clientProtocol = new TesterTlsClientProtocol(clientRead, clientWrite, secureRandom);
		TlsServerProtocol serverProtocol = new TlsServerProtocol(serverRead, serverWrite, secureRandom);

		ServerThread serverThread = new ServerThread(serverProtocol);
		serverThread.start();

		List<LogMessage> result = new ArrayList<LogMessage>();
		LogMessageDAO logMessageDAO = null;
		TesterTlsClient client = new TesterTlsClient(logMessageDAO, result, new DefaultTlsTestParameters());
		clientProtocol.connect(client);

		// NOTE: Because we write-all before we read-any, this length can't be more than the pipe capacity
		int length = 1000;

		byte[] data = new byte[length];
		secureRandom.nextBytes(data);

		OutputStream output = clientProtocol.getOutputStream();
		output.write(data);

		byte[] echo = new byte[data.length];
		int count = Streams.readFully(clientProtocol.getInputStream(), echo);

		assertEquals(count, data.length);
		assertTrue(Arrays.areEqual(data, echo));

		output.close();

		serverThread.join();
	}

	static class ServerThread extends Thread
	{
		private final TlsServerProtocol serverProtocol;

		ServerThread(TlsServerProtocol serverProtocol)
		{
			this.serverProtocol = serverProtocol;
		}

		@Override
		public void run()
		{
			try
			{
				MockTlsServer server = new MockTlsServer(new DefaultTlsTestParameters());
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
