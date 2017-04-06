package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.secunet.eidserver.testbed.common.enumerations.Interface;
import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;

public class TlsTestingTestingPojo extends BaseTestingPojo implements Serializable, Tls
{
	private static final long serialVersionUID = 2242053987046436540L;

	private Set<IcsTlsSignaturealgorithms> signatureAlgorithms = new HashSet<>();

	private List<IcsTlsCiphersuite> tlsCiphersuites = new ArrayList<>();

	private Set<IcsEllipticcurve> ellipticCurves = new HashSet<>();

	private Set<TlsClientCertificate> clientCertificates = new HashSet<>();

	private IcsTlsVersion tlsVersion;

	private Interface functionalInterface;

	public TlsTestingTestingPojo()
	{
	}

	@Override
	public Set<IcsTlsSignaturealgorithms> getSignatureAlgorithms()
	{
		return signatureAlgorithms;
	}

	@Override
	public void setSignatureAlgorithms(Set<IcsTlsSignaturealgorithms> signatureAlgorithms) throws NumberFormatException
	{
		this.signatureAlgorithms = signatureAlgorithms;
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
	public IcsTlsVersion getTlsVersion()
	{
		return tlsVersion;
	}

	@Override
	public void setTlsVersion(IcsTlsVersion tlsVersion)
	{
		this.tlsVersion = tlsVersion;
	}

	@Override
	public List<IcsTlsCiphersuite> getTlsCiphersuites()
	{
		return tlsCiphersuites;
	}

	@Override
	public void setTlsCiphersuites(List<IcsTlsCiphersuite> tlsCiphersuites)
	{
		this.tlsCiphersuites = tlsCiphersuites;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null || this.getClass() != object.getClass())
		{
			return false;
		}
		final TlsTestingTestingPojo other = (TlsTestingTestingPojo) object;
		if ((this.getSignatureAlgorithms() == null) ? (other.getSignatureAlgorithms() != null) : !this.getSignatureAlgorithms().equals(other.getSignatureAlgorithms()))
		{
			return false;
		}
		if ((this.getTlsCiphersuites() == null) ? (other.getTlsCiphersuites() != null) : !this.getTlsCiphersuites().equals(other.getTlsCiphersuites()))
		{
			return false;
		}
		if ((this.getTlsVersion() == null) ? (other.getTlsVersion() != null) : !this.getTlsVersion().equals(other.getTlsVersion()))
		{
			return false;
		}
		if ((this.getEllipticCurves() == null) ? (other.getEllipticCurves() != null) : !this.getEllipticCurves().equals(other.getEllipticCurves()))
		{
			return false;
		}
		if ((this.getInterface() == null) ? (other.getInterface() != null) : !this.getInterface().equals(other.getInterface()))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 53 * hash + (this.getSignatureAlgorithms() != null ? this.getSignatureAlgorithms().hashCode() : 0);
		hash = 53 * hash + (this.getTlsCiphersuites() != null ? this.getTlsCiphersuites().hashCode() : 0);
		hash = 53 * hash + (this.getTlsVersion() != null ? this.getTlsVersion().hashCode() : 0);
		hash = 53 * hash + (this.getEllipticCurves() != null ? this.getEllipticCurves().hashCode() : 0);
		hash = 53 * hash + (this.getInterface() != null ? this.getInterface().hashCode() : 0);
		return hash;
	}

	@Override
	public Interface getInterface()
	{
		return functionalInterface;
	}

	@Override
	public void setInterface(Interface functionalInterface)
	{
		this.functionalInterface = functionalInterface;
	}

	@Override
	public int[] getCiphersuitesAsRfcInt() throws IllegalAccessException, NoSuchFieldException
	{
		return BcTlsHelper.convertCipherSuiteSetToIntArray(tlsCiphersuites);
	}

	@Override
	public void setCiphersuitesAsRfcInt(int[] tlsCiphersuites)
	{
		this.tlsCiphersuites = BcTlsHelper.convertIntArrayToCiphersuite(tlsCiphersuites);
	}

	@Override
	public Set<TlsClientCertificate> getClientCertificates()
	{
		return this.clientCertificates;
	}

	@Override
	public void setClientCertificates(Set<TlsClientCertificate> clientCertificates)
	{
		this.clientCertificates = clientCertificates;
	}

}