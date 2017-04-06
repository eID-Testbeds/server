/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.exceptions.ApplicationError;
import com.secunet.eidserver.testbed.common.exceptions.AuthenticationException;
import com.secunet.eidserver.testbed.common.exceptions.UserNotFoundException;
import com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController;
import com.secunet.eidserver.testbed.common.interfaces.dao.TokenDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.UserDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Stateless
public class AuthenticationControllerBean implements AuthenticationController
{
	private final Logger CLASS_LOGGER = Logger.getLogger(AuthenticationControllerBean.class.getName());
	private static final long TOKEN_LIFETIME = 432000000; // 5 days

	@EJB
	private TokenDAO tokenDAO;

	@EJB
	private UserDAO userDAO;

	/**
	 * Package visibility only for the JUnit test.
	 * 
	 * @param tokenDAO
	 *            The TokenDAO
	 */
	void setTokenDAO(TokenDAO tokenDAO)
	{
		this.tokenDAO = tokenDAO;
	}

	/**
	 * Package visibility only for the JUnit test.
	 * 
	 * @param userDAO
	 *            The UserDAO
	 */
	void setUserDAO(UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	@Override
	public Token findToken(final String token)
	{
		return tokenDAO.findByToken(token);
	}

	/**
	 * @see com.secunet.eidserver.testbed.common.interfaces.beans.AuthenticationController#authenticateUser(String, String)
	 */
	@Override
	public String authenticateUser(final String userName, final String secret)
	{
		User user = userDAO.findByName(userName);

		if (null == user)
			throw new UserNotFoundException(userName);

		final Token oldToken = tokenDAO.findByUser(user);

		if (null != oldToken)
		{
			final long loginDate = oldToken.getLoginDate().getTime();
			final long currentDate = new Date().getTime();

			if (loginDate + TOKEN_LIFETIME > currentDate)
			{
				return oldToken.getToken();
			}
			else
			{
				user.setToken(null);
				userDAO.update(user);
				tokenDAO.delete(oldToken);
			}
		}

		try
		{
			if (0 != user.getPwdHash().compareToIgnoreCase(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(secret.getBytes())).toString(16)))
			{
				throw new AuthenticationException(userName);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			CLASS_LOGGER.severe(String.format("Could not create hashed secret: %s", e.getMessage()));
			throw new ApplicationError("Could not create hashed secret.", e);
		}

		final Date logindate = new Date();

		final String tbh = user.getName() + user.getPwdHash() + logindate.toString();
		final Token token = tokenDAO.createNew();
		token.setLoginDate(logindate);
		token.setUser(user);

		try
		{
			token.setToken(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(tbh.getBytes())).toString(16));
		}
		catch (NoSuchAlgorithmException e)
		{
			CLASS_LOGGER.severe(String.format("Could not create hashed identifier: %s", e.getMessage()));
			throw new ApplicationError("Could not create hashed identifier.", e);
		}

		user.setToken(token);
		userDAO.update(user);
		tokenDAO.persist(token);

		return token.getToken();
	}
}
