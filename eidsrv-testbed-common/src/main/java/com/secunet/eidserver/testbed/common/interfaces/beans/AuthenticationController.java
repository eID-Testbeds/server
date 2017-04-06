package com.secunet.eidserver.testbed.common.interfaces.beans;

import javax.ejb.Local;

import com.secunet.eidserver.testbed.common.interfaces.entities.Token;

@Local
public interface AuthenticationController
{
	/**
	 * 
	 * @param token
	 * @return
	 */
	public Token findToken(final String token);
    
	/**
	 * Authenticate the user using his secret. Returns a token that has to be used during subsequent API calls. Tokens are valid for two days.
	 * @param userName {@link String} The name of the user to authenticate 
	 * @param secret {@link String} The secret of the user to authenticate
	 * @return {@link String} The token for the user.
	 */
    public String authenticateUser(final String userName, final String secret);

}
