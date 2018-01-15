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
 * <p>Java-Klasse für ics.crypto.saml complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.saml">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="TransportSecurity" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls"/>
 *         &lt;element name="XmlSignature" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsignatures"/>
 *         &lt;element name="XmlEncryption" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml.xmlencryption"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.saml", propOrder = {

})
public class IcsCryptoSaml {

    @XmlElement(name = "TransportSecurity", required = true)
    protected IcsCryptoTls transportSecurity;
    @XmlElement(name = "XmlSignature", required = true)
    protected IcsXmlsignatures xmlSignature;
    @XmlElement(name = "XmlEncryption", required = true)
    protected IcsCryptoSamlXmlencryption xmlEncryption;

    /**
     * Ruft den Wert der transportSecurity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoTls }
     *     
     */
    public IcsCryptoTls getTransportSecurity() {
        return transportSecurity;
    }

    /**
     * Legt den Wert der transportSecurity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoTls }
     *     
     */
    public void setTransportSecurity(IcsCryptoTls value) {
        this.transportSecurity = value;
    }

    /**
     * Ruft den Wert der xmlSignature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsignatures }
     *     
     */
    public IcsXmlsignatures getXmlSignature() {
        return xmlSignature;
    }

    /**
     * Legt den Wert der xmlSignature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsignatures }
     *     
     */
    public void setXmlSignature(IcsXmlsignatures value) {
        this.xmlSignature = value;
    }

    /**
     * Ruft den Wert der xmlEncryption-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoSamlXmlencryption }
     *     
     */
    public IcsCryptoSamlXmlencryption getXmlEncryption() {
        return xmlEncryption;
    }

    /**
     * Legt den Wert der xmlEncryption-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoSamlXmlencryption }
     *     
     */
    public void setXmlEncryption(IcsCryptoSamlXmlencryption value) {
        this.xmlEncryption = value;
    }

}
