package com.secunet.eidserver.testbed.services;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface CertificateService
{

	@WebMethod
	public String uploadX509Certificate(final String profileId, final String certificateName, final String base64EncodedCertificate, final String token);

}
