package com.secunet.eidserver.testbed.common.helper;

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;

public class BcTlsHelperTest
{

	@Test
	public void testConvertCipherSuiteSetToIntArray() throws Exception
	{

		Set<IcsTlsCiphersuite> cipherSuites = new LinkedHashSet<IcsTlsCiphersuite>();
		cipherSuites.addAll(Arrays.asList(IcsTlsCiphersuite.values()));

		int[] result = BcTlsHelper.convertCipherSuiteSetToIntArray(cipherSuites);

		assertTrue(result.length > 0);
	}

}
