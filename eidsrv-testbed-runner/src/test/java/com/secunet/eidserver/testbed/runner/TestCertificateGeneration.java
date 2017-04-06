package com.secunet.eidserver.testbed.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import com.secunet.testbedutils.bouncycertgen.BouncyCertificateGenerator;
import com.secunet.testbedutils.bouncycertgen.GeneratedCertificate;
import com.secunet.testbedutils.bouncycertgen.xml.x509.CertificateDefinitions;
import com.secunet.testbedutils.utilities.JaxBUtil;

public class TestCertificateGeneration
{
	public static void main(String[] args)
	{
		List<String> xmlFileList = new LinkedList<String>();
		xmlFileList.add("DocumentSignerPKI.xml");
		System.out.println("Beginning x509 certificate generation.");
		List<GeneratedCertificate> certificates = makex509Certificates(xmlFileList);
		System.out.println("Certificate generation complete, writing results...");
		for (GeneratedCertificate genCert : certificates)
		{
			FileWriter certWriter;
			JcaPEMWriter certPEMwriter = null;
			FileWriter keyWriter;
			JcaPEMWriter keyPEMwriter = null;
			try
			{
				// write certificate
//				String certName = genCert.getDefinition().getName() + ".PEM";
//				certWriter = new FileWriter(certName);
//				certPEMwriter = new JcaPEMWriter(certWriter);
//				certPEMwriter.writeObject(genCert.getCertificate());
//				certPEMwriter.flush();
//				certPEMwriter.close();
//				System.out.println("Successfully created certificate " + certName + ".");
				// write certificate
				String certNameDER = genCert.getDefinition().getName() + ".DER";
				writeFile(certNameDER, genCert.getCertificate().getEncoded());
				System.out.println("Successfully created certificate " + certNameDER + ".");
				// write key if necessary
				if (genCert.getDefinition().getKeyFile() == null)
				{
//					String keyName = genCert.getDefinition().getName() + "_privKey.PEM";
//					keyWriter = new FileWriter(keyName);
//					keyPEMwriter = new JcaPEMWriter(keyWriter);
//					keyPEMwriter.writeObject(genCert.getKeyPair().getPrivate());
//					keyPEMwriter.flush();
//					keyPEMwriter.close();
//					System.out.println("Successfully created private key file " + keyName + ".");
					String keyNameDER = genCert.getDefinition().getName() + "_privKey.DER";
					writeFile(keyNameDER, genCert.getKeyPair().getPrivate().getEncoded());
					System.out.println("Successfully created private key file " + keyNameDER + ".");
				}
			}
			catch (IOException | CertificateEncodingException e)
			{
				StringWriter trace = new StringWriter();
				e.printStackTrace(new PrintWriter(trace));
				System.out.println("Writing one of the certificates or keys has failed:" + System.getProperty("line.separator") + trace.toString());
			}
			finally
			{
				try
				{
					if (certPEMwriter != null)
					{
						certPEMwriter.close();
					}
					if (keyPEMwriter != null)
					{
						keyPEMwriter.close();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		System.out.println("Completed x509 certificate generation.");
	}
	
	public static List<GeneratedCertificate> makex509Certificates(List<String> xmlPaths)
	{
		List<GeneratedCertificate> certificates = new ArrayList<GeneratedCertificate>();
		for (String path : xmlPaths)
		{
			File xmlFile = new File(path);
			CertificateDefinitions certificateDefinitions = JaxBUtil.unmarshal(xmlFile, CertificateDefinitions.class);
			certificates.addAll(BouncyCertificateGenerator.getGenerator().makex509Certificate(certificateDefinitions));
		}
		return certificates;
	}
	
	private static byte[] writeFile(String name, byte[] data) throws FileNotFoundException, IOException
	{
	    try(FileOutputStream fos = new FileOutputStream(name))
	    {
		    fos.write(data);
		    fos.close();
	    }
	    return data;
	}
}
