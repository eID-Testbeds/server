/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.exceptions;

/**
 *
 */
public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 9812319232344L;
    public UserAlreadyExistsException(final String userName) {
        super(String.format("The user %s already exists.", userName));
    }
}
