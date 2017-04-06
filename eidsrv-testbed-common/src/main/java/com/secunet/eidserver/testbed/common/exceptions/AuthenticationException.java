/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.exceptions;

/**
 *
 */
public class AuthenticationException extends RuntimeException {
	private final static long serialVersionUID = 37825478L;
    public AuthenticationException(final String userName) {
        super(String.format("Authentication failed for user %s", userName));
    }
}
