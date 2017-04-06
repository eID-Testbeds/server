package com.secunet.tr03129.cvcert;

import java.security.InvalidParameterException;

import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorization;
import com.secunet.testbedutils.cvc.cvcertificate.CVAuthorizationAT;

public enum CVAuthorizationType
{

	AuthorizationA(CVTermIndicator.A), AuthorizationB(CVTermIndicator.B), AuthorizationC(CVTermIndicator.C), AuthorizationD(CVTermIndicator.D), AuthorizationA2(CVTermIndicator.A2), AuthorizationEDSA(
			CVTermIndicator.EDSA), AuthorizationEECDSA(CVTermIndicator.EECDSA), AuthorizationERSA(CVTermIndicator.ERSA);

	private CVAuthorization authorization;
	private CVTermIndicator indicator;

	private CVAuthorizationType(CVTermIndicator indicator)
	{
		this.indicator = indicator;

		CVAuthorization authorization = new CVAuthorizationAT();

		switch (indicator)
		{
			case B:
			{
				// write authorization
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG20, false);

				// read authorization
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG1, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG2, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG3, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG4, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG5, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG6, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG7, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG8, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG9, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG10, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG13, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG14, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG15, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG16, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG20, false);

				// common authorization
				authorization.setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_InstallCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_PINManagement, true);
				authorization.setAuth(CVAuthorizationAT.auth_CANAllowed, true);
				authorization.setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, true);
				authorization.setAuth(CVAuthorizationAT.auth_RestrictedIdentification, true);
				authorization.setAuth(CVAuthorizationAT.auth_CommunityIDVerification, true);
				authorization.setAuth(CVAuthorizationAT.auth_AgeVerification, true);

				break;
			}

			case C:
			{
				// write authorization
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG20, false);

				// read authorization
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG1, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG2, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG3, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG4, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG5, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG6, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG7, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG8, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG9, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG10, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG13, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG14, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG15, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG16, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG20, false);

				// common authorization
				authorization.setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_InstallCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_PINManagement, true);
				authorization.setAuth(CVAuthorizationAT.auth_CANAllowed, true);
				authorization.setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, true);
				authorization.setAuth(CVAuthorizationAT.auth_RestrictedIdentification, false);
				authorization.setAuth(CVAuthorizationAT.auth_CommunityIDVerification, false);
				authorization.setAuth(CVAuthorizationAT.auth_AgeVerification, false);

				break;
			}

			case D:
			{
				// write authorization
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG20, false);

				// read authorization
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG1, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG2, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG3, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG4, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG5, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG6, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG7, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG8, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG9, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG10, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG13, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG14, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG15, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG16, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG17, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG18, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG19, false);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG20, false);

				// common authorization
				authorization.setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_InstallCertificate, false);
				authorization.setAuth(CVAuthorizationAT.auth_PINManagement, true);
				authorization.setAuth(CVAuthorizationAT.auth_CANAllowed, true);
				authorization.setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, true);
				authorization.setAuth(CVAuthorizationAT.auth_RestrictedIdentification, true);
				authorization.setAuth(CVAuthorizationAT.auth_CommunityIDVerification, false);
				authorization.setAuth(CVAuthorizationAT.auth_AgeVerification, false);

				break;
			}

			default:
			{
				// write authorization
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG17, true);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG18, true);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG19, true);
				authorization.setAuth(CVAuthorizationAT.auth_Write_DG20, true);

				// read authorization
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG1, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG2, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG3, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG4, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG5, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG6, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG7, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG8, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG9, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG10, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG13, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG14, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG15, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG16, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG17, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG18, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG19, true);
				authorization.setAuth(CVAuthorizationAT.auth_Read_DG20, true);

				// common authorization
				authorization.setAuth(CVAuthorizationAT.auth_InstallQulifiedCertificate, true);
				authorization.setAuth(CVAuthorizationAT.auth_InstallCertificate, true);
				authorization.setAuth(CVAuthorizationAT.auth_PINManagement, true);
				authorization.setAuth(CVAuthorizationAT.auth_CANAllowed, true);
				authorization.setAuth(CVAuthorizationAT.auth_PrivilegedTerminal, true);
				authorization.setAuth(CVAuthorizationAT.auth_RestrictedIdentification, true);
				authorization.setAuth(CVAuthorizationAT.auth_CommunityIDVerification, true);
				authorization.setAuth(CVAuthorizationAT.auth_AgeVerification, true);

				break;
			}
		}

		this.authorization = authorization;
	}

	/**
	 * @return the authorization
	 */
	public CVAuthorization getAuthorization()
	{
		return authorization;
	}

	/**
	 * @return the indicator
	 */
	public CVTermIndicator getTypIndicator()
	{
		return indicator;
	}

	public static CVAuthorizationType getCertificateAuthorization(CVTermIndicator indicator) throws InvalidParameterException
	{
		CVAuthorizationType[] typs = CVAuthorizationType.values();

		for (CVAuthorizationType typ : typs)
		{
			if (typ.indicator == indicator)
			{
				return typ;
			}
		}

		throw new InvalidParameterException("Unable to find a terminal authorization template for the indicator " + indicator);
	}
}
