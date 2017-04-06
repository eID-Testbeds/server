package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.exceptions.TestrunException;
import com.secunet.eidserver.testbed.common.interfaces.entities.LogMessage;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;

@Local
public interface Runner
{

	/**
	 * Run the given testcase using the data provided by the profile.
	 * 
	 * @param profile
	 * @param tCase
	 * @return
	 */
	public List<LogMessage> runTest(TestCandidate profile, TestCase tCase) throws TestrunException;


	/**
	 * Returns a map containing all static certificates
	 * 
	 * @return {@link Map} Certificate map containing the relative paths and names as keys of type {@link String} and the certificates themselves as {@link byte}[]
	 * @throws IOException
	 */
	public Map<String, byte[]> getStaticCertificatesAndKeys() throws IOException;

}
