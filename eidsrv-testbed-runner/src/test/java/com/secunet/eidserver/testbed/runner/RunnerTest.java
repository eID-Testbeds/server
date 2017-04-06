//package com.secunet.eidserver.testbed.runner;
//
//import static org.testng.AssertJUnit.assertNotNull;
//
//import java.io.InputStream;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.testng.annotations.Test;
//
//import com.secunet.eidserver.testbed.common.interfaces.LogMessage;
//import com.secunet.eidserver.testbed.common.interfaces.TestCase;
//import com.secunet.eidserver.testbed.common.interfaces.TestCaseStep;
//import com.secunet.eidserver.testbed.common.interfaces.TestMessage;
//import com.secunet.eidserver.testbed.model.entities.TestCaseEntity;
//import com.secunet.eidserver.testbed.model.entities.TestCaseStepEntity;
//import com.secunet.eidserver.testbed.model.entities.TestMessageEntity;
//import com.secunet.testbedutils.utilities.CertificateUtil;
//
//public class RunnerTest {
//
//	// These tests requires a local TLS server to be accepting connections
//	// supplying a certificate that was issued by the "server.crt" certificate
//	// Example: openssl.exe s_server -accept 4433 -key ~/server.key -cert
//	// ~/server.crt
//	// Furthermore, the test can be performed interactive, i.e. the TLS server
//	// has to supply some message
//	final static boolean TLS_RUNNING = true;
//	final static boolean INTERACTIVE = false;
//
//	@Test
//	public void runnerPSKTestcase() throws Exception {
//		InputStream certinput = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.crt");
//		final X509Certificate cert = CertificateUtil.loadX509Certificate(certinput);
//		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
//		
//		// open a server
//     /*   Runnable serverTask = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                	X509CertificateHolder sigCert = new JcaX509CertificateHolder(cert);
//    				Certificate bcCert = new Certificate(new org.bouncycastle.asn1.x509.Certificate[] { sigCert.toASN1Structure() });
//    				InputStream keyinput = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.key");
//    				KeyPair pair = CertificateUtil.loadKeyPair(keyinput);
//    				
//                    ServerSocket serverSocket = new ServerSocket(4433);
//                    serverSocket.setSoTimeout(8000);
//                    while (true) {
//    					Socket socket = serverSocket.accept();
//    					TlsServerProtocol tlsServerProtocol = new TlsServerProtocol(socket.getInputStream(), socket.getOutputStream(), new SecureRandom());
//    				
//    					tlsServerProtocol.accept(new DefaultTlsServer(new TlsCipherFactory() {
//							
//							@Override
//							public TlsCipher createCipher(TlsContext context, int encryptionAlgorithm,
//									int macAlgorithm) throws IOException {
//								return null;
//							}
//						}){});
//    				
//    					
//
//    						
//    					
//    				}
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (CertificateEncodingException e) {
//					e.printStackTrace();
//				}
//            }
//        };
//        clientProcessingPool.execute(serverTask);
//*/
//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//		   
//        // Create the public and private keys
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
//        
//		// wait a second for the server to open
//		Thread.sleep(1000);
//		
//
//		 SecureRandom random = new SecureRandom();
//         generator.initialize(1024, random);
//
//         KeyPair pair = generator.generateKeyPair();
//         PublicKey pubKey = pair.getPublic();
//         PrivateKey privKey = pair.getPrivate();
//         
//         KeyPairGenerator generator2 = KeyPairGenerator.getInstance("RSA", "BC");
//         
//         SecureRandom random2 = new SecureRandom();
//  
//         generator2.initialize(1024, random2);
//
//         KeyPair pair2 = generator2.generateKeyPair();
//         PublicKey pubKey2 = pair2.getPublic();
//         PrivateKey privKey2 = pair2.getPrivate();
//		
//		
//		if (TLS_RUNNING) {
//			// Get the server certificate
//			assertNotNull("Test certificate is missing", getClass().getResource("/server.crt"));
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			Map<String,X509Certificate> certs = new HashMap<String,X509Certificate>();
//			certs.put("MyCert",cert);
//
//			// Generate a simple TestCase
//			TestCase TC = new TestCaseEntity();
//			List<TestCaseStep> TS_List = new ArrayList<TestCaseStep>();
//
//			// Add reading step in case an interactive test is possible
//			if (INTERACTIVE) {
//				TestCaseStep TS1 = new TestCaseStepEntity();
//				TS1.setInbound(true);
//				TS_List.add(TS1);
//			}
//
//			TestCaseStep TS2 = new TestCaseStepEntity();
//			TS2.setInbound(false);
//			TestMessage TM = new TestMessageEntity();
//			TM.setMessage("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//					+"<step xmlns=\"http://www.secunet.com\">"
//					+"<HttpStepToken><name>message</name><value>"
//					+"GET Something"
//					+"</value></HttpStepToken></step>");
//			TS2.setTestMessage(TM);
//			TS_List.add(TS2);
//			TC.setTestCaseSteps(TS_List);
//
//			//Run the test
//			Runner run = new RunnerImpl();
//			List<LogMessage> result = run.runTest(TC, "127.0.0.1", true, certs, "Client_identity", "123456", pubKey, privKey);
//
////			System.out.println("Logmessages of the TestCase");
////			System.out.println("------------------------------------------------------");
//			// Check Log Messages
//			Iterator<LogMessage> i = result.iterator();
//			while (i.hasNext()) {
//				LogMessage logMsg = i.next();
////				System.out.println(logMsg.getMessage());
////				assert logMsg.getSuccess();
//
//			}
////			System.out.println("------------------------------------------------------");
//
//		}
//	}
//}
