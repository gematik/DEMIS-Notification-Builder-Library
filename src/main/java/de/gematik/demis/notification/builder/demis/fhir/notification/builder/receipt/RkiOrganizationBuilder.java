/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import org.hl7.fhir.r4.model.*;

public class RkiOrganizationBuilder {

  public Organization createRkiOrganization() {

    Organization rkiOrganization = new Organization();
    rkiOrganization.setId("1.");
    rkiOrganization.addIdentifier(
        new Identifier()
            .setSystem("https://demis.rki.de/fhir/CodeSystem/reportingSite")
            .setValue("1."));
    rkiOrganization.setName("Robert Koch-Institut");
    rkiOrganization.addTelecom(
        new TelecomDataBuilder()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue("demis@rki.de")
            .buildContactPoint());
    rkiOrganization.addAddress(
        new AddressDataBuilder()
            .setStreet("Nordufer")
            .setHouseNumber("20")
            .setPostalCode("13353")
            .setCity("Berlin")
            .setCountry("20422")
            .buildAddress());

    return rkiOrganization;
  }
}
