package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
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
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;

/**
 * Note: BitLength and EllipticCurve are mutually exclusive!
 * 
 */
@Entity
@Table(name = "XML_SIGNATURE_ENTITY")
@NamedQuery(name = "XmlSignatureEntity.findAll", query = "SELECT x FROM XmlSignatureEntity x")
public class XmlSignatureEntity extends BaseEntity implements Serializable, XmlSignature
{
	private static final long serialVersionUID = 674224205816515393L;

	@Column(name = "SIGNATURE_ALGORITHM")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecSignatureUri signatureAlgorithm;


	@Column(name = "CANONICALIZATION_METHOD")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecSignatureCanonicalization canonicalizationMethod;

	@Column(name = "DIGEST_METHOD")
	@Enumerated(EnumType.STRING)
	private IcsXmlsecSignatureDigest digestMethod;

	@Column(name = "BIT_LENGTH")
	private Set<BigInteger> bitLengths;

	@Column(name = "EC_CURVE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlSignatureEntity()
	{
	}

	@Override
	public IcsXmlsecSignatureUri getSignatureAlgorithm()
	{
		return this.signatureAlgorithm;
	}

	@Override
	public void setSignatureAlgorithm(IcsXmlsecSignatureUri signatureAlgorithm)
	{
		this.signatureAlgorithm = signatureAlgorithm;
	}

	@Override
	public Set<BigInteger> getBitLengths()
	{
		return bitLengths;
	}

	@Override
	public void setBitLengths(Set<BigInteger> bitLengths) throws NumberFormatException
	{
		// Since vaadin selection will return null, instead of an empty set
		// and its faster to fix this here instead of in each formular field
		// we set the null value as a new empty HashSet
		if (bitLengths == null)
			bitLengths = new HashSet<BigInteger>();

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
		XmlSignatureEntity other = (XmlSignatureEntity) obj;
		if (bitLengths == null)
		{
			if (other.bitLengths != null)
				return false;
		}
		else if (!bitLengths.equals(other.bitLengths))
			return false;
		if (canonicalizationMethod != other.canonicalizationMethod)
			return false;
		if (digestMethod != other.digestMethod)
			return false;
		if (ellipticCurves == null)
		{
			if (other.ellipticCurves != null)
				return false;
		}
		else if (!ellipticCurves.equals(other.ellipticCurves))
			return false;
		if (signatureAlgorithm != other.signatureAlgorithm)
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
		result = prime * result + ((canonicalizationMethod == null) ? 0 : canonicalizationMethod.hashCode());
		result = prime * result + ((digestMethod == null) ? 0 : digestMethod.hashCode());
		result = prime * result + ((ellipticCurves == null) ? 0 : ellipticCurves.hashCode());
		result = prime * result + ((signatureAlgorithm == null) ? 0 : signatureAlgorithm.hashCode());
		return result;
	}

	@Override
	public IcsXmlsecSignatureCanonicalization getCanonicalizationMethod()
	{
		return canonicalizationMethod;
	}

	@Override
	public void setCanonicalizationMethod(IcsXmlsecSignatureCanonicalization canonicalizationMethod)
	{
		this.canonicalizationMethod = canonicalizationMethod;
	}

	@Override
	public IcsXmlsecSignatureDigest getDigestMethod()
	{
		return digestMethod;
	}

	@Override
	public void setDigestMethod(IcsXmlsecSignatureDigest digestMethod)
	{
		this.digestMethod = digestMethod;
	}

	@Override
	public String toString()
	{
		return "Signature Algorithm=" + signatureAlgorithm + ", Canonicalization Method=" + canonicalizationMethod + " (digest=" + digestMethod + ", bit lengths=" + bitLengths + ", ell. curves="
				+ ellipticCurves + ")";
	}

}