package com.secunet.eidserver.testbed.common.exceptions;

public class RIPubKeyRestoreException extends RuntimeException {
	private static final long serialVersionUID = -4751644098339633849L;
	
	public RIPubKeyRestoreException(String message) {
		super(message);
	}
	
	public RIPubKeyRestoreException() {
		super("Could not restore the servers public key for Restricted Identification");
	}

}
