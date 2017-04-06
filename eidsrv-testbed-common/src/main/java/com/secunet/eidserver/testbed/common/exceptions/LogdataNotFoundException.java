package com.secunet.eidserver.testbed.common.exceptions;

public class LogdataNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 3640456709655146364L;

	public LogdataNotFoundException(long profileId) {
		super("No log data has been found for the profile with the id " + profileId);
	}
	
	public LogdataNotFoundException(String message) {
		super(message);
	}

}
