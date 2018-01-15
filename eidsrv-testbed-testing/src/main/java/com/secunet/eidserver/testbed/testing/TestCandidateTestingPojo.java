package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsCaDomainparams;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestRun;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;

public class TestCandidateTestingPojo extends BaseTestingPojo implements Serializable, TestCandidate
{
	private static final long serialVersionUID = 1L;

	private String profileName;

	private String candidateName;

	private int apiMajor;

	private int apiMinor;

	private int apiSubminor;

	private URL ecardapiUrl;

	private URL eidinterfaceUrl;

	private URL samlUrl;

	private URL attachedUrl;

	private Set<IcsCa> chipAuthenticationAlgorithms = new HashSet<>();

	private Set<IcsCaDomainparams> chipAuthenticationDomainParameters = new HashSet<>();

	private Set<IcsMandatoryprofile> mandatoryProfiles = new HashSet<>();

	private Set<IcsOptionalprofile> optionalProfiles = new HashSet<>();

	private Set<TestCase> testCases = new HashSet<>();

	private Set<TestRun> testRuns = new HashSet<>();

	private Set<Tls> tlsEcardApiPsk = new HashSet<>();

	private Set<Tls> tlsEcardApiAttached = new HashSet<>();

	private Set<Tls> tlsEidInterface = new HashSet<>();

	private Set<Tls> tlsSaml = new HashSet<>();

	private String vendor;

	private int versionMajor;

	private int versionMinor;

	private int versionSubminor;

	private Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithms = new HashSet<>();

	private Set<XmlEncryptionKeyAgreement> xmlKeyAgreement = new HashSet<>();

	private Set<XmlEncryptionKeyTransport> xmlKeyTransport = new HashSet<>();

	private Set<XmlSignature> xmlSignatureAlgorithmsEid = new HashSet<>();

	private Set<XmlSignature> xmlSignatureAlgorithmsSaml = new HashSet<>();

	private Set<CertificateX509> x509Certificates = new HashSet<>();

	private Set<Report> reports = new HashSet<>();

	private boolean multiClientCapable;

	private String eidasMetadata;

	public TestCandidateTestingPojo()
	{
	}

	@Override
	public String getVendor()
	{
		return this.vendor;
	}

	@Override
	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	@Override
	public int getVersionMajor()
	{
		return this.versionMajor;
	}

	@Override
	public void setVersionMajor(int versionMajor)
	{
		this.versionMajor = versionMajor;
	}

	@Override
	public int getVersionMinor()
	{
		return this.versionMinor;
	}

	@Override
	public void setVersionMinor(int versionMinor)
	{
		this.versionMinor = versionMinor;
	}

	@Override
	public int getVersionSubminor()
	{
		return this.versionSubminor;
	}

	@Override
	public void setVersionSubminor(int versionSubminor)
	{
		this.versionSubminor = versionSubminor;
	}

	@Override
	public int getApiMajor()
	{
		return this.apiMajor;
	}

	@Override
	public void setApiMajor(int apiMajor)
	{
		this.apiMajor = apiMajor;
	}

	@Override
	public int getApiMinor()
	{
		return this.apiMinor;
	}

	@Override
	public void setApiMinor(int apiMinor)
	{
		this.apiMinor = apiMinor;
	}

	@Override
	public int getApiSubminor()
	{
		return this.apiSubminor;
	}

	@Override
	public void setApiSubminor(int apiSubminor)
	{
		this.apiSubminor = apiSubminor;
	}

	@Override
	public URL getSamlUrl()
	{
		return this.samlUrl;
	}

	@Override
	public void setSamlUrl(URL samlUrl)
	{
		this.samlUrl = samlUrl;
	}

	@Override
	public Set<IcsOptionalprofile> getOptionalProfiles()
	{
		return optionalProfiles;
	}

	@Override
	public void setOptionalProfiles(Set<IcsOptionalprofile> optionalProfiles)
	{
		this.optionalProfiles = optionalProfiles;
	}

	@Override
	public Set<IcsMandatoryprofile> getMandatoryProfiles()
	{
		return mandatoryProfiles;
	}

	@Override
	public void setMandatoryProfiles(Set<IcsMandatoryprofile> mandatoryProfiles)
	{
		this.mandatoryProfiles = mandatoryProfiles;
	}

	@Override
	public Set<IcsCa> getChipAuthenticationAlgorithms()
	{
		return chipAuthenticationAlgorithms;
	}

	@Override
	public void setChipAuthenticationAlgorithms(Set<IcsCa> chipAuthenticationAlgorithms)
	{
		this.chipAuthenticationAlgorithms = chipAuthenticationAlgorithms;
	}

	@Override
	public Set<IcsXmlsecEncryptionContentUri> getXmlEncryptionAlgorithms()
	{
		return xmlEncryptionAlgorithms;
	}

	@Override
	public void setXmlEncryptionAlgorithms(Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithms)
	{
		this.xmlEncryptionAlgorithms = xmlEncryptionAlgorithms;
	}

	@Override
	public Set<XmlSignature> getXmlSignatureAlgorithmsEid()
	{
		return xmlSignatureAlgorithmsEid;
	}

	@Override
	public void setXmlSignatureAlgorithmsEid(Set<XmlSignature> xmlSignatureAlgorithmsEid)
	{
		this.xmlSignatureAlgorithmsEid = xmlSignatureAlgorithmsEid;
	}

	@Override
	public Set<XmlSignature> getXmlSignatureAlgorithmsSaml()
	{
		return xmlSignatureAlgorithmsSaml;
	}

	@Override
	public void setXmlSignatureAlgorithmsSaml(Set<XmlSignature> xmlSignatureAlgorithmsSaml)
	{
		this.xmlSignatureAlgorithmsSaml = xmlSignatureAlgorithmsSaml;
	}

	@Override
	public Set<TestCase> getTestCases()
	{
		return testCases;
	}

	@Override
	public void setTestCases(Set<TestCase> testCases)
	{
		this.testCases = testCases;
	}

	@Override
	public Set<CertificateX509> getX509Certificates()
	{
		return x509Certificates;
	}

	@Override
	public void setX509Certificates(Set<CertificateX509> x509Certificates)
	{
		this.x509Certificates = x509Certificates;
	}

	@Override
	public Set<TestRun> getTestRuns()
	{
		return testRuns;
	}

	@Override
	public void setTestRuns(Set<TestRun> testRuns)
	{
		this.testRuns = testRuns;
	}

	@Override
	public Set<Report> getReports()
	{
		return reports;
	}

	@Override
	public void setReports(Set<Report> reports)
	{
		this.reports = reports;
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate#getCandidateName()
	 */
	@Override
	public String getCandidateName()
	{
		return candidateName;
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate#setCandidateName()
	 */
	@Override
	public void setCandidateName(String candidateName)
	{
		this.candidateName = candidateName;
	}

	@Override
	public Set<Tls> getTlsEcardApiPsk()
	{
		return tlsEcardApiPsk;
	}

	@Override
	public void setTlsEcardApiPsk(Set<Tls> tlsEcardApiPsk)
	{
		this.tlsEcardApiPsk = tlsEcardApiPsk;
	}

	@Override
	public Set<Tls> getTlsEcardApiAttached()
	{
		return tlsEcardApiAttached;
	}

	@Override
	public void setTlsEcardApiAttached(Set<Tls> tlsEcardApiAttached)
	{
		this.tlsEcardApiAttached = tlsEcardApiAttached;
	}

	@Override
	public Set<Tls> getTlsEidInterface()
	{
		return tlsEidInterface;
	}

	@Override
	public void setTlsEidInterface(Set<Tls> tlsEidInterface)
	{
		this.tlsEidInterface = tlsEidInterface;
	}

	@Override
	public Set<Tls> getTlsSaml()
	{
		return tlsSaml;
	}

	@Override
	public void setTlsSaml(Set<Tls> tlsSaml)
	{
		this.tlsSaml = tlsSaml;
	}

	@Override
	public Set<XmlEncryptionKeyAgreement> getXmlKeyAgreement()
	{
		return xmlKeyAgreement;
	}

	@Override
	public void setXmlKeyAgreement(Set<XmlEncryptionKeyAgreement> xmlKeyAgreement)
	{
		this.xmlKeyAgreement = xmlKeyAgreement;
	}

	@Override
	public Set<XmlEncryptionKeyTransport> getXmlKeyTransport()
	{
		return xmlKeyTransport;
	}

	@Override
	public void setXmlKeyTransport(Set<XmlEncryptionKeyTransport> xmlKeyTransport)
	{
		this.xmlKeyTransport = xmlKeyTransport;
	}

	@Override
	public Set<IcsCaDomainparams> getChipAuthenticationDomainParameters()
	{
		return chipAuthenticationDomainParameters;
	}

	@Override
	public void setChipAuthenticationDomainParameters(Set<IcsCaDomainparams> chipAuthenticationDomainParameters)
	{
		this.chipAuthenticationDomainParameters = chipAuthenticationDomainParameters;
	}

	/**
	 * @return the profileName
	 */
	@Override
	public String getProfileName()
	{
		return profileName;
	}

	/**
	 * @param profileName
	 *            the profileName to set
	 */
	@Override
	public void setProfileName(String profileName)
	{
		this.profileName = profileName;
	}

	@Override
	public URL getEcardapiUrl()
	{
		return ecardapiUrl;
	}

	@Override
	public void setEcardapiUrl(URL eCardApiUrl)
	{
		this.ecardapiUrl = eCardApiUrl;
	}

	@Override
	public URL getEidinterfaceUrl()
	{
		return eidinterfaceUrl;
	}

	@Override
	public void setEidinterfaceUrl(URL eidinterfaceUrl)
	{
		this.eidinterfaceUrl = eidinterfaceUrl;
	}

	@Override
	public URL getAttachedTcTokenUrl()
	{
		return attachedUrl;
	}

	@Override
	public void setAttachedTcTokenUrl(URL attachedUrl)
	{
		this.attachedUrl = attachedUrl;
	}

	@Override
	public boolean isMultiClientCapable()
	{
		return this.multiClientCapable;
	}

	@Override
	public void setMultiClientCapable(boolean multiClientCapable)
	{
		this.multiClientCapable = multiClientCapable;
	}

	@Override
	public void setEidasMetadata(String eidasMetadataXml)
	{
		this.eidasMetadata = eidasMetadataXml;
	}

	@Override
	public String getEidasMetadata()
	{
		return this.eidasMetadata;
	}

}