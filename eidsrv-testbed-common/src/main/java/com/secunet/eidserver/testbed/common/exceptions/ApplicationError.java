package com.secunet.eidserver.testbed.common.exceptions;

/**
 *
 */
public class ApplicationError extends Error {
	private static final long serialVersionUID = 65465465L;
	
    public ApplicationError() {
        super();
    }

    public ApplicationError(final String message) {
        super(message);
    }

    public ApplicationError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ApplicationError(final Throwable cause) {
        super(cause);
    }
}
