/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.exceptions;

/**
 *
 */
public class InvalidRightsException extends RuntimeException {
	private static final long serialVersionUID = 98749234234L;
    public InvalidRightsException(final String userName) {
        super(String.format("User %s has insufficient rights.", userName));
    }
}
