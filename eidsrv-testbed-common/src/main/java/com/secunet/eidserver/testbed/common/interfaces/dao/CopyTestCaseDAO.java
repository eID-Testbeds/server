package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;

@Local
public interface CopyTestCaseDAO extends GenericDAO<CopyTestCase>
{

	public CopyTestCase findByName(final String name);

	public int elementCount();

}
