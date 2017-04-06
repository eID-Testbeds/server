package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

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
public class XmlSignatureTestingPojo extends BaseTestingPojo implements Serializable, XmlSignature
{
	private static final long serialVersionUID = 674224205816515393L;

	private IcsXmlsecSignatureUri signatureAlgorithm;

	private IcsXmlsecSignatureCanonicalization canonicalizationMethod;

	private IcsXmlsecSignatureDigest digestMethod;

	private Set<BigInteger> bitLengths;

	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlSignatureTestingPojo()
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

	@Override
	public boolean equals(Object object)
	{
		if (object == null || this.getClass() != object.getClass())
		{
			return false;
		}
		final XmlSignatureTestingPojo other = (XmlSignatureTestingPojo) object;
		if ((this.getDigestMethod() == null) ? (other.getDigestMethod() != null) : !this.getDigestMethod().equals(other.getDigestMethod()))
		{
			return false;
		}
		if ((this.getSignatureAlgorithm() == null) ? (other.getSignatureAlgorithm() != null) : !this.getSignatureAlgorithm().equals(other.getSignatureAlgorithm()))
		{
			return false;
		}
		if ((this.getCanonicalizationMethod() == null) ? (other.getCanonicalizationMethod() != null) : !this.getCanonicalizationMethod().equals(other.getCanonicalizationMethod()))
		{
			return false;
		}
		if ((this.getBitLengths() == null) ? (other.getBitLengths() != null) : !this.getBitLengths().equals(other.getBitLengths()))
		{
			return false;
		}
		if ((this.getEllipticCurves() == null) ? (other.getEllipticCurves() != null) : !this.getEllipticCurves().equals(other.getEllipticCurves()))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 53 * hash + (this.getDigestMethod() != null ? this.getDigestMethod().hashCode() : 0);
		hash = 53 * hash + (this.getSignatureAlgorithm() != null ? this.getSignatureAlgorithm().hashCode() : 0);
		hash = 53 * hash + (this.getCanonicalizationMethod() != null ? this.getCanonicalizationMethod().hashCode() : 0);
		hash = 53 * hash + (this.getBitLengths() != null ? this.getBitLengths().hashCode() : 0);
		hash = 53 * hash + (this.getEllipticCurves() != null ? this.getEllipticCurves().hashCode() : 0);
		return hash;
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

}