package com.secunet.eidserver.testbed.tlsclient;


import static org.testng.AssertJUnit.assertEquals;

import java.security.Security;

import org.testng.annotations.Test;

public class TlsClientTest{

	//These tests requires a local TLS server to be accepting connections supplying a certificate that was issued by the "server.crt" certificate
	//Example: openssl.exe s_server -accept 4433 -key ~/server.key -cert ~/server.crt
	//If not the TLS_RUNNING variable can be used to skip the tests
	final static boolean TLS_RUNNING = false;

	@Test
	public void testOpenConnection() throws Exception {
		if (TLS_RUNNING){
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			//Create a TLS object
			TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.1", 4433);

			//open a connection
			int ret = TestTLS.connect();
			assertEquals(0, ret);

			//send a test string
			ret = TestTLS.write("Hello World\n");
			assertEquals(0, ret);

			//close the connection
			ret =TestTLS.close();
			assertEquals(0, ret);
		}
	}

	@Test
	public void testConnection_noSharedCiphers() throws Exception {
		if (TLS_RUNNING){
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			//Create a TLS object
			TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.1", 4433);

			//Remove all supported suites
			TestTLS.setCipherSuites(new int[] {});

			//open a connection - expect a error code
			int ret = TestTLS.connect();
			assert ret!=0;
		}
	}
}