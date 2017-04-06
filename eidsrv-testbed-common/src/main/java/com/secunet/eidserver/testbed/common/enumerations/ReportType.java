package com.secunet.eidserver.testbed.common.enumerations;

public enum ReportType {
	PDF(1),
	DOCX(2);
	
	private final int type;
	
	private ReportType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public static ReportType createUsingValue(int type) {
		// note: int cant be null!
		for(ReportType bl: ReportType.values()) {
			if(type == bl.getType()) {
				return bl;
			}
		}
		return null;
	}

}
