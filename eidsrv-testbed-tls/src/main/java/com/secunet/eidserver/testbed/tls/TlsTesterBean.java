package com.secunet.eidserver.testbed.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.util.io.Streams;

import com.secunet.eidserver.testbed.common.interfaces.TlsTestParameters;
import com.secunet.eidserver.testbed.common.interfaces.beans.TlsTester;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;

@Stateless
public class TlsTesterBean implements TlsTester
{
	// private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private LogMessageDAO logMessageDAO;

	@Override
	public List<LogMessage> runTlsTest(String host, int port, TlsTestParameters parameters, byte[] outData, byte[] inData) throws UnknownHostException, IOException
	{
		List<LogMessage> result = new ArrayList<LogMessage>();
		Socket socket = connectToHost(host, port);
		runTlsTest(result, socket.getInputStream(), socket.getOutputStream(), parameters, outData, inData);
		disconnect(socket);
		return result;
	}

	void runTlsTest(List<LogMessage> result, InputStream in, OutputStream out, TlsTestParameters parameters, byte[] outData, byte[] inData)
	{
		validateParameters(parameters);
		
		// choose appropriate TLS client implementation
		TlsClient tlsClient = null;
		if(parameters.getPSK() == null)
		{
			tlsClient = new TesterTlsClient(logMessageDAO, result, parameters);
		}
		else
		{
			tlsClient = new TesterTlsPskClient(logMessageDAO, result, parameters);
		}
		
		// perform TLS protocol
		TesterTlsClientProtocol protocol = null;
		try
		{
			protocol = beginTls(in, out, tlsClient, parameters);
		}
		catch (IOException e)
		{
			LogMessage msg = logMessageDAO.createNew();
			msg.setTestStepName(TlsTesterSteps.beginTls.toString());
			msg.setSuccess(false);
			msg.setMessage("Exception occured during TLS handshake: " + e.getMessage());
			result.add(msg);
		}
		if(protocol != null)
		{
			try
			{
				transmitAndVerifyData(result, protocol, outData, inData);
			}
			catch (IOException e)
			{
				LogMessage msg = logMessageDAO.createNew();
				msg.setTestStepName(TlsTesterSteps.transmitAndVerifyData.toString());
				msg.setSuccess(false);
				msg.setMessage("Exception occured during data transmission: " + e.getMessage());
				result.add(msg);
			}
			try
			{
				endTls(protocol);
			}
			catch (IOException e)
			{
				LogMessage msg = logMessageDAO.createNew();
				msg.setTestStepName(TlsTesterSteps.endTls.toString());
				msg.setSuccess(false);
				msg.setMessage("Exception occured during TLS teardown: " + e.getMessage());
				result.add(msg);
			}
		}
	}

	private void disconnect(Socket socket) throws IOException
	{
		socket.close();
	}

	private void endTls(TesterTlsClientProtocol protocol) throws IOException
	{
		protocol.close();
	}

	private void transmitAndVerifyData(List<LogMessage> result, TesterTlsClientProtocol protocol, byte[] outData, byte[] inData) throws IOException
	{
		if (outData != null && outData.length > 0)
		{
			OutputStream output = protocol.getOutputStream();
			output.write(outData);
			output.flush();

			if (inData != null && inData.length > 0)
			{
				byte[] receivedData = new byte[inData.length];
				int count = Streams.readFully(protocol.getInputStream(), receivedData);

				LogMessage msg = logMessageDAO.createNew();
				msg.setTestStepName(TlsTesterSteps.transmitAndVerifyData.toString());
				if (inData.length == count && Arrays.equals(inData, receivedData))
				{
					msg.setSuccess(true);
					msg.setMessage("Server returned expected application data.");

				}
				else
				{
					msg.setSuccess(false);
					msg.setMessage("Expected application data " + Arrays.toString(inData) + ", but server sent " + Arrays.toString(receivedData) + ".");
				}
				result.add(msg);
			}
		}
	}

	private TesterTlsClientProtocol beginTls(InputStream in, OutputStream out, TlsClient ttc, TlsTestParameters parameters) throws IOException
	{
		TesterTlsClientProtocol protocol = new TesterTlsClientProtocol(in, out, new SecureRandom());
		try
		{
			protocol.connect(ttc);
		}
		catch(IOException e)
		{
			// if a fatal TLS error (= IOException during connect() ) is expected, the exception is skipped and no connection is returned 
			if( (parameters.getAlertLevelReceived() == 2) && (protocol != null) && protocol.isClosed() )
			{
				return null;
			}
			else
			{
				throw e;
			}
		}
		return protocol;
	}

	private Socket connectToHost(String host, int port) throws UnknownHostException, IOException
	{
		return new Socket(host, port);
	}

	private void validateParameters(TlsTestParameters parameters)
	{
		if (parameters.getCipherSuites() == null || parameters.getCipherSuites().length < 1)
		{
			throw new IllegalArgumentException("Array of CipherSuites must not be empty.");
		}
		
		//TODO more validation
	}

}
