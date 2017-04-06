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
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;

@Entity
@Table(name = "XML_ENCRYPTION_TRANSPORT")
@NamedQuery(name = "XmlEncryptionKeyTransportEntity.findAll", query = "SELECT x FROM XmlEncryptionKeyTransportEntity x")
public class XmlEncryptionKeyTransportEntity extends BaseEntity implements Serializable, XmlEncryptionKeyTransport
{
	private static final long serialVersionUID = -7927845987503142070L;

	@Column(name = "TRANSPORT_ALGORITHM")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecEncryptionKeyTransportUri transportAlgorithm;

	@Column(name = "BIT_LENGTH")
	private Set<BigInteger> bitLengths;

	@Column(name = "EC_CURVE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlEncryptionKeyTransportEntity()
	{
	}

	@Override
	public IcsXmlsecEncryptionKeyTransportUri getTransportAlgorithm()
	{
		return this.transportAlgorithm;
	}

	@Override
	public void setTransportAlgorithm(IcsXmlsecEncryptionKeyTransportUri transportAlgorithm)
	{
		this.transportAlgorithm = transportAlgorithm;
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
		XmlEncryptionKeyTransportEntity other = (XmlEncryptionKeyTransportEntity) obj;
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
		if (transportAlgorithm != other.transportAlgorithm)
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
		int result = prime * ((bitLengths == null) ? 0 : bitLengths.hashCode());
		result = prime * result + ((ellipticCurves == null) ? 0 : ellipticCurves.hashCode());
		result = prime * result + ((transportAlgorithm == null) ? 0 : transportAlgorithm.hashCode());
		return result;
	}
}