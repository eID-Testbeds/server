package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateEncodingException;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.exceptions.CertificateGenerationExcpetion;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;


@Local
public interface CertificateController
{

	/**
	 * Upload a base64-encoded x509 certificate to the database
	 * 
	 * @param profileId
	 * @param name
	 * @param base64data
	 * @return
	 */
	public String uploadX509(String profileId, String name, String base64data);

	/**
	 * Generates all certificates needed for the candidate.
	 * 
	 * @param candidate
	 *            {@link TestCandidate} The candidate for which the certificates shall be generated
	 * @return
	 * @throws CertificateGenerationExcpetion
	 * @throws MalformedURLException
	 */
	public Set<CertificateX509> generateCertificates(TestCandidate candidate) throws CertificateGenerationExcpetion, MalformedURLException;

	/**
	 * Asynchronously return a zip file containing all certificates for the given instance.
	 * 
	 * @param candidate
	 *            The candidate for which the certificates have been created
	 * @return {@link byte}[] certificates
	 * @throws IOException
	 * @throws CertificateEncodingException
	 */
	public Future<byte[]> downloadCertificates(TestCandidate candidate) throws IOException, CertificateEncodingException;

	/**
	 * Delete the x509 certificates created for the given candidate.
	 * 
	 * @param candidate
	 */
	public void deleteCertificates(TestCandidate candidate);

}
