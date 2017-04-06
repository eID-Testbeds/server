/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.exceptions;

/**
 *
 */
public class UserNotFoundException extends RuntimeException {
	private final static long serialVersionUID = 7234782438523L;
    public UserNotFoundException(final String userName) {
        super(String.format("Could not find user %s.", userName));
    }     
}
