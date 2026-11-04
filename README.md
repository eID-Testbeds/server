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
Build [common-testbed-utilities](https://github.com/eID-Testbeds/common-testbed-utilities) first with `mvn clean install`, as its artifacts aren't available from a public Maven repository.

The eID-Server can then be built by executing `mvn clean install`. Add `-DskipTests` to skip the unit tests.
The resulting `ear` file may be deployed on an application server. 

A complete guideline for an eID-Testbed deployment is given in the handbook (`server/install/handbook.md`, available in German only).

## Dependencies
The build generates a [CycloneDX](https://cyclonedx.org/) Software Bill of Materials at `target/sbom.json`, listing the dependencies of all modules with their versions, licenses and hashes.

The file is not generated in offline mode (`mvn -o`).
