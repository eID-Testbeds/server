package com.secunet.eidserver.testbed.controller;

import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.classes.NaiveTrustManager;
import com.secunet.eidserver.testbed.common.classes.Singletons;
import com.secunet.eidserver.testbed.common.eidas.EidasAuthnRequestHandler;
import com.secunet.eidserver.testbed.common.helper.CryptoHelper;
import com.secunet.eidserver.testbed.common.ics.*;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.Runner;
import com.secunet.eidserver.testbed.common.interfaces.dao.*;
import com.secunet.eidserver.testbed.common.interfaces.entities.*;
import com.secunet.eidserver.testbed.common.types.testcase.EService;
import de.bsi.testbedutils.bouncycertgen.xml.POSeIDAS.*;
import de.governikus._2021._03.eumw_config.*;
import net.shibboleth.shared.xml.SerializeSupport;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorMarshaller;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.w3c.dom.Element;
import se.swedenconnect.opensaml.eidas.ext.NodeCountry;
import se.swedenconnect.opensaml.eidas.ext.impl.NodeCountryBuilder;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateless
public class CertificateControllerBean implements CertificateController
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private CertificateX509DAO x509DAO;

	@EJB
	private CertificateCVDAO cvDAO;

	@EJB
	private CryptoFileDAO cryptoFileDAO;

	@EJB
	private BlacklistDAO blacklistDAO;

	@EJB
	private Runner runnerBean;

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
						if(x.getCertificateName().startsWith("CERT_ECARD_TLS_EIDSERVER_ATTACHED_1_")) { // special case for eidas middleware configuration
							ZipEntry eidasprivk = new ZipEntry("eIDAS/crypt-material/" + x.getCertificateName() + ".jks");
							zos.putNextEntry(eidasprivk);
							ks.store(zos, "123456".toCharArray());
							zos.closeEntry();
						}
					}
					catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e)
					{
						// nothing
					}
				}
			}
		}

		ZipEntry zem = new ZipEntry("eIDAS/serviceprovider-metadata/metadata.xml");
		zos.putNextEntry(zem);
		zos.write(candidate.getEidasMetadata().getBytes(StandardCharsets.UTF_8));
		zos.closeEntry();

//		ZipEntry zep = new ZipEntry("eIDAS/POSeIDAS.xml");
//		zos.putNextEntry(zep);
//		zos.write(getPOSeIDASBytes(candidate));
//		zos.closeEntry();

		ZipEntry zen = new ZipEntry("eIDAS/eIDASMiddlewareConfig.xml");
		zos.putNextEntry(zen);
		zos.write(getNewEidasMiddlewareConfigBytes(candidate));
		zos.closeEntry();

		Map<String, byte[]> certificatesAndKeys = CryptoHelper.getCryptoFileMap(candidate);

		for (String path : certificatesAndKeys.keySet())
		{
			if (path.startsWith("keystores") || path.endsWith("EECDSA.pem"))
				continue;
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
	public GeneratedCertsContainer generateCertificates(TestCandidate candidate) throws IOException {
		return CryptoHelper.generateCertificatesAndKeys(new SecureRandom().generateSeed(31), candidate.getAttachedTcTokenUrl());
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController#getMetadataBytes(TestCandidate candidate)
	 */
	@Override
	public byte[] getMetadataBytes(TestCandidate candidate) {
		try {
			EntityDescriptor ed = getMetadata(candidate);
			EntityDescriptorMarshaller edm = new EntityDescriptorMarshaller();
			Element all = edm.marshall(ed);
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			try(ByteArrayOutputStream bout = new ByteArrayOutputStream()){
				trans.transform(new DOMSource(all), new StreamResult(bout));
				return bout.toByteArray();
			}
		} catch (Exception e) {
			logger.error("Error occurred while getting metadata bytes", e);
			return null;
		}
	}
	private EntityDescriptor getMetadata(TestCandidate candidate) throws InitializationException, IOException, MarshallingException, SignatureException, NoSuchFieldException, IllegalAccessException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, SecurityException {
			EidasAuthnRequestHandler.Init();
			X509Credential signCreds = runnerBean.getSignatureCreds(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F, candidate); //TODO: Why F?????
			X509Credential cryptCreds = runnerBean.getEncryptionCreds(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.RSA_ALG_ID, EService.F, candidate);
			EntityDescriptor entityDescriptor = buildEntityDescriptor(signCreds, cryptCreds, candidate);

			EidasAuthnRequestHandler.sign(entityDescriptor, signCreds);
			logger.info("Successfully generated metadata. Metadata ID: {}", entityDescriptor.getID());
			if (logger.isDebugEnabled()) {
				logger.debug("Generated metadata: {}", getXmlString(entityDescriptor));
			}
			return entityDescriptor;
	}

	private String getXmlString(final XMLObject object) throws MarshallingException {
			Element entityDescriptorElement = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object).marshall(object);
			return SerializeSupport.nodeToString(entityDescriptorElement);
	}

	private <T> T buildSAMLObject(final Class<T> clazz) throws NoSuchFieldException, IllegalAccessException {
		T object;
		XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
		QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
		object = (T) builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
		return object;
	}

	private EntityDescriptor buildEntityDescriptor(X509Credential signCredential, X509Credential cryptCredential, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException, SecurityException {
		EntityDescriptor descriptor = buildSAMLObject(EntityDescriptor.class);
		descriptor.setEntityID("https://doesnotexi.st"); // Should be a HTTPS URL, but that is not compatible with the existing tests
		descriptor.setValidUntil(Instant.now().plus(356, ChronoUnit.DAYS));
		descriptor.setID(generateEntityDescriptorId());
		descriptor.setExtensions(generateMetadataExtensions(candidate));
		descriptor.getRoleDescriptors().add(buildSPSSODescriptor(signCredential, cryptCredential, candidate));
		return descriptor;
	}

	private Extensions generateMetadataExtensions(TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException {
		Extensions extensions = buildSAMLObject(Extensions.class);
		extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);
		addUsedDigestMethodsToExtensions(extensions, candidate);
		addUsedSigingMethodsToExtensions(extensions, candidate);
		//addUsedEncryptionMethodsToExtensions(extensions);
		return extensions;
	}

	private void addUsedSigingMethodsToExtensions(Extensions extensions, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException {
		Set<Object[]> usedSigningMethods = new LinkedHashSet<>();
		if (candidate.getXmlSignatureAlgorithmsEidas() == null || candidate.getXmlSignatureAlgorithmsEidas().isEmpty()) {
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_256_MGF1.value(), 3072, 4096});
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_384_MGF1.value(), 3072, 4096});
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_RSA_SHA_512_MGF1.value(), 3072, 4096});
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_256.value(), 256, null});
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_384.value(), 256, null});
			usedSigningMethods.add(new Object[]{IcsXmlsecSignatureUri.HTTP_WWW_W_3_ORG_2001_04_XMLDSIG_MORE_ECDSA_SHA_512.value(), 256, null});
		} else {
			for (XmlSignature xmlSignature : candidate.getXmlSignatureAlgorithmsEidas()) {
				Set<BigInteger> bitLengths = xmlSignature.getBitLengths();
				Integer minKeySize = 256;
				Integer maxKeySize = null;
				if (bitLengths != null && !bitLengths.isEmpty()) {
					minKeySize = bitLengths.stream().mapToInt(BigInteger::intValue).min().orElse(0);
					maxKeySize = bitLengths.stream().mapToInt(BigInteger::intValue).max().orElse(0);
				}
				usedSigningMethods.add(new Object[]{xmlSignature.getSignatureAlgorithm().value(), minKeySize, maxKeySize});
			}
		}
		for (Object[] signingMethod : usedSigningMethods) {
			SigningMethod method = buildSAMLObject(SigningMethod.class);
			method.setAlgorithm((String) signingMethod[0]);
			if (signingMethod[1] != null) {
				method.setMinKeySize((int) signingMethod[1]);
			}
			if (signingMethod[2] != null) {
				method.setMaxKeySize((int) signingMethod[2]);
			}
			extensions.getUnknownXMLObjects().add(method);
		}
	}

	private void addUsedDigestMethodsToExtensions(Extensions extensions, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException {
		Set<String> usedDigestMethods = new LinkedHashSet<>();
		if (candidate.getXmlSignatureAlgorithmsEidas() == null || candidate.getXmlSignatureAlgorithmsEidas().isEmpty()) {
			usedDigestMethods.add(IcsXmlsecSignatureDigest.HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256.value());
		} else {
			for (XmlSignature xmlSignature : candidate.getXmlSignatureAlgorithmsEidas()) {
				usedDigestMethods.add(xmlSignature.getDigestMethod().value());
			}
		}

		for (String digestMethod : usedDigestMethods) {
			DigestMethod method = buildSAMLObject(DigestMethod.class);
			method.setAlgorithm(digestMethod);
			extensions.getUnknownXMLObjects().add(method);
		}
	}

	private void addUsedEncryptionMethodsToKeyDescriptor(KeyDescriptor kd, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException {
		Set<String> usedEncryptionMethods = new LinkedHashSet<>();
		if (candidate.getXmlEncryptionAlgorithmsEidas() == null || candidate.getXmlEncryptionAlgorithmsEidas().isEmpty()) {
			usedEncryptionMethods.add(IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_256_GCM.value());
			usedEncryptionMethods.add(IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_192_GCM.value());
			usedEncryptionMethods.add(IcsXmlsecEncryptionContentUri.HTTP_WWW_W_3_ORG_2009_XMLENC_11_AES_128_GCM.value());
		} else {
			usedEncryptionMethods.addAll(candidate.getXmlEncryptionAlgorithmsEidas().stream().map(IcsXmlsecEncryptionContentUri::value).collect(Collectors.toSet()));
		}


		for (String encryptionMethod : usedEncryptionMethods) {
			EncryptionMethod method = buildSAMLObject(EncryptionMethod.class);
			method.setAlgorithm(encryptionMethod);
			kd.getEncryptionMethods().add(method);
		}
	}

	private String generateEntityDescriptorId() {
		return "_".concat(RandomStringUtils.randomAlphanumeric(39)).toLowerCase();
	}

	private SPSSODescriptor buildSPSSODescriptor(X509Credential signCredential, X509Credential cryptCredential, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException, SecurityException {
		SPSSODescriptor spDescriptor = buildSAMLObject(SPSSODescriptor.class);

		spDescriptor.setAuthnRequestsSigned(true);
		spDescriptor.setWantAssertionsSigned(true);

		spDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

		spDescriptor.getAssertionConsumerServices().add(getAssertionConsumerService(SAMLConstants.SAML2_POST_BINDING_URI, 0, true));

		spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING,
				generateKeyInfoForCredential(signCredential), candidate));

		spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION,
				generateKeyInfoForCredential(cryptCredential), candidate));

		NodeCountry nodeCountry = new NodeCountryBuilder().buildObject();
		nodeCountry.setNodeCountry("DE");


		Extensions extensions = buildSAMLObject(Extensions.class);
		extensions.getUnknownXMLObjects().add(nodeCountry);
		spDescriptor.setExtensions(extensions);



		return spDescriptor;
	}

	private Collection<NameIDFormat> buildNameIDFormat() throws NoSuchFieldException, IllegalAccessException {
		Collection<NameIDFormat> formats = new LinkedList<>();
		NameIDFormat persistentNameID = buildSAMLObject(NameIDFormat.class);
		persistentNameID.setURI(NameIDType.PERSISTENT);
		formats.add(persistentNameID);
		NameIDFormat transientNameID = buildSAMLObject(NameIDFormat.class);
		transientNameID.setURI(NameIDType.TRANSIENT);
		formats.add(transientNameID);
		NameIDFormat unspecNameID = buildSAMLObject(NameIDFormat.class);
		unspecNameID.setURI(NameIDType.UNSPECIFIED);
		formats.add(unspecNameID);
		return formats;
	}

	@SuppressWarnings("SameParameterValue")
	private AssertionConsumerService getAssertionConsumerService(String binding, int index,
																 boolean isDefault) throws NoSuchFieldException, IllegalAccessException {
		AssertionConsumerService consumer = buildSAMLObject(AssertionConsumerService.class);
		consumer.setLocation("https://some.where/ConnectorPost");
		consumer.setBinding(binding);
		if (isDefault) {
			consumer.setIsDefault(true);
		}
		consumer.setIndex(index);
		return consumer;
	}

	private KeyDescriptor getKeyDescriptor(UsageType type, KeyInfo key, TestCandidate candidate) throws NoSuchFieldException, IllegalAccessException {
		KeyDescriptor descriptor = buildSAMLObject(KeyDescriptor.class);
		descriptor.setUse(type);
		descriptor.setKeyInfo(key);
		if (type == UsageType.ENCRYPTION) {
			addUsedEncryptionMethodsToKeyDescriptor(descriptor, candidate);
		}
		return descriptor;
	}

	private KeyInfo generateKeyInfoForCredential(Credential credential) throws SecurityException {
		NamedKeyInfoGeneratorManager generatorManager = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
		return generatorManager.getDefaultManager().getFactory(credential).newInstance().generate(credential);
	}

	@Override
	public byte[] getNewEidasMiddlewareConfigBytes(TestCandidate candidate) {
		try {
			EidasMiddlewareConfig eidasMiddlewareConfig = getNewEidasMiddlewareConfig(candidate);
			JAXBContext jaxbContext = JAXBContext.newInstance(EidasMiddlewareConfig.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(eidasMiddlewareConfig, baos);
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error("Error occurred while getting eIDAS middleware configuration bytes", e);
			return new byte[0];
		}
	}

	private EidasMiddlewareConfig getNewEidasMiddlewareConfig(TestCandidate candidate) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
		URL eCardApiUrl = candidate.getEcardapiUrl(); //get one of the urls to split it up
		URL localServerUrl = Singletons.currentUrl;
		String host = localServerUrl.getProtocol() + "://" + localServerUrl.getHost() + ":" + localServerUrl.getPort();
		String serverUrl = eCardApiUrl.getProtocol() + "://" + eCardApiUrl.getHost();
		String taServiceUrl = host + "/CA1/TA-Service";
		String riServiceUrl = host + "/CA1/RI-Service";
		String paServiceUrl = host + "/CA1/PA-Service";

		EidasMiddlewareConfig eidasMiddlewareConfig = new EidasMiddlewareConfig();
		eidasMiddlewareConfig.setServerUrl(serverUrl);
		EidasMiddlewareConfig.EidasConfiguration eidasConfiguration = new EidasMiddlewareConfig.EidasConfiguration();
		ConnectorMetadataType connectorMetadata = new ConnectorMetadataType();
		connectorMetadata.setEntityID("https://doesnotexi.st");
		connectorMetadata.setValue(getMetadataBytes(candidate));
		eidasConfiguration.getConnectorMetadata().add(connectorMetadata);
		eidasConfiguration.setDoSign(false);
		eidasConfiguration.setMetadataValidity(0);
		eidasConfiguration.setCountryCode("DE");
		ContactType contact = new ContactType();
		contact.setCompany("");
		contact.setEmail("");
		contact.setGivenname("");
		contact.setSurname("");
		contact.setTelephone("");
		eidasConfiguration.setContactPerson(contact);
		OrganizationType organization = new OrganizationType();
		organization.setName("");
		organization.setDisplayname("");
		organization.setUrl("");
		organization.setLanguage("");
		eidasConfiguration.setOrganization(organization);
		eidasConfiguration.setMetadataSignatureVerificationCertificateName("metadata-signature-verification");
		eidasConfiguration.setSignatureKeyPairName("signature_keypair");
		eidasMiddlewareConfig.setEidasConfiguration(eidasConfiguration);
		EidasMiddlewareConfig.EidConfiguration eidConfiguration = new EidasMiddlewareConfig.EidConfiguration();
		DvcaConfigurationType dvcaConfiguration = new DvcaConfigurationType();
		dvcaConfiguration.setName("testDVCA");
		dvcaConfiguration.setRestrictedIdServiceUrl(riServiceUrl);
		dvcaConfiguration.setTerminalAuthServiceUrl(taServiceUrl);
		dvcaConfiguration.setPassiveAuthServiceUrl(paServiceUrl);
		dvcaConfiguration.setBlackListTrustAnchorCertificateName("BlacklistSigner");
		dvcaConfiguration.setMasterListTrustAnchorCertificateName("MasterlistSigner");
		dvcaConfiguration.setServerSSLCertificateName("dvca-server");
		eidConfiguration.getDvcaConfiguration().add(dvcaConfiguration);

		EidasMiddlewareConfig.KeyData keyData = new EidasMiddlewareConfig.KeyData();

		List<String> serviceNames = new ArrayList<>();
		serviceNames.add("A");
		serviceNames.add("B");
		serviceNames.add("C");
		serviceNames.add("D");
		serviceNames.add("ERSA");
		serviceNames.add("EECDSA");
		serviceNames.add("F");
		serviceNames.add("G");
		for(String current : serviceNames) {
			ServiceProviderType serviceProvider = new ServiceProviderType();
			serviceProvider.setName("Testbed" + current);
			serviceProvider.setEnabled(true);
			serviceProvider.setCVCRefID(current);
			serviceProvider.setClientKeyPairName(current + "_clientauth");
			serviceProvider.setDvcaConfigurationName("testDVCA");
			eidConfiguration.getServiceProvider().add(serviceProvider);
			CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
			if (current.equals("EECDSA")) {
				algorithm = CryptoHelper.Algorithm.ECDSA_ALG_ID;
			}
			X509Certificate clientCertificate = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, algorithm, current + "_clientauth", candidate);
			PrivateKey clientKey = CryptoHelper.loadKey(CryptoHelper.Protocol.SAML_PROT_ID, algorithm, current + "_clientauth", candidate);
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(null,null);
			ks.setKeyEntry(current + "_clientauth", clientKey, "123456".toCharArray(), new Certificate[] {clientCertificate});
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ks.store(baos, "123456".toCharArray());
			KeyStoreType keyStore = new KeyStoreType();
			keyStore.setKeyStore(baos.toByteArray());
			keyStore.setName(current + "_keystore");
			keyStore.setPassword("123456");
			keyStore.setType(KeyStoreTypeType.PKCS_12);
			keyData.getKeyStore().add(keyStore);
			KeyPairType keyPair = new KeyPairType();
			keyPair.setAlias(current + "_clientauth");
			keyPair.setKeyStoreName(current + "_keystore");
			keyPair.setPassword("123456");
			keyPair.setName(current + "_clientauth");
			keyData.getKeyPair().add(keyPair);
		}
		eidasMiddlewareConfig.setEidConfiguration(eidConfiguration);

		// Server certificate
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[]{new NaiveTrustManager()}, new java.security.SecureRandom());


		URL url = new URL(host);
		if (url.getProtocol().equalsIgnoreCase("https")) {
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.connect();
			X509Certificate[] certs = (X509Certificate[]) connection.getServerCertificates();

			CertificateType serverCertificate = new CertificateType();
			serverCertificate.setCertificate(certs[0].getEncoded());
			serverCertificate.setName("dvca-server");
			keyData.getCertificate().add(serverCertificate);
		}

		//Metadata Signing certificate
		X509Credential signCreds = runnerBean.getSignatureCreds(CryptoHelper.Protocol.SAML_PROT_ID, CryptoHelper.Algorithm.ECDSA_ALG_ID, EService.F, candidate); //TODO: Why F?????
		CertificateType signCertificate = new CertificateType();
		signCertificate.setCertificate(signCreds.getEntityCertificate().getEncoded());
		signCertificate.setName("metadata-signature-verification");
		keyData.getCertificate().add(signCertificate);

		//Metadata Signing Keypair

		Map<String,byte[]> cryptoMap = CryptoHelper.getCryptoFileMap(candidate);
		KeyStoreType keyStore = new KeyStoreType();
		keyStore.setKeyStore(cryptoMap.get("eIDAS/crypt-material/A.p12"));
		keyStore.setName("signature_keystore");
		keyStore.setPassword("123456");
		keyStore.setType(KeyStoreTypeType.PKCS_12);
		keyData.getKeyStore().add(keyStore);
		KeyPairType keyPair = new KeyPairType();
		keyPair.setAlias("signature.eid-acme_a.com");
		keyPair.setKeyStoreName("signature_keystore");
		keyPair.setPassword("123456");
		keyPair.setName("signature_keypair");
		keyData.getKeyPair().add(keyPair);

		//Blacklist Signing certificate

		CertificateType blackListCertificate = new CertificateType();
		blackListCertificate.setCertificate(cryptoMap.get("certificates/CSCA/" + "CERT_ECARD_CSCA_1_BLACKLIST_SIGNER.pem"));
		blackListCertificate.setName("BlacklistSigner");
		keyData.getCertificate().add(blackListCertificate);

		//Masterlist Signing certificate
		CertificateType masterListCertificate = new CertificateType();
		masterListCertificate.setCertificate(cryptoMap.get("certificates/CSCA/" + "CERT_ECARD_CSCA_1_MASTERLIST_SIGNER.pem"));
		masterListCertificate.setName("MasterlistSigner");
		keyData.getCertificate().add(masterListCertificate);

		eidasMiddlewareConfig.setKeyData(keyData);
		return eidasMiddlewareConfig;
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController#getPOSeIDASBytes(TestCandidate) 
	 */
	@Override
	public byte[] getPOSeIDASBytes(TestCandidate candidate) {
		try {
			POSeIDASConfiguration configurationObject = getPOSeIDAS(candidate);

			JAXBContext jaxbContext = JAXBContext.newInstance(POSeIDASConfiguration.class);
			Marshaller marshaller = jaxbContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(configurationObject, baos);

			return baos.toByteArray();
		} catch (JAXBException e) {
			logger.error("Error during XML marshalling.", e);
			return new byte[0];
		} catch (Exception e) {
			logger.error("Unexpected error occurred.", e);
			return new byte[0];
		}
	}

	/**
	 * Generates a POSeIDAS configuration for a given test candidate.
	 * This method constructs a POSeIDAS configuration object, which includes setting up server URLs, loading certificates,
	 * configuring terminal, restricted ID, and passive auth services, and creating service providers.
	 * It attempts to load two specific certificates from predefined paths and fetches the server SSL certificate.
	 *
	 * @param candidate The test candidate for whom the POSeIDAS configuration is being generated.
	 * @return A fully constructed POSeIDASConfiguration object.
	 * @throws IOException If there is an error reading certificate files or connecting to the server URL.
	 * @throws CertificateEncodingException If there is an error encoding the certificates to Base64.
	 */
	private POSeIDASConfiguration getPOSeIDAS(TestCandidate candidate) throws IOException, CertificateEncodingException {
		POSeIDASConfiguration poSeIDASConfiguration = new POSeIDASConfiguration();

		// some fields that will be used multiple times
		URL eCardApiUrl = candidate.getEcardapiUrl(); //get one of the urls to split it up
		URL localServerUrl = Singletons.currentUrl;
		String host = localServerUrl.getProtocol() + "://" + localServerUrl.getHost() + ":" + localServerUrl.getPort();
		String serverUrl = eCardApiUrl.getProtocol() + "://" + eCardApiUrl.getHost() + "/eidas-middleware";
		String taServiceUrl = host + "/CA1/TA-Service";
		String riServiceUrl = host + "/CA1/RI-Service";
		String paServiceUrl = host + "/CA1/PA-Service";
		String paosReceiverUrl = eCardApiUrl.toString();
		String testDvca = "testDVCA";

		// some objects that will be used multiple times
		Base64.Encoder base64encoder = Base64.getEncoder();

		X509Certificate blackListCert, masterListCert;
		CertificateFactory certFactory;
		try {
			Map<String,byte[]> cryptoMap = CryptoHelper.getCryptoFileMap(candidate);
			certFactory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);

			blackListCert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(cryptoMap.get("certificates/CSCA/" + "CERT_ECARD_CSCA_1_BLACKLIST_SIGNER.pem")));
			masterListCert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(cryptoMap.get("certificates/CSCA/" + "CERT_ECARD_CSCA_1_MASTERLIST_SIGNER.pem")));
		} catch (CertificateException | NoSuchProviderException e) {
            logger.error("Exception happened while writing certificate content to POSeIDAS.xml, Exception", e);
			throw new RuntimeException(e);
        }

		/*
		 * Getting Server SSL Certificate as PEM
		 */
		String serverCertificate = "";
		try {
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new NaiveTrustManager()}, new java.security.SecureRandom());


			URL url = new URL(host);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.connect();
			X509Certificate[] certs = (X509Certificate[]) connection.getServerCertificates();

			serverCertificate = base64encoder.encodeToString(certs[0].getEncoded());
		} catch (SSLException e) {
			logger.error("SSL handshake failed: ", e);
		} catch (CertificateEncodingException e) {
			logger.error("Failed to encode certificate: ", e);
		} catch (Exception e) {
			logger.error("Error when getting ServerCertificate: ", e);
		}

		String blackListTrustAnchor = base64encoder.encodeToString(blackListCert.getEncoded());
		String masterListTrustAnchor = base64encoder.encodeToString(masterListCert.getEncoded());

        /*
		 * Creating top-level elements
		 */
		poSeIDASConfiguration.setServerUrl(serverUrl);

		poSeIDASConfiguration.setSessionManagerUsesDatabase(true);

		poSeIDASConfiguration.setSessionMaxPendingRequests(500);

		/*
		 * Generating TimerConfiguration
		 */
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setLength(2);
		timerConfig.setUnit(11);

		TimerConfiguration timerConfiguration = new TimerConfiguration();
		timerConfiguration.setCertRenewal(timerConfig);
		timerConfiguration.setBlacklistRenewal(timerConfig);
		timerConfiguration.setMasterAndDefectListRenewal(timerConfig);
		poSeIDASConfiguration.setTimerConfiguration(timerConfiguration);

		/*
		 * Generating ServiceProviders
		 */
		List<ServiceProvider> serviceProviders = new ArrayList<>();
		List<String> serviceNames = new ArrayList<>();
		serviceNames.add("A");
		serviceNames.add("B");
		serviceNames.add("C");
		serviceNames.add("D");
		serviceNames.add("ERSA");
		serviceNames.add("EECDSA");
		serviceNames.add("F");
		serviceNames.add("G");
		for(String current : serviceNames) {

			CryptoHelper.Algorithm algorithm = CryptoHelper.Algorithm.RSA_ALG_ID;
			if (current.equals("EECDSA")) {
				algorithm = CryptoHelper.Algorithm.ECDSA_ALG_ID;
			}
			X509Certificate clientCertificate = CryptoHelper.loadCertificate(CryptoHelper.Protocol.SAML_PROT_ID, algorithm, current + "_clientauth", candidate);
			PrivateKey clientKey = CryptoHelper.loadKey(CryptoHelper.Protocol.SAML_PROT_ID, algorithm, current + "_clientauth", candidate);

			ServiceProvider serviceProvider = new ServiceProvider();
			serviceProvider.setEntityID("Testbed" + current);
			serviceProvider.setEnabled(true);

			SslKeys sslKeysA = new SslKeys();
			sslKeysA.setId("testDVCA");
			sslKeysA.setServerCertificate(serverCertificate);
			sslKeysA.setClientCertificate(base64encoder.encodeToString(clientCertificate.getEncoded()));
			StringWriter sw = new StringWriter();
			try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(sw)) {
				jcaPEMWriter.writeObject(clientKey);
			}
			String pemString = sw.toString();
			pemString = pemString.replace("-----BEGIN RSA PRIVATE KEY-----", "")
					.replace("-----END RSA PRIVATE KEY-----", "")
					.replace("-----BEGIN EC PRIVATE KEY-----", "")
					.replace("-----BEGIN ECDH PRIVATE KEY-----", "")
					.replace("-----BEGIN ECDSA PRIVATE KEY-----", "")
					.replace("-----END EC PRIVATE KEY-----", "")
					.replace("-----END ECDH PRIVATE KEY-----", "")
					.replace("-----END ECDSA PRIVATE KEY-----", "")
					.replaceAll("\\r\\n|\\r|\\n", "");
			sslKeysA.setClientKey(pemString);
			sw.close();


			TerminalAuthService terminalAuthService = new TerminalAuthService();
			terminalAuthService.setSslKeysId(testDvca);
			RestrictedIdService restrictedIdService = new RestrictedIdService();
			restrictedIdService.setSslKeysId(testDvca);
			PassiveAuthService passiveAuthService = new PassiveAuthService();
			passiveAuthService.setSslKeysId(testDvca);

			String queryParam = "?service=" + current;
			terminalAuthService.setUrl(taServiceUrl + queryParam);
			restrictedIdService.setUrl(riServiceUrl + queryParam);
			passiveAuthService.setUrl(paServiceUrl + queryParam);

			PkiConnectorConfiguration pkiConnectorConfiguration = createPkiConnectorConfiguration(serverUrl, blackListTrustAnchor, masterListTrustAnchor,
					terminalAuthService, restrictedIdService, passiveAuthService, sslKeysA);

			EPAConnectorConfiguration epaConnectorConfiguration = new EPAConnectorConfiguration();
			epaConnectorConfiguration.setUpdateCVC(true);
			epaConnectorConfiguration.setCvcRefId(current);
			epaConnectorConfiguration.setPkiConnectorConfiguration(pkiConnectorConfiguration);
			epaConnectorConfiguration.setPaosReceiverURL(paosReceiverUrl);
			epaConnectorConfiguration.setHoursRefreshCVCBeforeExpires(1);

			serviceProvider.setEpaConnectorConfiguration(epaConnectorConfiguration);

			serviceProviders.add(serviceProvider);
		}
		poSeIDASConfiguration.setServiceProviders(serviceProviders);

		return poSeIDASConfiguration;
	}

	private PkiConnectorConfiguration createPkiConnectorConfiguration(String serverUrl,
																	  String blackListTrustAnchor,
																	  String masterListTrustAnchor,
																	  TerminalAuthService terminalAuthService,
																	  RestrictedIdService restrictedIdService,
																	  PassiveAuthService passiveAuthService,
																	  SslKeys sslKeys) {
		PkiConnectorConfiguration pkiConnectorConfiguration = new PkiConnectorConfiguration();

		pkiConnectorConfiguration.setBlackListTrustAnchor(blackListTrustAnchor);
		pkiConnectorConfiguration.setMasterListTrustAnchor(masterListTrustAnchor);
		pkiConnectorConfiguration.setAutentServerUrl(serverUrl);
		pkiConnectorConfiguration.setPolicyImplementationId("govDvca");
		pkiConnectorConfiguration.setSslKeys(sslKeys);
		pkiConnectorConfiguration.setTerminalAuthService(terminalAuthService);
		pkiConnectorConfiguration.setRestrictedIdService(restrictedIdService);
		pkiConnectorConfiguration.setPassiveAuthService(passiveAuthService);


		return pkiConnectorConfiguration;
	}

	@Override
	public void deleteCertificates(TestCandidate candidate)
	{
		for (CertificateX509 cert : candidate.getX509Certificates())
		{
			x509DAO.delete(cert);
		}

		for (CertificateCV certificate : candidate.getCvCertificates())
		{
			cvDAO.delete(certificate);
		}

		for (CryptoFile file : candidate.getCryptoFiles())
		{
			cryptoFileDAO.delete(file);
		}

		for (Blacklist blacklist : candidate.getBlacklists())
		{
			blacklistDAO.delete(blacklist);
		}
	}

}
