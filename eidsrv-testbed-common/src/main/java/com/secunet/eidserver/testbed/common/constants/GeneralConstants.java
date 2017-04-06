package com.secunet.eidserver.testbed.common.constants;

import com.secunet.eidserver.testbed.common.types.testcase.EService;

public interface GeneralConstants
{
	public final static boolean DEBUG_MODE = false;

	// error codes
	public final static String ELEMENT_NOT_FOUND = "ELEMENT_NOT_FOUND";

	// useragent and version number
	public final static String USER_AGENT_NAME = "TestbedClient";
	public final static int USER_AGENT_MAJOR = 0;
	public final static int USER_AGENT_MINOR = 1;
	public final static int USER_AGENT_SUBMINOR = 0;

	// ca
	public final static String CA_NAME = "Testbed CA";
	public final static String CA_ORGANIZATION_NAME = "secunet AG";
	public final static String CA_ORGANIZATION_UNIT_NAME = "Homeland Security";
	public final static String CA_COUNTRY = "DE";

	// eservice url
	public final static String TESTBED_REFRESH_URL = "https://e-service.secunet.de/refresh";

	// provider-name
	public final static String PROVIDER_NAME = "e-service.secunet.de";

	// http line endings
	public final static String HTTP_LINE_ENDING = "\r\n";

	// certificate names
	// set 1
	public final static String CERT_CV_CVCA_1 = "CERT_CV_CVCA_1";
	public final static String CERT_CV_DV_1_A = "CERT_CV_DV_1_A";
	public final static String CERT_CV_TERM_1_A = "CERT_CV_TERM_1_A";
	public final static String CERT_ECARD_TLS_EIDSERVER_1 = "CERT_ECARD_TLS_EIDSERVER_1";
	public final static String CERT_TLS_SAML_1 = "CERT_TLS_SAML_1";

	// random elements
	public final static String CHALLENGE = "531D7F48EBFF2E30";
	public final static String IDPICC = "2CB7775C2D0D85BEE09A2962F39D8E34FBC5F4AA8FFDB7ABD46A4B41B90E25A8";
	public final static String NONCE = "B7E75E9053DACF69";

	// TLS testing data
	public final static String TLS_STEP_MARKER = "OUT_TLS";
	public final static String TLS_VERSION = "TLS_VERSION";
	public final static String TLS_VERSION_SELECTED = "TLS_SELECTED_VERSION";
	public final static String TLS_CIPHER_SUITES = "TLS_CIPHER_SUITES";
	public final static String TLS_SELECTED_CIPHER_SUITE = "TLS_SELECTED_CIPHER_SUITE";
	public final static String TLS_ELLIPTIC_CURVES = "TLS_ELLIPTIC_CURVES";
	public final static String TLS_SUPPORTED_SIGNATURE_ALGORITHMS = "TLS_SUPPORTED_SIGNATURE_ALGORITHMS";
	public final static String TLS_ALERT_LEVEL_RECEIVED = "TLS_ALERT_LEVEL_RECEIVED";

	// XML Signature
	public final static String XML_SIGNATURE = "XML_SIGNATURE";
	public final static String XML_SIGNATURE_URI = "XML_SIGNATURE_URI";
	public final static String XML_SIGNATURE_CANONICALIZATION = "XML_SIGNATURE_CANONICALIZATION";
	public final static String XML_SIGNATURE_DIGEST = "XML_SIGNATURE_DIGEST";

	// default request counter and document validity
	public final static String DEFAULT_REQUEST_COUNTER = "1";
	public final static String DEFAULT_DOCUMENT_VALIDITY = "valid";

	// datagroup permissions
	public final static String PERMISSION_PROHIBITED = "PROHIBITED";
	public final static String PERMISSION_OPTIONAL = "OPTIONAL";
	public final static String PERMISSION_REQUIRED = "REQUIRED";
	public final static String PERMISSION_ALLOWED = "ALLOWED";

	// requested data prefixes (values will be replaced by defaults)
	public final static String REQUESTED_PREFIX = "USEOP_";
	public final static String ALLOWED_BY_USER_PREFIX = "OABU_";
	public final static String PERSONAL_PREFIX = "PERSONAL_";

	// SAML authnrequest variables
	public final static String SAML_AUTHNREQUEST_ISSUER = "SAML_AUTHNREQUEST_ISSUER";
	public final static String SAML_AUTHNREQUEST_ASSERTION_CONSUMER_SERVICE_URL = "SAML_AUTHNREQUEST_ASSERTION_CONSUMER_SERVICE_URL";
	public final static String SAML_AUTHNREQUEST_EXTENSIONS = "SAML_AUTHNREQUEST_EXTENSIONS";
	public final static String SAML_AUTHNREQUEST_ENCRYPTED_AUTHN_REQUEST_EXTENSION = "SAML_AUTHNREQUEST_ENCRYPTED_AUTHN_REQUEST_EXTENSION";

	// SAML encryption algorithms
	public final static String SAML_ENCRYPTION_BLOCK_CIPHER = "SAML_ENCRYPTION_BLOCK_CIPHER";
	public final static String SAML_ENCRYPTION_KEY_TRANSPORT = "SAML_ENCRYPTION_KEY_TRANSPORT";

	// default comparison values
	public final static String AGE_COMPARISON_VALUE = "18";
	public final static String PLACE_COMPARISON_VALUE = "0276420420";

	public final static String ENCRYPTION_SUFFIX = "_ENC";

	public final static String MANIPULATED_SUFFIX = "_MANIPULATED";

	// default eservice
	public final static EService DEFAULT_ESERVICE = EService.A;

}
