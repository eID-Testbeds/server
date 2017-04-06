package com.secunet.eidserver.testbed.controller;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.Interface;
import com.secunet.eidserver.testbed.common.exceptions.CandidateException;
import com.secunet.eidserver.testbed.common.exceptions.ComponentNotInitializedException;
import com.secunet.eidserver.testbed.common.exceptions.TestcaseNotFoundException;
import com.secunet.eidserver.testbed.common.ics.Ics;
import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsCaDomainparams;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoEcardapi;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoEcardapiPsktls;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoEidinterface;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoEidinterfaceTlsClientcertificate;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoEidinterfaceTlsTypeEcAndSignatureWithClientcertificates;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoSaml;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoSamlXmlencryptionKeyagreementAlgorithm;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoSamlXmlencryptionKeytransportAlgorithm;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoTls;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoTlsEllipticcurves;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoTlsType;
import com.secunet.eidserver.testbed.common.ics.IcsCryptoTlsTypeEcAndSignature;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsignature;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsignatures;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TlsClientCertificateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TlsDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyAgreementDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyTransportDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlSignatureAlgorithmDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.controller.RunControllerBean.RunTask;

@Stateful
public class CandidateControllerBean implements CandidateController
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private TestGenerator generator;

	@EJB
	private CertificateController certificateController;

	@EJB
	private TestCandidateDAO testCandidateDAO;

	@EJB
	private XmlEncryptionKeyAgreementDAO xmlEncryptionKeyAgreementDAO;

	@EJB
	private XmlEncryptionKeyTransportDAO xmlEncryptionKeyTransportDAO;

	@EJB
	private TlsClientCertificateDAO tlsClientCertificateDAO;

	@EJB
	private DefaultTestCaseDAO defaultTestCaseDAO;

	@EJB
	private XmlSignatureAlgorithmDAO xmlSigDAO;

	@EJB
	private TlsDAO tlsDAO;

	@EJB
	private TestCaseStepDAO testCaseStepDAO;

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#createCandidate(String)
	 */
	@Asynchronous
	@Override
	public Future<TestCandidate> createCandidate(final String icsData) throws JAXBException, MalformedURLException
	{
		// strip CDATA
		String local = icsData;
		if (icsData.startsWith("<![CDATA["))
		{
			local = icsData.substring("<![CDATA[".length());
			local = local.substring(0, local.length() - ("]]>".length()));
		}
		// load ics
		JAXBContext jc = JAXBContext.newInstance("com.secunet.eidserver.testbed.common.ics");
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<Ics> jaxbIcs = (JAXBElement<Ics>) u.unmarshal(new StringReader(local));
		final Ics ics = jaxbIcs.getValue();
		final TestCandidate testCandidate = testCandidateDAO.createNew();
		// load ics data
		setApiInformationFromIcs(ics, testCandidate);
		setMetadataFromIcs(ics, testCandidate);
		setProfilesFromIcs(ics, testCandidate);
		setCryptographyFromIcs(ics, testCandidate);
		return new AsyncResult<TestCandidate>(testCandidate);
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#loadDefaultTestcases(TestCandidate)
	 */
	@Override
	public Set<TestCase> loadRelevantDefaultTestcases(TestCandidate candidate) throws ComponentNotInitializedException, TestcaseNotFoundException
	{
		// load all testcases
		Set<DefaultTestCase> defaultTestcases = null;
		try
		{
			@SuppressWarnings("unchecked")
			Collection<DefaultTestCase> dfltCollection = (Collection<DefaultTestCase>) defaultTestCaseDAO.findAll();
			defaultTestcases = new HashSet<DefaultTestCase>(dfltCollection);
		}
		catch (EntityNotFoundException e)
		{
			throw new TestcaseNotFoundException("Default test cases have not been loaded. Please generate these first.");
		}

		// filter for the given candidate (by profile)
		Set<TestCase> relevant = defaultTestcases.stream().filter(dtc -> candidate.getMandatoryProfiles().containsAll(dtc.getMandatoryProfiles()))
				.filter(dtc -> candidate.getOptionalProfiles().containsAll(dtc.getOptionalProfiles())).collect(Collectors.toSet());
		// also filter by other ics data (e.g. supported SAML encryption params)
		// XML encryption (SAML)

		for (TestCase testCase : relevant)
		{
			if (testCase.getVariables().containsKey(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER) && testCase.getVariables().containsKey(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER)
					&& candidate.getOptionalProfiles().contains(IcsOptionalprofile.SAML) && candidate.getXmlEncryptionAlgorithms() != null && candidate.getXmlKeyTransport() != null)
			{
				String blockCipher = testCase.getVariables().get(GeneralConstants.SAML_ENCRYPTION_BLOCK_CIPHER);
				IcsXmlsecEncryptionContentUri blockUri = IcsXmlsecEncryptionContentUri.fromValue(blockCipher);
				if (blockCipher != null && candidate.getXmlEncryptionAlgorithms().contains(blockUri))
				{
					String keyTransport = testCase.getVariables().get(GeneralConstants.SAML_ENCRYPTION_KEY_TRANSPORT);
					IcsXmlsecEncryptionKeyTransportUri transportUri = IcsXmlsecEncryptionKeyTransportUri.fromValue(keyTransport);
					if (transportUri != null)
					{
						for (XmlEncryptionKeyTransport transport : candidate.getXmlKeyTransport())
						{
							if (transport.getTransportAlgorithm().equals(transportUri))
							{
								RunTask xmlEncryptionTask = new RunTask(candidate, testCase, startDate);
								callables.add(xmlEncryptionTask);
								break;
							}
						}
					}
				}
			}
		}
		return relevant;
	}

	private void setApiInformationFromIcs(Ics ics, TestCandidate testCandidate)
	{
		if (null != ics.getAPI().getApiVersionMajor())
		{
			testCandidate.setApiMajor(ics.getAPI().getApiVersionMajor().intValue());
		}
		else
		{
			testCandidate.setApiMajor(0);
			logger.warn("No major API version was provided in the ICS.");
		}
		if (null != ics.getAPI().getApiVersionMinor())
		{
			testCandidate.setApiMinor(ics.getAPI().getApiVersionMinor().intValue());
		}
		if (null != ics.getAPI().getApiVersionSubminor())
		{
			testCandidate.setApiSubminor(ics.getAPI().getApiVersionSubminor().intValue());
		}
	}

	private void setMetadataFromIcs(final Ics ics, final TestCandidate testCandidate) throws MalformedURLException
	{
		if (null != ics.getMetadata().getVendor())
		{
			testCandidate.setVendor(ics.getMetadata().getVendor());
		}
		else
		{
			logger.warn("No name was provided in the ICS.");
		}
		if (null != ics.getMetadata().getName())
		{
			testCandidate.setCandidateName(ics.getMetadata().getName());
		}
		else
		{
			logger.warn("No name was provided in the ICS.");
		}
		// add urls. if the url is not parseable, throw an error later on
		if (null != ics.getMetadata().getECardApiUrl() && !ics.getMetadata().getECardApiUrl().isEmpty())
		{
			String url = ics.getMetadata().getECardApiUrl();
			if (!url.startsWith("https://"))
			{
				url = ("https://" + url);
			}
			testCandidate.setEcardapiUrl(new URL(url));
		}
		else
		{
			logger.warn("There was no URL provided for the server. You will be unable to run tests using the eCard-API-Interface.");
		}
		if (null != ics.getMetadata().getEIdInterfaceiUrl() && !ics.getMetadata().getEIdInterfaceiUrl().isEmpty())
		{
			String url = ics.getMetadata().getEIdInterfaceiUrl();
			if (!url.startsWith("https://"))
			{
				url = ("https://" + url);
			}
			testCandidate.setEidinterfaceUrl(new URL(url));
		}
		else
		{
			logger.warn("There was no URL provided for the server. You will be unable to run tests using the eID-Interface.");
		}
		if (null != ics.getMetadata().getSamlUrl() && !ics.getMetadata().getSamlUrl().isEmpty())
		{
			String url = ics.getMetadata().getSamlUrl();
			if (!url.startsWith("https://"))
			{
				url = ("https://" + url);
			}
			testCandidate.setSamlUrl(new URL(url));
		}
		else
		{
			logger.warn("There was no URL provided for the SAML processor. You will be unable to run SAML testcases.");
		}
		if (null != ics.getMetadata().getAttachedTcTokenUrl() && !ics.getMetadata().getAttachedTcTokenUrl().isEmpty())
		{
			String url = ics.getMetadata().getAttachedTcTokenUrl();
			if (!url.startsWith("https://"))
			{
				url = ("https://" + url);
			}
			testCandidate.setAttachedTcTokenUrl(new URL(url));
		}
		else
		{
			logger.warn("There was no URL provided for the TC Token retreival in attached mode. You will be unable to run testcases involving an attached eService.");
		}
		if (null != ics.getMetadata().getVersionMajor())
		{
			testCandidate.setVersionMajor(ics.getMetadata().getVersionMajor().intValue());
		}
		if (null != ics.getMetadata().getVersionMinor())
		{
			testCandidate.setVersionMinor(ics.getMetadata().getVersionMinor().intValue());
		}
		if (null != ics.getMetadata().getVersionSubminor())
		{
			testCandidate.setVersionSubminor(ics.getMetadata().getVersionSubminor().intValue());
		}
		// multi client capable server flag
		if (null == ics.getMetadata().isMultiClientCapable() || ics.getMetadata().isMultiClientCapable())
		{
			testCandidate.setMultiClientCapable(true);
		}
		else
		{
			testCandidate.setMultiClientCapable(false);
		}
	}

	private void setProfilesFromIcs(final Ics ics, TestCandidate testCandidate)
	{
		// init the profile lists
		Set<IcsMandatoryprofile> mProfiles = new HashSet<IcsMandatoryprofile>();
		Set<IcsOptionalprofile> oProfiles = new HashSet<IcsOptionalprofile>();
		if (null != ics.getProfiles().getMandatoryProfileOrOptionalProfile())
		{
			// assign the profiles
			for (Object profile : ics.getProfiles().getMandatoryProfileOrOptionalProfile())
			{
				if (profile instanceof IcsMandatoryprofile)
				{
					mProfiles.add((IcsMandatoryprofile) profile);
				}
				else
				{
					oProfiles.add((IcsOptionalprofile) profile);
				}
			}
		}
		else
		{
			logger.error("No Profiles have been provided.");
		}
		testCandidate.setMandatoryProfiles(mProfiles);
		testCandidate.setOptionalProfiles(oProfiles);
	}

	/**
	 * Returns a {@link Tls} with pre-filled fields for version and ciphersuites
	 * 
	 * @param c
	 * @return
	 */
	private Tls getTlsWithVersionAndCiphersuites(IcsCryptoTlsType c)
	{
		Tls t = tlsDAO.createNew();
		t.setTlsVersion(c.getVersion());
		t.setTlsCiphersuites(c.getCiphersuites().getCiphersuite());
		return t;
	}

	/**
	 * Set the fields for Elliptic Curves and Signature Algorithms for a given {@link Tls}
	 * 
	 * @param ec
	 * @param t
	 * @return
	 */
	private void setTlsWithCurvesAndSignatures(IcsCryptoTlsTypeEcAndSignature ec, Tls t)
	{
		Set<IcsEllipticcurve> curves = new HashSet<>();
		Set<IcsTlsSignaturealgorithms> signatureAlgorithms = new HashSet<>();
		for (Object es : ec.getEllipticCurvesOrSignatureAlgorithms())
		{
			if (es instanceof IcsCryptoTlsEllipticcurves)
			{
				IcsCryptoTlsEllipticcurves e = (IcsCryptoTlsEllipticcurves) es;
				curves.addAll(e.getNamedCurve());
			}
			else
			{
				IcsCryptoTlsSignaturealgorithms s = (IcsCryptoTlsSignaturealgorithms) es;
				signatureAlgorithms.addAll(s.getAlgorithm());
			}
		}
		t.setEllipticCurves(curves);
		t.setSignatureAlgorithms(signatureAlgorithms);
	}

	/**
	 * Generate a {@link XmlSignature} from the given {@link IcsXmlsignature}
	 * 
	 * @param s
	 * @return
	 */
	private XmlSignature createSignature(IcsXmlsignature s)
	{
		XmlSignature sig = xmlSigDAO.createNew();
		sig.setCanonicalizationMethod(s.getCanonicalization());
		sig.setDigestMethod(s.getDigest());
		sig.setSignatureAlgorithm(s.getURI());
		Set<IcsEllipticcurve> curves = new HashSet<>();
		Set<BigInteger> bitLengths = new HashSet<>();
		for (Object o : s.getParameters().getBitLengthOrNamedCurve())
		{
			if (o instanceof IcsEllipticcurve)
			{
				curves.add((IcsEllipticcurve) o);
			}
			else
			{
				bitLengths.add((BigInteger) o);
			}
		}
		if (curves.size() > 0)
		{
			sig.setEllipticCurves(curves);
		}
		else
		{
			sig.setBitLengths(bitLengths);
		}
		return sig;
	}

	private void setCryptographyFromIcs(final Ics ics, TestCandidate testCandidate)
	{
		if (null != ics.getCryptography().getECardApi())
		{
			IcsCryptoEcardapi eCardApi = ics.getCryptography().getECardApi();

			// PSK channel
			if (null != eCardApi.getPskChannel())
			{
				IcsCryptoEcardapiPsktls pskChannel = eCardApi.getPskChannel();
				Set<Tls> pskTls = new HashSet<>();
				for (IcsCryptoTlsType pskType : pskChannel.getTLS())
				{
					Tls t = getTlsWithVersionAndCiphersuites(pskType);
					t.setInterface(Interface.ECARDAPI_PSK);
					pskTls.add(t);
				}
				testCandidate.setTlsEcardApiPsk(pskTls);
			}
			else
			{
				logger.error("No cryptography information about the PSK channel has been provided in the ICS.");
			}

			// attached server
			if (null != eCardApi.getAttachedServer())
			{
				IcsCryptoTls attachedChannel = eCardApi.getAttachedServer();
				Set<Tls> attachedTls = new HashSet<>();
				for (IcsCryptoTlsTypeEcAndSignature attachedType : attachedChannel.getTLS())
				{
					Tls t = getTlsWithVersionAndCiphersuites(attachedType);
					setTlsWithCurvesAndSignatures(attachedType, t);
					t.setInterface(Interface.ECARDAPI_ATTACHED);
					attachedTls.add(t);
				}
				testCandidate.setTlsEcardApiAttached(attachedTls);
			}
			else
			{
				logger.warn("No cryptography information about the attached server mode has been provided in the ICS.");
			}
		}
		else
		{
			logger.error("No eCard-API data has been provided in the ICS.");
		}

		// eid interface
		if (null != ics.getCryptography().getEIDInterface())
		{
			IcsCryptoEidinterface eidChannel = ics.getCryptography().getEIDInterface();
			Set<Tls> eidTls = new HashSet<>();
			Set<XmlSignature> signatures = new HashSet<>();
			for (Object tx : eidChannel.getTLSOrXmlSignature())
			{
				if (tx instanceof IcsCryptoEidinterfaceTlsTypeEcAndSignatureWithClientcertificates)
				{
					IcsCryptoEidinterfaceTlsTypeEcAndSignatureWithClientcertificates t = (IcsCryptoEidinterfaceTlsTypeEcAndSignatureWithClientcertificates) tx;
					Tls tls = getTlsWithVersionAndCiphersuites(t);
					setTlsWithCurvesAndSignatures(t, tls);
					if (t.getClientCertificates() != null && t.getClientCertificates().getClientCertificate() != null)
					{
						Set<TlsClientCertificate> certificates = new HashSet<>();
						for (IcsCryptoEidinterfaceTlsClientcertificate cert : t.getClientCertificates().getClientCertificate())
						{
							TlsClientCertificate clientCert = tlsClientCertificateDAO.createNew();
							clientCert.setCertificateType(cert.getType());
							clientCert.setSignatureAlgorithm(cert.getSignatureAlgorithm());
							certificates.add(clientCert);
						}
						tls.setClientCertificates(certificates);
					}
					tls.setInterface(Interface.EIDINTERFACE);
					eidTls.add(tls);
				}
				else
				{
					IcsXmlsignatures x = (IcsXmlsignatures) tx;
					for (IcsXmlsignature s : x.getSignature())
					{
						signatures.add(createSignature(s));
					}
				}
			}
			testCandidate.setTlsEidInterface(eidTls);
			testCandidate.setXmlSignatureAlgorithmsEid(signatures);
		}
		else
		{
			logger.warn("No cryptography information about the eID-Interface communication has been provided in the ICS.");
		}

		// SAML
		if (null != ics.getCryptography().getSAML())
		{
			IcsCryptoSaml samlInterface = ics.getCryptography().getSAML();
			if (null != samlInterface.getTransportSecurity() && null != samlInterface.getTransportSecurity().getTLS())
			{
				Set<Tls> samlTls = new HashSet<>();
				for (IcsCryptoTlsTypeEcAndSignature tls : samlInterface.getTransportSecurity().getTLS())
				{
					Tls t = getTlsWithVersionAndCiphersuites(tls);
					setTlsWithCurvesAndSignatures(tls, t);
					t.setInterface(Interface.SAML);
					samlTls.add(t);
					testCandidate.setTlsSaml(samlTls);
				}
			}
			else
			{
				logger.error("No information about the supported TLS cryptography was provided for the SAML channel.");
			}

			if (null != samlInterface.getXmlSignature() && null != samlInterface.getXmlSignature().getSignature())
			{
				Set<XmlSignature> signatures = new HashSet<>();
				for (IcsXmlsignature sig : samlInterface.getXmlSignature().getSignature())
				{
					signatures.add(createSignature(sig));
				}
				testCandidate.setXmlSignatureAlgorithmsSaml(signatures);
			}
			else
			{
				logger.error("No information about the supported XML signature algorithms was provided for the SAML channel.");
			}

			if (null != samlInterface.getXmlEncryption())
			{
				Set<IcsXmlsecEncryptionContentUri> contentEncryptionAlgorithms = new HashSet<>();
				if (null != samlInterface.getXmlEncryption().getContentEncryptionAlgorithms())
				{
					contentEncryptionAlgorithms.addAll(samlInterface.getXmlEncryption().getContentEncryptionAlgorithms().getURI());
				}
				else
				{
					logger.error("No information about the supported XML content enryption algorithms was provided for the SAML channel.");
				}
				testCandidate.setXmlEncryptionAlgorithms(contentEncryptionAlgorithms);

				Set<XmlEncryptionKeyAgreement> xmlEncryptionKeyAgreement = new HashSet<>();
				if (null != samlInterface.getXmlEncryption().getKeyAgreementAlgorithms())
				{
					for (IcsCryptoSamlXmlencryptionKeyagreementAlgorithm kaa : samlInterface.getXmlEncryption().getKeyAgreementAlgorithms().getAgreementAlgorithm())
					{
						XmlEncryptionKeyAgreement agreement = xmlEncryptionKeyAgreementDAO.createNew();
						agreement.setKeyAgreementAlgorithm(kaa.getURI());
						agreement.setKeyWrappingAlgorithm(kaa.getKeyWrappingUri());
						Set<IcsEllipticcurve> curves = new HashSet<>();
						Set<BigInteger> bitLength = new HashSet<>();
						for (Object es : kaa.getParameters().getBitLengthOrNamedCurve())
						{
							if (es instanceof IcsEllipticcurve)
							{
								curves.add((IcsEllipticcurve) es);
							}
							else
							{
								bitLength.add((BigInteger) es);
							}
						}
						agreement.setBitLengths(bitLength);
						agreement.setEllipticCurves(curves);
						xmlEncryptionKeyAgreement.add(agreement);
					}
				}
				else
				{
					logger.error("No information about the supported XML key agreement algorithms was provided for the SAML channel.");
				}
				testCandidate.setXmlKeyAgreement(xmlEncryptionKeyAgreement);

				Set<XmlEncryptionKeyTransport> xmlEncryptionKeyTransport = new HashSet<>();
				if (null != samlInterface.getXmlEncryption().getKeyTransportAlgorithms())
				{
					for (IcsCryptoSamlXmlencryptionKeytransportAlgorithm kta : samlInterface.getXmlEncryption().getKeyTransportAlgorithms().getTransportAlgorithm())
					{
						XmlEncryptionKeyTransport transport = xmlEncryptionKeyTransportDAO.createNew();
						transport.setTransportAlgorithm(kta.getURI());
						Set<BigInteger> bitLength = new HashSet<>(kta.getParameter());
						transport.setBitLengths(bitLength);
						xmlEncryptionKeyTransport.add(transport);
					}
				}
				else
				{
					logger.error("No information about the supported XML key transport algorithms was provided for the SAML channel.");
				}
				testCandidate.setXmlKeyTransport(xmlEncryptionKeyTransport);
			}
			else
			{
				logger.error("No information about the supported XML encryption algorithms was provided for the SAML channel.");
			}
		}
		else
		{
			logger.warn("No cryptography information about the SAML interface communication has been provided in the ICS.");
		}

		// chip authentication
		if (null != ics.getCryptography().getChipAuthentication() && null != ics.getCryptography().getChipAuthentication().getAlgorithmOrDomainParameter())
		{
			Set<IcsCa> caSet = new HashSet<IcsCa>();
			Set<IcsCaDomainparams> domainSet = new HashSet<IcsCaDomainparams>();
			for (Object ca : ics.getCryptography().getChipAuthentication().getAlgorithmOrDomainParameter())
			{
				if (ca instanceof IcsCa)
				{
					caSet.add((IcsCa) ca);
				}
				else
				{
					domainSet.add((IcsCaDomainparams) ca);
				}
			}
			testCandidate.setChipAuthenticationAlgorithms(caSet);
			testCandidate.setChipAuthenticationDomainParameters(domainSet);
		}
		else
		{
			logger.error("No Chip Authentication algorithms or domain parameters have been provided.");
		}
	}


	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#saveCandidate(TestCandidate)
	 */
	@Override
	public void saveCandidate(TestCandidate testCandidate) throws CandidateException
	{
		try
		{
			boolean exists = ((testCandidateDAO.findById(testCandidate.getId()) == null) ? false : true);

			// load default testcases
			Set<TestCase> relevantTestcases = loadRelevantDefaultTestcases(testCandidate);
			testCandidate.setTestCases(relevantTestcases);

			// generate a new certificate set and persists it aswell
			certificateController.deleteCertificates(testCandidate);
			Set<CertificateX509> x509Certificates = certificateController.generateCertificates(testCandidate);
			testCandidate.setX509Certificates(x509Certificates);

			// persist the candidate
			if (exists)
			{
				testCandidateDAO.update(testCandidate);
			}
			else
			{
				testCandidateDAO.persist(testCandidate);
			}
		}
		catch (Exception e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw new CandidateException("Could not persist candidate due to: " + sw.toString());
		}
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#getCandidate(String)
	 */
	@Override
	public TestCandidate getCandidate(String uuid) throws EntityNotFoundException
	{
		return testCandidateDAO.findById(uuid);
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#getAllCandidates()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<TestCandidate> getAllCandidates()
	{
		return (Set<TestCandidate>) testCandidateDAO.findAll();
	};

	/**
	 * Delete the given test candidate
	 * 
	 * @param candidate
	 */
	@Override
	public void deleteCandidate(TestCandidate candidate)
	{
		testCandidateDAO.delete(candidate);
	}

	/**
	 * Delete a test profile given it's ID
	 * 
	 * @param id
	 */
	@Override
	public void deleteCandidateById(String id) throws CandidateException
	{
		TestCandidate candidate = testCandidateDAO.findById(id);
		if (candidate != null)
		{
			deleteCandidate(candidate);
		}
		else
		{
			throw new CandidateException("Candidate with ID " + id + " does not exist.");
		}
	}

	@Override
	public boolean isCandidateNameAlreadyTaken(String candidateName)
	{
		return testCandidateDAO.isCandidateNameAlreadyTaken(candidateName);
	}

}
