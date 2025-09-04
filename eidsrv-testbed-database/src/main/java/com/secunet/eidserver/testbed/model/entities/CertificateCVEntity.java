package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateCV;
import de.bsi.testbedutils.cvc.cvcertificate.CVCertificate;
import de.bsi.testbedutils.cvc.cvcertificate.DataBuffer;
import de.bsi.testbedutils.cvc.cvcertificate.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Entity
@Table(name = "CERTIFICATE_CV")
@NamedQuery(name = "CertificateCVEntity.findAll", query = "SELECT t FROM CertificateCVEntity t")
public class CertificateCVEntity extends BaseEntity implements Serializable, CertificateCV
{
	private static final long serialVersionUID = 1603400191631663139L;

	private static final Logger logger = LogManager.getRootLogger();

	@Column(name = "CERTIFICATE_NAME")
	private String certificateName;

	@Lob
	@Column(length = 500000)
	private byte[] cvcertificate;

	@Override
	public CVCertificate getCvCertificate()
	{
		try
		{
			return new CVCertificate(new DataBuffer(cvcertificate));
		}
		catch (CVTagNotFoundException | CVBufferNotEmptyException | CVInvalidOidException |
               CVDecodeErrorException | CVInvalidDateException | CVInvalidECPointLengthException e)
		{
			logger.warn("Could not read CV certificate: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setCvCertificate(CVCertificate cvCertificate)
	{
        try
		{
            this.cvcertificate = cvCertificate.generateCert().toByteArray();
        } catch (CVAuthorityRefNotValidException | CVInvalidKeySourceException | CVSignOpKeyMismatchException | CVInvalidOidException | CVMissingKeyException | CVKeyTypeNotSupportedException e) {
			logger.warn("Could not set CV certificate: " + e.getMessage());
        }
	}

	@Override
	public String getCertificateName()
	{
		return certificateName;
	}

	@Override
	public void setCertificateName(String certificateName)
	{
		this.certificateName = certificateName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certificateName == null) ? 0 : certificateName.hashCode());
		result = prime * result + Arrays.hashCode(cvcertificate);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CertificateCVEntity other = (CertificateCVEntity) obj;
		if (certificateName == null)
		{
			if (other.certificateName != null)
				return false;
		}
		else if (!certificateName.equals(other.certificateName))
			return false;
		if (!Arrays.equals(cvcertificate, other.cvcertificate))
			return false;
		return true;
	}
}
