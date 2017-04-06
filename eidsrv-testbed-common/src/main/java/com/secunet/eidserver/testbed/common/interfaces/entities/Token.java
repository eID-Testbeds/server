package com.secunet.eidserver.testbed.common.interfaces.entities;

import java.util.Date;

public interface Token {
	
	public String getId();
	
	public void setId(String id);
	
	public String getToken();

	public void setToken(String token);

	public User getUser();

	public void setUser(User user);

	public Date getLoginDate();

	public void setLoginDate(Date loginDate);

}
