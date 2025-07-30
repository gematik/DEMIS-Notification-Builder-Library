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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotifiedPersonNominalDataBuilderTest {

  private NotifiedPersonNominalDataBuilder notifiedPersonDataBuilder;

  @BeforeEach
  void setUp() {
    notifiedPersonDataBuilder = new NotifiedPersonNominalDataBuilder();
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

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldSetBirthdate() {
    DateType birthdate = new DateType("1975-12-24");
    notifiedPersonDataBuilder.setBirthdate(birthdate);

    Patient patient = notifiedPersonDataBuilder.build();

    DateType birthDateElement = patient.getBirthDateElement();
    assertThat(birthDateElement).usingRecursiveComparison().isEqualTo(birthdate);
  }

  @Test
  void shouldSetIdWhenNonGiven() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("SomeId");
      Patient patient = notifiedPersonDataBuilder.build();
      assertThat(patient.getId()).isEqualTo("SomeId");
    }
  }

  @Test
  void shouldKeepIdWhenSet() {
    String id = "init-id";
    IBase resource = new NotifiedPersonNominalDataBuilder().setId(id).build();
    if (resource instanceof Patient patient) {
      assertThat(patient.getId()).isEqualTo("Patient/init-id");
    } else {
      fail("Resource is not a Patient");
    }
  }

  @Test
  void shouldUseSetId() {
    String id = "SomeId";
    notifiedPersonDataBuilder.setId(id);

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldAddAddress() {
    Address someAddress = new AddressDataBuilder().setCity("City").buildExampleAddress();
    notifiedPersonDataBuilder.addAddress(someAddress);

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getAddress()).hasSize(1);
    assertThat(patient.getAddress().get(0)).isEqualTo(someAddress);
  }

  @Test
  void shouldAddTelecom() {
    ContactPoint contactPoint = new ContactPoint();
    notifiedPersonDataBuilder.addTelecom(contactPoint);

    Patient patient = notifiedPersonDataBuilder.build();

    assertThat(patient.getTelecom()).hasSize(1);
    assertThat(patient.getTelecom().get(0)).isEqualTo(contactPoint);
  }
}
