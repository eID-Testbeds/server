//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.tls.type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.tls.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Ciphersuites" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls.ciphersuites.type"/>
 *       &lt;/choice>
 *       &lt;attribute name="version" use="required" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.tls.version" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.tls.type", propOrder = {
    "ciphersuites"
})
@XmlSeeAlso({
    IcsCryptoTlsTypeEcAndSignature.class
})
public class IcsCryptoTlsType {

    @XmlElement(name = "Ciphersuites")
    protected IcsCryptoTlsCiphersuitesType ciphersuites;
    @XmlAttribute(name = "version", required = true)
    protected IcsTlsVersion version;

    /**
     * Ruft den Wert der ciphersuites-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoTlsCiphersuitesType }
     *     
     */
    public IcsCryptoTlsCiphersuitesType getCiphersuites() {
        return ciphersuites;
    }

    /**
     * Legt den Wert der ciphersuites-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoTlsCiphersuitesType }
     *     
     */
    public void setCiphersuites(IcsCryptoTlsCiphersuitesType value) {
        this.ciphersuites = value;
    }

    /**
     * Ruft den Wert der version-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsTlsVersion }
     *     
     */
    public IcsTlsVersion getVersion() {
        return version;
    }

    /**
     * Legt den Wert der version-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsTlsVersion }
     *     
     */
    public void setVersion(IcsTlsVersion value) {
        this.version = value;
    }

}
