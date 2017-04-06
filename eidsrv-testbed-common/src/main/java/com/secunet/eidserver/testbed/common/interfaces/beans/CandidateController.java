package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.Local;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;

import com.secunet.eidserver.testbed.common.exceptions.CandidateException;
import com.secunet.eidserver.testbed.common.exceptions.ComponentNotInitializedException;
import com.secunet.eidserver.testbed.common.exceptions.TestcaseNotFoundException;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCase;

@Local
public interface CandidateController
{
	/**
	 * Delete the given test candidate
	 * 
	 * @param candidate
	 */
	public void deleteCandidate(TestCandidate canidate);

	/**
	 * Delete a test candidate given it's ID
	 * 
	 * @param id
	 */
	public void deleteCandidateById(String id) throws CandidateException;

	/**
	 * Create a test candidate using the ICS data and the given vendor name
	 * 
	 * @param icsData
	 *            A well-formed XML representation of the ICS
	 * @return
	 * @throws JAXBException
	 */
	public Future<TestCandidate> createCandidate(final String icsData) throws JAXBException, MalformedURLException;

	/**
	 * Update the given profile in the database and simultaneously add the certificates
	 * 
	 * @param profile
	 *            The {@link TestCandidate} which contains all the data about a given candidate. This may have been updates by the user and therefore needs to be persisted.
	 * @throws CandidateException
	 */
	public void saveCandidate(TestCandidate candidate) throws CandidateException;

	/**
	 * Load all testcases for the given profile. Note that this is intended to
	 * be used just by the createProfile method within this class, but due to
	 * EJB restrictions it has to remain public for testing purposes.
	 * 
	 * @param candidate
	 * @throws ComponentNotInitializedException
	 * @throws TestcaseNotFoundException
	 * @return
	 */
	public Set<TestCase> loadRelevantDefaultTestcases(TestCandidate candidate) throws ComponentNotInitializedException, TestcaseNotFoundException;

	/**
	 * Returns the {@link TestCandidate} with the given UUID. In case no such element is found, an {@link EntityNotFoundException} is raised.
	 * 
	 * @param uuid
	 * @return
	 * @throws EntityNotFoundException
	 */
	public TestCandidate getCandidate(String uuid) throws EntityNotFoundException;

	/**
	 * Returns all available test candidates. In case none are found, an empty set is returned.
	 * 
	 * @return
	 */
	public @NotNull Set<TestCandidate> getAllCandidates();

	public boolean isCandidateNameAlreadyTaken(String profileName);

}
