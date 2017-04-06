package com.secunet.testbedutils.bouncycertgen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;

import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.bouncycertgen.x509.X509CertificateFactory;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinition;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinitions;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class BouncyCertificateGenerator implements CertificateGenerator
{

	private static final Logger logger = LogManager.getRootLogger();
	private static BouncyCertificateGenerator generator = null;

	/**
	 * @see com.secunet.testbedutils.bouncycertgen.CertificateGenerator#makex509Certificates(List<String>)
	 */
	@Override
	public List<GeneratedCertificate> makex509Certificates(List<String> xmlPaths)
	{
		List<GeneratedCertificate> certificates = new ArrayList<>();
		// note: each XML may contain an infinite number of certificate
		// definitions
		for (String path : xmlPaths)
		{
			certificates.addAll(makex509Certificate(path));
		}
		return certificates;
	}

	/**
	 * @see com.secunet.testbedutils.bouncycertgen.CertificateGenerator#makex509Certificate(CertificateDefinitions)
	 */
	@Override
	public List<GeneratedCertificate> makex509Certificate(CertificateDefinitions definitions)
	{
		List<GeneratedCertificate> certificates = new ArrayList<>();
		for (CertificateDefinition certificateDefinition : definitions.getCertificateDefinition())
		{
			// get signer certificate and key pair from list
			GeneratedCertificate certSigner = null;
			X500Name issuerName = new X500Name(X509CertificateFactory.buildIssuerOrSubjectString(certificateDefinition, true));
			for (GeneratedCertificate signer : certificates)
			{
				X500Name issuerSubjectName = new X500Name(signer.getCertificate().getSubjectX500Principal().getName());
				if (issuerSubjectName.equals(issuerName))
				{
					certSigner = signer;
					break;
				}
			}

			// create certificate holder
			GeneratedCertificate generatedCertificate = X509CertificateFactory.createX509(certificateDefinition, certSigner);
			certificates.add(generatedCertificate);
		}
		return certificates;
	}

	/**
	 * @see com.secunet.testbedutils.bouncycertgen.CertificateGenerator#makex509Certificate(String)
	 */
	@Override
	public List<GeneratedCertificate> makex509Certificate(String definition)
	{
		// note: each XML may contain an infinite number of certificate
		// definitions
		CertificateDefinitions certificateDefinitions = JaxBUtil.unmarshal(definition, CertificateDefinitions.class);
		return makex509Certificate(certificateDefinitions);
	}

	/**
	 * @see com.secunet.testbedutils.bouncycertgen.CertificateGenerator#readFromFileSystem(String)
	 */
	@Override
	public X509Certificate readFromFileSystem(String path)
	{
		CertificateFactory certFactory;
		try
		{
			certFactory = CertificateFactory.getInstance("X.509", "BC");
			InputStream stream = new FileInputStream(path);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(stream);
			return cert;
		}
		catch (CertificateException | NoSuchProviderException e)
		{
			logger.error("Could not read x509 certificate: " + e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			StringWriter trace = new StringWriter();
			e.printStackTrace(new PrintWriter(trace));
			logger.error("The certificate could not be loaded from file the file " + path + ":" + System.getProperty("line.separator") + trace.toString());
		}
		return null;
	}

	/**
	 * @see com.secunet.testbedutils.bouncycertgen.CertificateGenerator#makeCVCertifcates(String)
	 */
	@Override
	public List<CVCertificate> makeCVCertifcates(String xmlPath)
	{
		CVCertGen gen = new CVCertGen();
		List<CVCertificate> certList = null;
		// Check XML file
		if (gen.checkXML(xmlPath))
		{
			try
			{
				certList = gen.generateFromXML(xmlPath, true);
			}
			catch (Exception e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				logger.error("The CV certificate(s) could not be generated:" + System.getProperty("line.separator") + trace.toString());
			}
		}
		return certList;
	}

	/**
	 * Return the generator instance
	 *
	 * @return
	 */
	public static BouncyCertificateGenerator getGenerator()
	{
		if (generator == null)
		{
			generator = new BouncyCertificateGenerator();
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		}
		return generator;
	}

}
