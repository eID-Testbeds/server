package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.math.BigInteger;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementWrappingUri;

/**
 * NOTE: Implementing classes MUST override hashCode and equals!
 * 
 */
public interface XmlEncryptionKeyAgreement
{

	/**
	 * Retrieve the UUID of the key agreement algorithm. This ID is only used for persisting purposes and has no influence on the tests.
	 * 
	 * @return {@link String} The UUID
	 */
	public String getId();

	/**
	 * Set the UUID of the key agreement algorithm. This ID is only used for persisting purposes and has no influence on the tests.
	 * 
	 * @return {@link String} The UUID
	 */
	public void setId(String id);


	public IcsXmlsecEncryptionKeyAgreementUri getKeyAgreementAlgorithm();

	public void setKeyAgreementAlgorithm(IcsXmlsecEncryptionKeyAgreementUri agreementAlgorithm);

	public IcsXmlsecEncryptionKeyAgreementWrappingUri getKeyWrappingAlgorithm();

	public void setKeyWrappingAlgorithm(IcsXmlsecEncryptionKeyAgreementWrappingUri agreementAlgorithm);

	public Set<BigInteger> getBitLengths();

	public void setBitLengths(Set<BigInteger> bitLengths);

	public Set<IcsEllipticcurve> getEllipticCurves();

	public void setEllipticCurves(Set<IcsEllipticcurve> ellipticCurves);
}
