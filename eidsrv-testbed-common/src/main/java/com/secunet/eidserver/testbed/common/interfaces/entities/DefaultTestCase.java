package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.List;

public interface DefaultTestCase extends TestCase
{

	/**
	 * @return the copies
	 */
	public List<CopyTestCase> getCopies();

	/**
	 * @param copies
	 *            the copies to set
	 */
	public void setCopies(List<CopyTestCase> copies);

	/**
	 * @param copy
	 *            the copy to add
	 */
	public void addCopy(CopyTestCase copy);

}
