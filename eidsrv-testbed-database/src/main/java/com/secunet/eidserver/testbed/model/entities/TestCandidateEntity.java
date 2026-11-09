package com.secunet.eidserver.testbed.model.entities;

import com.secunet.eidserver.testbed.common.ics.*;
import com.secunet.eidserver.testbed.common.interfaces.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TEST_CANDIDATE")
@NamedQueries({ @NamedQuery(name = "TestCandidateEntity.findAll", query = "SELECT t FROM TestCandidateEntity t"),
		@NamedQuery(name = "TestCandidateEntity.exists", query = "SELECT COUNT(1) FROM TestCandidateEntity x WHERE x.profileName LIKE :name") })
public class TestCandidateEntity extends BaseEntity implements Serializable, TestCandidate
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getRootLogger();

	@Column(name = "PROFILE_NAME", unique = true)
	private String profileName;

	@Column(name = "CANDIDATE_NAME")
	private String candidateName;

	@Column(name = "API_MAJOR")
	private int apiMajor;

	@Column(name = "API_MINOR")
	private int apiMinor;

	@Column(name = "API_SUBMINOR")
	private int apiSubminor;

	@Column(name = "ECARD_API_URL")
	private String ecardapiUrl;

	@Column(name = "EIDINTERFACE_URL")
	private String eidinterfaceUrl;

	@Column(name = "SAML_PROCESSOR_URL")
	private String samlUrl;

	@Column(name = "ATTACHED_TCTOKEN_URL")
	private String attachedTcTokenUrl;

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@Column(name = "CHIP_AUTHENTICATION_ALGORITHM")
	private Set<IcsCa> chipAuthenticationAlgorithms = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@Column(name = "CHIP_AUTHENTICATION_DOMAINPARAMS")
	private Set<IcsCaDomainparams> chipAuthenticationDomainParameters = new HashSet<>();

	@Column(name = "MANDATORY_PROFILE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsMandatoryprofile> mandatoryProfiles = new HashSet<>();

	@Column(name = "OPTIONAL_PROFILE")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsOptionalprofile> optionalProfiles = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = BaseTestCaseEntity.class)
	@JoinTable(name = "Candidate_Testcase", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "TESTCASE_ID", referencedColumnName = "id") })
	private Set<TestCase> testCases = new HashSet<>();

	@JoinColumn(name = "TEST_RUNS")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = TestRunEntity.class)
	private Set<TestRun> testRuns = new HashSet<>();

	private String vendor;

	@Column(name = "VERSION_MAJOR")
	private int versionMajor;

	@Column(name = "VERSION_MINOR")
	private int versionMinor;

	@Column(name = "VERSION_SUBMINOR")
	private int versionSubminor;

	@Column(name = "XML_CONTENT_ENCRYPTION_ALGORITHM_SAML")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithmsSaml = new HashSet<>();

	@Column(name = "XML_CONTENT_ENCRYPTION_ALGORITHM_EIDAS")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithmsEidas = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyAgreementEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyAgreementSaml", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "AGREEMENT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyAgreement> xmlKeyAgreementSaml = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyAgreementEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyAgreementEidas", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "AGREEMENT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyAgreement> xmlKeyAgreementEidas = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyTransportEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyTransportSaml", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "TRANSPORT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyTransport> xmlKeyTransportSaml = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyTransportEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyTransportEidas", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "TRANSPORT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyTransport> xmlKeyTransportEidas = new HashSet<>();

	@JoinColumn(name = "XML_SIGNATURE_ALGORITHM_EID")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = XmlSignatureEntity.class, cascade = CascadeType.ALL)
	private Set<XmlSignature> xmlSignatureAlgorithmsEid = new HashSet<>();

	@JoinColumn(name = "XML_SIGNATURE_ALGORITHM_SAML")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = XmlSignatureEntity.class, cascade = CascadeType.ALL)
	private Set<XmlSignature> xmlSignatureAlgorithmsSaml = new HashSet<>();

	@JoinColumn(name = "XML_SIGNATURE_ALGORITHM_EIDAS")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = XmlSignatureEntity.class, cascade = CascadeType.ALL)
	private Set<XmlSignature> xmlSignatureAlgorithmsEidas = new HashSet<>();

	@JoinColumn(name = "X509_CERTIFICATE")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateX509Entity.class, cascade = CascadeType.ALL)
	private Set<CertificateX509> x509Certificates = new HashSet<>();

	@JoinColumn(name = "CV_CERTIFICATE")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateCVEntity.class, cascade = CascadeType.ALL)
	private Set<CertificateCV> cvCertificates = new HashSet<>();

	@JoinColumn(name = "BLACKLIST_LNK")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = BlacklistEntity.class, cascade = CascadeType.ALL)
	private Set<Blacklist> blacklists = new HashSet<>();

	@JoinColumn(name = "CRYPTO_FILE")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CryptoFileEntity.class, cascade = CascadeType.ALL)
	private Set<CryptoFile> cryptoFiles = new HashSet<>();

	@JoinColumn(name = "Report")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = ReportEntity.class)
	private Set<Report> reports = new HashSet<>();

	@Column(name = "MULTI_CLIENT_CAPABLE")
	private boolean multiClientCapable;

	@Column(name = "EIDAS_METADATA")
	private String eidasMetadata;

	@Lob
	@Column(length = 500000)
	private byte[] masterList;

	@Lob
	@Column(length = 500000)
	private byte[] defectlist;

	//TODO: Add storage for certificates

	public TestCandidateEntity()
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
		try
		{
			return new URL(this.samlUrl);
		}
		catch (MalformedURLException e)
		{
			logger.warn("Could not read SAML URL: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setSamlUrl(URL samlUrl)
	{
		if (samlUrl != null)
			this.samlUrl = samlUrl.toString();
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

	public Set<IcsXmlsecEncryptionContentUri> getXmlEncryptionAlgorithmsSaml()
	{
		return xmlEncryptionAlgorithmsSaml;
	}

	@Override
	public void setXmlEncryptionAlgorithmsSaml(Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithmsSaml)
	{
		this.xmlEncryptionAlgorithmsSaml = xmlEncryptionAlgorithmsSaml;
	}

	public Set<IcsXmlsecEncryptionContentUri> getXmlEncryptionAlgorithmsEidas()
	{
		return xmlEncryptionAlgorithmsEidas;
	}

	@Override
	public void setXmlEncryptionAlgorithmsEidas(Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithmsEidas)
	{
		this.xmlEncryptionAlgorithmsEidas = xmlEncryptionAlgorithmsEidas;
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
	public Set<XmlSignature> getXmlSignatureAlgorithmsEidas()
	{
		return xmlSignatureAlgorithmsEidas;
	}

	@Override
	public void setXmlSignatureAlgorithmsEidas(Set<XmlSignature> xmlSignatureAlgorithmsEidas)
	{
		this.xmlSignatureAlgorithmsEidas = xmlSignatureAlgorithmsEidas;
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
	public Set<CertificateCV> getCvCertificates()
	{
		return cvCertificates;
	}

	@Override
	public void setCvCertificates(Set<CertificateCV> cvCertificates)
	{
		this.cvCertificates = cvCertificates;
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
	 * @see com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate#setCandidateName(String)
	 */
	@Override
	public void setCandidateName(String candidateName)
	{
		this.candidateName = candidateName;
	}

	@Override
	public Set<XmlEncryptionKeyAgreement> getXmlKeyAgreementSaml()
	{
		return xmlKeyAgreementSaml;
	}

	@Override
	public void setXmlKeyAgreementSaml(Set<XmlEncryptionKeyAgreement> xmlKeyAgreement)
	{
		this.xmlKeyAgreementSaml = xmlKeyAgreement;
	}

	@Override
	public Set<XmlEncryptionKeyAgreement> getXmlKeyAgreementEidas()
	{
		return xmlKeyAgreementEidas;
	}

	@Override
	public void setXmlKeyAgreementEidas(Set<XmlEncryptionKeyAgreement> xmlKeyAgreementEidas)
	{
		this.xmlKeyAgreementEidas = xmlKeyAgreementEidas;
	}

	@Override
	public Set<XmlEncryptionKeyTransport> getXmlKeyTransportSaml()
	{
		return xmlKeyTransportSaml;
	}

	@Override
	public void setXmlKeyTransportSaml(Set<XmlEncryptionKeyTransport> xmlKeyTransport)
	{
		this.xmlKeyTransportSaml = xmlKeyTransport;
	}

	@Override
	public Set<XmlEncryptionKeyTransport> getXmlKeyTransportEidas()
	{
		return xmlKeyTransportEidas;
	}

	@Override
	public void setXmlKeyTransportEidas(Set<XmlEncryptionKeyTransport> xmlKeyTransportEidas)
	{
		this.xmlKeyTransportEidas = xmlKeyTransportEidas;
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
		try
		{
			return new URL(this.ecardapiUrl);
		}
		catch (MalformedURLException e)
		{
			logger.warn("Could not read eCard-API URL: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setEcardapiUrl(URL ecardapiUrl)
	{
		if (ecardapiUrl != null)
			this.ecardapiUrl = ecardapiUrl.toString();
	}

	@Override
	public URL getEidinterfaceUrl()
	{
		try
		{
			return new URL(this.eidinterfaceUrl);
		}
		catch (MalformedURLException e)
		{
			logger.warn("Could not read eID-Interface-API URL: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setEidinterfaceUrl(URL eidinterfaceUrl)
	{
		if (eidinterfaceUrl != null)
			this.eidinterfaceUrl = eidinterfaceUrl.toString();
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
		int result = prime + apiMajor;
		result = prime * result + apiMinor;
		result = prime * result + apiSubminor;
		result = prime * result + ((candidateName == null) ? 0 : candidateName.hashCode());
		result = prime * result + ((chipAuthenticationAlgorithms == null) ? 0 : chipAuthenticationAlgorithms.hashCode());
		result = prime * result + ((chipAuthenticationDomainParameters == null) ? 0 : chipAuthenticationDomainParameters.hashCode());
		result = prime * result + ((ecardapiUrl == null) ? 0 : ecardapiUrl.hashCode());
		result = prime * result + ((eidinterfaceUrl == null) ? 0 : eidinterfaceUrl.hashCode());
		result = prime * result + ((testRuns == null) ? 0 : testRuns.hashCode());
		result = prime * result + ((mandatoryProfiles == null) ? 0 : mandatoryProfiles.hashCode());
		result = prime * result + ((optionalProfiles == null) ? 0 : optionalProfiles.hashCode());
		result = prime * result + ((profileName == null) ? 0 : profileName.hashCode());
		result = prime * result + ((reports == null) ? 0 : reports.hashCode());
		result = prime * result + ((samlUrl == null) ? 0 : samlUrl.hashCode());
		result = prime * result + ((testCases == null) ? 0 : testCases.hashCode());
		result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
		result = prime * result + versionMajor;
		result = prime * result + versionMinor;
		result = prime * result + versionSubminor;
		result = prime * result + ((x509Certificates == null) ? 0 : x509Certificates.hashCode());
		result = prime * result + ((xmlEncryptionAlgorithmsSaml == null) ? 0 : xmlEncryptionAlgorithmsSaml.hashCode());
		result = prime * result + ((xmlKeyAgreementSaml == null) ? 0 : xmlKeyAgreementSaml.hashCode());
		result = prime * result + ((xmlKeyTransportSaml == null) ? 0 : xmlKeyTransportSaml.hashCode());
		result = prime * result + ((xmlSignatureAlgorithmsEid == null) ? 0 : xmlSignatureAlgorithmsEid.hashCode());
		result = prime * result + ((xmlSignatureAlgorithmsSaml == null) ? 0 : xmlSignatureAlgorithmsSaml.hashCode());
		result = prime * result + ((blacklists == null) ? 0 : blacklists.hashCode());
		result = prime * result + ((cvCertificates == null) ? 0 : cvCertificates.hashCode());
		result = prime * result + ((cryptoFiles == null) ? 0 : cryptoFiles.hashCode());
		result = prime * result + ((masterList == null) ? 0 : Arrays.hashCode(masterList));
		result = prime * result + ((defectlist == null) ? 0 : Arrays.hashCode(defectlist));
		return result;
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
		TestCandidateEntity other = (TestCandidateEntity) obj;
		if (apiMajor != other.apiMajor)
			return false;
		if (apiMinor != other.apiMinor)
			return false;
		if (apiSubminor != other.apiSubminor)
			return false;
		if (candidateName == null)
		{
			if (other.candidateName != null)
				return false;
		}
		else if (!candidateName.equals(other.candidateName))
			return false;
		if (chipAuthenticationAlgorithms == null)
		{
			if (other.chipAuthenticationAlgorithms != null)
				return false;
		}
		else if (!chipAuthenticationAlgorithms.equals(other.chipAuthenticationAlgorithms))
			return false;
		if (chipAuthenticationDomainParameters == null)
		{
			if (other.chipAuthenticationDomainParameters != null)
				return false;
		}
		else if (!chipAuthenticationDomainParameters.equals(other.chipAuthenticationDomainParameters))
			return false;
		if (ecardapiUrl == null)
		{
			if (other.ecardapiUrl != null)
				return false;
		}
		else if (!ecardapiUrl.equals(other.ecardapiUrl))
			return false;
		if (eidinterfaceUrl == null)
		{
			if (other.eidinterfaceUrl != null)
				return false;
		}
		else if (!eidinterfaceUrl.equals(other.eidinterfaceUrl))
			return false;
		if (testRuns == null)
		{
			if (other.testRuns != null)
				return false;
		}
		else if (!testRuns.equals(other.testRuns))
			return false;
		if (mandatoryProfiles == null)
		{
			if (other.mandatoryProfiles != null)
				return false;
		}
		else if (!mandatoryProfiles.equals(other.mandatoryProfiles))
			return false;
		if (optionalProfiles == null)
		{
			if (other.optionalProfiles != null)
				return false;
		}
		else if (!optionalProfiles.equals(other.optionalProfiles))
			return false;
		if (profileName == null)
		{
			if (other.profileName != null)
				return false;
		}
		else if (!profileName.equals(other.profileName))
			return false;
		if (reports == null)
		{
			if (other.reports != null)
				return false;
		}
		else if (!reports.equals(other.reports))
			return false;
		if (samlUrl == null)
		{
			if (other.samlUrl != null)
				return false;
		}
		else if (!samlUrl.equals(other.samlUrl))
			return false;
		if (testCases == null)
		{
			if (other.testCases != null)
				return false;
		}
		else if (!testCases.equals(other.testCases))
			return false;
		if (vendor == null)
		{
			if (other.vendor != null)
				return false;
		}
		else if (!vendor.equals(other.vendor))
			return false;
		if (versionMajor != other.versionMajor)
			return false;
		if (versionMinor != other.versionMinor)
			return false;
		if (versionSubminor != other.versionSubminor)
			return false;
		if (x509Certificates == null)
		{
			if (other.x509Certificates != null)
				return false;
		}
		else if (!x509Certificates.equals(other.x509Certificates))
			return false;
		if (xmlEncryptionAlgorithmsSaml == null)
		{
			if (other.xmlEncryptionAlgorithmsSaml != null)
				return false;
		}
		else if (!xmlEncryptionAlgorithmsSaml.equals(other.xmlEncryptionAlgorithmsSaml))
			return false;
		if (xmlKeyAgreementSaml == null)
		{
			if (other.xmlKeyAgreementSaml != null)
				return false;
		}
		else if (!xmlKeyAgreementSaml.equals(other.xmlKeyAgreementSaml))
			return false;
		if (xmlKeyTransportSaml == null)
		{
			if (other.xmlKeyTransportSaml != null)
				return false;
		}
		else if (!xmlKeyTransportSaml.equals(other.xmlKeyTransportSaml))
			return false;
		if (xmlSignatureAlgorithmsEid == null)
		{
			if (other.xmlSignatureAlgorithmsEid != null)
				return false;
		}
		else if (!xmlSignatureAlgorithmsEid.equals(other.xmlSignatureAlgorithmsEid))
			return false;
		if (xmlSignatureAlgorithmsSaml == null)
		{
			if (other.xmlSignatureAlgorithmsSaml != null)
				return false;
		}
		else if (!xmlSignatureAlgorithmsSaml.equals(other.xmlSignatureAlgorithmsSaml))
			return false;
		if (blacklists == null)
		{
			if (other.blacklists != null)
				return false;
		}
		else if (!blacklists.equals(other.blacklists))
			return false;
		if (cvCertificates == null)
		{
			if (other.cvCertificates != null)
				return false;
		}
		else if (!cvCertificates.equals(other.cvCertificates))
			return false;
		if (cryptoFiles == null)
		{
			if (other.cryptoFiles != null)
				return false;
		}
		else if (!cryptoFiles.equals(other.cryptoFiles))
			return false;
		if (!Arrays.equals(masterList, other.masterList))
			return false;
		if (!Arrays.equals(defectlist, other.defectlist))
			return false;
		return true;
	}

	@Override
	public URL getAttachedTcTokenUrl()
	{
		try
		{
			return new URL(this.attachedTcTokenUrl);
		}
		catch (MalformedURLException e)
		{
			logger.warn("Could not read attached URL: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setAttachedTcTokenUrl(URL attachedTcTokenUrl)
	{
		if (attachedTcTokenUrl != null)
			this.attachedTcTokenUrl = attachedTcTokenUrl.toString();
	}

	/**
	 * @return the multiClientCapable
	 */
	@Override
	public boolean isMultiClientCapable()
	{
		return multiClientCapable;
	}

	/**
	 * @param multiClientCapable
	 *            the multiClientCapable to set
	 */
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

	@Override
	public Set<Blacklist> getBlacklists() {
		return blacklists;
	}

	@Override
	public void setBlacklists(Set<Blacklist> blacklists) {
		this.blacklists = blacklists;
	}

	@Override
	public Set<CryptoFile> getCryptoFiles() {
		return cryptoFiles;
	}

	@Override
	public void setCryptoFiles(Set<CryptoFile> cryptoFiles) {
		this.cryptoFiles = cryptoFiles;
	}

	public byte[] getMasterlist() {
		return masterList;
	}

	public void setMasterlist(byte[] masterList) {
		this.masterList = masterList;
	}

	@Override
	public byte[] getDefectlist() {
		return defectlist;
	}

	@Override
	public void setDefectlist(byte[] defectlist) {
		this.defectlist = defectlist;
	}
}
