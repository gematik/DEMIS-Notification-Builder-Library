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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class PractitionerBuilderTest {

  @Test
  void shouldCreateNotifier() {
    PractitionerBuilder practitionerBuilder = new PractitionerBuilder();
    practitionerBuilder.asNotifier();
    Practitioner practitioner = practitionerBuilder.build();

    assertThat(practitioner.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/Notifier");
  }

  @Test
  void shouldCreateSubmittingPerson() {
    PractitionerBuilder practitionerBuilder = new PractitionerBuilder();
    practitionerBuilder.asSubmittingPerson();
    Practitioner practitioner = practitionerBuilder.build();

    assertThat(practitioner.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/SubmittingPerson");
  }

  @Test
  void shouldCreateWithDefaultValues() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      PractitionerBuilder practitionerBuilder = new PractitionerBuilder();
      practitionerBuilder.setDefaultData();
      Practitioner practitioner = practitionerBuilder.build();

      assertThat(practitioner.getId()).isEqualTo("1");
    }
  }

  @Test
  void shouldCreatePractitionerWithGivenData() {
    PractitionerBuilder practitionerBuilder = new PractitionerBuilder();
    HumanName practitionerName = new HumanName();
    practitionerBuilder.setPractitionerName(practitionerName);
    ContactPoint contactPoint = new ContactPoint();
    practitionerBuilder.addContactPoint(contactPoint);
    Address address = new Address();
    practitionerBuilder.setAddress(address);
    practitionerBuilder.setActive(true);

    Practitioner practitioner = practitionerBuilder.build();
    assertThat(practitioner.getName()).containsOnly(practitionerName);
    assertThat(practitioner.getTelecom()).containsOnly(contactPoint);
    assertThat(practitioner.getAddress()).containsOnly(address);
    assertThat(practitioner.getActive()).isTrue();
  }
}
