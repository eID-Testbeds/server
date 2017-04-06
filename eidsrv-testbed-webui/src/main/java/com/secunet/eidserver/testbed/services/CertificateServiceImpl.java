package com.secunet.eidserver.testbed.services;

import javax.ejb.EJB;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.CertificateService")
public class CertificateServiceImpl implements CertificateService
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private AuthenticationController authenticationController;

	@EJB
	private CertificateController certificateController;

	@Override
	public String uploadX509Certificate(String profileId, String certificateName, String base64EncodedCertificate, String token)
	{
		logger.trace(String.format("Upload X509 certificate %s >>", certificateName));

		final Token authToken = authenticationController.findToken(token);

		if (Role.PROFILE_CREATOR != authToken.getUser().getRole() && Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return certificateController.uploadX509(profileId, certificateName, base64EncodedCertificate);
	}

}
