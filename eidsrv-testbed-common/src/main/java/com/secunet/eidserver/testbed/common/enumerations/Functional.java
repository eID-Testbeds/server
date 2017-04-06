package com.secunet.eidserver.testbed.common.enumerations;

public enum Functional
{
	CHARLENGTH, CREATE_SOAP_SECURITY_HEADER, CREATE_SAML, CREATE_SAML_NO_SIGALG_NO_SIGNATURE, CREATE_SAML_NO_SIGNATURE, UUID, CHAT, AUTHENTICATION_TOKEN, APDU;

	public static final String BEGIN_TEXTMARK = "[[";
	public static final String END_TEXTMARK = "]]";

	public String getTextMark()
	{
		return BEGIN_TEXTMARK + this.name() + END_TEXTMARK;
	}
}
