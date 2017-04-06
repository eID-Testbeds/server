package com.secunet.eidserver.testbed.runner;

import org.opensaml.common.SAMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * SAML 2.0 Extensions
 */
public interface AuthnRequestExtension extends SAMLObject, ElementExtensibleXMLObject
{

	/** Local name, no namespace */
	public final static String LOCAL_NAME = "AuthnRequestExtension";
}