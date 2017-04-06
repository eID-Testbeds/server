package com.secunet.eidserver.testbed.model.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;

@Entity
@Table(name = "DEFAULT_TEST_CASE")
public class DefaultTestCaseEntity extends BaseTestCaseEntity implements DefaultTestCase
{
	private static final long serialVersionUID = 3787020989600978533L;

	@OneToMany(mappedBy = "defaultTestcase", targetEntity = CopyTestCaseEntity.class)
	private List<CopyTestCase> copies = new ArrayList<>();

	/**
	 * @return the copies
	 */
	@Override
	public List<CopyTestCase> getCopies()
	{
		return copies;
	}

	/**
	 * @param copies
	 *            the copies to set
	 */
	@Override
	public void setCopies(List<CopyTestCase> copies)
	{
		this.copies = copies;
	}

	/**
	 * @param copy
	 *            the copy to add
	 */
	@Override
	public void addCopy(CopyTestCase copy)
	{
		if (this.copies == null)
		{
			copies = new ArrayList<>();
		}
		copies.add(copy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultTestCaseEntity other = (DefaultTestCaseEntity) obj;
		if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}

}