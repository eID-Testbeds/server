//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.02 um 01:01:12 PM CET 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modulePath" type="{http://www.secunet.com}modulePath"/>
 *         &lt;element name="manualExplanation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profiles" type="{http://www.secunet.com}profiles"/>
 *         &lt;element name="eservice" type="{http://www.secunet.com}eService"/>
 *         &lt;element name="clientExplanation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eidcard" type="{http://www.secunet.com}eidCard" minOccurs="0"/>
 *         &lt;element name="certificateBaseNames" type="{http://www.secunet.com}certNames" minOccurs="0"/>
 *         &lt;element name="steps" type="{http://www.secunet.com}stepsType"/>
 *         &lt;element name="variables" type="{http://www.secunet.com}variables" minOccurs="0"/>
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
    "name",
    "description",
    "modulePath",
    "manualExplanation",
    "profiles",
    "eservice",
    "clientExplanation",
    "eidcard",
    "certificateBaseNames",
    "steps",
    "variables"
})
@XmlRootElement(name = "testcaseDefinition")
public class TestcaseDefinition {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected ModulePath modulePath;
    protected String manualExplanation;
    @XmlElement(required = true)
    protected Profiles profiles;
    @XmlElement(required = true, defaultValue = "A")
    @XmlSchemaType(name = "string")
    protected EService eservice;
    protected String clientExplanation;
    @XmlSchemaType(name = "string")
    protected EidCard eidcard;
    protected CertNames certificateBaseNames;
    @XmlElement(required = true)
    protected StepsType steps;
    protected Variables variables;

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
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der modulePath-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ModulePath }
     *     
     */
    public ModulePath getModulePath() {
        return modulePath;
    }

    /**
     * Legt den Wert der modulePath-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ModulePath }
     *     
     */
    public void setModulePath(ModulePath value) {
        this.modulePath = value;
    }

    /**
     * Ruft den Wert der manualExplanation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManualExplanation() {
        return manualExplanation;
    }

    /**
     * Legt den Wert der manualExplanation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManualExplanation(String value) {
        this.manualExplanation = value;
    }

    /**
     * Ruft den Wert der profiles-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Profiles }
     *     
     */
    public Profiles getProfiles() {
        return profiles;
    }

    /**
     * Legt den Wert der profiles-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Profiles }
     *     
     */
    public void setProfiles(Profiles value) {
        this.profiles = value;
    }

    /**
     * Ruft den Wert der eservice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EService }
     *     
     */
    public EService getEservice() {
        return eservice;
    }

    /**
     * Legt den Wert der eservice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EService }
     *     
     */
    public void setEservice(EService value) {
        this.eservice = value;
    }

    /**
     * Ruft den Wert der clientExplanation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientExplanation() {
        return clientExplanation;
    }

    /**
     * Legt den Wert der clientExplanation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientExplanation(String value) {
        this.clientExplanation = value;
    }

    /**
     * Ruft den Wert der eidcard-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EidCard }
     *     
     */
    public EidCard getEidcard() {
        return eidcard;
    }

    /**
     * Legt den Wert der eidcard-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EidCard }
     *     
     */
    public void setEidcard(EidCard value) {
        this.eidcard = value;
    }

    /**
     * Ruft den Wert der certificateBaseNames-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertNames }
     *     
     */
    public CertNames getCertificateBaseNames() {
        return certificateBaseNames;
    }

    /**
     * Legt den Wert der certificateBaseNames-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertNames }
     *     
     */
    public void setCertificateBaseNames(CertNames value) {
        this.certificateBaseNames = value;
    }

    /**
     * Ruft den Wert der steps-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StepsType }
     *     
     */
    public StepsType getSteps() {
        return steps;
    }

    /**
     * Legt den Wert der steps-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StepsType }
     *     
     */
    public void setSteps(StepsType value) {
        this.steps = value;
    }

    /**
     * Ruft den Wert der variables-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Variables }
     *     
     */
    public Variables getVariables() {
        return variables;
    }

    /**
     * Legt den Wert der variables-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Variables }
     *     
     */
    public void setVariables(Variables value) {
        this.variables = value;
    }

}
