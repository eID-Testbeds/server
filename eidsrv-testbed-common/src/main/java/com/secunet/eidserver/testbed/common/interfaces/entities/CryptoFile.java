package com.secunet.eidserver.testbed.common.interfaces.entities;

public interface CryptoFile
{

	public byte[] getContent();
	public void setContent(byte[] content);
	public String getFilename();
	public void setFilename(String filename);
	public String getId();
	public void setId(String id);

}
