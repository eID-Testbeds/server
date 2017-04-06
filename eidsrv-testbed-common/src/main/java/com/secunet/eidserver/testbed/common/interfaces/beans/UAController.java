package com.secunet.eidserver.testbed.common.interfaces.beans;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.exceptions.ApplicationError;
import com.secunet.eidserver.testbed.common.exceptions.UserAlreadyExistsException;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Local
public interface UAController
{
	/**
	 * Get an collection of available roles.
	 */
	public Set<Role> getRoles();

	public Collection<User> getUsers();
	
	/**
	 * Creates an admin account if none exists yet. If an admin does exist no new account will be created.
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public String createAdmin(final String name, final String password);

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
	 */
	public String createUser(final String userName, final String secret, final Role role);

	/**
	 * Delete an account.
	 *
	 * @param userName
	 *            The name of the account to delete.
	 */
	public void deleteUser(final String userName);

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
	public void setSecret(final String userName, final String oldSecret, final String secret);

	/**
	 * Assign the profile with the given id to the user with the given name
	 * 
	 * @param userName
	 * @param profileId
	 * @return
	 */
	public String assignProfile(final String userName, final String profileId);

	public User findUser(String userName);

}
