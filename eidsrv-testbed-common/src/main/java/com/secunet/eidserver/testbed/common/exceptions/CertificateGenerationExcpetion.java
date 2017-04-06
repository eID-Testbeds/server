package com.secunet.eidserver.testbed.common.exceptions;

import javax.security.cert.CertificateException;

public class CertificateGenerationExcpetion extends CertificateException
{
	private static final long serialVersionUID = 8921553844271262450L;

	public CertificateGenerationExcpetion()
	{
		super();
	}

	public CertificateGenerationExcpetion(String message)
	{
		super(message);
	}
}
