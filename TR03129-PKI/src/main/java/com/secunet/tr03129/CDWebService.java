package com.secunet.tr03129;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.BindingType;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.tr03129.cd.DVCACERTDESCRIPTION;
import com.secunet.tr03129.cd.GetCertificateDescriptionRequest;
import com.secunet.tr03129.cd.GetCertificateDescriptionResponse;
import com.secunet.tr03129.cvcert.CertDescRegistry;

@WebService(endpointInterface = "com.secunet.tr03129.cd.DVCACERTDESCRIPTION", wsdlLocation = "WEB-INF/wsdl/CertDesc.wsdl", targetNamespace = "urn:DVCA/v2", serviceName = "DVCA_CertDescriptionService", portName = "Soap12")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.BARE)
@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class CDWebService implements DVCACERTDESCRIPTION
{
	private static final Logger log = LogManager.getLogger(CDWebService.class);

	@Override
	public GetCertificateDescriptionResponse getCertificateDescription(GetCertificateDescriptionRequest request)
	{
		log.log(Level.INFO, "Incoming request: getCertificateDescription(" + request + ")");

		GetCertificateDescriptionResponse response = new GetCertificateDescriptionResponse();

		byte[] hash = request.getHash();
		if (hash != null && hash.length > 0)
		{
			log.log(Level.INFO, "Recieved hash: " + Hex.encodeHexString(hash));
			byte[] description = CertDescRegistry.getDescriptionForHash(hash);
			if (description == null)
			{
				log.log(Level.INFO, "The hash was not found in the certificate description database");
			}
			else
			{
				log.log(Level.INFO, "The hash corresponds to the description " + Hex.encodeHexString(description));
				response.setCertificateDescription(description);
			}
		}
		else
		{
			log.error("The request did not contain a certificate hash");
		}
		return response;
	}
}
