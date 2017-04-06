package com.secunet.eidserver.testbed.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.util.PrivateKeyFactory;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.exceptions.TestrunException;
import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.interfaces.beans.Runner;
import com.secunet.eidserver.testbed.common.interfaces.beans.TlsTester;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.tlsclient.TLSConnectionClient;

@Stateless
public class RunnerBean implements Runner
{
	private static final Logger logger = LogManager.getLogger(RunnerBean.class);

	@EJB
	private LogMessageDAO logMessageDAO;

	@EJB
	private TlsTester tlsTester;

	@Override
	public List<LogMessage> runTest(TestCandidate candidate, TestCase testCase) throws TestrunException
	{
		List<LogMessage> result = new ArrayList<LogMessage>();

		try
		{
			// Verify input
			if (null == candidate)
			{
				logger.fatal("Testcase must not be null.");
				return result;
			}
			if (null == testCase)
			{
				logger.fatal("Profile must not be null");
				return result;
			}
			if (testCase.getTestCaseSteps().size() == 0)
			{
				logger.fatal("Testcase must contain test steps.");
				return result;
			}

			logger.debug(candidate.getProfileName() + " (" + candidate.getCandidateName() + ") - " + testCase.getName());

			// load certificates from candidate
			Map<String, X509Certificate> x509Certificates = loadCertificatesFromCandidate(candidate);

			// load variables
			KnownValues values = loadVariables(testCase);

			// get the client certificate and its key
			Certificate clientAuthCertificate = loadTlsClientAuthenticationCertificate(candidate, testCase);
			AsymmetricKeyParameter clientAuthKey = loadTlsClientAuthenticationPrivateKey(candidate, testCase);

			// it is a non-psk tls test case
			if ((!testCase.getTestCaseSteps().isEmpty() && testCase.getTestCaseSteps().get(0).getMessage().contains(("TlsStepToken"))) ? true : false)
			{
				RunnerTLS runnerTls = new RunnerTLS(tlsTester);
				List<LogMessage> resultTls = runnerTls.runTest(candidate, testCase, values, clientAuthCertificate, clientAuthKey);
				result.addAll(resultTls);
			}
			// it is a PSK TLS test case, so we need to initialize the PSK channel via SOAP
			else if (testCase.getMandatoryProfiles().contains(IcsMandatoryprofile.CRYPTO) && testCase.getOptionalProfiles().contains(IcsOptionalprofile.TLS_PSK))
			{
				if (null != candidate.getEidinterfaceUrl() && null != candidate.getEcardapiUrl())
				{
					runTlsPskViaSoap(result, candidate, testCase, values, x509Certificates, clientAuthCertificate, clientAuthKey);
				}
				else
				{
					LogMessage failMsg = logMessageDAO.createNew();
					failMsg.setSuccess(false);
					failMsg.setMessage("No eID SOAP / eCardAPI URL has been provided for the given profile.");
					result.add(failMsg);
					logger.fatal("No eID SOAP / eCardAPI URL has been provided for the given profile, aborting.");
				}
			}
			// Check whether SAML should be used
			else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML))
			{
				if (null != candidate.getSamlUrl())
				{
					runSaml(result, candidate, testCase, values, x509Certificates, clientAuthCertificate, clientAuthKey);
				}
				else
				{
					LogMessage failMsg = logMessageDAO.createNew();
					failMsg.setSuccess(false);
					failMsg.setMessage("No SAML URL has been provided for the given profile.");
					result.add(failMsg);
					logger.fatal("No SAML URL has been provided for the given profile, aborting.");
				}
			}
			// it is a SOAP or SOAP attached test case
			else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SOAP) || testCase.getOptionalProfiles().contains(IcsOptionalprofile.ESER_ATTACHED))
			{
				if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SOAP))
				{
					if (null != candidate.getEidinterfaceUrl())
					{
						runSoap(result, candidate, testCase, values, false, x509Certificates, false, clientAuthCertificate, clientAuthKey);
					}
					else
					{
						LogMessage failMsg = logMessageDAO.createNew();
						failMsg.setSuccess(false);
						failMsg.setMessage("No eID SOAP URL has been provided for the given profile.");
						result.add(failMsg);
						logger.fatal("No eID SOAP URL has been provided for the given profile, aborting.");
					}
				}
				else
				{
					if (null != candidate.getAttachedTcTokenUrl())
					{
						runSoap(result, candidate, testCase, values, false, x509Certificates, true, clientAuthCertificate, clientAuthKey);
					}
					else
					{
						LogMessage failMsg = logMessageDAO.createNew();
						failMsg.setSuccess(false);
						failMsg.setMessage("No URL for the retreival of the TC Token has been provided for the given profile.");
						result.add(failMsg);
						logger.fatal("No URL for the retreival of the TC Token has been provided for the given profile, aborting.");
					}
				}
			}
			// it is a PAOS test case, which we initialize via SOAP interface
			else if (testCase.getMandatoryProfiles().contains(IcsMandatoryprofile.PAOS))
			{
				if (null != candidate.getEidinterfaceUrl() && null != candidate.getEcardapiUrl())
				{
					runSoap(result, candidate, testCase, values, false, x509Certificates, false, clientAuthCertificate, clientAuthKey);
				}
				else
				{
					LogMessage failMsg = logMessageDAO.createNew();
					failMsg.setSuccess(false);
					failMsg.setMessage("No eID SOAP / eCardAPI URL has been provided for the given PAOS profile.");
					result.add(failMsg);
					logger.fatal("No eID SOAP / eCardAPI URL has been provided for the given PAOS profile, aborting.");
				}
			}
			else
			{
				// no interface has been specified
				LogMessage failMsg = logMessageDAO.createNew();
				failMsg.setSuccess(false);
				failMsg.setMessage("Testcases without profiles SAML, SOAP or CRYPTO are not supported.");
				failMsg.setTestStepName("Initialization");
				result.add(failMsg);
				logger.fatal("Testcases without profiles SAML, SOAP or CRYPTO are not supported.");
			}
		}
		catch (Exception e)
		{
			throw new TestrunException(result, e);
		}
		return result;
	}


	/**
	 * Read all variables defined in the testcase into a new map
	 * 
	 * @param testCase
	 * @return
	 */
	private KnownValues loadVariables(TestCase testCase)
	{
		KnownValues values = new KnownValues();
		for (String variableName : testCase.getVariables().keySet())
		{
			KnownValue value = new KnownValue(variableName, testCase.getVariables().get(variableName));
			values.add(value);
		}
		return values;
	}

	// Open a TLS connection to the SOAP interface
	private TLSConnectionClient establishConnectionToSOAPInterface(List<LogMessage> result, TestCandidate candidate, X509Certificate cert, Certificate clientAuthCertificate,
			AsymmetricKeyParameter clientAuthKey)
	{
		TLSConnectionClient soapConnection = null;

		LogMessage tlsFailure = logMessageDAO.createNew();
		tlsFailure.setTestStepName("TLS SOAP initialization");
		try
		{
			// TLSConnectionClient connection = new TLSConnectionClient(cert, candidate.getEidinterfaceUrl().getHost(), candidate.getEidinterfaceUrl().getPort());
			TLSConnectionClient connection = new TLSConnectionClient(cert, candidate.getEidinterfaceUrl().getHost(), candidate.getEidinterfaceUrl().getPort(), clientAuthCertificate, clientAuthKey);
			int[] ciphersuites = getCipherSuites(candidate.getTlsEidInterface());
			connection.setCipherSuites(ciphersuites);
			connection.connect();
			Result certificateResult = connection.getResult();
			String message = "Established TLS connection to " + candidate.getEidinterfaceUrl().getHost() + ", TLS handshake: " + connection.getHandshakeData() + ". " + certificateResult.getMessage();
			tlsFailure.setMessage(message);
			if (!GeneralConstants.DEBUG_MODE)
			{
				tlsFailure.setSuccess(certificateResult.wasSuccessful());
			}
			else
			{
				tlsFailure.setSuccess(true);
			}
			soapConnection = connection;
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not establish TLS connection to " + candidate.getEidinterfaceUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getEidinterfaceUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
					+ ". See technical log for stacktrace");
		}
		finally
		{
			result.add(tlsFailure);
		}

		return soapConnection;
	}

	// Open a TLS connection to the attached server
	private TLSConnectionClient establishConnectionToAttachedServer(List<LogMessage> result, TestCandidate candidate, X509Certificate cert)
	{
		TLSConnectionClient attachedServerConnection = null;

		LogMessage tlsFailure = logMessageDAO.createNew();
		tlsFailure.setTestStepName("TLS attached initialization");
		try
		{
			attachedServerConnection = new TLSConnectionClient(cert, candidate.getAttachedTcTokenUrl().getHost(), candidate.getAttachedTcTokenUrl().getPort());
			attachedServerConnection.connect();
			Result certificateResult = attachedServerConnection.getResult();
			String message = "Established TLS connection to " + candidate.getAttachedTcTokenUrl().getHost() + ", TLS handshake: " + attachedServerConnection.getHandshakeData() + ". "
					+ certificateResult.getMessage();
			tlsFailure.setMessage(message);
			if (!GeneralConstants.DEBUG_MODE)
			{
				tlsFailure.setSuccess(certificateResult.wasSuccessful());
			}
			else
			{
				tlsFailure.setSuccess(true);
			}
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error(
					"Could not establish TLS connection to " + candidate.getAttachedTcTokenUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getAttachedTcTokenUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
					+ ". See technical log for stacktrace");
		}
		finally
		{
			result.add(tlsFailure);
		}

		return attachedServerConnection;
	}

	// Open a TLS connection to the SAML interface
	private TLSConnectionClient establishConnectionToSAMLProcessor(List<LogMessage> result, TestCandidate candidate, X509Certificate cert)
	{
		TLSConnectionClient samlConnection = null;

		LogMessage tlsFailure = logMessageDAO.createNew();
		tlsFailure.setTestStepName("TLS SAML initialization");
		try
		{
			TLSConnectionClient connection = new TLSConnectionClient(cert, candidate.getSamlUrl().getHost(), candidate.getSamlUrl().getPort());
			int[] ciphersuites = getCipherSuites(candidate.getTlsSaml());
			connection.setCipherSuites(ciphersuites);
			connection.connect();
			Result certificateResult = connection.getResult();
			String message = "Established TLS connection to " + candidate.getSamlUrl().getHost() + ", TLS handshake: " + connection.getHandshakeData() + ". " + certificateResult.getMessage();
			tlsFailure.setMessage(message);
			if (!GeneralConstants.DEBUG_MODE)
			{
				tlsFailure.setSuccess(certificateResult.wasSuccessful());
			}
			else
			{
				tlsFailure.setSuccess(true);
			}
			samlConnection = connection;
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not establish TLS connection to " + candidate.getSamlUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage(
					"Could not establish TLS connection to " + candidate.getSamlUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage() + ". See technical log for stacktrace");
		}
		finally
		{
			result.add(tlsFailure);
		}

		return samlConnection;
	}

	private TLSConnectionClient establishConnectionToEcardApiInterface(List<LogMessage> result, TestCandidate candidate, StepHandler handler, X509Certificate cert)
	{
		TLSConnectionClient eCardApiConnection = null;

		// check required KnownValues
		if (handler.getKnownValues().containsElement(Replaceable.PSK.toString()) && handler.getKnownValues().containsElement(Replaceable.SESSIONIDENTIFIER.toString()))
		{
			LogMessage tlsFailure = logMessageDAO.createNew();
			tlsFailure.setTestStepName("TLS eCardAPI initialization");
			try
			{
				// Open a TLS connection to the eCardAPI interface
				byte[] pskBytes = Hex.decodeHex(handler.getKnownValues().get(Replaceable.PSK.toString()).getValue().toCharArray());
				TLSConnectionClient connection = new TLSConnectionClient(cert, candidate.getEcardapiUrl().getHost(), candidate.getEcardapiUrl().getPort(),
						handler.getKnownValues().get(Replaceable.SESSIONIDENTIFIER.toString()).getValue().getBytes(), pskBytes);
				connection.setCipherSuites(getCipherSuites(candidate.getTlsEcardApiPsk()));
				connection.connect();
				Result certificateResult = connection.getResult();
				String message = "Established TLS connection to " + candidate.getEcardapiUrl().getHost() + ", TLS handshake: " + connection.getHandshakeData() + ". " + certificateResult.getMessage();
				tlsFailure.setMessage(message);
				if (!GeneralConstants.DEBUG_MODE)
				{
					tlsFailure.setSuccess(certificateResult.wasSuccessful());
				}
				else
				{
					tlsFailure.setSuccess(true);
				}
				eCardApiConnection = connection;
			}
			catch (Exception e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not establish TLS connection to " + candidate.getEcardapiUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
				tlsFailure.setSuccess(false);
				tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getEcardapiUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
						+ ". See technical log for stacktrace");
			}
			finally
			{
				result.add(tlsFailure);
			}
		}

		return eCardApiConnection;
	}

	// run a TLS-PSK test case, initialize via SOAP interface
	private void runTlsPskViaSoap(List<LogMessage> result, TestCandidate candidate, TestCase testCase, KnownValues values, Map<String, X509Certificate> x509Certificates,
			Certificate clientAuthCertificate, AsymmetricKeyParameter clientAuthKey)
	{
		int lastSoapStepIndex = runSoap(result, candidate, testCase, values, true, x509Certificates, false, clientAuthCertificate, clientAuthKey);

		if (lastSoapStepIndex > -1)
		{
			int firstTlsStepIndex = lastSoapStepIndex + 1;

			if (firstTlsStepIndex < testCase.getTestCaseSteps().size())
			{
				RunnerTLS runnerTls = new RunnerTLS(tlsTester);
				List<LogMessage> resultTls = runnerTls.runTest(candidate, testCase, values, clientAuthCertificate, clientAuthKey, firstTlsStepIndex);
				result.addAll(resultTls);
			}
		}
	}

	/**
	 * Run a SOAP test case
	 * 
	 * @param useOnlyEidInterface
	 *            if true, all steps that do not target the eID interface are skipped
	 * @return index of last processed step or -1 for error; if no step was skipped, this should be equal to <code>testCase.getTestCaseSteps().size() - 1</code>
	 */
	private int runSoap(List<LogMessage> result, TestCandidate candidate, TestCase testCase, KnownValues values, boolean useOnlyEidInterface, Map<String, X509Certificate> x509Certificates,
			boolean testAttached, Certificate clientAuthCertificate, AsymmetricKeyParameter clientAuthKey)
	{
		logger.debug("Starting Testcase using SOAP");

		int numCv = getNumberOfCvCertificates(testCase.getEservice());

		// create the handler
		StepHandler handler;
		if (testAttached)
		{
			handler = new StepHandlerSOAP(candidate.getCandidateName(), candidate.getAttachedTcTokenUrl(), GeneralConstants.TESTBED_REFRESH_URL, values, logMessageDAO, testCase.getEservice(),
					testCase.getCard(), numCv);
		}
		else
		{
			handler = new StepHandlerSOAP(candidate.getCandidateName(), candidate.getEcardapiUrl(), GeneralConstants.TESTBED_REFRESH_URL, candidate.getEidinterfaceUrl(), values, logMessageDAO,
					testCase.getEservice(), testCase.getCard(), numCv);
		}

		int lastProcessedStepIndex = processSteps(result, candidate, testCase, values, useOnlyEidInterface, handler, x509Certificates, clientAuthCertificate, clientAuthKey);

		logger.debug("Testcase using SOAP completed.");

		return lastProcessedStepIndex;
	}

	private X509Certificate findTlsServerCertificateForInterface(TargetInterfaceType targetInterface, Map<String, X509Certificate> x509Certificates)
	{
		String prefix;
		switch (targetInterface)
		{
			case E_CARD_API:
				prefix = "CERT_ECARD_TLS_EIDSERVER_1_";
				break;
			case E_ID_INTERFACE:
				prefix = "CERT_EID_TLS_EIDSERVER_1";
				break;
			case SAML:
				prefix = "CERT_ECARD_TLS_SAMLPROCESSOR_1_";
				break;
			case ATTACHED:
				prefix = "CERT_ECARD_TLS_EIDSERVER_1_ATTACHED_";
				break;
			default:
				prefix = null;
		}
		if (prefix != null)
		{
			for (String name : x509Certificates.keySet())
			{
				if (name.startsWith(prefix))
				{
					return x509Certificates.get(name);
				}
			}
		}
		return null;
	}

	private int processSteps(List<LogMessage> result, TestCandidate candidate, TestCase testCase, KnownValues values, boolean useOnlyEidInterface, StepHandler handler,
			Map<String, X509Certificate> x509Certificates, Certificate clientAuthCertificate, AsymmetricKeyParameter clientAuthKey)
	{
		TLSConnectionClient connection = null;
		TLSConnectionClient soapConnection = null;
		TLSConnectionClient eCardApiConnection = null;
		TLSConnectionClient samlConnection = null;
		TLSConnectionClient attachedConnection = null;

		List<TestCaseStep> testStepList = testCase.getTestCaseSteps();

		// loop over test steps
		int lastProcessedStepIndex = 0;
		for (int i = 0; i < testStepList.size(); i++)
		{
			TestCaseStep currentStep = testStepList.get(i);

			// check, if this method is only used to initialize a PSK channel -> if so, skip all steps that do not use the eID interface
			if (useOnlyEidInterface && currentStep.getTarget() != TargetInterfaceType.E_ID_INTERFACE)
				continue;

			lastProcessedStepIndex = i;

			// get the connection to the correct endpoint
			X509Certificate cert = findTlsServerCertificateForInterface(currentStep.getTarget(), x509Certificates);
			if (currentStep.getTarget() == TargetInterfaceType.E_ID_INTERFACE)
			{
				if (soapConnection == null || soapConnection.isClosed())
				{
					soapConnection = establishConnectionToSOAPInterface(result, candidate, cert, clientAuthCertificate, clientAuthKey);
				}
				connection = soapConnection;
			}
			else if (currentStep.getTarget() == TargetInterfaceType.E_CARD_API)
			{
				// check if are are running an attached test case
				if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.ESER_ATTACHED))
				{
					// note: in attached mode, the server also simulates a browser during the first step. therefore, after the response is received,
					// we need to reconnect to the server in order to simulate the usage of an eID-Client
					if (attachedConnection.isClosed() || lastProcessedStepIndex < 1)
					{
						attachedConnection = establishConnectionToAttachedServer(result, candidate, cert);
					}
					connection = attachedConnection;
				}
				else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML_ATTACHED))
				{
					if (samlConnection == null || samlConnection.isClosed())
					{
						samlConnection = establishConnectionToSAMLProcessor(result, candidate, cert);
					}
					connection = samlConnection;
				}
				// regular ecard api communication is required
				else
				{
					if (eCardApiConnection == null || eCardApiConnection.isClosed())
					{
						eCardApiConnection = establishConnectionToEcardApiInterface(result, candidate, handler, cert);
					}
					connection = eCardApiConnection;
				}
			}
			else if (currentStep.getTarget() == TargetInterfaceType.SAML)
			{
				if (samlConnection == null || samlConnection.isClosed())
				{
					samlConnection = establishConnectionToSAMLProcessor(result, candidate, cert);
				}
				connection = samlConnection;
			}
			else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.ESER_ATTACHED))
			{
				// note: this block is reached only once - for the very first connection to the website which is used to fetch the tc token url
				connection = establishConnectionToAttachedServer(result, candidate, cert);
			}
			else
			{
				LogMessage newTls = logMessageDAO.createNew();
				newTls.setTestStepName("No connection has been specified for test case step " + currentStep.getName());
				newTls.setSuccess(false);
				result.add(newTls);
				logger.error("No connection has been specified for test case step " + currentStep.getName());
				return -1;
			}

			if (null == connection)
			{
				LogMessage newTls = logMessageDAO.createNew();
				newTls.setTestStepName("No connection established for test case '" + testCase.getName() + "' during step '" + currentStep.getName() + "'");
				newTls.setSuccess(false);
				result.add(newTls);
				logger.error("No connection established for test case '" + testCase.getName() + "' during step '" + currentStep.getName() + "'");
				return -1;
			}

			// Check whether it is in or outbound
			if (currentStep.getInbound())
			{
				// Get Message from the server
				String fromServer = null;
				try
				{
					fromServer = connection.read();
					if (currentStep.getName().toUpperCase().startsWith(TestStepType.IN_SAML_ASSERTION.value().toUpperCase()))
					{
						if (fromServer != null && fromServer.length() > 0)
						{
							LogMessage acValidateMessage = ((StepHandlerSAML) handler).validateAssertion(fromServer, currentStep.getName());
							result.add(acValidateMessage);
						}
						else
						{
							LogMessage readFailure = logMessageDAO.createNew();
							readFailure.setTestStepName("SAML assertion readout");
							readFailure.setSuccess(false);
							if (fromServer.length() > 0)
							{
								readFailure.setMessage("Did not receive a valid assertion:" + System.getProperty("line.separator") + fromServer);
							}
							else
							{
								readFailure.setMessage("The server did not respond.");
							}
							result.add(readFailure);
						}
						break;
					}
					else
					{
						fromServer = followRedirects(connection, result, fromServer, currentStep.getName());
						// check if the response was ok
						if (fromServer.length() == 0)
						{
							LogMessage readFailure = logMessageDAO.createNew();
							readFailure.setTestStepName("HTTP read");
							readFailure.setSuccess(false);
							readFailure.setMessage("The server did not respond.");
							result.add(readFailure);
							break;
						}
					}
				}
				catch (IOException e)
				{
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.error("Could not read message from server: IO Exception" + System.getProperty("line.separator") + trace.toString());
					LogMessage readFailure = logMessageDAO.createNew();
					readFailure.setTestStepName("HTTP read");
					readFailure.setSuccess(false);
					readFailure.setMessage("Could not read message from server: IO Exception - " + e.getMessage() + ". See technical log for stacktrace");
					result.add(readFailure);
					break;
				}

				// get all alternative messages for this step
				TestCaseStep current = null;
				List<TestCaseStep> alternatives = new ArrayList<TestCaseStep>();
				alternatives.add(currentStep);
				int j = i + 2;
				if (currentStep.isOptional())
				{
					// add all following optional steps to alternatives
					while (j < testStepList.size() && (current = testStepList.get(j)).isOptional())
					{
						alternatives.add(current);
						j += 2;
					}
					// add first mandatory step to alternatives
					if (j < testStepList.size() && (!current.isOptional()))
					{
						alternatives.add(current);
					}
				}

				// validate
				List<LogMessage> validationLogMessages = handler.validateStep(fromServer, alternatives);

				// check if the last validation was successful. if not, add
				// all failed alternatives to the log
				if (validationLogMessages.get(validationLogMessages.size() - 1).getSuccess())
				{
					result.add(validationLogMessages.get(validationLogMessages.size() - 1));
				}
				else
				{
					result.addAll(validationLogMessages);
				}

				// did we have to skip some optional steps?
				if (handler.getStepsToSkip() > 0)
				{
					i += (handler.getStepsToSkip() * 2);
					handler.resetStepsToSkip();
				}
			}
			else
			{
				// if it is an outbound message, we generate the string
				String outBoundMsg = handler.generateMsg(currentStep.getMessage());

				// check if we have to repeat a step
				if (outBoundMsg.contains("OutputAPDU"))
				{
					i -= 2;
				}

				// Check whether sending was successful and generate an appropriate LogMessage
				LogMessage sendMsg = logMessageDAO.createNew();
				sendMsg.setTestStepName(currentStep.getName());
				if (connection.write(outBoundMsg) == 0)
				{
					sendMsg.setSuccess(true);
					String msg = "Sent to Server: " + outBoundMsg;
					String additional;
					if (null != (additional = handler.getAdditionalOutboundInfo()))
					{
						msg += System.getProperty("line.separator") + additional;
					}
					sendMsg.setMessage(msg);
					// logger.debug("Sent to Server: " + outBoundMsg);
					logger.debug("Outbound Step " + i + " (" + currentStep.getName() + ") completed successfully");
				}
				else
				{
					sendMsg.setSuccess(false);
					sendMsg.setMessage("Failure while sending to Server: " + outBoundMsg);
					logger.warn("Sending via TLS Connection failed: " + outBoundMsg);
				}
				result.add(sendMsg);
			}
		}

		// close connections
		if (soapConnection != null)
		{
			soapConnection.close();
		}
		if (eCardApiConnection != null)
		{
			eCardApiConnection.close();
		}
		if (samlConnection != null)
		{
			samlConnection.close();
		}
		if (attachedConnection != null)
		{
			attachedConnection.close();
		}

		return lastProcessedStepIndex;
	}

	// run a SAML testcase
	private int runSaml(List<LogMessage> result, TestCandidate candidate, TestCase testCase, KnownValues values, Map<String, X509Certificate> x509Certificates, Certificate clientAuthCertificate,
			AsymmetricKeyParameter clientAuthKey)
	{
		logger.debug("Starting Testcase using SAML");

		int numCv = getNumberOfCvCertificates(testCase.getEservice());

		// create the handler
		StepHandler handler = new StepHandlerSAML(candidate.getCandidateName(), candidate.getEcardapiUrl(), GeneralConstants.TESTBED_REFRESH_URL, candidate.getSamlUrl(), values, logMessageDAO,
				testCase.getEservice(), testCase.getCard(), numCv);

		int lastProcessedStepIndex = processSteps(result, candidate, testCase, values, false, handler, x509Certificates, clientAuthCertificate, clientAuthKey);

		logger.debug("SAML testcase completed.");

		return lastProcessedStepIndex;
	}

	// fill certificate map from profile data
	private Map<String, X509Certificate> loadCertificatesFromCandidate(TestCandidate candidate)
	{
		HashMap<String, X509Certificate> x509Certificates = new HashMap<>();
		if (candidate.getX509Certificates() != null)
		{
			for (CertificateX509 cert : candidate.getX509Certificates())
			{
				x509Certificates.put(cert.getCertificateName(), cert.getX509certificate());
			}
		}
		return x509Certificates;
	}

	// follow redirects, if necessary
	private String followRedirects(TLSConnectionClient client, List<LogMessage> result, String message, String stepName) throws IOException
	{
		// is it a redirect? if not just return the message itself
		if (message.startsWith("HTTP/1.1 302"))
		{
			// log the message
			LogMessage receivedToken = logMessageDAO.createNew();
			receivedToken.setTestStepName(stepName);
			receivedToken.setMessage("Response received from server: " + message);
			receivedToken.setSuccess(true);
			result.add(receivedToken);
			String lines[] = message.split("\\r?\\n");
			String newMessage;
			for (String line : lines)
			{
				if (line.startsWith("Location") || line.startsWith("URL"))
				{
					String newLocation = line.substring(line.indexOf("http"));
					client.write("GET " + newLocation + " HTTP/1.1" + GeneralConstants.HTTP_LINE_ENDING + "Content-Type: text/xml;charset=utf-8" + GeneralConstants.HTTP_LINE_ENDING
							+ GeneralConstants.HTTP_LINE_ENDING);
					newMessage = client.read();
					newMessage = followRedirects(client, result, newMessage, stepName);
					return newMessage;
				}
			}
		}
		return message;
	}

	/**
	 * Returns the amount of expected CV certificates from a list of base names.
	 * 
	 * @param {@link
	 * 			EService} service
	 * @return
	 */
	private int getNumberOfCvCertificates(EService service)
	{
		switch (service)
		{
			case A_2:
				return 4;
			default:
				return 3;
		}
	}

	/**
	 * This method returns the ciphersuites supported by the <i>highest</i> available TLS version.
	 * 
	 * @param input
	 * @return
	 */
	private int[] getCipherSuites(Set<Tls> input)
	{
		if (input != null && input.size() > 0)
		{
			// find the highest TLS version first
			Tls highest = null;
			for (Tls t : input)
			{
				if (t.getTlsVersion() == IcsTlsVersion.TL_SV_12)
				{
					highest = t;
					break;
				}
				else if (t.getTlsVersion() == IcsTlsVersion.TL_SV_11)
				{
					highest = t;
				}
				else if (t.getTlsVersion() == IcsTlsVersion.TL_SV_10 && highest.getTlsVersion() != IcsTlsVersion.TL_SV_11)
				{
					highest = t;
				}
				else if (highest.getTlsVersion() == null)
				{
					highest = t;
				}
			}

			try
			{
				return BcTlsHelper.convertCipherSuiteSetToIntArray(highest.getTlsCiphersuites());
			}
			catch (IllegalAccessException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Error while converting ciphersuites into RFC format: " + System.getProperty("line.separator") + trace.toString());
			}
		}
		// input was empty, return empty array as well
		return new int[] {};
	}

	private String getClientCertificateName(TestCase tc)
	{
		for (String baseName : tc.getCertificateBaseNames())
		{
			if (baseName.toUpperCase().startsWith("CERT_EID_TLS_ESERVICE_"))
			{
				return baseName;
			}
		}
		logger.warn("No TLS client certificate name found in the list of certificate base names used by the test case.");
		return null;
	}

	private Certificate loadTlsClientAuthenticationCertificate(TestCandidate candidate, TestCase testCase)
	{
		Certificate cert = null;
		try
		{
			CryptoHelper.Protocol port = CryptoHelper.Protocol.SOAP_PROT_ID;
			EService service = candidate.isMultiClientCapable() ? testCase.getEservice() : GeneralConstants.DEFAULT_ESERVICE;
			CryptoHelper.Algorithm alg = CryptoHelper.getAlgorithmFromService(service);
			X509Certificate x509Cert = CryptoHelper.loadSignatureCertificate(port, alg, service);
			org.bouncycastle.asn1.x509.Certificate bcCert = org.bouncycastle.asn1.x509.Certificate.getInstance(x509Cert.getEncoded());
			cert = new Certificate(new org.bouncycastle.asn1.x509.Certificate[] { bcCert });
		}
		catch (CertificateEncodingException ignore)
		{

		}
		return cert;
	}

	private AsymmetricKeyParameter loadTlsClientAuthenticationPrivateKey(TestCandidate candidate, TestCase testCase)
	{
		AsymmetricKeyParameter keyParam = null;
		try
		{
			CryptoHelper.Protocol prot = CryptoHelper.Protocol.SOAP_PROT_ID;
			EService service = candidate.isMultiClientCapable() ? testCase.getEservice() : GeneralConstants.DEFAULT_ESERVICE;
			CryptoHelper.Algorithm alg = CryptoHelper.getAlgorithmFromService(service);
			PrivateKey privateKey = CryptoHelper.loadSignatureKey(prot, alg, service);
			keyParam = PrivateKeyFactory.createKey(privateKey.getEncoded());
		}
		catch (IOException ignore)
		{
			StringWriter trace = new StringWriter();
			ignore.printStackTrace(new PrintWriter(trace));
			logger.error("Error while loading the client authentication certificate: " + System.getProperty("line.separator") + trace.toString());
		}
		return keyParam;
	}


	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.Runner#getStaticCertificatesAndKeys()
	 */
	@Override
	public Map<String, byte[]> getStaticCertificatesAndKeys() throws IOException
	{
		Map<String, byte[]> certificatesAndKeys = new HashMap<>();

		// x509 certificates
		insertFiles("certificates/CSCA", certificatesAndKeys);
		insertFiles("certificates/RSA", certificatesAndKeys);
		insertFiles("certificates/DSA", certificatesAndKeys);
		insertFiles("certificates/ECDSA", certificatesAndKeys);

		// x509 keys
		insertFiles("keys/RSA", certificatesAndKeys);
		insertFiles("keys/DSA", certificatesAndKeys);
		insertFiles("keys/ECDSA", certificatesAndKeys);

		// CV
		insertFiles("CVCertificates/certs", certificatesAndKeys);
		insertFiles("CVCertificates/desc", certificatesAndKeys);
		insertFiles("CVCertificates/keys", certificatesAndKeys);

		// x509 keys
		return certificatesAndKeys;
	}

	/**
	 * Insert all the files in a given subfolder into the given map. Does not work recusively.
	 * 
	 * @param path
	 * @param fileMap
	 * @throws IOException
	 */
	private void insertFiles(String path, Map<String, byte[]> fileMap) throws IOException
	{
		File folder = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
		for (File f : folder.listFiles())
		{
			if (f.isFile())
			{
				fileMap.put(path + "/" + f.getName(), IOUtils.toByteArray(new FileInputStream(f)));
			}
		}
	}

}
