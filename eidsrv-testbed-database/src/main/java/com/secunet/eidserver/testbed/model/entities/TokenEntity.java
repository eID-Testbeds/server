package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Entity
@Table(name = "TOKEN")
@NamedQuery(name = "TokenEntity.findAll", query = "SELECT t FROM TokenEntity t")
public class TokenEntity extends BaseEntity implements Serializable, Token
{
	private static final long serialVersionUID = 1L;

	@Column(name = "TOKEN")
	private String token;

	@Column(name = "LOGIN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date loginDate;

	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = UserEntity.class)
	@JoinColumn(name = "USER")
	private User user;

	public TokenEntity()
	{
	}

	@Override
	public String getToken()
	{
		return this.token;
	}

	@Override
	public void setToken(String token)
	{
		this.token = token;
	}

	@Override
	public User getUser()
	{
		return this.user;
	}

	@Override
	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public Date getLoginDate()
	{
		return loginDate;
	}

	@Override
	public void setLoginDate(Date loginDate)
	{
		this.loginDate = loginDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * ((loginDate == null) ? 0 : loginDate.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenEntity other = (TokenEntity) obj;
		if (loginDate == null)
		{
			if (other.loginDate != null)
				return false;
		}
		else if (!loginDate.equals(other.loginDate))
			return false;
		if (token == null)
		{
			if (other.token != null)
				return false;
		}
		else if (!token.equals(other.token))
			return false;
		if (user == null)
		{
			if (other.user != null)
				return false;
		}
		else if (!user.equals(other.user))
			return false;
		return true;
	}

}