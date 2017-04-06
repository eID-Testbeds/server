package com.secunet.eidserver.testbed.report.data;

public class LogsReportObject {
	
	private String id;	
	private String testCaseName;	
	private String logMessage;
	private String logStatus;
	
	
	public String getId() {
		return id;
	}
	public void setId(String testCaseId) {
		this.id = testCaseId;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	public String getLogMessage() {
		return logMessage;
	}
	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}
	public String getLogStatus() {
		return logStatus;
	}
	public void setLogStatus(String logStatus) {
		this.logStatus = logStatus;
	}
	
	
	
}
