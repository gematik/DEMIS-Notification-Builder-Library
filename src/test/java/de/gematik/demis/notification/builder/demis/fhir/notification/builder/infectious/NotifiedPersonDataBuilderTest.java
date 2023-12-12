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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotifiedPersonDataBuilderTest {

  private NotifiedPersonDataBuilder notifiedPersonDataBuilder;

  @BeforeEach
  void setUp() {
    notifiedPersonDataBuilder = new NotifiedPersonDataBuilder();
  }

  @Test
  void shouldSetHumanName() {
    HumanName humanName =
        new HumanNameDataBuilder()
            .setFamilyName("FAMILIENNAME")
            .addGivenName("VORNAME")
            .buildHumanName();
    notifiedPersonDataBuilder.setHumanName(humanName);

    Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

    assertThat(patient.getName()).hasSize(1);
    assertThat(patient.getName().get(0).getFamily()).isEqualTo("FAMILIENNAME");
    assertThat(patient.getName().get(0).getGiven()).hasSize(1);
    assertThat(patient.getName().get(0).getGiven().get(0)).hasToString("VORNAME");
  }

  @Test
  void shouldSetGender() {
    notifiedPersonDataBuilder.setGender(Enumerations.AdministrativeGender.FEMALE);

    Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
  }

  @Test
  void shouldSetNotifiedPersonId() {
    notifiedPersonDataBuilder.setId("SomeId");

    Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

    assertThat(patient.getId()).isEqualTo("SomeId");
  }

  @Test
  void shouldSetAddress() {
    Address someAddress = new AddressDataBuilder().setCity("City").buildExampleAddress();
    notifiedPersonDataBuilder.addAddress(
        someAddress,
        "https://demis.rki.de/fhir/CodeSystem/addressUse",
        "current",
        "Derzeitiger Aufenthaltsort");

    Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

    assertThat(patient.getAddress()).hasSize(1);
    assertThat(patient.getAddress().get(0)).isEqualTo(someAddress);
  }

  @Test
  void shouldsetBirthdate() {
    LocalDate localDateHelper = LocalDate.now();
    Date birthdate = Date.from(localDateHelper.atStartOfDay(ZoneId.systemDefault()).toInstant());
    notifiedPersonDataBuilder.setBirthdate(birthdate);

    Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

    assertThat(patient.getBirthDate()).isEqualTo(birthdate);
  }

  @Test
  void shouldSetIdWithSetDefault() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("SomeId");
      notifiedPersonDataBuilder.setDefaultValues();

      Patient patient = notifiedPersonDataBuilder.buildExampleNotifiedPerson();

      assertThat(patient.getId()).isEqualTo("SomeId");
    }
  }
}
