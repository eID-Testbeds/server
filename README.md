# Server
The eID-Server is the technical component used by eServices to integrate the Online-Authentication according to
client [BSI TR-03124-1](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03124/TR-03124_node.html) and server [BSI TR-03130-1](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03130/TR-03130_node.html) specification between eServices and an eID-Cards
based on Extended Access Control v2 - e.g. the German ID card or the German residence Permit - into the IT systems. 

eID-Servers can be implemented by different vendors. In order to ensure interoperability with other components of
the system, the BSI specifies in conformity tests.

The eID-Server-Testbed is a test tool that allows to perform conformity tests according to [BSI TR-03130-4](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03130/TR-03130_node.html) of
eID-Servers based on [BSI TR-03130-1](https://www.bsi.bund.de/DE/Themen/Unternehmen-und-Organisationen/Standards-und-Zertifizierung/Technische-Richtlinien/TR-nach-Thema-sortiert/tr03130/TR-03130_node.html).

## Requirements

### Building
+ JDK 17.0.12+ (July 16, 2024)
+ Maven 3.6.3+ (November 25, 2019)

### Runtime
+ JRE 17.0.12+ (July 16, 2024)
+ Payara 5 (v5.2022.5) Full (http://www.payara.fish/downloads)

## Build and Deployment
The eID-Server can be build by executing `mvn clean install`.
The resulting `ear` file may be deployed on an application server. 

A complete guideline for an eID-Testbed deployment is given in the handbook (`server/install/handbook.md`, available in German only).

## Dependencies

| Description        | Library                                             | Version | Date               |
| :----------------- | :-------------------------------------------------- | :------ | :----------------- |
| Testing            | org.testng.testng                                   | 7.9.0   | Dec 26, 2023       |
| Test-Generation    | org.mockito.mockito-core                            | 5.12    | May 11, 2024       |
| Test-Generation    | org.mockito.mockito-testng                          | 0.5.2   | Aug 29, 2023       |
| Logging            | org.apache.logging.log4j.log4j-api                  | 2.24.0  | Sep 06, 2024       |
| Logging            | org.apache.logging.log4j.log4j-core                 | 2.24.0  | Sep 06, 2024       |
| Logging            | org.apache.logging.log4j.log4j-slf4j-impl           | 2.24.0  | Sep 06, 2024       |
| Logging            | org.slf4j.slf-api                                   | 1.7.36  | Feb 08, 2022       |
| Logging            | org.slf4j.slf-ext                                   | 1.7.36  | Feb 08, 2022       |
| JakartaEE Platform | jakarta.platform.jakarta.jakartaee-api              | 8.0.0   | Sep 10, 2019       |
| JakartaEE Platform | jakarta.platform.jakarta.jakarta.xml.bind-api       | 2.3.2   | Jan 08, 2019       |
| JaxB               | com.sun.xml.bind.jaxb-impl                          | 2.3.3   | Apr 27, 2020       |
| Cryptography       | org.bouncycastle.bcprov-jdk18on                     | 1.78.1  | Apr 18, 2024       |
| Cryptography       | org.bouncycastle.bcutil-jdk18on                     | 1.78.1  | Apr 18, 2024       |
| XML Security       | org.apache.santuario.xmlsec                         | 3.0.4   | Feb 22, 2024       |
| SAML               | se.swedenconnect.opensaml.opensaml-security-ext     | 4.1.1   | Jun 12, 2024       |
| SAML               | se.swedenconnect.opensaml.opensaml-eidas            | 3.1.0   | Nov 16, 2021 **!** |
| SAML               | org.opensaml.opensaml-core-api                      | 5.1.2   | Apr 15, 2024 **!** |
| SAML               | org.opensaml.opensaml-\*                            | 5.1.2   | Apr 15, 2024 **!** |
|                    | **resetbuttonfortextfield**                         |         |                    |
| Vaadin             | com.vaadin.vaadin-compatibility-server              | 8.16.0  | Jun 10, 2022       |
| Vaadin             | com.vaadin.vaadin-compatibility-client              | 8.16.0  | Jun 10, 2022       |
|                    | **eidsrv-testbed-common**                           |         |                    |
| Apache Commons     | commons-io.commons.io                               | 2.5     | Apr 22, 2016       |
| Apache Commons     | org.apache.commons.commons-text                     | 1.12.0  | Apr 16, 2024       |
|                    | **eidsrv-testbed-database**                         |         |                    |
| Database           | com.h2database.h2                                   | 2.1.212 | Apr 10, 2022       |
|                    | **eidsrv-testbed-reportgenerator**                  |         |                    |
| Reporting          | net.sf.jasperreports.jasperreports                  | 6.2.0   | Nov 06, 2015       |
|                    | **eidsrv-testbed-reportgenerator**                  |         |                    |
| XML Security       | org.apache.santuario.xmlsec                         | 3.0.2   | Mar 30, 2023       |
| Glassfish          | org.glassfish.metro.webservices-api-osgi            | 2.4.3   | Jan 21, 2019       |
| Glassfish          | org.glassfish.metro.webservices-osgi                | 2.4.3   | Jan 22, 2019       |
| XML Streaming      | org.jvnet.staxex.stax-ex                            | 2.1.0   | Apr 11, 2022       |
|                    | **eidsrv-testbed-webui**                            |         |                    |
| Vaadin             | com.vaadin.vaadin-compatibility-server              | 8.16.0  | Jun 10, 2022       |
| Vaadin             | com.vaadin.vaadin-themes                            | 8.16.0  | Jun 10, 2022       |
| Vaadin             | com.vaadin.vaadin-compatibility-client              | 8.16.0  | Jun 10, 2022       |
| Vaadin             | com.vaadin.vaadin-push                              | 8.16.0  | Jun 10, 2022       |
| Vaadin             | org.vaadin.addon.confirmdialog                      | 3.2.0   | Jun 13, 2017       |
| Vaadin             | org.vaadin.viritin                                  | 2.12    | Mar 18, 2021       |
| Vaadin             | org.vaadin.viritin-v7-compatibility                 | 1.0     | Nov 16, 2017       |
| Vaadin             | com.vaadin.vaadin-cdi                               | 3.0.1   | Jun 13, 2018       |
| CDI                | org.apache.deltaspike.core.deltaspike-core-api      | 1.7.2   | Nov 01, 2016       |
| CDI                | org.apache.deltaspike.core.deltaspike-core-impl     | 1.7.2   | Nov 01, 2016       |
| Web                | io.javalin.javalin                                  | 4.6.7   | Oct 24, 2022       |
| Logging            | org.slf4j.slf4j-api                                 | 1.7.36  | Feb 08, 2022       |
| Logging            | org.slf4j.jcl-over-slf4j                            | 1.7.36  | Feb 08, 2022       |
| Logging            | org.slf4j.slf4j-log4j12                             | 1.7.36  | Feb 08, 2022       |
| Defect Detection   | com.google.code.findbugs.jsr305                     | 3.0.2   | Mar 31, 2017       |