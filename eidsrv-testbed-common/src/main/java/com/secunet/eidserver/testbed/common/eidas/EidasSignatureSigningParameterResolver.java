package com.secunet.eidserver.testbed.common.eidas;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor;
import org.opensaml.xmlsec.algorithm.KeyLengthSpecifiedAlgorithm;
import org.opensaml.xmlsec.algorithm.KeySpecifiedAlgorithm;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PrivateKey;
import java.util.function.Predicate;

public class EidasSignatureSigningParameterResolver extends BasicSignatureSigningParametersResolver {
    @Override
    public SignatureSigningParameters resolveSingle(@Nonnull final CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(SignatureSigningConfigurationCriterion.class),
                "Resolver requires an instance of SignatureSigningConfigurationCriterion");

        final Predicate<String> includeExcludePredicate = getIncludeExcludePredicate(criteria);

        final SignatureSigningParameters params = new SignatureSigningParameters();

        resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, includeExcludePredicate);

        params.setSignatureReferenceDigestMethod(resolveReferenceDigestMethod(criteria, includeExcludePredicate));
        params.setSignatureReferenceCanonicalizationAlgorithm(resolveReferenceCanonicalizationAlgorithm(criteria));

        params.setSignatureCanonicalizationAlgorithm(resolveCanonicalizationAlgorithm(criteria));

        if (params.getSigningCredential() != null) {
            params.setKeyInfoGenerator(resolveKeyInfoGenerator(criteria, params.getSigningCredential()));
            params.setSignatureHMACOutputLength(resolveHMACOutputLength(criteria, params.getSigningCredential(),
                    params.getSignatureAlgorithm()));
        }

        if (validate(params)) {
            logResult(params);
            return params;
        }
        return null;
    }
    @Override
    protected boolean credentialSupportsAlgorithm(@Nonnull Credential credential, @Nonnull String algorithm) {
        return credentialSupportsAlgorithmForSigning(credential,
                getAlgorithmRegistry().get(algorithm));
    }

    private static boolean credentialSupportsAlgorithmForSigning(@Nullable final Credential credential,
                                                                @Nullable final AlgorithmDescriptor algorithm) {
        if (credential == null || algorithm == null) {
            return false;
        }

        final Key key = CredentialSupport.extractSigningKey(credential);
        if (key == null) {
            return false;
        }

        switch(algorithm.getType()) {
            case Signature:
                if (!(key instanceof PrivateKey)) {
                    return false;
                }
                break;
            case Mac:
                if (!(key instanceof SecretKey)) {
                    return false;
                }
                break;
            default:
                return false;
        }

        return checkKeyAlgorithmAndLength(key, algorithm);
    }

    private static boolean checkKeyAlgorithmAndLength(@Nonnull final Key key,
                                                     @Nonnull final AlgorithmDescriptor algorithm) {

        if (algorithm instanceof KeySpecifiedAlgorithm) {
            final String specifiedKey = ((KeySpecifiedAlgorithm)algorithm).getKey();
            if (specifiedKey.equals("EC")) {
                // It happens, that the ECDSA key is recognized as type EC.
                // This fails in the opensaml standard implementation.
                if (!"EC".equals(key.getAlgorithm()) && !"ECDSA".equals(key.getAlgorithm())) {
                    return false;
                }
            } else {
                if (!specifiedKey.equals(key.getAlgorithm())) {
                    return false;
                }
            }
        }

        if (algorithm instanceof KeyLengthSpecifiedAlgorithm) {
            final Integer specifiedKeyLength = ((KeyLengthSpecifiedAlgorithm)algorithm).getKeyLength();
            if (!specifiedKeyLength.equals(KeySupport.getKeyLength(key))) {
                return false;
            }
        }

        return true;
    }
}
