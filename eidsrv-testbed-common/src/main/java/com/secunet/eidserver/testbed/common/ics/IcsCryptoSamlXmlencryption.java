//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The data type for the XML encryption.
 * 
 * <p>Java-Klasse für ics.crypto.saml.xmlencryption complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.saml.xmlencryption">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="KeyTransportAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml.xmlencryption.keytransport"/>
 *         &lt;element name="KeyAgreementAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml.xmlencryption.keyagreement.algorithms"/>
 *         &lt;element name="ContentEncryptionAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml.xmlencryption.contentencryption"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.saml.xmlencryption", propOrder = {

})
public class IcsCryptoSamlXmlencryption {

    @XmlElement(name = "KeyTransportAlgorithms", required = true)
    protected IcsCryptoSamlXmlencryptionKeytransport keyTransportAlgorithms;
    @XmlElement(name = "KeyAgreementAlgorithms", required = true)
    protected IcsCryptoSamlXmlencryptionKeyagreementAlgorithms keyAgreementAlgorithms;
    @XmlElement(name = "ContentEncryptionAlgorithms", required = true)
    protected IcsCryptoSamlXmlencryptionContentencryption contentEncryptionAlgorithms;

    /**
     * Ruft den Wert der keyTransportAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoSamlXmlencryptionKeytransport }
     *     
     */
    public IcsCryptoSamlXmlencryptionKeytransport getKeyTransportAlgorithms() {
        return keyTransportAlgorithms;
    }

    /**
     * Legt den Wert der keyTransportAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoSamlXmlencryptionKeytransport }
     *     
     */
    public void setKeyTransportAlgorithms(IcsCryptoSamlXmlencryptionKeytransport value) {
        this.keyTransportAlgorithms = value;
    }

    /**
     * Ruft den Wert der keyAgreementAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoSamlXmlencryptionKeyagreementAlgorithms }
     *     
     */
    public IcsCryptoSamlXmlencryptionKeyagreementAlgorithms getKeyAgreementAlgorithms() {
        return keyAgreementAlgorithms;
    }

    /**
     * Legt den Wert der keyAgreementAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoSamlXmlencryptionKeyagreementAlgorithms }
     *     
     */
    public void setKeyAgreementAlgorithms(IcsCryptoSamlXmlencryptionKeyagreementAlgorithms value) {
        this.keyAgreementAlgorithms = value;
    }

    /**
     * Ruft den Wert der contentEncryptionAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoSamlXmlencryptionContentencryption }
     *     
     */
    public IcsCryptoSamlXmlencryptionContentencryption getContentEncryptionAlgorithms() {
        return contentEncryptionAlgorithms;
    }

    /**
     * Legt den Wert der contentEncryptionAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoSamlXmlencryptionContentencryption }
     *     
     */
    public void setContentEncryptionAlgorithms(IcsCryptoSamlXmlencryptionContentencryption value) {
        this.contentEncryptionAlgorithms = value;
    }

}
