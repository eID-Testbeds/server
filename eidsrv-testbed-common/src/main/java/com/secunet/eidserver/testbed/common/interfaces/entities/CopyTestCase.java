package com.secunet.eidserver.testbed.common.interfaces.entities;

public interface CopyTestCase extends TestCase {
	
	/**
	 * @return the defaultTestcase
	 */
	public DefaultTestCase getDefaultTestcase();

	/**
	 * This sets the default testcase for the copy and adds a reference from the default test case to the copy
	 * @param defaultTestcase the defaultTestcase to set
	 */
	public void setDefaultTestcase(DefaultTestCase defaultTestcase);

	/**
	 * @return the suffix
	 */
	public String getSuffix();

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix);

}
