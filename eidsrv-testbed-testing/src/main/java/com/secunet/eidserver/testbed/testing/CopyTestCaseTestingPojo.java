package com.secunet.eidserver.testbed.testing;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;

public class CopyTestCaseTestingPojo extends BaseTestCaseTestingPojo implements CopyTestCase
{
	private static final long serialVersionUID = 1828346670379182725L;

	private DefaultTestCase defaultTestcase;

	private String suffix;

	/**
	 * @return the defaultTestcase
	 */
	@Override
	public DefaultTestCase getDefaultTestcase()
	{
		return defaultTestcase;
	}

	/**
	 * This sets the default testcase for the copy and adds a reference from the default test case to the copy
	 * 
	 * @param defaultTestcase
	 *            the defaultTestcase to set
	 */
	@Override
	public void setDefaultTestcase(DefaultTestCase defaultTestcase)
	{
		this.defaultTestcase = defaultTestcase;
		this.defaultTestcase.addCopy(this);
	}

	/**
	 * @return the suffix
	 */
	@Override
	public String getSuffix()
	{
		return suffix;
	}

	/**
	 * @param suffix
	 *            the suffix to set
	 */
	@Override
	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
}