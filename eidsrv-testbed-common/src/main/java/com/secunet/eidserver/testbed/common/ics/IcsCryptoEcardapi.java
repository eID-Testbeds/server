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
 * <p>Java-Klasse für ics.crypto.ecardapi complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.ecardapi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PskChannel" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.ecardapi.psktls" minOccurs="0"/>
 *         &lt;element name="AttachedServer" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.ecardapi", propOrder = {
    "pskChannel",
    "attachedServer"
})
public class IcsCryptoEcardapi {

    @XmlElement(name = "PskChannel")
    protected IcsCryptoEcardapiPsktls pskChannel;
    @XmlElement(name = "AttachedServer")
    protected IcsCryptoTls attachedServer;

    /**
     * Ruft den Wert der pskChannel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoEcardapiPsktls }
     *     
     */
    public IcsCryptoEcardapiPsktls getPskChannel() {
        return pskChannel;
    }

    /**
     * Legt den Wert der pskChannel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoEcardapiPsktls }
     *     
     */
    public void setPskChannel(IcsCryptoEcardapiPsktls value) {
        this.pskChannel = value;
    }

    /**
     * Ruft den Wert der attachedServer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoTls }
     *     
     */
    public IcsCryptoTls getAttachedServer() {
        return attachedServer;
    }

    /**
     * Legt den Wert der attachedServer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoTls }
     *     
     */
    public void setAttachedServer(IcsCryptoTls value) {
        this.attachedServer = value;
    }

}
