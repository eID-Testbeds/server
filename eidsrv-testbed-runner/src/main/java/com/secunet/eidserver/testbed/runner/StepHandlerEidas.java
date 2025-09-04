package com.secunet.eidserver.testbed.runner;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.eidas.*;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.exceptions.EidasErrorException;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Algorithm;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Protocol;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.*;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.utilities.JaxBUtil;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.XMLParserException;
import net.shibboleth.shared.xml.impl.BasicParserPool;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.AuthnRequestMarshaller;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import se.swedenconnect.opensaml.eidas.common.EidasLoaEnum;
import se.swedenconnect.opensaml.eidas.ext.SPTypeEnumeration;
import se.swedenconnect.opensaml.eidas.ext.attributes.EidasAttributeValueType;
import se.swedenconnect.opensaml.eidas.ext.attributes.impl.DateOfBirthTypeUnmarshaller;
import se.swedenconnect.opensaml.xmlsec.encryption.support.DecryptionUtils;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;


public class StepHandlerEidas extends StepHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StepHandlerEidas.class);

	public StepHandlerEidas(TestCandidate candidate, KnownValues values, LogMessageDAO logMessageDAO, EService service, EidCard card, int expectedNumOfCv)
	{
		super(candidate, values, logMessageDAO, service, card, expectedNumOfCv);

		super.fillCommonData(candidate, GeneralConstants.TESTBED_REFRESH_URL, card);
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE.toString(), candidate.getAttachedTcTokenUrl().toString()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_HOSTNAME.toString(), candidate.getAttachedTcTokenUrl().getHost()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_PATH.toString(), getNonEmptyPath(candidate.getAttachedTcTokenUrl())));
		// default request counter value
		knownValues.add(new KnownValue(Replaceable.REQUESTCOUNTER.toString(), GeneralConstants.DEFAULT_REQUEST_COUNTER));

	}

	@Override
	public List<LogMessage> validateStep(String receivedMessage, List<TestCaseStep> alternatives)
	{
		// redirect binding
		String testCaseName = alternatives.get(0).getName();
		boolean redirect;
		if ((redirect = TestStepType.IN_EIDAS_RESPONSE.toString().equals(testCaseName)) || TestStepType.IN_EIDAS_RESPONSE_POST.toString().equals(testCaseName))
		{
			LogMessage message;
			 if (redirect)
			 {
			 message = validateEidasResponse(parseSAMLResponse(receivedMessage, true), testCaseName);
			 }
			 else
			 {
			 message = validateEidasResponse(parseSAMLResponse(receivedMessage, false), testCaseName);
			 }
			return List.of(message);
		}
		else
		{
			return super.validateStep(receivedMessage, alternatives);
		}
	}

	/**
	 * Validate an unparsed received eIDAS SAML response
	 *
	 * @param receivedMessage Complete HTTP content including header
	 * @param testCaseName Name of TestCase. Important for correct logging / log parsing.
	 * @return Log message containing the result of this testcase
	 */
	private LogMessage validateEidasResponse(String receivedMessage, String testCaseName)  {
		LogMessage message = logMessageDAO.createNew();
		message.setTestStepName(testCaseName);
		String resultString;

		// fetch the data we will be comparing against
		KnownValues requestedValues = knownValues.getStartingWith(GeneralConstants.EIDAS_REQUESTED_PREFIX);
		Map<EidasNaturalPersonAttributes, Boolean> allowed = getRequestAttributes(requestedValues);
		KnownValues expectedAttributeValues = knownValues.getStartingWith(GeneralConstants.PERSONAL_PREFIX);
		Map<EidasNaturalPersonAttributes, String> expectedValues = new HashMap<>();
		for (KnownValue knownValue : expectedAttributeValues) {
			String plainEidasName = knownValue.getName().substring(GeneralConstants.PERSONAL_PREFIX.length());
			expectedValues.put(getNaturalAttributeFromName(plainEidasName), knownValue.getValue());
		}

		// decrypt the eidas assertion
		try {
			// X509Certificate cert = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.ECDSA_ALG_ID, service.toString());
			X509Certificate cert = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.RSA_ALG_ID, "A", candidate); // Was ECDSA and service.toString(). eIDAS-middleware does use other credentials with the provided config however...!


			InputStream is = IOUtils.toInputStream(receivedMessage, StandardCharsets.UTF_8);

			InputStream encryptionInput = new ByteArrayInputStream(CryptoHelper.getCryptoFileMap(candidate).get("keystores/" + Algorithm.RSA_ALG_ID.getAlgorithmName() + "/" + service.value() + GeneralConstants.ENCRYPTION_SUFFIX + ".p12"));

			EidasUtils.X509KeyPair encryptionPair = EidasUtils.readPKCS12(encryptionInput, "123456".toCharArray(), "ENC");

			Response openSamlResp = getOpenSamlResponse(is, cert);
			List<Assertion> assertions = processSAMLResponse(openSamlResp, cert, CredentialSupport.getSimpleCredential(encryptionPair.getCert(), encryptionPair.getKey()));
			List<EidasAttributeValueType> attributes = processAllAttributes(assertions);
			// validate the results

			message.setSuccess(true);
			StringBuilder resultStringBuilder = new StringBuilder();
			resultStringBuilder.append("Validating the eIDAS response").append(System.getProperty("line.separator"));
			for (EidasNaturalPersonAttributes allowedKey : allowed.keySet()) {
				boolean hadAttribute = false;
				for (EidasAttributeValueType attribute : attributes) {
					if (allowedKey.getAttributeClass().isAssignableFrom(attribute.getClass())) {
						hadAttribute = true;
						resultStringBuilder.append("Found attribute ").append(allowedKey.getFriendlyName()).append(" in the response.").append(System.getProperty("line.separator"));

						if (expectedValues.containsKey(allowedKey)) {
							if (allowed.get(allowedKey)) {
								if (attribute.toStringValue().equals(expectedValues.get(allowedKey))) {
									resultStringBuilder.append("The attribute ").append(allowedKey.getFriendlyName()).append(" did contain the expected value ").append(expectedValues.get(allowedKey)).append(System.getProperty("line.separator"));
								} else {
									resultStringBuilder.append("The attribute ").append(allowedKey.getFriendlyName()).append(" did contain the wrong value ").append(attribute.toStringValue()).append(System.getProperty("line.separator"));
									message.setSuccess(false);
								}
							} else {
								resultStringBuilder.append("The attribute ").append(allowedKey.getFriendlyName()).append(" was present although it has been deselected by the user.").append(System.getProperty("line.separator"));
								message.setSuccess(false);
							}
						} else {
							resultStringBuilder.append("The testcase did not contain comparison data for the attribute ").append(allowedKey.getFriendlyName()).append(System.getProperty("line.separator"));
							LOGGER.warn("The testcase did not contain comparison data for the attribute " + allowedKey.getFriendlyName());
						}

						break;
					}
				}
				if (!hadAttribute && allowed.get(allowedKey)) {
					resultStringBuilder.append("The requested attribute ").append(allowedKey.getFriendlyName()).append(" was missing from the assertion.").append(System.getProperty("line.separator"));
					message.setSuccess(false);
				}
			}
			message.setMessage(resultStringBuilder.toString());
			return message;
		} catch (EidasErrorException e) {
			resultString = "";
			// check, if there are any expected eIDAS errors
			KnownValues expectedErrorValues = knownValues.getStartingWith(GeneralConstants.EIDAS_ERROR_PREFIX);
			if (expectedErrorValues.isEmpty()) {
				message.setSuccess(false);
				resultString = "The SAML assertion indicated an unexpected error (code " + e.getErrorCode() + ").";
			} else {
				// check, if we received the expected eIDAS errors
				message.setSuccess(true);
				for (KnownValue toCheck : expectedErrorValues) {
					String expected = toCheck.getValue();
					String received = e.getErrorCode();
					if (expected.equals(received)) {
						resultString = "The eIDAS error attribute " + toCheck.getName() + " did contain the expected value " + expected + System.getProperty("line.separator");
					} else {
						resultString = "The eIDAS error attribute " + toCheck.getName() + " did contain the wrong value " + received + ". The expected value was " + expected
								+ System.getProperty("line.separator");
						message.setSuccess(false);
					}
				}
			}
			message.setMessage(resultString);
			return message;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			message.setMessage("Validating the eIDAS request failed due to " + e.getMessage());
			message.setSuccess(false);
			return message;
		}
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
			if (header.contains(Functional.CREATE_SAML.getTextMark()) || header.contains(Functional.CREATE_SAML_NO_SIGALG_NO_SIGNATURE.getTextMark())
					|| header.contains(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark()))
			{
				super.fillDataToRequest();

				// create SAML request for redirect bindings
				try
				{
					header = createEidasRequest(header, true, header.contains(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark()));
				}
				catch ( IOException | MarshallingException | SignatureException
						| TransformerFactoryConfigurationError | TransformerException | InitializationException ignore)
				{
					// An error happening here is an internal error.
				}
			}
			else
			{
				if (null != step.getProtocolStepToken() && !step.getProtocolStepToken().isEmpty())
				{
					StepToken protocolToken = step.getProtocolStepToken().get(0);
					protocolBody = protocolToken.getValue();
					if (protocolBody.contains(Functional.CREATE_SAML.getTextMark()))
					{
						super.fillDataToRequest();

						// create SAML request for post bindings
						try
						{
							protocolBody = createEidasRequest(protocolBody, false, protocolBody.contains(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark()));
						}
						catch ( IOException | MarshallingException | SignatureException
								| TransformerFactoryConfigurationError | TransformerException | InitializationException ignore)
						{
							// An error happening here is an internal error
						}
					}
				}
			}
			// generate the rest of the message
			String returnMsg = super.generateMsg(header, protocolBody);

			// save values
			if (step.getToSave() != null && !step.getToSave().isEmpty())
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

	/**
	 * Create the eIDAS AuthnRequest
	 *
	 * @param token Message template containing one of the appropriate placeholders
	 * @param isRedirect if the request should be used for an HTTP redirect (means, it is compressed using deflate)
	 * @param stripSignature remove the signature after signing for test purposes
	 * @return Message with placeholders replaced
	 * @throws IOException If a required file could not be read
	 * @throws TransformerException If an internal error occurs with XML transformation. Should not happen.
	 * @throws MarshallingException If an internal error occurs with XML marshalling. Should not happen.
	 * @throws SignatureException If the signature fails
	 * @throws InitializationException If the AuthnRequest class could not be initialized properly. Should not happen.
	 */
	private String createEidasRequest(String token, boolean isRedirect, boolean stripSignature) throws IOException, TransformerException, MarshallingException, SignatureException, InitializationException {
		X509Certificate signCertificate;
		PrivateKey signKey;
		String algorithm;

		// TODO disabled due to application server restrictions. Re-enable once the xmlsec library is updated
		// if (service == EService.EECDSA)
		// {
		signCertificate = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.ECDSA_ALG_ID, EService.F.toString(), candidate);

		if (signCertificate == null)
			throw new IOException("Signature certificate could not be read.");
		algorithm = Algorithm.ECDSA_ALG_ID.getAlgorithmName();


		try (InputStream input = new ByteArrayInputStream(CryptoHelper.getCryptoFileMap(candidate).get("keys/" + algorithm + "/F.pem"))) {
            try (InputStreamReader isr = new InputStreamReader(input);
                 PEMParser pemParser = new PEMParser(isr))
            {
                Object parsed = pemParser.readObject();

				PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) parsed;
				JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                signKey = converter.getPrivateKey(privateKeyInfo);
            }
        }

		String destination = knownValues.get(Replaceable.ATTACHED_WEBPAGE.toString()).getValue();
		String issuer = "https://doesnotexi.st";
		String provider = null;


		X509Credential signCreds = new BasicX509Credential(signCertificate,signKey);
		KnownValues requestAttributes = knownValues.getStartingWith(GeneralConstants.EIDAS_REQUESTED_PREFIX);

		Map<EidasNaturalPersonAttributes, Boolean> requestedAttributesMap = getRequestAttributes(requestAttributes);
		SPTypeEnumeration type = SPTypeEnumeration.PUBLIC;
		if (this.service != EService.A) {
			type = SPTypeEnumeration.PRIVATE;
			provider = "Testbed" + this.service.toString();
		}

		byte[] result;
		if (knownValues.get("ADD_RANDOM_ATTRIBUTE") != null)
		{
			// Decompiled old eidas-starterkit-1.0.1.jar and rebuilt this functionality
			result = createRequest(issuer, destination, signCreds, requestedAttributesMap, type, NameID.TRANSIENT, EidasLoaEnum.LOA_HIGH, provider, true);
		}
		else
		{
			result = createRequest(issuer, destination, signCreds, requestedAttributesMap, type, NameID.TRANSIENT, EidasLoaEnum.LOA_HIGH, provider);
		}
		LOGGER.debug("Created eIDAS request: " + new String(result, StandardCharsets.UTF_8));

		if (stripSignature || knownValues.get("BREAK_XML_SIGNATURE") != null)
		{
			// String request = new String(result, "UTF-8");
			Source source = new StreamSource(new ByteArrayInputStream(result));
			DOMResult domResult = new DOMResult();
			TransformerFactory.newInstance().newTransformer().transform(source, domResult);
			Document document = (Document) domResult.getNode();
			Element signatureNode = (Element) document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0);

			if (stripSignature)
			{
				signatureNode.getParentNode().removeChild(signatureNode);
			}
			else
			{
				BitSet bitSet = BitSet.valueOf(Base64.decodeBase64(signatureNode.getTextContent()));
				bitSet.flip(0, bitSet.length());
				signatureNode.setTextContent(Base64.encodeBase64String(bitSet.toByteArray()));
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(document), new StreamResult(bos));
			result = bos.toByteArray();
		}

		String encoded;
		// we have to deflate the result in case it is a redirect call
		if (isRedirect)
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
			DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(os, deflater);
			deflaterOutputStream.write(result);
			deflaterOutputStream.close();
			os.close();
			encoded = Base64.encodeBase64String(os.toByteArray());
		}
		else
		{
			encoded = Base64.encodeBase64String(result);
		}
		encoded = URLEncoder.encode(encoded, StandardCharsets.UTF_8);

		// replace the placeholder
		if (stripSignature)
		{
			token = token.replace(Functional.CREATE_SAML_NO_SIGNATURE.getTextMark(), encoded);
		}
		else
		{
			token = token.replace(Functional.CREATE_SAML.getTextMark(), encoded);
		}
		return token;
	}

	/**
	 * Transform a {@link KnownValues} object into a easily parseable
	 * map of EidasNaturalPersonAttributes and Boolean (for the REQUIRED attribute)
	 *
	 * @param requestAttributes {@link KnownValues} of this test step
	 * @return easily parseable map
	 */
	private Map<EidasNaturalPersonAttributes, Boolean> getRequestAttributes(KnownValues requestAttributes)
	{
		Map<EidasNaturalPersonAttributes, Boolean> requestedAttributesMap = new HashMap<>();
		for (KnownValue requested : requestAttributes)
		{
			String plainEidasName = requested.getName().substring(GeneralConstants.EIDAS_REQUESTED_PREFIX.length());
			boolean permission = GeneralConstants.PERMISSION_REQUIRED.equals(requested.getValue());
			requestedAttributesMap.put(getNaturalAttributeFromName(plainEidasName), permission);
		}
		return requestedAttributesMap;
	}

	/**
	 * Search {@link EidasNaturalPersonAttributes} enum for supplied name
	 *
	 * @param name name to search for
	 * @return EidasNaturalPersonAttributes object or null if not found
	 */
	private EidasNaturalPersonAttributes getNaturalAttributeFromName(String name)
	{
		for (EidasNaturalPersonAttributes attribute : EidasNaturalPersonAttributes.values())
		{
			if (attribute.getFriendlyName().equals(name))
			{
				return attribute;
			}
		}
		LOGGER.warn("Could not find the eIDAS attribute corresponding to " + name);
		return null;
	}

	/**
	 * Parse the complete server response (that is including headers)
	 * Output consists of SAML XML as String
	 *
	 * @param message raw server response
	 * @param isRedirect if it is an HTTP redirect or not
	 * @return SAML XML
	 */
	private String parseSAMLResponse(String message, boolean isRedirect)
	{
		Pattern p;
		if (isRedirect)
		{
			p = Pattern.compile("(SAMLResponse=)(.*?)(\n)", Pattern.DOTALL);
		}
		else
		{
			p = Pattern.compile("(<input name=\"SAMLResponse\" value=\")(.*?)(\" type=\"hidden\"/>)", Pattern.DOTALL);
		}
		Matcher m = p.matcher(message);
		String samlResponse = null;
		while (m.find()) // find last occurrence of given pattern
		{
			samlResponse = m.group(2);
			byte[] decodedAssertion = Base64.decodeBase64(samlResponse);
			if (isRedirect)
			{
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				Inflater inflater = new Inflater(true);
				InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream, inflater);
				try
				{
					inflaterOutputStream.write(decodedAssertion);
					inflaterOutputStream.close();
					samlResponse = byteArrayOutputStream.toString();
					inflaterOutputStream.close();
					byteArrayOutputStream.close();
				}
				catch (IOException e)
				{
					LOGGER.error("Unable to inflate the eIDAS assertion due to " + e.getMessage(), e);
				}
			}
			else
			{
				samlResponse = new String(decodedAssertion, StandardCharsets.UTF_8);
			}
		}

		return samlResponse;
	}

	/**
	 * Unmarshal the SAML Response and validates it against the given certificate
	 * Partly copied from Eidas-Middleware.
	 *
	 * @param is InputStream
	 * @param cert certificate to validate against
	 * @return SAML Response Object
	 * @throws ComponentInitializationException If OpenSAML could not be initialized properly
	 * @throws XMLParserException If there is invalid XML
	 * @throws UnmarshallingException If there is an error during unmarshalling
	 */
	private static Response getOpenSamlResponse(InputStream is, X509Certificate cert)
			throws ComponentInitializationException, XMLParserException, UnmarshallingException, SecurityException, SignatureException {
		BasicParserPool ppMgr = EidasUtils.getBasicParserPool();
		Document inCommonMDDoc = ppMgr.parse(is);
		Element metadataRoot = inCommonMDDoc.getDocumentElement();
		// Get appropriate unmarshaller
		UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
		if (unmarshaller != null) {
			Response r = (Response) unmarshaller.unmarshall(metadataRoot);
			EidasAuthnRequestHandler.validate(r, cert);
			return r;
		} else {
			throw new UnmarshallingException("Could not create unmarshaller for Response");
		}
	}

	/**
	 * Decrypt and validate (if needed) the Assertions of a SAML response
	 *
	 * @param resp SAML Response object
	 * @param cert Signature validation certificate
	 * @param decryptionCredential KeyPair to decrypt Assertions with.
	 * @return List of decrypted SAML Assertions
	 * @throws DecryptionException If the decryption fails
	 * @throws SecurityException If there is a problem building the signature chain of trust. Should not happen normally.
	 * @throws SignatureException If there is a problem validating an assertion's signature
	 * @throws EidasErrorException If the SAML Response has an error code different from SUCCESS
	 */
	private static List<Assertion> processSAMLResponse(Response resp,
											X509Certificate cert,
											Credential decryptionCredential)
			throws DecryptionException, SecurityException, SignatureException, EidasErrorException {
		if (!StatusCode.SUCCESS.equals(resp.getStatus().getStatusCode().getValue()))
		{
			LOGGER.error("Error happened: " + resp.getStatus().getStatusCode().getValue());
			throw new EidasErrorException(resp.getStatus().getStatusCode().getValue());
		}
		else
		{
			OuterDecrypter decr = new OuterDecrypter(DecryptionUtils.createDecryptionParameters(decryptionCredential));
			decr.setRootInNewDocument(true);
			List<Assertion> assertions = new ArrayList<>();
			for (EncryptedAssertion enc : resp.getEncryptedAssertions()) {
				Assertion decrypted = decr.decrypt(enc);
				if (decrypted.getSignature() != null) {
					EidasAuthnRequestHandler.validate(decrypted,cert);
				}
				assertions.add(decrypted);
			}
			return assertions;
		}
	}

	/**
	 * Extract eIDAS Attributes from Assertions
	 *
	 * @param assertions The assertions to parse
	 * @return	List of eIDAS Attributes contained in the supplied assertions
	 * @throws UnmarshallingException If there was a problem unmarshalling the attributes or attribute statements
	 */
	private static List<EidasAttributeValueType> processAllAttributes(List<Assertion> assertions) throws UnmarshallingException {
		List<EidasAttributeValueType> attributes = new ArrayList<>();
		for (Assertion assertion : assertions) {
			for (AttributeStatement as : assertion.getAttributeStatements()) {
				for (Attribute att : as.getAttributes()) {
					if (att.getAttributeValues().isEmpty()) {
						continue;
					}

				//EidasNaturalPersonAttributes personAttributes = EidasNaturalPersonAttributes.getValueOf(att.getName());
					for (XMLObject attributeValue : att.getAttributeValues()) {
						Element domElement = attributeValue.getDOM();
						if (domElement != null) {
							UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
							Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(domElement);
							if (unmarshaller != null) {
								EidasAttributeValueType eidasAttribute;
								LOGGER.warn("XUnmarshalling " + att.getName() + " with " + unmarshaller.getClass().getName());
								if (unmarshaller instanceof DateOfBirthTypeUnmarshaller)
									unmarshaller = new TolerantDateOfBirthTypeUnmarshaller(); //Workaround for Date with missing parts
								LOGGER.warn("XUnmarshalling " + att.getName() + " with " + unmarshaller.getClass().getName());
								eidasAttribute = (EidasAttributeValueType) unmarshaller.unmarshall(domElement);
								attributes.add(eidasAttribute);
							} else {
								throw new UnmarshallingException("Could not create unmarshaller for EidasAttributeValueType");
							}
						} else {
							throw new UnmarshallingException("DOM of AttributeValue was empty");
						}

					}
				}
			}
		}
		return attributes;
	}

	/**
	 * Create an eIDAS AuthnRequest
	 *
	 * @param _issuer Issuer
	 * @param _destination Destination
	 * @param _signerCredentials Credentials for XML Signature
	 * @param _requestedAttributes Map of requested natural person attributes and a boolean to tell if it is a required or optional one
	 * @param _selectorType Either {@link SPTypeEnumeration} PUBLIC or PRIVATE
	 * @param _nameIdPolicy Either {@link org.opensaml.saml.saml2.core.NameIDType} TRANSIENT, PERSISTENT or UNSPECIFIED as string
	 * @param _loa Either {@link EidasLoaEnum} LOW or HIGH
	 * @param _provider Provider
	 * @return Signed eIDAS AuthnRequest as a byte array
	 * @throws InitializationException If OpenSAML could not be initialized properly
	 * @throws SignatureException If there was a problem signing the request
	 * @throws MarshallingException If there was a problem marshalling the request to byte array
	 * @throws TransformerException TODO: Look when it happens
	 * @throws IOException If there was a problem reading a certificate file
	 */
	private static byte[] createRequest(String _issuer, String _destination, X509Credential _signerCredentials, Map<EidasNaturalPersonAttributes, Boolean> _requestedAttributes, SPTypeEnumeration _selectorType, @SuppressWarnings("SameParameterValue") String _nameIdPolicy, @SuppressWarnings("SameParameterValue") EidasLoaEnum _loa, String _provider) throws InitializationException, SignatureException, MarshallingException, TransformerException, IOException {
		return createRequest(_issuer, _destination, _signerCredentials, _requestedAttributes, _selectorType, _nameIdPolicy, _loa, _provider, false);
	}

	/**
	 * Create an eIDAS AuthnRequest
	 *
	 * @param _issuer Issuer
	 * @param _destination Destination
	 * @param _signerCredentials Credentials for XML Signature
	 * @param _requestedAttributes Map of requested natural person attributes and a boolean to tell if it is a required or optional one
	 * @param _selectorType Either {@link SPTypeEnumeration} PUBLIC or PRIVATE
	 * @param _nameIdPolicy Either {@link org.opensaml.saml.saml2.core.NameIDType} TRANSIENT, PERSISTENT or UNSPECIFIED as string
	 * @param _loa Either {@link EidasLoaEnum} LOW or HIGH
	 * @param _provider Provider
	 * @param addRandomAttribute Adds an attribute named "RANDOM_ATTRIBUTE" if true. Was present in patched eIDAS starterkit and used in this handler.
	 * @return Signed eIDAS AuthnRequest as a byte array
	 * @throws InitializationException If OpenSAML could not be initialized properly
	 * @throws SignatureException If there was a problem signing the request
	 * @throws MarshallingException If there was a problem marshalling the request to byte array
	 * @throws TransformerException TODO: Look when it happens
	 * @throws IOException If there was a problem reading a certificate file
	 */
	private static byte[] createRequest(String _issuer, String _destination, X509Credential _signerCredentials, Map<EidasNaturalPersonAttributes, Boolean> _requestedAttributes, SPTypeEnumeration _selectorType, String _nameIdPolicy, EidasLoaEnum _loa, String _provider, boolean addRandomAttribute) throws InitializationException, SignatureException, MarshallingException, TransformerException, IOException {
		EidasAuthnRequestHandler.Init();
		AuthnRequest authnRequest = EidasAuthnRequestHandler.createAuthnRequest("_" + UUID.randomUUID(), _destination, _issuer, _selectorType, _requestedAttributes, _nameIdPolicy, _loa, _provider, addRandomAttribute);
		AuthnRequestMarshaller arm = new AuthnRequestMarshaller();

		EidasAuthnRequestHandler.sign(authnRequest, _signerCredentials);
		Element all = arm.marshall(authnRequest);
		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		try(ByteArrayOutputStream bout = new ByteArrayOutputStream()){
			trans.transform(new DOMSource(all), new StreamResult(bout));
			return bout.toByteArray();
		}
	}

}
