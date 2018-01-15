//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.08.14 um 02:18:00 PM CEST 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für referenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="referenceType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.secunet.com>testcaseNameType">
 *       &lt;attribute name="targetInterface" use="required" type="{http://www.secunet.com}targetInterfaceType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referenceType", propOrder = {
    "value"
})
public class ReferenceType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "targetInterface", required = true)
    protected TargetInterfaceType targetInterface;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

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

}
