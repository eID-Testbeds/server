package com.secunet.eidserver.testbed.common.eidas;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.*;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.AbstractCredentialResolver;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.criterion.SignatureValidationConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.w3c.dom.Attr;
import se.swedenconnect.opensaml.OpenSAMLInitializer;
import se.swedenconnect.opensaml.OpenSAMLSecurityDefaultsConfig;
import se.swedenconnect.opensaml.OpenSAMLSecurityExtensionConfig;
import se.swedenconnect.opensaml.eidas.common.EidasConstants;
import se.swedenconnect.opensaml.eidas.common.EidasLoaEnum;
import se.swedenconnect.opensaml.eidas.ext.RequestedAttribute;
import se.swedenconnect.opensaml.eidas.ext.RequestedAttributes;
import se.swedenconnect.opensaml.eidas.ext.SPType;
import se.swedenconnect.opensaml.eidas.ext.SPTypeEnumeration;
import se.swedenconnect.opensaml.eidas.ext.impl.RequestedAttributeBuilder;
import se.swedenconnect.opensaml.eidas.ext.impl.RequestedAttributesBuilder;
import se.swedenconnect.opensaml.eidas.ext.impl.SPTypeBuilder;
import se.swedenconnect.opensaml.eidas.xmlsec.RelaxedEidasSecurityConfiguration;

import javax.annotation.Nonnull;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EidasAuthnRequestHandler {

    private static boolean isInit = false;

    public static synchronized void Init() throws InitializationException {
        if(!isInit)
        {
            try {
                OpenSAMLInitializer.getInstance()
                        .initialize(
                                new OpenSAMLSecurityDefaultsConfig(new RelaxedEidasSecurityConfiguration()),
                                new OpenSAMLSecurityExtensionConfig());
            } catch (Exception e) {
                throw new InitializationException(e);
            }
            //Security.addProvider(new BouncyCastleProvider());
            org.apache.xml.security.algorithms.JCEMapper.setProviderId("BC");
            isInit=true;
        }
    }

    /**
     * Creates an eIDAS {@code AuthnRequest} message.
     *
     * @param requestID
     *          the ID of the request
     * @param destination
     *          the destination to where the request is to be sent
     * @param spEntityID
     *          the entityID of the entity creating the request
     * @param spType
     *          the eIDAS SP type (public or private)
     * @param requestedAttributes
     *          a list of requested attributes to be included as an eIDAS request extension
     * @param loa
     *          the eIDAS Level of Assurance to be included as a requested authentication context
     * @param provider
     *          the eIDAS Provider name
     * @return a (non-signed) {@code AuthnRequest} message
     */
    public static AuthnRequest createAuthnRequest(
            String requestID,
            String destination,
            String spEntityID,
            SPTypeEnumeration spType,
            Map<EidasNaturalPersonAttributes,Boolean> requestedAttributes,
            String nameID,
            EidasLoaEnum loa,
            String provider,
            boolean addRandomAttribute) {

        AuthnRequest request = new AuthnRequestBuilder().buildObject();
        request.getNamespaceManager().registerNamespaceDeclaration(new Namespace(EidasConstants.EIDAS_NS, EidasConstants.EIDAS_PREFIX));

        // Add the request attributes.
        //
        request.setID(requestID);
        request.setDestination(destination);
        request.setIssueInstant(Instant.now());

        // Add the issuer element (the entity that issues this request).
        //
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setFormat(NameIDType.ENTITY);
        issuer.setValue(spEntityID);
        request.setIssuer(issuer);

        request.setIsPassive(false);
        //request.setProviderName(provider);

        Extensions extensions = new ExtensionsBuilder().buildObject();

        // Add the type of SP as an extension.
        //
        SPType spTypeElement = new SPTypeBuilder().buildObject();
        spTypeElement.setType(spType);
        extensions.getUnknownXMLObjects().add(spTypeElement);

        // Add the eIDAS requested attributes as an extension.
        //
        if ((requestedAttributes != null && !requestedAttributes.isEmpty()) || addRandomAttribute) {
            RequestedAttributes requestedAttributesElement = new RequestedAttributesBuilder().buildObject();

            // Also see the RequestedAttributeTemplates class ...
            if (requestedAttributes != null && !requestedAttributes.isEmpty()) {
                for (Map.Entry<EidasNaturalPersonAttributes, Boolean> attr : requestedAttributes.entrySet()) {
                    RequestedAttribute reqAttr = new RequestedAttributeBuilder().buildObject();
                    reqAttr.setName(attr.getKey().getName());
                    reqAttr.setNameFormat(Attribute.URI_REFERENCE);
                    reqAttr.setIsRequired(attr.getValue());
                    requestedAttributesElement.getRequestedAttributes().add(reqAttr);
                }
            }

            if (addRandomAttribute) {
                RequestedAttribute reqAttr = new RequestedAttributeBuilder().buildObject();
                reqAttr.setName("RANDOM_ATTRIBUTE");
                reqAttr.setNameFormat(Attribute.URI_REFERENCE);
                reqAttr.setIsRequired(true);
                requestedAttributesElement.getRequestedAttributes().add(reqAttr);
            }

            extensions.getUnknownXMLObjects().add(requestedAttributesElement);
        }
        request.setExtensions(extensions);

        if (provider != null) {
            Scoping scoping = new ScopingBuilder().buildObject();
            RequesterID reqID = new RequesterIDBuilder().buildObject();
            reqID.setURI(provider);
            scoping.getRequesterIDs().add(reqID);
            request.setScoping(scoping);
        }

        // Set the requested NameID policy to "persistent".
        //
        NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
        nameIDPolicy.setFormat(nameID);
        nameIDPolicy.setAllowCreate(true);
        request.setNameIDPolicy(nameIDPolicy);

        // Create the requested authentication context and assign the "level of assurance" that we require
        // the authentication to be performed under.
        //
        RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM); // Should be exact!
        AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
        authnContextClassRef.setURI(loa.getUri());
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
        request.setRequestedAuthnContext(requestedAuthnContext);
        request.setForceAuthn(true);

        return request;
    }

    /**
     * Signs the supplied SAML object using the credentials.
     *
     * @param object
     *          object to sign
     * @param signingCredentials
     *          signature credentials
     * @param <T>
     *          the object type
     * @throws SignatureException
     *           for signature creation errors
     */
    public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials) throws SignatureException {
        try {
            object.setSignature(null);

            BasicSignatureSigningConfiguration signatureCreds = new BasicSignatureSigningConfiguration();
            signatureCreds.setSigningCredentials(Collections.singletonList(signingCredentials));
            SignatureSigningParameters parameters = getSignatureSigningParameters(signatureCreds);
            SignatureSupport.signObject(object, parameters);
        }
        catch (ResolverException | SecurityException | MarshallingException e) {
            throw new SignatureException(e);
        }
    }

    /**
     * Get {@link SignatureSigningParameters} from {@link BasicSignatureSigningConfiguration}
     *
     * @param signatureCreds Signature credentials as {@link BasicSignatureSigningConfiguration}
     * @return Signature credentials as {@link SignatureSigningParameters}
     * @throws ResolverException Internal problem resolving the signature criteria set. Should not happen.
     */
    private static SignatureSigningParameters getSignatureSigningParameters(BasicSignatureSigningConfiguration signatureCreds) throws ResolverException {
        EidasSignatureSigningParameterResolver signatureParametersResolver = new EidasSignatureSigningParameterResolver();
        CriteriaSet criteriaSet = new CriteriaSet(new SignatureSigningConfigurationCriterion(
                SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(),  // Will get us our eIDAS config
                signatureCreds));

        SignatureSigningParameters parameters = signatureParametersResolver.resolveSingle(criteriaSet);
        if (parameters == null)
            throw new ResolverException("Could not resolve signature parameters.");
        return parameters;
    }

    /**
     * Validate a SAML signature
     *
     * @param object SAML message
     * @param cert Certificate to validate against
     * @throws SignatureException If signature was invalid
     * @throws SecurityException If validation failed internally
     */
    public static <T extends SignableSAMLObject> void validate(T object, X509Certificate cert) throws SignatureException, SecurityException {

        // Temporary code until we figure out how to make the OpenSAML unmarshaller to
        // mark the ID attribute as an ID.
        //
        Attr idAttr = object.getDOM() != null ? object.getDOM().getAttributeNode("ID") : null;
        if (idAttr != null) {
            idAttr.getOwnerElement().setIdAttributeNode(idAttr, true);
        }

        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(new StaticCredentialResolver(cert),
                DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());

        Signature signature = object.getSignature();
        if (signature == null) {
            throw new SignatureException("Object is not signed");
        }
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new SignatureValidationConfigurationCriterion(
                // Gets us our eIDAS configuration
                SecurityConfigurationSupport.getGlobalSignatureValidationConfiguration()));

        if (!trustEngine.validate(signature, criteriaSet)) {
            throw new SignatureException("Signature validation failed");
        }
    }

    /**
     * Super-simple credential resolver which only gives out a list containing the supplied certificate when created
     */
    public static class StaticCredentialResolver extends AbstractCredentialResolver {

        private final X509Credential cred;

        public StaticCredentialResolver(X509Certificate cert) {
            this.cred = new BasicX509Credential(cert);
        }

        @Nonnull
        @Override
        public Iterable<Credential> resolve(CriteriaSet criteriaSet) {
            return List.of(this.cred);
        }

    }
}
