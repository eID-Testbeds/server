package com.secunet.eidserver.testbed.common.eidas;

/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Class which implements SAML2-specific options for {@link EncryptedElementType} objects.
 *
 * <p>
 * For information on other parameters and options, and general XML Encryption issues,
 * see {@link org.opensaml.xmlsec.encryption.support.Decrypter}.
 * </p>
 */
public class OuterDecrypter extends InnerDecrypter {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(OuterDecrypter.class);

    /**
     * Constructor.
     *
     * @param params decryption parameters to use
     */
    public OuterDecrypter(final DecryptionParameters params) {
        super(params);
    }

    /**
     * Constructor.
     *
     * @param newResolver resolver for data encryption keys.
     * @param newKEKResolver resolver for key encryption keys.
     * @param newEncKeyResolver resolver for EncryptedKey elements
     */
    public OuterDecrypter(@Nullable final KeyInfoCredentialResolver newResolver,
                     @Nullable final KeyInfoCredentialResolver newKEKResolver,
                     @Nullable final EncryptedKeyResolver newEncKeyResolver) {

        super(newResolver, newKEKResolver, newEncKeyResolver, null, null);
    }

    /**
     * Constructor.
     *
     * @param newResolver resolver for data encryption keys.
     * @param newKEKResolver resolver for key encryption keys.
     * @param newEncKeyResolver resolver for EncryptedKey elements
     * @param includeAlgos collection of included algorithm URIs
     * @param excludeAlgos collection of excluded algorithm URIs
     */
    public OuterDecrypter(@Nullable final KeyInfoCredentialResolver newResolver,
                     @Nullable final KeyInfoCredentialResolver newKEKResolver,
                     @Nullable final EncryptedKeyResolver newEncKeyResolver,
                     @Nullable final Collection<String> includeAlgos,
                     @Nullable final Collection<String> excludeAlgos) {

        super(newResolver, newKEKResolver, newEncKeyResolver, includeAlgos, excludeAlgos);
    }

    /**
     * Decrypt the specified EncryptedAssertion.
     *
     * @param encryptedAssertion the EncryptedAssertion to decrypt
     * @return an Assertion
     * @throws DecryptionException thrown when decryption generates an error
     */
    public Assertion decrypt(@Nonnull final EncryptedAssertion encryptedAssertion) throws DecryptionException {
        final SAMLObject samlObject = decryptData(encryptedAssertion);
        if (! (samlObject instanceof Assertion)) {
            throw new DecryptionException("Decrypted SAMLObject was not an instance of Assertion");
        }
        return (Assertion) samlObject;
    }

    /**
     * Decrypt the specified EncryptedAttribute.
     *
     * @param encryptedAttribute the EncryptedAttribute to decrypt
     * @return an Attribute
     * @throws DecryptionException thrown when decryption generates an error
     */
    public Attribute decrypt(@Nonnull final EncryptedAttribute encryptedAttribute) throws DecryptionException {
        final SAMLObject samlObject = decryptData(encryptedAttribute);
        if (! (samlObject instanceof Attribute)) {
            throw new DecryptionException("Decrypted SAMLObject was not an instance of Attribute");
        }
        return (Attribute) samlObject;
    }

    /**
     * Decrypt the specified EncryptedID.
     *
     * <p>
     * Note that an EncryptedID can contain a NameID, an Assertion
     * or a BaseID.  It is up to the caller to determine the type of
     * the resulting SAMLObject.
     * </p>
     *
     * @param encryptedID the EncryptedID to decrypt
     * @return an XMLObject
     * @throws DecryptionException thrown when decryption generates an error
     */
    public SAMLObject decrypt(@Nonnull final EncryptedID encryptedID) throws DecryptionException {
        return decryptData(encryptedID);
    }


    /**
     * Decrypt the specified NewEncryptedID.
     *
     * @param newEncryptedID the NewEncryptedID to decrypt
     * @return a NewID
     * @throws DecryptionException thrown when decryption generates an error
     */
    public NewID decrypt(@Nonnull final NewEncryptedID newEncryptedID) throws DecryptionException {
        final SAMLObject samlObject = decryptData(newEncryptedID);
        if (! (samlObject instanceof NewID)) {
            throw new DecryptionException("Decrypted SAMLObject was not an instance of NewID");
        }
        return (NewID) samlObject;
    }

    /**
     * Decrypt the specified instance of EncryptedElementType, and return it as an instance
     * of the specified QName.
     *
     *
     * @param encElement the EncryptedElementType to decrypt
     * @return the decrypted SAMLObject
     * @throws DecryptionException thrown when decryption generates an error
     */
    private SAMLObject decryptData(@Nonnull final EncryptedElementType encElement) throws DecryptionException {

        if (encElement.getEncryptedData() == null) {
            throw new DecryptionException("Element had no EncryptedData child");
        }

        XMLObject xmlObject = null;
        try {
            xmlObject = decryptData(encElement.getEncryptedData(), isRootInNewDocument());
        } catch (final DecryptionException e) {
            log.error("SAML Decrypter encountered an error decrypting element content: {}", e.getMessage());
            throw e;
        }

        logPostDecryption(xmlObject);

        if (! (xmlObject instanceof SAMLObject)) {
            throw new DecryptionException("Decrypted XMLObject was not an instance of SAMLObject");
        }

        return (SAMLObject) xmlObject;
    }

    /**
     * Log the target object after decryption.
     *
     * @param xmlObject the decrypted XMLObject
     */
    private void logPostDecryption(final XMLObject xmlObject) {
        if (log.isDebugEnabled()) {
            try {
                final Element dom = XMLObjectSupport.marshall(xmlObject);
                log.debug("XML after decryption:\n{}", SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("Unable to marshall decrypted XML for logging purposes", e);
            }
        }
    }
}