package com.secunet.eidserver.testbed.runner;

import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.saml.common.SAMLObject;

/**
 * SAML 2.0 Extensions
 */
public interface AuthnRequestExtension extends SAMLObject, ElementExtensibleXMLObject
{

	/** Local name, no namespace */
	public final static String LOCAL_NAME = "AuthnRequestExtension";
}