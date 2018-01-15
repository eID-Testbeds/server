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
 * The data type for the Cryptography.
 * 
 * <p>Java-Klasse für ics.crypto complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="eCard-Api" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.ecardapi"/>
 *         &lt;element name="eID-Interface" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.eidinterface" minOccurs="0"/>
 *         &lt;element name="SAML" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml" minOccurs="0"/>
 *         &lt;element name="ChipAuthentication" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.ca"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto", propOrder = {

})
public class IcsCrypto {

    @XmlElement(name = "eCard-Api", required = true)
    protected IcsCryptoEcardapi eCardApi;
    @XmlElement(name = "eID-Interface")
    protected IcsCryptoEidinterface eidInterface;
    @XmlElement(name = "SAML")
    protected IcsCryptoSaml saml;
    @XmlElement(name = "ChipAuthentication", required = true)
    protected IcsCryptoCa chipAuthentication;

    /**
     * Ruft den Wert der eCardApi-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoEcardapi }
     *     
     */
    public IcsCryptoEcardapi getECardApi() {
        return eCardApi;
    }

    /**
     * Legt den Wert der eCardApi-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoEcardapi }
     *     
     */
    public void setECardApi(IcsCryptoEcardapi value) {
        this.eCardApi = value;
    }

    /**
     * Ruft den Wert der eidInterface-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoEidinterface }
     *     
     */
    public IcsCryptoEidinterface getEIDInterface() {
        return eidInterface;
    }

    /**
     * Legt den Wert der eidInterface-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoEidinterface }
     *     
     */
    public void setEIDInterface(IcsCryptoEidinterface value) {
        this.eidInterface = value;
    }

    /**
     * Ruft den Wert der saml-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoSaml }
     *     
     */
    public IcsCryptoSaml getSAML() {
        return saml;
    }

    /**
     * Legt den Wert der saml-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoSaml }
     *     
     */
    public void setSAML(IcsCryptoSaml value) {
        this.saml = value;
    }

    /**
     * Ruft den Wert der chipAuthentication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoCa }
     *     
     */
    public IcsCryptoCa getChipAuthentication() {
        return chipAuthentication;
    }

    /**
     * Legt den Wert der chipAuthentication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoCa }
     *     
     */
    public void setChipAuthentication(IcsCryptoCa value) {
        this.chipAuthentication = value;
    }

}
