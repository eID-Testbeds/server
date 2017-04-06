package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.net.URL;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsCaDomainparams;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;

public interface TestCandidate
{

	/**
	 * Retrieve the UUID of the test candidate entity. This ID is only used for persisting purposes and has no influence on the tests.
	 * 
	 * @return {@link String} The UUID
	 */
	public String getId();

	/**
	 * Set the UUID of the test candidate entity. This ID is only used for persisting purposes and has no influence on the tests.
	 * 
	 * @param {@link
	 * 			String} The UUID
	 */
	public void setId(String id);

	/**
	 * Retrieve the profile name of the test candidate entity.
	 * 
	 * @return {@link String} The profile name
	 */
	public String getProfileName();

	/**
	 * Set the profile name of the test candidate entity.
	 * 
	 * @return {@link String} The profile name
	 */
	public void setProfileName(String profileName);

	/**
	 * Retrieve the name assigned to the test candidate.
	 * 
	 * @return {@link String} The candidateName
	 */
	public String getCandidateName();

	/**
	 * Set the name of the test candidate.
	 * 
	 * @param {@linkString}
	 * 			candidateName
	 */
	public void setCandidateName(String candidateName);

	/**
	 * Retrieve the name of the vendor of the test candidate.
	 * 
	 * @return {@link String} The vendor
	 */
	public String getVendor();

	/**
	 * Set the name of the vendor of the test candidate.
	 * 
	 * @param {@linkString}
	 * 			vendor
	 */
	public void setVendor(String vendor);

	/**
	 * Retrieve the major version number of the test candidate.
	 * 
	 * @return {@link int} versionMajor
	 */
	public int getVersionMajor();

	/**
	 * Set the major version number of the test candidate.
	 * 
	 * @param {@link
	 * 			int} versionMajor
	 */
	public void setVersionMajor(int versionMajor);

	/**
	 * Retrieve the minor version number of the test candidate.
	 * 
	 * @return {@link int} versionMinor
	 */
	public int getVersionMinor();

	/**
	 * Set the minor version number of the test candidate.
	 * 
	 * @param {@link
	 * 			int} versionMinor
	 */
	public void setVersionMinor(int versionMinor);

	/**
	 * Retrieve the subminor (bugfix) version number of the test candidate.
	 * 
	 * @return {@link int} versionSubminor
	 */
	public int getVersionSubminor();

	/**
	 * Set the subminor (bugfix) version number of the test candidate.
	 * 
	 * @param {@link
	 * 			int} versionSubminor
	 */
	public void setVersionSubminor(int versionSubminor);

	/**
	 * Retrieve the major version number of the eCard-API supported by the test candidate.
	 * 
	 * @return {@link int} apiMajor
	 */
	public int getApiMajor();

	/**
	 * Set the major version number of the eCard-API supported by the test candidate.
	 * 
	 * @param {@link
	 * 			int} apiMajor
	 */
	public void setApiMajor(int apiMajor);

	/**
	 * Retrieve the minor version number of the eCard-API supported by the test candidate.
	 * 
	 * @return {@link int} apiMinor
	 */
	public int getApiMinor();

	/**
	 * Set the minor version number of the eCard-API supported by the test candidate.
	 * 
	 * @param {@link
	 * 			int} apiMinor
	 */
	public void setApiMinor(int apiMinor);

	/**
	 * Retrieve the subminor version number of the eCard-API supported by the test candidate.
	 * 
	 * @return {@link int} apiSubminor
	 */
	public int getApiSubminor();

	/**
	 * Set the subminor version number of the eCard-API supported by the test candidate.
	 * 
	 * @param {@link
	 * 			int} apiSubminor
	 */
	public void setApiSubminor(int apiSubminor);

	/**
	 * Retrieve the URL which is used to connect to the candidate on the eCard-API-Interface.
	 * 
	 * @return {@link URL} ecardapiUrl
	 */
	public URL getEcardapiUrl();

	/**
	 * Set the URL which is used to connect to the candidate on the eCard-API-Interface.
	 * 
	 * @param {@link
	 * 			URL} ecardapiUrl
	 */
	public void setEcardapiUrl(URL ecardapiUrl);

	/**
	 * Retrieve the URL which is used to connect to the candidate on the eID-Interface.
	 * 
	 * @return {@link URL} eidinterfaceUrl
	 */
	public URL getEidinterfaceUrl();

	/**
	 * Set the URL which is used to connect to the candidate on the eID-Interface.
	 * 
	 * @param {@link
	 * 			URL} eidinterfaceUrl
	 */
	public void setEidinterfaceUrl(URL eidinterfaceUrl);

	/**
	 * Retrieve the URL which is used to connect to the candidate when the SAML profile is used.
	 * 
	 * @return {@link URL} samlUrl
	 */
	public URL getSamlUrl();

	/**
	 * Set the URL which is used to connect to the candidate when the SAML profile is used.
	 * 
	 * @param {@link
	 * 			URL} samlUrl
	 */
	public void setSamlUrl(URL samlUrl);

	/**
	 * Retrieve the URL of the TC Token in case the candidate is operating in attached mode.
	 * 
	 * @return {@link URL} attachedTcTokenUrl
	 */
	public URL getAttachedTcTokenUrl();

	/**
	 * Set the URL of the TC Token in case the candidate is operating in attached mode.
	 * 
	 * @param {@link
	 * 			URL} attachedTcTokenUrl
	 */
	public void setAttachedTcTokenUrl(URL attachedTcTokenUrl);

	/**
	 * Retrieve the {@link Set} of {@link IcsOptionalprofile}s supported by the test candidate.
	 * 
	 * @return {@link Set<IcsOptionalprofile>} optionalProfiles
	 */
	public Set<IcsOptionalprofile> getOptionalProfiles();

	/**
	 * Set the {@link Set} of {@link IcsOptionalprofile}s supported by the test candidate.
	 * 
	 * @param {@link
	 * 			Set<IcsOptionalprofile>} optionalProfiles
	 */
	public void setOptionalProfiles(Set<IcsOptionalprofile> optionalProfiles);

	/**
	 * Retrieve the {@link Set} of {@link IcsMandatoryprofile}s supported by the test candidate.
	 * 
	 * @return {@link Set<IcsMandatoryprofile>} mandatoryProfiles
	 */
	public Set<IcsMandatoryprofile> getMandatoryProfiles();

	/**
	 * Set the {@link Set} of {@link IcsMandatoryprofile}s supported by the test candidate.
	 * 
	 * @param {@link
	 * 			Set<IcsMandatoryprofile>} mandatoryProfiles
	 */
	public void setMandatoryProfiles(Set<IcsMandatoryprofile> mandatoryProfiles);


	/**
	 * Retrieve the {@link Set} of Chip Authentication algorithms supported by the test candidate.
	 * 
	 * @return {@link
	 * 		Set<IcsCa>} chipAuthenticationAlgorithms
	 */
	public Set<IcsCa> getChipAuthenticationAlgorithms();

	/**
	 * Set the {@link Set} of Chip Authentication algorithms supported by the test candidate.
	 * 
	 * @param {@link
	 * 			Set<IcsCa>} chipAuthenticationAlgorithms
	 */
	public void setChipAuthenticationAlgorithms(Set<IcsCa> chipAuthenticationAlgorithms);

	/**
	 * Retrieve the {@link Set} of Chip Authentication domain parameters supported by the test candidate.
	 * 
	 * @return {@link
	 * 		Set<IcsCa>} chipAuthenticationAlgorithms
	 */
	public Set<IcsCaDomainparams> getChipAuthenticationDomainParameters();

	/**
	 * Set the {@link Set} of Chip Authentication algorithms supported by the test candidate.
	 * 
	 * @param {@link
	 * 			Set<IcsCa>} chipAuthenticationAlgorithms
	 */
	public void setChipAuthenticationDomainParameters(Set<IcsCaDomainparams> domainParameters);

	/**
	 * Retrieve the {@link Set} of XML content encryption algorithms supported by the test candidate.
	 * 
	 * @return {@link
	 * 		Set<IcsXmlsecEncryptionContentUri>} xmlEncryptionAlgorithms
	 */
	public Set<IcsXmlsecEncryptionContentUri> getXmlEncryptionAlgorithms();

	/**
	 * Set the {@link Set} of XML content encryption algorithms supported by the test candidate.
	 * 
	 * @param {@link
	 * 			Set<IcsXmlsecEncryptionContentUri>} xmlEncryptionAlgorithms
	 */
	public void setXmlEncryptionAlgorithms(Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithms);

	public Set<XmlSignature> getXmlSignatureAlgorithmsEid();

	public void setXmlSignatureAlgorithmsEid(Set<XmlSignature> xmlSignatureAlgorithmsEid);

	public Set<XmlSignature> getXmlSignatureAlgorithmsSaml();

	public void setXmlSignatureAlgorithmsSaml(Set<XmlSignature> xmlSignatureAlgorithmsSaml);

	public Set<TestCase> getTestCases();

	public void setTestCases(Set<TestCase> testCases);

	public Set<CertificateX509> getX509Certificates();

	public void setX509Certificates(Set<CertificateX509> x509Certificates);

	public Set<TestRun> getTestRuns();

	public void setTestRuns(Set<TestRun> logs);

	public Set<Report> getReports();

	public void setReports(Set<Report> reports);

	public Set<Tls> getTlsEcardApiPsk();

	public void setTlsEcardApiPsk(Set<Tls> tlsEcardApiPsk);

	public Set<Tls> getTlsEcardApiAttached();

	public void setTlsEcardApiAttached(Set<Tls> tlsEcardApiAttached);

	public Set<Tls> getTlsEidInterface();

	public void setTlsEidInterface(Set<Tls> tlsEidInterface);

	public Set<Tls> getTlsSaml();

	public void setTlsSaml(Set<Tls> tlsSaml);

	public Set<XmlEncryptionKeyAgreement> getXmlKeyAgreement();

	public void setXmlKeyAgreement(Set<XmlEncryptionKeyAgreement> xmlKeyAgreement);

	public Set<XmlEncryptionKeyTransport> getXmlKeyTransport();

	public void setXmlKeyTransport(Set<XmlEncryptionKeyTransport> xmlKeyTransport);

	/**
	 * Returns <i>true</i> if the server is capable to serve multiple clients without reconfiguration.
	 * 
	 * @return
	 */
	public boolean isMultiClientCapable();

	/**
	 * Sets whether the server is capable to serve multiple clients without reconfiguration.
	 * 
	 * @param multiClientCapable
	 *            {@link boolean}
	 */
	public void setMultiClientCapable(boolean multiClientCapable);
}
