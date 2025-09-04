/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 *
 * This should be the only class which is mostly a copy of Eidas-Middleware/Eidas-Starterkit
 */

package com.secunet.eidserver.testbed.common.eidas;

import se.swedenconnect.opensaml.eidas.ext.attributes.*;

import javax.xml.namespace.QName;


public enum EidasNaturalPersonAttributes
{
  FIRST_NAME(AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_NAME,
             AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_FRIENDLY_NAME,
             CurrentGivenNameType.TYPE_NAME,
             CurrentGivenNameType.class),
  BIRTH_NAME(AttributeConstants.EIDAS_BIRTH_NAME_ATTRIBUTE_NAME,
             AttributeConstants.EIDAS_BIRTH_NAME_ATTRIBUTE_FRIENDLY_NAME,
             BirthNameType.TYPE_NAME,
             BirthNameType.class),
  FAMILY_NAME(AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_NAME,
              AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_FRIENDLY_NAME,
              CurrentFamilyNameType.TYPE_NAME,
              CurrentFamilyNameType.class),
  DATE_OF_BIRTH(AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_NAME,
                AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_FRIENDLY_NAME,
                DateOfBirthType.TYPE_NAME,
                DateOfBirthType.class),
  PLACE_OF_BIRTH(AttributeConstants.EIDAS_PLACE_OF_BIRTH_ATTRIBUTE_NAME,
                 AttributeConstants.EIDAS_PLACE_OF_BIRTH_ATTRIBUTE_FRIENDLY_NAME,
                 PlaceOfBirthType.TYPE_NAME,
                 PlaceOfBirthType.class),
  GENDER(AttributeConstants.EIDAS_GENDER_ATTRIBUTE_NAME,
         AttributeConstants.EIDAS_GENDER_ATTRIBUTE_FRIENDLY_NAME,
         GenderType.TYPE_NAME,
         GenderType.class),
  CURRENT_ADDRESS(AttributeConstants.EIDAS_CURRENT_ADDRESS_ATTRIBUTE_NAME,
                  AttributeConstants.EIDAS_CURRENT_ADDRESS_ATTRIBUTE_FRIENDLY_NAME,
                  CurrentAddressType.TYPE_NAME,
                  CurrentAddressType.class),
  PERSON_IDENTIFIER(AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_NAME,
                    AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_FRIENDLY_NAME,
                    PersonIdentifierType.TYPE_NAME,
                    PersonIdentifierType.class);


  private final String name;
  private final String friendlyName;
  private final QName qName;

  private final Class<? extends EidasAttributeValueType> attributeClass;

  EidasNaturalPersonAttributes(String name, String friendlyName, QName qName, Class<? extends EidasAttributeValueType> attributeClass) {
    this.name = name;
    this.friendlyName = friendlyName;
    this.qName = qName;
    this.attributeClass = attributeClass;
  }

  public String getName() {
    return name;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public QName getqName() {
    return qName;
  }

  public Class<? extends EidasAttributeValueType> getAttributeClass() {
    return attributeClass;
  }

  /**
   * Create an instance of the EidasAttribute corresponding to the NaturalPersonAttribute type
   *
   * @return instance of EidasAttribute corresponding to the NaturalPersonAttribute type
   */
  public EidasAttributeValueType getInstance()
  {
    try
    {
      return attributeClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new IllegalStateException("Unable to instantiate attribute type.", e);
    }
  }

  public static EidasNaturalPersonAttributes getValueOf(String s) throws IllegalArgumentException
  {
    for ( EidasNaturalPersonAttributes enpa : EidasNaturalPersonAttributes.values() )
    {
      if (enpa.name.equals(s))
      {
        return enpa;
      }
    }
    throw new IllegalArgumentException("Unsupported EidasNaturalPersonAttributes value:" + s);
  }
}
