package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.Set;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;

@Local
public interface TestGenerator
{

	/**
	 * Generate the set of default testcases
	 * 
	 */
	public Set<DefaultTestCase> generateDefaultTestcases();

}
