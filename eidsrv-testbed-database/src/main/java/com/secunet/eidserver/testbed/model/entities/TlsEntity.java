package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.enumerations.Interface;
import com.secunet.eidserver.testbed.common.helper.BcTlsHelper;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;

@Entity
@Table(name = "TLS")
@NamedQuery(name = "TlsEntity.findAll", query = "SELECT t FROM TlsEntity t")
public class TlsEntity extends BaseEntity implements Serializable, Tls
{
	private static final long serialVersionUID = 2242053987046436540L;

	@Column(name = "SIGNATURE_ALGORITHM")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsTlsSignaturealgorithms> signatureAlgorithms = new HashSet<>();

	@Column(name = "CIPHERSUITE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<IcsTlsCiphersuite> tlsCiphersuites = new ArrayList<>();

	@Column(name = "CURVE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsEllipticcurve> ellipticCurves = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TlsClientCertificateEntity.class)
	@JoinTable(name = "Tls_ClientCertificate", joinColumns = { @JoinColumn(name = "TLS_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "CLIENT_CERTIFICATE_ID", referencedColumnName = "id") })
	private Set<TlsClientCertificate> clientCertificates = new HashSet<>();

	@Column(name = "VERSION")
	@Enumerated(EnumType.STRING)
	private IcsTlsVersion tlsVersion;

	@Column(name = "INTERFACE")
	@Enumerated(EnumType.STRING)
	private Interface functionalInterface;

	public TlsEntity()
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
		TlsEntity other = (TlsEntity) obj;
		if (clientCertificates == null)
		{
			if (other.clientCertificates != null)
				return false;
		}
		else if (!clientCertificates.equals(other.clientCertificates))
			return false;
		if (ellipticCurves == null)
		{
			if (other.ellipticCurves != null)
				return false;
		}
		else if (!ellipticCurves.equals(other.ellipticCurves))
			return false;
		if (functionalInterface != other.functionalInterface)
			return false;
		if (signatureAlgorithms == null)
		{
			if (other.signatureAlgorithms != null)
				return false;
		}
		else if (!signatureAlgorithms.equals(other.signatureAlgorithms))
			return false;
		if (tlsCiphersuites == null)
		{
			if (other.tlsCiphersuites != null)
				return false;
		}
		else if (!tlsCiphersuites.equals(other.tlsCiphersuites))
			return false;
		if (tlsVersion != other.tlsVersion)
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
		int result = prime * ((clientCertificates == null) ? 0 : clientCertificates.hashCode());
		result = prime * result + ((ellipticCurves == null) ? 0 : ellipticCurves.hashCode());
		result = prime * result + ((functionalInterface == null) ? 0 : functionalInterface.hashCode());
		result = prime * result + ((signatureAlgorithms == null) ? 0 : signatureAlgorithms.hashCode());
		result = prime * result + ((tlsCiphersuites == null) ? 0 : tlsCiphersuites.hashCode());
		result = prime * result + ((tlsVersion == null) ? 0 : tlsVersion.hashCode());
		return result;
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

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.entities.Tls#getCiphersuitesAsRfcInt()
	 */
	@Override
	public int[] getCiphersuitesAsRfcInt() throws IllegalAccessException, NoSuchFieldException
	{
		return BcTlsHelper.convertCipherSuiteSetToIntArray(tlsCiphersuites);
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.entities.Tls#setCiphersuitesAsRfcInt(int[])
	 */
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

	@Override
	public String toString()
	{
		String stringRep = "ID: " + this.getId() + System.getProperty("line.separator");
		if (getInterface() != null)
			stringRep += "Interface: " + this.getInterface().toString() + System.getProperty("line.separator");
		else
			stringRep += "Interface: NULL" + System.getProperty("line.separator");
		if (this.getTlsVersion() != null)
			stringRep += "TLS Version: " + this.getTlsVersion().value() + System.getProperty("line.separator");
		else
			stringRep += "TLS Version: NULL" + System.getProperty("line.separator");
		String suites = new String();
		for (IcsTlsCiphersuite suite : this.getTlsCiphersuites())
		{
			if (suite != null)
				suites += suite.value() + ", ";
		}
		stringRep += "Ciphersuites: " + ((suites.length() > 0) ? suites.substring(0, suites.length() - 2) : "") + System.getProperty("line.separator");
		String sigAlgs = new String();
		for (IcsTlsSignaturealgorithms sigAlg : this.getSignatureAlgorithms())
		{
			sigAlgs += sigAlg.value() + ", ";
		}
		stringRep += "Signature Algorithms: " + ((sigAlgs.length() > 0) ? sigAlgs.substring(0, sigAlgs.length() - 2) : "") + System.getProperty("line.separator");
		stringRep += "Contains client certificates: " + ((this.getClientCertificates().size() > 0) ? "Yes" : "No");
		return stringRep;
	}

}