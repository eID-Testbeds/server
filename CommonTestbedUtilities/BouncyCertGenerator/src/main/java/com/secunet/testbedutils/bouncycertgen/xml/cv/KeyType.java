
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für keyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="keyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filePrivateKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="filePublicKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;sequence>
 *           &lt;element name="algorithm" type="{http://www.secunet.com}algorithmType"/>
 *           &lt;choice>
 *             &lt;element name="ecdsa" type="{http://www.secunet.com}ecdsaType"/>
 *             &lt;element name="rsa">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;sequence>
 *                       &lt;element name="publicExpo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                       &lt;element name="length" type="{http://www.secunet.com}keyLengthType"/>
 *                     &lt;/sequence>
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/choice>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="create" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keyType", namespace = "http://www.secunet.com", propOrder = {
    "filePrivateKey",
    "filePublicKey",
    "algorithm",
    "ecdsa",
    "rsa"
})
public class KeyType {

    @XmlElement(namespace = "http://www.secunet.com", required = true)
    protected String filePrivateKey;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String filePublicKey;
    @XmlElement(namespace = "http://www.secunet.com", required = true)
    @XmlSchemaType(name = "string")
    protected AlgorithmType algorithm;
    @XmlElement(namespace = "http://www.secunet.com")
    @XmlSchemaType(name = "string")
    protected EcdsaType ecdsa;
    @XmlElement(namespace = "http://www.secunet.com")
    protected KeyType.Rsa rsa;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "create")
    protected Boolean create;

    /**
     * Ruft den Wert der filePrivateKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilePrivateKey() {
        return filePrivateKey;
    }

    /**
     * Legt den Wert der filePrivateKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilePrivateKey(String value) {
        this.filePrivateKey = value;
    }

    /**
     * Ruft den Wert der filePublicKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilePublicKey() {
        return filePublicKey;
    }

    /**
     * Legt den Wert der filePublicKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilePublicKey(String value) {
        this.filePublicKey = value;
    }

    /**
     * Ruft den Wert der algorithm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AlgorithmType }
     *     
     */
    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    /**
     * Legt den Wert der algorithm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AlgorithmType }
     *     
     */
    public void setAlgorithm(AlgorithmType value) {
        this.algorithm = value;
    }

    /**
     * Ruft den Wert der ecdsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcdsaType }
     *     
     */
    public EcdsaType getEcdsa() {
        return ecdsa;
    }

    /**
     * Legt den Wert der ecdsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcdsaType }
     *     
     */
    public void setEcdsa(EcdsaType value) {
        this.ecdsa = value;
    }

    /**
     * Ruft den Wert der rsa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KeyType.Rsa }
     *     
     */
    public KeyType.Rsa getRsa() {
        return rsa;
    }

    /**
     * Legt den Wert der rsa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyType.Rsa }
     *     
     */
    public void setRsa(KeyType.Rsa value) {
        this.rsa = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der create-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCreate() {
        return create;
    }

    /**
     * Legt den Wert der create-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCreate(Boolean value) {
        this.create = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="publicExpo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
     *         &lt;element name="length" type="{http://www.secunet.com}keyLengthType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "publicExpo",
        "length"
    })
    public static class Rsa {

        @XmlElement(namespace = "http://www.secunet.com")
        protected BigInteger publicExpo;
        @XmlElement(namespace = "http://www.secunet.com", required = true)
        protected BigInteger length;

        /**
         * Ruft den Wert der publicExpo-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getPublicExpo() {
            return publicExpo;
        }

        /**
         * Legt den Wert der publicExpo-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setPublicExpo(BigInteger value) {
            this.publicExpo = value;
        }

        /**
         * Ruft den Wert der length-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getLength() {
            return length;
        }

        /**
         * Legt den Wert der length-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setLength(BigInteger value) {
            this.length = value;
        }

    }

}
