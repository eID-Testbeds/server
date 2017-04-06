/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.services;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.secunet.eidserver.testbed.common.enumerations.Role;

/**
 *  
 */
@WebService
public interface UserService
{
	@WebMethod
	Set<Role> getRoles();

	@WebMethod
	String createAdmin(final String name, final String password);

	@WebMethod
	String createUser(final String userName, final String secret, final Role role, final String token);

	@WebMethod
	String assignProfile(final String userName, final String profileId, final String token);

	@WebMethod
	String login(final String userName, final String password);
}
