package com.secunet.eidserver.testbed.common.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public ObjectAlreadyExistsException( Class type, String data ){
		super( String.format("Object already exists [type = %s, data = %s]", type.getSimpleName(), data) );
	}
	
}
