package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementWrappingUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;

public class XmlEncryptionKeyAgreementTestingPojo extends BaseTestingPojo implements Serializable, XmlEncryptionKeyAgreement
{
	private static final long serialVersionUID = 7205273950543653594L;

	private IcsXmlsecEncryptionKeyAgreementUri agreementAlgorithm;

	private IcsXmlsecEncryptionKeyAgreementWrappingUri wrappingAlgorithm;

	private Set<BigInteger> bitLengths;

	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlEncryptionKeyAgreementTestingPojo()
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

	@Override
	public boolean equals(Object object)
	{
		if (object == null || this.getClass() != object.getClass())
		{
			return false;
		}
		final XmlSignatureTestingPojo other = (XmlSignatureTestingPojo) object;
		if ((this.getKeyAgreementAlgorithm() == null) ? (other.getDigestMethod() != null) : !this.getKeyAgreementAlgorithm().equals(other.getDigestMethod()))
		{
			return false;
		}
		if ((this.getKeyWrappingAlgorithm() == null) ? (other.getSignatureAlgorithm() != null) : !this.getKeyWrappingAlgorithm().equals(other.getSignatureAlgorithm()))
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
		hash = 53 * hash + (this.getKeyAgreementAlgorithm() != null ? this.getKeyAgreementAlgorithm().hashCode() : 0);
		hash = 53 * hash + (this.getKeyWrappingAlgorithm() != null ? this.getKeyWrappingAlgorithm().hashCode() : 0);
		hash = 53 * hash + (this.getBitLengths() != null ? this.getBitLengths().hashCode() : 0);
		hash = 53 * hash + (this.getEllipticCurves() != null ? this.getEllipticCurves().hashCode() : 0);
		return hash;
	}

}