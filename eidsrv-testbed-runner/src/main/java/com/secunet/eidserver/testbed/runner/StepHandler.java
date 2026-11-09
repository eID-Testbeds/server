package com.secunet.eidserver.testbed.runner;

import com.secunet.eidserver.testbed.common.classes.EidCards;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.*;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import de.bsi.testbedutils.cvc.cvcertificate.CVCertificate;
import de.bsi.testbedutils.cvc.cvcertificate.DataBuffer;
import de.bsi.testbedutils.cvc.cvcertificate.exception.*;
import de.bsi.testbedutils.utilities.JaxBUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class StepHandler
{
	private static final Logger logger = LogManager.getLogger(StepHandler.class);
	protected final LogMessageDAO logMessageDAO;

	// additional outbound data
	protected String additionalOutboundData;

	// the known variables regarding this run
	protected KnownValues knownValues;

	// the known certificates
	protected Map<String, CVCertificate> knownCvCertificates;

	// abort the test?
	private boolean abort = false;

	// last parsing result
	private String schemaParsingResult = "";

	// the eservice used to run this test
	protected final EService service;

	// the eid card defintion to use
	private final EidCard card;

	// if necessary, this counter is incremented to indicate that steps have to
	// be skipped
	private int stepsToSkip = 0;

	private final int expectedNumOfCv;

	protected TestCandidate candidate;

	// the instance used to valide message elements that need computations
	protected ComputationHelper computationHelper;

	protected StepHandler(TestCandidate candidate, KnownValues values, LogMessageDAO logMessageDAO, final EService service, final EidCard card, final int expectedNumOfCv)
	{
		this.candidate = candidate;
        this.knownValues = Objects.requireNonNullElseGet(values, KnownValues::new);
		this.logMessageDAO = logMessageDAO;
		this.card = card;
		this.service = service;
		this.expectedNumOfCv = expectedNumOfCv;
	}

	// fill values that are already known
	protected void fillCommonData(TestCandidate candidate, String testbedRefreshAddress, EidCard card)
	{
		knownValues.add(new KnownValue(Replaceable.USER_AGENT.toString(),
				(GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "." + GeneralConstants.USER_AGENT_SUBMINOR)));
		knownValues.add(new KnownValue(Replaceable.TESTBED_USER_AGENT_NAME.toString(), GeneralConstants.USER_AGENT_NAME));
		if (candidate.getEcardapiUrl() != null)
		{
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_URL.toString(), candidate.getEcardapiUrl().toString()));
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_HOSTNAME.toString(), candidate.getEcardapiUrl().getHost()));
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_PATH.toString(), getNonEmptyPath(candidate.getEcardapiUrl())));
		}
		knownValues.add(new KnownValue(Replaceable.TESTBED_REFRESH_ADDRESS.toString(), testbedRefreshAddress));
		// user agent numbers are int values, need to append an empty string
		knownValues.add(new KnownValue(Replaceable.TESTBED_USER_AGENT_MAJOR.toString(), GeneralConstants.USER_AGENT_MAJOR + ""));
		knownValues.add(new KnownValue(Replaceable.TESTBED_USER_AGENT_MINOR.toString(), GeneralConstants.USER_AGENT_MINOR + ""));
		knownValues.add(new KnownValue(Replaceable.TESTBED_USER_AGENT_SUBMINOR.toString(), GeneralConstants.USER_AGENT_SUBMINOR + ""));
		// random elements
		knownValues.add(new KnownValue(Replaceable.CHALLENGE.toString(), GeneralConstants.CHALLENGE));
		knownValues.add(new KnownValue(Replaceable.IDPICC.toString(), GeneralConstants.IDPICC));
		knownValues.add(new KnownValue(Replaceable.NONCE.toString(), GeneralConstants.NONCE));
		// data files
		knownValues.add(new KnownValue(Replaceable.EFCARDACCESS.toString(), EidCards.cardAccess(card)));
		knownValues.add(new KnownValue(Replaceable.EFCARDSECURITY.toString(), EidCards.cardSecurity(card)));
		// CA private key
		knownValues.add(new KnownValue(Replaceable.CA_PRIVATE.toString(), EidCards.caPrivate(card)));

		// optional: add default values for variables used to extract data from the card
		fillIfNonexistant(Replaceable.DOCUMENT_VALIDITY.toString(), GeneralConstants.DEFAULT_DOCUMENT_VALIDITY);
		fillIfNonexistant(Replaceable.RCHATMANDATORY.toString(), "false");
		fillIfNonexistant(Replaceable.OCHATMANDATORY.toString(), "false");
		fillIfNonexistant(Replaceable.AGE_COMPARISON_VALUE.toString(), GeneralConstants.AGE_COMPARISON_VALUE);
		fillIfNonexistant(Replaceable.PLACE_COMPARISON_VALUE.toString(), GeneralConstants.PLACE_COMPARISON_VALUE);
		for (RequestAttribute ra : RequestAttribute.values())
		{
			fillIfNonexistant(GeneralConstants.REQUESTED_PREFIX + ra.toString().toUpperCase(), GeneralConstants.PERMISSION_PROHIBITED);
		}
		for (SpecialFunction sf : SpecialFunction.values())
		{
			fillIfNonexistant(GeneralConstants.REQUESTED_PREFIX + sf.toString().toUpperCase(), GeneralConstants.PERMISSION_PROHIBITED);
		}

		this.candidate = candidate;
	}

	protected void fillDataToRequest()
	{
		// fill the values for the operations allowed by user map
		for (RequestAttribute ra : RequestAttribute.values())
		{
			if (GeneralConstants.PERMISSION_REQUIRED.equals(knownValues.get(GeneralConstants.REQUESTED_PREFIX + ra.toString().toUpperCase()).getValue())
					|| GeneralConstants.PERMISSION_OPTIONAL.equals(knownValues.get(GeneralConstants.REQUESTED_PREFIX + ra.toString().toUpperCase()).getValue()))
			{
				fillIfNonexistant(GeneralConstants.ALLOWED_BY_USER_PREFIX + ra.toString().toUpperCase(), GeneralConstants.PERMISSION_ALLOWED);
			}
			else
			{
				fillIfNonexistant(GeneralConstants.ALLOWED_BY_USER_PREFIX + ra.toString().toUpperCase(), GeneralConstants.PERMISSION_PROHIBITED);
			}
		}
		for (SpecialFunction sf : SpecialFunction.values())
		{
			if (knownValues.containsElement((GeneralConstants.REQUESTED_PREFIX + sf.toString().toUpperCase()))
					&& (GeneralConstants.PERMISSION_REQUIRED.equals(knownValues.get(GeneralConstants.REQUESTED_PREFIX + sf.toString().toUpperCase()).getValue())
							|| GeneralConstants.PERMISSION_OPTIONAL.equals(knownValues.get(GeneralConstants.REQUESTED_PREFIX + sf.toString().toUpperCase()).getValue())))
			{
				fillIfNonexistant(GeneralConstants.ALLOWED_BY_USER_PREFIX + sf.toString().toUpperCase(), GeneralConstants.PERMISSION_ALLOWED);
			}
			else
			{
				fillIfNonexistant(GeneralConstants.ALLOWED_BY_USER_PREFIX + sf.toString().toUpperCase(), GeneralConstants.PERMISSION_PROHIBITED);
			}
		}
	}

	protected void fillIfNonexistant(String item, String value)
	{
		if (!knownValues.containsElement(item))
		{
			knownValues.add(new KnownValue(item, value));
		}
	}

	/**
	 * Generate the message that will be sent to the server using an XML step definition
	 * 
	 * @param generateFrom Source message
	 * @return Generated message
	 * @throws InvalidTestCaseDescriptionException If the test case description is invalid
	 * @throws EphemeralKeyNotFoundException If the ephemeral key is not found
	 * @throws SharedSecretNotYetReadyException If the shared secret is not yet ready
	 */
	protected abstract String generateMsg(String generateFrom) throws InvalidTestCaseDescriptionException, EphemeralKeyNotFoundException, SharedSecretNotYetReadyException;

	/**
	 * Generate the message that will be sent to the server
	 * 
	 * @param httpHeader HTTP header
	 * @param protocolBody Protocol body
	 * @return Generated message
	 * @throws InvalidTestCaseDescriptionException If the test case description is invalid
	 * @throws EphemeralKeyNotFoundException If the ephemeral key is not found
	 * @throws SharedSecretNotYetReadyException If the shared secret is not yet ready
	 */
	protected String generateMsg(String httpHeader, String protocolBody) throws InvalidTestCaseDescriptionException, EphemeralKeyNotFoundException, SharedSecretNotYetReadyException
	{
		// concat header and body
		String returnMsg;
		if (null != protocolBody && !protocolBody.isEmpty())
		{
			returnMsg = httpHeader + GeneralConstants.HTTP_LINE_ENDING + GeneralConstants.HTTP_LINE_ENDING + protocolBody;
		}
		else
		{
			returnMsg = httpHeader + GeneralConstants.HTTP_LINE_ENDING + GeneralConstants.HTTP_LINE_ENDING;
		}

		// generate path of RefreshAddress for HTTP GET messages
		if (returnMsg.contains(Functional.REFRESHADDRESS_PATH.getTextMark()))
		{
			returnMsg = returnMsg.replace(Functional.REFRESHADDRESS_PATH.getTextMark(), getRefreshAddressPath());
		}
		// generate uuids
		if (returnMsg.contains(Functional.UUID.getTextMark()))
		{
			returnMsg = returnMsg.replace(Functional.UUID.getTextMark(), UUID.randomUUID().toString());
		}
		// generate chat
		if (returnMsg.contains(Functional.CHAT.getTextMark()))
		{
			String chat = CHATOperations.computeCHAT(knownValues);
			returnMsg = returnMsg.replace(Functional.CHAT.getTextMark(), chat);
		}
		// generate authentication token
		if (returnMsg.contains(Functional.AUTHENTICATION_TOKEN.getTextMark()))
		{
            String authenticationToken = computationHelper.computeAuthenticationToken(knownValues.get(Replaceable.EPHEMERALPUBLICKEY.toString()).getValue());
            if (authenticationToken != null)
            {
                returnMsg = returnMsg.replace(Functional.AUTHENTICATION_TOKEN.getTextMark(), authenticationToken);
            }
        }
		// insert responseapdus
		if (returnMsg.contains(Functional.APDU.getTextMark()))
		{
			// compute the apdus
			returnMsg = returnMsg.replace(Functional.APDU.getTextMark(), computationHelper.getPreviousApdus());
		}
		// do we have to compute the charlength?
		returnMsg = returnMsg.replaceAll("(\r)?\n", "\r\n");
		if (returnMsg.contains(Functional.CHARLENGTH.getTextMark()))
		{
			returnMsg = returnMsg.replace(Functional.CHARLENGTH.getTextMark(), "" + getCharlength(returnMsg));
		}

		return returnMsg;
	}

	// compute the charlength of the message body
	private int getCharlength(String message)
	{
		String body = "";
		// try all kinds of delimiters
		int delimiterPosition = message.indexOf(GeneralConstants.HTTP_LINE_ENDING + GeneralConstants.HTTP_LINE_ENDING);
		if (delimiterPosition == -1)
		{
			delimiterPosition = message.indexOf("\n\n");
			// fall back to xml only
			if (delimiterPosition == -1)
			{
				delimiterPosition = message.indexOf("<?xml");
				if (delimiterPosition == -1)
				{
					logger.warn("Could not compute the proper message length of the outgoing message.");
				}
				else
				{
					body = body.substring(delimiterPosition);
				}
			}
			else
			{
				body = message.substring(delimiterPosition + ("\n\n".length()));
			}
		}
		else
		{
			body = message.substring(delimiterPosition + (2 * GeneralConstants.HTTP_LINE_ENDING.length()));
		}
		return body.length();
	}

	// validate a given message step
	@SuppressWarnings("rawtypes")
	protected List<LogMessage> validateStep(String receivedMessage, List<TestCaseStep> alternatives)
	{
		LogMessage result = logMessageDAO.createNew();
		result.setTestStepName(alternatives.get(0).getName());
		result.setSuccess(true);
		String endResult = "Processing step " + alternatives.get(0).getName() + ". Message was:" + System.lineSeparator();
		endResult += receivedMessage + System.lineSeparator();
		StringBuilder resultMessage = new StringBuilder("Results: " + System.lineSeparator());
		int resultMessageLength = resultMessage.length();

		// split into http and body
		String[] messageComponents = receivedMessage.split(GeneralConstants.HTTP_LINE_ENDING + GeneralConstants.HTTP_LINE_ENDING);
		// read http headers and status code
		Map<String, String> httpHeaders = getHttpHeaders(messageComponents[0]);

		// load step from the message value
		String toTest = alternatives.get(0).getMessage();
		toTest = insertValues(toTest);
		Step step = JaxBUtil.unmarshal(toTest, Step.class);

		// validate http tokens
		for (StepToken htoken : step.getHttpStepToken())
		{
			// does the received message contain the token?
			if (httpHeaders.containsKey(htoken.getName()))
			{
				if (!httpHeaders.get(htoken.getName()).equals(htoken.getValue()))
				{
					if (htoken.isIsMandatory())
					{
						result.setSuccess(false);
					}
					resultMessage.append("Wrong HTTP token ").append(htoken.getName()).append(". Expected value was ").append(htoken.getValue()).append(", received ").append(httpHeaders.get(htoken.getName())).append(".").append(System.lineSeparator());
				}
			}
			else
			{
				// the message did not contain the token
				if (htoken.isIsMandatory())
				{
					result.setSuccess(false);
				}
				resultMessage.append("Missing HTTP token ").append(htoken.getName()).append(".").append(System.lineSeparator());
			}
		}

		if (httpHeaders.get("HTTP_STATUS_CODE").startsWith("HTTP/1.1 200") && messageComponents.length > 1 && !messageComponents[1].isEmpty())
		{
			// check if we operate in attached mode
			if (alternatives.get(0).getName().startsWith(TestStepType.IN_ATTACHED_WEBPAGE.value()))
			{
				logger.debug("IN_ATTACHED_WEBPAGE");
				String sessionCookie = parseSessionCookie(receivedMessage);
				if (sessionCookie != null)
				{
					String jsessionid_cookiewrapper = "\nCookie: ";
					knownValues.add(new KnownValue(Replaceable.ATTACHED_JSESSIONID.toString(), sessionCookie));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_JSESSIONID_COOKIEWRAPPER.toString(), jsessionid_cookiewrapper));
					resultMessage.append("Received session cookie: ").append(sessionCookie);
				}
				else
				{
					String jsessionid_cookiewrapper = "";
					knownValues.add(new KnownValue(Replaceable.ATTACHED_JSESSIONID.toString(), ""));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_JSESSIONID_COOKIEWRAPPER.toString(), jsessionid_cookiewrapper));
					resultMessage.append("Could not parse a session cookie from the received message");
				}


				URL tcTokenUrl = parseTcTokenUrl(receivedMessage);
				if (tcTokenUrl != null)
				{
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN.toString(), tcTokenUrl.toString()));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN_HOSTNAME.toString(), tcTokenUrl.getHost()));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN_PATH.toString(), getNonEmptyPath(tcTokenUrl)));
					resultMessage.append("Received TC Token URL: ").append(tcTokenUrl);
				}
				else
				{
					resultMessage.append("Could not parse a valid TC Token URL from the received message");
					result.setSuccess(false);
				}
			}
			else
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder;
				try
				{
					// load schema file to validate against, if provided
					boolean schemaValidated = false;
					if (step.getSchema() != null && !step.getSchema().isEmpty())
					{
						InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/" + step.getSchema());
						if (input != null)
						{
							SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
							Schema schema = sf.newSchema(new StreamSource(input));
							factory.setSchema(schema);
							schemaValidated = true;
						}
						else
						{
							resultMessage.append("The schema specified schema file named ").append(step.getSchema()).append(" could not be loaded.").append(System.lineSeparator());
						}
					}

					builder = factory.newDocumentBuilder();
					builder.setErrorHandler(new ErrorHandler() {
						@Override
						public void warning(SAXParseException exception) {
							schemaParsingResult += ("[Warning] " + exception.getMessage() + System.lineSeparator());
						}

						@Override
						public void fatalError(SAXParseException exception) {
							schemaParsingResult += ("[Fatal error] " + exception.getMessage() + System.lineSeparator());
						}

						@Override
						public void error(SAXParseException exception) {
							schemaParsingResult += ("[Error] " + exception.getMessage() + System.lineSeparator());
						}
					});
					Document document = builder.parse(new InputSource(new StringReader(messageComponents[1])));

					// save parsing error message, if we have got any
					if (schemaValidated)
					{
						if (!schemaParsingResult.isEmpty())
						{
							resultMessage.append("Validation results against the schema ").append(step.getSchema()).append(":").append(System.lineSeparator());
							resultMessage.append(schemaParsingResult);
							schemaParsingResult = "";
						}
						else
						{
							resultMessage.append("Successfully validated the message against the schema ").append(step.getSchema()).append(".").append(System.lineSeparator());
						}
					}

					// validate protocol tokens
					Result res = checkTokens(step.getProtocolStepToken(), document);
					if (result.getSuccess())
					{
						result.setSuccess(res.wasSuccessful());
					}
					resultMessage.append(res.getMessage());

					// save the data necessary to compute future steps
					resultMessage.append(saveData(step, document, messageComponents[1]));

					// check if this is a EAC1 InputType message
					if (alternatives.get(0).getName().startsWith(TestStepType.IN_DID_AUTHENTICATE_EAC_1_INPUTTYPE.value()))
					{
						logger.debug("IN_DID_AUTHENTICATE_EAC_1_INPUTTYPE");
						String savingResult = saveCVcertificates(document);
						if (savingResult != null)
						{
							resultMessage.append(savingResult).append(System.lineSeparator());
						}
						computationHelper = new ComputationHelper(knownValues.get(Replaceable.EFCARDACCESS.toString()).getValue(), knownValues.get(Replaceable.NONCE.toString()).getValue(),
								knownValues.get(Replaceable.CA_PRIVATE.toString()).getValue());
						Result cvValidationResult = computationHelper.validateCVcertificates(knownCvCertificates, expectedNumOfCv, service, candidate);
						if (result.getSuccess())
						{
							boolean temp = cvValidationResult.wasSuccessful();
							result.setSuccess(temp);
						}
						resultMessage.append(cvValidationResult.getMessage()).append(System.lineSeparator());
						Result descriptionResult = validateCertificateDescription(document);
						if (result.getSuccess())
						{
							result.setSuccess(descriptionResult.wasSuccessful());
						}
						resultMessage.append(descriptionResult.getMessage()).append(System.lineSeparator());
					}
					// check if this is a EAC2 InputType message
					else if (alternatives.get(0).getName().startsWith(TestStepType.IN_DID_AUTHENTICATE_EAC_2_INPUTTYPE.value()))
					{
						logger.debug("IN_DID_AUTHENTICATE_EAC_2_INPUTTYPE");
						NodeList nodes = getElementsByLocalTagName(document, "Signature");
						if (nodes.getLength() > 0)
						{
							Result signatureResult = validateSignature(nodes);
							if (result.getSuccess())
							{
								result.setSuccess(signatureResult.wasSuccessful());
							}
							resultMessage.append(signatureResult.getMessage());
							// skip additionalinputtype
							this.stepsToSkip = 1;
						}
					}
					// check if this is an EAC AdditionalInputType message
					else if (alternatives.get(0).getName().startsWith(TestStepType.IN_DID_AUTHENTICATE_EACADDITIONALINPUTTYPE.value()))
					{
						logger.debug("IN_DID_AUTHENTICATE_EACADDITIONALINPUTTYPE");
						Result signatureResult = validateSignature(getElementsByLocalTagName(document, "Signature"));
						if (result.getSuccess())
						{
							result.setSuccess(signatureResult.wasSuccessful());
						}
						resultMessage.append(signatureResult.getMessage());
					}
					// check if this is a Transmit message
					else if (alternatives.get(0).getName().startsWith(TestStepType.IN_TRANSMIT.value()))
					{
						logger.debug("IN_TRANSMIT");
						NodeList nodes = getElementsByLocalTagName(document, "InputAPDU");
						List<String> inputAPDUvalues = new ArrayList<>();
						for (int i = 0; i < nodes.getLength(); i++)
						{
							inputAPDUvalues.add(nodes.item(i).getTextContent());
						}
						resultMessage.append(computationHelper.handleApduList(inputAPDUvalues, card, knownValues.get(Replaceable.AGE_COMPARISON_VALUE.toString()).getValue(),
                                knownValues.get(Replaceable.PLACE_COMPARISON_VALUE.toString()).getValue()));
					}
					// check if this is a StartPAOSResponse without an error. if so, check the apdus
					else if (alternatives.get(0).getName().equals((TestStepType.IN_START_PAOS_RESPONSE.value())))
					{
						logger.debug("IN_START_PAOS_RESPONSE");
						Result apduResult = checkReceivedApdus();
						result.setSuccess(apduResult.wasSuccessful());
						resultMessage.append(apduResult.getMessage());
					}
				}
				catch (Exception e)
				{
					result.setSuccess(false);
					this.abort = true;
					resultMessage.append("Internal testbed error - unable to parse the received message. See technical log for details.");
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.warn("Could not parse received message: " + System.lineSeparator() + trace);
				}
			}
		}
		else
		{
			resultMessage.append("The received message did not contain a SOAP body");
		}

		// was the result ok? if not, do we have alternatives?
		List<LogMessage> alternativeValidations = null;
		if (!result.getSuccess() && alternatives.size() > 1)
		{
			resultMessage.append("Validation failed, trying alternative message.");
			stepsToSkip++;
			alternativeValidations = validateStep(receivedMessage, alternatives.subList(1, alternatives.size()));
		}

		// set the log text
		if (resultMessage.length() > resultMessageLength)
		{
			endResult += resultMessage;
		}
		result.setMessage(endResult);

		// build the list
		List<LogMessage> resultList = new ArrayList<>();
		resultList.add(result);
		if (alternativeValidations != null)
		{
			resultList.addAll(alternativeValidations);
		}
		return resultList;
	}

	@SuppressWarnings("rawtypes")
	protected Result checkReceivedApdus()
	{
		Set<RequestAttribute> attributes = computationHelper.getReceivedAttribues();
		Set<SpecialFunction> functions = computationHelper.getReceivedFunctions();

		boolean success = true;
		StringBuilder message = new StringBuilder();

		// first, check if all elements that have been requested by the user have also been requested by the server
		KnownValues allowedByUser = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX);
		for (KnownValue kv : allowedByUser)
		{
			if (kv.getValue().equals(GeneralConstants.PERMISSION_ALLOWED))
			{
				RequestAttribute ra = RequestAttribute.getFromName(kv.getName(), true);
				if (ra != null)
				{
					if (attributes.contains(ra))
					{
						attributes.remove(ra);
					}
					else
					{
						success = false;
						message.append("The attribute ").append(ra).append(" was requested by the user, but the server did not send a request for it to the card.").append(System.lineSeparator());
					}
				}
				else
				{
					SpecialFunction sf = SpecialFunction.getFromName(kv.getName(), true);
					if (functions.contains(sf))
					{
						functions.remove(sf);
					}
					else
					{
						success = false;
						message.append("The function ").append(sf.toString()).append(" was requested by the user, but the server did not send a request for it to the card.").append(System.lineSeparator());
					}
				}
			}
		}

		// now check if additional elements have been requested that were not allowed by the user
		if (!attributes.isEmpty())
		{
			success = false;
			java.util.StringJoiner sj = new java.util.StringJoiner(", ", ": ", ("." + System.lineSeparator()));
			attributes.forEach(ra -> sj.add(ra.toString()));
			message.append("The server requested the additional attributes").append(sj);
		}
		if (functions.size() > 1 || (functions.size() == 1 && !functions.contains(SpecialFunction.RestrictedID)))
		{
			success = false;
			java.util.StringJoiner sj = new java.util.StringJoiner(", ", ": ", ("." + System.lineSeparator()));
			for (SpecialFunction sf : functions)
			{
				if (!sf.equals(SpecialFunction.RestrictedID))
				{
					sj.add(sf.toString());
				}
			}
			message.append("The server requested the additional functions").append(sj);
		}

		return new Result(success, message.toString());
	}

	// validate the certificate description
	@SuppressWarnings("rawtypes")
	private Result validateCertificateDescription(Document document)
	{
		// TODO reactivate certificate description check
		return new Result(true, "The certificate description was not checked, we supplied it");

		// NodeList nodes = getElementsByLocalTagName(document, "CertificateDescription");
		// if (nodes == null || nodes.getLength() == 0)
		// {
		// return new Result(false, "A certificate description has not been found.");
		// }
		// else
		// {
		// // element validation already assured that the certificate
		// // description only exists once
		// DataBuffer descriptionBuffer = new DataBuffer(DatatypeConverter.parseHexBinary(nodes.item(0).getTextContent()));
		// try
		// {
		// CertificateDescription description = new CertificateDescription(descriptionBuffer);
		// // hash the TLS2 certificate
		// X509Certificate tls2Cert = knownX509Certificates.get(GeneralConstants.CERT_TLS_SAML_1 + candidateName);
		// DataBuffer xBuffer = new DataBuffer(tls2Cert.getEncoded());
		// // TODO adapt to algorithm
		// DataBuffer hash = DataBuffer.generateHash(xBuffer, TAAlgorithm.ECDSA_SHA_256);
		// for (DataBuffer communiationCertHash : description.getCommCertificates())
		// {
		// if (communiationCertHash.asHex().equals(hash.asHex()))
		// {
		// return new Result(true, "The certificate description did contain a matching TLS2 hash value");
		// }
		// }
		// }
		// catch (CertificateDescParseException | CertificateEncodingException | NoSuchAlgorithmException | NoSuchProviderException e)
		// {
		// StringWriter trace = new StringWriter();
		// e.printStackTrace(new PrintWriter(trace));
		// logger.warn("Could not parse the Certificate Description: " + System.getProperty("line.separator") + trace.toString());
		// }
		// }
		// return new Result(false, "The certificate description did not contain the TLS2 certificate hash");
	}

	// validate the signed challenge sent by the server
	@SuppressWarnings("rawtypes")
	private Result validateSignature(NodeList signatureNodes)
	{
		String resultMessage = "";
		boolean success = false;
		if (signatureNodes == null || signatureNodes.getLength() == 0 || signatureNodes.item(0).getTextContent().isEmpty())
		{
			resultMessage += "Signature has not been found";
		}
		else if (signatureNodes.getLength() > 1)
		{
			resultMessage += "More than one Signature element has been found";
		}
		else
		{
			if (knownValues.containsElement(Replaceable.AUTHENTICATEDAUXILIARYDATA.toString()))
			{
				success = (computationHelper.isValidSignature(signatureNodes.item(0).getTextContent(), knownValues.get(Replaceable.IDPICC.toString()).getValue(),
						knownValues.get(Replaceable.CHALLENGE.toString()).getValue(), knownValues.get(Replaceable.EPHEMERALPUBLICKEY.toString()).getValue(),
						knownValues.get(Replaceable.AUTHENTICATEDAUXILIARYDATA.toString()).getValue(), candidate));
			}
			else
			{
				success = (computationHelper.isValidSignature(signatureNodes.item(0).getTextContent(), knownValues.get(Replaceable.IDPICC.toString()).getValue(),
						knownValues.get(Replaceable.CHALLENGE.toString()).getValue(), knownValues.get(Replaceable.EPHEMERALPUBLICKEY.toString()).getValue(), candidate));
			}
			resultMessage += "Signature validation " + ((success) ? "was successful" : "failed");
		}
		resultMessage += System.lineSeparator();
		return new Result(success, resultMessage);
	}

	private String saveData(Step step, Document document, String documentString)
	{
		if (document != null)
		{
			List<OptAttrString> toSaves = step.getToSave();
			List<String> remaining = new ArrayList<>();
			// search for nodes first
			StringBuilder result = new StringBuilder();
			for (OptAttrString toSave : toSaves)
			{
				String name = toSave.getValue();
				String knownValueName = (toSave.getPlaceholder() != null) ? toSave.getPlaceholder() : name;
				String elementName = (toSave.getParentName() != null) ? (toSave.getParentName() + "/" + name) : name;
				NodeList nodeList = getElementsByTagNameIgnoreCase(documentString, document, name);

				ArrayList<Node> nodes = filterNodeListByParentName(nodeList, toSave);

				if (nodes.isEmpty() || nodes.get(0).getTextContent().isEmpty())
				{
					remaining.add(name);
				}
				else
				{
					knownValues.add(new KnownValue(knownValueName, nodes.get(0).getTextContent()));
					result.append("Saved value of element ").append(elementName).append(" as ").append(knownValueName).append(": ").append(nodes.get(0).getTextContent()).append(System.lineSeparator());
				}
			}

			// if some data could not be found, check if it exists as attribute
			if (!remaining.isEmpty())
			{
				for (String name : remaining)
				{
					NodeList nodes = evaluateIgnoreCase(documentString, document, name);
					if (nodes == null || nodes.getLength() == 0 || nodes.item(0).getAttributes() == null || nodes.item(0).getAttributes().getLength() == 0
							|| nodes.item(0).getAttributes().getNamedItem(name) == null || nodes.item(0).getAttributes().getNamedItem(name).getTextContent().isEmpty())
					{
						// check if the token is mandatory. if not, ignore
						for (StepToken token : step.getProtocolStepToken())
						{
							if (token.getName().equals(name))
							{
								if (token.isIsMandatory())
								{
									this.abort = true;
									return "Necessary token " + name + " not found, aborting the test." + System.lineSeparator();
								}
								else
								{
									result.append("Token ").append(name).append(" was not provided, therefore it's value will not be saved").append(System.lineSeparator());
									break;
								}
							}
						}
					}
					knownValues.add(new KnownValue(name.toUpperCase(), nodes.item(0).getAttributes().getNamedItem(name).getTextContent()));
					result.append("Saved value of attribute ").append(name).append(": ").append(nodes.item(0).getAttributes().getNamedItem(name).getTextContent()).append(System.lineSeparator());
				}
			}
			return result.toString();
		}
		return "No data has been saved.";
	}

	// save the data that is needed for future steps
	protected String saveData(Step step, String protocol, String documentString)
	{
		if (protocol == null)
		{
			return "No protocol message has been provided. No data will be saved for this step";
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(protocol);
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Could not save values: " + System.lineSeparator() + trace);
		}

		return saveData(step, document, documentString);
	}

	protected NodeList getElementsByLocalTagName(Document document, String name)
	{
		// first, try the given name
		NodeList nodeList = document.getElementsByTagNameNS("*", name);
		if (nodeList.getLength() == 0)
		{
			nodeList = document.getElementsByTagNameNS("", name);
		}
		return nodeList;
	}

	// searches for a given element name ignoring the case. this function
	// selects the first match
	private NodeList getElementsByTagNameIgnoreCase(String documentString, Document document, String name)
	{
		// first, try the given name
		NodeList nodeList = getElementsByLocalTagName(document, name);
		if (nodeList == null || nodeList.getLength() == 0 || nodeList.item(0).getTextContent().isEmpty())
		{
			// now, treat the document as text and search for the word
			String newName = findMostProbableName(documentString, name);
			if (newName != null)
			{
				nodeList = getElementsByLocalTagName(document, newName);
			}
		}
		return nodeList;
	}

	// searches for a given attribute name ignoring the case. this function
	// selects the first match
	private NodeList evaluateIgnoreCase(String documentString, Document document, String name)
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		NodeList nodes = null;
		try
		{
			// first, try the given name
			nodes = (NodeList) xpath.evaluate("//*[@" + name + "]", document, XPathConstants.NODESET);
			if (nodes == null || nodes.getLength() == 0 || nodes.item(0).getAttributes() == null || nodes.item(0).getAttributes().getLength() == 0
					|| nodes.item(0).getAttributes().getNamedItem(name) == null || nodes.item(0).getAttributes().getNamedItem(name).getTextContent().isEmpty())
			{
				// now, treat the document as text and search for the word
				String newName = findMostProbableName(documentString, name);
				if (newName != null)
				{
					nodes = (NodeList) xpath.evaluate("//*[@" + name + "]", document, XPathConstants.NODESET);
				}
			}
		}
		catch (XPathExpressionException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.warn("Could not parse the XPATH expression: " + System.lineSeparator() + trace);
		}
		return nodes;
	}

	// find the most probable case sensitive name for the given string, e.g.
	// "messageID" might match "MessageID" or "messageid"
	private String findMostProbableName(String text, String name)
	{
		Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		String result = null;
		if (matcher.find())
		{
			result = matcher.group(0);
		}
		return result;
	}

	// save the CV certificates provided in the given step. If any error occurs a String is returned
	protected String saveCVcertificates(Document document)
	{
		knownCvCertificates = new HashMap<>();
		NodeList nodes = getElementsByLocalTagName(document, "Certificate");
		String message = null;
		for (int i = 0; i < nodes.getLength(); i++)
		{
			CVCertificate c;
			try
			{
				DataBuffer cvBuffer = new DataBuffer(Hex.decodeHex(nodes.item(i).getTextContent().toCharArray()));
				c = new CVCertificate(cvBuffer);
				knownCvCertificates.put(c.getCertHolderRef() + candidate.getCandidateName(), c);
			}
			catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException | DOMException
					| DecoderException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.warn("Could not read CV certificate: " + System.lineSeparator() + trace);
				message = ("Could not read CV certificate: " + e.getLocalizedMessage() + ". See technical log for details.");
				break;
			}
		}
		return message;
	}

	// check if the tokens match the expected data
	@SuppressWarnings("rawtypes")
	private Result checkTokens(List<StepToken> tokens, Document document)
	{
		boolean result = true;
		StringBuilder resultMessage = new StringBuilder();
		for (StepToken token : tokens)
		{
			NodeList nodeList = getElementsByLocalTagName(document, token.getName());

			ArrayList<Node> nodes = filterNodeListByParentName(nodeList, token);

			int maxOccurences = 1;
			if (token.getMaxNumberOfOccurences() != null)
			{
				maxOccurences = token.getMaxNumberOfOccurences().intValue();
			}
			// token not found
			if (nodes.isEmpty())
			{
				// PersonalData tokens are mandatory if provided
				if (token.isIsMandatory() || (null != token.getParentName()) && (token.getParentName().equals("PersonalData") && !token.getValue().startsWith("[PERSONAL_")))
				{
					result = false;
				}
				resultMessage.append((token.isIsMandatory() || ((null != token.getParentName()) && token.getParentName().equals("PersonalData") && !token.getValue().startsWith("[PERSONAL_")))
                        ? "Mandatory"
                        : "Optional").append(" protocol token ").append(token.getName());
				if (null != token.getParentName())
				{
					resultMessage.append(" with parent ").append(token.getParentName());
				}
				resultMessage.append(" not found.").append(System.lineSeparator());
			}
			// check if the token occurred more often than the allowed number of
			// times
			else if (nodes.size() > maxOccurences)
			{
				result = false;
				resultMessage.append("Protocol token ").append(token.getName()).append(" occured more than the allowed limit of ").append(maxOccurences).append(" times.");
			}
			// found a single token, check the content
			else
			{
				// check token value, if present
				String receivedValue = nodes.get(0).getTextContent();
				if (token.getValue() != null && !(token.getValue().startsWith("[") && token.getValue().endsWith("]")))
				{
					boolean fail = false;
					if (token.isNegate())
					{
						if (receivedValue.toLowerCase().contains(token.getValue().toLowerCase()))
						{
							fail = true;
						}
					}
					else
					{
						if (!receivedValue.toLowerCase().contains(token.getValue().toLowerCase()))
						{
							fail = true;
						}
					}
					if (fail)
					{
						// if the token isn't mandatory refrain from changing the
						// result of the test
						if (token.isIsMandatory() || "true".equalsIgnoreCase(token.isIsReallyMandatory()))
						{
							result = false;
						}
						resultMessage.append("Wrong protocol token value for token ").append(token.getName()).append(" with parent ").append(token.getParentName()).append(". Expected value was ");
						if (token.isNegate())
						{
							resultMessage.append("a value that differs from ");
						}
						resultMessage.append(token.getValue()).append(", but received ").append((!receivedValue.isEmpty()) ? receivedValue : "an empty element").append(".").append(System.lineSeparator());
					}
				}
			}
		}
		return new Result(result, resultMessage.toString());
	}

	private ArrayList<Node> filterNodeListByParentName(NodeList nodeList, StepToken token)
	{
		return filterNodeListByParentName(nodeList, token.getParentName());
	}

	private ArrayList<Node> filterNodeListByParentName(NodeList nodeList, OptAttrString optName)
	{
		return filterNodeListByParentName(nodeList, optName.getParentName());
	}

	private ArrayList<Node> filterNodeListByParentName(NodeList nodeList, String parentName)
	{
		ArrayList<Node> nodes = new ArrayList<>();

		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node aNode = nodeList.item(i);

			// Do we have to check parentName?
			if (parentName != null)
			{
				// Yes, we must check parentName
				if (aNode.getParentNode() == null || aNode.getParentNode().getLocalName() == null || !parentName.equals(aNode.getParentNode().getLocalName()))
				{
					// no parent or parentName does not match -> skip node
					continue;
				}
			}
			nodes.add(aNode);
		}

		return nodes;
	}

	// this method replaces all known variables with their actual values
	protected String insertValues(String testMessage)
	{
		return insertValues(knownValues, testMessage);
	}

	protected static String insertValues(KnownValues knownValues, String testMessage)
	{
        for (KnownValue v : knownValues) {
            if (testMessage.contains(v.getPlaceholder().toUpperCase())) {
                testMessage = testMessage.replace(v.getPlaceholder().toUpperCase(), v.getValue());
            }
        }
		return testMessage;
	}

	protected Map<String, String> getHttpHeaders(String message)
	{
		Map<String, String> httpHeaders = new HashMap<>();
		String[] httpLines = message.split(GeneralConstants.HTTP_LINE_ENDING);
		StringBuilder headerValue = new StringBuilder();
		String headerName = "";
		httpHeaders.put("HTTP_STATUS_CODE", httpLines[0]);
		for (int i = 1; i < httpLines.length; i++)
		{
			String headerCandidate = httpLines[i];
			if (headerCandidate.charAt(0) == ' ' || headerCandidate.charAt(0) == '\t')
			{
				// linebreak, add to known keypair
				headerValue.append(headerCandidate);
			}
			else
			{
				// previous header finished, save it
				if (headerValue.length() > 0)
				{
					httpHeaders.put(headerName, headerValue.toString());
				}
				// new header
				headerName = headerCandidate.substring(0, headerCandidate.indexOf(':'));
				headerValue = new StringBuilder(headerCandidate.substring(headerCandidate.indexOf(':') + 1));
				// remove trailing whitespace or tab
				if (headerValue.toString().startsWith(" ") || headerValue.toString().startsWith("\t"))
				{
					headerValue = new StringBuilder(headerValue.substring(1));
				}
			}
		}
		// save the last header
		httpHeaders.put(headerName, headerValue.toString());
		return httpHeaders;
	}

	/**
	 * Add or override a known value
	 * 
	 * @param value {@link KnownValue}
	 */
	protected void addKnownValue(KnownValue value)
	{
		knownValues.add(value);
	}

	/**
	 * Returns the values that have been collected during the current test run
	 * 
	 * @return {@link KnownValues}
	 */
	public KnownValues getKnownValues()
	{
		return knownValues;
	}

	/**
	 * Returns whether the test shall be aborted
	 * 
	 * @return {@link Boolean}
	 */
	public boolean getAbortState()
	{
		return abort;
	}

	/**
	 * Returns the amount of steps that shall be skipped in order to account for
	 * the number of optional steps skipped
	 * 
	 * @return {@link Integer}
	 */
	public int getStepsToSkip()
	{
		return stepsToSkip;
	}

	/**
	 * Resets the amount of steps to skip
	 */
	public void resetStepsToSkip()
	{
		this.stepsToSkip = 0;
	}

	/**
	 * @return the knownCvCertificates
	 */
	public Map<String, CVCertificate> getKnownCvCertificates()
	{
		return knownCvCertificates;
	}

	/**
	 * Convenience method to retrieve a port number from a URL.
	 * 
	 * @param url URL to extract port number from
	 * @return port number from given URL or a default value
	 */
	public int getNonEmptyPort(URL url)
	{
		int port = url.getPort();
		if (port == -1)
		{
			port = url.getDefaultPort();
		}
		if (port == -1)
		{
			port = 443;
		}
		return port;
	}

	/**
	 * Convenience method to retrieve a path from a URL.
	 * 
	 * @param url URL to extract path from
	 * @return path from given URL or the default string "/".
	 */
	public String getNonEmptyPath(URL url)
	{
		String path = url.getPath();
		if (path.isEmpty())
		{
			path = "/";
		}
		if (url.getQuery() != null && !url.getQuery().isEmpty())
		{
			path += ("?" + url.getQuery());
		}
		return path;
	}

	/**
	 * Extracts the path from the refresh address
	 * 
	 * @return path of the refresh address
	 */
	protected String getRefreshAddressPath()
	{
		String refreshPath = "/";
		try
		{
			URL refreshUrl = new URL(knownValues.get(Replaceable.REFRESHADDRESS.toString()).getValue());
			refreshPath = getNonEmptyPath(refreshUrl);
		}
		catch (MalformedURLException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Getting pathof the refresh URL failed: " + System.lineSeparator() + trace);
		}
		return refreshPath;
	}

	/**
	 * Set the {@link ComputationHelper} to use for this instance.
	 * 
	 * @param ch
	 *            {@link ComputationHelper} The computation helper that shall be used
	 */
	protected void setComputationHelper(ComputationHelper ch)
	{
		this.computationHelper = ch;
	}

	/**
	 * This function returns additional data about outbound steps which has to be shown to the tester.
	 * The stored additional data will be deleted after the call.
	 * 
	 * @return {@link String} The additional data
	 */
	protected String getAdditionalOutboundInfo()
	{
		String local = additionalOutboundData;
		additionalOutboundData = null;
		return local;
	}

    /**
	 * Parses the TC Token Url from a HTML webpage. The result will be stored in the ATTACHED_TC_TOKEN
	 * TODO JavaScript support
	 * 
	 * @param message
	 *            {@link String}
	 * 
	 * @return {@link URL} The TC Token URL
	 */
	protected URL parseTcTokenUrl(String message)
	{
		Pattern p = Pattern.compile("(<a [^<>]*href=\"(http|eid)://127.0.0.1:24727/eID-Client\\?tcTokenURL=)([^\"]*)(\"[^<>]*>)");
		Matcher m = p.matcher(message);
		String tcTokenUrl = null;
		while (m.find()) // find last occurrence of given pattern
		{
			tcTokenUrl = m.group(3);
		}
		if (tcTokenUrl != null)
		{
			try
			{
				return new URL(URLDecoder.decode(tcTokenUrl, StandardCharsets.UTF_8));
			}
			catch (MalformedURLException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not decode TCToken URL from received message due to: " + e.getMessage() + System.lineSeparator() + trace);
			}
		}
		return null;
	}

	/**
	 * Parses the session cookie from the HTTP header. The result will be stored in the ATTACHED_JSESSIONID.
	 * Name and value are kept as they are, because a cookie value is opaque to the client (RFC 6265, section 4.1.1)
	 * and servers do not have to name their session cookie JSESSIONID (e.g. Spring Session uses SESSION).
	 * A cookie with a known session name is preferred, otherwise the first cookie is used.
	 * TODO JavaScript support
	 *
	 * @param message
	 *
	 * @return {String} The cookie as name=value, ready to be sent in a Cookie header, or null if no cookie was set
	 */
	protected static String parseSessionCookie(String message)
	{
		Pattern p = Pattern.compile("^Set-Cookie:[ \\t]*([^=;\\s]+)=([^;\\s]+)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(message);
		String firstCookie = null;
		while (m.find())
		{
			String name = m.group(1);
			String cookie = name + "=" + m.group(2);
			if ("JSESSIONID".equalsIgnoreCase(name) || "SESSION".equalsIgnoreCase(name))
			{
				return cookie;
			}
			if (firstCookie == null)
			{
				firstCookie = cookie;
			}
		}
		return firstCookie;
	}

	/**
	 * Returns the {@link KnownValues} for which the user has restricted the access
	 * 
	 * @return {@link Set} of {@link String} containing the restricted attributes
	 */
	protected Set<String> getRestrictedByUser()
	{
		KnownValues allowedValues = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX);
		Set<String> restrictedAttributes = allowedValues.stream().filter(kv -> kv.getValue().equals(GeneralConstants.PERMISSION_PROHIBITED))
				.map(kv -> kv.getName().substring(GeneralConstants.ALLOWED_BY_USER_PREFIX.length())).collect(Collectors.toSet());
		return restrictedAttributes;
	}


}
