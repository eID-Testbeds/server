//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.08.14 um 02:18:00 PM CEST 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="TargetInterface" type="{http://www.secunet.com}targetInterfaceType"/>
 *         &lt;element name="Optional" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Schema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TlsStepToken" type="{http://www.secunet.com}stepToken" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="HttpStepToken" type="{http://www.secunet.com}stepToken" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ProtocolStepToken" type="{http://www.secunet.com}stepToken" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ToSave" type="{http://www.secunet.com}optAttrString" maxOccurs="unbounded" minOccurs="0"/>
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
    "targetInterface",
    "optional",
    "schema",
    "tlsStepToken",
    "httpStepToken",
    "protocolStepToken",
    "toSave"
})
@XmlRootElement(name = "Step")
public class Step {

    @XmlElement(name = "TargetInterface", required = true)
    @XmlSchemaType(name = "string")
    protected TargetInterfaceType targetInterface;
    @XmlElement(name = "Optional")
    protected Boolean optional;
    @XmlElement(name = "Schema")
    protected String schema;
    @XmlElement(name = "TlsStepToken")
    protected List<StepToken> tlsStepToken;
    @XmlElement(name = "HttpStepToken")
    protected List<StepToken> httpStepToken;
    @XmlElement(name = "ProtocolStepToken")
    protected List<StepToken> protocolStepToken;
    @XmlElement(name = "ToSave")
    protected List<OptAttrString> toSave;

    /**
     * Ruft den Wert der targetInterface-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TargetInterfaceType }
     *     
     */
    public TargetInterfaceType getTargetInterface() {
        return targetInterface;
    }

    /**
     * Legt den Wert der targetInterface-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetInterfaceType }
     *     
     */
    public void setTargetInterface(TargetInterfaceType value) {
        this.targetInterface = value;
    }

    /**
     * Ruft den Wert der optional-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOptional() {
        return optional;
    }

    /**
     * Legt den Wert der optional-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Ruft den Wert der schema-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Legt den Wert der schema-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchema(String value) {
        this.schema = value;
    }

    /**
     * Gets the value of the tlsStepToken property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tlsStepToken property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTlsStepToken().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepToken }
     * 
     * 
     */
    public List<StepToken> getTlsStepToken() {
        if (tlsStepToken == null) {
            tlsStepToken = new ArrayList<StepToken>();
        }
        return this.tlsStepToken;
    }

    /**
     * Gets the value of the httpStepToken property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the httpStepToken property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHttpStepToken().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepToken }
     * 
     * 
     */
    public List<StepToken> getHttpStepToken() {
        if (httpStepToken == null) {
            httpStepToken = new ArrayList<StepToken>();
        }
        return this.httpStepToken;
    }

    /**
     * Gets the value of the protocolStepToken property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the protocolStepToken property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProtocolStepToken().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepToken }
     * 
     * 
     */
    public List<StepToken> getProtocolStepToken() {
        if (protocolStepToken == null) {
            protocolStepToken = new ArrayList<StepToken>();
        }
        return this.protocolStepToken;
    }

    /**
     * Gets the value of the toSave property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the toSave property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getToSave().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OptAttrString }
     * 
     * 
     */
    public List<OptAttrString> getToSave() {
        if (toSave == null) {
            toSave = new ArrayList<OptAttrString>();
        }
        return this.toSave;
    }

}
