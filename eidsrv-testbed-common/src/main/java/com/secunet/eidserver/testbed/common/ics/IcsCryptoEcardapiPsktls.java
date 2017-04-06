//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.ecardapi.psktls complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.ecardapi.psktls">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TLS" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls.type" maxOccurs="3"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.ecardapi.psktls", propOrder = {
    "tls"
})
public class IcsCryptoEcardapiPsktls {

    @XmlElement(name = "TLS", required = true)
    protected List<IcsCryptoTlsType> tls;

    /**
     * Gets the value of the tls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTLS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsCryptoTlsType }
     * 
     * 
     */
    public List<IcsCryptoTlsType> getTLS() {
        if (tls == null) {
            tls = new ArrayList<IcsCryptoTlsType>();
        }
        return this.tls;
    }

}
