package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;

@Entity
@Table(name = "REPORT")
@NamedQuery(name = "ReportEntity.findAll", query = "SELECT t FROM ReportEntity t")
public class ReportEntity extends BaseEntity implements Serializable, Report
{
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	private ReportType type;

	@Lob
	@Column(length = 500000)
	private byte[] reportData;

	@Override
	public byte[] getReportData()
	{
		return reportData;
	}

	@Override
	public void setReportData(byte[] reportData)
	{
		this.reportData = reportData;
	}

	@Override
	public void setReportType(ReportType type)
	{
		this.type = type;
	}

	@Override
	public ReportType getReportType()
	{
		return type;
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
		int result = prime * Arrays.hashCode(reportData);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		ReportEntity other = (ReportEntity) obj;
		if (!Arrays.equals(reportData, other.reportData))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
