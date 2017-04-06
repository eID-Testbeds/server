package com.secunet.tr03129;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import com.secunet.tr03129.cd.GetCertificateDescriptionRequest;
import com.secunet.tr03129.cd.GetCertificateDescriptionResponse;
import com.secunet.tr03129.cvcert.CertificateDescription;

public class TestCDWebservice
{
	private CDWebService underTest = new CDWebService();

	@Test
	public void testGetCertificateDescription() throws IOException
	{
		GetCertificateDescriptionRequest mockRequest = new GetCertificateDescriptionRequest();
		mockRequest.setHash(CertificateDescription.CERT_DESC_TERM_1_A.getHash());
		GetCertificateDescriptionResponse response = underTest.getCertificateDescription(mockRequest);
		Assert.assertNotNull(response);
		Assert.assertArrayEquals(CertificateDescription.CERT_DESC_TERM_1_A.getBytes(), response.getCertificateDescription());
	}

	@Test(expected = InvalidParameterException.class)
	public void testUnknownCertificateDescription() throws DecoderException
	{
		GetCertificateDescriptionRequest mockRequest = new GetCertificateDescriptionRequest();
		mockRequest.setHash(Hex.decodeHex("D0993C5373A7638382C68D2D33622DB255855821B5B20FEF0E10B6A83A6E4F99".toCharArray()));
		GetCertificateDescriptionResponse response = underTest.getCertificateDescription(mockRequest);
		Assert.assertNotNull(response);
		Assert.assertNull(response.getCertificateDescription());
	}

}
