/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secunet.eidserver.testbed.model.database.dao;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import com.secunet.eidserver.testbed.common.interfaces.dao.TokenDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;
import com.secunet.eidserver.testbed.model.entities.TokenEntity;

@Stateless
public class TokenJPA extends GenericJPA<Token> implements TokenDAO
{
	public TokenJPA()
	{
		super("Token");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Token> findAll()
	{
		Collection<Token> col = entityManager.createQuery("SELECT x FROM TokenEntity x").getResultList();
		return (Set<Token>) createSetFromCollection(col);
	}

	@Override
	public Token findByUser(final User user)
	{
		try
		{
			return (Token) entityManager.createQuery("SELECT t FROM TokenEntity t WHERE t.user.id = :id").setParameter("id", user.getId()).getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}
	}

	@Override
	public Token findByToken(final String token)
	{
		try
		{
			return (Token) entityManager.createQuery("SELECT t FROM TokenEntity t WHERE t.token = :token").setParameter("token", token).getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}
	}

	@Override
	public Token createNew()
	{
		Token instance = new TokenEntity();
		return instance;
	}
}
