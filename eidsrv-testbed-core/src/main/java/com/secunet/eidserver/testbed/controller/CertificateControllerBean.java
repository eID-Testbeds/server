package com.secunet.eidserver.testbed.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.exceptions.CertificateGenerationExcpetion;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.Runner;
import com.secunet.eidserver.testbed.common.interfaces.dao.CertificateX509DAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.testbedutils.bouncycertgen.BouncyCertificateGenerator;
import com.secunet.testbedutils.bouncycertgen.CertificateGenerator;
import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.xml.x509.AlgorithmType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.AlgorithmTypeEC;
import com.secunet.testbedutils.bouncycertgen.xml.x509.AltNameType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinition;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinitions;
import com.secunet.testbedutils.bouncycertgen.xml.x509.DnType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.ExtendedKeyUsageType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.ExtensionsType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.GeneralNameTypeType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.GeneralNamesType.GeneralName;
import com.secunet.testbedutils.bouncycertgen.xml.x509.KeyUsageType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.SignatureAlgorithmType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.SignatureAlgorithmTypeDSA;
import com.secunet.testbedutils.bouncycertgen.xml.x509.SignatureAlgorithmTypeECDSA;
import com.secunet.testbedutils.bouncycertgen.xml.x509.SignatureAlgorithmTypeRSA;

@Stateless
public class CertificateControllerBean implements CertificateController
{
	private static final Logger logger = LogManager.getRootLogger();

	public static final int RSA = 1 << 1;
	public static final int ECDSA = 1 << 2;
	public static final int DSS = 1 << 3;

	@EJB
	private TestCandidateDAO testCandidateDAO;

	@EJB
	private CertificateX509DAO x509DAO;

	@EJB
	private Runner runnerBean;

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController#uploadX509(String, String, String)
	 */
	@Override
	public String uploadX509(String profileId, String name, String base64data)
	{
		TestCandidate candidate = testCandidateDAO.findById(profileId);
		try
		{
			X509Certificate x509cert = CertificateGenerator.x509FromBase64String(base64data);
			CertificateX509 cert = x509DAO.findByName(name + candidate.getCandidateName());
			// check if a certificate with the given name already exists for the
			// profile
			if (cert != null)
			{
				cert.setX509certificate(x509cert);
			}
			else
			{
				// it did not exist, create a new one
				cert = x509DAO.createNew();
				cert.setCertificateName(name + candidate.getCandidateName());
				cert.setX509certificate(x509cert);
				x509DAO.persist(cert);
			}
			Set<CertificateX509> certificates = candidate.getX509Certificates();
			if (certificates == null)
			{
				certificates = new HashSet<CertificateX509>();
			}
			certificates.add(cert);
			candidate.setX509Certificates(certificates);
			testCandidateDAO.update(candidate);
			return (name + candidate.getCandidateName());
		}
		catch (CertificateException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("Could not process x509 certificate: " + base64data + System.getProperty("line.separator") + "Trace:" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController#downloadCertificates(TestCandidate)
	 */
	@Override
	@Asynchronous
	public Future<byte[]> downloadCertificates(TestCandidate candidate) throws IOException, CertificateEncodingException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		// generated x509
		for (CertificateX509 x : candidate.getX509Certificates())
		{
			// certificate
			ZipEntry ze = new ZipEntry("candidate_tls/" + x.getCertificateName() + ".crt");
			zos.putNextEntry(ze);
			zos.write(x.getX509certificate().getEncoded());
			zos.closeEntry();

			KeyPair pair = x.getKeyPair();
			if (null != pair)
			{
				// private key, if existing
				if (null != pair.getPrivate())
				{
					try
					{
						KeyStore ks = KeyStore.getInstance("JKS");
						ks.load(null, null);
						ks.setKeyEntry("alias", pair.getPrivate(), "123456".toCharArray(), new Certificate[] { x.getX509certificate() });
						ZipEntry privk = new ZipEntry("candidate_tls/keys/" + x.getCertificateName() + ".jks");
						zos.putNextEntry(privk);
						ks.store(zos, "123456".toCharArray());
						zos.closeEntry();
					}
					catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e)
					{
						// nothing
					}
				}
			}
		}

		Map<String, byte[]> certificatesAndKeys = runnerBean.getStaticCertificatesAndKeys();
		for (String path : certificatesAndKeys.keySet())
		{
			ZipEntry ze = new ZipEntry(path);
			zos.putNextEntry(ze);
			zos.write(certificatesAndKeys.get(path));
			zos.closeEntry();
		}

		zos.close();
		return new AsyncResult<>(baos.toByteArray());
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController#generateCertificates(TestCandidate)
	 */
	@Override
	public Set<CertificateX509> generateCertificates(TestCandidate candidate) throws CertificateGenerationExcpetion, MalformedURLException
	{
		CertificateDefinitions tlsDefinitions = createTls(candidate);
		CertificateGenerator generator = new BouncyCertificateGenerator();
		List<GeneratedCertificate> generated = generator.makex509Certificate(tlsDefinitions);
		Set<CertificateX509> certificates = new HashSet<>();
		for (GeneratedCertificate x509 : generated)
		{
			CertificateX509 x = x509DAO.createNew();
			x.setCertificateName(x509.getDefinition().getName());
			x.setX509certificate(x509.getCertificate());
			x.setKeyPair(x509.getKeyPair());
			x509DAO.persist(x);
			certificates.add(x);
		}
		return certificates;
	};

	// create TLS certificate definitions
	private CertificateDefinitions createTls(TestCandidate candidate) throws MalformedURLException
	{
		CertificateDefinitions def = new CertificateDefinitions();

		// issuer
		DnType issuer = new DnType();
		issuer.setCommonName(GeneralConstants.CA_NAME);
		issuer.setCountry(GeneralConstants.CA_COUNTRY);
		issuer.setOrganization(GeneralConstants.CA_ORGANIZATION_NAME);
		issuer.setOrganizationalUnit(GeneralConstants.CA_ORGANIZATION_UNIT_NAME);

		// subject
		DnType subject = new DnType();
		subject.setCommonName(candidate.getCandidateName() + " " + candidate.getVersionMajor() + "." + candidate.getVersionMinor() + "." + candidate.getVersionSubminor());
		subject.setCountry("DE");
		subject.setOrganization(candidate.getVendor());
		subject.setOrganizationalUnit("");

		// eCardAPI
		if (null != candidate.getEcardapiUrl())
		{
			// PSK
			if (null != candidate.getTlsEcardApiPsk() && !candidate.getTlsEcardApiPsk().isEmpty())
			{
				Set<CertificateDefinition> definitions = createDefinitions(candidate.getTlsEcardApiPsk(), candidate.getEcardapiUrl(), "CERT_ECARD_TLS_EIDSERVER_1_");
				for (CertificateDefinition cd : definitions)
				{
					cd.setIssuer(issuer);
					cd.setSubject(subject);
					def.getCertificateDefinition().add(cd);
				}
			}

			// attached
			if (null != candidate.getTlsEcardApiAttached() && !candidate.getTlsEcardApiAttached().isEmpty())
			{
				Set<CertificateDefinition> definitions = createDefinitions(candidate.getTlsEcardApiAttached(), candidate.getAttachedTcTokenUrl(), "CERT_ECARD_TLS_EIDSERVER_ATTACHED_1_");
				for (CertificateDefinition cd : definitions)
				{
					cd.setIssuer(issuer);
					cd.setSubject(subject);
					def.getCertificateDefinition().add(cd);
				}
			}
		}

		// eID-Interface
		if (null != candidate.getEidinterfaceUrl())
		{
			if (null != candidate.getTlsEidInterface() && !candidate.getTlsEidInterface().isEmpty())
			{
				Set<CertificateDefinition> definitions = createDefinitions(candidate.getTlsEidInterface(), candidate.getEidinterfaceUrl(), "CERT_EID_TLS_EIDSERVER_1_");
				for (CertificateDefinition cd : definitions)
				{
					cd.setIssuer(issuer);
					cd.setSubject(subject);
					def.getCertificateDefinition().add(cd);
				}
			}
		}

		// SAML interface
		if (null != candidate.getSamlUrl())
		{
			if (null != candidate.getTlsSaml() && !candidate.getTlsSaml().isEmpty())
			{
				Set<CertificateDefinition> definitions = createDefinitions(candidate.getTlsSaml(), candidate.getSamlUrl(), "CERT_ECARD_TLS_SAMLPROCESSOR_1_");
				for (CertificateDefinition cd : definitions)
				{
					cd.setIssuer(issuer);
					cd.setSubject(subject);
					def.getCertificateDefinition().add(cd);
				}
			}
		}

		return def;
	}

	/**
	 * Create {@link CertificateDefinition}s for the given {@link Tls} set and URL.
	 * 
	 * @param tlsEntries
	 *            {@link Tls}
	 * @param url
	 *            {@link URL}
	 * @return
	 */
	private Set<CertificateDefinition> createDefinitions(Set<Tls> tlsEntries, URL url, String name)
	{
		Set<IcsTlsSignaturealgorithms> alreadyProcessedSignatureAlgorithms = new HashSet<>();
		Set<String> existingDefinitions = new HashSet<>();

		Set<CertificateDefinition> definitions = new HashSet<>();
		for (Tls tls : tlsEntries)
		{
			// if TLS 1.2 is encountered, process the signature algorithms
			if (tls.getTlsVersion().equals(IcsTlsVersion.TL_SV_12))
			{
				if (tls.getSignatureAlgorithms().size() > 0)
				{
					for (IcsTlsSignaturealgorithms sigAlg : tls.getSignatureAlgorithms())
					{
						if (!alreadyProcessedSignatureAlgorithms.contains(sigAlg))
						{
							if (sigAlg.value().endsWith("RSA"))
							{
								CertificateDefinition definition = createDefinition(url);
								definition.setName(name + "_" + sigAlg.value());
								SignatureAlgorithmType sat = new SignatureAlgorithmType();
								sat.setRSA(SignatureAlgorithmTypeRSA.fromValue(sigAlg.value()));
								definition.setSignatureAlgorithm(sat);
								AlgorithmType algorithmType = new AlgorithmType();
								algorithmType.setRSA(2048);
								definition.setKeyAlgorithm(algorithmType);
								definitions.add(definition);
							}
							else if (sigAlg.value().endsWith("ECDSA"))
							{
								for (IcsEllipticcurve curve : tls.getEllipticCurves())
								{
									CertificateDefinition definition = createDefinition(url);
									definition.setName(name + "_" + sigAlg.value());
									SignatureAlgorithmType sat = new SignatureAlgorithmType();
									sat.setECDSA(SignatureAlgorithmTypeECDSA.fromValue(sigAlg.value()));
									definition.setSignatureAlgorithm(sat);
									AlgorithmType algorithmType = new AlgorithmType();
									algorithmType.setECDSA(AlgorithmTypeEC.fromValue(curve.value()));
									definition.setKeyAlgorithm(algorithmType);
									definitions.add(definition);
									existingDefinitions.add(curve.value());
								}
							}
							// DSA
							else
							{
								CertificateDefinition definition = createDefinition(url);
								definition.setName(name + "_" + sigAlg.value());
								SignatureAlgorithmType sat = new SignatureAlgorithmType();
								sat.setDSA(SignatureAlgorithmTypeDSA.fromValue(sigAlg.value()));
								definition.setSignatureAlgorithm(sat);
								AlgorithmType algorithmType = new AlgorithmType();
								algorithmType.setDSA(2048);
								definition.setKeyAlgorithm(algorithmType);
								definitions.add(definition);
							}

							alreadyProcessedSignatureAlgorithms.add(sigAlg);
						}
					}
				}
				// RSA PSK does not specify signature algorithms
				else
				{
					CertificateDefinition definition = createDefinition(url);
					definition.setName(name);
					AlgorithmType algorithmType = new AlgorithmType();
					algorithmType.setRSA(2048);
					definition.setKeyAlgorithm(algorithmType);
					definitions.add(definition);
				}
			}
			// pre-tls 1.2
			else
			{
				for (IcsTlsCiphersuite suite : tls.getTlsCiphersuites())
				{
					CertificateDefinition definition = createDefinition(url);
					AlgorithmType algorithmType = new AlgorithmType();
					if (suite.value().contains("RSA") && !existingDefinitions.contains("RSA"))
					{
						definition.setName(name + "_RSA");
						algorithmType.setRSA(2048);
						definition.setKeyAlgorithm(algorithmType);
						existingDefinitions.add("RSA");
					}
					else if (suite.value().contains("DSS") && !existingDefinitions.contains("DSA"))
					{
						definition.setName(name + "_DSA");
						algorithmType.setDSA(2048);
						definition.setKeyAlgorithm(algorithmType);
						existingDefinitions.add("DSA");
					}
					definitions.add(definition);
				}
			}
		}
		return definitions;
	}

	/**
	 * Create a (non-specific) TLS {@link CertificateDefinition} for the given {@link URL}
	 * 
	 * @param url
	 *            {@link URL}
	 * @return
	 */
	private CertificateDefinition createDefinition(URL url)
	{
		CertificateDefinition definition = new CertificateDefinition();

		// extensions
		ExtensionsType extensions = new ExtensionsType();
		// keyUsage and extendedKeyUsage
		KeyUsageType keyUsageType = new KeyUsageType();
		keyUsageType.setKeyAgreement(true);
		keyUsageType.setDigitalSignature(true);
		keyUsageType.setKeyEncipherment(true);
		extensions.setKeyUsage(keyUsageType);
		ExtendedKeyUsageType extKeyUsage = new ExtendedKeyUsageType();
		extKeyUsage.setServerAuth(true);
		extKeyUsage.setClientAuth(true);
		extensions.setExtendedKeyUsage(extKeyUsage);
		// subjectAlternativeName
		AltNameType subjectAltName = new AltNameType();
		GeneralName name = new GeneralName();
		name.setType(GeneralNameTypeType.D_NS_NAME);
		name.setValue(url.getHost());
		subjectAltName.getGeneralName().add(name);
		extensions.setSubjectAltName(subjectAltName);
		definition.setExtensions(extensions);

		return definition;
	}

	@Override
	public void deleteCertificates(TestCandidate candidate)
	{
		for (CertificateX509 cert : candidate.getX509Certificates())
		{
			x509DAO.delete(cert);
		}
	}

}
