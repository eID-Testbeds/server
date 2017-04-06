/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.common.exceptions;


public class ReportException extends RuntimeException {
	
	private static final long serialVersionUID = -1L;
    
	public ReportException(final String msg) {
        super(String.format("Failed generating report: %s", msg));
    }
	
}
