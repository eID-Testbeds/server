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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.eidinterface.tls.clientcertificate complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.eidinterface.tls.clientcertificate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Type" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.tls.clientcertificate.type"/>
 *         &lt;element name="SignatureAlgorithm" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.tls.signaturealgorithms"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.eidinterface.tls.clientcertificate", propOrder = {

})
public class IcsCryptoEidinterfaceTlsClientcertificate {

    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected IcsTlsClientcertificateType type;
    @XmlElement(name = "SignatureAlgorithm", required = true)
    @XmlSchemaType(name = "string")
    protected IcsTlsSignaturealgorithms signatureAlgorithm;

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsTlsClientcertificateType }
     *     
     */
    public IcsTlsClientcertificateType getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsTlsClientcertificateType }
     *     
     */
    public void setType(IcsTlsClientcertificateType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der signatureAlgorithm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsTlsSignaturealgorithms }
     *     
     */
    public IcsTlsSignaturealgorithms getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Legt den Wert der signatureAlgorithm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsTlsSignaturealgorithms }
     *     
     */
    public void setSignatureAlgorithm(IcsTlsSignaturealgorithms value) {
        this.signatureAlgorithm = value;
    }

}
