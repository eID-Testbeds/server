package com.secunet.eidserver.testbed.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.classes.TestRunModel;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.exceptions.TestcaseNotFoundException;
import com.secunet.eidserver.testbed.common.exceptions.TestrunException;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.interfaces.beans.RunController;
import com.secunet.eidserver.testbed.common.interfaces.beans.Runner;
import com.secunet.eidserver.testbed.common.interfaces.dao.CertificateX509DAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestRunDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.testbedutils.helperclasses.Pair;

@Singleton
public class RunControllerBean implements RunController
{

	private static final Logger logger = LogManager.getRootLogger();

	Map<TestCandidate, TestRunModel> runningModels = new HashMap<>();

	@Resource(lookup = "concurrent/testExecutor")
	private ManagedExecutorService executorService;

	@EJB
	private Runner runner;

	@EJB
	private TestCandidateDAO testCandidateDAO;

	@EJB
	private DefaultTestCaseDAO testCaseDAO;

	@EJB
	private LogDAO logDAO;

	@EJB
	private LogMessageDAO logMessageDAO;

	@EJB
	private CertificateX509DAO x509DAO;

	@EJB
	private TestRunDAO testRunDAO;

	@PostConstruct
	public void init()
	{
		logger.debug("RunController initialization: ", toString());
	}

	@PreDestroy
	public void destroy()
	{
		logger.debug("RunController shutdown: ", toString());
	}

	@Override
	public List<LogMessage> run(final String testName, final String candidateId) throws Exception
	{
		// fetch data
		TestCandidate candidate = testCandidateDAO.findById(candidateId);
		TestCase tc = null;
		for (TestCase t : candidate.getTestCases())
		{
			if (t.getName().equals(testName))
			{
				tc = t;
				break;
			}
		}
		if (tc == null)
		{
			throw new TestcaseNotFoundException("Could not find testcase named " + testName);
		}
		List<LogMessage> logs = runner.runTest(candidate, tc);
		return logs;
	}

	@Override
	public Map<TestCase, List<LogMessage>> runAll(final String candidateId) throws Exception
	{
		// fetch data
		TestCandidate candidate = testCandidateDAO.findById(candidateId);
		Map<TestCase, List<LogMessage>> complete = new HashMap<>();
		// run each testcase
		for (TestCase tc : candidate.getTestCases())
		{
			List<LogMessage> logs = runner.runTest(candidate, tc);
			complete.put(tc, logs);
		}
		return complete;
	}

	@Override
	public List<String> getTestCaseNames(final String profileId)
	{
		List<String> testCaseNames = new ArrayList<String>();
		// fetch data
		TestCandidate profile = testCandidateDAO.findById(profileId);
		for (TestCase tc : profile.getTestCases())
		{
			testCaseNames.add(tc.getName());
		}
		return testCaseNames;
	}

	@Override
	public String toString()
	{
		return "RunController{" + "testCaseDAO=" + testCaseDAO + '}';
	}

	@Override
	public void startTestCasesForProfile(String candidateName, List<String> testCaseNames) throws Exception
	{
		for (String name : testCaseNames)
		{
			run(name, candidateName);
		}
	}

	@Override
	public void startTestrun(TestRunModel model) throws Exception
	{
		Date startDate = new Date();
		TestCandidate testCandidate = model.getCandidate();
		if (model.getLogs() == null)
		{
			model.setLogs(new HashSet<>());
		}
		model.setExecutionStarted(new Date());
		runningModels.put(testCandidate, model);
		final List<Callable<Log>> callables = new ArrayList<>();
		Set<TestCase> nonExpandedToRemove = new HashSet<>();
		Set<TestCase> expandedToAdd = new HashSet<>();
		for (TestCase testCase : model.getProgress().keySet())
		{
			// "expand" crypto testcases
			if ((testCase.getTestCaseSteps().size() == 1 && testCase.getTestCaseSteps().get(0).getName().startsWith(GeneralConstants.TLS_STEP_MARKER))
					|| (testCase.getTestCaseSteps().size() == 3 && testCase.getTestCaseSteps().get(2).getName().startsWith(GeneralConstants.TLS_STEP_MARKER)))
			{
				List<TestCase> expanded = expandCryptoTasks(testCandidate, testCase, startDate);
				List<RunTask> tasks = new ArrayList<>();
				for (TestCase tc : expanded)
				{
					tasks.add(new RunTask(testCandidate, tc, startDate));

				}
				nonExpandedToRemove.add(testCase);
				expandedToAdd.addAll(expanded);
				callables.addAll(tasks);
			}
			// XML Signature
			else if (testCase.getTestCaseSteps().size() == 2 && null != testCase.getVariables() && !testCase.getVariables().isEmpty()
					&& testCase.getVariables().containsKey(GeneralConstants.XML_SIGNATURE) && !testCase.getVariables().get(GeneralConstants.XML_SIGNATURE).isEmpty())
			{
				if (!testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML) && testCase.getVariables().containsKey(GeneralConstants.XML_SIGNATURE_DIGEST)
						&& !testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_DIGEST).isEmpty())
				{
					// special negative SOAP test case: test all supported signature algorithms using configured digest and canonicalization
					LinkedList<TestCase> xmlSignatureTestCases = getExpandedXmlSignatureTestCases(testCase);
					xmlSignatureTestCases.forEach(tc -> {
						expandedToAdd.add(tc);
						callables.add(new RunTask(testCandidate, tc, startDate));
					});
				}
				else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML) && testCase.getVariables().containsKey(GeneralConstants.XML_SIGNATURE_URI)
						&& !testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_URI).isEmpty())
				{
					// special negative SAML test case: test unsupported signature algorithms
					expandedToAdd.add(testCase);
					callables.add(new RunTask(testCandidate, testCase, startDate));
				}
				// add all combinations signature algorithm, canonicalization and digest declared in the ics for the current interface
				else
				{
					Set<XmlSignature> algos = null;
					if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML))
					{
						algos = testCandidate.getXmlSignatureAlgorithmsSaml();
					}
					else
					{
						algos = testCandidate.getXmlSignatureAlgorithmsEid();
					}

					for (XmlSignature xmlsig : algos)
					{
						// Check signature algorithm
						if (testCase.getVariables().get(GeneralConstants.XML_SIGNATURE).startsWith(CryptoHelper.getAlgorithm(xmlsig.getSignatureAlgorithm().value(), true)))
						{
							TestCase xmlTestCase = cloneTestCase(testCase);
							xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_URI, xmlsig.getSignatureAlgorithm().value());
							if (!testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML))
							{
								xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_DIGEST, xmlsig.getDigestMethod().value()); // will not be used for SAML
								xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_CANONICALIZATION, xmlsig.getCanonicalizationMethod().value()); // will not be used for SAML
								xmlTestCase.setName(
										testCase.getName() + "_" + xmlsig.getSignatureAlgorithm().value() + "_" + xmlsig.getCanonicalizationMethod().value() + "_" + xmlsig.getDigestMethod().value());
							}
							else
							{
								xmlTestCase.setName(testCase.getName() + "_" + xmlsig.getSignatureAlgorithm().value());

							}
							expandedToAdd.add(xmlTestCase);
							callables.add(new RunTask(testCandidate, xmlTestCase, startDate));
						}
					}
				}
				nonExpandedToRemove.add(testCase);
			}
			// XML encryption handling - only add test cases supported by the candidate and expand all key transports
			// TODO also expand other crypto parameters
			else if (testCase.getTestCaseSteps().size() == 2 && null != testCase.getVariables() && !testCase.getVariables().isEmpty()
					&& testCase.getVariables().containsKey(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER) && !testCase.getVariables().get(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER).isEmpty())
			{
				nonExpandedToRemove.add(testCase);
				for (IcsXmlsecEncryptionContentUri xmlEncryption : testCandidate.getXmlEncryptionAlgorithms())
				{
					if (xmlEncryption.value().equals(testCase.getVariables().get(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER)))
					{
						for (XmlEncryptionKeyTransport xmlKeyTransport : testCandidate.getXmlKeyTransport())
						{
							TestCase encryptionTestCase = cloneTestCase(testCase);
							encryptionTestCase.getVariables().put(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER, xmlKeyTransport.getTransportAlgorithm().value());
							encryptionTestCase.setName(testCase.getName() + "_" + xmlKeyTransport.getTransportAlgorithm().value());
							expandedToAdd.add(encryptionTestCase);
							callables.add(new RunTask(testCandidate, encryptionTestCase, startDate));
						}
						break;
					}
				}
			}
			else
			{
				callables.add(new RunTask(testCandidate, testCase, startDate));
			}
		}

		// remove the original test case from the model and add the expanded tests
		for (TestCase toRemove : nonExpandedToRemove)
		{
			model.getProgress().remove(toRemove);
		}
		for (TestCase toAdd : expandedToAdd)
		{
			model.getProgress().put(toAdd, 0);
		}

		// note: invoke all; does not return until the execution of all tasks is over. submit them one at a time instead
		if (callables.isEmpty())
		{
			finishTestrun(model, startDate, testCandidate);
		}
		else
		{
			callables.forEach(callable -> executorService.submit(callable));
		}
	}

	/**
	 * Create a copy of the given {@link TestCase}. This is not implemented as a copy constructor due to JPA (EclipseLink) constraints.
	 * 
	 * @param testCase
	 * @return
	 */
	private TestCase cloneTestCase(TestCase testCase)
	{
		TestCase xmlTestCase = testCaseDAO.createNew();
		xmlTestCase.setCandidates(testCase.getCandidates());
		xmlTestCase.setCard(testCase.getCard());
		xmlTestCase.setEservice(testCase.getEservice());
		xmlTestCase.setCertificateBaseNames(testCase.getCertificateBaseNames());
		xmlTestCase.setDescription(testCase.getDescription());
		xmlTestCase.setMandatoryProfiles(testCase.getMandatoryProfiles());
		xmlTestCase.setModule(testCase.getModule());
		xmlTestCase.setOptionalProfiles(testCase.getOptionalProfiles());
		xmlTestCase.setTestCaseSteps(testCase.getTestCaseSteps());
		xmlTestCase.setRelevantSteps(testCase.getRelevantSteps());
		Map<String, String> vars = new HashMap<>();
		for (Map.Entry<String, String> entry : testCase.getVariables().entrySet())
		{
			vars.put(entry.getKey(), entry.getValue());
		}
		xmlTestCase.setVariables(vars);
		return xmlTestCase;
	}

	/**
	 * Create a {@link List} of {@link TestCase}s from the given XML signature testCase. The list contains a
	 * 
	 * @param testCase
	 * @return
	 */
	private LinkedList<TestCase> getExpandedXmlSignatureTestCases(TestCase testCase)
	{
		LinkedList<TestCase> list = new LinkedList<TestCase>();

		if (testCase.getVariables().containsKey(GeneralConstants.XML_SIGNATURE_DIGEST) && !testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_DIGEST).isEmpty())
		{
			String canonicalization = IcsXmlsecSignatureCanonicalization.HTTP_WWW_W_3_ORG_2001_10_XML_EXC_C_14_N.value();
			if (testCase.getVariables().containsKey(GeneralConstants.XML_SIGNATURE_CANONICALIZATION) && !testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_CANONICALIZATION).isEmpty())
			{
				canonicalization = testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_CANONICALIZATION);
			}
			String digest = testCase.getVariables().get(GeneralConstants.XML_SIGNATURE_DIGEST);
			List<String> signAlgos = CryptoHelper.getSupportedSignatureAlgorithms(CryptoHelper.getAlgorithm(testCase.getVariables().get(GeneralConstants.XML_SIGNATURE), true));
			for (String signAlgo : signAlgos)
			{
				TestCase xmlTestCase = cloneTestCase(testCase);
				xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_URI, signAlgo);
				xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_DIGEST, digest);
				xmlTestCase.getVariables().put(GeneralConstants.XML_SIGNATURE_CANONICALIZATION, canonicalization);
				xmlTestCase.setName(testCase.getName() + "_" + signAlgo + "_" + canonicalization + "_" + digest);
				list.add(xmlTestCase);
			}
		}
		return list;
	}

	/**
	 * Expands TLS cryptography tests by iterating through the ICS parameters. Parameters specified in the test case are kept, non-specified parameters are overwritten.
	 * 
	 * @param testCandidate
	 *            {@link testCandidate} The test candidate for which the tests shall be created
	 * @param testCase
	 *            {@link TestCase} The test case which is to be expanded
	 * @return
	 */
	protected List<TestCase> expandCryptoTasks(TestCandidate testCandidate, TestCase testCase, Date startDate)
	{
		// which interface are we testing? see OUT_TLS step (= last step)
		TargetInterfaceType target = testCase.getTestCaseSteps().get(testCase.getTestCaseSteps().size() - 1).getTarget();
		Set<Tls> tlsSet = null;
		switch (target)
		{
			case E_ID_INTERFACE:
				tlsSet = testCandidate.getTlsEidInterface();
				break;
			case E_CARD_API:
				tlsSet = testCandidate.getTlsEcardApiPsk();
				break;
			case SAML:
				tlsSet = testCandidate.getTlsSaml();
				break;
			case ATTACHED:
				tlsSet = testCandidate.getTlsEcardApiAttached();
				break;
		}

		Map<String, ArrayList<String>> tlsCiphersuites = new HashMap<>();
		List<String> ellipticCurves = new ArrayList<>();
		List<String> signatureAlgorithms = new ArrayList<>();

		// check if tls data was provided for this interface
		if (null != tlsSet && !tlsSet.isEmpty())
		{
			if (testCase.getVariables().containsKey(GeneralConstants.TLS_VERSION))
			{
				boolean hadTlsVersion = false;
				for (Tls tls : tlsSet)
				{
					if (tls.getTlsVersion().value().equals(testCase.getVariables().get(GeneralConstants.TLS_VERSION)))
					{
						hadTlsVersion = true;
						fillParameters(tls, testCase, tlsCiphersuites, ellipticCurves, signatureAlgorithms);
					}
				}
				if (!hadTlsVersion)
				{
					// Given test case tries to use an unsupported TLS version.
					// It may be a negative test that provides all TLS parameters.
					if (testCase.getVariables().containsKey(GeneralConstants.TLS_CIPHER_SUITES) && testCase.getVariables().containsKey(GeneralConstants.TLS_ELLIPTIC_CURVES)
							&& testCase.getVariables().containsKey(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS))
					{
						ArrayList<String> suiteList = new ArrayList<>();
						if (!testCase.getVariables().get(GeneralConstants.TLS_CIPHER_SUITES).isEmpty())
						{
							suiteList.add(testCase.getVariables().get(GeneralConstants.TLS_CIPHER_SUITES));
							tlsCiphersuites.put(testCase.getVariables().get(GeneralConstants.TLS_VERSION), suiteList);
						}
						if (!testCase.getVariables().get(GeneralConstants.TLS_ELLIPTIC_CURVES).isEmpty())
						{
							ellipticCurves.add(testCase.getVariables().get(GeneralConstants.TLS_ELLIPTIC_CURVES));
						}
						if (!testCase.getVariables().get(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS).isEmpty())
						{
							signatureAlgorithms.add(testCase.getVariables().get(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS));
						}
						logger.info("Using TLS data for the requested version " + testCase.getVariables().get(GeneralConstants.TLS_VERSION) + " directly from test case definition.");
					}
					else
					{
						logger.error("No TLS data was found for the requested version " + testCase.getVariables().get(GeneralConstants.TLS_VERSION) + ".");
					}
				}
			}
			// iterate over all TLS versions
			else
			{
				for (Tls tls : tlsSet)
				{
					fillParameters(tls, testCase, tlsCiphersuites, ellipticCurves, signatureAlgorithms);
				}
			}
		}
		else
		{
			logger.error("No TLS data was found for the selected interface " + target.value() + ".");
		}


		// now build the run tasks
		List<TestCase> testCases = new ArrayList<>();
		for (String version : tlsCiphersuites.keySet())
		{
			for (String ciphersuite : tlsCiphersuites.get(version))
			{
				if (ciphersuite.contains("RSA_PSK"))
				{
					TestCase tc = createExpandedTestCase(testCase, version, ciphersuite, null, null);
					testCases.add(tc);
				}
				else
				{
					// in case of TLSv1.2, add ec and sigalgs
					if (version.equals(IcsTlsVersion.TL_SV_12.value()))
					{
						if (ciphersuite.contains("ECDSA"))
						{
							for (String curve : ellipticCurves)
							{
								for (String sigAlg : signatureAlgorithms)
								{
									TestCase tc = createExpandedTestCase(testCase, version, ciphersuite, curve, sigAlg);
									testCases.add(tc);
								}
							}
						}
						else
						{
							for (String sigAlg : signatureAlgorithms)
							{
								TestCase tc = createExpandedTestCase(testCase, version, ciphersuite, null, sigAlg);
								testCases.add(tc);
							}
						}
					}
					else
					{
						if (ciphersuite.contains("ECDSA"))
						{
							for (String curve : ellipticCurves)
							{
								TestCase tc = createExpandedTestCase(testCase, version, ciphersuite, curve, null);
								testCases.add(tc);
							}
						}
						else
						{
							TestCase tc = createExpandedTestCase(testCase, version, ciphersuite, null, null);
							testCases.add(tc);
						}
					}
				}
			}
		}

		return testCases;
	}

	private TestCase createExpandedTestCase(TestCase testCase, String version, String ciphersuite, String curve, String sigAlg)
	{
		TestCase tc = testCaseDAO.createNew();
		String testCaseName = testCase.getName() + "_" + ciphersuite;
		tc.setCandidates(testCase.getCandidates());
		tc.setCard(testCase.getCard());
		tc.setEservice(testCase.getEservice());
		tc.setCertificateBaseNames(testCase.getCertificateBaseNames());
		tc.setDescription(testCase.getDescription());
		tc.setMandatoryProfiles(testCase.getMandatoryProfiles());
		tc.setModule(testCase.getModule());
		tc.setOptionalProfiles(testCase.getOptionalProfiles());
		tc.setTestCaseSteps(testCase.getTestCaseSteps());
		tc.setRelevantSteps(testCase.getRelevantSteps());
		// create copy of variables
		Map<String, String> vars = new HashMap<>(testCase.getVariables());
		vars.put(GeneralConstants.TLS_VERSION, version);
		if (!vars.containsKey(GeneralConstants.TLS_VERSION_SELECTED))
		{
			vars.put(GeneralConstants.TLS_VERSION_SELECTED, version);
		}
		vars.put(GeneralConstants.TLS_CIPHER_SUITES, ciphersuite);
		if (!vars.containsKey(GeneralConstants.TLS_SELECTED_CIPHER_SUITE))
		{
			vars.put(GeneralConstants.TLS_SELECTED_CIPHER_SUITE, ciphersuite);
		}
		if (sigAlg != null)
		{
			vars.put(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS, sigAlg);
			testCaseName += "_" + sigAlg;
		}
		if (curve != null)
		{
			vars.put(GeneralConstants.TLS_ELLIPTIC_CURVES, curve);
			testCaseName += "_" + curve;
		}
		tc.setName(testCaseName);
		tc.setVariables(vars);
		return tc;
	}

	private void fillParameters(Tls tls, TestCase testCase, Map<String, ArrayList<String>> tlsCiphersuites, List<String> ellipticCurves, List<String> signatureAlgorithms)
	{
		// process the ciphersuites
		ArrayList<String> suiteList = new ArrayList<>();
		if (testCase.getVariables().containsKey(GeneralConstants.TLS_CIPHER_SUITES))
		{
			// if the ciphersuites variable exists but is empty, fill it with the ciphersuites defined for the candidate
			if (testCase.getVariables().get(GeneralConstants.TLS_CIPHER_SUITES).isEmpty())
			{
				String suites = new String();
				for (IcsTlsCiphersuite suite : tls.getTlsCiphersuites())
				{
					suites += suite.value() + ",";
				}
				suiteList.add(suites.substring(0, suites.length() - 1));
			}
			// if it isn't empty, just process it as provided
			else
			{
				suiteList.add(testCase.getVariables().get(GeneralConstants.TLS_CIPHER_SUITES));
			}
		}
		else
		{
			// if the ciphersuite variable does not exist, iterate over each suite provided in the ics
			for (IcsTlsCiphersuite suite : tls.getTlsCiphersuites())
			{
				suiteList.add(suite.value());
			}
		}
		tlsCiphersuites.put(tls.getTlsVersion().value(), suiteList);

		// if tls1.2 is to be processed, also check the elliptic curves and signature algorithms
		if (tls.getTlsVersion().equals(IcsTlsVersion.TL_SV_12))
		{
			// curves
			if (testCase.getVariables().containsKey(GeneralConstants.TLS_ELLIPTIC_CURVES))
			{
				if (testCase.getVariables().get(GeneralConstants.TLS_ELLIPTIC_CURVES).isEmpty())
				{
					String curves = new String();
					for (IcsEllipticcurve curve : tls.getEllipticCurves())
					{
						curves += curve.value() + ",";
					}
					ellipticCurves.add(curves.substring(0, curves.length() - 1));
				}
				else
				{
					ellipticCurves.add(testCase.getVariables().get(GeneralConstants.TLS_ELLIPTIC_CURVES));
				}
			}
			else
			{
				for (IcsEllipticcurve curve : tls.getEllipticCurves())
				{
					ellipticCurves.add(curve.value());
				}
			}

			// signature algorithms
			if (testCase.getVariables().containsKey(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS))
			{
				if (testCase.getVariables().get(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS).isEmpty())
				{
					String sigAlgs = new String();
					for (IcsTlsSignaturealgorithms sigAlg : tls.getSignatureAlgorithms())
					{
						sigAlgs += sigAlg.value() + ",";
					}
					signatureAlgorithms.add(sigAlgs.substring(0, sigAlgs.length() - 1));
				}
				else
				{
					signatureAlgorithms.add(testCase.getVariables().get(GeneralConstants.TLS_SUPPORTED_SIGNATURE_ALGORITHMS));
				}
			}
			else
			{
				for (IcsTlsSignaturealgorithms sigAlg : tls.getSignatureAlgorithms())
				{
					signatureAlgorithms.add(sigAlg.value());
				}
			}
		}
	}

	@Override
	public TestRunModel getTestRunModel(TestCandidate candidate)
	{
		if (runningModels.containsKey(candidate))
		{
			return runningModels.get(candidate);
		}
		else
		{
			return null;
		}
	}

	private void finishTestrun(TestRunModel model, Date startDate, TestCandidate testCandidate)
	{
		{
			model.setFinished(new Date());
			TestRun testRun = testRunDAO.createNew();
			testRun.setStartDate(startDate);
			testRun.setEndDate(new Date());
			testRun.setLogs(model.getLogs());
			testRunDAO.persist(testRun);
			Set<TestRun> testRuns = testCandidate.getTestRuns();
			testRuns.add(testRun);
			testCandidateDAO.update(testCandidate);
		}
	}

	/**
	 * Private class used for running test cases in parallel.
	 *
	 */
	protected class RunTask implements Callable<Log>
	{
		private final TestCase testCase;
		private final TestCandidate testCandidate;
		private final Date startDate;

		public RunTask(final TestCandidate testCandidate, final TestCase testCase, final Date startDate)
		{
			this.testCase = testCase;
			this.testCandidate = testCandidate;
			this.startDate = startDate;
		}

		@Override
		public Log call() throws Exception
		{
			// run testcase and persist the result
			TestRunModel model = runningModels.get(testCandidate);
			Log log = null;
			List<LogMessage> logMessages;
			try
			{
				logMessages = runner.runTest(testCandidate, testCase);
				log = logDAO.createNew();
				log.setLogMessages(logMessages);
				log.setTestCase(testCase.getName());
				log.setRunDate(startDate);
				log.setSuccess(wasSuccessful(testCase, logMessages));
				logDAO.persist(log);
				model.getLogs().add(log);
			}
			catch (TestrunException e)
			{
				LogMessage failedMessage = logMessageDAO.createNew();
				failedMessage.setSuccess(false);
				failedMessage.setTestStepName("Internal error - " + e.getInnerException().getClass().getName());
				failedMessage.setMessage("An internal error occured. Please see the technical log for details.");
				logMessages = e.getLogMessagesUntilFailure();
				logMessages.add(failedMessage);
				log = logDAO.createNew();
				log.setLogMessages(logMessages);
				log.setTestCase(testCase.getName());
				log.setRunDate(startDate);
				log.setSuccess(false);
				logDAO.persist(log);
				model.getLogs().add(log);
			}
			finally
			{
				// update the model
				model.getProgress().put(testCase, 100);

				// check if all test runs are finished
				boolean done = true;
				for (int progress : model.getProgress().values())
				{
					if (progress != 100)
					{
						done = false;
						break;
					}
				}

				if (done)
				{
					finishTestrun(model, startDate, testCandidate);
				}
			}
			return log;
		}
	}

	/**
	 * Check whether the run was successful
	 * 
	 * @param testCase
	 *            The {@link TestCase} which was run
	 * @param messages
	 *            The {@link List} of {@link LogMessage}s
	 * @return A boolean indicating whether the test was successful
	 */
	protected boolean wasSuccessful(TestCase testCase, List<LogMessage> messages)
	{
		boolean success = true;
		// regular test
		int stepIndex = 0;
		List<Pair<String, String>> relevantSteps = testCase.getRelevantSteps();
		for (Pair<String, String> relevantPair : relevantSteps)
		{
			boolean started = false;
			for (int i = stepIndex; i < messages.size(); i++)
			{
				LogMessage message = messages.get(i);
				if (message.getTestStepName().equals(relevantPair.getA()))
				{
					// check if a tls step preceded the first relevant step
					if (i != 0)
					{
						LogMessage previousMessage = messages.get(i - 1);
						if (previousMessage.getTestStepName().contains("TLS"))
						{
							success &= previousMessage.getSuccess();
						}
					}
					started = true;
				}
				if (started)
				{
					success &= message.getSuccess();
				}
				if (message.getTestStepName().equals(relevantPair.getB()))
				{
					stepIndex = i;
					break;
				}
			}
		}
		return success;
	}
}
