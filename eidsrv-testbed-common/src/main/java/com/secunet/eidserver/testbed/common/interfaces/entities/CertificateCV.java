package com.secunet.eidserver.testbed.common.interfaces.entities;

import de.bsi.testbedutils.cvc.cvcertificate.CVCertificate;

public interface CertificateCV
{

	public CVCertificate getCvCertificate();

	public void setCvCertificate(CVCertificate CvCertificate);

	public String getId();

	public void setId(String id);

	public String getCertificateName();

	public void setCertificateName(String certificateName);

}
