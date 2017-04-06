package com.secunet.eidserver.testbed.report;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.interfaces.beans.ReportGenerator;
import com.secunet.eidserver.testbed.common.interfaces.entities.Log;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;
import com.secunet.eidserver.testbed.testing.DefaultTestCaseTestingPojo;
import com.secunet.eidserver.testbed.testing.LogMessageTestingPojo;
import com.secunet.eidserver.testbed.testing.LogTestingPojo;
import com.secunet.eidserver.testbed.testing.TestCandidateTestingPojo;

public class ReportGeneratorTest
{

	Set<Log> logs;
	ReportGenerator generator;
	TestCandidate candidate;
	final String tmpDir = System.getProperty("java.io.tmpdir");

	@BeforeTest
	public void setUp()
	{

		logs = new HashSet<Log>();
		for (int i = 0; i < 4; i++)
		{
			Log log = new LogTestingPojo();
			TestCase tc = new DefaultTestCaseTestingPojo();
			tc.setName("Some Testcase");
			log.setTestCase(tc.getName());
			log.setRunDate(new Date());
			List<LogMessage> messages = new ArrayList<LogMessage>();
			for (int j = 0; j < i; j++)
			{
				LogMessage message = new LogMessageTestingPojo();
				message.setMessage(
						"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
				message.setSuccess(true);
				messages.add(message);
			}
			log.setLogMessages(messages);
			logs.add(log);
		}

		generator = new ReportGeneratorBean();

		candidate = new TestCandidateTestingPojo();
		candidate.setCandidateName("ProfileName-JUnit");
		try
		{
			candidate.setEcardapiUrl(new URL("https://candidate.url"));
			candidate.setSamlUrl(new URL("https://saml.url"));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		candidate.setApiMajor(1);
		candidate.setApiMinor(0);
		candidate.setApiSubminor(0);
		candidate.setVersionMajor(1);
		candidate.setVersionMinor(0);
		candidate.setVersionSubminor(0);

		HashSet<IcsMandatoryprofile> mandatory = new HashSet<IcsMandatoryprofile>();
		mandatory.add(IcsMandatoryprofile.CRYPTO);
		mandatory.add(IcsMandatoryprofile.PAOS);
		candidate.setMandatoryProfiles(mandatory);
		// check null issues:
		candidate.setOptionalProfiles(null);

	}

	@Test
	public void generatePdfReport() throws Exception
	{

		byte[] bytes = generator.generateReport(candidate, logs, ReportType.PDF);
		assertNotNull("Report-byte[] null", bytes);
		assertTrue("Report-byte[] empty", bytes.length > 0);


		// export it!
		File pdfFile = new File(tmpDir + "/JasperReport_pdf.pdf");
		FileOutputStream fos = new FileOutputStream(pdfFile);
		fos.write(bytes);
		fos.close();

	}

	@Test
	public void generateDocxReport() throws Exception
	{

		byte[] bytes = generator.generateReport(candidate, logs, ReportType.DOCX);
		assertNotNull("Report-byte[] null", bytes);
		assertTrue("Report-byte[] empty", bytes.length > 0);


		// export it!
		File pdfFile = new File(tmpDir + "/JasperReport_docx.docx");
		FileOutputStream fos = new FileOutputStream(pdfFile);
		fos.write(bytes);
		fos.close();

	}

}
