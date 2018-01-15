/**To change this license header,choose License Headers in Project Properties.*To change this template file,choose Tools|Templates*and open the template in the editor.*/
package com.secunet.eidserver.testbed.controller;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.concurrent.Future;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.interfaces.beans.TestGenerator;
import com.secunet.eidserver.testbed.common.interfaces.dao.DefaultTestCaseDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCaseStepDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TlsClientCertificateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TlsDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyAgreementDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyTransportDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlSignatureAlgorithmDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.TlsClientCertificate;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.testing.TestCandidateTestingPojo;
import com.secunet.eidserver.testbed.testing.TlsClientCertificateTestingPojo;
import com.secunet.eidserver.testbed.testing.TlsTestingTestingPojo;
import com.secunet.eidserver.testbed.testing.XmlEncryptionKeyAgreementTestingPojo;
import com.secunet.eidserver.testbed.testing.XmlEncryptionKeyTransportTestingPojo;
import com.secunet.eidserver.testbed.testing.XmlSignatureTestingPojo;

/****/

public class CandidateControllerTest
{
	@InjectMocks
	private CandidateControllerBean candidateController = new CandidateControllerBean();
	@Mock
	private TestGenerator generatorMock;
	@Mock
	private TestCandidateDAO testCandidateDAOMock;
	@Mock
	private DefaultTestCaseDAO defaultTestcaseDAOMock;
	@Mock
	private XmlSignatureAlgorithmDAO xmlSignatureAlgorithmDAOMock;
	@Mock
	private XmlEncryptionKeyAgreementDAO xmlEncryptionKeyAgreementDAOmock;
	@Mock
	private XmlEncryptionKeyTransportDAO xmlEncryptionKeyTransportDAOmock;
	@Mock
	private TlsDAO tlsDAOMock;
	@Mock
	private TlsClientCertificateDAO tlsClientCertificateDAOmock;
	@Mock
	private TestCaseStepDAO testcaseStepDAOMock;

	@BeforeClass
	private void initMocks() throws CertificateEncodingException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException,
			TransformerFactoryConfigurationError, TransformerException
	{
		// mocks themselves
		MockitoAnnotations.initMocks(this);

		// empty POJOs
		TestCandidate emptyCandidate = new TestCandidateTestingPojo();
		when(testCandidateDAOMock.createNew()).thenReturn(emptyCandidate);
		Tls emptyTls = new TlsTestingTestingPojo();
		when(tlsDAOMock.createNew()).thenReturn(emptyTls);
		TlsClientCertificate emptyTlsClientCertificate = new TlsClientCertificateTestingPojo();
		when(tlsClientCertificateDAOmock.createNew()).thenReturn(emptyTlsClientCertificate);
		XmlSignature emptyXmlSignature = new XmlSignatureTestingPojo();
		when(xmlSignatureAlgorithmDAOMock.createNew()).thenReturn(emptyXmlSignature);
		XmlEncryptionKeyAgreement emptyXmlKeyAgreement = new XmlEncryptionKeyAgreementTestingPojo();
		when(xmlEncryptionKeyAgreementDAOmock.createNew()).thenReturn(emptyXmlKeyAgreement);
		XmlEncryptionKeyTransport emptyXmlKeyTransport = new XmlEncryptionKeyTransportTestingPojo();
		when(xmlEncryptionKeyTransportDAOmock.createNew()).thenReturn(emptyXmlKeyTransport);
	}

	/**
	 * This test verifies the correct creation of a test candidate from an ICS XML file. Only the first layer of data is tested, e.g. no verificiation of cryptographic subelements takes place. For the creation of an entire candidate with all its
	 * elements, refer to the integration test.
	 *
	 * @throws Exception
	 */
	@Test(enabled = true)
	public void testCreateTestCandidate() throws Exception
	{
		// read xml
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./src/test/resources/ics_test.xml"), "UTF-8"));
		String xmlData = new String();
		try
		{
			String line;
			while ((line = in.readLine()) != null)
			{
				xmlData = xmlData.concat(line);
			}
		}
		finally
		{
			in.close();
		}

		Future<TestCandidate> resultFuture = candidateController.createCandidate(xmlData);
		TestCandidate result = resultFuture.get();

		assertNotNull(result);
		// metadata
		assertEquals("https://secunet.com:8445/ecardpaos/paosreceiver", result.getEcardapiUrl().toString());
		assertEquals("https://secunet.com:8443/eID-Server/eID", result.getEidinterfaceUrl().toString());
		assertEquals("https://secunet.com:8443/saml/async", result.getSamlUrl().toString());
		assertEquals("secunet AG", result.getVendor());
		assertEquals("secunet eID-Server", result.getCandidateName());
		assertEquals(0, result.getVersionMajor());
		assertEquals(5, result.getVersionMinor());
		// api
		assertEquals(2, result.getApiMajor());
		assertEquals(0, result.getApiMinor());
		assertEquals(1, result.getApiSubminor());
		// profiles
		boolean containsMandatory = true;
		assertEquals(2, result.getMandatoryProfiles().size());
		for (IcsMandatoryprofile mProfile : result.getMandatoryProfiles())
		{
			containsMandatory &= (mProfile.equals(IcsMandatoryprofile.EAC) || mProfile.equals(IcsMandatoryprofile.PAOS));
		}
		assertEquals(true, containsMandatory);
		boolean containsOptional = true;
		assertEquals(2, result.getOptionalProfiles().size());
		for (IcsOptionalprofile oProfile : result.getOptionalProfiles())
		{
			containsOptional &= (oProfile.equals(IcsOptionalprofile.TLS_PSK) || oProfile.equals(IcsOptionalprofile.SAML));
		}
		assertEquals(true, containsOptional);

		// cryptography
		// eCard-API
		assertNotNull(result.getTlsEcardApiPsk());
		assertNotNull(result.getTlsEcardApiAttached());

		// eID-Interface
		// TLS
		assertNotNull(result.getTlsEidInterface());
		// signature algorithms
		assertNotNull(result.getXmlSignatureAlgorithmsEid());

		// SAML-Profile
		// TLS
		assertNotNull(result.getTlsSaml());
		// signature algorithms
		assertNotNull(result.getXmlSignatureAlgorithmsSaml());

		// xml encryption
		assertNotNull(result.getXmlEncryptionAlgorithms());

		// CA
		assertNotNull(result.getChipAuthenticationAlgorithms());
		assertEquals(2, result.getChipAuthenticationAlgorithms().size());
		assertTrue(result.getChipAuthenticationAlgorithms().contains(IcsCa.ID_CA_DH_AES_CBC_CMAC_256) && result.getChipAuthenticationAlgorithms().contains(IcsCa.ID_CA_ECDH_AES_CBC_CMAC_256));
	}
}
