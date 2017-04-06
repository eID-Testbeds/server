package com.secunet.eidserver.testbed.common.exceptions;

public class InvalidObjectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	
	public InvalidObjectException( Object object, String reason ){
		super( String.format("Invalid Object [%s]. %s", object.toString(), reason) );
	}
	
	
}
