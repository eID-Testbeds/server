package com.secunet.eidserver.testbed.common.interfaces.entities;

import com.secunet.eidserver.testbed.common.enumerations.ReportType;

public interface Report {
	
	public String getId();
	
	public void setId(String id);
	
	public byte[] getReportData();

	public void setReportData(byte[] reportData);
	
	/**
	 * Set the type for this report. Available types are {@link ReportType}.
	 * @param type
	 */
	public void setReportType(ReportType type);
	
	/**
	 * Returns the type of the report. Available types are {@link ReportType}.
	 * @return
	 */
	public ReportType getReportType();

}
