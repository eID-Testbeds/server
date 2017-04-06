/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.ics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

/**
 * 
 */
public class IcsTest
{
	/**
	 * This test verifies the correct unmarshalling of valid ICS XMLs. Note that this just verifies basic unmarshalling functionality. The correct translation of an entire XML document into objects is tested in @see
	 * com.secunet.eidserver.testbed.controller.CandidateControllerTest#testCreateTestCandidate()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnMarshallIcs() throws Exception
	{
		JAXBContext jc = JAXBContext.newInstance("com.secunet.eidserver.testbed.common.ics");
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<Ics> jaxbIcs = (JAXBElement<Ics>) u.unmarshal(new File("src/test/resources/ics_test.xml"));

		Ics ics = jaxbIcs.getValue();

		// Test an sample of available data.

		assertEquals(2, ics.getAPI().getApiVersionMajor().longValue());
		assertEquals("secunet.com:8445/ecardpaos/paosreceiver", ics.getMetadata().getECardApiUrl());
		assertEquals("secunet AG", ics.getMetadata().getVendor());

		List<Object> ca = ics.getCryptography().getChipAuthentication().getAlgorithmOrDomainParameter();
		assertEquals(ca.size(), 4);
		for (Object c : ca)
		{
			if (c instanceof IcsCa)
			{
				IcsCa conv = (IcsCa) c;
				assertTrue(conv.equals(IcsCa.ID_CA_DH_AES_CBC_CMAC_256) || conv.equals(IcsCa.ID_CA_ECDH_AES_CBC_CMAC_256));
			}
			else if (c instanceof IcsCaDomainparams)
			{
				IcsCaDomainparams conv = (IcsCaDomainparams) c;
				assertTrue(conv.equals(IcsCaDomainparams.MODP_2048_256) || conv.equals(IcsCaDomainparams.BRAINPOOLP_384_R_1));
			}
			else
			{
				fail("Found an unkown object in CA node.");
			}
		}

		List<Object> profiles = ics.getProfiles().getMandatoryProfileOrOptionalProfile();
		assertEquals(4, profiles.size());
		for (Object p : profiles)
		{
			if (p instanceof IcsMandatoryprofile)
			{
				IcsMandatoryprofile conv = (IcsMandatoryprofile) p;
				assertTrue(conv.equals(IcsMandatoryprofile.EAC) || conv.equals(IcsMandatoryprofile.PAOS));
			}
			else if (p instanceof IcsOptionalprofile)
			{
				IcsOptionalprofile conv = (IcsOptionalprofile) p;
				assertTrue(conv.equals(IcsOptionalprofile.SAML) || conv.equals(IcsOptionalprofile.TLS_PSK));
			}
			else
			{
				fail("Found an unkown object in CA node.");
			}
		}
	}
}
