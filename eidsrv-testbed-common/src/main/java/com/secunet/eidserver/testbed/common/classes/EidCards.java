package com.secunet.eidserver.testbed.common.classes;

import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.eidserver.testbed.common.enumerations.EidRequestApdu;
import com.secunet.eidserver.testbed.common.enumerations.EidResponseApdu;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;
import de.bsi.testbedutils.bouncycertgen.asn1.BSIObjectIdentifiers;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EidCards
{
	private static final Logger logger = LogManager.getRootLogger();

	private static final Map<EidCard, Map<EidRequestApdu, EidResponseApdu>> apdus = new HashMap<>();
	private static final Map<EidCard, String> caPrivate = new HashMap<>();
	private static final Map<EidCard, String> cardSecurity = new HashMap<>();
	private static final Map<EidCard, String> cardAccess = new HashMap<>();
	private static final Map<EidCard, LocalDate> birthday = new HashMap<>();
	private static final Map<EidCard, String> communityId = new HashMap<>();

	public static void initCards(TestCandidate candidate)
	{
		apdus.clear();
		caPrivate.clear();
		cardSecurity.clear();
		cardAccess.clear();
		birthday.clear();
		communityId.clear();

		for (EidCard e : EidCard.values())
		{
			apdus.put(e, generateApdus(e));
			caPrivate.put(e, generateCaPrivate(e));
			if (candidate != null)
				cardSecurity.put(e, generateCardSecurityFile(e, candidate));
			cardAccess.put(e, generateCardAccessFile(e));
			birthday.put(e, getBirthday(e));
			communityId.put(e, generateCommunityId(e));
		}
	}

	/**
	 * Returns an unmodifiable mapping of request and response APDUs for the given {@link EidCard}.
	 * 
	 * @param card
	 *            {@link EidCard} The card for which the mapping shall be returned
	 * @return Map of request and response APDUs
	 */
	public static Map<EidRequestApdu, EidResponseApdu> apdus(EidCard card)
	{
		return Collections.unmodifiableMap(apdus.get(card));
	}

	/**
	 * Returns the Chip Authentication private key for the given {@link EidCard}.
	 * 
	 * @param card The card for which the Chip Authentication private key shall be returned
	 * @return The Chip Authentication private key
	 */
	public static String caPrivate(EidCard card)
	{
		return caPrivate.get(card);
	}

	/**
	 * Returns the card security file for the given {@link EidCard}.
	 * 
	 * @param card The card for which the card security file shall be returned
	 * @return The card security file
	 */
	public static String cardSecurity(EidCard card)
	{
		return cardSecurity.get(card);
	}

	/**
	 * Returns the card access file for the given {@link EidCard}.
	 * 
	 * @param card The card for which the card access file shall be returned
	 * @return The card access file
	 */
	public static String cardAccess(EidCard card)
	{
		return cardAccess.get(card);
	}

	/**
	 * Returns the birthday for the given {@link EidCard}.
	 * 
	 * @param card The card for which the birthday shall be returned
	 * @return The birthday
	 */
	public static LocalDate birthday(EidCard card)
	{
		return birthday.get(card);
	}

	/**
	 * Returns the community ID for the given {@link EidCard}.
	 * 
	 * @param card The card for which the community ID shall be returned
	 * @return The community ID
	 */
	public static String communityId(EidCard card)
	{
		return communityId.get(card);
	}


	private static LocalDate getBirthday(EidCard card)
	{
		String hexAge = apdus.get(card).get(EidRequestApdu.READ_DG8).getResponseApdu();
		hexAge = hexAge.substring(0, hexAge.lastIndexOf(EidResponseApdu.OK.getResponseApdu()));
		hexAge = hexAge.substring("680A1208".length());
		LocalDate bday;
		try
		{
			hexAge = new String(Hex.decodeHex(hexAge.toCharArray()));
			bday = LocalDate.parse(hexAge.substring(0,4) + "1231", DateTimeFormatter.ofPattern("yyyyMMdd"));
			try
			{
				bday = LocalDate.parse(hexAge, DateTimeFormatter.ofPattern("yyyyMMdd"));
			}
			catch (DateTimeParseException e)
			{
				logger.log(Level.DEBUG, "Could not parse birthdate for card " + card.toString() + " from its definition. However, the year was readable. The date will be set to " + bday.getYear() + "-12-31. Message: " + e.getLocalizedMessage());
			}
		}
		catch (DateTimeParseException | DecoderException e)
		{
			logger.log(Level.WARN, "Could not parse birthdate for card " + card.toString() + " from its definition. The date will be set to 1970-01-01. Message: " + e.getLocalizedMessage());
			bday = LocalDate.of(1970, 1, 1);
		}
		return bday;
	}

	private static String generateCommunityId(EidCard card)
	{
		String hexPlace = apdus.get(card).get(EidRequestApdu.READ_DG18).getResponseApdu();
		hexPlace = hexPlace.substring(0, hexPlace.lastIndexOf(EidResponseApdu.OK.getResponseApdu()));
		hexPlace = hexPlace.substring("72090407".length());
		return hexPlace;
	}

	private static String generateCaPrivate(EidCard card)
	{
        return switch (card) {
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.CA_PRIVATE_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            default -> EidCardFiles.CA_PRIVATE.getContent();
        };
	}

	//TODO: Document
	private static String generateCardSecurityFile(EidCard card, TestCandidate candidate) {
		String content, certName = "CERT_ECARD_DS_1_A", privateKey = "CERT_ECARD_DS_1_A_privKey";
		boolean destroySignature = false;
		switch (card) {
			case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
				break;
			case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256:
				content = EidCardFiles.EFCARDSECURITY_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
				break;
			case EIDCARD_5:
				content = EidCardFiles.EFCARDSECURITY_DIFFERENT_RI_KEY_ID_CARD5.getContent();
				break;
			case EIDCARD_6:
				content = EidCardFiles.EFCARDSECURITY_DIFFERENT_RI_KEY_ID_CARD6.getContent();
				break;
			case EIDCARD_9:
				content = EidCardFiles.EFCARDSECURITY_DS_A.getContent();
				destroySignature = true;
				break;
			case EIDCARD_10:
				content = EidCardFiles.EFCARDSECURITY_DS_B.getContent();
				certName = "CERT_ECARD_DS_1_B";
				privateKey = "CERT_ECARD_DS_1_B_privKey";
				break;
			case EIDCARD_11:
				return EidCardFiles.EFCARDSECURITY_UNKNOWN_DS.getContent();
			case EIDCARD_13:
				content = EidCardFiles.EFCARDSECURITY_CA_KEY_NOT_ON_CURVE.getContent();
				break;
			case EIDCARD_15:
				content = EidCardFiles.EFCARDSECURITY_DS_A_CA3.getContent();
				break;
			default:
				content = EidCardFiles.EFCARDSECURITY_DS_A.getContent();
		}
		return signCMSSignedData(content, certName, privateKey, destroySignature, candidate);
	}

	private static String generateCardAccessFile(EidCard card)
	{
        return switch (card) {
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP256R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP384R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_128.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_192.getContent();
            case EIDCARD_4_T_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256 ->
                    EidCardFiles.EFCARDACCESS_BRAINPOOLP512R1_CA_ECDH_AES_CBC_CMAC_256.getContent();
            case EIDCARD_12_1 -> EidCardFiles.EFCARDACCESS_MANIPULATED_1.getContent();
            case EIDCARD_12_2 -> EidCardFiles.EFCARDACCESS_MANIPULATED_2.getContent();
            case EIDCARD_12_3 -> EidCardFiles.EFCARDACCESS_MANIPULATED_3.getContent();
            case EIDCARD_15 -> EidCardFiles.EFCARDACCESS_CA3.getContent();
            default -> EidCardFiles.EFCARDACCESS.getContent();
        };
	}

	/**
	 * Signs data with a supplied certificate and private key.
	 * Handles specific exceptions by logging them
	 * and returns null to indicate failure.
	 *
	 * @param innerData Hexadecimal string representing the unsigned CMS data.
	 * @param certName The name of the certificate file to be used for signing.
	 * @param keyName The name of the private key file to be used for signing.
	 * @param destroySignature If true, the last 4 bytes of the signature will be set to 0.
	 * @param candidate The test candidate to load the certificate and private key from.
	 * @return A hexadecimal string representing the signed CMS data, or null if an error occurs.
	 */
	private static String signCMSSignedData(String innerData, String certName, String keyName, boolean destroySignature, TestCandidate candidate) {
		byte[] cardSecurityBytes = DatatypeConverter.parseHexBinary(innerData);

		try {

			CMSTypedData originalContent = new CMSProcessableByteArray(BSIObjectIdentifiers.SecurityObject, cardSecurityBytes);

			X509Certificate signCertificate = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SOAP_PROT_ID, CryptoHelper.Algorithm.CSCA_ALG_ID, certName, ".DER", candidate);
			PrivateKey privateKey = CryptoHelper.loadKey(CryptoHelper.Protocol.SOAP_PROT_ID, CryptoHelper.Algorithm.CSCA_ALG_ID, keyName, candidate);

			CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
			signedDataGenerator.setDefiniteLengthEncoding(true);
			JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA384withECDSA");
			signerBuilder.setProvider(new BouncyCastleProvider());
			SignerInfoGenerator sigGen = new JcaSignerInfoGeneratorBuilder(
					new JcaDigestCalculatorProviderBuilder().setProvider(new BouncyCastleProvider()).build())
					.build(signerBuilder.build(privateKey), signCertificate);

			signedDataGenerator.addCertificates(new JcaCertStore(Collections.singletonList(signCertificate)));

			final CMSAttributeTableGenerator sAttrGen = sigGen.getSignedAttributeTableGenerator();
			signedDataGenerator.addSignerInfoGenerator(new SignerInfoGenerator(sigGen
					, parameters -> sAttrGen.getAttributes(parameters).remove(CMSAttributes.cmsAlgorithmProtect).remove(CMSAttributes.signingTime), sigGen.getUnsignedAttributeTableGenerator())
			);

			CMSSignedData newSignedData = signedDataGenerator.generate(originalContent, true);

			byte[] encodedSignedData = newSignedData.getEncoded();
			if(destroySignature) {
				System.arraycopy(new byte[4], 0, encodedSignedData, encodedSignedData.length - 4, 4);
			}

			return Hex.encodeHexString(encodedSignedData);
		} catch (CMSException e) {
			logger.error("CMSException while signing CMS Data: " + e.getMessage(), e);
		} catch (OperatorCreationException e) {
			logger.error("OperatorCreationException while creating content signer: " + e.getMessage(), e);
		} catch (CertificateEncodingException e) {
			logger.error("CertificateEncodingException for input certificate: " + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("IOException encountered: " + e.getMessage(), e);
		}
		return null;
	}

	private static Map<EidRequestApdu, EidResponseApdu> generateApdus(EidCard card)
	{
		Map<EidRequestApdu, EidResponseApdu> responses = getAllOk();
		switch (card)
		{
			case EIDCARD_2:
				responses.put(EidRequestApdu.READ_DG6, EidResponseApdu.NOT_ON_CHIP);
				responses.put(EidRequestApdu.READ_DG10, EidResponseApdu.NOT_ON_CHIP);
				responses.put(EidRequestApdu.READ_DG13, EidResponseApdu.NOT_ON_CHIP);
				responses.put(EidRequestApdu.READ_DG19, EidResponseApdu.NOT_ON_CHIP);
				responses.put(EidRequestApdu.READ_DG20, EidResponseApdu.NOT_ON_CHIP);
				break;
			case EIDCARD_3:
				responses.put(EidRequestApdu.READ_DG8, EidResponseApdu.UNKNOWN_DAY_MONTH_OF_BIRTH);
				responses.put(EidRequestApdu.READ_DG13, EidResponseApdu.EMPTY_DG13);
				responses.put(EidRequestApdu.READ_DG17, EidResponseApdu.NO_PLACE_INFO);
				responses.put(EidRequestApdu.READ_DG18, EidResponseApdu.EMPTY_DG18);
				break;
			case EIDCARD_5:
				// This card has different RI key IDs
				responses.put(EidRequestApdu.SELECT_RI_KEYID_1, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_2, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_3, EidResponseApdu.OK);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_4, EidResponseApdu.OK);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_5, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_6, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				break;
			case EIDCARD_6:
				// This card has different RI key IDs
				responses.put(EidRequestApdu.SELECT_RI_KEYID_1, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_2, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_3, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_4, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_5, EidResponseApdu.OK);
				responses.put(EidRequestApdu.SELECT_RI_KEYID_6, EidResponseApdu.OK);
				break;
			case EIDCARD_7:
				responses.put(EidRequestApdu.DOCUMENT_VALIDITY, EidResponseApdu.VERIFICATION_FAILED);
				break;
			case EIDCARD_8:
				responses.put(EidRequestApdu.RESTRICTED_IDENTIFICATION, EidResponseApdu.RESTRICTED_ID_REVOKED);
				break;
			case EIDCARD_9:
				// This card contains a manipulated EF.CardSecurity file
				break;
			case EIDCARD_10:
				// This card contains a manipulated EF.CardSecurity file
				break;
			case EIDCARD_11:
				// This card contains a manipulated EF.CardSecurity file
				break;
			case EIDCARD_12_1:
			case EIDCARD_12_2:
			case EIDCARD_12_3:
				// This card contains a manipulated EF.CardAccess file
				break;
			case EIDCARD_13:
				// This card contains a manipulated EF.CardSecurity file
				break;
			case EIDCARD_14:
				responses.put(EidRequestApdu.READ_DG17, EidResponseApdu.AT_DG17);
			default:
				break;
		}
		return responses;
	}

	/**
	 * Get the default map with correct answers for every field
	 * 
	 * @return Map with correct answers for every field
	 */
	private static Map<EidRequestApdu, EidResponseApdu> getAllOk()
	{
		Map<EidRequestApdu, EidResponseApdu> responses = new HashMap<>();
		responses.put(EidRequestApdu.READ_DG1, EidResponseApdu.DG1);
		responses.put(EidRequestApdu.READ_DG2, EidResponseApdu.DG2);
		responses.put(EidRequestApdu.READ_DG3, EidResponseApdu.DG3);
		responses.put(EidRequestApdu.READ_DG4, EidResponseApdu.DG4);
		responses.put(EidRequestApdu.READ_DG5, EidResponseApdu.DG5);
		responses.put(EidRequestApdu.READ_DG6, EidResponseApdu.DG6);
		responses.put(EidRequestApdu.READ_DG7, EidResponseApdu.DG7);
		responses.put(EidRequestApdu.READ_DG8, EidResponseApdu.DG8);
		responses.put(EidRequestApdu.READ_DG9, EidResponseApdu.DG9);
		responses.put(EidRequestApdu.READ_DG10, EidResponseApdu.DG10);
		responses.put(EidRequestApdu.READ_DG13, EidResponseApdu.DG13);
		responses.put(EidRequestApdu.READ_DG17, EidResponseApdu.DG17);
		responses.put(EidRequestApdu.READ_DG18, EidResponseApdu.DG18);
		responses.put(EidRequestApdu.READ_DG19, EidResponseApdu.DG19);
		responses.put(EidRequestApdu.READ_DG20, EidResponseApdu.DG20);
		// special functions. note that age and place verification actually will be computed from the comparison data, so it wouldnt be necessary to leave it enabled here
		responses.put(EidRequestApdu.AGE_VERIFICATION, EidResponseApdu.OK);
		responses.put(EidRequestApdu.PLACE_VERIFICATION, EidResponseApdu.OK);
		responses.put(EidRequestApdu.RESTRICTED_IDENTIFICATION, EidResponseApdu.RESTRICTED_ID);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_1, EidResponseApdu.OK);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_2, EidResponseApdu.OK);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_3, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_4, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_5, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
		responses.put(EidRequestApdu.SELECT_RI_KEYID_6, EidResponseApdu.REFERENCED_DATA_NOT_FOUND);
		// other
		responses.put(EidRequestApdu.SET_AT, EidResponseApdu.STATUS_OK);
		responses.put(EidRequestApdu.FILE_SELECTION, EidResponseApdu.STATUS_OK);
		responses.put(EidRequestApdu.VERIFY_AUX_DATA, EidResponseApdu.STATUS_OK);
		responses.put(EidRequestApdu.DOCUMENT_VALIDITY, EidResponseApdu.STATUS_OK);

		return responses;
	}

}
