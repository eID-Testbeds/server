package com.secunet.eidserver.testbed.controller;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Interface;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.testing.DefaultTestCaseTestingPojo;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCandidateTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCaseStepTestingPojo;
import com.secunet.eidserver.testbed.testing.TlsTestingTestingPojo;
import com.secunet.testbedutils.helperclasses.Pair;

public class RunControllerTest
{
	@InjectMocks
	private RunControllerBean runController = new RunControllerBean();

	@Mock
	private DefaultTestCaseDAO defaultTestcaseDAOMock;

	private TestCandidate candidate;

	@BeforeClass
	public void setup() throws Exception
	{
		// mocks themselves
		MockitoAnnotations.initMocks(this);

		// empty POJOs
		DefaultTestCase testCase = new DefaultTestCaseTestingPojo();
		when(defaultTestcaseDAOMock.createNew()).thenReturn(testCase);

		// create the candidate
		candidate = new TestCandidateTestingPojo();
		Set<Tls> ecardTls = new HashSet<>();

		// 1.2
		Tls tls = new TlsTestingTestingPojo();
		List<IcsTlsCiphersuite> tlsCiphersuites = new ArrayList<>();
		tlsCiphersuites.add(IcsTlsCiphersuite.TLS_DH_DSS_WITH_3_DES_EDE_CBC_SHA);
		tlsCiphersuites.add(IcsTlsCiphersuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA);
		tlsCiphersuites.add(IcsTlsCiphersuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA_384);
		tls.setTlsCiphersuites(tlsCiphersuites);
		Set<IcsEllipticcurve> ellipticCurves = new HashSet<>();
		ellipticCurves.add(IcsEllipticcurve.SECP_256_R_1);
		ellipticCurves.add(IcsEllipticcurve.SECT_239_K_1);
		tls.setEllipticCurves(ellipticCurves);
		Set<IcsTlsSignaturealgorithms> signatureAlgorithms = new HashSet<>();
		signatureAlgorithms.add(IcsTlsSignaturealgorithms.SHA_1_WITH_RSA);
		signatureAlgorithms.add(IcsTlsSignaturealgorithms.SHA_256_WITH_ECDSA);
		tls.setSignatureAlgorithms(signatureAlgorithms);
		tls.setInterface(Interface.ECARDAPI_PSK);
		tls.setTlsVersion(IcsTlsVersion.TL_SV_12);
		ecardTls.add(tls);

		// 1.1, going to be ignored
		Tls tlsOld = new TlsTestingTestingPojo();
		List<IcsTlsCiphersuite> tlsCiphersuitesOld = new ArrayList<>();
		tlsCiphersuitesOld.add(IcsTlsCiphersuite.TLS_DH_DSS_WITH_3_DES_EDE_CBC_SHA);
		tlsCiphersuitesOld.add(IcsTlsCiphersuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA);
		tlsCiphersuitesOld.add(IcsTlsCiphersuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA_256);
		tlsOld.setTlsCiphersuites(tlsCiphersuitesOld);
		tlsOld.setInterface(Interface.ECARDAPI_PSK);
		tlsOld.setTlsVersion(IcsTlsVersion.TL_SV_11);
		ecardTls.add(tlsOld);

		candidate.setTlsEcardApiPsk(ecardTls);
	}

	@AfterClass
	public void teardown() throws Exception
	{
	}

	@Test(enabled = true)
	public void testIterateAll() throws Exception
	{
		Date startDate = new Date();
		// testcase
		TestCase tlsTestCase = new DefaultTestCaseTestingPojo();
		TestCaseStep outTls = new TestCaseStepTestingPojo();
		outTls.setName(GeneralConstants.TLS_STEP_MARKER);
		outTls.setTarget(TargetInterfaceType.E_CARD_API);
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		testCaseSteps.add(outTls);
		tlsTestCase.setTestCaseSteps(testCaseSteps);

		// run the expansion and verify the results
		List<TestCase> tcs = runController.expandCryptoTasks(candidate, tlsTestCase, startDate);
		assertNotNull(tcs);
		assertEquals(11, tcs.size());
	}

	@Test(enabled = true)
	public void testIterateSuites() throws Exception
	{
		Date startDate = new Date();
		// testcase
		TestCase tlsTestCase = new DefaultTestCaseTestingPojo();
		TestCaseStep outTls = new TestCaseStepTestingPojo();
		outTls.setName(GeneralConstants.TLS_STEP_MARKER);
		outTls.setTarget(TargetInterfaceType.E_CARD_API);
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		testCaseSteps.add(outTls);
		tlsTestCase.setTestCaseSteps(testCaseSteps);
		Map<String, String> variables = new HashMap<>();
		variables.put(GeneralConstants.TLS_VERSION, "TLSv12");
		variables.put(GeneralConstants.TLS_ELLIPTIC_CURVES, "secp256k1,secp256r1");
		variables.put(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS, "SHA1withECDSA,SHA512withRSA");
		tlsTestCase.setVariables(variables);

		// run the expansion and verify the results
		List<TestCase> tcs = runController.expandCryptoTasks(candidate, tlsTestCase, startDate);
		assertNotNull(tcs);
		assertEquals(3, tcs.size());
	}

	@Test(enabled = true)
	public void testIterateVersions() throws Exception
	{
		Date startDate = new Date();
		// testcase
		TestCase tlsTestCase = new DefaultTestCaseTestingPojo();
		TestCaseStep outTls = new TestCaseStepTestingPojo();
		outTls.setName(GeneralConstants.TLS_STEP_MARKER);
		outTls.setTarget(TargetInterfaceType.E_CARD_API);
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		testCaseSteps.add(outTls);
		tlsTestCase.setTestCaseSteps(testCaseSteps);
		Map<String, String> variables = new HashMap<>();
		variables.put(GeneralConstants.TLS_CIPHER_SUITES, "TLS_DH_DSS_WITH_3_DES_EDE_CBC_SHA,TLS_DH_DSS_WITH_AES_128_CBC_SHA");
		variables.put(GeneralConstants.TLS_ELLIPTIC_CURVES, "secp256k1,secp256r1");
		variables.put(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS, "SHA1withECDSA,SHA512withRSA");
		tlsTestCase.setVariables(variables);

		// run the expansion and verify the results
		List<TestCase> tcs = runController.expandCryptoTasks(candidate, tlsTestCase, startDate);
		assertNotNull(tcs);
		assertEquals(2, tcs.size());
	}

	@Test(enabled = true)
	public void testIterateCurves() throws Exception
	{
		Date startDate = new Date();
		// testcase
		TestCase tlsTestCase = new DefaultTestCaseTestingPojo();
		TestCaseStep outTls = new TestCaseStepTestingPojo();
		outTls.setName(GeneralConstants.TLS_STEP_MARKER);
		outTls.setTarget(TargetInterfaceType.E_CARD_API);
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		testCaseSteps.add(outTls);
		tlsTestCase.setTestCaseSteps(testCaseSteps);
		Map<String, String> variables = new HashMap<>();
		variables.put(GeneralConstants.TLS_VERSION, "TLSv12");
		variables.put(GeneralConstants.TLS_CIPHER_SUITES, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA_384,TLS_DH_DSS_WITH_AES_128_CBC_SHA");
		variables.put(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS, "SHA1withECDSA,SHA512withRSA");
		tlsTestCase.setVariables(variables);

		// run the expansion and verify the results
		List<TestCase> tcs = runController.expandCryptoTasks(candidate, tlsTestCase, startDate);
		assertNotNull(tcs);
		assertEquals(2, tcs.size());
	}

	@Test(enabled = true)
	public void testIterateSignatureAlgorithms() throws Exception
	{
		Date startDate = new Date();
		// testcase
		TestCase tlsTestCase = new DefaultTestCaseTestingPojo();
		TestCaseStep outTls = new TestCaseStepTestingPojo();
		outTls.setName(GeneralConstants.TLS_STEP_MARKER);
		outTls.setTarget(TargetInterfaceType.E_CARD_API);
		List<TestCaseStep> testCaseSteps = new ArrayList<>();
		testCaseSteps.add(outTls);
		tlsTestCase.setTestCaseSteps(testCaseSteps);
		Map<String, String> variables = new HashMap<>();
		variables.put(GeneralConstants.TLS_VERSION, "TLSv12");
		variables.put(GeneralConstants.TLS_CIPHER_SUITES, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA_384,TLS_DH_DSS_WITH_AES_128_CBC_SHA");
		variables.put(GeneralConstants.TLS_ELLIPTIC_CURVES, "secp256k1,secp256r1");
		tlsTestCase.setVariables(variables);

		// run the expansion and verify the results
		List<TestCase> tcs = runController.expandCryptoTasks(candidate, tlsTestCase, startDate);
		assertNotNull(tcs);
		assertEquals(2, tcs.size());
	}

	@Test(enabled = true)
	public void testSkipIrrelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();
		boolean success = runController.wasSuccessful(testCase, messages);
		assertTrue(success);
	}

	@Test(enabled = true)
	public void testNegativeSkipIrrelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();
		LogMessage failedRelevant = new LogMessageTestingPojo();
		failedRelevant.setTestStepName("FAILED");
		failedRelevant.setSuccess(false);
		messages.add(1, failedRelevant);
		boolean success = runController.wasSuccessful(testCase, messages);
		assertFalse(success);
	}

	@Test(enabled = true)
	public void testIncludeTlsInRelevant()
	{
		TestCase testCase = prepareRelevantTestCase();
		List<LogMessage> messages = prepareRelevantTestMessages();

		LogMessage tlsMessage = new LogMessageTestingPojo();
		tlsMessage.setTestStepName("FIRST");
		tlsMessage.setSuccess(false);
		messages.add(0, tlsMessage);

		boolean success = runController.wasSuccessful(testCase, messages);
		assertFalse(success);
	}

	private TestCase prepareRelevantTestCase()
	{
		TestCase testCase = new DefaultTestCaseTestingPojo();

		List<Pair<String, String>> relevantSteps = new ArrayList<>();
		Pair<String, String> firstRelevant = new Pair<>("FIRST", "MID");
		Pair<String, String> secondRelevant = new Pair<>("MID2", "END");
		relevantSteps.add(firstRelevant);
		relevantSteps.add(secondRelevant);
		testCase.setRelevantSteps(relevantSteps);

		TestCaseStep first = new TestCaseStepTestingPojo();

		// testCase.setTestCaseSteps(testCaseSteps);
		return testCase;
	}

	private List<LogMessage> prepareRelevantTestMessages()
	{
		List<LogMessage> messages = new ArrayList<>();

		LogMessage first = new LogMessageTestingPojo();
		first.setTestStepName("FIRST");
		first.setSuccess(true);

		LogMessage second = new LogMessageTestingPojo();
		second.setTestStepName("MID");
		second.setSuccess(true);

		LogMessage irrelevantFailure = new LogMessageTestingPojo();
		irrelevantFailure.setTestStepName("FAILURE");
		irrelevantFailure.setSuccess(true);

		LogMessage relevantAgain = new LogMessageTestingPojo();
		relevantAgain.setTestStepName("MID2");
		relevantAgain.setSuccess(true);

		LogMessage last = new LogMessageTestingPojo();
		last.setTestStepName("END");
		last.setSuccess(true);

		messages.add(first);
		messages.add(second);
		messages.add(irrelevantFailure);
		messages.add(relevantAgain);
		messages.add(last);

		return messages;
	}

}

