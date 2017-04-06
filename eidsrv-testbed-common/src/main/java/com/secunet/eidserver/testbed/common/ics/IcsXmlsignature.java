//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.09.27 um 03:45:45 PM CEST 
//


package com.secunet.eidserver.testbed.common.ics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * This element contains the data specifying the supported XML encryption algorithms.
 * 
 * <p>Java-Klasse für ics.xmlsignature complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ics.xmlsignature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Digest" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsec.signature.digest"/>
 *         &lt;element name="Canonicalization" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsec.signature.canonicalization"/>
 *         &lt;element name="Parameters" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsecurity.parameters"/>
 *       &lt;/all>
 *       &lt;attribute name="URI" use="required" type="{http://trdoccheck.bsi.bund.de/server/ics}ics.xmlsec.signature.uri" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ics.xmlsignature", propOrder = {

})
public class IcsXmlsignature {

    @XmlElement(name = "Digest", required = true)
    @XmlSchemaType(name = "string")
    protected IcsXmlsecSignatureDigest digest;
    @XmlElement(name = "Canonicalization", required = true)
    @XmlSchemaType(name = "string")
    protected IcsXmlsecSignatureCanonicalization canonicalization;
    @XmlElement(name = "Parameters", required = true)
    protected IcsXmlsecurityParameters parameters;
    @XmlAttribute(name = "URI", required = true)
    protected IcsXmlsecSignatureUri uri;

    /**
     * Ruft den Wert der digest-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecSignatureDigest }
     *     
     */
    public IcsXmlsecSignatureDigest getDigest() {
        return digest;
    }

    /**
     * Legt den Wert der digest-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecSignatureDigest }
     *     
     */
    public void setDigest(IcsXmlsecSignatureDigest value) {
        this.digest = value;
    }

    /**
     * Ruft den Wert der canonicalization-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecSignatureCanonicalization }
     *     
     */
    public IcsXmlsecSignatureCanonicalization getCanonicalization() {
        return canonicalization;
    }

    /**
     * Legt den Wert der canonicalization-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecSignatureCanonicalization }
     *     
     */
    public void setCanonicalization(IcsXmlsecSignatureCanonicalization value) {
        this.canonicalization = value;
    }

    /**
     * Ruft den Wert der parameters-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecurityParameters }
     *     
     */
    public IcsXmlsecurityParameters getParameters() {
        return parameters;
    }

    /**
     * Legt den Wert der parameters-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecurityParameters }
     *     
     */
    public void setParameters(IcsXmlsecurityParameters value) {
        this.parameters = value;
    }

    /**
     * Ruft den Wert der uri-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IcsXmlsecSignatureUri }
     *     
     */
    public IcsXmlsecSignatureUri getURI() {
        return uri;
    }

    /**
     * Legt den Wert der uri-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IcsXmlsecSignatureUri }
     *     
     */
    public void setURI(IcsXmlsecSignatureUri value) {
        this.uri = value;
    }

}
