//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.secunet.eidserver.testbed.common.ics package. 
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

    private final static QName _Ics_QNAME = new QName("http://trdoccheck.bsi.bund.de/server/ics", "Ics");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.secunet.eidserver.testbed.common.ics
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Ics }
     * 
     */
    public Ics createIcs() {
        return new Ics();
    }

    /**
     * Create an instance of {@link IcsXmlsignatures }
     * 
     */
    public IcsXmlsignatures createIcsXmlsignatures() {
        return new IcsXmlsignatures();
    }

    /**
     * Create an instance of {@link IcsProfiles }
     * 
     */
    public IcsProfiles createIcsProfiles() {
        return new IcsProfiles();
    }

    /**
     * Create an instance of {@link IcsCryptoEidinterface }
     * 
     */
    public IcsCryptoEidinterface createIcsCryptoEidinterface() {
        return new IcsCryptoEidinterface();
    }

    /**
     * Create an instance of {@link IcsApi }
     * 
     */
    public IcsApi createIcsApi() {
        return new IcsApi();
    }

    /**
     * Create an instance of {@link IcsXmlsignature }
     * 
     */
    public IcsXmlsignature createIcsXmlsignature() {
        return new IcsXmlsignature();
    }

    /**
     * Create an instance of {@link IcsXmlencryptionKeyagreementAlgorithms }
     * 
     */
    public IcsXmlencryptionKeyagreementAlgorithms createIcsCryptoSamlXmlencryptionKeyagreementAlgorithms() {
        return new IcsXmlencryptionKeyagreementAlgorithms();
    }

    /**
     * Create an instance of {@link IcsXmlencryptionKeytransportAlgorithm }
     * 
     */
    public IcsXmlencryptionKeytransportAlgorithm createIcsCryptoSamlXmlencryptionKeytransportAlgorithm() {
        return new IcsXmlencryptionKeytransportAlgorithm();
    }

    /**
     * Create an instance of {@link IcsXmlencryption }
     * 
     */
    public IcsXmlencryption createIcsXmlencryption() {
        return new IcsXmlencryption();
    }

    /**
     * Create an instance of {@link IcsMetadata }
     * 
     */
    public IcsMetadata createIcsMetadata() {
        return new IcsMetadata();
    }

    /**
     * Create an instance of {@link IcsCryptoSaml }
     * 
     */
    public IcsCryptoSaml createIcsCryptoSaml() {
        return new IcsCryptoSaml();
    }

    /**
     * Create an instance of {@link IcsXmlencryptionKeytransport }
     * 
     */
    public IcsXmlencryptionKeytransport createIcsCryptoSamlXmlencryptionKeytransport() {
        return new IcsXmlencryptionKeytransport();
    }

    /**
     * Create an instance of {@link IcsXmlencryptionKeyagreementAlgorithm }
     * 
     */
    public IcsXmlencryptionKeyagreementAlgorithm createIcsCryptoSamlXmlencryptionKeyagreementAlgorithm() {
        return new IcsXmlencryptionKeyagreementAlgorithm();
    }

    /**
     * Create an instance of {@link IcsCryptoCa }
     * 
     */
    public IcsCryptoCa createIcsCryptoCa() {
        return new IcsCryptoCa();
    }

    /**
     * Create an instance of {@link IcsXmlsecurityParameters }
     * 
     */
    public IcsXmlsecurityParameters createIcsXmlsecurityParameters() {
        return new IcsXmlsecurityParameters();
    }

    /**
     * Create an instance of {@link IcsCrypto }
     * 
     */
    public IcsCrypto createIcsCrypto() {
        return new IcsCrypto();
    }

    /**
     * Create an instance of {@link IcsXmlencryptionContentencryption }
     * 
     */
    public IcsXmlencryptionContentencryption createIcsCryptoSamlXmlencryptionContentencryption() {
        return new IcsXmlencryptionContentencryption();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ics }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://trdoccheck.bsi.bund.de/server/ics", name = "Ics")
    public JAXBElement<Ics> createIcs(Ics value) {
        return new JAXBElement<Ics>(_Ics_QNAME, Ics.class, null, value);
    }

}
