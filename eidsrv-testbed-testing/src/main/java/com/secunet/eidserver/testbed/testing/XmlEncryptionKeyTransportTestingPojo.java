package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;

public class XmlEncryptionKeyTransportTestingPojo extends BaseTestingPojo implements Serializable, XmlEncryptionKeyTransport
{
	private static final long serialVersionUID = -7927845987503142070L;

	private IcsXmlsecEncryptionKeyTransportUri transportAlgorithm;

	private Set<BigInteger> bitLengths;

	private Set<IcsEllipticcurve> ellipticCurves;

	public XmlEncryptionKeyTransportTestingPojo()
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

	@Override
	public boolean equals(Object object)
	{
		if (object == null || this.getClass() != object.getClass())
		{
			return false;
		}
		final XmlSignatureTestingPojo other = (XmlSignatureTestingPojo) object;
		if ((this.getTransportAlgorithm() == null) ? (other.getDigestMethod() != null) : !this.getTransportAlgorithm().equals(other.getDigestMethod()))
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
		hash = 53 * hash + (this.getTransportAlgorithm() != null ? this.getTransportAlgorithm().hashCode() : 0);
		hash = 53 * hash + (this.getBitLengths() != null ? this.getBitLengths().hashCode() : 0);
		hash = 53 * hash + (this.getEllipticCurves() != null ? this.getEllipticCurves().hashCode() : 0);
		return hash;
	}
}