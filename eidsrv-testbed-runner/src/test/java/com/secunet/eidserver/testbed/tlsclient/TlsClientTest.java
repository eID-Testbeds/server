//package com.secunet.eidserver.testbed.tlsclient;
//
//
//import static org.testng.AssertJUnit.assertEquals;
//import static org.testng.AssertJUnit.assertNotNull;
//
//import java.security.Security;
//import java.security.cert.X509Certificate;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.testng.annotations.Test;
//
//import com.secunet.testbedutils.bouncycertgen.BouncyCertificateGenerator;
//
//
//public class TlsClientTest{
//
//	//These tests requires a local TLS server to be accepting connections supplying a certificate that was issued by the "server.crt" certificate
//	//Example: openssl.exe s_server -accept 4433 -key ~/server.key -cert ~/server.crt
//	//If not the TLS_RUNNING variable can be used to skip the tests
//	final static boolean TLS_RUNNING = false;
//
//	@Test
//	public void testOpenConnection() throws Exception {
//		if (TLS_RUNNING){
//
//			//Get the server certificate
//			assertNotNull("Test certificate is missing: server.crt", getClass().getResource("/server.crt"));
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			X509Certificate cert = BouncyCertificateGenerator.getGenerator().readFromFileSystem(getClass().getResource("/server.crt").getFile());
//			Map<String, X509Certificate> certs = new HashMap<String, X509Certificate>();
//			certs.put("TESTCERT", cert);
//
//			//Create a TLS object 
//			TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.1", 4433,certs);
//
//			//open a connection
//			int ret = TestTLS.connect();
//			assertEquals(0, ret);
//
//			//send a test string
//			ret = TestTLS.write("Hello World\n");
//			assertEquals(0, ret);
//
//			//close the connection
//			ret =TestTLS.close();
//			assertEquals(0, ret);
//		}
//	}
//
//	@Test
//	public void testConnection_invalidHost() throws Exception {
//		//Get the server certificate
//		assertNotNull("Test certificate is missing: server.crt", getClass().getResource("/server.crt"));
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		X509Certificate cert = BouncyCertificateGenerator.getGenerator().readFromFileSystem(getClass().getResource("/server.crt").getFile());
//		Map<String, X509Certificate> certs = new HashMap<String, X509Certificate>();
//		certs.put("TESTCERT", cert);
//
//		//Create a TLS object - supply an invalid host and port
//		TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.5", 1234,certs);
//
//		//open a connection - expect a error code
//		int ret = TestTLS.connect();
//		assert ret!=0;
//	}
//
//	@Test
//	public void testConnection_invalidCert() throws Exception {
//		if (TLS_RUNNING){
//			//Get the test certificate
//			assertNotNull("Test certificate is missing: test_cert.crt", getClass().getResource("/test_cert.crt"));
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			X509Certificate cert = BouncyCertificateGenerator.getGenerator().readFromFileSystem(getClass().getResource("/test_cert.crt").getFile());
//			Map<String, X509Certificate> certs = new HashMap<String, X509Certificate>();
//			certs.put("TESTCERT", cert);
//
//			//Create a TLS object - supply an invalid certificate
//			TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.1", 4433,certs);
//
//			//open a connection - expect a error code
//			int ret = TestTLS.connect();
//			assert ret!=0;
//		}
//	}
//	
//	@Test
//	public void testConnection_noSharedCiphers() throws Exception {
//		if (TLS_RUNNING){
//			//Get the server certificate
//			assertNotNull("Test certificate is missing: server.crt", getClass().getResource("/server.crt"));
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			X509Certificate cert = BouncyCertificateGenerator.getGenerator().readFromFileSystem(getClass().getResource("/test_cert.crt").getFile());
//			Map<String, X509Certificate> certs = new HashMap<String, X509Certificate>();
//			certs.put("TESTCERT", cert);
//
//			//Create a TLS object
//			TLSConnectionClient TestTLS = new TLSConnectionClient("127.0.0.1", 4433,certs);
//			
//			//Remove all supported suites
//			TestTLS.setCipherSuites(new int[] {});
//
//			//open a connection - expect a error code
//			int ret = TestTLS.connect();
//			assert ret!=0;
//		}
//	}
//}