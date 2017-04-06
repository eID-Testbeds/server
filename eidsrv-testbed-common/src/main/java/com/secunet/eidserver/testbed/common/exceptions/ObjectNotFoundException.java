package com.secunet.eidserver.testbed.common.exceptions;

public class ObjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public ObjectNotFoundException( Class type, String name ){
		super( String.format("Object not found [type = %s, name = %s]", type.getSimpleName(), name) );
	}
	
	@SuppressWarnings("rawtypes")
	public ObjectNotFoundException( Class type, long id ){
		super( String.format("Object not found [type = %s, id = %d]", type.getSimpleName(), id) );
	}
	
	
}
