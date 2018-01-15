//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.30 um 03:44:07 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.eidinterface.tls.clientcertificates.type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.eidinterface.tls.clientcertificates.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="ClientCertificate" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.eidinterface.tls.clientcertificate"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.eidinterface.tls.clientcertificates.type", propOrder = {
    "clientCertificate"
})
public class IcsCryptoEidinterfaceTlsClientcertificatesType {

    @XmlElement(name = "ClientCertificate")
    protected List<IcsCryptoEidinterfaceTlsClientcertificate> clientCertificate;

    /**
     * Gets the value of the clientCertificate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clientCertificate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClientCertificate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsCryptoEidinterfaceTlsClientcertificate }
     * 
     * 
     */
    public List<IcsCryptoEidinterfaceTlsClientcertificate> getClientCertificate() {
        if (clientCertificate == null) {
            clientCertificate = new ArrayList<IcsCryptoEidinterfaceTlsClientcertificate>();
        }
        return this.clientCertificate;
    }

}
