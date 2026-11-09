package com.secunet.eidserver.testbed.common.interfaces.entities;

public interface Blacklist
{

	public byte[] getContent();
	public void setContent(byte[] content);
	public char getEserviceID();
	public void setEserviceID(char eServiceID);
	public String getId();

	public void setId(String id);

}
