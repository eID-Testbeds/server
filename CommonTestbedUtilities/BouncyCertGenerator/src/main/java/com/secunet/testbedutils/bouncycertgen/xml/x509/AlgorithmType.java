
package com.secunet.testbedutils.bouncycertgen.xml.x509;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für algorithmType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="algorithmType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ECDSA" type="{http://www.secunet.com}algorithmTypeEC"/>
 *         &lt;element name="RSA" type="{http://www.secunet.com}algorithmTypeRSADSA"/>
 *         &lt;element name="DSA" type="{http://www.secunet.com}algorithmTypeRSADSA"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "algorithmType", propOrder = {
    "ecdsa",
    "rsa",
    "dsa"
})
public class AlgorithmType {

    @XmlElement(name = "ECDSA")
    @XmlSchemaType(name = "string")
    protected AlgorithmTypeEC ecdsa;
    @XmlElement(name = "RSA")
    protected Integer rsa;
    @XmlElement(name = "DSA")
    protected Integer dsa;

    /**
     * Ruft den Wert der ecdsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AlgorithmTypeEC }
     *     
     */
    public AlgorithmTypeEC getECDSA() {
        return ecdsa;
    }

    /**
     * Legt den Wert der ecdsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AlgorithmTypeEC }
     *     
     */
    public void setECDSA(AlgorithmTypeEC value) {
        this.ecdsa = value;
    }

    /**
     * Ruft den Wert der rsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRSA() {
        return rsa;
    }

    /**
     * Legt den Wert der rsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRSA(Integer value) {
        this.rsa = value;
    }

    /**
     * Ruft den Wert der dsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDSA() {
        return dsa;
    }

    /**
     * Legt den Wert der dsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDSA(Integer value) {
        this.dsa = value;
    }

}
