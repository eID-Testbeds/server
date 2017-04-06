
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für descriptionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="descriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="issuerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="issuerURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subjectName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subjectURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="fileTermsOfUsage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *           &lt;element name="termsOfUsage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="redirectURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commCerts" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fileCommCert" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="hashCommCert" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fileDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="import" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descriptionType", namespace = "http://www.secunet.com", propOrder = {
    "issuerName",
    "issuerURL",
    "subjectName",
    "subjectURL",
    "fileTermsOfUsage",
    "termsOfUsage",
    "redirectURL",
    "commCerts",
    "fileDescription"
})
public class DescriptionType {

    @XmlElement(namespace = "http://www.secunet.com")
    protected String issuerName;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String issuerURL;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String subjectName;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String subjectURL;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String fileTermsOfUsage;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String termsOfUsage;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String redirectURL;
    @XmlElement(namespace = "http://www.secunet.com")
    protected DescriptionType.CommCerts commCerts;
    @XmlElement(namespace = "http://www.secunet.com")
    protected String fileDescription;
    @XmlAttribute(name = "import")
    protected Boolean _import;

    /**
     * Ruft den Wert der issuerName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * Legt den Wert der issuerName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerName(String value) {
        this.issuerName = value;
    }

    /**
     * Ruft den Wert der issuerURL-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerURL() {
        return issuerURL;
    }

    /**
     * Legt den Wert der issuerURL-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerURL(String value) {
        this.issuerURL = value;
    }

    /**
     * Ruft den Wert der subjectName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Legt den Wert der subjectName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectName(String value) {
        this.subjectName = value;
    }

    /**
     * Ruft den Wert der subjectURL-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectURL() {
        return subjectURL;
    }

    /**
     * Legt den Wert der subjectURL-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectURL(String value) {
        this.subjectURL = value;
    }

    /**
     * Ruft den Wert der fileTermsOfUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileTermsOfUsage() {
        return fileTermsOfUsage;
    }

    /**
     * Legt den Wert der fileTermsOfUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileTermsOfUsage(String value) {
        this.fileTermsOfUsage = value;
    }

    /**
     * Ruft den Wert der termsOfUsage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermsOfUsage() {
        return termsOfUsage;
    }

    /**
     * Legt den Wert der termsOfUsage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermsOfUsage(String value) {
        this.termsOfUsage = value;
    }

    /**
     * Ruft den Wert der redirectURL-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * Legt den Wert der redirectURL-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRedirectURL(String value) {
        this.redirectURL = value;
    }

    /**
     * Ruft den Wert der commCerts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DescriptionType.CommCerts }
     *     
     */
    public DescriptionType.CommCerts getCommCerts() {
        return commCerts;
    }

    /**
     * Legt den Wert der commCerts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptionType.CommCerts }
     *     
     */
    public void setCommCerts(DescriptionType.CommCerts value) {
        this.commCerts = value;
    }

    /**
     * Ruft den Wert der fileDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileDescription() {
        return fileDescription;
    }

    /**
     * Legt den Wert der fileDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileDescription(String value) {
        this.fileDescription = value;
    }

    /**
     * Ruft den Wert der import-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isImport() {
        return _import;
    }

    /**
     * Legt den Wert der import-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setImport(Boolean value) {
        this._import = value;
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
     *         &lt;element name="fileCommCert" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="hashCommCert" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
        "fileCommCert",
        "hashCommCert"
    })
    public static class CommCerts {

        @XmlElement(namespace = "http://www.secunet.com")
        protected List<String> fileCommCert;
        @XmlElement(namespace = "http://www.secunet.com")
        protected List<String> hashCommCert;

        /**
         * Gets the value of the fileCommCert property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fileCommCert property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFileCommCert().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getFileCommCert() {
            if (fileCommCert == null) {
                fileCommCert = new ArrayList<String>();
            }
            return this.fileCommCert;
        }

        /**
         * Gets the value of the hashCommCert property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hashCommCert property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHashCommCert().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getHashCommCert() {
            if (hashCommCert == null) {
                hashCommCert = new ArrayList<String>();
            }
            return this.hashCommCert;
        }

    }

}
