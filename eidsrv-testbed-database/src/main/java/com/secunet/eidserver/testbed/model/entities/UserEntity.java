package com.secunet.eidserver.testbed.model.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.secunet.eidserver.testbed.common.enumerations.Role;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Token;
import com.secunet.eidserver.testbed.common.interfaces.entities.User;

@Entity
@Table(name = "TESTBED_USER")
@NamedQuery(name = "UserEntity.findAll", query = "SELECT u FROM UserEntity u")
public class UserEntity extends BaseEntity implements Serializable, User
{
	private static final long serialVersionUID = 1L;

	private String name;

	@Column(name = "PWD_HASH")
	private String pwdHash;

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.REMOVE, targetEntity = TokenEntity.class)
	private Token token;

	@ManyToMany(targetEntity = TestCandidateEntity.class)
	@JoinTable(name = "USER_TESTPROFILES", joinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "TESTPROFILE_ID", referencedColumnName = "ID") })
	private Set<TestCandidate> testProfiles;

	public UserEntity()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = prime * ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		UserEntity other = (UserEntity) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (role != other.role)
			return false;
		return true;
	}

}