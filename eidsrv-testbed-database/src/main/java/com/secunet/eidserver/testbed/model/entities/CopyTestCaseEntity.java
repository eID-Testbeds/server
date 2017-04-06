package com.secunet.eidserver.testbed.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.interfaces.entities.CopyTestCase;
import com.secunet.eidserver.testbed.common.interfaces.entities.DefaultTestCase;

@Entity
@Table(name = "COPY_TEST_CASE")
public class CopyTestCaseEntity extends BaseTestCaseEntity implements CopyTestCase
{
	private static final long serialVersionUID = 1828346670379182725L;

	@JoinColumn(name = "DEFAULT_TEST_CASE_ID", referencedColumnName = "name")
	@ManyToOne(optional = false, targetEntity = DefaultTestCaseEntity.class)
	private DefaultTestCase defaultTestcase;

	@Column(name = "suffix")
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
		result = prime * result + ((defaultTestcase == null) ? 0 : defaultTestcase.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
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
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CopyTestCaseEntity other = (CopyTestCaseEntity) obj;
		if (defaultTestcase == null)
		{
			if (other.defaultTestcase != null)
				return false;
		}
		else if (!defaultTestcase.equals(other.defaultTestcase))
			return false;
		if (suffix == null)
		{
			if (other.suffix != null)
				return false;
		}
		else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}
}