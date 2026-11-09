package com.secunet.eidserver.testbed.controller;

import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.exceptions.CandidateException;
import com.secunet.eidserver.testbed.common.exceptions.CertificateGenerationExcpetion;
import com.secunet.eidserver.testbed.common.exceptions.ComponentNotInitializedException;
import com.secunet.eidserver.testbed.common.exceptions.TestcaseNotFoundException;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.*;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.*;
import com.secunet.eidserver.testbed.common.interfaces.entities.*;
import de.bsi.testbedutils.bouncycertgen.GeneratedCertificate;
import de.bsi.testbedutils.cvc.cvcertificate.CVCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Stateful
public class CandidateControllerBean implements CandidateController
{
	private static final Logger logger = LoggerFactory.getLogger(CandidateControllerBean.class);

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
	private DefaultTestCaseDAO defaultTestCaseDAO;

	@EJB
	private XmlSignatureAlgorithmDAO xmlSigDAO;

	@EJB
	private CertificateX509DAO x509DAO;

	@EJB
	private CertificateCVDAO cvDAO;

	@EJB
	private BlacklistDAO blacklistDAO;

	@EJB
	private CryptoFileDAO cryptoFileDAO;

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#createCandidate(String)
	 */
	@Asynchronous
	@Override
	public Future<TestCandidate> createCandidate(final String icsData) throws JAXBException, IOException, TransformerFactoryConfigurationError, TransformerException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException
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

		// Optional<String> base64EidasMetadata = runner.createEidasMetadata(testCandidate.getEcardapiUrl().toString());
		// if (base64EidasMetadata.isPresent())
		// {
		// testCandidate.setEidasMetadata(base64EidasMetadata.get());
		// }
		// else
		// {
		// logger.warn("Could not generate the eIDAS metadata XML.");
		// }
		return new AsyncResult<TestCandidate>(testCandidate);
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController#loadRelevantDefaultTestcases(TestCandidate)
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
				.filter(dtc -> candidate.getOptionalProfiles().containsAll(dtc.getOptionalProfiles())).filter(dtc -> !dtc.getVariables().containsKey("CA_ALGORITHM") || candidate.getChipAuthenticationAlgorithms().contains(IcsCa.fromValue(dtc.getVariables().get("CA_ALGORITHM")))).filter(dtc -> !dtc.getVariables().containsKey("CA_PARAMETERS") || candidate.getChipAuthenticationDomainParameters().contains(IcsCaDomainparams.fromValue(dtc.getVariables().get("CA_PARAMETERS")))).collect(Collectors.toSet());
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
		// eid interface
		if (null != ics.getCryptography().getEIDInterface())
		{
			IcsCryptoEidinterface eidChannel = ics.getCryptography().getEIDInterface();
			Set<XmlSignature> signatures = new HashSet<>();
			IcsXmlsignatures x = eidChannel.getXmlSignature();
			for (IcsXmlsignature s : x.getSignature())
			{
				signatures.add(createSignature(s));
			}
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
				testCandidate.setXmlEncryptionAlgorithmsSaml(contentEncryptionAlgorithms);

				Set<XmlEncryptionKeyAgreement> xmlEncryptionKeyAgreement = new HashSet<>();
				if (null != samlInterface.getXmlEncryption().getKeyAgreementAlgorithms())
				{
					for (IcsXmlencryptionKeyagreementAlgorithm kaa : samlInterface.getXmlEncryption().getKeyAgreementAlgorithms().getAgreementAlgorithm())
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
				testCandidate.setXmlKeyAgreementSaml(xmlEncryptionKeyAgreement);

				Set<XmlEncryptionKeyTransport> xmlEncryptionKeyTransport = new HashSet<>();
				if (null != samlInterface.getXmlEncryption().getKeyTransportAlgorithms())
				{
					for (IcsXmlencryptionKeytransportAlgorithm kta : samlInterface.getXmlEncryption().getKeyTransportAlgorithms().getTransportAlgorithm())
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
				testCandidate.setXmlKeyTransportSaml(xmlEncryptionKeyTransport);
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

		// eIDAS
		if (null != ics.getCryptography().getEIDAS())
		{
			IcsCryptoEidas eidasInterface = ics.getCryptography().getEIDAS();

			if (null != eidasInterface.getXmlSignature() && null != eidasInterface.getXmlSignature().getSignature())
			{
				Set<XmlSignature> signatures = new HashSet<>();
				for (IcsXmlsignature sig : eidasInterface.getXmlSignature().getSignature())
				{
					signatures.add(createSignature(sig));
				}
				testCandidate.setXmlSignatureAlgorithmsEidas(signatures);
			}
			else
			{
				logger.error("No information about the supported XML signature algorithms was provided for the eIDAS channel.");
			}

			if (null != eidasInterface.getXmlEncryption())
			{
				Set<IcsXmlsecEncryptionContentUri> contentEncryptionAlgorithms = new HashSet<>();
				if (null != eidasInterface.getXmlEncryption().getContentEncryptionAlgorithms())
				{
					contentEncryptionAlgorithms.addAll(eidasInterface.getXmlEncryption().getContentEncryptionAlgorithms().getURI());
				}
				else
				{
					logger.error("No information about the supported XML content enryption algorithms was provided for the eIDAS channel.");
				}
				testCandidate.setXmlEncryptionAlgorithmsEidas(contentEncryptionAlgorithms);

				Set<XmlEncryptionKeyAgreement> xmlEncryptionKeyAgreement = new HashSet<>();
				if (null != eidasInterface.getXmlEncryption().getKeyAgreementAlgorithms())
				{
					for (IcsXmlencryptionKeyagreementAlgorithm kaa : eidasInterface.getXmlEncryption().getKeyAgreementAlgorithms().getAgreementAlgorithm())
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
					logger.error("No information about the supported XML key agreement algorithms was provided for the eIDAS channel.");
				}
				testCandidate.setXmlKeyAgreementEidas(xmlEncryptionKeyAgreement);

				Set<XmlEncryptionKeyTransport> xmlEncryptionKeyTransport = new HashSet<>();
				if (null != eidasInterface.getXmlEncryption().getKeyTransportAlgorithms())
				{
					for (IcsXmlencryptionKeytransportAlgorithm kta : eidasInterface.getXmlEncryption().getKeyTransportAlgorithms().getTransportAlgorithm())
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
					logger.error("No information about the supported XML key transport algorithms was provided for the eIDAS channel.");
				}
				testCandidate.setXmlKeyTransportEidas(xmlEncryptionKeyTransport);
			}
			else
			{
				logger.error("No information about the supported XML encryption algorithms was provided for the eIDAS channel.");
			}
		}
		else
		{
			logger.warn("No cryptography information about the eIDAS interface communication has been provided in the ICS.");
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
			boolean exists = (testCandidateDAO.findById(testCandidate.getId()) != null);

			//TODO: Only generate certs on creation, not on update (maybe discuss)

			// load default testcases
			Set<TestCase> relevantTestcases = loadRelevantDefaultTestcases(testCandidate);
			testCandidate.setTestCases(relevantTestcases);

			// generate a new certificate set and persists it aswell
			certificateController.deleteCertificates(testCandidate);
			GeneratedCertsContainer generatedCertificates = certificateController.generateCertificates(testCandidate);

			Set<CertificateX509> certificates = new HashSet<>();
			for (GeneratedCertificate x509 : generatedCertificates.getCerts())
			{
				CertificateX509 x = x509DAO.createNew();
				x.setCertificateName(x509.getDefinition().fileName);
				x.setX509certificate(x509.getCertificate());
				x.setKeyPair(x509.getKeyPair());
				x509DAO.persist(x);
				certificates.add(x);
			}
			testCandidate.setX509Certificates(certificates);

			Set<CertificateCV> certificatesCV = new HashSet<>();
			for (CVCertificate cv : generatedCertificates.getCvCerts())
			{
				CertificateCV x = cvDAO.createNew();
				x.setCertificateName(cv.getCertHolderRef());
				x.setCvCertificate(cv);
				cvDAO.persist(x);
				certificatesCV.add(x);
			}
			testCandidate.setCvCertificates(certificatesCV);
			testCandidate.setMasterlist(generatedCertificates.getMasterList());
			testCandidate.setDefectlist(generatedCertificates.getDefectList());
			Set<Blacklist> blacklists = new HashSet<>();
			for (Map.Entry<Character, byte[]> entry : generatedCertificates.getBlackLists().entrySet())
			{
				Blacklist b = blacklistDAO.createNew();
				b.setContent(entry.getValue());
				b.setEserviceID(entry.getKey());
				blacklistDAO.persist(b);
				blacklists.add(b);
			}
			testCandidate.setBlacklists(blacklists);
			Set<CryptoFile> cryptoFiles = new HashSet<>();
			for (Map.Entry<String, byte[]> entry : generatedCertificates.getAllFiles().entrySet())
			{
				CryptoFile b = cryptoFileDAO.createNew();
				b.setContent(entry.getValue());
				b.setFilename(entry.getKey());
				cryptoFileDAO.persist(b);
				cryptoFiles.add(b);
			}
			testCandidate.setCryptoFiles(cryptoFiles);

			String metadata = new String(certificateController.getMetadataBytes(testCandidate), StandardCharsets.UTF_8);
			testCandidate.setEidasMetadata(metadata);
			CryptoHelper.writeCryptoFiles(testCandidate);



			// persist the candidate
			if (exists)
			{
				testCandidateDAO.update(testCandidate);
			}
			else
			{
				testCandidateDAO.persist(testCandidate);
			}
		} catch (CertificateGenerationExcpetion | IOException e) {
			logger.error("Could not persist candidate due to: {}", e.getMessage(), e);
			throw new CandidateException("Could not persist candidate due to an exception : " + e.getClass() + ", " +  e.getMessage());
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
