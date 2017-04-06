//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.03.02 um 01:01:12 PM CET 
//


package com.secunet.eidserver.testbed.common.types.testcase;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für stepsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="stepsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="step" type="{http://www.secunet.com}testStepType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="reference" type="{http://www.secunet.com}referenceType" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stepsType", propOrder = {
    "stepOrReference"
})
public class StepsType {

    @XmlElements({
        @XmlElement(name = "step", type = TestStepType.class),
        @XmlElement(name = "reference", type = ReferenceType.class)
    })
    protected List<Object> stepOrReference;

    /**
     * Gets the value of the stepOrReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stepOrReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStepOrReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TestStepType }
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Object> getStepOrReference() {
        if (stepOrReference == null) {
            stepOrReference = new ArrayList<Object>();
        }
        return this.stepOrReference;
    }

}
