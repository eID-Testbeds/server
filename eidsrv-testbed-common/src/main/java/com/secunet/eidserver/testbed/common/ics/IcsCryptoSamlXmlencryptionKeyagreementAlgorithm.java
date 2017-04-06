//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.saml.xmlencryption.keyagreement.algorithm complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.saml.xmlencryption.keyagreement.algorithm">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Parameters" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsecurity.parameters"/>
 *         &lt;element name="KeyWrappingUri" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsec.encryption.key.agreement.wrapping.uri"/>
 *       &lt;/all>
 *       &lt;attribute name="URI" use="required" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsec.encryption.key.agreement.uri" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.saml.xmlencryption.keyagreement.algorithm", propOrder = {

})
public class IcsCryptoSamlXmlencryptionKeyagreementAlgorithm {

    @XmlElement(name = "Parameters", required = true)
    protected IcsXmlsecurityParameters parameters;
    @XmlElement(name = "KeyWrappingUri", required = true)
    @XmlSchemaType(name = "string")
    protected IcsXmlsecEncryptionKeyAgreementWrappingUri keyWrappingUri;
    @XmlAttribute(name = "URI", required = true)
    protected IcsXmlsecEncryptionKeyAgreementUri uri;

    /**
     * Ruft den Wert der parameters-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecurityParameters }
     *     
     */
    public IcsXmlsecurityParameters getParameters() {
        return parameters;
    }

    /**
     * Legt den Wert der parameters-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecurityParameters }
     *     
     */
    public void setParameters(IcsXmlsecurityParameters value) {
        this.parameters = value;
    }

    /**
     * Ruft den Wert der keyWrappingUri-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecEncryptionKeyAgreementWrappingUri }
     *     
     */
    public IcsXmlsecEncryptionKeyAgreementWrappingUri getKeyWrappingUri() {
        return keyWrappingUri;
    }

    /**
     * Legt den Wert der keyWrappingUri-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecEncryptionKeyAgreementWrappingUri }
     *     
     */
    public void setKeyWrappingUri(IcsXmlsecEncryptionKeyAgreementWrappingUri value) {
        this.keyWrappingUri = value;
    }

    /**
     * Ruft den Wert der uri-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecEncryptionKeyAgreementUri }
     *     
     */
    public IcsXmlsecEncryptionKeyAgreementUri getURI() {
        return uri;
    }

    /**
     * Legt den Wert der uri-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecEncryptionKeyAgreementUri }
     *     
     */
    public void setURI(IcsXmlsecEncryptionKeyAgreementUri value) {
        this.uri = value;
    }

}
