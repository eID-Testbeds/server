package com.secunet.eidserver.testbed.common.classes;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Implements a naive trust manager that bypasses all the SSL certificate validation checks.
 */
public class NaiveTrustManager implements X509TrustManager {

    /**
     * Bypasses the check for client certificate trust. This method does not perform
     * any validation, allowing all client certificates to be trusted.
     *
     * @param x509Certificates the client certificates.
     * @param authType the authentication type based on the client certificate.
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) {
        // intentionally left blank to trust all client certificates
    }

    /**
     * Bypasses the check for server certificate trust. This method does not perform
     * any validation, allowing all server certificates to be trusted. It's designed
     * to accept self-signed certificates or certificates from non-trusted CAs.
     *
     * @param x509Certificates the server certificates.
     * @param authType the key exchange algorithm used.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) {
        // intentionally left blank to trust all server certificates
    }

    /**
     * Returns an empty array of certificate authority certificates which are trusted
     * for authenticating peers. This implementation returns an empty array as it
     * bypasses all the checks.
     *
     * @return an empty array of X509Certificate.
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}