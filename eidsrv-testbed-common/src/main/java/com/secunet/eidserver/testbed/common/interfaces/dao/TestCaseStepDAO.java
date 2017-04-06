package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.TestCaseStep;

@Local
public interface TestCaseStepDAO extends GenericDAO<TestCaseStep>
{

	public TestCaseStep findByName(final String name);

}
