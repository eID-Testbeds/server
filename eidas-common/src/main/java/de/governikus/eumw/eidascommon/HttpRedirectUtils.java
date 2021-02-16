/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.eidascommon;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.xml.bind.DatatypeConverter;


/**
 * Definitions and methods needed to implement HTTP redirect binding.
 * @author TT
 */
public final class HttpRedirectUtils
{

  private HttpRedirectUtils()
  {
    // Nothing
  }

  /**
   * name of HTTP request parameter holding the SAML request
   */
  public static final String REQUEST_PARAMNAME = "SAMLRequest";

  /**
   * name of HTTP request/response parameter holding the SAML response
   */
  public static final String RESPONSE_PARAMNAME = "SAMLResponse";

  /**
   * name of HTTP request parameter holding the signature algorithm
   */
  private static final String SIGALG_PARAMNAME = "SigAlg";

  /**
   * name of HTTP request parameter holding the relay state
   */
  public static final String RELAYSTATE_PARAMNAME = "RelayState";

  /**
   * name of HTTP request parameter holding the signature value
   */
  private static final String SIGVALUE_PARAMNAME = "Signature";

  /* The following algorithm names are from http://www.w3.org/TR/xmlsec-algorithms/ */
  /**
   * Value to specify algorithm SHA256WithRSA. Using this value is not quite legal by SAML specification but
   * using any legal value is not safe by BSI specification.
   */
  private static final String SIGALG_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

  /**
   * In the past (before 3.7.1.0) we had a wrong constant here, this is the old wrong version
   *
   * @Deprecated use {@link #SIGALG_RSA_SHA256}.
   */
  @Deprecated
  private static final String SIGALG_RSA_SHA256_OLD_WRONG = "http://www.w3.org/2001/04/xmldsig-more/rsa-sha256";

  /**
   * Value to specify algorithm SHA1WithRSA. This value must be supported by SAML specification but is
   * considered as insecure by BSI.
   */
  private static final String SIGALG_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

  /**
   * Value to specify algorithm SHA256WithDSA.
   */
  private static final String SIGALG_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";

  /**
   * Value to specify algorithm SHA1WithDSA.
   */
  private static final String SIGALG_DSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";

  /**
   * Value to specify algorithm SHA512WithECDSA.
   */
  private static final String SIGALG_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";

  /**
   * Value to specify algorithm SHA384WithECDSA.
   */
  private static final String SIGALG_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";

  /**
   * Value to specify algorithm SHA256WithECDSA.
   */
  private static final String SIGALG_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";

  /**
   * Value to specify algorithm SHA1WithECDSA.
   */
  private static final String SIGALG_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";

  /**
   * Name of HTTP request parameter identifying the request as referencing to a former SAML request. If this
   * parameter is given, all other parameters are ignored.
   */
  public static final String REFERENCE_PARAMNAME = "refID";



  /**
   * deflate and base64-encode the input, making it effectively almost by a third longer.
   */
  public static String deflate(byte[] input)
  {
    byte[] output = new byte[2 * input.length];
    Deflater compresser = new Deflater(3, true);
    compresser.setInput(input);
    compresser.finish();
    int compressedDataLength = compresser.deflate(output);
    byte[] result = new byte[compressedDataLength];
    System.arraycopy(output, 0, result, 0, compressedDataLength);
    return DatatypeConverter.printBase64Binary(result);
  }

  /**
   * reverse method for {@link #deflate(byte[])}
   *
   * @throws DataFormatException
   */
  public static byte[] inflate(String input) throws DataFormatException
  {
    byte[] b = DatatypeConverter.parseBase64Binary(input);
    Inflater decompessor = new Inflater(true);
    decompessor.setInput(b);
    byte[] output = new byte[10 * b.length];
    int length = decompessor.inflate(output);
    byte[] result = new byte[length];
    System.arraycopy(output, 0, result, 0, length);
    return result;
  }

  /**
   * Builds a query string, using default JCE provider
   *
   * @param url URL of the identity provider
   * @param data SAML request or response data, not encoded or deflated yet
   * @param isRequest true to indicate that data is a SAML request
   * @param relayState transparent value which is returned unchanged with the response
   * @param sigKey key to sign the data with
   * @param digestAlg Hash algorithm used for parameter signature SHA256 or SHA1
   * @return complete query string
   * @throws UnsupportedEncodingException
   * @throws GeneralSecurityException
   * @throws MalformedURLException
   */
  public static String createQueryString(String url,
                                         byte[] data,
                                         boolean isRequest,
                                         String relayState,
                                         PrivateKey sigKey,
                                         String digestAlg)
    throws UnsupportedEncodingException, GeneralSecurityException, MalformedURLException
  {
    return createQueryString(url, data, isRequest, relayState, sigKey, digestAlg, null, true);
  }

  /**
   * Builds a query string
   *
   * @param url URL of the identity provider
   * @param data SAML request or response data, not encoded or deflated yet
   * @param relayState transparent value which is returned unchanged with the response
   * @param isRequest true to indicate that data is a SAML request
   * @param sigKey key to sign the data with
   * @param digestAlg Hash algorithm used for parameter signature SHA256 or SHA1
   * @param provider name of the JCE provider to use
   * @param hot flag indicating if server is in hot state
   * @return complete query string
   * @throws UnsupportedEncodingException
   * @throws GeneralSecurityException
   * @throws MalformedURLException
   */
  private static String createQueryString(String url,
                                         byte[] data,
                                         boolean isRequest,
                                         String relayState,
                                         PrivateKey sigKey,
                                         String digestAlg,
                                         String provider,
                                         boolean hot)
    throws UnsupportedEncodingException, GeneralSecurityException, MalformedURLException
  {
    StringBuilder result = new StringBuilder();
    appendParam(result, true, (isRequest ? REQUEST_PARAMNAME : RESPONSE_PARAMNAME), deflate(data));
    if (relayState != null)
    {
      appendParam(result, false, RELAYSTATE_PARAMNAME, relayState);
    }
    if (sigKey != null)
    {
      String algoName = sigKey.getAlgorithm();
      String alg = null;
      String sigAlg = null;
      if ("RSA".equalsIgnoreCase(algoName))
      {
        alg = "SHA1".equals(digestAlg) ? SIGALG_RSA_SHA1 : SIGALG_RSA_SHA256;
        sigAlg = digestAlg + "WithRSA";
      }
      else if ("DSA".equalsIgnoreCase(algoName))
      {
        alg = "SHA1".equals(digestAlg) ? SIGALG_DSA_SHA1 : SIGALG_DSA_SHA256;
        sigAlg = digestAlg + "WithDSA";
      }
      else if ("EC".equalsIgnoreCase(algoName) || "ECDSA".equalsIgnoreCase(algoName))
      {
        if ("SHA1".equals(digestAlg) || "SHA-1".equals(digestAlg))
        {
          alg = SIGALG_ECDSA_SHA1;
        }
        else if ("SHA256".equals(digestAlg) || "SHA-256".equals(digestAlg))
        {
          alg = SIGALG_ECDSA_SHA256;
        }
        else if ("SHA384".equals(digestAlg) || "SHA-384".equals(digestAlg))
        {
          alg = SIGALG_ECDSA_SHA384;
        }
        else if ("SHA512".equals(digestAlg) || "SHA-512".equals(digestAlg))
        {
          alg = SIGALG_ECDSA_SHA512;
        }
        else
        {
          throw new GeneralSecurityException("Given digest algorithm " + digestAlg + " not supported");
        }
        sigAlg = digestAlg + "WithECDSA";
      }
      else
      {
        throw new GeneralSecurityException("Unsupported signature algorithm: " + algoName);
      }
      appendParam(result, false, SIGALG_PARAMNAME, alg);
      Signature sig = provider == null ? Signature.getInstance(sigAlg)
        : Signature.getInstance(sigAlg, provider);
      sig.initSign(sigKey);
      sig.update(result.toString().getBytes(Utils.ENCODING));
      byte[] value = sig.sign();
      appendParam(result, false, SIGVALUE_PARAMNAME, DatatypeConverter.printBase64Binary(value));
      if (!hot)
      {
        appendParam(result, false, "notHot", "true");
      }
    }

    URL forwardURL = new URL(url);
    if (forwardURL.getQuery() == null || forwardURL.getQuery().isEmpty())
    {
      return url + "?" + result.toString();
    }
    else
    {
      return url + "&" + result.toString();
    }
  }

  private static void appendParam(StringBuilder result, boolean isFirst, String name, String value)
    throws UnsupportedEncodingException
  {
    if (!isFirst)
    {
      result.append('&');
    }
    result.append(name);
    result.append('=');
    result.append(URLEncoder.encode(value, Utils.ENCODING));
  }
}
