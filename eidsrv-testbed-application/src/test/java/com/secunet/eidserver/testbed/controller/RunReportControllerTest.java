/*
package com.secunet.eidserver.testbed.controller;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.constants.GeneralConstants;
import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestProfile;
import com.secunet.eidserver.testbed.dao.ReportDAO;
import com.secunet.eidserver.testbed.dao.TestProfileDAO;
import com.secunet.eidserver.testbed.testhelper.ContainerProvider;

public class RunReportControllerTest {
	private TestProfileDAO testProfileDAO;
	private String profileId;
	private String name;
	private final String tmpDir = System.getProperty("java.io.tmpdir");

	@BeforeClass
	public void setup() throws Exception {
		testProfileDAO = (TestProfileDAO) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/TestProfileDAO");
	}

	@AfterClass
	public void teardown() throws Exception {
		// nothing now
	}

	@Test(enabled = false)
	public void testConnect() throws Exception {
		// create the profile for the test instance
		final ProfileControllerBean profileController = (ProfileControllerBean) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/ProfileController");
		assertNotNull(profileController);
		// ics
		String xmlData = getFileContentAsString("./src/test/resources/ics_test.xml");
		assertTrue(xmlData.length() > 0);
		// xml encryption, if available
		String samlEnc = getFileContentAsString("./src/test/resources/saml_enc_base64.txt");
		// xml signature, if available
		String samlSig = getFileContentAsString("./src/test/resources/saml_sig_base64.txt");
		
		// create profile
		TestProfile profileToTest = profileController.createProfile(xmlData);
		profileId = profileToTest.getId();
		name = profileToTest.getVendor() + "_" + profileToTest.getName();
		// save certificates, if available
		if(samlEnc != null && samlEnc.length() > 0 && samlSig != null && samlSig.length() > 0) {
			final CertificateControllerBean certificateController = (CertificateControllerBean) ContainerProvider.getContainer().getContext()
					.lookup("java:global/classes/CertificateController");
			certificateController.uploadX509(profileId, "SAML_ENC", samlEnc);
			certificateController.uploadX509(profileId, "SAML_SIG", samlSig);
		}
		// read back to get the updated test profile containing the certificates
		profileToTest = testProfileDAO.findById(profileId);
		
		// run the test
		final RunControllerBean runController = (RunControllerBean) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/RunController");
		int amountOfCertificates =  profileToTest.getX509Certificates().size();
		runController.runAll(profileId);
		
		// read the profile back from the database
		TestProfile readProfile = testProfileDAO.findById(profileId);
		
		// check if logs have been written
		assertNotNull(readProfile);
		assertNotNull(readProfile.getLogs());
		assertTrue(readProfile.getLogs().size() > 0);
		// check if new certificates have been found
		assertTrue(amountOfCertificates < readProfile.getX509Certificates().size());
		assertNotNull(readProfile.getCvCertificates());
		assertTrue(readProfile.getCvCertificates().size() > 0);
	}
	
	@Test(enabled = false, dependsOnMethods = "testConnect")
	public void testReport() throws Exception {
		// create reports
		final ReportControllerBean reportController = (ReportControllerBean) ContainerProvider.getContainer().getContext()
				.lookup("java:global/classes/ReportController");
		assertNotNull(reportController);
		String pdfResult = reportController.createReport(profileId, 1);
		assertTrue(pdfResult != GeneralConstants.ELEMENT_NOT_FOUND);
		String docxResult = reportController.createReport(profileId, 2);
		assertTrue(pdfResult != GeneralConstants.ELEMENT_NOT_FOUND);
		
		// read reports from the db and write them to a file
		final ReportDAO reportDAO;
		reportDAO = (ReportDAO) ContainerProvider.getContainer().getContext()
					.lookup("java:global/classes/ReportDAO");
		
		File pdfFile = new File(tmpDir + "/" + name + ".pdf");
		FileOutputStream fos = new FileOutputStream(pdfFile);
		Report pdfEntity = reportDAO.findById(pdfResult);
		assertNotNull(pdfEntity);
		assertNotNull(pdfEntity.getReportData());
		assertTrue(pdfEntity.getReportType() == ReportType.PDF);
		fos.write(pdfEntity.getReportData());
		fos.close();
		
		File docxFile = new File(tmpDir + "/" + name + ".docx");
		fos = new FileOutputStream(docxFile);
		Report docxEntity = reportDAO.findById(docxResult);
		assertNotNull(docxEntity);
		assertNotNull(docxEntity.getReportData());
		assertTrue(docxEntity.getReportType() == ReportType.DOCX);
		fos.write(docxEntity.getReportData());
		fos.close();
	}
	
	private String getFileContentAsString(String path) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		String data = new String();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				data = data.concat(line);
			}
		} finally {
			br.close();
		}
		return data;
	}

}
*/
