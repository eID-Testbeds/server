
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.secunet.testbedutils.bouncycertgen.xml.cv package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.secunet.testbedutils.bouncycertgen.xml.cv
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Root }
     * 
     */
    public Root createRoot() {
        return new Root();
    }

    /**
     * Create an instance of {@link KeyType }
     * 
     */
    public KeyType createKeyType() {
        return new KeyType();
    }

    /**
     * Create an instance of {@link DescriptionType }
     * 
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link CertType }
     * 
     */
    public CertType createCertType() {
        return new CertType();
    }

    /**
     * Create an instance of {@link Root.Keys }
     * 
     */
    public Root.Keys createRootKeys() {
        return new Root.Keys();
    }

    /**
     * Create an instance of {@link TerminalSectorType }
     * 
     */
    public TerminalSectorType createTerminalSectorType() {
        return new TerminalSectorType();
    }

    /**
     * Create an instance of {@link CertHolderAuthType }
     * 
     */
    public CertHolderAuthType createCertHolderAuthType() {
        return new CertHolderAuthType();
    }

    /**
     * Create an instance of {@link PublicKeyType }
     * 
     */
    public PublicKeyType createPublicKeyType() {
        return new PublicKeyType();
    }

    /**
     * Create an instance of {@link SignKeyType }
     * 
     */
    public SignKeyType createSignKeyType() {
        return new SignKeyType();
    }

    /**
     * Create an instance of {@link KeyType.Rsa }
     * 
     */
    public KeyType.Rsa createKeyTypeRsa() {
        return new KeyType.Rsa();
    }

    /**
     * Create an instance of {@link DescriptionType.CommCerts }
     * 
     */
    public DescriptionType.CommCerts createDescriptionTypeCommCerts() {
        return new DescriptionType.CommCerts();
    }

    /**
     * Create an instance of {@link CertType.Extensions }
     * 
     */
    public CertType.Extensions createCertTypeExtensions() {
        return new CertType.Extensions();
    }

    /**
     * Create an instance of {@link CertType.OutputFile }
     * 
     */
    public CertType.OutputFile createCertTypeOutputFile() {
        return new CertType.OutputFile();
    }

}
