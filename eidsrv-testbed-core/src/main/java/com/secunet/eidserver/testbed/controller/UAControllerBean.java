package com.secunet.eidserver.testbed.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.ApplicationError;
import com.secunet.eidserver.testbed.common.exceptions.ProfileNotFoundException;
import com.secunet.eidserver.testbed.common.exceptions.UserAlreadyExistsException;
import com.secunet.eidserver.testbed.common.exceptions.UserNotFoundException;
import com.secunet.eidserver.testbed.common.exceptions.WrongCredentialsException;
import com.secunet.eidserver.testbed.common.interfaces.beans.UAController;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.UserDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Stateless
public class UAControllerBean implements UAController
{
	private static final Logger CLASS_LOGGER = Logger.getLogger(UAControllerBean.class.getName());

	@EJB
	private UserDAO userDAO;

	@EJB
	private TestCandidateDAO testCandidateDAO;

	/**
	 * Package visibility only for the JUnit test.
	 * 
	 * @param testCandidateDAO
	 *            The TestCandidateDAO
	 */
	void setTestProfileDAO(TestCandidateDAO testCandidateDAO)
	{
		this.testCandidateDAO = testCandidateDAO;
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

	@PostConstruct
	public void init()
	{
		CLASS_LOGGER.log(Level.INFO, "UAController initialization: {0}", toString());
	}

	@PreDestroy
	public void destroy()
	{
		CLASS_LOGGER.log(Level.INFO, "UAController shutdown: {0}", toString());
	}

	/**
	 * Get an collection of available roles.
	 */
	@Override
	public Set<Role> getRoles()
	{
		Set<Role> roleSet = new HashSet<Role>();
		for (Role r : Role.values())
		{
			roleSet.add(r);
		}
		return roleSet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<User> getUsers()
	{
		return (Collection<User>) userDAO.findAll();
	}

	/**
	 * Creates an admin account if none exists yet. If an admin does exist no new account will be created.
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	@Override
	public String createAdmin(final String name, final String password)
	{
		@SuppressWarnings("unchecked")
		Collection<User> users = (Collection<User>) userDAO.findAll();
		if (users != null && users.size() > 0)
		{
			Iterator<User> userIter = users.iterator();
			while (userIter.hasNext())
			{
				User u = userIter.next();
				if (Role.ADMIN.equals(u.getRole()))
				{
					throw new ApplicationError("An administrator does already exist in the system.");
				}
			}
		}
		return createUser(name, password, Role.ADMIN);
	}

	/**
	 * Create a new user.
	 *
	 * @param userName
	 *            The name of the account.
	 * @param secret
	 *            The secret oft the account (Pure!
	 * @param roleName
	 *            Not Hashed).
	 * 
	 * @throws ApplicationError
	 * @throws UserAlreadyExistsException
	 * 
	 * @return {@link String} The result of the operation
	 */
	@Override
	public String createUser(final String userName, final String secret, final Role role)
	{
		if (null != userDAO.findByName(userName))
		{
			CLASS_LOGGER.warning(String.format("Create user failed. User already present: %s", userName));
			throw new UserAlreadyExistsException(userName);
		}

		final User user = userDAO.createNew();
		user.setName(userName);

		user.setRole(role);

		try
		{
			user.setPwdHash(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(secret.getBytes())).toString(16));
		}
		catch (NoSuchAlgorithmException e)
		{
			CLASS_LOGGER.severe(String.format("Could not create hashed secret: %s", e.getMessage()));
			throw new ApplicationError("Could not create hashed secret.", e);
		}

		userDAO.persist(user);

		return "User " + userName + " was succesfully created.";
	}

	/**
	 * Delete an account.
	 *
	 * @param userName
	 *            The name of the account to delete.
	 */
	@Override
	public void deleteUser(final String userName)
	{
		final User user;
		if (null == (user = userDAO.findByName(userName)))
		{
			CLASS_LOGGER.warning(String.format("Delete account failed. Account not found: %s", userName));
			throw new UserNotFoundException(userName);
		}

		userDAO.delete(user);
	}

	/**
	 * Set the secret of the account.
	 *
	 * @param userName
	 *            The name of the account to modify.
	 * @param oldSecret
	 *            The old password of the user.
	 * @param secret
	 *            The secret for the account (Pure! Not Hashed).
	 */
	@Override
	public void setSecret(final String userName, final String oldSecret, final String secret)
	{
		final User user;
		if (null == (user = userDAO.findByName(userName)))
		{
			CLASS_LOGGER.warning(String.format("Change secret failed. Account not found: %s", userName));
			throw new UserNotFoundException(userName);
		}

		// verify old password
		try
		{
			if (!user.getPwdHash().equals(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(oldSecret.getBytes())).toString(16)))
			{
				throw new WrongCredentialsException();
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			CLASS_LOGGER.severe(String.format("Could not verify hashed secret: %s", e.getMessage()));
			throw new ApplicationError("Could not create hashed secret.", e);
		}

		// set new password
		try
		{
			user.setPwdHash(new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(secret.getBytes())).toString(16));
		}
		catch (NoSuchAlgorithmException e)
		{
			CLASS_LOGGER.severe(String.format("Could not create hashed secret: %s", e.getMessage()));
			throw new ApplicationError("Could not create hashed secret.", e);
		}

		userDAO.update(user);
	}

	/**
	 * Assign the profile with the given id to the user with the given name
	 * 
	 * @param userName
	 * @param profileId
	 * @return
	 */
	@Override
	public String assignProfile(final String userName, final String profileId)
	{
		final User user;
		if (null == (user = userDAO.findByName(userName)))
		{
			CLASS_LOGGER.warning(String.format("Assigning the profile failed. Account not found: %s", userName));
			throw new UserNotFoundException(userName);
		}

		final TestCandidate profile;
		if (null == (profile = testCandidateDAO.findById(profileId)))
		{
			CLASS_LOGGER.warning(String.format("Assigning the profile failed. Profile not found: %d", profileId));
			throw new ProfileNotFoundException(profileId);
		}

		Set<TestCandidate> profiles = user.getProfiles();
		profiles.add(profile);
		user.setProfiles(profiles);
		userDAO.update(user);
		return "The TestProfile with ID " + profileId + " has been assigned to the User " + userName;
	}

	@Override
	public String toString()
	{
		return "UAController{" + "userDAO=" + userDAO + '}';
	}

	@Override
	public User findUser(String userName)
	{
		return userDAO.findByName(userName);
	}
}
