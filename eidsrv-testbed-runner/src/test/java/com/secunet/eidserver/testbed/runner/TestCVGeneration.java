package com.secunet.eidserver.testbed.runner;

import java.io.File;
import java.util.ArrayList;

import com.secunet.testbedutils.bouncycertgen.cv.CVCertGen;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;

public class TestCVGeneration
{

	public static void main(String[] args)
	{
		try
		{
			String xmlFile = "CVCertificates.xml";
			CVCertGen gen = new CVCertGen(new File("cv"));
			ArrayList<CVCertificate> certs = gen.generateFromXML(xmlFile, true);
			System.out.println("Generated " + certs.size() + " certs.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
