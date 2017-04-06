//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.03.07 um 03:04:33 PM CET 
//


package com.secunet.eidserver.testbed.common.ics;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ics.crypto.eidinterface.tls.clientcertificates complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.eidinterface.tls.clientcertificates">
 *   &lt;complexContent>
 *     &lt;extension base="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls.type.ec.and.signature">
 *       &lt;choice>
 *         &lt;element name="ClientCertificates" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.eidinterface.tls.clientcertificates.type" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.eidinterface.tls.clientcertificates", propOrder = {
    "clientCertificates"
})
public class IcsCryptoEidinterfaceTlsClientcertificates
    extends IcsCryptoTlsTypeEcAndSignature
{

    @XmlElement(name = "ClientCertificates")
    protected List<IcsCryptoEidinterfaceTlsClientcertificatesType> clientCertificates;

    /**
     * Gets the value of the clientCertificates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clientCertificates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClientCertificates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsCryptoEidinterfaceTlsClientcertificatesType }
     * 
     * 
     */
    public List<IcsCryptoEidinterfaceTlsClientcertificatesType> getClientCertificates() {
        if (clientCertificates == null) {
            clientCertificates = new ArrayList<IcsCryptoEidinterfaceTlsClientcertificatesType>();
        }
        return this.clientCertificates;
    }

}
