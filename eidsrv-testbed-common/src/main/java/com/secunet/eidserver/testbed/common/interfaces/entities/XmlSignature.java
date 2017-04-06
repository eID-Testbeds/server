package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.math.BigInteger;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;

/**
 * NOTE: Subclasses MUST override hashCode and equals!
 * 
 */
public interface XmlSignature
{

	public String getId();

	public void setId(String id);

	public IcsXmlsecSignatureUri getSignatureAlgorithm();

	public void setSignatureAlgorithm(IcsXmlsecSignatureUri signatureAlgorithm);

	public Set<BigInteger> getBitLengths();

	public void setBitLengths(Set<BigInteger> bitLengths);

	public Set<IcsEllipticcurve> getEllipticCurves();

	public void setEllipticCurves(Set<IcsEllipticcurve> ellipticCurves);

	public IcsXmlsecSignatureCanonicalization getCanonicalizationMethod();

	public void setCanonicalizationMethod(IcsXmlsecSignatureCanonicalization canonicalizationMethod);

	public IcsXmlsecSignatureDigest getDigestMethod();

	public void setDigestMethod(IcsXmlsecSignatureDigest digestMethod);

}
