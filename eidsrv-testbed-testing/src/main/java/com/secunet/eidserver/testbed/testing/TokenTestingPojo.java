package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.Date;

import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

public class TokenTestingPojo extends BaseTestingPojo implements Serializable, Token
{
	private static final long serialVersionUID = 1L;

	private String token;

	private Date loginDate;

	private User user;

	public TokenTestingPojo()
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

}