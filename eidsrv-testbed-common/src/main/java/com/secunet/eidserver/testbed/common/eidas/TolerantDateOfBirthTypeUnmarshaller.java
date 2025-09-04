package com.secunet.eidserver.testbed.common.eidas;

/*
 * Copyright 2016-2021 Litsec AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.LocalDate;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import se.swedenconnect.opensaml.eidas.ext.attributes.DateOfBirthType;

/**
 * Thread-safe unmarshaller for {@link DateOfBirthType} objects.
 *
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class TolerantDateOfBirthTypeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        DateOfBirthType date = (DateOfBirthType) xmlObject;
        try {
            System.err.println("elementContent: " + elementContent);
            date.setDate(LocalDate.parse(elementContent));
        }
        catch (Exception e) {
            System.err.println("Failed to parse date: " + elementContent + " - trying to parse as year only");
            date.setDate(LocalDate.parse(elementContent.substring(0,4) + "-12-31"));
        }
    }

}

