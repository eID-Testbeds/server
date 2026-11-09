package com.secunet.eidserver.testbed.common.interfaces.beans;

import com.secunet.eidserver.testbed.common.classes.GeneratedCertsContainer;
import com.secunet.eidserver.testbed.common.exceptions.CertificateGenerationExcpetion;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

import javax.ejb.Local;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.Future;


@Local
public interface CertificateController
{

	/**
     * Generates all certificates needed for the candidate.
     *
     * @param candidate {@link TestCandidate} The candidate for which the certificates shall be generated
     * @return
     * @throws CertificateGenerationExcpetion
     * @throws MalformedURLException
     */
	public GeneratedCertsContainer generateCertificates(TestCandidate candidate) throws CertificateGenerationExcpetion, IOException;

	/**[
	 * Asynchronously return a zip file containing all certificates for the given instance.
	 * 
	 * @param candidate
	 *            The candidate for which the certificates have been created
	 * @return {@link Future}<byte[]> Byte array of certificates
	 * @throws IOException
	 * @throws CertificateEncodingException
	 */
	public Future<byte[]> downloadCertificates(TestCandidate candidate) throws IOException, CertificateEncodingException;

	/**
	 * Retrieves the metadata associated with an entity in byte array format.
	 * This method marshals the EntityDescriptor to an XML Element, transforms it to a byte array,
	 * and handles any exceptions that might occur during the process.
	 *
	 * @return A byte array containing the metadata in XML format, or null if an error occurs.
	 */
    byte[] getMetadataBytes(TestCandidate candidate);

	/**
	 * Serializes a POSeIDAS configuration related to a specific test candidate to XML format and returns it as a byte array.
	 * Utilizes JAXB for marshalling the configuration object into XML. Handles JAXBException for XML marshalling issues
	 * and a generic Exception for any other unexpected errors.
	 *
	 * @param candidate The test candidate for which the POSeIDAS configuration is retrieved and serialized.
	 * @return A byte array containing the serialized XML data of the POSeIDAS configuration, or an empty byte array if an error occurs.
	 */
	byte[] getPOSeIDASBytes(TestCandidate candidate);

	/**
	 * Serializes a new EIDAS middleware configuration related to a specific test candidate to XML format and returns it as a byte array.
	 * Utilizes JAXB for marshalling the configuration object into XML. Handles JAXBException for XML marshalling issues
	 * and a generic Exception for any other unexpected errors.
	 *
	 * @param candidate The test candidate for which the new EIDAS middleware configuration is retrieved and serialized.
	 * @return A byte array containing the serialized XML data of the new EIDAS middleware configuration, or an empty byte array if an error occurs.
	 */
	byte[] getNewEidasMiddlewareConfigBytes(TestCandidate candidate);

    /**
	 * Delete the x509 certificates created for the given candidate.
	 * 
	 * @param candidate
	 */
	public void deleteCertificates(TestCandidate candidate);

}
