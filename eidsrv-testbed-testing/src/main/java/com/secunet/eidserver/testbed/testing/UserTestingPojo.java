package com.secunet.eidserver.testbed.testing;

import java.io.Serializable;
import java.util.Set;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

public class UserTestingPojo extends BaseTestingPojo implements Serializable, User
{
	private static final long serialVersionUID = 1L;

	private String name;

	private String pwdHash;

	private Role role;

	private Token token;

	private Set<TestCandidate> testProfiles;

	public UserTestingPojo()
	{
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getPwdHash()
	{
		return this.pwdHash;
	}

	@Override
	public void setPwdHash(String pwdHash)
	{
		this.pwdHash = pwdHash;
	}

	@Override
	public Role getRole()
	{
		return this.role;
	}

	@Override
	public void setRole(Role role)
	{
		this.role = role;
	}

	@Override
	public Token getToken()
	{
		return token;
	}

	@Override
	public void setToken(Token token)
	{
		this.token = token;
	}

	@Override
	public Set<TestCandidate> getProfiles()
	{
		return this.testProfiles;
	}

	@Override
	public void setProfiles(Set<TestCandidate> profiles)
	{
		this.testProfiles = profiles;
	}

}