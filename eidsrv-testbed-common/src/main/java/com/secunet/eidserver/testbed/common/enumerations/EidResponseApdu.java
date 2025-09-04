package com.secunet.eidserver.testbed.common.enumerations;

import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public enum EidResponseApdu
{
	// Data groups
	DG1("6104130249449000"), // DocumentType (ID)
	DG2("62031301449000"), // IssuingState (D)
	DG3(generateExpiryDate()), // DateOfExpiry (20201031)
	DG4("64070C054552494B419000"), // GivenNames (ERIKA)
	DG5("650C0C0A4D55535445524D414E4E9000"), // FamilyNames (MUSTERMANN)
	DG6("66190C174F5244454E53534348574553544552204A4F48414E4E419000"), // ArtisticName (ORDENSSCHWESTER JOHANNA)
	DG7("67050C0344522E9000"), // AcademicTitle (DR)
	DG8("680A120831393634303831329000"), // DateOfBirth (19640812)
	DG9("690AA1080C064245524C494E9000"), // PlaceOfBirth (BERLIN)
	DG10("6A031301449000"), // Nationality (D)
	DG13("6D080C064741424C45529000"), // BirthName (GABLER)
	DG17("712C302AAA110C0F484549444553545241535345203137AB070C05454D44454EAD03130144AE07130535313134379000"), // PlaceOfResidence (HEIDESTRASSE 17, 51147 EMDEN)
	DG18("72090407027642042001239000"), // CommunityID/MunicipalityID () Was 7C, corrected to 72
	DG19("73190C174F5244454E53534348574553544552204A4F48414E4E419000"), // ResidencePermitI (ORDENSSCHWESTER JOHANNA) Why it was 66? This should be 73
	DG20("74190C174F5244454E53534348574553544552204A4F48414E4E419000"), // ResidencePermitII (ORDENSSCHWESTER JOHANNA) Why it was 66? This should be 74

	// NOTONCHIP
	NOT_ON_CHIP("6A82"),

	// Referenced data not found
	REFERENCED_DATA_NOT_FOUND("6A88"),

	// noPlaceInfo - DG17
	NO_PLACE_INFO("7120A21E0C1C6B65696E6520576F686E756E6720696E20446575747363686C616E649000"),

	AT_DG17("7131302FAA170C15414E20444552204841555054535452415353452034AB060C045749454EAD0413024154AE061304313039309000"), // PlaceOfResidence (AN DER HAUPTSTRASSE 4, AT-1090 WIEN)

	// FAILURE
	VERIFICATION_FAILED("6300"),

	// OK
	OK("9000"),

	// EMPTY
	EMPTY_DG18("720204009000"),

	EMPTY_DG13("6D020C009000"),

	// OK
	STATUS_OK("99029000"),

	// RI
	RESTRICTED_ID("7C22812042424242424242424242424242424242424242424242424242424242424242429000"),
	
	// RI revoked
	RESTRICTED_ID_REVOKED("7C22812044444444444444444444444444444444444444444444444444444444444444449000"),

	// unknown day and month of birth for dg8 ("1960<SPACE><SPACE><SPACE><SPACE>") See TR-03127 3.4.3
	/*
	 * Nicht bei allen Ausweisinhabern ist das Geburtsdatum vollständig bekannt.
	 * In der Datengruppe DG8 wird das Geburtsdatum im Format JJJJMMTT gespeichert,
	 * soweit es bekannt ist, unbekannte Teile werden durch Leerzeichen aufgefüllt.
	 */
	UNKNOWN_DAY_MONTH_OF_BIRTH("680A120831393630202020209000");

	private final String responseApdu;

	EidResponseApdu(String responseApdu)
	{
		this.responseApdu = responseApdu;
	}

	/**
	 * Get the value of the response APDU
	 * 
	 * @return the value of the response APDU
	 */
	public String getResponseApdu()
	{
		return responseApdu;
	}

	private static String generateExpiryDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateHex = Hex.toHexString(sdf.format(cal.getTime()).getBytes(StandardCharsets.US_ASCII));
		return "630A1208" + dateHex + "9000";
	}

}
