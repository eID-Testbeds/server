
package com.secunet.testbedutils.bouncycertgen.xml.cv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für certHolderAuthType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="certHolderAuthType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="role" type="{http://www.secunet.com}roleType"/>
 *         &lt;element name="writeDG17" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="writeDG18" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="writeDG19" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="writeDG20" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="writeDG21" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG1" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG2" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG3" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG4" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG5" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG6" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG7" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG8" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG9" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG10" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG11" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG12" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG13" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG14" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG15" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG16" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG17" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG18" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG19" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG20" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readDG21" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="installQualifiedCertificate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="installCertificate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="pinManagement" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="canAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="privilegedTerminal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="restrictedIdentification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="communityIDVerification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ageVerification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readEPassDG3" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="readEPassDG4" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="generateQualifiedElectronicSignature" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="generateElectronicSignature" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.secunet.com}terminalType" />
 *       &lt;attribute name="forceOID" type="{http://www.w3.org/2001/XMLSchema}hexBinary" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "certHolderAuthType", namespace = "http://www.secunet.com", propOrder = {
    "role",
    "writeDG17",
    "writeDG18",
    "writeDG19",
    "writeDG20",
    "writeDG21",
    "readDG1",
    "readDG2",
    "readDG3",
    "readDG4",
    "readDG5",
    "readDG6",
    "readDG7",
    "readDG8",
    "readDG9",
    "readDG10",
    "readDG11",
    "readDG12",
    "readDG13",
    "readDG14",
    "readDG15",
    "readDG16",
    "readDG17",
    "readDG18",
    "readDG19",
    "readDG20",
    "readDG21",
    "installQualifiedCertificate",
    "installCertificate",
    "pinManagement",
    "canAllowed",
    "privilegedTerminal",
    "restrictedIdentification",
    "communityIDVerification",
    "ageVerification",
    "readEPassDG3",
    "readEPassDG4",
    "generateQualifiedElectronicSignature",
    "generateElectronicSignature"
})
public class CertHolderAuthType {

    @XmlElement(namespace = "http://www.secunet.com", required = true)
    @XmlSchemaType(name = "string")
    protected RoleType role;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean writeDG17;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean writeDG18;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean writeDG19;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean writeDG20;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean writeDG21;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG1;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG2;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG3;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG4;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG5;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG6;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG7;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG8;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG9;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG10;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG11;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG12;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG13;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG14;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG15;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG16;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG17;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG18;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG19;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG20;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readDG21;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean installQualifiedCertificate;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean installCertificate;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean pinManagement;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean canAllowed;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean privilegedTerminal;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean restrictedIdentification;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean communityIDVerification;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean ageVerification;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readEPassDG3;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean readEPassDG4;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean generateQualifiedElectronicSignature;
    @XmlElement(namespace = "http://www.secunet.com")
    protected Boolean generateElectronicSignature;
    @XmlAttribute(name = "type", required = true)
    protected TerminalType type;
    @XmlAttribute(name = "forceOID")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] forceOID;

    /**
     * Ruft den Wert der role-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RoleType }
     *     
     */
    public RoleType getRole() {
        return role;
    }

    /**
     * Legt den Wert der role-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleType }
     *     
     */
    public void setRole(RoleType value) {
        this.role = value;
    }

    /**
     * Ruft den Wert der writeDG17-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteDG17() {
        return writeDG17;
    }

    /**
     * Legt den Wert der writeDG17-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteDG17(Boolean value) {
        this.writeDG17 = value;
    }

    /**
     * Ruft den Wert der writeDG18-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteDG18() {
        return writeDG18;
    }

    /**
     * Legt den Wert der writeDG18-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteDG18(Boolean value) {
        this.writeDG18 = value;
    }

    /**
     * Ruft den Wert der writeDG19-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteDG19() {
        return writeDG19;
    }

    /**
     * Legt den Wert der writeDG19-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteDG19(Boolean value) {
        this.writeDG19 = value;
    }

    /**
     * Ruft den Wert der writeDG20-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteDG20() {
        return writeDG20;
    }

    /**
     * Legt den Wert der writeDG20-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteDG20(Boolean value) {
        this.writeDG20 = value;
    }

    /**
     * Ruft den Wert der writeDG21-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWriteDG21() {
        return writeDG21;
    }

    /**
     * Legt den Wert der writeDG21-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteDG21(Boolean value) {
        this.writeDG21 = value;
    }

    /**
     * Ruft den Wert der readDG1-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG1() {
        return readDG1;
    }

    /**
     * Legt den Wert der readDG1-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG1(Boolean value) {
        this.readDG1 = value;
    }

    /**
     * Ruft den Wert der readDG2-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG2() {
        return readDG2;
    }

    /**
     * Legt den Wert der readDG2-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG2(Boolean value) {
        this.readDG2 = value;
    }

    /**
     * Ruft den Wert der readDG3-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG3() {
        return readDG3;
    }

    /**
     * Legt den Wert der readDG3-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG3(Boolean value) {
        this.readDG3 = value;
    }

    /**
     * Ruft den Wert der readDG4-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG4() {
        return readDG4;
    }

    /**
     * Legt den Wert der readDG4-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG4(Boolean value) {
        this.readDG4 = value;
    }

    /**
     * Ruft den Wert der readDG5-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG5() {
        return readDG5;
    }

    /**
     * Legt den Wert der readDG5-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG5(Boolean value) {
        this.readDG5 = value;
    }

    /**
     * Ruft den Wert der readDG6-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG6() {
        return readDG6;
    }

    /**
     * Legt den Wert der readDG6-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG6(Boolean value) {
        this.readDG6 = value;
    }

    /**
     * Ruft den Wert der readDG7-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG7() {
        return readDG7;
    }

    /**
     * Legt den Wert der readDG7-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG7(Boolean value) {
        this.readDG7 = value;
    }

    /**
     * Ruft den Wert der readDG8-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG8() {
        return readDG8;
    }

    /**
     * Legt den Wert der readDG8-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG8(Boolean value) {
        this.readDG8 = value;
    }

    /**
     * Ruft den Wert der readDG9-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG9() {
        return readDG9;
    }

    /**
     * Legt den Wert der readDG9-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG9(Boolean value) {
        this.readDG9 = value;
    }

    /**
     * Ruft den Wert der readDG10-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG10() {
        return readDG10;
    }

    /**
     * Legt den Wert der readDG10-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG10(Boolean value) {
        this.readDG10 = value;
    }

    /**
     * Ruft den Wert der readDG11-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG11() {
        return readDG11;
    }

    /**
     * Legt den Wert der readDG11-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG11(Boolean value) {
        this.readDG11 = value;
    }

    /**
     * Ruft den Wert der readDG12-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG12() {
        return readDG12;
    }

    /**
     * Legt den Wert der readDG12-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG12(Boolean value) {
        this.readDG12 = value;
    }

    /**
     * Ruft den Wert der readDG13-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG13() {
        return readDG13;
    }

    /**
     * Legt den Wert der readDG13-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG13(Boolean value) {
        this.readDG13 = value;
    }

    /**
     * Ruft den Wert der readDG14-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG14() {
        return readDG14;
    }

    /**
     * Legt den Wert der readDG14-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG14(Boolean value) {
        this.readDG14 = value;
    }

    /**
     * Ruft den Wert der readDG15-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG15() {
        return readDG15;
    }

    /**
     * Legt den Wert der readDG15-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG15(Boolean value) {
        this.readDG15 = value;
    }

    /**
     * Ruft den Wert der readDG16-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG16() {
        return readDG16;
    }

    /**
     * Legt den Wert der readDG16-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG16(Boolean value) {
        this.readDG16 = value;
    }

    /**
     * Ruft den Wert der readDG17-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG17() {
        return readDG17;
    }

    /**
     * Legt den Wert der readDG17-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG17(Boolean value) {
        this.readDG17 = value;
    }

    /**
     * Ruft den Wert der readDG18-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG18() {
        return readDG18;
    }

    /**
     * Legt den Wert der readDG18-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG18(Boolean value) {
        this.readDG18 = value;
    }

    /**
     * Ruft den Wert der readDG19-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG19() {
        return readDG19;
    }

    /**
     * Legt den Wert der readDG19-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG19(Boolean value) {
        this.readDG19 = value;
    }

    /**
     * Ruft den Wert der readDG20-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG20() {
        return readDG20;
    }

    /**
     * Legt den Wert der readDG20-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG20(Boolean value) {
        this.readDG20 = value;
    }

    /**
     * Ruft den Wert der readDG21-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadDG21() {
        return readDG21;
    }

    /**
     * Legt den Wert der readDG21-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadDG21(Boolean value) {
        this.readDG21 = value;
    }

    /**
     * Ruft den Wert der installQualifiedCertificate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInstallQualifiedCertificate() {
        return installQualifiedCertificate;
    }

    /**
     * Legt den Wert der installQualifiedCertificate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInstallQualifiedCertificate(Boolean value) {
        this.installQualifiedCertificate = value;
    }

    /**
     * Ruft den Wert der installCertificate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInstallCertificate() {
        return installCertificate;
    }

    /**
     * Legt den Wert der installCertificate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInstallCertificate(Boolean value) {
        this.installCertificate = value;
    }

    /**
     * Ruft den Wert der pinManagement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPinManagement() {
        return pinManagement;
    }

    /**
     * Legt den Wert der pinManagement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPinManagement(Boolean value) {
        this.pinManagement = value;
    }

    /**
     * Ruft den Wert der canAllowed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanAllowed() {
        return canAllowed;
    }

    /**
     * Legt den Wert der canAllowed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanAllowed(Boolean value) {
        this.canAllowed = value;
    }

    /**
     * Ruft den Wert der privilegedTerminal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrivilegedTerminal() {
        return privilegedTerminal;
    }

    /**
     * Legt den Wert der privilegedTerminal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrivilegedTerminal(Boolean value) {
        this.privilegedTerminal = value;
    }

    /**
     * Ruft den Wert der restrictedIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRestrictedIdentification() {
        return restrictedIdentification;
    }

    /**
     * Legt den Wert der restrictedIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRestrictedIdentification(Boolean value) {
        this.restrictedIdentification = value;
    }

    /**
     * Ruft den Wert der communityIDVerification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCommunityIDVerification() {
        return communityIDVerification;
    }

    /**
     * Legt den Wert der communityIDVerification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCommunityIDVerification(Boolean value) {
        this.communityIDVerification = value;
    }

    /**
     * Ruft den Wert der ageVerification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAgeVerification() {
        return ageVerification;
    }

    /**
     * Legt den Wert der ageVerification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAgeVerification(Boolean value) {
        this.ageVerification = value;
    }

    /**
     * Ruft den Wert der readEPassDG3-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadEPassDG3() {
        return readEPassDG3;
    }

    /**
     * Legt den Wert der readEPassDG3-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadEPassDG3(Boolean value) {
        this.readEPassDG3 = value;
    }

    /**
     * Ruft den Wert der readEPassDG4-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadEPassDG4() {
        return readEPassDG4;
    }

    /**
     * Legt den Wert der readEPassDG4-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadEPassDG4(Boolean value) {
        this.readEPassDG4 = value;
    }

    /**
     * Ruft den Wert der generateQualifiedElectronicSignature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGenerateQualifiedElectronicSignature() {
        return generateQualifiedElectronicSignature;
    }

    /**
     * Legt den Wert der generateQualifiedElectronicSignature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGenerateQualifiedElectronicSignature(Boolean value) {
        this.generateQualifiedElectronicSignature = value;
    }

    /**
     * Ruft den Wert der generateElectronicSignature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGenerateElectronicSignature() {
        return generateElectronicSignature;
    }

    /**
     * Legt den Wert der generateElectronicSignature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGenerateElectronicSignature(Boolean value) {
        this.generateElectronicSignature = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TerminalType }
     *     
     */
    public TerminalType getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TerminalType }
     *     
     */
    public void setType(TerminalType value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der forceOID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getForceOID() {
        return forceOID;
    }

    /**
     * Legt den Wert der forceOID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForceOID(byte[] value) {
        this.forceOID = value;
    }

}
