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
 * The data type for the Metadata elements.
 * 
 * <p>Java-Klasse für ics.metadata complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.metadata">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="eCardApiUrl" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.notBlankURI" minOccurs="0"/>
 *         &lt;element name="eIdInterfaceiUrl" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.notBlankURI" minOccurs="0"/>
 *         &lt;element name="SamlUrl" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.notBlankURI" minOccurs="0"/>
 *         &lt;element name="AttachedTcTokenUrl" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.notBlankURI" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Vendor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VersionMajor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="VersionMinor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="VersionSubminor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="MultiClientCapable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.metadata", propOrder = {

})
public class IcsMetadata {

    @XmlSchemaType(name = "anyURI")
    protected String eCardApiUrl;
    @XmlSchemaType(name = "anyURI")
    protected String eIdInterfaceiUrl;
    @XmlElement(name = "SamlUrl")
    @XmlSchemaType(name = "anyURI")
    protected String samlUrl;
    @XmlElement(name = "AttachedTcTokenUrl")
    @XmlSchemaType(name = "anyURI")
    protected String attachedTcTokenUrl;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Vendor", required = true)
    protected String vendor;
    @XmlElement(name = "VersionMajor", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger versionMajor;
    @XmlElement(name = "VersionMinor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger versionMinor;
    @XmlElement(name = "VersionSubminor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger versionSubminor;
    @XmlElement(name = "MultiClientCapable", defaultValue = "true")
    protected Boolean multiClientCapable;

    /**
     * Ruft den Wert der eCardApiUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getECardApiUrl() {
        return eCardApiUrl;
    }

    /**
     * Legt den Wert der eCardApiUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setECardApiUrl(String value) {
        this.eCardApiUrl = value;
    }

    /**
     * Ruft den Wert der eIdInterfaceiUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEIdInterfaceiUrl() {
        return eIdInterfaceiUrl;
    }

    /**
     * Legt den Wert der eIdInterfaceiUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEIdInterfaceiUrl(String value) {
        this.eIdInterfaceiUrl = value;
    }

    /**
     * Ruft den Wert der samlUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamlUrl() {
        return samlUrl;
    }

    /**
     * Legt den Wert der samlUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamlUrl(String value) {
        this.samlUrl = value;
    }

    /**
     * Ruft den Wert der attachedTcTokenUrl-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttachedTcTokenUrl() {
        return attachedTcTokenUrl;
    }

    /**
     * Legt den Wert der attachedTcTokenUrl-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttachedTcTokenUrl(String value) {
        this.attachedTcTokenUrl = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der vendor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Legt den Wert der vendor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * Ruft den Wert der versionMajor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersionMajor() {
        return versionMajor;
    }

    /**
     * Legt den Wert der versionMajor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersionMajor(BigInteger value) {
        this.versionMajor = value;
    }

    /**
     * Ruft den Wert der versionMinor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersionMinor() {
        return versionMinor;
    }

    /**
     * Legt den Wert der versionMinor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersionMinor(BigInteger value) {
        this.versionMinor = value;
    }

    /**
     * Ruft den Wert der versionSubminor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersionSubminor() {
        return versionSubminor;
    }

    /**
     * Legt den Wert der versionSubminor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersionSubminor(BigInteger value) {
        this.versionSubminor = value;
    }

    /**
     * Ruft den Wert der multiClientCapable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiClientCapable() {
        return multiClientCapable;
    }

    /**
     * Legt den Wert der multiClientCapable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiClientCapable(Boolean value) {
        this.multiClientCapable = value;
    }

}
