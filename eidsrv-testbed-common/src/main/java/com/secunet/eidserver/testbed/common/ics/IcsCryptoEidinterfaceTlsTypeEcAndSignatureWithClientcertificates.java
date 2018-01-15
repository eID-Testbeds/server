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
 * <p>Java-Klasse für ics.crypto.eidinterface.tls.type.ec.and.signature.with.clientcertificates complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.eidinterface.tls.type.ec.and.signature.with.clientcertificates">
 *   &lt;complexContent>
 *     &lt;extension base="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.tls.type.ec.and.signature">
 *       &lt;choice>
 *         &lt;element name="ClientCertificates" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.eidinterface.tls.clientcertificates.type" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.eidinterface.tls.type.ec.and.signature.with.clientcertificates", propOrder = {
    "clientCertificates"
})
public class IcsCryptoEidinterfaceTlsTypeEcAndSignatureWithClientcertificates
    extends IcsCryptoTlsTypeEcAndSignature
{

    @XmlElement(name = "ClientCertificates")
    protected IcsCryptoEidinterfaceTlsClientcertificatesType clientCertificates;

    /**
     * Ruft den Wert der clientCertificates-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsCryptoEidinterfaceTlsClientcertificatesType }
     *     
     */
    public IcsCryptoEidinterfaceTlsClientcertificatesType getClientCertificates() {
        return clientCertificates;
    }

    /**
     * Legt den Wert der clientCertificates-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsCryptoEidinterfaceTlsClientcertificatesType }
     *     
     */
    public void setClientCertificates(IcsCryptoEidinterfaceTlsClientcertificatesType value) {
        this.clientCertificates = value;
    }

}
