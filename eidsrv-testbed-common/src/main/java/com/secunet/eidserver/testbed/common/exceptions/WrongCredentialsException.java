package com.secunet.eidserver.testbed.common.exceptions;

public class WrongCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 987492323421134L;
    public WrongCredentialsException() {
        super("Wrong username or password.");
    }
}
