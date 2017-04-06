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
 * <p>Java-Klasse für ics.crypto.saml.xmlencryption.keyagreement.algorithms complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.crypto.saml.xmlencryption.keyagreement.algorithms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="AgreementAlgorithm" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.crypto.saml.xmlencryption.keyagreement.algorithm"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.crypto.saml.xmlencryption.keyagreement.algorithms", propOrder = {
    "agreementAlgorithm"
})
public class IcsCryptoSamlXmlencryptionKeyagreementAlgorithms {

    @XmlElement(name = "AgreementAlgorithm", required = true)
    protected List<IcsCryptoSamlXmlencryptionKeyagreementAlgorithm> agreementAlgorithm;

    /**
     * Gets the value of the agreementAlgorithm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agreementAlgorithm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAgreementAlgorithm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IcsCryptoSamlXmlencryptionKeyagreementAlgorithm }
     * 
     * 
     */
    public List<IcsCryptoSamlXmlencryptionKeyagreementAlgorithm> getAgreementAlgorithm() {
        if (agreementAlgorithm == null) {
            agreementAlgorithm = new ArrayList<IcsCryptoSamlXmlencryptionKeyagreementAlgorithm>();
        }
        return this.agreementAlgorithm;
    }

}
