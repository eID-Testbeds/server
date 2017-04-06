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
 * The data type for the ICS elements.
 * 
 * <p>Java-Klasse für ics complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Metadata" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.metadata"/>
 *         &lt;element name="API" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.api"/>
 *         &lt;element name="Profiles" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.profiles"/>
 *         &lt;element name="Cryptography" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics", propOrder = {

})
public class Ics {

    @XmlElement(name = "Metadata", required = true)
    protected IcsMetadata metadata;
    @XmlElement(name = "API", required = true)
    protected IcsApi api;
    @XmlElement(name = "Profiles", required = true)
    protected IcsProfiles profiles;
    @XmlElement(name = "Cryptography", required = true)
    protected IcsCrypto cryptography;

    /**
     * Ruft den Wert der metadata-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsMetadata }
     *     
     */
    public IcsMetadata getMetadata() {
        return metadata;
    }

    /**
     * Legt den Wert der metadata-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsMetadata }
     *     
     */
    public void setMetadata(IcsMetadata value) {
        this.metadata = value;
    }

    /**
     * Ruft den Wert der api-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsApi }
     *     
     */
    public IcsApi getAPI() {
        return api;
    }

    /**
     * Legt den Wert der api-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsApi }
     *     
     */
    public void setAPI(IcsApi value) {
        this.api = value;
    }

    /**
     * Ruft den Wert der profiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsProfiles }
     *     
     */
    public IcsProfiles getProfiles() {
        return profiles;
    }

    /**
     * Legt den Wert der profiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsProfiles }
     *     
     */
    public void setProfiles(IcsProfiles value) {
        this.profiles = value;
    }

    /**
     * Ruft den Wert der cryptography-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCrypto }
     *     
     */
    public IcsCrypto getCryptography() {
        return cryptography;
    }

    /**
     * Legt den Wert der cryptography-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCrypto }
     *     
     */
    public void setCryptography(IcsCrypto value) {
        this.cryptography = value;
    }

}
