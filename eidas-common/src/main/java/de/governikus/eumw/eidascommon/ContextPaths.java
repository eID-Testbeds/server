/*
 * Copyright (c) 2020 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.eidascommon;

public final class ContextPaths
{

  /**
   * The context path of the eIDAS Middleware
   */
  public static final String EIDAS_CONTEXT_PATH = "/eidas-middleware";

  /**
   * The context path of the eIDAS admin interface
   */
  public static final String ADMIN_CONTEXT_PATH = "/admin-interface";

  /**
   * The path to the eIDAS Middleware metadata servlet
   */
  public static final String METADATA = "/Metadata";

  /**
   * The path to the eIDAS SAML request receiver servlet
   */
  public static final String REQUEST_RECEIVER = "/RequestReceiver";

  /**
   * The path to the eIDAS SAML response sender servlet
   */
  public static final String RESPONSE_SENDER = "/ResponseSender";

  /**
   * The path to the eIDAS TcToken servlet
   */
  public static final String TC_TOKEN = "/TcToken";

  /**
   * The path to the POSeIDAS PAOS receiver
   */
  public static final String PAOS_SERVLET = "/paosreceiver";

  /**
   * The path to the AusweisApp2 redirect controller
   */
  public static final String AUSWEISAPP_REDIRECT = "/ausweisapp-redirect";

  /**
   * The path to the login page of the admin interface
   */
  public static final String LOGIN = "/login";

  private ContextPaths()
  {
    // empty private constructor
  }
}
