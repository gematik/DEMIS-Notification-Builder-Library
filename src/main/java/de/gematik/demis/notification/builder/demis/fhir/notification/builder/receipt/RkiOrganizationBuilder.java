package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 gematik GmbH
 * %%
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import org.hl7.fhir.r4.model.*;

public class RkiOrganizationBuilder {

  private static final String ORGANIZATION_ID = "1.";
  private static final String IDENTIFIER_SYSTEM =
      "https://demis.rki.de/fhir/CodeSystem/reportingSite";
  private static final String ORGANIZATION_NAME = "Robert Koch-Institut";
  private static final String TELECOM_EMAIL = "demis-support@rki.de";
  private static final String ADDRESS_STREET = "Nordufer";
  private static final String ADDRESS_HOUSE_NUMBER = "20";
  private static final String ADDRESS_POSTAL_CODE = "13353";
  private static final String ADDRESS_CITY = "Berlin";
  private static final String ADDRESS_COUNTRY = "DE";

  public Organization createRkiOrganization() {

    Organization rkiOrganization = new Organization();
    rkiOrganization.setId(ORGANIZATION_ID);
    rkiOrganization.addIdentifier(
        new Identifier().setSystem(IDENTIFIER_SYSTEM).setValue(ORGANIZATION_ID));
    rkiOrganization.setName(ORGANIZATION_NAME);
    rkiOrganization.addTelecom(
        new TelecomDataBuilder()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue(TELECOM_EMAIL)
            .build());
    rkiOrganization.addAddress(
        new AddressDataBuilder()
            .setStreet(ADDRESS_STREET)
            .setHouseNumber(ADDRESS_HOUSE_NUMBER)
            .setPostalCode(ADDRESS_POSTAL_CODE)
            .setCity(ADDRESS_CITY)
            .setCountry(ADDRESS_COUNTRY)
            .build());

    return rkiOrganization;
  }
}
