package com.secunet.workaround;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.io.UnmarshallingException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import de.governikus.eidassaml.starterkit.EidasAttribute;
import de.governikus.eidassaml.starterkit.EidasNaturalPersonAttributes;
import de.governikus.eidassaml.starterkit.EidasResponse;
import de.governikus.eidassaml.starterkit.EidasSaml;
import de.governikus.eidassaml.starterkit.ErrorCodeException;
import de.governikus.eidassaml.starterkit.Utils;
import de.governikus.eidassaml.starterkit.Utils.X509KeyPair;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.BirthNameAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.CurrentAddressAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.DateOfBirthAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.FamilyNameAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.GenderAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.GivenNameAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.PersonIdentifyerAttribute;
import de.governikus.eidassaml.starterkit.natural_persons_attribute.PlaceOfBirthAttribute;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import spark.Request;
import spark.Response;

public class Workaround {

	public static void main(String[] args) {
		get("/hello", (req, res) -> "Hello World");

		post("/workaround", "application/json", Workaround::retreiveEidasResponse);

	}

	private static String retreiveEidasResponse(final Request request, final Response response) {
		if (request.body() == null || request.body().length() == 0) {
			response.status(403);
			System.out.println("Did not receive a SAML assertion");
			return "Did not receive a SAML assertion";
		}
		System.out.println("Received SAML assertion:" + System.getProperty("line.separator") + request.body());
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> receivedMap = gson.fromJson(request.body(), type);
		if (!receivedMap.containsKey("SAMLResponse")) {
			response.status(403);
			System.out.println("Could not parse the request in order to obtain a SAML assertion");
			return "Could not parse the reuqest in order to obtain a SAML assertion";
		}

		byte[] decoded;
		try {
			decoded = Base64.decode(receivedMap.get("SAMLResponse"));
			ByteArrayInputStream is = new ByteArrayInputStream(decoded);

			System.out.println("Decoded as string:" + new String(decoded));

			// InputStream is = IOUtils.toInputStream(receivedMessage,
			// StandardCharsets.UTF_8);

			File signatureFile = new File(System.getProperty("user.dir") + "/src/crypto/A.crt");
			X509Certificate signatureCert = Utils.readX509Certificate(new FileInputStream(signatureFile));
			System.out.println("Loaded signature cert from: " + signatureFile);

			File encryptionFile = new File(System.getProperty("user.dir") + "/src/crypto/F_ENC.p12");
			X509KeyPair encryptionPair = Utils.ReadPKCS12(new FileInputStream(encryptionFile), "123456".toCharArray(),
					"ENC");
			System.out.println("Loaded encryption key from: " + encryptionFile);

			EidasResponse eidasResponse = EidasSaml.ParseResponse(is, new Utils.X509KeyPair[] { encryptionPair },
					new X509Certificate[] { signatureCert });
			System.out.println("Parsed response");
			
			Map<String, String> attributes = createAttributeContainers(eidasResponse.getAttributes());

			Type listOfTestObject = new TypeToken<Map<String,String>>() {
			}.getType();
			String jsonData = gson.toJson(attributes, listOfTestObject);
			System.out.println(jsonData);
			return jsonData;
		} catch (Base64DecodingException | CertificateException | NoSuchProviderException | IOException
				| UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | InitializationException
				| XMLParserException | UnmarshallingException| ComponentInitializationException e) {
			e.printStackTrace();
			response.status(403);
			System.out.println("Parsing the SAML assertion failed due to: " + e.getMessage());
			return "Parsing the SAML assertion failed due to: " + e.getMessage();
		} catch(ErrorCodeException ec) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("EIDAS_ERROR_SAMLSTATUS", ec.getCode().getSamlStatus());
			errorMap.put("EIDAS_ERROR_CODE_NAME", ec.getCode().name());
			errorMap.put("EIDAS_ERROR_MESSAGE", ec.getMessage());
			Type listOfTestObject = new TypeToken<Map<String,String>>() {
			}.getType();
			String jsonData = gson.toJson(errorMap, listOfTestObject);
			System.out.println(jsonData);
			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			response.status(403);
			System.out.println("Unspecified exception: " + e.getMessage());
			return "Unspecified exception: " + e.getMessage();
		}
	}

	private static Map<String, String> createAttributeContainers(List<EidasAttribute> attributes)
	{
		Map<String, String> attributeMap = new HashMap<>();
		for(EidasAttribute attribute: attributes)
		{
			if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.BirthName) {
				BirthNameAttribute realAttribute = (BirthNameAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.BirthName.getFriendlyName(), realAttribute.getValue());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.CurrentAddress) {
				CurrentAddressAttribute realAttribute = (CurrentAddressAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.CurrentAddress.getFriendlyName(), realAttribute.generate());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.DateOfBirth) {
				DateOfBirthAttribute realAttribute = (DateOfBirthAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.DateOfBirth.getFriendlyName(), realAttribute.getDate());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.FamilyName) {
				FamilyNameAttribute realAttribute = (FamilyNameAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.FamilyName.getFriendlyName(), realAttribute.getValue());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.FirstName) {
				GivenNameAttribute realAttribute = (GivenNameAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.FirstName.getFriendlyName(), realAttribute.getValue());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.Gender) {
				GenderAttribute realAttribute = (GenderAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.Gender.getFriendlyName(), realAttribute.generate());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.PersonIdentifier) {
				PersonIdentifyerAttribute realAttribute = (PersonIdentifyerAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.PersonIdentifier.getFriendlyName(), realAttribute.getId());
			} else if(attribute.getNaturalPersonAttributeType() == EidasNaturalPersonAttributes.PlaceOfBirth) {
				PlaceOfBirthAttribute realAttribute = (PlaceOfBirthAttribute) attribute;
				attributeMap.put(EidasNaturalPersonAttributes.PlaceOfBirth.getFriendlyName(), realAttribute.generate());
			}
		}
		return attributeMap;
	}

}
