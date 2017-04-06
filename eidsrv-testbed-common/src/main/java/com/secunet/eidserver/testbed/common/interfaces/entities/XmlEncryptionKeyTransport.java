package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.math.BigInteger;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;

/**
 * NOTE: Implementing classes MUST override hashCode and equals!
 * 
 */
public interface XmlEncryptionKeyTransport
{

	public String getId();

	public void setId(String id);

	public IcsXmlsecEncryptionKeyTransportUri getTransportAlgorithm();

	public void setTransportAlgorithm(IcsXmlsecEncryptionKeyTransportUri transportAlgorithm);

	public Set<BigInteger> getBitLengths();

	public void setBitLengths(Set<BigInteger> bitLengths);

	public Set<IcsEllipticcurve> getEllipticCurves();

	public void setEllipticCurves(Set<IcsEllipticcurve> ellipticCurves);

}
