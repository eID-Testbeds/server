package com.secunet.eidserver.testbed.runner.eidas;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.naming.ConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Functional;
import com.secunet.eidserver.testbed.common.enumerations.Replaceable;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Algorithm;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper.Protocol;
import com.secunet.eidserver.testbed.common.interfaces.dao.LogMessageDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import com.secunet.eidserver.testbed.common.types.testcase.Step;
import com.secunet.eidserver.testbed.common.types.testcase.StepToken;
import com.secunet.eidserver.testbed.common.types.testcase.TestStepType;
import com.secunet.eidserver.testbed.runner.KnownValue;
import com.secunet.eidserver.testbed.runner.KnownValues;
import com.secunet.eidserver.testbed.runner.StepHandler;
import com.secunet.eidserver.testbed.runner.exceptions.EphemeralKeyNotFoundException;
import com.secunet.eidserver.testbed.runner.exceptions.InvalidTestCaseDescriptionException;
import com.secunet.eidserver.testbed.runner.exceptions.SharedSecretNotYetReadyException;
import com.secunet.testbedutils.utilities.JaxBUtil;

import de.governikus.eumw.eidascommon.ErrorCodeException;
import de.governikus.eumw.eidascommon.Utils;
import de.governikus.eumw.eidasstarterkit.EidasAttribute;
import de.governikus.eumw.eidasstarterkit.EidasLoA;
import de.governikus.eumw.eidasstarterkit.EidasNameIdType;
import de.governikus.eumw.eidasstarterkit.EidasNaturalPersonAttributes;
import de.governikus.eumw.eidasstarterkit.EidasRequestSectorType;
import de.governikus.eumw.eidasstarterkit.EidasResponse;
import de.governikus.eumw.eidasstarterkit.EidasSaml;
import de.governikus.eumw.eidasstarterkit.EidasSigner;
import de.governikus.eumw.eidasstarterkit.person_attributes.AbstractLatinScriptAttribute;
import de.governikus.eumw.eidasstarterkit.person_attributes.EidasPersonAttributes;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

public class StepHandlerEidas extends StepHandler
{
	private static Logger LOGGER = LoggerFactory.getLogger(StepHandlerEidas.class);

	public StepHandlerEidas(String candidateName, URL eCardApiUrl, KnownValues values, LogMessageDAO logMessageDAO, EService service, EidCard card, int expectedNumOfCv, URL attachedUrl)
	{
		super(candidateName, values, logMessageDAO, service, card, expectedNumOfCv);

		super.fillCommonData(candidateName, eCardApiUrl, GeneralConstants.TESTBED_REFRESH_URL, card);
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE.toString(), attachedUrl.toString()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_HOSTNAME.toString(), attachedUrl.getHost()));
		knownValues.add(new KnownValue(Replaceable.ATTACHED_WEBPAGE_PATH.toString(), getNonEmptyPath(attachedUrl)));
		// default request counter value
		knownValues.add(new KnownValue(Replaceable.REQUESTCOUNTER.toString(), GeneralConstants.DEFAULT_REQUEST_COUNTER));

		// init santuario and the eidas starterkit
		try
		{
			EidasSaml.init();
		}
		catch (InitializationException e)
		{
			LOGGER.error("Could not initialize XML security engine: " + e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	public List<LogMessage> validateStep(String receivedMessage, List<TestCaseStep> alternatives)
	{
		// redirect binding
		String testCaseName = alternatives.get(0).getName();
		boolean redirect = false;
		if ((redirect = TestStepType.IN_EIDAS_RESPONSE.toString().equals(testCaseName)) || TestStepType.IN_EIDAS_RESPONSE_POST.toString().equals(testCaseName))
		{
			LogMessage message = null;
			// try
			// {
			// TODO DISABLE THIS WORKAROUND
			message = WORKAROUND(receivedMessage, testCaseName);
			// if (redirect)
			// {
			// message = validateEidasResponse(parseSAMLResponse(receivedMessage, true), testCaseName);
			// }
			// else
			// {
			// message = validateEidasResponse(parseSAMLResponse(receivedMessage, false), testCaseName);
			// }
			// }
			// catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | IOException | InitializationException
			// | XMLParserException | UnmarshallingException | ComponentInitializationException e)
			// {
			// LOGGER.error("Validating the eIDAS request failed due to " + e.getMessage(), e);
			// }
			List<LogMessage> messages = new ArrayList<>();
			if (message != null)
			{
				messages.add(message);
			}
			return messages;
		}
		else
		{
			return super.validateStep(receivedMessage, alternatives);
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
				catch (CertificateEncodingException | ConfigurationException | IOException | XMLParserException | UnmarshallingException | MarshallingException | SignatureException
						| TransformerFactoryConfigurationError | TransformerException | InitializationException | ComponentInitializationException e)
				{
					LOGGER.error("Unable to create eIDAS request due to " + e.getMessage(), e);
					e.printStackTrace();
				}
			}
			else
			{
				if (null != step.getProtocolStepToken() && step.getProtocolStepToken().size() > 0)
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
						catch (CertificateEncodingException | ConfigurationException | IOException | XMLParserException | UnmarshallingException | MarshallingException | SignatureException
								| TransformerFactoryConfigurationError | TransformerException | InitializationException | ComponentInitializationException e)
						{
							LOGGER.error("Unable to create eIDAS request due to " + e.getMessage(), e);
							e.printStackTrace();
						}
					}
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

	private LogMessage validateEidasResponse(String receivedMessage, String testCaseName) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, NoSuchProviderException, InitializationException, XMLParserException, UnmarshallingException, ComponentInitializationException
	{
		// fetch the data we will be comparing against
		KnownValues requestedValues = knownValues.getStartingWith(GeneralConstants.EIDAS_REQUESTED_PREFIX);
		Map<EidasPersonAttributes, Boolean> allowed = getRequestAttributes(requestedValues);
		KnownValues expectedAttributeValues = knownValues.getStartingWith(GeneralConstants.PERSONAL_PREFIX);
		Map<EidasNaturalPersonAttributes, String> expectedValues = new HashMap<>();
		for (KnownValue knownValue : expectedAttributeValues)
		{
			String plainEidasName = knownValue.getName().substring(GeneralConstants.PERSONAL_PREFIX.length(), knownValue.getName().length());
			expectedValues.put(getNaturalAttributeFromName(plainEidasName), knownValue.getValue());
		}

		// decrypt the eidas assertion
		try
		{
			X509Certificate cert = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.ECDSA_ALG_ID, service.toString());


			InputStream is = IOUtils.toInputStream(receivedMessage, StandardCharsets.UTF_8);

			InputStream signatureInput = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("keystores/" + Algorithm.ECDSA_ALG_ID.getAlgorithmName() + "/" + service.toString() + ".p12");
			// readPKCS12 is private and needs to be patched
			Utils.X509KeyPair signaturePair = Utils.readPKCS12(signatureInput, "123456".toCharArray(), "SIG");
			InputStream encryptionInput = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("keystores/" + Algorithm.RSA_ALG_ID.getAlgorithmName() + "/" + service.toString() + GeneralConstants.ENCRYPTION_SUFFIX + ".p12");
			Utils.X509KeyPair encryptionPair = Utils.readPKCS12(encryptionInput, "123456".toCharArray(), "ENC");

			// TODO the key pair is handled through array operations inside the eidas starterkit. validate the correct order once this method is reached
			EidasResponse eidasResponse = EidasSaml.parseResponse(is, new Utils.X509KeyPair[] { encryptionPair }, new X509Certificate[] { cert });

			// validate the results
			LogMessage message = logMessageDAO.createNew();
			message.setSuccess(true);
			message.setTestStepName(testCaseName);
			String resultString = "Validating the eIDAS response" + System.getProperty("line.separator");
			for (EidasPersonAttributes allowedKey : allowed.keySet())
			{
				boolean hadAttribute = false;
				for (EidasAttribute attribute : eidasResponse.getAttributes())
				{
					if (attribute.getPersonAttributeType() == allowedKey)
					{
						hadAttribute = true;
						resultString += "Found attribute " + allowedKey.getFriendlyName() + " in the response." + System.getProperty("line.separator");

						if (expectedValues.containsKey(allowedKey))
						{
							if (allowed.get(allowedKey))
							{
								AbstractLatinScriptAttribute attr = (AbstractLatinScriptAttribute)attribute;
								if (attr.getPersonAttributeType().getValue().equals(expectedValues.get(allowedKey)))
								{
									resultString += "The attribute " + allowedKey.getFriendlyName() + " did contain the expected value " + expectedValues.get(allowedKey)
											+ System.getProperty("line.separator");
								}
								else
								{
									resultString += "The attribute " + allowedKey.getFriendlyName() + " did contain the wrong value " + attr.getPersonAttributeType().getFriendlyName() + System.getProperty("line.separator");
									message.setSuccess(false);
								}
							}
							else
							{
								resultString += "The attribute " + allowedKey.getFriendlyName() + " was present although it has been deselected by the user." + System.getProperty("line.separator");
								message.setSuccess(false);
							}
						}
						else
						{
							resultString += "The testcase did not contain comparison data for the attribute " + allowedKey.getFriendlyName() + System.getProperty("line.separator");
							LOGGER.warn("The testcase did not contain comparison data for the attribute " + allowedKey.getFriendlyName());
						}

						break;
					}
				}
				if (!hadAttribute && allowed.get(allowedKey))
				{
					resultString += "The requested attribute " + allowedKey.getFriendlyName() + " was missing from the assertion." + System.getProperty("line.separator");
					message.setSuccess(false);
				}
			}
			message.setMessage(resultString);
			return message;
		}
		catch (ErrorCodeException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private String createEidasRequest(String token, boolean isRedirect, boolean stripSignature)
			throws CertificateEncodingException, ConfigurationException, IOException, ConfigurationException, UnmarshallingException, MarshallingException, SignatureException,
			TransformerFactoryConfigurationError, TransformerException, XMLParserException, InitializationException, ComponentInitializationException
	{
		X509Certificate signCertificate = null;
		PrivateKey signKey = null;
		String algorithm = "";

		// TODO disabled due to application server restrictions. reenable once the xmlsec library is updated
		// if (service == EService.EECDSA)
		// {
		signCertificate = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.ECDSA_ALG_ID, EService.F.toString());
		// signCertificate = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.ECDSA_ALG_ID, service.toString());
		algorithm = Algorithm.ECDSA_ALG_ID.getAlgorithmName();
		// }
		// else
		// {
		// signCertificate = CryptoHelper.loadCertificate(Protocol.SAML_PROT_ID, Algorithm.RSA_ALG_ID, service.toString());
		// algorithm = Algorithm.RSA_ALG_ID.getAlgorithmName();
		// }
		// try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("keys/" + algorithm + "/" + service.toString() + ".pem");
		try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("keys/" + algorithm + "/F.pem"); InputStreamReader isr = new InputStreamReader(input);
				PEMParser pemParser = new PEMParser(isr);)
		{
			Object parsed = pemParser.readObject();
			KeyPair pair = new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) parsed);
			signKey = pair.getPrivate();
		}

		String destination = knownValues.get(Replaceable.ATTACHED_WEBPAGE.toString()).getValue();
		String issuer = "http://testbed.test";
		String provider = "Testbed" + this.service.toString();
		EidasSigner signer = new EidasSigner(true, signKey, signCertificate);
		KnownValues requestAttributes = knownValues.getStartingWith(GeneralConstants.EIDAS_REQUESTED_PREFIX);

		Map<EidasPersonAttributes, Boolean> requestedAttributesMap = getRequestAttributes(requestAttributes);
		EidasRequestSectorType type = EidasRequestSectorType.PUBLIC;
		if (!this.service.toString().endsWith("A")) {
			type = EidasRequestSectorType.PRIVATE;
		}

		byte[] result = null;
		if (knownValues.get("ADD_RANDOM_ATTRIBUTE") != null)
		{
			// TODO: had additional parameter true - clarify
			result = EidasSaml.createRequest(issuer, destination, provider, signer, requestedAttributesMap, type, EidasNameIdType.TRANSIENT, EidasLoA.HIGH);
		}
		else
		{
			result = EidasSaml.createRequest(issuer, destination, provider, signer, requestedAttributesMap, type, EidasNameIdType.TRANSIENT, EidasLoA.HIGH);
		}
		LOGGER.debug("Created eIDAS request: " + new String(result, "UTF-8"));

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

		String encoded = null;
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
		encoded = URLEncoder.encode(encoded, "UTF-8");

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

	private Map<EidasPersonAttributes, Boolean> getRequestAttributes(KnownValues requestAttributes)
	{
		Map<EidasPersonAttributes, Boolean> requestedAttributesMap = new HashMap<>();
		for (KnownValue requested : requestAttributes)
		{
			String plainEidasName = requested.getName().substring(GeneralConstants.EIDAS_REQUESTED_PREFIX.length(), requested.getName().length());
			boolean permission = GeneralConstants.PERMISSION_REQUIRED.equals(requested.getValue());
			requestedAttributesMap.put(getNaturalAttributeFromName(plainEidasName), permission);
		}
		return requestedAttributesMap;
	}

	private EidasNaturalPersonAttributes getNaturalAttributeFromName(String name)
	{
		for (EidasNaturalPersonAttributes attribute : EidasNaturalPersonAttributes.values())
		{
			if (attribute.getFriendlyName().equals(name))
			{
				return attribute;
			}
		}
		LOGGER.warn("Could not find the eIDAS attribute corresponing to " + name);
		return null;
	}

	private String parseSAMLResponse(String message, boolean isRedirect)
	{
		Pattern p = null;
		if (isRedirect)
		{
			p = Pattern.compile("(SAMLResponse=)(.*?)(\n)", Pattern.DOTALL);
		}
		else
		{
			p = Pattern.compile("(<input type=\"hidden\" name=\"SAMLResponse\" value=\")(.*?)(\" />)", Pattern.DOTALL);
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
	 * WARNING
	 * REMOVE THIS IN PRODUCTION; IT IS JUST A WORKAROUND FOR THE CURRENT EIDAS BUGS
	 * 
	 * @param response
	 * @return
	 */
	private LogMessage WORKAROUND(String response, String testCaseName)
	{
		// prepare the content json
		Pattern p = Pattern.compile("(<input type=\"hidden\" name=\"SAMLResponse\" value=\")(.*?)(\" />)", Pattern.DOTALL);
		Matcher m = p.matcher(response);
		String samlResponse = null;
		while (m.find()) // find last occurrence of given pattern
		{
			samlResponse = m.group(2);
		}
		EidasWorkaroundHolder holder = new EidasWorkaroundHolder(samlResponse);
		Gson gson = new Gson();
		String jsonResponse = gson.toJson(holder);

		// call the workaround server
		URL url;
		try
		{

			url = new URL("http://localhost:4567/workaround");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			// httpCon.setFixedLengthStreamingMode(jsonResponse.length());
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Accept", "application/json");
			OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
			out.write(jsonResponse);
			out.flush();
			String json = "";
			if (httpCon.getResponseCode() == 200)
			{
				try (InputStreamReader instream = new InputStreamReader(httpCon.getInputStream()); BufferedReader buffer = new BufferedReader(instream))
				{
					String line;
					while ((line = buffer.readLine()) != null)
					{
						json += line;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			out.close();
			httpCon.disconnect();

			LOGGER.info("Received data from workaround server: " + json);

			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> receivedAttributes = gson.fromJson(json, type);

			LogMessage message = logMessageDAO.createNew();
			message.setTestStepName(testCaseName);
			String resultString = "Validating the eIDAS response" + System.getProperty("line.separator");

			// check if the server returned a SAML error (see workaround)
			if (receivedAttributes.size() == 3 && receivedAttributes.containsKey(GeneralConstants.EIDAS_ERROR_SAMLSTATUS))
			{
				// check, if there are any expected eIDAS errors
				KnownValues expectedErrorValues = knownValues.getStartingWith(GeneralConstants.EIDAS_ERROR_PREFIX);
				if (expectedErrorValues.isEmpty())
				{
					message.setSuccess(false);
					resultString += "The SAML assertion indicated an unexpected error (code " + receivedAttributes.get(GeneralConstants.EIDAS_ERROR_SAMLSTATUS) + ").";
				}
				else
				{
					// check, if we received the expected eIDAS errors
					message.setSuccess(true);
					for (KnownValue toCheck : expectedErrorValues)
					{
						String expected = toCheck.getValue();
						String received = receivedAttributes.get(toCheck.getName());
						if (expected.equals(received))
						{
							resultString += "The eIDAS error attribute " + toCheck.getName() + " did contain the expected value " + expected + System.getProperty("line.separator");
						}
						else
						{
							resultString += "The eIDAS error attribute " + toCheck.getName() + " did contain the wrong value " + received + ". The expected value was " + expected
									+ System.getProperty("line.separator");
							message.setSuccess(false);
						}
					}
				}
			}
			else
			{
				// fetch the data we will be comparing against
				KnownValues requestedValues = knownValues.getStartingWith(GeneralConstants.EIDAS_REQUESTED_PREFIX);
				Map<EidasPersonAttributes, Boolean> requested = getRequestAttributes(requestedValues);
				Set<String> restrictedEidValues = getRestrictedByUser();
				final Set<String> uppercaseRestrictedEidValues = restrictedEidValues.stream().map(s -> s.toUpperCase()).collect(Collectors.toSet());
				KnownValues expectedAttributeValues = knownValues.getStartingWith(GeneralConstants.PERSONAL_PREFIX);
				Map<EidasNaturalPersonAttributes, String> expectedValues = new HashMap<>();
				for (KnownValue knownValue : expectedAttributeValues)
				{
					String plainEidasName = knownValue.getName().substring(GeneralConstants.PERSONAL_PREFIX.length(), knownValue.getName().length());
					expectedValues.put(getNaturalAttributeFromName(plainEidasName), knownValue.getValue());
				}

				// validate the results
				message.setSuccess(true);
				for (EidasPersonAttributes requestedKey : requested.keySet())
				{
					boolean hadAttribute = false;
					for (String receivedKey : receivedAttributes.keySet())
					{
						Set<String> receivedEidAttributes = EidEidasMapping.getCorrespondingEidAttributes(receivedKey);
						final Set<String> uppercaseReceivedEidAttributes = receivedEidAttributes.stream().map(s -> s.toUpperCase()).collect(Collectors.toSet());
						if (Collections.disjoint(uppercaseReceivedEidAttributes, uppercaseRestrictedEidValues))
						{
							if (requestedKey.getFriendlyName().equals(receivedKey))
							{
								hadAttribute = true;
								resultString += "Found attribute " + requestedKey.getFriendlyName() + " in the response." + System.getProperty("line.separator");

								if (expectedValues.containsKey(requestedKey))
								{
									String attributeValue = receivedAttributes.get(receivedKey);
									if (attributeValue.equals(expectedValues.get(requestedKey)))
									{
										resultString += "The attribute " + requestedKey.getFriendlyName() + " did contain the expected value " + expectedValues.get(requestedKey)
												+ System.getProperty("line.separator");
									}
									else
									{
										resultString += "The attribute " + requestedKey.getFriendlyName() + " did contain the wrong value " + attributeValue + System.getProperty("line.separator")
												+ ". The expected value was " + expectedValues.get(requestedKey);
										message.setSuccess(false);
									}

								}
								else
								{
									resultString += "The testcase did not contain comparison data for the attribute " + requestedKey.getFriendlyName() + System.getProperty("line.separator");
									LOGGER.warn("The testcase did not contain comparison data for the attribute " + requestedKey.getFriendlyName());
								}

								break;
							}
						}
						else
						{
							resultString += "The attribute " + requestedKey.getFriendlyName() + " was present although it (or some part of it) has been deselected by the user."
									+ System.getProperty("line.separator");
							message.setSuccess(false);
						}
					}
					if (!hadAttribute && requested.get(requestedKey))
					{
						Set<String> requestedEidAttributes = EidEidasMapping.getCorrespondingEidAttributes(requestedKey);
						final Set<String> uppercaseRequestedEidAttributes = requestedEidAttributes.stream().map(s -> s.toUpperCase()).collect(Collectors.toSet());
						if (Collections.disjoint(uppercaseRequestedEidAttributes, uppercaseRestrictedEidValues))
						{
							resultString += "The requested attribute " + requestedKey.getFriendlyName() + " was missing from the assertion." + System.getProperty("line.separator");
							message.setSuccess(false);
						}
						else
						{
							resultString += "The requested attribute " + requestedKey.getFriendlyName() + " was missing from the assertion because it was deselected by the user."
									+ System.getProperty("line.separator");
						}
					}
				}
			}

			message.setMessage(resultString);
			return message;
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private class EidasWorkaroundHolder implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4629148177865779359L;
		@SerializedName("SAMLResponse")
		private final String samlResponse;

		public EidasWorkaroundHolder(String samlResponse)
		{
			this.samlResponse = samlResponse;
		}

		/**
		 * @return the samlResponse
		 */
		public String getSamlResponse()
		{
			return samlResponse;
		}
	}

}
