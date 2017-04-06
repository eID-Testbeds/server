
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r signatureAlgorithmType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="signatureAlgorithmType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ECDSA" type="{http://www.secunet.com}signatureAlgorithmTypeECDSA"/>
 *         &lt;element name="RSA" type="{http://www.secunet.com}signatureAlgorithmTypeRSA"/>
 *         &lt;element name="DSA" type="{http://www.secunet.com}signatureAlgorithmTypeDSA"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "signatureAlgorithmType", propOrder = {
    "ecdsa",
    "rsa",
    "dsa"
})
public class SignatureAlgorithmType {

    @XmlElement(name = "ECDSA")
    @XmlSchemaType(name = "string")
    protected SignatureAlgorithmTypeECDSA ecdsa;
    @XmlElement(name = "RSA")
    @XmlSchemaType(name = "string")
    protected SignatureAlgorithmTypeRSA rsa;
    @XmlElement(name = "DSA")
    @XmlSchemaType(name = "string")
    protected SignatureAlgorithmTypeDSA dsa;

    /**
     * Ruft den Wert der ecdsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmTypeECDSA }
     *     
     */
    public SignatureAlgorithmTypeECDSA getECDSA() {
        return ecdsa;
    }

    /**
     * Legt den Wert der ecdsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmTypeECDSA }
     *     
     */
    public void setECDSA(SignatureAlgorithmTypeECDSA value) {
        this.ecdsa = value;
    }

    /**
     * Ruft den Wert der rsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmTypeRSA }
     *     
     */
    public SignatureAlgorithmTypeRSA getRSA() {
        return rsa;
    }

    /**
     * Legt den Wert der rsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmTypeRSA }
     *     
     */
    public void setRSA(SignatureAlgorithmTypeRSA value) {
        this.rsa = value;
    }

    /**
     * Ruft den Wert der dsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureAlgorithmTypeDSA }
     *     
     */
    public SignatureAlgorithmTypeDSA getDSA() {
        return dsa;
    }

    /**
     * Legt den Wert der dsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureAlgorithmTypeDSA }
     *     
     */
    public void setDSA(SignatureAlgorithmTypeDSA value) {
        this.dsa = value;
    }

}
