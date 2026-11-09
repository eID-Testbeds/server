package com.secunet.eidserver.testbed.runner;

import org.bouncycastle.tls.Certificate;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.eidas.EidasUtils;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.exceptions.TestrunException;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Algorithm;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Protocol;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.beans.Runner;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.*;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.TargetInterfaceType;
import com.secunet.eidserver.testbed.tlsclient.TLSConnectionClient;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.tls.crypto.TlsCertificate;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCertificate;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Stateless
public class RunnerBean implements Runner
{
	private static final Logger logger = LogManager.getLogger(RunnerBean.class);

	@EJB
	private LogMessageDAO logMessageDAO;

	@Override
	public List<LogMessage> runTest(TestCandidate candidate, TestCase testCase) throws TestrunException
	{
		List<LogMessage> result = new ArrayList<>();

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
			if (testCase.getTestCaseSteps().isEmpty())
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


			if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML))
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
			// eIDAS middleware tests
			else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.EIDAS_MW))
			{
				if (null != candidate.getAttachedTcTokenUrl())
				{
					runEidas(result, candidate, testCase, values, x509Certificates, clientAuthCertificate, clientAuthKey);
				}
				else
				{
					LogMessage failMsg = logMessageDAO.createNew();
					failMsg.setSuccess(false);
					failMsg.setMessage("No eIDAS middleware URL has been provided for the given profile.");
					result.add(failMsg);
					logger.fatal("No eIDAS middleware URL has been provided for the given profile, aborting.");
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
			logger.error(e);
			throw new TestrunException(result, e);
		}
		return result;
	}


	private int runEidas(List<LogMessage> result, TestCandidate candidate, TestCase testCase, KnownValues values, Map<String, X509Certificate> x509Certificates, Certificate clientAuthCertificate,
			AsymmetricKeyParameter clientAuthKey)
	{
		logger.debug("Starting eIDAS middleware testcase");

		// create the handler
		StepHandler handler = new StepHandlerEidas(candidate, values, logMessageDAO, testCase.getEservice(), testCase.getCard(),
				getNumberOfCvCertificates(testCase.getEservice()));

		int lastProcessedStepIndex = processSteps(result, candidate, testCase, values, false, handler, x509Certificates, clientAuthCertificate, clientAuthKey);

		logger.debug("eIDAS middleware testcase completed");

		return lastProcessedStepIndex;

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
	private TLSConnectionClient establishConnectionToSOAPInterface(List<LogMessage> result, TestCandidate candidate, Certificate clientAuthCertificate, AsymmetricKeyParameter clientAuthKey)
	{
		TLSConnectionClient soapConnection = null;

		try
		{
			// TLSConnectionClient connection = new TLSConnectionClient(cert, candidate.getEidinterfaceUrl().getHost(), candidate.getEidinterfaceUrl().getPort());
			PrivateKey pk = (PrivateKey) clientAuthKey;
			soapConnection = new TLSConnectionClient(candidate.getEidinterfaceUrl().getHost(), candidate.getEidinterfaceUrl().getPort(), clientAuthCertificate.getCertificateAt(0).getEncoded(), pk.getEncoded());
			soapConnection.connect();
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not establish TLS connection to " + candidate.getEidinterfaceUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			LogMessage tlsFailure = logMessageDAO.createNew();
			tlsFailure.setTestStepName("TLS SOAP initialization");
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getEidinterfaceUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
					+ ". See technical log for stacktrace");
			result.add(tlsFailure);
		}

		return soapConnection;
	}

	// Open a TLS connection to the attached server
	private TLSConnectionClient establishConnectionToAttachedServer(List<LogMessage> result, TestCandidate candidate)
	{
		TLSConnectionClient attachedServerConnection = null;

		try
		{
			attachedServerConnection = new TLSConnectionClient(candidate.getAttachedTcTokenUrl().getHost(), candidate.getAttachedTcTokenUrl().getPort());
			attachedServerConnection.connect();
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error(
					"Could not establish TLS connection to " + candidate.getAttachedTcTokenUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			LogMessage tlsFailure = logMessageDAO.createNew();
			tlsFailure.setTestStepName("TLS attached initialization");
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getAttachedTcTokenUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
					+ ". See technical log for stacktrace");
			result.add(tlsFailure);
		}

		return attachedServerConnection;
	}

	// Open a TLS connection to the SAML interface
	private TLSConnectionClient establishConnectionToSAMLProcessor(List<LogMessage> result, TestCandidate candidate)
	{
		TLSConnectionClient samlConnection = null;

		try
		{
			TLSConnectionClient connection = new TLSConnectionClient(candidate.getSamlUrl().getHost(), candidate.getSamlUrl().getPort());
			connection.connect();
			samlConnection = connection;
		}
		catch (Exception e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not establish TLS connection to " + candidate.getSamlUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
			LogMessage tlsFailure = logMessageDAO.createNew();
			tlsFailure.setTestStepName("TLS SAML initialization");
			tlsFailure.setSuccess(false);
			tlsFailure.setMessage(
					"Could not establish TLS connection to " + candidate.getSamlUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage() + ". See technical log for stacktrace");
		}

		return samlConnection;
	}

	private TLSConnectionClient establishConnectionToEcardApiInterface(List<LogMessage> result, TestCandidate candidate, StepHandler handler)
	{
		TLSConnectionClient eCardApiConnection = null;

		// check required KnownValues
		if (handler.getKnownValues().containsElement(Replaceable.PSK.toString()) && handler.getKnownValues().containsElement(Replaceable.SESSIONIDENTIFIER.toString()))
		{
			try
			{
				// Open a TLS connection to the eCardAPI interface
				byte[] pskBytes = Hex.decodeHex(handler.getKnownValues().get(Replaceable.PSK.toString()).getValue().toCharArray());
				TLSConnectionClient connection = new TLSConnectionClient(candidate.getEcardapiUrl().getHost(), candidate.getEcardapiUrl().getPort(),
						handler.getKnownValues().get(Replaceable.SESSIONIDENTIFIER.toString()).getValue().getBytes(), pskBytes);
				connection.connect();
				eCardApiConnection = connection;
			}
			catch (Exception e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not establish TLS connection to " + candidate.getEcardapiUrl().getHost() + ": " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
				LogMessage tlsFailure = logMessageDAO.createNew();
				tlsFailure.setTestStepName("TLS eCardAPI initialization");
				tlsFailure.setSuccess(false);
				tlsFailure.setMessage("Could not establish TLS connection to " + candidate.getEcardapiUrl().getHost() + ": " + e.getClass().getName() + " - " + e.getMessage()
						+ ". See technical log for stacktrace");
			}
		}

		return eCardApiConnection;
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
		StepHandler handler = new StepHandlerSOAP(candidate, GeneralConstants.TESTBED_REFRESH_URL, values, logMessageDAO, testCase.getEservice(),
				testCase.getCard(), numCv, testAttached);

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
				prefix = "CERT_ECARD_TLS_EIDSERVER_ATTACHED_1_";
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
			if (currentStep.getTarget() == TargetInterfaceType.E_ID_INTERFACE)
			{
				if (soapConnection == null || soapConnection.isClosed())
				{
					soapConnection = establishConnectionToSOAPInterface(result, candidate, clientAuthCertificate, clientAuthKey);
				}
				connection = soapConnection;
			}
			else if (currentStep.getTarget() == TargetInterfaceType.ATTACHED)
			{
				// note: in attached mode, the server also simulates a browser during the first step. therefore, after the response is received,
				// we need to reconnect to the server in order to simulate the usage of an eID-Client
				if (attachedConnection == null || attachedConnection.isClosed() || lastProcessedStepIndex < 1)
				{
					attachedConnection = establishConnectionToAttachedServer(result, candidate);
				}
				connection = attachedConnection;
			}
			else if (currentStep.getTarget() == TargetInterfaceType.E_CARD_API)
			{
				// check if are are running an attached test case
				if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.ESER_ATTACHED) || testCase.getOptionalProfiles().contains(IcsOptionalprofile.EIDAS_MW))
				{
					// note: in attached mode, the server also simulates a browser during the first step. therefore, after the response is received,
					// we need to reconnect to the server in order to simulate the usage of an eID-Client
					if (attachedConnection == null || attachedConnection.isClosed())
					{
						attachedConnection = establishConnectionToAttachedServer(result, candidate);
						// TODO the attached connection may only be resumed, never reestablished according to TR-03130
					}
					connection = attachedConnection;
				}
				else if (testCase.getOptionalProfiles().contains(IcsOptionalprofile.SAML_ATTACHED))
				{
					if (samlConnection == null || samlConnection.isClosed())
					{
						samlConnection = establishConnectionToSAMLProcessor(result, candidate);
					}
					connection = samlConnection;
				}
				// regular ecard api communication is required
				else
				{
					if (eCardApiConnection == null || eCardApiConnection.isClosed())
					{
						eCardApiConnection = establishConnectionToEcardApiInterface(result, candidate, handler);
					}
					connection = eCardApiConnection;
				}
			}
			else if (currentStep.getTarget() == TargetInterfaceType.SAML)
			{
				if (samlConnection == null || samlConnection.isClosed())
				{
					samlConnection = establishConnectionToSAMLProcessor(result, candidate);
				}
				connection = samlConnection;
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

				if (outBoundMsg.startsWith("TLS_RENEGOTIATE_")) {
					connection.close();
					try {
						connection.connect();
					} catch (Exception e) {
						LogMessage sendMsg = logMessageDAO.createNew();
						sendMsg.setTestStepName(currentStep.getName());
						sendMsg.setSuccess(!outBoundMsg.equals("TLS_RENEGOTIATE_TRUE"));
						sendMsg.setMessage("Failure while reconnecting to Server(" + connection.getTargetInfo() + ")");
						logger.warn("Sending via TLS Connection failed: " + outBoundMsg);
						result.add(sendMsg);
					}
					continue;
				}

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
					String msg = "Sent to Server(" + connection.getTargetInfo() + "): " + outBoundMsg;
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
					sendMsg.setMessage("Failure while sending to Server(" + connection.getTargetInfo() + "): " + outBoundMsg);
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
		StepHandler handler = new StepHandlerSAML(candidate, GeneralConstants.TESTBED_REFRESH_URL, values, logMessageDAO,
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
		if (message.startsWith("HTTP/1.1 302") || message.startsWith("HTTP/1.1 307"))
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
				return 3;
			default:
				return 2;
		}
	}

	private Certificate loadTlsClientAuthenticationCertificate(TestCandidate candidate, TestCase testCase)
	{
		Certificate cert = null;
		try
		{
			Protocol port = Protocol.SOAP_PROT_ID;
			EService service = candidate.isMultiClientCapable() ? testCase.getEservice() : GeneralConstants.DEFAULT_ESERVICE;
			// TODO ECDSA does not work for the EU Middleware, remove this workaround once it is fixed
			if (service == EService.F || service == EService.EECDSA)
			{
				service = EService.A;
			}
			Algorithm alg = CryptoHelper.getAlgorithmFromService(service);
			X509Certificate x509Cert = CryptoHelper.loadCertificate(port, alg, service.toString() + "_clientauth", candidate);
			BcTlsCertificate bcCert = new BcTlsCertificate(new BcTlsCrypto(new SecureRandom()), x509Cert.getEncoded());
			cert = new Certificate(new TlsCertificate[] { bcCert });
		}
		catch (CertificateEncodingException | IOException ignore)
		{
			StringWriter trace = new StringWriter();
			ignore.printStackTrace(new PrintWriter(trace));
			logger.error("Error while loading the client authentication certificate: " + System.getProperty("line.separator") + trace.toString());
		}
		return cert;
	}

	private AsymmetricKeyParameter loadTlsClientAuthenticationPrivateKey(TestCandidate candidate, TestCase testCase)
	{
		AsymmetricKeyParameter keyParam = null;
		try
		{
			Protocol prot = Protocol.SOAP_PROT_ID;
			EService service = candidate.isMultiClientCapable() ? testCase.getEservice() : GeneralConstants.DEFAULT_ESERVICE;
			// TODO ECDSA does not work for the EU Middleware, remove this workaround once it is fixed
			if (service == EService.F || service == EService.EECDSA)
			{
				service = EService.A;
			}
			Algorithm alg = CryptoHelper.getAlgorithmFromService(service);
			PrivateKey privateKey = CryptoHelper.loadKey(prot, alg, service.toString() + "_clientauth", candidate);
			keyParam = PrivateKeyFactory.createKey(privateKey.getEncoded());
		}
		catch (IOException ignore)
		{
			StringWriter trace = new StringWriter();
			ignore.printStackTrace(new PrintWriter(trace));
			logger.error("Error while loading the client authentication key: " + System.getProperty("line.separator") + trace.toString());
		}
		return keyParam;
	}

	/**
	 * Retrieves the signature credentials as an X509Credential object for the given protocol, algorithm, and service.
	 * Loads a certificate and a private key from a specified location based on the algorithm and service.
	 * The method expects the private key to be stored in PEM format.
	 *
	 * @param protocol The protocol used.
	 * @param algorithm The cryptographic algorithm.
	 * @param service The service for which credentials are needed.
	 * @return An X509Credential object containing the loaded certificate and private key.
	 * @throws IOException If there's an issue reading the key file.
	 * @throws UnrecoverableKeyException If the key cannot be recovered.
	 * @throws CertificateException If there's an issue with the certificate.
	 * @throws KeyStoreException If there's an issue with the keystore.
	 * @throws NoSuchAlgorithmException If the algorithm is not supported.
	 */
	@Override
	public X509Credential getSignatureCreds(Protocol protocol, Algorithm algorithm, EService service, TestCandidate candidate) throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException {

		X509Certificate signCertificate = CryptoHelper.loadCertificate(protocol, algorithm, service.value(), candidate);
		PrivateKey signKey = null;

		try (InputStream input = new ByteArrayInputStream(CryptoHelper.getCryptoFileMap(candidate).get("keys/" + algorithm.getAlgorithmName() + "/" + service.value() + ".pem")); InputStreamReader isr = new InputStreamReader(input);
			 PEMParser pemParser = new PEMParser(isr);)
		{
			Object parsed = pemParser.readObject();

			PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) parsed;
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			signKey = converter.getPrivateKey(privateKeyInfo);
		}
		return new BasicX509Credential(signCertificate,signKey);
	}

	/**
	 * Retrieves the encryption credentials as an X509Credential object for the given protocol, algorithm, and service.
	 * This method specifically reads a PKCS12 key store to extract the certificate and private key used for encryption.
	 *
	 * @param protocol The protocol used.
	 * @param algorithm The cryptographic algorithm, expected to be RSA for encryption.
	 * @param service The service for which encryption credentials are needed.
	 * @return An X509Credential object containing the loaded certificate and private key for encryption.
	 * @throws UnrecoverableKeyException If the private key cannot be recovered.
	 * @throws CertificateException If there's an issue with the certificate.
	 * @throws KeyStoreException If there's an issue with the keystore.
	 * @throws NoSuchAlgorithmException If the algorithm is not supported.
	 * @throws IOException If there's an issue reading the key store file.
	 */
	@Override
	public X509Credential getEncryptionCreds(Protocol protocol, Algorithm algorithm, EService service, TestCandidate candidate) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
		InputStream encryptionInput = new ByteArrayInputStream(CryptoHelper.getCryptoFileMap(candidate).get("keystores/" + Algorithm.RSA_ALG_ID.getAlgorithmName() + "/" + service.value() + GeneralConstants.ENCRYPTION_SUFFIX + ".p12"));

		EidasUtils.X509KeyPair encryptionPair = EidasUtils.readPKCS12(encryptionInput, "123456".toCharArray(), "ENC");
		return new BasicX509Credential(encryptionPair.getCert(), encryptionPair.getKey());
	}

}
