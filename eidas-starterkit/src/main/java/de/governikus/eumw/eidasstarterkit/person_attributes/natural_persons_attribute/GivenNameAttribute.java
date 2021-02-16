/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.eidasstarterkit.person_attributes.natural_persons_attribute;

import de.governikus.eumw.eidascommon.Utils;
import de.governikus.eumw.eidasstarterkit.EidasNaturalPersonAttributes;
import de.governikus.eumw.eidasstarterkit.person_attributes.AbstractNonLatinScriptAttribute;
import de.governikus.eumw.eidasstarterkit.person_attributes.EidasPersonAttributes;


public class GivenNameAttribute extends AbstractNonLatinScriptAttribute
{

  public GivenNameAttribute()
  {}

  public GivenNameAttribute(String value)
  {
    super(value);
  }

  @Override
  public String getTemplateName()
  {
    return Utils.isNullOrEmpty(getNonLatinScript()) ? "givenname" : "givenname_transliterated";

  }

  public GivenNameAttribute(String latinScript, String nonLatinScript)
  {
    super(latinScript, nonLatinScript);
  }


  @Override
  public EidasAttributeType type()
  {
    return EidasAttributeType.GIVEN_NAME;
  }

  @Override
  public EidasPersonAttributes getPersonAttributeType()
  {
    return EidasNaturalPersonAttributes.FIRST_NAME;
  }
}
