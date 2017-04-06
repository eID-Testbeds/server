package com.secunet.tr03129.cvcert;

import java.security.InvalidParameterException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificateHolderReference;

public enum CVTermIndicator
{
	// note: this has to be a duplicate of the eService enum in the eID-Server testbed due to restrictions of the project structure
	A, B, C, D, EDSA, ERSA, EECDSA, A2,

	Unknown;

	static Logger log = LogManager.getLogger(CVTermIndicator.class);

	public static CVTermIndicator getCertificateAuthorization(CVCertificateHolderReference cvReference) throws InvalidParameterException
	{
		if (cvReference == null)
		{
			throw new InvalidParameterException("The certificate holder reference was not provided");
		}

		String mnemonic = cvReference.getMnemonic();
		log.debug("Search for the right CVTermIndicator for mnemonic: " + mnemonic);

		if (mnemonic == null || mnemonic.isEmpty())
		{
			return Unknown;
		}
		else
		{
			for (CVTermIndicator cvIndicator : CVTermIndicator.values())
			{
				if (mnemonic.endsWith(cvIndicator.toString()))
				{
					return cvIndicator;
				}
			}
			log.warn("Could not find a CV terminal indicator for terminal mnemonic " + mnemonic);
			return Unknown;
		}
	}
}
