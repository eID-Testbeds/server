package com.secunet.eidserver.testbed.tlsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.SecureRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;

public class TLSConnectionClient
{
	private static final Logger logger = LogManager.getLogger(TLSConnectionClient.class);
	private final String host;
	private final int port;
	private Socket socket;
	private TlsClientProtocol tlsClientProtocol;
	private TlsClient myTLSClient;
	private Certificate clientCert;
	private AsymmetricKeyParameter clientKey;
	private byte[] PSKIdentity, PreSharedKey;
	private final boolean usePSK;
	private final boolean clientAuth;
	private int[] supported_suites;
	private BufferedReader reader;
	private String handshakeData;

	// Initialize the class to use client certificates
	public TLSConnectionClient(String target_host, int target_port, Certificate client_cert, AsymmetricKeyParameter client_keys)
	{
		host = target_host;
		port = (target_port != -1) ? target_port : 443;
		clientCert = client_cert;
		clientKey = client_keys;
		usePSK = false;
		clientAuth = true;
		supported_suites = new int[] { CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384 };
	}

	// Initialize the class to use client certificates and PSK
	public TLSConnectionClient(String target_host, int target_port, Certificate client_cert, AsymmetricKeyParameter client_keys, byte[] PSK_ID, byte[] PSK)
	{
		host = target_host;
		port = (target_port != -1) ? target_port : 443;
		clientCert = client_cert;
		clientKey = client_keys;
		PSKIdentity = PSK_ID;
		PreSharedKey = PSK;
		usePSK = true;
		clientAuth = true;
		supported_suites = new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA };
	}

	// Initialize the class to use sever certificates only
	public TLSConnectionClient(String target_host, int target_port)
	{
		host = target_host;
		port = (target_port != -1) ? target_port : 443;
		usePSK = false;
		clientCert = null;
		clientKey = null;
		clientAuth = false;
		supported_suites = new int[] { CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 };

	}

	// Initialize the class to use server certificates and PSK
	public TLSConnectionClient(String target_host, int target_port, byte[] PSK_ID, byte[] PSK)
	{
		host = target_host;
		port = (target_port != -1) ? target_port : 443;
		PSKIdentity = PSK_ID;
		PreSharedKey = PSK;
		usePSK = true;
		clientAuth = false;
		supported_suites = new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA };
	}

	// Allows the manipulation cipher suites that can be used for the connection
	public void setCipherSuites(int[] supported_cipher_suites)
	{
		supported_suites = supported_cipher_suites;
	}

	// Establish the connection
	public int connect() throws IOException, IllegalArgumentException, IllegalAccessException
	{
		// Generate a source for random numbers
		SecureRandom random = new SecureRandom();

		// Choose the connection class depending on the usage of PSK/Client certificate
		if (usePSK)
		{
			myTLSClient = new TLSConnectionPSK(host, PSKIdentity, PreSharedKey);
		}
		else
		{
			if (clientAuth)
			{
				myTLSClient = new TLSConnection(clientCert, clientKey, supported_suites);
			}
			else
			{
				myTLSClient = new TLSConnection(supported_suites);
			}
		}
		// Open the connection
		socket = new Socket(host, port);
		logger.debug("Connection opened to " + host + " on port " + port);
		tlsClientProtocol = new TlsClientProtocol(socket.getInputStream(), socket.getOutputStream(), random);
		tlsClientProtocol.connect(myTLSClient);

		reader = new BufferedReader(new InputStreamReader(tlsClientProtocol.getInputStream()));
		logger.debug("TLS connection established to " + host);
		return 0;
	}

	// Send a String via the TLS Connection
	public int write(String msg)
	{
		try
		{
			tlsClientProtocol.getOutputStream().write(msg.getBytes("UTF-8"));
			tlsClientProtocol.getOutputStream().flush();
			logger.debug("Sucessfully sent: " + msg);
		}
		catch (UnsupportedEncodingException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Error while writing TLS Stream: Could not decode message " + msg + System.getProperty("line.separator") + trace.toString());
			return -3;
		}
		catch (IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("I/O Exception while writing to TLS Stream" + msg + System.getProperty("line.separator") + trace.toString());
			return -1;
		}
		finally
		{

		}
		return 0;
	}

	/**
	 * Read from the socket of this client; use the regular transfer mode (= chunked mode disabled)
	 * 
	 * @return
	 * @throws IOException
	 */
	public String read() throws IOException
	{
		// read the header
		StringWriter headerWriter = new StringWriter();
		String line = new String();
		int length = -1;
		boolean chunkedMode = false;
		boolean first = true;
		while (null != (line = reader.readLine()))
		{
			if (line.length() == 0)
			{
				// some server implementations seem to send an empty line before the actual http data. this must be skipped
				if (first)
				{
					continue;
				}
				else
				{
					break;
				}
			}
			first = false;
			if (line.startsWith("Content-Length: "))
			{
				String lengthString = line.substring("Content-Length:".length());
				lengthString = lengthString.replaceAll("\\s+", "");
				length = Integer.valueOf(lengthString);
			}
			if (line.startsWith("Transfer-Encoding: chunked"))
			{
				chunkedMode = true;
			}
			headerWriter.append(line).append(GeneralConstants.HTTP_LINE_ENDING);
		}
		headerWriter.append(GeneralConstants.HTTP_LINE_ENDING);

		// read the body
		String body = new String();
		if (chunkedMode)
		{
			body = readChunked();
		}
		else
		{
			body = readRegular(length);
		}
		String msg = headerWriter.toString() + body;
		logger.debug("Got response: " + msg);
		return (msg);
	}

	/**
	 * Read from the socket of this client; use the chunked transfer mode
	 * 
	 * @return
	 * @throws IOException
	 */
	private String readChunked() throws IOException
	{
		// read the first chunk
		String toReadString = reader.readLine();
		int toRead = 0;
		try
		{
			toRead = Integer.parseInt(toReadString, 16);
		}
		catch (NumberFormatException e)
		{
			logger.error("Chunked mode transfer was indicated, but the first line did not contain a length definition for the following chunk", e);
			return "";
		}
		StringWriter bodyWriter = new StringWriter();

		// process the following chunks
		while (toRead != 0)
		{
			// read the current chunk
			char[] buffer = new char[toRead];
			int read = 0;
			while (read != toRead)
			{
				read += reader.read(buffer, read, toRead - read);
			}
			bodyWriter.append(new String(buffer));

			// check if there are more chunks to process
			toReadString = reader.readLine();
			if (toReadString.isEmpty())
			{
				// note: some implementations send one empty line between chunks
				toReadString = reader.readLine();
			}
			if (toReadString == null)
			{
				break;
			}
			else
			{
				try
				{
					toRead = Integer.parseInt(toReadString, 16);
				}
				catch (NumberFormatException e)
				{
					logger.error("A chunk of the message did not contain the length of the data following it. Only the first part of the body will be returned - the message is incomplete!", e);
					return bodyWriter.toString();
				}
			}
		}

		return bodyWriter.toString();
	}

	private String readRegular(int length) throws IOException
	{
		StringWriter bodyWriter = new StringWriter();
		String line = new String();
		if (length == -1)
		{
			while (null != (line = reader.readLine()))
			{
				bodyWriter.append(line).append(GeneralConstants.HTTP_LINE_ENDING);
			}
		}
		else
		{
			char[] buffer = new char[length];
			reader.read(buffer);
			bodyWriter.append(new String(buffer));
		}
		return bodyWriter.toString();
	}

	// Close the connection
	public int close()
	{
		try
		{
			logger.debug("Closing connection");
			tlsClientProtocol.close();
			socket.close();
			logger.debug("Connection closed");
		}
		catch (IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not close connection to " + host + ": IO Exception" + System.getProperty("line.separator") + trace.toString());
			return -1;
		}
		return 0;
	}

	/**
	 * Get the TLS parameters agreed upon during the handshake
	 * 
	 * @return
	 */
	public String getHandshakeData()
	{
		return handshakeData;
	}

	// set the handshake data
	protected void setHandshakeData(String data)
	{
		this.handshakeData = data;
	}

	public boolean isClosed()
	{
		return this.tlsClientProtocol.isClosed();
	}

	/**
	 * Get the some info about the target of this connection
	 * 
	 * @return
	 */
	public String getTargetInfo()
	{
		return host + ":" + port;
	}

}
