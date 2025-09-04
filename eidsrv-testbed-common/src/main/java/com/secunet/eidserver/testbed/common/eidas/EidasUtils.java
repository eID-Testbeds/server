/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Copied from eidas middleware
 */

package com.secunet.eidserver.testbed.common.eidas;

import com.secunet.eidserver.testbed.common.classes.Singletons;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.impl.BasicParserPool;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;


/**
 * Helper methods needed for creating and parsing SAML messages.
 *
 * @author TT, hauke
 */
public final class EidasUtils
{
  /**
   * XML feature to disallow doctype declarations
   */
  private static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

  /**
   * XML feature to disallow external general entities
   */
  private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

  private EidasUtils()
  {
    super();
  }

  /**
   * An instance of this class hold a X.509 certificate and its private Key.
   *
   * @author hauke
   */
  public static class X509KeyPair
  {

    private final PrivateKey key;

    private final X509Certificate[] chain;

    public X509KeyPair(PrivateKey key, X509Certificate[] chain)
    {
      this.key = key;
      this.chain = chain;
    }

    private X509KeyPair(PrivateKey key, X509Certificate cert)
    {
      this.key = key;
      this.chain = (cert == null) ? null : new X509Certificate[]{cert};
    }

    /**
     * Returns the private key
     */
    public PrivateKey getKey()
    {
      return key;
    }

    /**
     * Returns the certificate for the private Key
     */
    public X509Certificate getCert()
    {
      return (chain == null || chain.length == 0) ? null : chain[0];
    }

    /**
     * Returns the certificate of the private key and its certificate chain.
     */
    public X509Certificate[] getChain()
    {
      return chain;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(chain);
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null || getClass() != obj.getClass())
      {
        return false;
      }
      X509KeyPair other = (X509KeyPair)obj;
      if (chain == null)
      {
        if (other.chain != null)
        {
          return false;
        }
      }
      else if (!Arrays.equals(chain, other.chain))
      {
        return false;
      }
      if (key == null)
      {
        return other.key == null;
      }
      else
      {
        return key.equals(other.key);
      }
    }

    @Override
    public String toString()
    {
      return "X509KeyPair [key=" + key + ", chain=" + Arrays.toString(chain) + "]";
    }

  }

  /**
   * @param stream PKCS12 File InputStream
   * @param password password to open PKCS12 file and read key
   * @param alias key alias
   * @return KeyPair
   * @throws KeyStoreException When KeyStore does not contain key with supplied alias
   * @throws NoSuchAlgorithmException If there is no algorithm available for that key
   * @throws CertificateException If there are problems with the certificate
   * @throws IOException On IO errors
   * @throws UnrecoverableKeyException If Key cant be read from keystore
   */
  public static X509KeyPair readPKCS12(InputStream stream, char[] password, String alias)
    throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
    UnrecoverableKeyException
  {
    KeyStore p12 = KeyStore.getInstance("pkcs12", Singletons.BC_PROVIDER);
    p12.load(stream, password);
    Enumeration<String> e = p12.aliases();
    PrivateKey key = null;
    X509Certificate cert = null;
    StringBuilder aliasBuf = new StringBuilder();
    while (e.hasMoreElements())
    {
      String currentalias = e.nextElement();
      aliasBuf.append(currentalias);
      aliasBuf.append(" ||| ");
      cert = (X509Certificate)p12.getCertificate(currentalias);
      key = (PrivateKey)p12.getKey(currentalias, password);
      if (key != null && (isNullOrEmpty(alias) || currentalias.equals(alias)))
      {
        // take the first one
        break;
      }
    }
    if (key != null)
    {
      return new X509KeyPair(key, cert);
    }
    else
    {
      throw new KeyStoreException("keystore does not contains alias " + alias + ". Try alias "
                                  + aliasBuf.toString());
    }
  }

  /**
   * Checks if a given string is null or empty.
   *
   * @param s the string to be checked
   * @return true if the string is null or empty, false otherwise
   */
  public static boolean isNullOrEmpty(String s)
  {
    return s == null || s.isEmpty();
  }

  /**
   * Returns an initialized {@link BasicParserPool} ready to use, configured with security features preventing
   * several XXE attacks.
   *
   * @return the parser pool
   * @throws ComponentInitializationException If parserPool cannot be initialized
   */
  public static BasicParserPool getBasicParserPool() throws ComponentInitializationException {
    BasicParserPool ppMgr = new BasicParserPool();
    ppMgr.setNamespaceAware(true);

    final HashMap<String, Boolean> features = new HashMap<>();
    features.put(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    features.put(DISALLOW_DOCTYPE_DECL, true);
    features.put(EXTERNAL_GENERAL_ENTITIES, false);
    features.put("http://xml.org/sax/features/external-parameter-entities", false);
    features.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    ppMgr.setBuilderFeatures(features);
    ppMgr.setXincludeAware(false);
    ppMgr.setExpandEntityReferences(false);

    ppMgr.initialize();
    return ppMgr;
  }
}
