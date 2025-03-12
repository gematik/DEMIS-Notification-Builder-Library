package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

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
 * #L%
 */

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

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getName()).hasSize(1);
    assertThat(patient.getName().get(0).getFamily()).isEqualTo("FAMILIENNAME");
    assertThat(patient.getName().get(0).getGiven()).hasSize(1);
    assertThat(patient.getName().get(0).getGiven().get(0)).hasToString("VORNAME");
  }

  @Test
  void shouldSetGender() {
    notifiedPersonDataBuilder.setGender(Enumerations.AdministrativeGender.FEMALE);

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
  }

  @Test
  void shouldSetNotifiedPersonId() {
    notifiedPersonDataBuilder.setId("SomeId");

    Patient patient = notifiedPersonDataBuilder.build();

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

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getAddress()).hasSize(1);
    assertThat(patient.getAddress().get(0)).isEqualTo(someAddress);
  }

  @Test
  void shouldsetBirthdate() {
    LocalDate localDateHelper = LocalDate.now();
    Date birthdate = Date.from(localDateHelper.atStartOfDay(ZoneId.systemDefault()).toInstant());
    notifiedPersonDataBuilder.setBirthdate(birthdate);

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getBirthDate()).isEqualTo(birthdate);
  }

  @Test
  void setDefaults_shouldSetValues() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("SomeId");
      notifiedPersonDataBuilder.setDefaults();
      Patient patient = notifiedPersonDataBuilder.build();
      assertThat(patient.getId()).isEqualTo("SomeId");
    }
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    Patient patient = new NotifiedPersonDataBuilder().setId(id).setDefaults().build();
    assertThat(patient.getId()).isEqualTo(id);
  }
}
