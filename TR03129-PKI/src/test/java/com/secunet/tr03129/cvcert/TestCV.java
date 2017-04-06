package com.secunet.tr03129.cvcert;

import java.security.InvalidParameterException;

import org.junit.Assert;
import org.junit.Test;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificateHolderReference;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVAuthorityRefNotValidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceInvalidCountryCode;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceTooLong;

public class TestCV
{
	@Test
	public void testGetCertificateAuthorization()
	{
		CVTermIndicator indicator = CVTermIndicator.A;
		Assert.assertEquals(CVAuthorizationType.AuthorizationA, CVAuthorizationType.getCertificateAuthorization(indicator));
	}

	@Test
	public void testCvcaIndicator()
	{
		Assert.assertEquals(CVCAIndicator.CA_1, CVCAIndicator.getCVCAIndicator("testbed-CA1-pki"));
	}

	@Test(expected = InvalidParameterException.class)
	public void testInvalidCvcaIndicator()
	{
		CVCAIndicator.getCVCAIndicator("testbed-UNDEFINED-pki");
	}

	@Test
	public void testTerminalIndicator() throws CVCertificateHolderReferenceInvalidCountryCode, CVCertificateHolderReferenceTooLong, CVAuthorityRefNotValidException
	{
		Assert.assertEquals(CVTermIndicator.A, CVTermIndicator.getCertificateAuthorization(new CVCertificateHolderReference("DETESTA00001")));
	}

	public void testInvalidTerminalIndicator() throws CVCertificateHolderReferenceInvalidCountryCode, CVCertificateHolderReferenceTooLong, CVAuthorityRefNotValidException
	{
		Assert.assertEquals(CVTermIndicator.Unknown, CVTermIndicator.getCertificateAuthorization(new CVCertificateHolderReference("DETESTZZZZ00001")));
	}
}
