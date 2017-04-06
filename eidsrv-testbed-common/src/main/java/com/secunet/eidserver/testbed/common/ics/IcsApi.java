//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * The data type for the eCard API declaration elements.
 * 
 * <p>Java-Klasse für ics.api complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.api">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="ApiVersionMajor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="ApiVersionMinor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="ApiVersionSubminor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.api", propOrder = {

})
public class IcsApi {

    @XmlElement(name = "ApiVersionMajor", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger apiVersionMajor;
    @XmlElement(name = "ApiVersionMinor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger apiVersionMinor;
    @XmlElement(name = "ApiVersionSubminor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger apiVersionSubminor;

    /**
     * Ruft den Wert der apiVersionMajor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getApiVersionMajor() {
        return apiVersionMajor;
    }

    /**
     * Legt den Wert der apiVersionMajor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setApiVersionMajor(BigInteger value) {
        this.apiVersionMajor = value;
    }

    /**
     * Ruft den Wert der apiVersionMinor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getApiVersionMinor() {
        return apiVersionMinor;
    }

    /**
     * Legt den Wert der apiVersionMinor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setApiVersionMinor(BigInteger value) {
        this.apiVersionMinor = value;
    }

    /**
     * Ruft den Wert der apiVersionSubminor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getApiVersionSubminor() {
        return apiVersionSubminor;
    }

    /**
     * Legt den Wert der apiVersionSubminor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setApiVersionSubminor(BigInteger value) {
        this.apiVersionSubminor = value;
    }

}
