package com.secunet.eidserver.testbed.common.classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.enumerations.EidCardFiles;
import com.secunet.eidserver.testbed.common.enumerations.EidRequestApdu;
import com.secunet.eidserver.testbed.common.enumerations.EidResponseApdu;
import com.secunet.eidserver.testbed.common.types.testcase.EidCard;

public class EidCards
{
	private static final Logger logger = LogManager.getRootLogger();

	private static final Map<EidCard, Map<EidRequestApdu, EidResponseApdu>> apdus;
	private static final Map<EidCard, String> caPrivate;
	private static final Map<EidCard, String> cardSecurity;
	private static final Map<EidCard, String> cardAccess;
	private static final Map<EidCard, LocalDate> birthday;
	private static final Map<EidCard, String> communityId;

	static
	{
		apdus = new HashMap<>();
		caPrivate = new HashMap<>();
		cardSecurity = new HashMap<>();
		cardAccess = new HashMap<>();
		birthday = new HashMap<>();
		communityId = new HashMap<>();
		for (EidCard e : EidCard.values())
		{
			apdus.put(e, generateApdus(e));
			caPrivate.put(e, generateCaPrivate(e));
			cardSecurity.put(e, generateCardSecurityFile(e));
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
	 * @return
	 */
	public static Map<EidRequestApdu, EidResponseApdu> apdus(EidCard card)
	{
		return Collections.unmodifiableMap(apdus.get(card));
	}

	/**
	 * Returns the Chip Authentication private key for the given {@link EidCard}.
	 * 
	 * @param card
	 * @return
	 */
	public static String caPrivate(EidCard card)
	{
		return caPrivate.get(card);
	}

	/**
	 * Returns the card security file for the given {@link EidCard}.
	 * 
	 * @param card
	 * @return
	 */
	public static String cardSecurity(EidCard card)
	{
		return cardSecurity.get(card);
	}

	/**
	 * Returns the card access file for the given {@link EidCard}.
	 * 
	 * @param card
	 * @return
	 */
	public static String cardAccess(EidCard card)
	{
		return cardAccess.get(card);
	}

	/**
	 * Returns the birthday for the given {@link EidCard}.
	 * 
	 * @param card
	 * @return
	 */
	public static LocalDate birthday(EidCard card)
	{
		return birthday.get(card);
	}

	/**
	 * Returns the community ID for the given {@link EidCard}.
	 * 
	 * @param card
	 * @return
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
			bday = LocalDate.parse(hexAge, DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		catch (DateTimeParseException | DecoderException e)
		{
			logger.log(Level.WARN, "Could not parse birthdate for card " + card.toString() + " from its definition. The date will be set to 1970-01-01. Message: " + e.getLocalizedMessage());
			bday = LocalDate.of(1970, 01, 01);
		}
		return bday;
	}

	private static String generateCommunityId(EidCard card)
	{
		String hexPlace = apdus.get(card).get(EidRequestApdu.READ_DG18).getResponseApdu();
		hexPlace = hexPlace.substring(0, hexPlace.lastIndexOf(EidResponseApdu.OK.getResponseApdu()));
		hexPlace = hexPlace.substring("7C228120".length());
		return hexPlace;
	}

	private static String generateCaPrivate(EidCard card)
	{
		switch (card)
		{
			default:
				return EidCardFiles.CA_PRIVATE.getContent();
		}
	}

	private static String generateCardSecurityFile(EidCard card)
	{
		switch (card)
		{
			case EIDCARD_5:
				return EidCardFiles.EFCARDSECURITY_DIFFERENT_RI_KEY_ID_CARD5.getContent();
			case EIDCARD_6:
				return EidCardFiles.EFCARDSECURITY_DIFFERENT_RI_KEY_ID_CARD6.getContent();
			case EIDCARD_9:
				return EidCardFiles.EFCARDSECURITY_BAD_SIGNATURE.getContent();
			case EIDCARD_10:
				return EidCardFiles.EFCARDSECURITY_DS_B.getContent();
			case EIDCARD_11:
				return EidCardFiles.EFCARDSECURITY_UNKNOWN_DS.getContent();
			case EIDCARD_13:
				return EidCardFiles.EFCARDSECURITY_CA_KEY_NOT_ON_CURVE.getContent();
			default:
				// return EidCardFiles.EFCARDSECURITY.getContent();
				return EidCardFiles.EFCARDSECURITY_DS_A.getContent();
		}
	}

	private static String generateCardAccessFile(EidCard card)
	{
		switch (card)
		{
			case EIDCARD_12_1:
				return EidCardFiles.EFCARDACCESS_MANIPULATED_1.getContent();
			case EIDCARD_12_2:
				return EidCardFiles.EFCARDACCESS_MANIPULATED_2.getContent();
			case EIDCARD_12_3:
				return EidCardFiles.EFCARDACCESS_MANIPULATED_3.getContent();
			default:
				return EidCardFiles.EFCARDACCESS.getContent();
		}
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
				responses.put(EidRequestApdu.READ_DG8, EidResponseApdu.UNKNOWN_YEAR_OF_BIRTH);
				responses.put(EidRequestApdu.READ_DG13, EidResponseApdu.NOT_ON_CHIP);
				responses.put(EidRequestApdu.READ_DG17, EidResponseApdu.NO_PLACE_INFO);
				responses.put(EidRequestApdu.READ_DG18, EidResponseApdu.EMPTY);
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
			default:
				break;
		}
		return responses;
	}

	/**
	 * Get the default map with correct answers for every field
	 * 
	 * @return
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
