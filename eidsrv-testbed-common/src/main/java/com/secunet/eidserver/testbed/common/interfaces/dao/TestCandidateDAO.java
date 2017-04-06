/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;

@Local
public interface TestCandidateDAO extends GenericDAO<TestCandidate>
{
	/**
	 * // * Find a {@link TestCandidate} using its unique profile name.
	 * // * @param profileName
	 * // * @return
	 * //
	 */
	// public TestCandidate findByProfileName(String profileName);


	public boolean isCandidateNameAlreadyTaken(String candidateName);
}
