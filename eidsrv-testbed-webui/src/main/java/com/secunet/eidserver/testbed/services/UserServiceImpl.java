/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.services;

import java.util.Set;

import javax.ejb.EJB;
import javax.jws.WebService;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.InvalidRightsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.beans.UAController;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

/**
 * 
 */
@WebService(endpointInterface = "com.secunet.eidserver.testbed.services.UserService")
public class UserServiceImpl implements UserService
{
	@EJB
	private UAController uac;

	@EJB
	private AuthenticationController authenticationController;

	@Override
	public Set<Role> getRoles()
	{
		return uac.getRoles();
	}

	@Override
	public String createAdmin(String userName, String password)
	{
		return uac.createAdmin(userName, password);
	}

	@Override
	public String createUser(String userName, String secret, Role role, String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return uac.createUser(userName, secret, role);
	}

	@Override
	public String assignProfile(final String userName, final String profileId, final String token)
	{
		final Token authToken = authenticationController.findToken(token);
		if (Role.ADMIN != authToken.getUser().getRole() && Role.PROFILE_CREATOR != authToken.getUser().getRole())
			throw new InvalidRightsException(authToken.getUser().getName());

		return uac.assignProfile(userName, profileId);
	}

	@Override
	public String login(final String userName, final String password)
	{
		return authenticationController.authenticateUser(userName, password);
	}

}
