package com.secunet.tr03129.cvcert;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.commons.codec.digest.DigestUtils;

import com.secunet.tr03129.util.FileUtil;

public enum CertificateDescription
{

	CERT_DESC_TERM_1_A(CVCAIndicator.CA_1, CVTermIndicator.A, "desc/CERT_ECARD_CV_TERM_1_A.bin"), CERT_DESC_TERM_1_B(CVCAIndicator.CA_1, CVTermIndicator.B,
			"desc/CERT_ECARD_CV_TERM_1_B.bin"), CERT_DESC_TERM_1_C(CVCAIndicator.CA_1, CVTermIndicator.C, "desc/CERT_ECARD_CV_TERM_1_C.bin"), CERT_DESC_TERM_1_D(CVCAIndicator.CA_1, CVTermIndicator.D,
					"desc/CERT_ECARD_CV_TERM_1_D.bin"), CERT_DESC_TERM_2_A(CVCAIndicator.CA_1, CVTermIndicator.A2, "desc/CERT_ECARD_CV_TERM_2_A.bin"), CERT_DESC_TERM_1_EDSA(CVCAIndicator.CA_1,
							CVTermIndicator.EDSA, "desc/CERT_ECARD_CV_TERM_1_E_DSA.bin"), CERT_DESC_TERM_1_EECDSA(CVCAIndicator.CA_1, CVTermIndicator.EECDSA,
									"desc/CERT_ECARD_CV_TERM_1_E_ECDSA.bin"), CERT_DESC_TERM_1_ERSA(CVCAIndicator.CA_1, CVTermIndicator.ERSA, "desc/CERT_ECARD_CV_TERM_1_E_RSA.bin");

	private String path;
	private CVCAIndicator caIndicator;
	private CVTermIndicator termIndicator;

	private CertificateDescription(CVCAIndicator caIndicator, CVTermIndicator termIndicator, String path)
	{
		this.caIndicator = caIndicator;
		this.termIndicator = termIndicator;
		this.path = path;
	}


	public byte[] getBytes() throws IOException
	{
		return FileUtil.getFileFromPath(path);
	}

	public byte[] getHash() throws IOException
	{
		return DigestUtils.sha256(getBytes());
	}

	public static CertificateDescription getCertificateDescription(final CVCAIndicator caIndicator, final CVTermIndicator termIndicator) throws InvalidParameterException
	{
		CertificateDescription[] certDesc = CertificateDescription.values();

		for (CertificateDescription desc : certDesc)
		{
			if (desc.caIndicator == caIndicator && desc.termIndicator == termIndicator)
				return desc;
		}

		throw new InvalidParameterException("No description found for the given CA indicator " + caIndicator + " and terminal indicator " + termIndicator);
	}
}
