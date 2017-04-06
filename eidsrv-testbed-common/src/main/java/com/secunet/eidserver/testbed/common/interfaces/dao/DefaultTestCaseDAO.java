package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;

@Local
public interface DefaultTestCaseDAO extends GenericDAO<DefaultTestCase>
{

	public DefaultTestCase findByName(final String name);

	public int elementCount();

}
