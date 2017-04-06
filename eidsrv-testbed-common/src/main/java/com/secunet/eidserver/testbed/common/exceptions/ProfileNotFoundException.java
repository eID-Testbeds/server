package com.secunet.eidserver.testbed.common.exceptions;

public class ProfileNotFoundException extends RuntimeException {
	private final static long serialVersionUID = 7232554338523L;

	public ProfileNotFoundException(final String profileId) {
		super(String.format("Could not find profile %s.", profileId));
	}
}