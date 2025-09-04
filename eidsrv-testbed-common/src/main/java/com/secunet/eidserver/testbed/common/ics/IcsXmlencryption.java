//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The data type for the XML encryption.
 * 
 * <p>Java-Klasse für ics.xmlencryption complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.xmlencryption">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="KeyTransportAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlencryption.keytransport"/>
 *         &lt;element name="KeyAgreementAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlencryption.keyagreement.algorithms"/>
 *         &lt;element name="ContentEncryptionAlgorithms" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlencryption.contentencryption"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.xmlencryption", propOrder = {

})
public class IcsXmlencryption {

    @XmlElement(name = "KeyTransportAlgorithms", required = true)
    protected IcsXmlencryptionKeytransport keyTransportAlgorithms;
    @XmlElement(name = "KeyAgreementAlgorithms", required = true)
    protected IcsXmlencryptionKeyagreementAlgorithms keyAgreementAlgorithms;
    @XmlElement(name = "ContentEncryptionAlgorithms", required = true)
    protected IcsXmlencryptionContentencryption contentEncryptionAlgorithms;

    /**
     * Ruft den Wert der keyTransportAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlencryptionKeytransport }
     *     
     */
    public IcsXmlencryptionKeytransport getKeyTransportAlgorithms() {
        return keyTransportAlgorithms;
    }

    /**
     * Legt den Wert der keyTransportAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlencryptionKeytransport }
     *     
     */
    public void setKeyTransportAlgorithms(IcsXmlencryptionKeytransport value) {
        this.keyTransportAlgorithms = value;
    }

    /**
     * Ruft den Wert der keyAgreementAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlencryptionKeyagreementAlgorithms }
     *     
     */
    public IcsXmlencryptionKeyagreementAlgorithms getKeyAgreementAlgorithms() {
        return keyAgreementAlgorithms;
    }

    /**
     * Legt den Wert der keyAgreementAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlencryptionKeyagreementAlgorithms }
     *     
     */
    public void setKeyAgreementAlgorithms(IcsXmlencryptionKeyagreementAlgorithms value) {
        this.keyAgreementAlgorithms = value;
    }

    /**
     * Ruft den Wert der contentEncryptionAlgorithms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlencryptionContentencryption }
     *     
     */
    public IcsXmlencryptionContentencryption getContentEncryptionAlgorithms() {
        return contentEncryptionAlgorithms;
    }

    /**
     * Legt den Wert der contentEncryptionAlgorithms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlencryptionContentencryption }
     *     
     */
    public void setContentEncryptionAlgorithms(IcsXmlencryptionContentencryption value) {
        this.contentEncryptionAlgorithms = value;
    }

}
