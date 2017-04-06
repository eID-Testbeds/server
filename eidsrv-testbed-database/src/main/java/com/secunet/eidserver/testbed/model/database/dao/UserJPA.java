package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import com.secunet.eidserver.testbed.common.interfaces.dao.UserDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;
import com.secunet.eidserver.testbed.model.entities.UserEntity;

@Stateless
public class UserJPA extends GenericJPA<User> implements UserDAO
{
	public UserJPA()
	{
		super("User");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<User> findAll()
	{
		Collection<User> col = entityManager.createQuery("SELECT x FROM UserEntity x").getResultList();
		return (Set<User>) createSetFromCollection(col);
	}

	@Override
	public User findByName(final String name)
	{
		try
		{
			return (User) entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.name = :name").setParameter("name", name).getSingleResult();
		}
		catch (NoResultException nre)
		{
			return null;
		}
	}

	@Override
	public User createNew()
	{
		User instance = new UserEntity();
		return instance;
	}

}
