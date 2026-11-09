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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.eidinterface complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.eidinterface">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="TLS" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.eidinterface.tls.type.ec.and.signature.with.clientcertificates"/>
 *         &lt;element name="XmlSignature" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsignatures"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.eidinterface", propOrder = {

})
public class IcsCryptoEidinterface {

    @XmlElements({
        @XmlElement(name = "XmlSignature", type = IcsXmlsignatures.class)
    })
    protected IcsXmlsignatures xmlSignature;

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

}
