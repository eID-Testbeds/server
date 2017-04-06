package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.List;
import java.util.Set;

import com.secunet.eidserver.testbed.common.enumerations.Interface;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;

/**
 * NOTE: This class MUST override hashCode and equals!
 * 
 *
 */
public interface Tls
{
	public String getId();

	public void setId(String id);

	public Set<IcsTlsSignaturealgorithms> getSignatureAlgorithms();

	public void setSignatureAlgorithms(Set<IcsTlsSignaturealgorithms> signatureAlgorithms);

	public Set<IcsEllipticcurve> getEllipticCurves();

	public void setEllipticCurves(Set<IcsEllipticcurve> ellipticCurves);

	public IcsTlsVersion getTlsVersion();

	public void setTlsVersion(IcsTlsVersion tlsVersion);

	public List<IcsTlsCiphersuite> getTlsCiphersuites();

	public void setTlsCiphersuites(List<IcsTlsCiphersuite> tlsCiphersuites);

	public int[] getCiphersuitesAsRfcInt() throws IllegalAccessException, NoSuchFieldException;

	public void setCiphersuitesAsRfcInt(int[] tlsCiphersuites);

	public Interface getInterface();

	public void setInterface(Interface i);

	public Set<TlsClientCertificate> getClientCertificates();

	public void setClientCertificates(Set<TlsClientCertificate> clientCertificates);

}
