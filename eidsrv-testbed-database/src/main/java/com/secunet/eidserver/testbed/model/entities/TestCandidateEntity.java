package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	@JoinColumn(name = "TLS_ECARDAPI_PSK")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TlsEntity.class)
	private Set<Tls> tlsEcardApiPsk = new HashSet<>();

	@JoinColumn(name = "TLS_ECARDAPI_ATTACHED")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TlsEntity.class)
	private Set<Tls> tlsEcardApiAttached = new HashSet<>();

	@JoinColumn(name = "TLS_EIDINTERFACE")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TlsEntity.class)
	private Set<Tls> tlsEidInterface = new HashSet<>();

	@JoinColumn(name = "TLS_SAML")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TlsEntity.class)
	private Set<Tls> tlsSaml = new HashSet<>();

	private String vendor;

	@Column(name = "VERSION_MAJOR")
	private int versionMajor;

	@Column(name = "VERSION_MINOR")
	private int versionMinor;

	@Column(name = "VERSION_SUBMINOR")
	private int versionSubminor;

	@Column(name = "XML_CONTENT_ENCRYPTION_ALGORITHM")
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<IcsXmlsecEncryptionContentUri> xmlEncryptionAlgorithms = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyAgreementEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyAgreement", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "AGREEMENT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyAgreement> xmlKeyAgreement = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = XmlEncryptionKeyTransportEntity.class, cascade = CascadeType.ALL)
	@JoinTable(name = "Candidate_KeyTransport", joinColumns = { @JoinColumn(name = "CANDIDATE_ID", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "TRANSPORT_ID", referencedColumnName = "id") })
	private Set<XmlEncryptionKeyTransport> xmlKeyTransport = new HashSet<>();

	@JoinColumn(name = "XML_SIGNATURE_ALGORITHM_EID")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = XmlSignatureEntity.class, cascade = CascadeType.ALL)
	private Set<XmlSignature> xmlSignatureAlgorithmsEid = new HashSet<>();

	@JoinColumn(name = "XML_SIGNATURE_ALGORITHM_SAML")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = XmlSignatureEntity.class, cascade = CascadeType.ALL)
	private Set<XmlSignature> xmlSignatureAlgorithmsSaml = new HashSet<>();

	@JoinColumn(name = "X509_CERTIFICATE")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateX509Entity.class, cascade = CascadeType.ALL)
	private Set<CertificateX509> x509Certificates = new HashSet<>();

	@JoinColumn(name = "Report")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = ReportEntity.class)
	private Set<Report> reports = new HashSet<>();

	@Column(name = "MULTI_CLIENT_CAPABLE")
	private boolean multiClientCapable;

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
		result = prime * result + ((tlsEcardApiAttached == null) ? 0 : tlsEcardApiAttached.hashCode());
		result = prime * result + ((tlsEcardApiPsk == null) ? 0 : tlsEcardApiPsk.hashCode());
		result = prime * result + ((tlsEidInterface == null) ? 0 : tlsEidInterface.hashCode());
		result = prime * result + ((tlsSaml == null) ? 0 : tlsSaml.hashCode());
		result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
		result = prime * result + versionMajor;
		result = prime * result + versionMinor;
		result = prime * result + versionSubminor;
		result = prime * result + ((x509Certificates == null) ? 0 : x509Certificates.hashCode());
		result = prime * result + ((xmlEncryptionAlgorithms == null) ? 0 : xmlEncryptionAlgorithms.hashCode());
		result = prime * result + ((xmlKeyAgreement == null) ? 0 : xmlKeyAgreement.hashCode());
		result = prime * result + ((xmlKeyTransport == null) ? 0 : xmlKeyTransport.hashCode());
		result = prime * result + ((xmlSignatureAlgorithmsEid == null) ? 0 : xmlSignatureAlgorithmsEid.hashCode());
		result = prime * result + ((xmlSignatureAlgorithmsSaml == null) ? 0 : xmlSignatureAlgorithmsSaml.hashCode());
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
		if (tlsEcardApiAttached == null)
		{
			if (other.tlsEcardApiAttached != null)
				return false;
		}
		else if (!tlsEcardApiAttached.equals(other.tlsEcardApiAttached))
			return false;
		if (tlsEcardApiPsk == null)
		{
			if (other.tlsEcardApiPsk != null)
				return false;
		}
		else if (!tlsEcardApiPsk.equals(other.tlsEcardApiPsk))
			return false;
		if (tlsEidInterface == null)
		{
			if (other.tlsEidInterface != null)
				return false;
		}
		else if (!tlsEidInterface.equals(other.tlsEidInterface))
			return false;
		if (tlsSaml == null)
		{
			if (other.tlsSaml != null)
				return false;
		}
		else if (!tlsSaml.equals(other.tlsSaml))
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
		if (xmlEncryptionAlgorithms == null)
		{
			if (other.xmlEncryptionAlgorithms != null)
				return false;
		}
		else if (!xmlEncryptionAlgorithms.equals(other.xmlEncryptionAlgorithms))
			return false;
		if (xmlKeyAgreement == null)
		{
			if (other.xmlKeyAgreement != null)
				return false;
		}
		else if (!xmlKeyAgreement.equals(other.xmlKeyAgreement))
			return false;
		if (xmlKeyTransport == null)
		{
			if (other.xmlKeyTransport != null)
				return false;
		}
		else if (!xmlKeyTransport.equals(other.xmlKeyTransport))
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

}