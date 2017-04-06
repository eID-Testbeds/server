package com.secunet.eidserver.testbed.common.interfaces.dao;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Local
public interface UserDAO extends GenericDAO<User>
{

	public User findByName(final String name);
}
