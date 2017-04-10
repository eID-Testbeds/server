package com.secunet.eidserver.testbed.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.encryption.Encrypter;
import org.opensaml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.encryption.EncryptedData;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.EncryptionException;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyMarshaller;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.enumerations.RequestAttribute;
import com.secunet.eidserver.testbed.common.enumerations.SpecialFunction;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class StepHandlerSAML extends StepHandler
{
	private static final Logger logger = LogManager.getLogger(StepHandlerSAML.class);

	/**
	 * Create a StepHandler for the SAML profile without known certificates
	 * (beside the encryption and signing certificate of the test candidate)
	 * 
	 * @param candidateName
	 * @param ecardApiUrl
	 * @param testbedRefreshAdress
	 * @param samlUrl
	 * @param x509Certificates
	 * @param logMessageDao
	 * @param expectedNumOfCv
	 *            {@link int}
	 */
	protected StepHandlerSAML(String candidateName, URL ecardApiUrl, String testbedRefreshAdress, URL samlUrl, KnownValues values, LogMessageDAO logMessageDao, EService service, EidCard card,
			final int expectedNumOfCv)
	{
		super(candidateName, values, logMessageDao, service, card, expectedNumOfCv);

		// fill values that are already known
		super.fillCommonData(candidateName, ecardApiUrl, testbedRefreshAdress, card);
		knownValues.add(new KnownValue(Replaceable.SAML_ID.toString(), UUID.randomUUID().toString()));
		knownValues.add(new KnownValue(Replaceable.SAML_PROCESSOR_URL.toString(), samlUrl.toString()));
		knownValues.add(new KnownValue(Replaceable.SAML_PROCESSOR_HOSTNAME.toString(), samlUrl.getHost()));
		knownValues.add(new KnownValue(Replaceable.SAML_PROCESSOR_PATH.toString(), samlUrl.getPath()));
	}

	/**
	 * Create a StepHandler for the SAML profile, known certificates and a
	 * CommunicationErrorAddress
	 * 
	 * @param profileSuffix
	 * @param eCardApiUrl
	 * @param testbedRefreshAdress
	 * @param x509Certificates
	 * @param cvCertificates
	 * @param communicationErrorAddress
	 * @param values
	 * @param logMessageDao
	 * @param expectedNumOfCv
	 *            {@link int}
	 */
	protected StepHandlerSAML(String profileSuffix, URL eCardApiUrl, String testbedRefreshAdress, URL samlUrl, String communicationErrorAddress, KnownValues values, LogMessageDAO logMessageDao,
			EService service, EidCard card, final int expectedNumOfCv)
	{
		// fill values that are already known
		this(profileSuffix, eCardApiUrl, testbedRefreshAdress, samlUrl, values, logMessageDao, service, card, expectedNumOfCv);
		knownValues.add(new KnownValue(Replaceable.COMMUNICATIONERRORADDRESS.toString(), communicationErrorAddress));
	}

	/**
	 * Extracts the path from the refresh address
	 */
	protected void updateRefreshAddress()
	{
		URL refreshUrl;
		try
		{
			refreshUrl = new URL(knownValues.get(Replaceable.REFRESHADDRESS.toString()).getValue());
			knownValues.add(new KnownValue(Replaceable.REFRESHADDRESS.toString(), getNonEmptyPath(refreshUrl)));
		}
		catch (MalformedURLException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Updating the refresh URL failed: " + System.getProperty("line.separator") + trace.toString());
		}
	}

	/**
	 * Validates the given assertion using the known data
	 * 
	 * @param assertion
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected LogMessage validateAssertion(String assertion, String stepName)
	{
		LogMessage assertionLog = logMessageDAO.createNew();
		assertionLog.setTestStepName(stepName);
		String result = "Processing step " + stepName + ". Received message was:" + System.getProperty("line.separator") + assertion + System.getProperty("line.separator")
				+ System.getProperty("line.separator") + "Results: " + System.getProperty("line.separator");
		boolean success = true;

		String responseString = "SAMLResponse";
		if (!assertion.contains(responseString) || !assertion.contains("SigAlg") || !assertion.contains("Signature"))
		{
			assertionLog.setMessage("The received SAML assertion was malformed.");
			assertionLog.setSuccess(false);
			return assertionLog;
		}


		String samlAssertion = null;
		String relayState = null;
		if (assertion.contains("&RelayState="))
		{
			samlAssertion = assertion.substring(assertion.indexOf(responseString) + responseString.length() + 1, assertion.indexOf("&RelayState="));
			relayState = assertion.substring(assertion.indexOf("&RelayState=") + "&RelayState=".length(), assertion.indexOf("&SigAlg="));
		}
		else
		{
			samlAssertion = assertion.substring(assertion.indexOf(responseString) + responseString.length() + 1, assertion.indexOf("&SigAlg="));
		}
		String SIGAlg = assertion.substring(assertion.indexOf("&SigAlg=") + "&SigAlg=".length(), assertion.indexOf("&Signature="));
		String signature = assertion.substring(assertion.indexOf("&Signature=") + "&Signature=".length(), assertion.indexOf("\r", assertion.indexOf("&Signature=")));
		try
		{
			// validate the signature
			CryptoHelper.Algorithm alg = CryptoHelper.Algorithm.RSA_ALG_ID;
			if (knownValues.containsElement(GeneralConstants.XML_SIGNATURE) && (knownValues.get(GeneralConstants.XML_SIGNATURE).getValue() != null))
			{
				String algo = CommonUtil.getSubstringBefore(knownValues.get(GeneralConstants.XML_SIGNATURE).getValue(), GeneralConstants.MANIPULATED_SUFFIX, true);
				alg = CryptoHelper.Algorithm.getFromAlgorithmName(algo);
			}

			X509Certificate sigCert = CryptoHelper.loadSignatureCertificate(CryptoHelper.Protocol.SAML_PROT_ID, alg, service);
			Result sigResult = verifySignature(samlAssertion, relayState, SIGAlg, signature, sigCert.getPublicKey());
			result += sigResult.getMessage() + System.getProperty("line.separator");
			success &= sigResult.wasSuccessful();
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | IOException | NoSuchProviderException | Base64DecodingException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not load SAML due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			result += "Could not validate SAML assertion due to problems while decoding the input";
			success = false;
		}
		catch (SignatureException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Signature verification of the SAML assertion failed: " + System.getProperty("line.separator") + trace.toString());
			result += "Signature verification of the SAML assertion failed";
			success = false;
		}
		try
		{
			// decrypt and validate the assertion
			Result valSuccess = processEncryptedAssertion(samlAssertion);
			result += valSuccess.getMessage();
			success &= valSuccess.wasSuccessful();
		}
		catch (IOException | Base64DecodingException | TransformerException | SAXException | ParserConfigurationException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not process SAML assertion due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			result += "Could not process SAML assertion";
			success = false;
		}
		assertionLog.setSuccess(success);
		assertionLog.setMessage(result);
		return assertionLog;
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

			String protocolBody = null;
			// create SAML request
			if (header.contains(Functional.CREATE_SAML.getTextMark()) || header.contains(Functional.CREATE_SAML_NO_SIGALG_NO_SIGNATURE.getTextMark())
					|| header.contains(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark()))
			{
				super.fillDataToRequest();
				header = createSAMLrequest(header);
			}
			else
			{
				// other message types
				if (null != step.getProtocolStepToken() && step.getProtocolStepToken().size() > 0)
				{
					StepToken protocolToken = step.getProtocolStepToken().get(0);
					protocolBody = protocolToken.getValue();
				}
			}
			// generate the rest of the message
			String returnMsg = null;
			try
			{
				returnMsg = super.generateMsg(header, protocolBody);
			}
			catch (InvalidTestCaseDescriptionException | EphemeralKeyNotFoundException | SharedSecretNotYetReadyException e)
			{
				throw e;
			}

			// save values
			if (step.getToSave() != null && step.getToSave().size() > 0)
			{
				super.saveData(step, protocolBody, returnMsg);
			}

			return returnMsg;
		}
		else
		{
			throw new InvalidTestCaseDescriptionException("Test case could not be loaded from data: " + System.getProperty("line.separator") + generateFrom);
		}
	}

	// create a saml request using the data obtained from the step
	protected String createSAMLrequest(String message)
	{
		try
		{
			// first, encrypt using the candidate SAML encryption key
			String certName = (service.equals(EService.EDSA) || service.equals(EService.EECDSA) || service.equals(EService.ERSA)) ? (EService.ERSA + GeneralConstants.ENCRYPTION_SUFFIX)
					: (service + GeneralConstants.ENCRYPTION_SUFFIX);
			X509Certificate x509Enc = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.RSA_ALG_ID, certName);
			if (x509Enc == null)
			{
				logger.error("Could not load encryption x509 certificate file: " + CryptoHelper.Protocol.SAML_PROT_ID.getProtocolName() + ", " + CryptoHelper.Algorithm.RSA_ALG_ID.getAlgorithmName()
						+ ", " + (service + GeneralConstants.ENCRYPTION_SUFFIX));
				return "";
			}

			PublicKey encKey = x509Enc.getPublicKey();

			AuthnRequest request = buildAuthRequest(encKey);

			String signatureAlgorithm;
			CryptoHelper.Algorithm algCryptoSign;
			if (knownValues.containsElement(GeneralConstants.XML_SIGNATURE) && (knownValues.get(GeneralConstants.XML_SIGNATURE).getValue() != null))
			{
				signatureAlgorithm = knownValues.get(GeneralConstants.XML_SIGNATURE_URI).getValue();
				algCryptoSign = CryptoHelper.Algorithm
						.getFromAlgorithmName(CommonUtil.getSubstringBefore(knownValues.get(GeneralConstants.XML_SIGNATURE).getValue(), GeneralConstants.MANIPULATED_SUFFIX, true));
			}
			else
			{
				signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
				algCryptoSign = CryptoHelper.getAlgorithmFromService(service);
			}

			// then sign using the testbed saml signing key
			if (message.contains(Functional.CREATE_SAML.getTextMark()))
				return message.replace(Functional.CREATE_SAML.getTextMark(), "?" + encodeAndSign(request, signatureAlgorithm, algCryptoSign, true, true));
			else if (message.contains(Functional.CREATE_SAML_NO_SIGALG_NO_SIGNATURE.getTextMark()))
				return message.replace(Functional.CREATE_SAML_NO_SIGALG_NO_SIGNATURE.getTextMark(), "?" + encodeAndSign(request, signatureAlgorithm, algCryptoSign, false, false));
			else if (message.contains(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark()))
				return message.replace(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark(), "?" + encodeAndSign(request, signatureAlgorithm, algCryptoSign, true, false));
			else
				return "";
		}
		catch (ConfigurationException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | MarshallingException | IOException | TransformerException | EncryptionException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not create SAML request due to: " + e.getClass().getName() + System.getProperty("line.separator") + trace.toString());
		}
		return "";
	}

	private String encodeAndSign(AuthnRequest request, String signatureAlgorithm, CryptoHelper.Algorithm alg, boolean withSigAlg, boolean withSignature)
			throws MarshallingException, IOException, NoSuchAlgorithmException, InvalidKeyException, java.security.SignatureException, TransformerException
	{
		Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(request);
		org.w3c.dom.Element authDOM = marshaller.marshall(request);
		StringWriter rspWrt = new StringWriter();
		XMLHelper.writeNode(authDOM, rspWrt);
		String messageXML = rspWrt.toString();
		additionalOutboundData = ((null != additionalOutboundData) ? additionalOutboundData : "") + System.getProperty("line.separator") + "Generated AuthnRequest:"
				+ System.getProperty("line.separator") + prettyPrintXml(messageXML, false);

		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);

		deflaterOutputStream.write(messageXML.getBytes());
		deflaterOutputStream.close();

		String samlAutnRequest = Base64.encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
		samlAutnRequest = URLEncoder.encode(samlAutnRequest, "UTF-8");

		if (withSigAlg)
		{
			// No SigAlg -> no Signature
			String signatureAlgorithmEncoded = URLEncoder.encode(signatureAlgorithm, "UTF-8");
			Signature sig = Signature.getInstance(CryptoHelper.getAlgorithmForSignatureUrl(signatureAlgorithm));

			sig.initSign(CryptoHelper.loadSignatureKey(CryptoHelper.Protocol.SAML_PROT_ID, alg, service));
			sig.update(("SAMLRequest=" + samlAutnRequest + "&SigAlg=" + signatureAlgorithmEncoded).getBytes());

			if (withSignature)
			{
				byte[] signed = sig.sign();
				String signature = Base64.encodeBytes(signed, Base64.DONT_BREAK_LINES);

				if (knownValues.containsElement(GeneralConstants.XML_SIGNATURE))
				{
					String sigValue = knownValues.get(GeneralConstants.XML_SIGNATURE).getValue();
					if ((sigValue != null) && (sigValue.endsWith(GeneralConstants.MANIPULATED_SUFFIX)))
					{
						if ((signed != null) && (signed.length > 0))
						{
							// TODO find a better solution to manipulate ;-)
							int byteToChange = signed.length / 2;
							if (signed[byteToChange] != 0x00)
								signed[byteToChange] = 0x00;
							else
								signed[byteToChange] = 0x01;

							String signatureMod = Base64.encodeBytes(signed, Base64.DONT_BREAK_LINES);
							if (signature.equals(signatureMod))
							{
								logger.error("Could not manipulate Signature");
								return "";
							}
							signature = signatureMod;
						}
					}
				}

				signature = URLEncoder.encode(signature, "UTF-8");

				return "SAMLRequest=" + samlAutnRequest + "&SigAlg=" + signatureAlgorithmEncoded + "&Signature=" + signature;
			}
			else
			{
				return "SAMLRequest=" + samlAutnRequest + "&SigAlg=" + signatureAlgorithmEncoded;
			}
		}
		else
		{
			return "SAMLRequest=" + samlAutnRequest;
		}
	}

	@SuppressWarnings("unchecked")
	protected AuthnRequest buildAuthRequest(PublicKey encKey) throws EncryptionException, MarshallingException, ConfigurationException, TransformerException
	{
		logger.entry("Building the AuthnRequest");
		DateTime now = new DateTime();
		DefaultBootstrap.bootstrap();
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		SAMLObjectBuilder<AuthnRequest> requestBuilder = (SAMLObjectBuilder<AuthnRequest>) builderFactory.getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
		AuthnRequest request = requestBuilder.buildObject();
		request.setIssueInstant(now);
		request.setVersion(SAMLVersion.VERSION_20);
		request.setID(knownValues.get(Replaceable.SAML_ID.toString()).getValue());
		request.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
		request.setDestination(knownValues.get(Replaceable.SAML_PROCESSOR_URL.toString()).getValue());
		if (knownValues.containsElement(GeneralConstants.SAML_AUTHNREQUEST_ASSERTION_CONSUMER_SERVICE_URL))
		{
			request.setAssertionConsumerServiceURL(knownValues.get(GeneralConstants.SAML_AUTHNREQUEST_ASSERTION_CONSUMER_SERVICE_URL).getValue());
		}
		else
		{
			request.setAssertionConsumerServiceURL(GeneralConstants.TESTBED_REFRESH_URL);
		}
		request.setProviderName(GeneralConstants.PROVIDER_NAME);
		request.getNamespaceManager().registerNamespace(new Namespace("http://bsi.bund.de/eID/", "eid"));
		request.getNamespaceManager().registerNamespace(new Namespace(SAMLConstants.SAML20_NS, SAMLConstants.SAML20_PREFIX));
		request.getNamespaceManager().registerNamespace(new Namespace("http://www.w3.org/2000/09/xmldsig#", "ds"));
		request.getNamespaceManager().registerNamespace(new Namespace("http://www.w3.org/2001/04/xmlenc#", "enc"));
		request.getNamespaceManager().registerNamespace(new Namespace("http://www.w3.org/2001/XMLSchema-instance", "xsi"));

		// Create Issuer
		SAMLObjectBuilder<?> issuerBuilder = (SAMLObjectBuilder<?>) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
		if (knownValues.containsElement(GeneralConstants.SAML_AUTHNREQUEST_ISSUER))
		{
			KnownValue issuerValue = knownValues.get(GeneralConstants.SAML_AUTHNREQUEST_ISSUER);
			if (null != issuerValue.getValue() && issuerValue.getValue().length() > 0)
			{
				Issuer issuer = (Issuer) issuerBuilder.buildObject();
				issuer.setValue(issuerValue.getValue());
				request.setIssuer(issuer);
			}
		}
		// no modifications to the issuer field
		else
		{
			Issuer issuer = (Issuer) issuerBuilder.buildObject();
			issuer.setValue(GeneralConstants.TESTBED_REFRESH_URL);
			request.setIssuer(issuer);
		}

		// Build Extensions
		if (knownValues.containsElement(GeneralConstants.SAML_AUTHNREQUEST_EXTENSIONS))
		{
			KnownValue issuerValue = knownValues.get(GeneralConstants.SAML_AUTHNREQUEST_EXTENSIONS);
			if (null != issuerValue.getValue() && issuerValue.getValue().toUpperCase().equals("NULL"))
			{
				QName name = new QName(SAMLConstants.SAML20P_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
				Extensions ext = (Extensions) Configuration.getBuilderFactory().getBuilder(name).buildObject(name);
				request.setExtensions(ext);
			}
		}
		else
		{
			QName name = new QName(SAMLConstants.SAML20P_NS, Extensions.LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
			Extensions ext = (Extensions) Configuration.getBuilderFactory().getBuilder(name).buildObject(name);


			// Build the encrypted part
			XMLObjectBuilder<XSAny> xsAnyBuilder = builderFactory.getBuilder(XSAny.TYPE_NAME);
			XSAny encrytpedAuthnRequestExtention = xsAnyBuilder.buildObject("http://bsi.bund.de/eID/", "EncryptedAuthnRequestExtension", "eid");
			if (!knownValues.containsElement(GeneralConstants.SAML_AUTHNREQUEST_ENCRYPTED_AUTHN_REQUEST_EXTENSION))
			{
				XSAny authnRequestExtension = xsAnyBuilder.buildObject("http://bsi.bund.de/eID/", "AuthnRequestExtension", "eid");
				authnRequestExtension.getNamespaceManager().registerNamespace(new Namespace(SAMLConstants.SAML20_NS, SAMLConstants.SAML20_PREFIX));
				authnRequestExtension.getNamespaceManager().registerNamespace(new Namespace("http://www.w3.org/2001/XMLSchema-Instance", "xsi"));
				authnRequestExtension.getNamespaceManager().registerNamespace(new Namespace("http://www.w3.org/2001/XMLSchema", "xs"));
				authnRequestExtension.getUnknownAttributes().put(new QName("Version"), "2");

				XSAny requestedAtrributes = xsAnyBuilder.buildObject("http://bsi.bund.de/eID/", "RequestedAttributes", "eid");

				// add the requested data groups and special functions
				KnownValues requestAttributes = knownValues.getStartingWith(GeneralConstants.REQUESTED_PREFIX);
				for (KnownValue kv : requestAttributes)
				{
					if (!GeneralConstants.PERMISSION_PROHIBITED.equals(kv.getValue()))
					{
						AttributeBuilder attributeBuilder = (AttributeBuilder) builderFactory.getBuilder(Attribute.TYPE_NAME);
						Attribute attr = attributeBuilder.buildObject();

						RequestAttribute ra = RequestAttribute.getFromName(kv.getName(), true);
						SpecialFunction sf = null;
						// regular attributes
						if (null != ra)
						{
							attr.getUnknownAttributes().put(new QName("Name"), ra.toString());
						}
						// special functions
						else
						{
							sf = SpecialFunction.getFromName(kv.getName(), true);
							attr.getUnknownAttributes().put(new QName("Name"), sf.toString());
						}
						attr.getNamespaceManager().registerNamespace(new Namespace("http://bsi.bund.de/eID/", "eid"));
						if (GeneralConstants.PERMISSION_REQUIRED.equals(kv.getValue()))
						{
							attr.getUnknownAttributes().put(new QName("http://bsi.bund.de/eID/", "RequiredAttribute", "eid"), "true");
						}
						else if (GeneralConstants.PERMISSION_ALLOWED.equals(kv.getValue()))
						{
							attr.getUnknownAttributes().put(new QName("http://bsi.bund.de/eID/", "RequiredAttribute", "eid"), "false");
						}

						if (null != sf)
						{
							if (sf.equals(SpecialFunction.AgeVerification))
							{
								KnownValue ageComparison = knownValues.get(Replaceable.AGE_COMPARISON_VALUE.toString());
								if (null != ageComparison && !ageComparison.getValue().toUpperCase().equals("NULL"))
								{
									XSAny ageVerification = xsAnyBuilder.buildObject("http://bsi.bund.de/eID/", "Age", "eid");
									ageVerification.setTextContent(ageComparison.getValue());
									XSAny attrValue = xsAnyBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
									attrValue.getUnknownAttributes().put(new QName("http://www.w3.org/2001/XMLSchema-Instance", "type", "xsi"),
											new QName("http://bsi.bund.de/eID/", "AgeVerificationRequestType", "eid"));
									attrValue.getUnknownXMLObjects().add(ageVerification);
									attr.getAttributeValues().add(attrValue);
								}
								else
								{
									logger.warn("No age comparison value has been provided, although the function itself has been requested. This may be an error.");
								}
							}
							else if (sf.equals(SpecialFunction.PlaceVerification))
							{
								KnownValue placeComparison = knownValues.get(Replaceable.PLACE_COMPARISON_VALUE.toString());
								if (null != placeComparison && !placeComparison.getValue().toUpperCase().equals("NULL"))
								{
									XSAny attrValue = xsAnyBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
									attrValue.getUnknownAttributes().put(new QName("http://www.w3.org/2001/XMLSchema-Instance", "type", "xsi"),
											new QName("http://bsi.bund.de/eID/", "PlaceVerificationRequestType", "eid"));
									XSAny placeVerification = xsAnyBuilder.buildObject("http://bsi.bund.de/eID/", "CommunityID", "eid");
									placeVerification.setTextContent(placeComparison.getValue());
									attrValue.getUnknownXMLObjects().add(placeVerification);
									attr.getAttributeValues().add(attrValue);
								}
								else
								{
									logger.warn("No place comparison value has been provided, although the function itself has been requested. This may be an error.");
								}
							}
						}
						requestedAtrributes.getUnknownXMLObjects().add(attr);
					}
				}

				authnRequestExtension.getUnknownXMLObjects().add(requestedAtrributes);
				logger.debug("Encrypting AuthnRequestExtension");

				// build encryption elements
				EncryptionParameters encryptionParameters = buildEncryptionParameters();
				KeyEncryptionParameters keyEncryptionParameters = buildKeyEncryptionParameters(encKey);
				Encrypter samlEncrypter = new Encrypter(encryptionParameters, keyEncryptionParameters);
				samlEncrypter.setKeyPlacement(KeyPlacement.INLINE);

				// a marshaller has to be created before encrypting the extension
				Element are = new XSAnyMarshaller().marshall(authnRequestExtension);

				String authnRequest = prettyPrintNode(are, false);
				additionalOutboundData = "Generated AuthnRequestExtension:" + System.getProperty("line.separator") + authnRequest;

				EncryptedData encryptedData = samlEncrypter.encryptElement(authnRequestExtension, encryptionParameters, keyEncryptionParameters);
				encrytpedAuthnRequestExtention.getUnknownXMLObjects().add(encryptedData);
				ext.getUnknownXMLObjects().add(encrytpedAuthnRequestExtention);
			}

			ext.getUnknownXMLObjects().add(encrytpedAuthnRequestExtention);
			request.setExtensions(ext);
			logger.entry(Level.FINEST, "Building SAML Request finished");
		}

		return request;
	}

	/**
	 * Creates a new instance of {@link KeyEncryptionParameters} using the values provided during the initialization of this step handler instance and the given public key
	 * 
	 * @param encKey
	 *            The {@link PublicKey} to use
	 * @return The {@link KeyEncryptionParameters}
	 */
	private KeyEncryptionParameters buildKeyEncryptionParameters(PublicKey encKey)
	{
		BasicCredential encCreadentials = new BasicCredential();
		encCreadentials.setPublicKey(encKey);
		encCreadentials.setUsageType(UsageType.ENCRYPTION);
		KeyEncryptionParameters keyEncryptionParams = new KeyEncryptionParameters();
		keyEncryptionParams.setEncryptionCredential(encCreadentials);
		if (knownValues.containsElement(GeneralConstants.SAML_ENCRYPTION_KEY_TRANSPORT))
		{
			String keyTransportVariable = knownValues.get(GeneralConstants.SAML_ENCRYPTION_KEY_TRANSPORT).getValue();
			if (keyTransportVariable.equals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15))
			{
				keyEncryptionParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
			}
			else if (keyTransportVariable.equals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11))
			{
				keyEncryptionParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
			}
			else if (keyTransportVariable.equals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP))
			{
				keyEncryptionParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
			}
			else
			{
				logger.warn("Unsupported algorithm was provided for the SAML key transport encryption variable: " + keyTransportVariable + ". Falling back to the default RSA15");
				keyEncryptionParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
			}
		}
		else
		{
			keyEncryptionParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
		}
		KeyInfoGeneratorFactory kigf = Configuration.getGlobalSecurityConfiguration().getKeyInfoGeneratorManager().getDefaultManager().getFactory(encCreadentials);
		keyEncryptionParams.setKeyInfoGenerator(kigf.newInstance());
		return keyEncryptionParams;
	}

	/**
	 * Creates a new instance of {@link EncryptionParameters} using the values provided during the initialization of this step handler instance
	 * 
	 * @return The {@link EncryptionParameters}
	 */
	private EncryptionParameters buildEncryptionParameters()
	{
		EncryptionParameters encParams = new EncryptionParameters();
		if (knownValues.containsElement(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER))
		{
			String blockVariable = knownValues.get(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER).getValue();
			if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
			}
			else if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
			}
			else if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
			}
			else if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
			}
			else if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
			}
			else if (blockVariable.equals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM))
			{
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
			}
			else
			{
				logger.warn("Unsupported algorithm was provided for the SAML block algorithm encryption variable: " + blockVariable + ". Falling back to the default AES128");
				encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
			}
		}
		else
		{
			encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
		}
		encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
		return encParams;
	}

	/**
	 * Verifies the SAML Assertion signature using the given parameters
	 * 
	 * @param SAMLAssertion
	 * @param SIGAlg
	 * @param relayState
	 * @param signature
	 * @param SigningKeyPub
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws Base64DecodingException
	 */
	@SuppressWarnings("rawtypes")
	private Result verifySignature(String SAMLAssertion, String relayState, String sigAlg, String signature, PublicKey SigningKeyPub)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, NoSuchProviderException, Base64DecodingException
	{
		String signatureValue = "SAMLResponse=" + SAMLAssertion;
		if (relayState != null)
		{
			signatureValue += "&RelayState=" + relayState;
		}
		signatureValue += "&SigAlg=" + sigAlg;
		sigAlg = URLDecoder.decode(sigAlg, "UTF-8");
		sigAlg = URLDecoder.decode(sigAlg, "UTF-8");
		signature = URLDecoder.decode(signature, "UTF-8");
		byte[] decodedSignature = Base64.decode(signature);

		Signature sig = Signature.getInstance(CryptoHelper.getAlgorithmForSignatureUrl(sigAlg), BouncyCastleProvider.PROVIDER_NAME);
		sig.initVerify(SigningKeyPub);
		sig.update(signatureValue.getBytes());
		if (!sig.verify(decodedSignature))
		{
			return new Result(false, "SAML Assertion signature verification failed");
		}
		else
		{
			return new Result(true, "SAML Assertion signature verification was successful");
		}
	}

	/**
	 * Process the encrypted assertion by inflating, decrypting and validating it
	 * 
	 * @param assertion
	 * @return
	 * @throws IOException
	 * @throws Base64DecodingException
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	@SuppressWarnings("rawtypes")
	private Result processEncryptedAssertion(String assertion) throws IOException, Base64DecodingException, TransformerException, SAXException, ParserConfigurationException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		assertion = URLDecoder.decode(assertion, "UTF-8");
		byte[] decodedAssertion = Base64.decode(assertion);

		Inflater inflater = new Inflater(true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream, inflater);
		inflaterOutputStream.write(decodedAssertion);
		inflaterOutputStream.close();
		String inflated = byteArrayOutputStream.toString();
		byteArrayOutputStream.close();
		String result = "";
		boolean success = true;
		if (inflated != null)
		{
			// destination
			Pattern destinationPattern = Pattern.compile("(Destination=\")(.+)(\" ID=)");
			Matcher destinationMatcher = destinationPattern.matcher(inflated);
			if (destinationMatcher.find())
			{
				boolean dest = knownValues.get(Replaceable.TESTBED_REFRESH_ADDRESS.toString()).getValue().equals(destinationMatcher.group(2));
				if (dest)
				{
					result += "Destination address was correct - " + destinationMatcher.group(2);
				}
				else
				{
					result += "Destination address was incorrect - expected " + knownValues.get(Replaceable.TESTBED_REFRESH_ADDRESS.toString()).getValue() + ", received "
							+ destinationMatcher.group(2);
				}
			}
			else
			{
				result += "Could not find destination address in assertion";
				success = false;
			}
			result += System.getProperty("line.separator");

			// id
			Pattern idPattern = Pattern.compile("(InResponseTo=\")(.+)(\" IssueInstant=)");
			Matcher idMatcher = idPattern.matcher(inflated);
			if (idMatcher.find())
			{
				boolean id = (knownValues.get(Replaceable.SAML_ID.toString()).getValue()).equals(idMatcher.group(2));
				if (id)
				{
					result += "SAML ID was correct - " + idMatcher.group(2);
				}
				else
				{
					result += "SAML ID was incorrect - expected " + knownValues.get(Replaceable.SAML_ID.toString()).getValue() + ", received " + idMatcher.group(2);
				}
			}
			else
			{
				result += "Could not find SAML ID in assertion";
				success = false;
			}
			result += System.getProperty("line.separator");

			// decrypt and validate the eid results
			if (inflated.contains("CipherValue"))
			{
				// get the symmetric key
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = null;
				builder = factory.newDocumentBuilder();
				Document samlDocument = builder.parse(new ByteArrayInputStream(inflated.getBytes()));
				Result<byte[]> symmResult = getSymmetricKey(samlDocument);
				result += symmResult.getMessage() + System.getProperty("line.separator");
				success &= symmResult.wasSuccessful();
				if (symmResult.wasSuccessful() && symmResult.getComputed().isPresent())
				{
					// decrypt the assertion
					Result<String> decryptResult = decryptAssertion(samlDocument, symmResult.getComputed().get());
					result += decryptResult.getMessage();
					success &= decryptResult.wasSuccessful();
					if (decryptResult.wasSuccessful() && decryptResult.getComputed().isPresent())
					{
						// validate the result
						Result validateResult = validateAssertion(decryptResult.getComputed().get());
						result += validateResult.getMessage();
						success &= validateResult.wasSuccessful();
					}
				}
			}
			else
			{
				success = false;
				result += "Message did not contain an encrypted assertion" + System.getProperty("line.separator");
			}

		}
		else
		{
			success = false;
			result += "Could not inflate SAML assertion" + System.getProperty("line.separator");
		}
		return new Result(success, result);
	}

	/**
	 * Validate the assertion
	 * 
	 * @param decryptedAssertion
	 * @return
	 * @throws TransformerException
	 */
	@SuppressWarnings("rawtypes")
	private Result validateAssertion(String decryptedAssertion) throws TransformerException
	{
		String message = "";
		boolean success = true;

		if (decryptedAssertion != null && decryptedAssertion.length() > 0)
		{
			// check data groups
			KnownValues requestedValues = knownValues.getStartingWith(GeneralConstants.ALLOWED_BY_USER_PREFIX);
			Set<RequestAttribute> attributeSet = new HashSet<>();
			Set<SpecialFunction> functionSet = new HashSet<>();
			for (KnownValue value : requestedValues)
			{
				if (GeneralConstants.PERMISSION_ALLOWED.equals(value.getValue()))
				{
					RequestAttribute ra = RequestAttribute.getFromName(value.getName(), true);
					if (ra != null)
					{
						attributeSet.add(ra);
					}
					else
					{
						functionSet.add(SpecialFunction.getFromName(value.getName(), true));
					}
				}
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			NodeList nodeList = null;
			try
			{
				factory.setNamespaceAware(true);
				builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new ByteArrayInputStream(decryptedAssertion.getBytes("utf-8"))));
				nodeList = getElementsByLocalTagName(document, "Attribute");
			}
			catch (ParserConfigurationException | SAXException | IOException e)
			{
				success = false;
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("Could not parse SAML assertion due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
			}

			// iterate over all nodes and search for the requested values
			boolean checkedValidity = false;
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				if (nodeList.item(i).hasAttributes())
				{
					Node nameNode = nodeList.item(i).getAttributes().getNamedItem("Name");
					if (null != nameNode)
					{
						RequestAttribute foundAttribute = RequestAttribute.getFromName(nameNode.getNodeValue(), false);
						if (null != foundAttribute)
						{
							if (attributeSet.contains(foundAttribute))
							{
								message += "Attribute " + nameNode.getNodeValue() + " was found";
								if (null != nodeList.item(i).getFirstChild() && "AttributeValue".equals(nodeList.item(i).getFirstChild().getLocalName()))
								{
									KnownValue value = knownValues.get(GeneralConstants.PERSONAL_PREFIX + nameNode.getNodeValue().toUpperCase());
									String content = prettyPrintNode(nodeList.item(i).getFirstChild(), true);
									logger.debug("Checking SAML Attribute content: " + content);

									// if (nodeList.item(i).getFirstChild().getFirstChild().getNodeValue().equals(value.getValue()))
									if (content != null && value != null && content.equals(value.getValue()))
									{
										message += " and contained the correct value " + value.getValue() + ".";
									}
									else
									{
										// message += ", but it contained the wrong value " + nameNode.getFirstChild().getNodeValue() + ". The expected value was " + value.getValue() + ".";
										message += ", but it contained the wrong value " + content + ". The expected value was " + ((value != null) ? value.getValue() : "not set") + ".";
										success = false;
									}
								}
								else
								{
									message += ", but no data has been read for that element.";
									success = false;
								}
							}
							else
							{
								message += "Unexpected attribute " + foundAttribute.toString() + " was found.";
								success = false;
							}
							// remove the attribute from the list of expected functions
							attributeSet.remove(foundAttribute);
						}
						else
						{
							SpecialFunction foundFunction = SpecialFunction.getFromName(nameNode.getNodeValue(), false);
							if (null != foundFunction)
							{
								if (functionSet.contains(foundFunction))
								{
									message += "Special function " + nameNode.getNodeValue() + " was found";
									if (null != nodeList.item(i).getFirstChild() && "AttributeValue".equals(nodeList.item(i).getFirstChild().getLocalName()))
									{
										Node attrValue = nodeList.item(i).getFirstChild();
										if ("Result".equals(attrValue.getLastChild().getLocalName()))
										{
											if (!GeneralConstants.DEBUG_MODE && !"FulfilsRequest".equals(attrValue.getLastChild().getFirstChild().getLocalName()))
											{
												message += ", but it did not contain the 'FulfilsRequest' child element ";
												success = false;
											}
											KnownValue value = knownValues.get(GeneralConstants.PERSONAL_PREFIX + nameNode.getNodeValue().toUpperCase());
											if (value == null)
											{
												message += ", but the value was not verified due to missing comparison data in the test description (ambiguous result)";
											}
											else
											{
												Node resultNode = attrValue.getLastChild().getFirstChild();
												if ("FulfilsRequest".equals(resultNode.getLocalName()))
												{
													resultNode = resultNode.getFirstChild();
												}
												if (value.getValue().toUpperCase().equals(resultNode.getNodeValue().toUpperCase()))
												{
													message += " and contained the correct value " + value.getValue() + ".";
												}
												else
												{
													message += ", but contained the wrong value " + attrValue.getLastChild().getFirstChild().getNodeValue() + ".";
													success = false;
												}
											}
										}
										else if ("ID".equals(attrValue.getLastChild().getLocalName()) || "ID2".equals(attrValue.getLastChild().getLocalName()))
										{
											KnownValue value = knownValues.get(GeneralConstants.PERSONAL_PREFIX + attrValue.getLastChild().getLocalName().toUpperCase());
											if (value == null)
											{
												message += ", but the value was not verified due to missing comparison data in the test description (ambiguous result)";
											}
											else
											{
												if (value.getValue().toUpperCase().equals(attrValue.getLastChild().getFirstChild().getNodeValue().toUpperCase()))
												{
													message += " and contained the correct value " + value.getValue() + ".";
												}
												else
												{
													message += ", but contained the wrong value " + attrValue.getLastChild().getFirstChild().getNodeValue() + ".";
													success = false;
												}
											}
										}
										else
										{
											message += ", but it did not contain a result.";
											success = false;
										}
									}
									else
									{
										message += ", but no data has been read for that element.";
										success = false;
									}
									// remove the special function from the list of expected functions
									functionSet.remove(foundFunction);
								}
								else
								{
									message += "Unexpected special function " + foundFunction.toString() + " was found.";
									success = false;
								}
							}
							else
							{
								if (Replaceable.DOCUMENT_VALIDITY.toString().replace("_", "").equals(nameNode.getNodeValue().toUpperCase()))
								{
									if (null != nodeList.item(i).getFirstChild() && "AttributeValue".equals(nodeList.item(i).getFirstChild().getLocalName()))
									{
										Node attrValue = nodeList.item(i).getFirstChild();
										if (attrValue.getLastChild().getLocalName().equals("Status"))
										{
											KnownValue value = knownValues.get(Replaceable.DOCUMENT_VALIDITY.toString());
											if (attrValue.getLastChild().getFirstChild().getNodeValue().toUpperCase().equals(value.getValue().toUpperCase()))
											{
												message += "Document validity has been successfully checked.";
												checkedValidity = true;
											}
											else
											{
												message += "The message did contain the document validity element, but the status was " + attrValue.getLastChild().getFirstChild()
														+ ". The expected status was " + value.getValue() + ".";
												success = false;
											}
										}
										else
										{
											message += "The message did contain the document validity element, but no status has been provided.";
											success = false;
										}
									}
									else
									{
										message += "The document validity element did not contain any child elements.";
										success = false;
									}
								}
								else
								{
									message += "Received attribute " + nameNode.getNodeValue() + " that has not been requested.";
									success = false;
								}
							}
						}
						message += System.getProperty("line.separator");
					}
				}
			}
			if (!checkedValidity)
			{
				message += "The server did not check the document validity.";
				success = false;
			}

			// check for missing data
			if (!attributeSet.isEmpty())
			{
				final StringBuilder attributeBuilder = new StringBuilder();
				functionSet.forEach(f -> attributeBuilder.append("Did not receive data for attribute " + f.toString() + System.getProperty("line.separator")));
				message += attributeBuilder.toString();
				success = false;
			}
			if (!functionSet.isEmpty())
			{
				final StringBuilder functionBuilder = new StringBuilder();
				functionSet.forEach(f -> functionBuilder.append("Did not receive data for special function " + f.toString() + System.getProperty("line.separator")));
				message += functionBuilder.toString();
				success = false;
			}
		}
		return new Result(success, message);
	}

	/**
	 * Decrypt the assertion using the symmetric key
	 * 
	 * @param samlDocument
	 * @param symmetricKey
	 * @return
	 * @throws TransformerException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	private Result<String> decryptAssertion(Document samlDocument, byte[] symmetricKey) throws TransformerException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		String message = "";
		boolean success = true;

		// decrypt the assertion

		NodeList cipherNodes = samlDocument.getElementsByTagName("xenc:CipherValue");
		Node cipherNode = null;
		for (int i = 0; i < cipherNodes.getLength(); i++)
		{
			cipherNode = cipherNodes.item(i);
			// CipherData -> EncryptedKey
			if (!"xenc:EncryptedKey".equals(cipherNode.getParentNode().getParentNode().getNodeName()))
			{
				break;
			}
		}
		String assertionContent = null;
		if (cipherNode == null || cipherNode.getTextContent().length() == 0)
		{
			message += "No encrypted data found in Assertion.";
			success = false;
		}
		else
		{
			// algorithm
			NodeList encryptionNodes = samlDocument.getElementsByTagName("xenc:EncryptionMethod");
			Node encryptionNode = null;
			for (int i = 0; i < encryptionNodes.getLength(); i++)
			{
				encryptionNode = encryptionNodes.item(i);
				if (!"xenc:EncryptedKey".equals(encryptionNode.getParentNode().getNodeName()))
				{
					break;
				}
			}
			String algUrl = encryptionNode.getAttributes().getNamedItem("Algorithm").getTextContent();
			IcsXmlsecEncryptionContentUri alg = IcsXmlsecEncryptionContentUri.fromValue(algUrl);
			Cipher symm = null;
			SecretKeySpec secretKeySpec = null;
			int ivLength = -1;
			if (alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_128_CBC || alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_192_CBC
					|| alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2001_04_XMLENC_AES_256_CBC)
			{
				symm = Cipher.getInstance("AES/CBC/NoPadding", "BC");
				secretKeySpec = new SecretKeySpec(symmetricKey, "AES");
				ivLength = 16;
			}
			else if (alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_128_GCM || alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_192_GCM
					|| alg == IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_256_GCM)
			{
				symm = Cipher.getInstance("AES/GCM/NoPadding", "BC");
				secretKeySpec = new SecretKeySpec(symmetricKey, "AES");
				ivLength = 16;
			}
			// note: camelia is ignored here
			else
			{
				symm = Cipher.getInstance("DESede/CBC/NoPadding", "BC");
				secretKeySpec = new SecretKeySpec(symmetricKey, "DES");
				ivLength = 8;
			}
			// IV = first ivLength bytes of the ciphertext
			byte[] deB64 = Base64.decode(cipherNode.getTextContent());
			IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(deB64, 0, ivLength));
			symm.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
			byte[] decoded = symm.doFinal(Arrays.copyOfRange(deB64, ivLength, deB64.length));
			if (decoded != null && decoded.length > 0)
			{
				assertionContent = IOUtils.toString(decoded, "UTF-8");
			}
			else
			{
				message += "Error while decoding the encrypted Assertion";
				success = false;
			}
		}

		assertionContent = assertionContent.substring(0, assertionContent.lastIndexOf("Assertion>") + "Assertion>".length());
		logger.debug("Decrypted assertion: " + assertionContent);

		String prettyDecrypted = prettyPrintXml(assertionContent, false);
		message += "Successfully decrypted Assertion content:" + System.getProperty("line.separator") + prettyDecrypted + System.getProperty("line.separator");
		Result<String> decryptResult = new Result<>(success, message);
		if (success)
		{
			decryptResult.setComputed(assertionContent);
		}
		return decryptResult;
	}

	/**
	 * Extract the symmetric key from the {@link Document}
	 * 
	 * @param samlDocument
	 * @return
	 */
	private Result<byte[]> getSymmetricKey(Document samlDocument)
	{
		String message = "";
		boolean success = true;
		NodeList keyNodes = samlDocument.getElementsByTagName("xenc:EncryptedKey");
		byte[] symmetricKeyBytes = null;
		if (keyNodes.getLength() > 1)
		{
			message += "More than one symmetric key node found in assertion, aborting.";
			success = false;
		}
		else
		{
			// check algorithm
			NodeList keyChildren = keyNodes.item(0).getChildNodes();
			Node algorithm = null;
			Node keyData = null;
			boolean isOEPE = false;
			for (int i = 0; i < keyChildren.getLength(); i++)
			{
				Node node = keyChildren.item(i);
				if ("xenc:EncryptionMethod".equals(node.getNodeName()))
				{
					algorithm = node;
				}
				else if ("xenc:CipherData".equals(node.getNodeName()))
				{
					keyData = node;
				}
			}
			if (algorithm == null)
			{
				message += "No asymmetric algorithm found to decrypt the Assertion.";
				success = false;
			}
			else
			{
				Node alg = algorithm.getAttributes().getNamedItem("Algorithm");
				if (alg == null)
				{
					message += "No asymmetric algorithm found to decrypt the Assertion.";
					success = false;
				}
				else if (!IcsXmlsecEncryptionKeyTransportUri.HTTP_WWW_W_3_ORG_2001_04_XMLENC_RSA_1_5.equals(IcsXmlsecEncryptionKeyTransportUri.fromValue(alg.getNodeValue())))
				{
					isOEPE = true;
					message += "RSA-OAEP is not supported yet";
					success = false;
				}
			}

			// decrypt
			if (keyData == null)
			{
				message += "No asymmetric key found to decrypt the Assertion.";
				success = false;
			}
			else
			{
				String algo = "RSA";
				CryptoHelper.Algorithm alg = CryptoHelper.Algorithm.RSA_ALG_ID;

				byte[] symmBytes = null;
				Cipher cipher;
				try
				{
					byte[] decodedKey = Base64.decode(keyData.getFirstChild().getTextContent());
					cipher = Cipher.getInstance(algo, "BC");
					cipher.init(Cipher.DECRYPT_MODE, CryptoHelper.loadKey(CryptoHelper.Protocol.SAML_PROT_ID, alg, (service + GeneralConstants.ENCRYPTION_SUFFIX)));
					cipher.update(decodedKey);
					symmBytes = cipher.doFinal();
				}
				catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | DOMException e)
				{
					success = false;
					StringWriter trace = new StringWriter();
					e.printStackTrace(new PrintWriter(trace));
					logger.error("Could not decrypt symmetric SAML key due to: " + e.getMessage() + System.getProperty("line.separator") + trace.toString());
				}
				if (symmBytes != null && symmBytes.length > 0)
				{
					// RSA 1_5, strip leading padding
					if (!isOEPE)
					{
						if (symmBytes[0] != (byte) 0x02)
						{
							message += "Decryption of the symmetric key failed - padded bytes not starting with 0x02";
							success = false;
						}
						else
						{
							int startFrom = 0;
							while (startFrom < symmBytes.length)
							{
								if (symmBytes[startFrom++] == (byte) 0x00)
								{
									break;
								}
							}
							if (startFrom >= symmBytes.length)
							{
								message += "Decryption of the symmetric key failed - padding error detected";
								success = false;
							}
							else
							{
								symmetricKeyBytes = Arrays.copyOfRange(symmBytes, startFrom, symmBytes.length);
								message += "Successfully extracted symmetric key " + DatatypeConverter.printHexBinary(symmetricKeyBytes);
							}
						}
					}
				}
			}
		}

		Result<byte[]> result = new Result<>(success, message);
		if (symmetricKeyBytes != null)
		{
			result.setComputed(symmetricKeyBytes);
		}
		return result;
	}

	/**
	 * Returns a pretty-printed XML of the source and its children
	 * 
	 * @param source
	 * @param asText
	 * @return
	 * @throws TransformerException
	 */
	private String prettyPrint(Source source, boolean asText) throws TransformerException
	{
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		if (asText)
		{
			transformer.setOutputProperty(OutputKeys.METHOD, "text");
		}
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, new StreamResult(buffer));
		return buffer.toString();
	}

	/**
	 * Returns a pretty-printed XML of the string
	 * 
	 * @param source
	 * @param asText
	 * @return
	 * @throws TransformerException
	 */
	private String prettyPrintXml(String source, boolean asText) throws TransformerException
	{
		return prettyPrint(new StreamSource(new StringReader(source)), asText);
	}

	/**
	 * Returns a pretty-printed XML of the node and its children
	 * 
	 * @param node
	 * @param asText
	 * @return
	 * @throws TransformerException
	 */
	private String prettyPrintNode(Node node, boolean asText) throws TransformerException
	{
		return prettyPrint(new DOMSource(node), asText);
	}
}
