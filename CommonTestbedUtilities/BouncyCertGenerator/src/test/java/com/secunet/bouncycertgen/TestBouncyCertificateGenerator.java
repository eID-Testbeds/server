package com.secunet.bouncycertgen;

import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.testng.annotations.Test;

import com.secunet.testbedutils.bouncycertgen.BouncyCertificateGenerator;
import com.secunet.testbedutils.bouncycertgen.CertificateGenerator;
import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.xml.x509.AltNameType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinition;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinitions;
import com.secunet.testbedutils.bouncycertgen.xml.x509.DnType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.ExtensionsType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.GeneralNameTypeType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.KeyUsageType;
import com.secunet.testbedutils.bouncycertgen.xml.x509.GeneralNamesType.GeneralName;

public class TestBouncyCertificateGenerator
{

	@Test(enabled = false)
	public void testCreateX509Selfsigned() throws Exception
	{
		CertificateDefinitions definitions = new CertificateDefinitions();
		CertificateDefinition definition = new CertificateDefinition();
		ExtensionsType extensions = new ExtensionsType();
		KeyUsageType keyUsageType = new KeyUsageType();
		keyUsageType.setKeyAgreement(true);
		extensions.setKeyUsage(keyUsageType);
		AltNameType subjectAltName = new AltNameType();
		GeneralName name = new GeneralName();
		name.setType(GeneralNameTypeType.D_NS_NAME);
		name.setValue("https://some.domain");
		subjectAltName.getGeneralName().add(name);
		extensions.setSubjectAltName(subjectAltName);
		definition.setExtensions(extensions);
		DnType issuer = new DnType();
		issuer.setCommonName("UnitIssuer");
		issuer.setCountry("DE");
		issuer.setOrganization("Vendor");
		issuer.setOrganizationalUnit("");
		definition.setIssuer(issuer);
		DnType subject = new DnType();
		subject.setCommonName("UnitSubject");
		subject.setCountry("DE");
		subject.setOrganization("Vendor");
		subject.setOrganizationalUnit("");
		definition.setName("TLS_ECARD_API");
		definition.setSubject(subject);

		definitions.getCertificateDefinition().add(definition);

		// generate
		CertificateGenerator generator = new BouncyCertificateGenerator();
		List<GeneratedCertificate> certificates = generator.makex509Certificate(definitions);
		assertNotNull(certificates);
	}

}
