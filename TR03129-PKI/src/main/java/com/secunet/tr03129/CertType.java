package com.secunet.tr03129;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public enum CertType
{

	MASTERLIST("x509/MASTERLIST_ECARD_CSCA_1.DER"), DEFECTLIST("x509/BLACKLIST_ECARD_DS_1.DER"), BLACKLIST("x509/BLACKLIST_ECARD_EIDCARDS_TERM_1_X.DER"), SECTOR_PUBLIC_KEY1(
			"keys/TERM_SECTOR1.x509.cv"), SECTOR_PUBLIC_KEY2("keys/TERM_SECTOR2.x509.cv"),

	// CVCAs
	CVCA1("certs/CERT_ECARD_CV_CVCA_1.cvcert"), CVCA2A("certs/CERT_ECARD_CV_CVCA_2_A.cvcert"), CVCA2B("certs/CERT_ECARD_CV_CVCA_2_B.cvcert"), CVCA2C(
			"certs/CERT_ECARD_CV_CVCA_2_C.cvcert"), CVCA2_LINK1("certs/CERT_ECARD_CV_LINK_2_A.cvcert"), CVCA2_LINK2("certs/CERT_ECARD_CV_LINK_2_B.cvcert"),

	// DVCAs
	DVCA1A("certs/CERT_ECARD_CV_DV_1_A.cvcert"), DVCA2A("certs/CERT_ECARD_CV_DV_2_A.cvcert"), DVCA1A_KEY("keys/CERT_ECARD_CV_DV_1_A.pkcs8"), DVCA2A_KEY("keys/CERT_ECARD_CV_DV_2_A.pkcs8");

	private String location;

	private CertType(String location)
	{
		this.location = location;
	}

	public String getFileLocation()
	{
		return location;
	}

	public byte[] getRaw() throws IOException
	{
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

		return IOUtils.toByteArray(stream);
	}
}
