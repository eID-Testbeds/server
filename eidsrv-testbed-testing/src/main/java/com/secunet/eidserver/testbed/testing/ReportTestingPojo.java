package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;
import com.secunet.eidserver.testbed.common.interfaces.entities.Report;

public class ReportTestingPojo extends BaseTestingPojo implements Serializable, Report
{
	private static final long serialVersionUID = 1L;

	private ReportType type;

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

}
