package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementWrappingUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;

@Entity
@Table(name = "XML_ENCRYPTION_AGREEMENT")
@NamedQuery(name = "XmlEncryptionKeyAgreementEntity.findAll", query = "SELECT x FROM XmlEncryptionKeyAgreementEntity x")
public class XmlEncryptionKeyAgreementEntity extends BaseEntity implements Serializable, XmlEncryptionKeyAgreement
{
	private static final long serialVersionUID = 7205273950543653594L;

	@Column(name = "AGREEMENT_ALGORITHM")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecEncryptionKeyAgreementUri agreementAlgorithm;

	@Column(name = "WRAPPING_ALGORITHM")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecEncryptionKeyAgreementWrappingUri wrappingAlgorithm;

	@Column(name = "BIT_LENGTH")
	private Set<BigInteger> bitLengths;

	@Column(name = "EC_CURVE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlEncryptionKeyAgreementEntity()
	{
	}

	@Override
	public IcsXmlsecEncryptionKeyAgreementUri getKeyAgreementAlgorithm()
	{
		return this.agreementAlgorithm;
	}

	@Override
	public void setKeyAgreementAlgorithm(IcsXmlsecEncryptionKeyAgreementUri agreementAlgorithm)
	{
		this.agreementAlgorithm = agreementAlgorithm;
	}

	@Override
	public IcsXmlsecEncryptionKeyAgreementWrappingUri getKeyWrappingAlgorithm()
	{
		return this.wrappingAlgorithm;
	}

	@Override
	public void setKeyWrappingAlgorithm(IcsXmlsecEncryptionKeyAgreementWrappingUri wrappingAlgorithm)
	{
		this.wrappingAlgorithm = wrappingAlgorithm;
	}

	@Override
	public Set<BigInteger> getBitLengths()
	{
		return bitLengths;
	}

	@Override
	public void setBitLengths(Set<BigInteger> bitLengths) throws NumberFormatException
	{
		if (!BcTlsHelper.checkOnlyAllowedBitlengths(bitLengths))
		{
			throw new NumberFormatException("The provided set contained unallowed values");
		}
		this.bitLengths = bitLengths;
	}

	@Override
	public Set<IcsEllipticcurve> getEllipticCurves()
	{
		return ellipticCurves;
	}

	@Override
	public void setEllipticCurves(Set<IcsEllipticcurve> ellipticCurves)
	{
		this.ellipticCurves = ellipticCurves;
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
		XmlEncryptionKeyAgreementEntity other = (XmlEncryptionKeyAgreementEntity) obj;
		if (agreementAlgorithm != other.agreementAlgorithm)
			return false;
		if (bitLengths == null)
		{
			if (other.bitLengths != null)
				return false;
		}
		else if (!bitLengths.equals(other.bitLengths))
			return false;
		if (ellipticCurves == null)
		{
			if (other.ellipticCurves != null)
				return false;
		}
		else if (!ellipticCurves.equals(other.ellipticCurves))
			return false;
		if (wrappingAlgorithm != other.wrappingAlgorithm)
			return false;
		return true;
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
		int result = prime * ((agreementAlgorithm == null) ? 0 : agreementAlgorithm.hashCode());
		result = prime * result + ((bitLengths == null) ? 0 : bitLengths.hashCode());
		result = prime * result + ((ellipticCurves == null) ? 0 : ellipticCurves.hashCode());
		result = prime * result + ((wrappingAlgorithm == null) ? 0 : wrappingAlgorithm.hashCode());
		return result;
	}

}