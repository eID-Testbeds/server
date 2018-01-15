package com.secunet.eidserver.testbed.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import com.secunet.eidserver.testbed.common.classes.EidCards;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.OptAttrString;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;
import com.secunet.testbedutils.utilities.JaxBUtil;

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

	// the candidate name
	protected String candidateName;

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

	// the instance used to valide message elements that need computations
	protected ComputationHelper computationHelper;

	protected StepHandler(String candidateName, KnownValues values, LogMessageDAO logMessageDAO, final EService service, final EidCard card, final int expectedNumOfCv)
	{
		this.candidateName = candidateName;
		if (null != values)
		{
			this.knownValues = values;
		}
		else
		{
			this.knownValues = new KnownValues();
		}
		this.logMessageDAO = logMessageDAO;
		this.card = card;
		this.service = service;
		this.expectedNumOfCv = expectedNumOfCv;
	}

	// fill values that are already known
	protected void fillCommonData(String candidateName, URL candidateUrl, String testbedRefreshAdress, EidCard card)
	{
		knownValues.add(new KnownValue(Replaceable.USER_AGENT.toString(),
				(GeneralConstants.USER_AGENT_NAME + "/" + GeneralConstants.USER_AGENT_MAJOR + "." + GeneralConstants.USER_AGENT_MINOR + "." + GeneralConstants.USER_AGENT_SUBMINOR)));
		knownValues.add(new KnownValue(Replaceable.TESTBED_USER_AGENT_NAME.toString(), GeneralConstants.USER_AGENT_NAME));
		if (candidateUrl != null)
		{
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_URL.toString(), candidateUrl.toString()));
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_HOSTNAME.toString(), candidateUrl.getHost()));
			knownValues.add(new KnownValue(Replaceable.CANDIDATE_PATH.toString(), getNonEmptyPath(candidateUrl)));
		}
		knownValues.add(new KnownValue(Replaceable.TESTBED_REFRESH_ADDRESS.toString(), testbedRefreshAdress));
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

		this.candidateName = candidateName;
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
	 * Generate the message that will be sent to the server using a XML step definition
	 * 
	 * @param xmlContent
	 * @return
	 * @throws InvalidTestCaseDescription
	 * @throws EphemeralKeyNotFoundException
	 * @throws SharedSecretNotYetReadyException
	 */
	protected abstract String generateMsg(String generateFrom) throws InvalidTestCaseDescriptionException, EphemeralKeyNotFoundException, SharedSecretNotYetReadyException;

	/**
	 * Generate the message that will be sent to the server
	 * 
	 * @param httpHeader
	 * @param protocolBody
	 * @return
	 * @throws InvalidTestCaseDescription
	 * @throws EphemeralKeyNotFoundException
	 * @throws SharedSecretNotYetReadyException
	 */
	protected String generateMsg(String httpHeader, String protocolBody) throws InvalidTestCaseDescriptionException, EphemeralKeyNotFoundException, SharedSecretNotYetReadyException
	{
		// concat header and body
		String returnMsg;
		if (null != protocolBody && protocolBody.length() > 0)
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
			try
			{
				String authenticationToken = computationHelper.computeAuthenticationToken(knownValues.get(Replaceable.EPHEMERALPUBLICKEY.toString()).getValue());
				if (authenticationToken != null)
				{
					returnMsg = returnMsg.replace(Functional.AUTHENTICATION_TOKEN.getTextMark(), authenticationToken);
				}
			}
			catch (SharedSecretNotYetReadyException e)
			{
				throw e;
			}
		}
		// insert responseapdus
		if (returnMsg.contains(Functional.APDU.getTextMark()))
		{
			// compute the apdus
			returnMsg = returnMsg.replace(Functional.APDU.getTextMark(), computationHelper.getPreviousApdus());
		}

		// do we have to compute the charlength?
		if (returnMsg.contains(Functional.CHARLENGTH.getTextMark()))
		{
			returnMsg = returnMsg.replace(Functional.CHARLENGTH.getTextMark(), "" + getCharlength(returnMsg));
		}

		return returnMsg;
	}

	// compute the charlength of the message body
	private int getCharlength(String message)
	{
		String body = new String();
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
	protected List<LogMessage> validateStep(String receivedMessage, List<TestCaseStep> alternatives)
	{
		LogMessage result = logMessageDAO.createNew();
		result.setTestStepName(alternatives.get(0).getName());
		result.setSuccess(true);
		String endResult = "Processing step " + alternatives.get(0).getName() + ". Message was:" + System.getProperty("line.separator");
		endResult += receivedMessage + System.getProperty("line.separator");
		String resultMessage = "Results: " + System.getProperty("line.separator");
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
					resultMessage += "Wrong HTTP token " + htoken.getName() + ". Expected value was " + htoken.getValue() + ", received " + httpHeaders.get(htoken.getName()) + "."
							+ System.getProperty("line.separator");
				}
			}
			else
			{
				// the message did not contain the token
				if (htoken.isIsMandatory())
				{
					result.setSuccess(false);
				}
				resultMessage += "Missing HTTP token " + htoken.getName() + "." + System.getProperty("line.separator");
			}
		}

		// create XML object out of the receivedMessage string
		if (httpHeaders.get("HTTP_STATUS_CODE").equals("HTTP/1.1 200 OK") && messageComponents.length > 1 && messageComponents[1].length() != 0)
		{
			// check if we operate in attached mode
			if (alternatives.get(0).getName().startsWith(TestStepType.IN_ATTACHED_WEBPAGE.value()))
			{
				logger.debug("IN_ATTACHED_WEBPAGE");

				URL tcTokenUrl = parseTcTokenUrl(receivedMessage);
				if (tcTokenUrl != null)
				{
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN.toString(), tcTokenUrl.toString()));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN_HOSTNAME.toString(), tcTokenUrl.getHost()));
					knownValues.add(new KnownValue(Replaceable.ATTACHED_TCTOKEN_PATH.toString(), getNonEmptyPath(tcTokenUrl)));
					resultMessage += "Received TC Token URL: " + tcTokenUrl.toString();
				}
				else
				{
					resultMessage += "Could not parse a valid TC Token URL from the received message";
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
					if (step.getSchema() != null && step.getSchema().length() > 0)
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
							resultMessage += "The schema specified schema file named " + step.getSchema() + " could not be loaded." + System.getProperty("line.separator");
						}
					}

					builder = factory.newDocumentBuilder();
					builder.setErrorHandler(new ErrorHandler() {
						@Override
						public void warning(SAXParseException exception) throws SAXException
						{
							schemaParsingResult += ("[Warning] " + exception.getMessage() + System.getProperty("line.separator"));
						}

						@Override
						public void fatalError(SAXParseException exception) throws SAXException
						{
							schemaParsingResult += ("[Fatal error] " + exception.getMessage() + System.getProperty("line.separator"));
						}

						@Override
						public void error(SAXParseException exception) throws SAXException
						{
							schemaParsingResult += ("[Error] " + exception.getMessage() + System.getProperty("line.separator"));
						}
					});
					Document document = builder.parse(new InputSource(new StringReader(messageComponents[1])));

					// save parsing error message, if we have got any
					if (schemaValidated)
					{
						if (schemaParsingResult.length() > 0)
						{
							resultMessage += "Validation results against the schema " + step.getSchema() + ":" + System.getProperty("line.separator");
							resultMessage += schemaParsingResult;
							schemaParsingResult = "";
						}
						else
						{
							resultMessage += "Successfully validated the message against the schema " + step.getSchema() + "." + System.getProperty("line.separator");
						}
					}

					// validate protocol tokens
					Result res = checkTokens(step.getProtocolStepToken(), document);
					if (result.getSuccess())
					{
						result.setSuccess(res.wasSuccessful());
					}
					resultMessage += res.getMessage();

					// save the data necessary to compute future steps
					resultMessage += saveData(step, document, messageComponents[1]);

					// check if this is a EAC1 InputType message
					if (alternatives.get(0).getName().startsWith(TestStepType.IN_DID_AUTHENTICATE_EAC_1_INPUTTYPE.value()))
					{
						logger.debug("IN_DID_AUTHENTICATE_EAC_1_INPUTTYPE");
						String savingResult = saveCVcertificates(document);
						if (savingResult != null)
						{
							resultMessage += savingResult + System.getProperty("line.separator");
						}
						computationHelper = new ComputationHelper(knownValues.get(Replaceable.EFCARDACCESS.toString()).getValue(), knownValues.get(Replaceable.NONCE.toString()).getValue(),
								knownValues.get(Replaceable.CA_PRIVATE.toString()).getValue());
						Result cvValidationResult = computationHelper.validateCVcertificates(knownCvCertificates, expectedNumOfCv, service);
						if (result.getSuccess())
						{
							result.setSuccess(cvValidationResult.wasSuccessful());
						}
						resultMessage += cvValidationResult.getMessage() + System.getProperty("line.separator");
						Result descriptionResult = validateCertificateDescription(document);
						if (result.getSuccess())
						{
							result.setSuccess(descriptionResult.wasSuccessful());
						}
						resultMessage += descriptionResult.getMessage() + System.getProperty("line.separator");
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
							resultMessage += signatureResult.getMessage();
							// skip additionalinputtype
							this.stepsToSkip = 1;
						}
					}
					// check if this is a EAC AdditionalInputType message
					else if (alternatives.get(0).getName().startsWith(TestStepType.IN_DID_AUTHENTICATE_EACADDITIONALINPUTTYPE.value()))
					{
						logger.debug("IN_DID_AUTHENTICATE_EACADDITIONALINPUTTYPE");
						Result signatureResult = validateSignature(getElementsByLocalTagName(document, "Signature"));
						if (result.getSuccess())
						{
							result.setSuccess(signatureResult.wasSuccessful());
						}
						resultMessage += signatureResult.getMessage();
					}
					// check if this is a Transmit message
					else if (alternatives.get(0).getName().startsWith(TestStepType.IN_TRANSMIT.value()))
					{
						logger.debug("IN_TRANSMIT");
						NodeList nodes = getElementsByLocalTagName(document, "InputAPDU");
						List<String> inputAPDUvalues = new ArrayList<String>();
						for (int i = 0; i < nodes.getLength(); i++)
						{
							inputAPDUvalues.add(nodes.item(i).getTextContent());
						}
						resultMessage += computationHelper.handleApduList(inputAPDUvalues, card, knownValues.get(Replaceable.AGE_COMPARISON_VALUE.toString()).getValue(),
								knownValues.get(Replaceable.PLACE_COMPARISON_VALUE.toString()).getValue());
					}
					// check if this is a StartPAOSResponse without an error. if so, check the apdus
					else if (alternatives.get(0).equals((TestStepType.IN_START_PAOS_RESPONSE)))
					{
						logger.debug("IN_START_PAOS_RESPONSE");
						Result apduResult = checkReceivedApdus();
						result.setSuccess(apduResult.wasSuccessful());
						resultMessage += apduResult.getMessage();
					}
				}
				catch (Exception e)
				{
					result.setSuccess(false);
					this.abort = true;
					resultMessage += "Internal testbed error - unable to parse the received message. See technical log for details.";
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.warn("Could not parse received message: " + System.getProperty("line.separator") + trace.toString());
				}
			}
		}
		else
		{
			resultMessage += "The received message did not contain a SOAP body";
		}

		// was the result ok? if not, do we have alternatives?
		List<LogMessage> alternativeValidations = null;
		if (!result.getSuccess() && alternatives.size() > 1)
		{
			resultMessage += "Validation failed, trying alternative message.";
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
		List<LogMessage> resultList = new ArrayList<LogMessage>();
		resultList.add(result);
		if (alternativeValidations != null)
		{
			resultList.addAll(alternativeValidations);
		}
		return resultList;
	}

	protected Result checkReceivedApdus()
	{
		Set<RequestAttribute> attributes = computationHelper.getReceivedAttribues();
		Set<SpecialFunction> functions = computationHelper.getReceivedFunctions();

		boolean success = true;
		String message = "";

		// first, check if all elements that have been requested by the user have also been requested by the server
		KnownValues allowedByUser = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX.toString());
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
						message += "The attribute " + ra.toString() + " was requested by the user, but the server did not send a request for it to the card." + System.getProperty("line.separator");
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
						message += "The function " + sf.toString() + " was requested by the user, but the server did not send a request for it to the card." + System.getProperty("line.separator");
					}
				}
			}
		}

		// now check if additional elements have been requested that were not allowed by the user
		if (!attributes.isEmpty())
		{
			success = false;
			java.util.StringJoiner sj = new java.util.StringJoiner(", ", ": ", ("." + System.getProperty("line.separator")));
			attributes.forEach(ra -> sj.add(ra.toString()));
			message += "The server requested the additional attributes" + sj.toString();
		}
		if (functions.size() > 1 || (functions.size() == 1 && !functions.contains(SpecialFunction.RestrictedID)))
		{
			success = false;
			java.util.StringJoiner sj = new java.util.StringJoiner(", ", ": ", ("." + System.getProperty("line.separator")));
			for (SpecialFunction sf : functions)
			{
				if (!sf.equals(SpecialFunction.RestrictedID))
				{
					sj.add(sf.toString());
				}
			}
			message += "The server requested the additional functions" + sj.toString();
		}

		return new Result(success, message);
	}

	// validate the certificate description
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
	private Result validateSignature(NodeList signatureNodes)
	{
		String resultMessage = "";
		boolean success = false;
		if (signatureNodes == null || signatureNodes.getLength() == 0 || signatureNodes.item(0).getTextContent().length() == 0)
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
						knownValues.get(Replaceable.AUTHENTICATEDAUXILIARYDATA.toString()).getValue()));
			}
			else
			{
				success = (computationHelper.isValidSignature(signatureNodes.item(0).getTextContent(), knownValues.get(Replaceable.IDPICC.toString()).getValue(),
						knownValues.get(Replaceable.CHALLENGE.toString()).getValue(), knownValues.get(Replaceable.EPHEMERALPUBLICKEY.toString()).getValue()));
			}
			resultMessage += "Signature validation " + ((success) ? "was successful" : "failed");
		}
		resultMessage += System.getProperty("line.separator");
		return new Result(success, resultMessage);
	}

	private String saveData(Step step, Document document, String documentString)
	{
		if (document != null)
		{
			List<OptAttrString> toSaves = step.getToSave();
			List<String> remaining = new ArrayList<String>();
			// search for nodes first
			String result = new String();
			for (OptAttrString toSave : toSaves)
			{
				String name = toSave.getValue();
				String knownValueName = (toSave.getPlaceholder() != null) ? toSave.getPlaceholder() : name;
				String elementName = (toSave.getParentName() != null) ? (toSave.getParentName() + "/" + name) : name;
				NodeList nodeList = getElementsByTagNameIgnoreCase(documentString, document, name);

				ArrayList<Node> nodes = filterNodeListByParentName(nodeList, toSave);

				if (nodes == null || nodes.size() == 0 || nodes.get(0).getTextContent().length() == 0)
				{
					remaining.add(name);
				}
				else
				{
					knownValues.add(new KnownValue(knownValueName, nodes.get(0).getTextContent()));
					result += ("Saved value of element " + elementName + " as " + knownValueName + ": " + nodes.get(0).getTextContent() + System.getProperty("line.separator"));
				}
			}

			// if some data could not be found, check if it exists as attribute
			if (remaining.size() > 0)
			{
				for (String name : remaining)
				{
					NodeList nodes = evaluateIgnoreCase(documentString, document, name);
					if (nodes == null || nodes.getLength() == 0 || nodes.item(0).getAttributes() == null || nodes.item(0).getAttributes().getLength() == 0
							|| nodes.item(0).getAttributes().getNamedItem(name) == null || nodes.item(0).getAttributes().getNamedItem(name).getTextContent().length() == 0)
					{
						// check if the token is mandatory. if not, ignore
						for (StepToken token : step.getProtocolStepToken())
						{
							if (token.getName().equals(name))
							{
								if (token.isIsMandatory())
								{
									this.abort = true;
									return "Necessary token " + name + " not found, aborting the test." + System.getProperty("line.separator");
								}
								else
								{
									result += "Token " + name + " was not provided, therefore it's value will not be saved" + System.getProperty("line.separator");
									break;
								}
							}
						}
					}
					knownValues.add(new KnownValue(name.toUpperCase(), nodes.item(0).getAttributes().getNamedItem(name).getTextContent()));
					result += ("Saved value of attribute " + name + ": " + nodes.item(0).getAttributes().getNamedItem(name).getTextContent() + System.getProperty("line.separator"));
				}
			}
			return result;
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
			logger.warn("Could not save values: " + System.getProperty("line.separator") + trace.toString());
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
		if (nodeList == null || nodeList.getLength() == 0 || nodeList.item(0).getTextContent().length() == 0)
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
					|| nodes.item(0).getAttributes().getNamedItem(name) == null || nodes.item(0).getAttributes().getNamedItem(name).getTextContent().length() == 0)
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
			logger.warn("Could not parse the XPATH expression: " + System.getProperty("line.separator") + trace.toString());
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
				knownCvCertificates.put(c.getCertHolderRef() + candidateName, c);
			}
			catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException | CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException | DOMException
					| DecoderException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.warn("Could not read CV certificate: " + System.getProperty("line.separator") + trace.toString());
				message = ("Could not read CV certificate: " + e.getLocalizedMessage() + ". See technical log for details.");
				break;
			}
		}
		return message;
	}

	// check if the tokens match the expected data
	private Result checkTokens(List<StepToken> tokens, Document document)
	{
		boolean result = true;
		String resultMessage = "";
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
			if (nodes == null || nodes.size() == 0)
			{
				// PersonalData tokens are mandatory if provided
				if (token.isIsMandatory() || (null != token.getParentName()) && (token.getParentName().equals("PersonalData") && !token.getValue().startsWith("[PERSONAL_")))
				{
					result = false;
				}
				resultMessage += ((token.isIsMandatory() || ((null != token.getParentName()) && token.getParentName().equals("PersonalData") && !token.getValue().startsWith("[PERSONAL_")))
						? "Mandatory"
						: "Optional") + " protocol token " + token.getName();
				if (null != token.getParentName())
				{
					resultMessage += " with parent " + token.getParentName();
				}
				resultMessage += " not found." + System.getProperty("line.separator");
			}
			// check if the token occurred more often than the allowed number of
			// times
			else if (nodes.size() > maxOccurences)
			{
				result = false;
				resultMessage += "Protocol token " + token.getName() + " occured more than the allowed limit of " + maxOccurences + " times.";
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
						if (token.isIsMandatory())
						{
							result = false;
						}
						resultMessage += "Wrong protocol token value for token " + token.getName() + " with parent " + token.getParentName() + ". Expected value was ";
						if (token.isNegate())
						{
							resultMessage += "a value that differs from ";
						}
						resultMessage += token.getValue() + ", but received " + ((receivedValue.length() > 0) ? receivedValue : "an empty element") + "." + System.getProperty("line.separator");
					}
				}
			}
		}
		return new Result(result, resultMessage);
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
		ArrayList<Node> nodes = new ArrayList<Node>();

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
		Iterator<KnownValue> iter = knownValues.iterator();
		while (iter.hasNext())
		{
			KnownValue v = iter.next();
			if (testMessage.contains(v.getPlaceholder().toUpperCase()))
			{
				testMessage = testMessage.replace(v.getPlaceholder().toUpperCase(), v.getValue());
			}
		}
		return testMessage;
	}

	protected Map<String, String> getHttpHeaders(String message)
	{
		Map<String, String> httpHeaders = new HashMap<String, String>();
		String[] httpLines = message.split(GeneralConstants.HTTP_LINE_ENDING);
		String headerValue = "";
		String headerName = "";
		httpHeaders.put("HTTP_STATUS_CODE", httpLines[0]);
		for (int i = 1; i < httpLines.length; i++)
		{
			String headerCandidate = httpLines[i];
			if (headerCandidate.charAt(0) == ' ' || headerCandidate.charAt(0) == '\t')
			{
				// linebreak, add to known keypair
				headerValue += headerCandidate;
			}
			else
			{
				// previous header finished, save it
				if (headerValue.length() > 0)
				{
					httpHeaders.put(headerName, headerValue);
					headerValue = "";
					headerName = "";
				}
				// new header
				headerName = headerCandidate.substring(0, headerCandidate.indexOf(':'));
				headerValue = headerCandidate.substring(headerCandidate.indexOf(':') + 1, headerCandidate.length());
				// remove trailing whitespace or tab
				if (headerValue.startsWith(" ") || headerValue.startsWith("\t"))
				{
					headerValue = headerValue.substring(1);
				}
			}
		}
		// save the last header
		httpHeaders.put(headerName, headerValue);
		return httpHeaders;
	}

	/**
	 * Add or override a known value
	 * 
	 * @param value
	 */
	protected void addKnownValue(KnownValue value)
	{
		knownValues.add(value);
	}

	/**
	 * Returns the values that have been collected during the current test run
	 * 
	 * @return
	 */
	public KnownValues getKnownValues()
	{
		return knownValues;
	}

	/**
	 * Returns whether or not the test shall be aborted
	 * 
	 * @return
	 */
	public boolean getAbortState()
	{
		return abort;
	}

	/**
	 * Returns the amount of steps that shall be skipped in order to account for
	 * the number of optional steps skipped
	 * 
	 * @return
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
	 * @param url
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
	 * @param url
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
	 * @return
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
			logger.error("Getting pathof the refresh URL failed: " + System.getProperty("line.separator") + trace.toString());
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
	 * 
	 * @return
	 */
	protected String getAdditionalOutboundInfo()
	{
		String local = additionalOutboundData;
		additionalOutboundData = null;
		return local;
	};

	/**
	 * Parses the TC Token Url from a HTML webpage. The result will be stored in the ATTACHED_TC_TOKEN
	 * 
	 * TODO JavaScript support
	 * 
	 * @param message
	 *            {@link String}
	 * 
	 * @return
	 */
	protected URL parseTcTokenUrl(String message)
	{
		Pattern p = Pattern.compile("(<a [^<>]*href=\"http://127.0.0.1:24727/eID-Client\\?tcTokenURL=)([^\"]*)(\"[^<>]*>)");
		Matcher m = p.matcher(message);
		String tcTokenUrl = null;
		while (m.find()) // find last occurrence of given pattern
		{
			tcTokenUrl = m.group(2);
		}
		if (tcTokenUrl != null)
		{
			try
			{
				return new URL(URLDecoder.decode(tcTokenUrl, "UTF-8"));
			}
			catch (UnsupportedEncodingException | MalformedURLException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not decode TCToken URL from received message due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			}
		}
		return null;
	}

	/**
	 * Returns the {@link KnownValues} for which the user has restricted the access
	 * 
	 * @return
	 */
	protected Set<String> getRestrictedByUser()
	{
		KnownValues allowedValues = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX);
		Set<String> restrictedAttributes = allowedValues.stream().filter(kv -> kv.getValue().equals(GeneralConstants.PERMISSION_PROHIBITED))
				.map(kv -> kv.getName().substring(GeneralConstants.ALLOWED_BY_USER_PREFIX.length(), kv.getName().length())).collect(Collectors.toSet());
		return restrictedAttributes;
	}


}
