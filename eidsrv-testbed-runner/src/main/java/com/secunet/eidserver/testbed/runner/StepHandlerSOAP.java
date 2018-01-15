package com.secunet.eidserver.testbed.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class StepHandlerSOAP extends StepHandler
{
	private static final Logger logger = LogManager.getLogger(StepHandlerSOAP.class);

	public static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	public static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	/**
	 * Create a StepHandler for the SOAP profile
	 * 
	 * @param candidateName
	 *            {@link String}
	 * @param eCardApiUrl
	 *            {@link URL}
	 * @param testbedRefreshAdress
	 *            {@link String}
	 * @param eidUrl
	 *            {@link URL}
	 * @param values
	 *            {@link KnownValues}
	 * @param logMessageDao
	 *            {@link LogMessageDAO}
	 * @param service
	 *            {@link EService}
	 * @param card
	 *            {@link EidCard}
	 * @param expectedNumOfCv
	 *            {@link int}
	 */
	protected StepHandlerSOAP(String candidateName, URL eCardApiUrl, String testbedRefreshAdress, URL eidUrl, KnownValues values, LogMessageDAO logMessageDao, EService service, EidCard card,
			final int expectedNumOfCv)
	{
		super(candidateName, values, logMessageDao, service, card, expectedNumOfCv);

		// fill values that are already known
		super.fillCommonData(candidateName, eCardApiUrl, testbedRefreshAdress, card);
		knownValues.add(new KnownValue(Replaceable.EID_INTERFACE_URL.toString(), eidUrl.toString()));
		knownValues.add(new KnownValue(Replaceable.EID_INTERFACE_HOSTNAME.toString(), eidUrl.getHost()));
		knownValues.add(new KnownValue(Replaceable.EID_INTERFACE_PATH.toString(), getNonEmptyPath(eidUrl)));
		// default request counter value
		knownValues.add(new KnownValue(Replaceable.REQUESTCOUNTER.toString(), GeneralConstants.DEFAULT_REQUEST_COUNTER));
	}

	/**
	 * Create a StepHandler for the SOAP profile in attached mode
	 * 
	 * @param candidateName
	 *            {@link String}
	 * @param eCardApiUrl
	 *            {@link URL}
	 * @param testbedRefreshAdress
	 *            {@link String}
	 * @param eidUrl
	 *            {@link URL}
	 * @param values
	 *            {@link KnownValues}
	 * @param logMessageDao
	 *            {@link LogMessageDAO}
	 * @param service
	 *            {@link EService}
	 * @param card
	 *            {@link EidCard}
	 * @param certificateBaseNames
	 *            {@link List} of {@link String}
	 * @param expectedNumOfCv
	 *            {@link int}
	 */
	protected StepHandlerSOAP(String candidateName, URL attachedUrl, String testbedRefreshAdress, KnownValues values, LogMessageDAO logMessageDao, EService service, EidCard card,
			final int expectedNumOfCv)
	{
		super(candidateName, values, logMessageDao, service, card, expectedNumOfCv);

		// fill values that are already known
		super.fillCommonData(candidateName, null, testbedRefreshAdress, card);
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE.toString(), attachedUrl.toString()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_HOSTNAME.toString(), attachedUrl.getHost()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_PATH.toString(), getNonEmptyPath(attachedUrl)));
		// default request counter value
		knownValues.add(new KnownValue(Replaceable.REQUESTCOUNTER.toString(), GeneralConstants.DEFAULT_REQUEST_COUNTER));
	}

	@Override
	protected String generateMsg(String generateFrom) throws InvalidTestCaseDescriptionException, EphemeralKeyNotFoundException, SharedSecretNotYetReadyException
	{
		generateFrom = insertValues(generateFrom);
		Step step = JaxBUtil.unmarshal(generateFrom, Step.class);

		if (step != null)
		{
			// outbound messages have only one http token and may have one protocol token
			StepToken headerToken = step.getHttpStepToken().get(0);
			String header = headerToken.getValue();
			String protocolBody = new String();
			if (null != step.getProtocolStepToken() && step.getProtocolStepToken().size() > 0)
			{
				StepToken protocolToken = step.getProtocolStepToken().get(0);
				protocolBody = protocolToken.getValue();

				// Finalize the USEID request. all data groups which were not provided as <i>REQUIRED</i> or <i>OPTIONAL</i> will be prohibited.
				// Note that it is assumed that comparison data for age- and place verification is ignored if the function itself is set to <i>PROHIBITED</i>.
				protocolBody = protocolBody.replaceAll("\\[USEOP_.*?\\]", "PROHIBITED");

				// create SOAP header with Security header
				if (protocolBody.contains(Functional.CREATE_SOAP_SECURITY_HEADER.getTextMark()))
				{
					protocolBody = protocolBody.replace(Functional.CREATE_SOAP_SECURITY_HEADER.getTextMark(), "");
					protocolBody = createSOAPrequest(protocolBody);
				}
			}

			// generate the rest of the message
			String returnMsg = super.generateMsg(header, protocolBody);

			// save values
			if (step.getToSave() != null && step.getToSave().size() > 0)
			{
				super.saveData(step, protocolBody, returnMsg);
			}

			// save USEID values
			// TODO implement a more elegant way to find out if this is a useIdRequest
			if (null != step.getProtocolStepToken() && !step.getProtocolStepToken().isEmpty() && step.getProtocolStepToken().get(0).getValue().contains("useIDRequest"))
			{
				// Mandatory
				Pattern requiredPattern = Pattern.compile("(eid:)([A-Za-z]*)(&gt;|>)(REQUIRED|OPTIONAL)");
				Matcher m = requiredPattern.matcher(step.getProtocolStepToken().get(0).getValue());
				while (m.find())
				{
					String name = m.group(2);
					knownValues.add(new KnownValue(GeneralConstants.REQUESTED_PREFIX + name.toUpperCase(), m.group(4)));
					fillIfNonexistant(GeneralConstants.ALLOWED_BY_USER_PREFIX + name.toUpperCase(), GeneralConstants.PERMISSION_ALLOWED);
				}

				// fill the operations allowed by user
				super.fillDataToRequest();
			}

			return returnMsg;
		}
		else
		{
			throw new InvalidTestCaseDescriptionException("Test case could not be loaded from data: " + System.getProperty("line.separator") + generateFrom);
		}
	}

	@Override
	public List<LogMessage> validateStep(String receivedMessage, List<TestCaseStep> alternatives)
	{
		if (TestStepType.IN_TC_TOKEN_ATTACHED.toString().equals(alternatives.get(0).getName()))
		{
			List<LogMessage> messages = super.validateStep(receivedMessage, alternatives);
			// we just need the path here, but parse the entire URL anyway for convenience reasons
			if (knownValues.containsElement("ServerAddress"))
			{
				try
				{
					URL serverAddress = new URL(knownValues.get("ServerAddress").getValue().toString());
					knownValues.add(new KnownValue(Replaceable.CANDIDATE_URL.toString(), serverAddress.toString()));
					knownValues.add(new KnownValue(Replaceable.CANDIDATE_HOSTNAME.toString(), serverAddress.getHost()));
					knownValues.add(new KnownValue(Replaceable.CANDIDATE_PATH.toString(), getNonEmptyPath(serverAddress)));
				}
				catch (MalformedURLException e)
				{
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.error("Could not decode TCToken URL from received message due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
					messages.get(messages.size()).setSuccess(false);
				}
			}
			return messages;
		}
		else
		{
			return super.validateStep(receivedMessage, alternatives);
		}
	}

	protected String createSOAPrequest(String message)
	{
		// check if this is a xml signature test case
		String signatureAlgorithm = null;
		String digestMethodAlgorithm = null;
		String canonicalizationAlgorithm = null;
		if (knownValues.containsElement(GeneralConstants.XML_SIGNATURE))
		{
			signatureAlgorithm = knownValues.get(GeneralConstants.XML_SIGNATURE_URI).getValue();
			digestMethodAlgorithm = knownValues.get(GeneralConstants.XML_SIGNATURE_DIGEST).getValue();
			canonicalizationAlgorithm = knownValues.get(GeneralConstants.XML_SIGNATURE_CANONICALIZATION).getValue();
		}
		else
		{
			signatureAlgorithm = SignatureMethod.RSA_SHA1;
			digestMethodAlgorithm = DigestMethod.SHA1;
			canonicalizationAlgorithm = CanonicalizationMethod.EXCLUSIVE;
		}

		try
		{
			CryptoHelper.Algorithm alg = null;
			if (signatureAlgorithm.contains("rsa"))
			{
				alg = CryptoHelper.Algorithm.RSA_ALG_ID;
			}
			else if (signatureAlgorithm.contains("ecdsa"))
			{
				alg = CryptoHelper.Algorithm.ECDSA_ALG_ID;
			}
			else
			{
				alg = CryptoHelper.Algorithm.DSA_ALG_ID;
			}
			PrivateKey privKey = CryptoHelper.loadKey(CryptoHelper.Protocol.SOAP_PROT_ID, alg, service.toString());
			if (privKey == null)
			{
				logger.error("Could not load key file: " + CryptoHelper.Protocol.SOAP_PROT_ID + ", " + alg + ", " + service.toString());
				return null;
			}
			X509Certificate x509Cert = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SOAP_PROT_ID, alg, service.toString());
			if (x509Cert == null)
			{
				logger.error("Could not load x509 certificate file: " + CryptoHelper.Protocol.SOAP_PROT_ID + ", " + alg + ", " + service.toString());
				return null;
			}

			Document xmlDoc = getDocument(message);

			DOMSource xmlSource = new DOMSource(xmlDoc.getDocumentElement());

			SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			soapPart.setContent(xmlSource);

			// soapMessage.writeTo(System.out);
			ByteArrayOutputStream baosSOAP = new ByteArrayOutputStream();
			soapMessage.writeTo(baosSOAP);
			logger.debug("SOAP message: \n" + baosSOAP);

			// Generate a DOM representation of the SOAP message

			// Get input source
			Source source = soapPart.getContent();
			org.w3c.dom.Node root = null;

			if (source instanceof DOMSource)
			{
				root = ((DOMSource) source).getNode();

			}
			else if (source instanceof SAXSource)
			{
				InputSource inSource = ((SAXSource) source).getInputSource();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = null;

				synchronized (dbf)
				{
					db = dbf.newDocumentBuilder();
				}
				Document doc = db.parse(inSource);
				root = doc.getDocumentElement();

			}
			else
			{
				logger.error("Cannot convert SOAP message (" + source.getClass().getName() + ") into a W3C DOM tree");
				return null;
			}

			// Assemble the signature parts
			String bodyId = "body-123";
			String timestampId = "time-123";
			String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
			XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());
			DigestMethod digestMethod = sigFactory.newDigestMethod(digestMethodAlgorithm, null);

			Transform transformBody = null;
			if (canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS))
			{
				transformBody = sigFactory.newTransform(canonicalizationAlgorithm, new ExcC14NParameterSpec(Arrays.asList("eid")));
			}
			else if (canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS))
			{
				transformBody = sigFactory.newTransform(canonicalizationAlgorithm, (TransformParameterSpec) null);
			}
			else
			{
				try
				{
					transformBody = sigFactory.newTransform(canonicalizationAlgorithm, (TransformParameterSpec) null);
				}
				catch (Exception e)
				{
					logger.error("Unsupported canonicalization algorithm: " + canonicalizationAlgorithm);
					throw e;
				}
			}


			Reference refBody = sigFactory.newReference("#" + bodyId, digestMethod, Collections.singletonList(transformBody), null, null);

			Transform transformTimestamp = null;
			if (canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS))
			{
				transformTimestamp = sigFactory.newTransform(canonicalizationAlgorithm, new ExcC14NParameterSpec(Arrays.asList("wsse", "eid", "soapenv")));
			}
			else if (canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS))
			{
				transformTimestamp = sigFactory.newTransform(canonicalizationAlgorithm, (TransformParameterSpec) null);
			}
			else
			{
				try
				{
					transformTimestamp = sigFactory.newTransform(canonicalizationAlgorithm, (TransformParameterSpec) null);
				}
				catch (Exception e)
				{
					logger.error("Unsupported canonicalization algorithm: " + canonicalizationAlgorithm);
					throw e;
				}
			}

			Reference refTimestamp = sigFactory.newReference("#" + timestampId, digestMethod, Collections.singletonList(transformTimestamp), null, null);

			SignedInfo signedInfo = null;
			if (canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS))
			{
				signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(canonicalizationAlgorithm, new ExcC14NParameterSpec(Arrays.asList("eid", "soapenv"))),
						sigFactory.newSignatureMethod(signatureAlgorithm, null), Arrays.asList(refBody, refTimestamp));
			}
			else if (canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE) || canonicalizationAlgorithm.equals(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS))
			{
				signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(canonicalizationAlgorithm, (C14NMethodParameterSpec) null),
						sigFactory.newSignatureMethod(signatureAlgorithm, null), Arrays.asList(refBody, refTimestamp));
			}
			else
			{
				try
				{
					signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod(canonicalizationAlgorithm, (C14NMethodParameterSpec) null),
							sigFactory.newSignatureMethod(signatureAlgorithm, null), Arrays.asList(refBody, refTimestamp));
				}
				catch (Exception e)
				{
					logger.error("Unsupported canonicalization algorithm: " + canonicalizationAlgorithm);
					throw e;
				}
			}


			KeyInfoFactory kif = sigFactory.getKeyInfoFactory();

			Element elementIssuerSerial = createDOMX509IssuerSerial(xmlDoc, x509Cert.getIssuerX500Principal().getName(), x509Cert.getSerialNumber());
			Element elementKeyData = createDOMX509Data(xmlDoc, elementIssuerSerial);
			Element elementSecurityTokenReference = createSecurityTokenReference(xmlDoc, elementKeyData);
			DOMStructure domKeyInfo = new DOMStructure(elementSecurityTokenReference);
			KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(domKeyInfo));


			XMLSignature sig = sigFactory.newXMLSignature(signedInfo, keyInfo);

			// Insert XML signature into DOM tree and sign

			SOAPHeaderElement headerElement = soapPart.getEnvelope().getHeader().addHeaderElement(soapPart.getEnvelope().createName("Security", "wsse", WSSE_NS));

			SOAPElement timestampSoapElement = headerElement.addChildElement(soapPart.getEnvelope().createName("Timestamp", "wsu", WSU_NS));
			timestampSoapElement.addAttribute(soapPart.getEnvelope().createName("Id", "wsu", WSU_NS), timestampId);
			SOAPElement timestampCreatedSoapElement = timestampSoapElement.addChildElement(soapPart.getEnvelope().createName("Created", "wsu", WSU_NS));
			SOAPElement timestampExpiredSoapElement = timestampSoapElement.addChildElement(soapPart.getEnvelope().createName("Expires", "wsu", WSU_NS));
			Instant now = Instant.now();
			Duration duration = Duration.ofMinutes(5);
			Instant expires = now.plus(duration);

			timestampCreatedSoapElement.setTextContent(now.toString());
			timestampExpiredSoapElement.setTextContent(expires.toString());

			// Find where to insert signature
			Element envelope = getFirstChildElement(root);
			Element header = getFirstChildElement(envelope);

			DOMSignContext sigContext = new DOMSignContext(privKey, headerElement);
			// Need to distinguish the Signature element in DSIG (from that in SOAP)
			sigContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");
			// register Body ID attribute
			sigContext.setIdAttributeNS(getNextSiblingElement(header), WSU_NS, "Id");
			sigContext.setIdAttributeNS(timestampSoapElement, WSU_NS, "Id");
			sig.sign(sigContext);

			if (knownValues.containsElement(GeneralConstants.XML_SIGNATURE))
			{
				String sigValue = knownValues.get(GeneralConstants.XML_SIGNATURE).getValue();
				if ((sigValue != null) && (sigValue.endsWith(GeneralConstants.MANIPULATED_SUFFIX)))
				{
					// Manipulate SignatureValue
					Element eltSecurity = getFirstChildElement(header);
					Element eltTimestamp = getFirstChildElement(eltSecurity);
					Element eltSignature = getNextSiblingElement(eltTimestamp);
					Element eltSignedInfo = getFirstChildElement(eltSignature);
					Element eltSignatureValue = getNextSiblingElement(eltSignedInfo);
					String base64Value = eltSignatureValue.getTextContent();
					byte[] decoded = Base64.decode(base64Value);
					if ((decoded != null) && (decoded.length > 0))
					{
						// todo: find a better solution to manipulate ;-)
						int byteToChange = decoded.length / 2;
						if (decoded[byteToChange] != 0x00)
							decoded[byteToChange] = 0x00;
						else
							decoded[byteToChange] = 0x01;
					}
					String base64ValueMod = Base64.toBase64String(decoded);
					if (base64Value.equals(base64ValueMod))
					{
						logger.error("Could not manipulate SignatureValue");
						return null;
					}
					eltSignatureValue.setTextContent(base64ValueMod);
				}
			}

			return prettyNodeToString(root);
		}
		catch (ParserConfigurationException | SAXException | IOException | TransformerException | MarshalException | XMLSignatureException | SOAPException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not create SOAP signature due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	private Document getDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
	}


	/**
	 * Returns the first child element of the specified node, or null if there
	 * is no such element.
	 *
	 * @param node
	 *            the node
	 * @return the first child element of the specified node, or null if there
	 *         is no such element
	 * @throws NullPointerException
	 *             if <code>node == null</code>
	 */
	private static Element getFirstChildElement(org.w3c.dom.Node node)
	{
		org.w3c.dom.Node child = node.getFirstChild();
		while (child != null && child.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
		{
			child = child.getNextSibling();
		}
		return (Element) child;
	}

	/**
	 * Returns the next sibling element of the specified node, or null if there
	 * is no such element.
	 *
	 * @param node
	 *            the node
	 * @return the next sibling element of the specified node, or null if there
	 *         is no such element
	 * @throws NullPointerException
	 *             if <code>node == null</code>
	 */
	public static Element getNextSiblingElement(org.w3c.dom.Node node)
	{
		org.w3c.dom.Node sibling = node.getNextSibling();
		while (sibling != null && sibling.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
		{
			sibling = sibling.getNextSibling();
		}
		return (Element) sibling;
	}

	public static Element createDOMX509IssuerSerial(Document doc, String issuer, BigInteger serialNumber)
	{
		if (issuer == null)
		{
			throw new NullPointerException("The issuerName cannot be null");
		}
		if (serialNumber == null)
		{
			throw new NullPointerException("The serialNumber cannot be null");
		}

		Element element = doc.createElementNS(XMLSignature.XMLNS, "ds:X509IssuerSerial");

		Element issuerNameElement = doc.createElementNS(XMLSignature.XMLNS, "ds:X509IssuerName");
		issuerNameElement.appendChild(doc.createTextNode(issuer));
		element.appendChild(issuerNameElement);

		Element serialNumberElement = doc.createElementNS(XMLSignature.XMLNS, "ds:X509SerialNumber");
		serialNumberElement.appendChild(doc.createTextNode(serialNumber.toString()));
		element.appendChild(serialNumberElement);

		return element;
	}

	public static Element createDOMX509Data(Document doc, Element domIssuerSerial)
	{
		Element element = doc.createElementNS(XMLSignature.XMLNS, "ds:X509Data");

		element.appendChild(domIssuerSerial);

		return element;
	}

	public static Element createSecurityTokenReference(Document doc, Element domIssuerSerial)
	{
		Element element = doc.createElementNS(WSSE_NS, "wsse:SecurityTokenReference");

		element.appendChild(domIssuerSerial);

		return element;
	}

	public String prettyDocumentToString(Document doc) throws IOException, TransformerException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			nodeToStream(doc.getDocumentElement(), baos);
			return new String(baos.toByteArray());
		}
	}

	public String prettyNodeToString(Node node) throws IOException, TransformerException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			nodeToStream(node, baos);
			return new String(baos.toByteArray());
		}
	}

	public void nodeToStream(Node node, OutputStream out) throws TransformerException
	{
		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(out);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.transform(source, result);
	}

}
