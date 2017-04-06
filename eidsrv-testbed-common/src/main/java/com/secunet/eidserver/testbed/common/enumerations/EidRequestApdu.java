package com.secunet.eidserver.testbed.common.enumerations;

public enum EidRequestApdu
{
	READ_DG1(RequestAttribute.DocumentType.toString(), "B08100"), //
	READ_DG2(RequestAttribute.IssuingState.toString(), "B08200"), //
	READ_DG3(RequestAttribute.DateOfExpiry.toString(), "B08300"), //
	READ_DG4(RequestAttribute.GivenNames.toString(), "B08400"), //
	READ_DG5(RequestAttribute.FamilyNames.toString(), "B08500"), //
	READ_DG6(RequestAttribute.ArtisticName.toString(), "B08600"), //
	READ_DG7(RequestAttribute.AcademicTitle.toString(), "B08700"), //
	READ_DG8(RequestAttribute.DateOfBirth.toString(), "B08800"), //
	READ_DG9(RequestAttribute.PlaceOfBirth.toString(), "B08900"), //
	READ_DG10(RequestAttribute.Nationality.toString(), "B08A00"), //
	READ_DG13(RequestAttribute.BirthName.toString(), "B08D00"), //
	READ_DG17(RequestAttribute.PlaceOfResidence.toString(), "B09100"), //
	READ_DG18(RequestAttribute.PlaceID.toString(), "B09200"), //
	READ_DG19(RequestAttribute.ResidencePermitI.toString(), "B09300"), //
	READ_DG20(RequestAttribute.ResidencePermitII.toString(), "B09400"), //
	FILE_SELECTION("", "E80704007F00070302"), //
	SET_AT("", "800A04007F00070202050203840102"), //
	VERIFY_AUX_DATA("", "800A04007F00070202050203840101"), //
	DOCUMENT_VALIDITY("", "060904007F000703010402"), //
	PLACE_VERIFICATION(SpecialFunction.PlaceVerification.toString(), "8C2080000B060904007F000703010403"), //
	AGE_VERIFICATION(SpecialFunction.AgeVerification.toString(), "8C2080000B060904007F000703010401"), //
	SELECT_RI_KEYID_1(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840101"), //
	SELECT_RI_KEYID_2(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840102"), //
	SELECT_RI_KEYID_3(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840103"), //
	SELECT_RI_KEYID_4(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840104"), //
	SELECT_RI_KEYID_5(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840105"), //
	SELECT_RI_KEYID_6(SpecialFunction.RestrictedID.toString(), "0C2241A40F800A04007F00070202050203840106"), //
//	RESTRICTED_IDENTIFICATION(SpecialFunction.RestrictedID.toString(), "0C860000000125");
	RESTRICTED_IDENTIFICATION(SpecialFunction.RestrictedID.toString(), "0C8600000001");

	private final String operationName;
	private final String requestApdu;

	private EidRequestApdu(String operationName, String requestApdu)
	{
		this.operationName = operationName;
		this.requestApdu = requestApdu;
	}

	public String getOperationName()
	{
		return operationName;
	}

	public String getRequestApdu()
	{
		return requestApdu;
	}

	public static EidRequestApdu getFromRequest(String request)
	{
		for (EidRequestApdu x : EidRequestApdu.values())
		{
			if (request != null && request.contains(x.getRequestApdu()))
			{
				return x;
			}
		}
		return null;
	}

}
