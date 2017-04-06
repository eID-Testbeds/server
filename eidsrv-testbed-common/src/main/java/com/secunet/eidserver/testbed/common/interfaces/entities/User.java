package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.Set;

import com.secunet.eidserver.testbed.common.enumerations.Role;

public interface User {
	
	public String getId();

	public void setId(String id);

	public String getName();

	public void setName(String name);

	public String getPwdHash();

	public void setPwdHash(String pwdHash);

	public Role getRole();

	public void setRole(Role role);

	public Token getToken();

	public void setToken(Token token);

	public Set<TestCandidate> getProfiles();
	
	public void setProfiles(Set<TestCandidate> profiles);
}
