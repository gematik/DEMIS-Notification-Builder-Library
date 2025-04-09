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

import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class NotifiedPersonAnonymousDataBuilderTest {

  @Test
  void shouldBuildPatientWithGivenIdAndGender() {
    NotifiedPersonAnonymousDataBuilder builder = new NotifiedPersonAnonymousDataBuilder();
    builder.setId("12345");
    builder.setGender(Enumerations.AdministrativeGender.MALE);

    Patient patient = builder.build();

    assertThat(patient.getId()).isEqualTo("Patient/12345");
    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.MALE);
  }

  @Test
  void shouldCopyPatientDetails() {
    Patient patientToCopy = new Patient();
    patientToCopy.setId("67890");
    patientToCopy.setGender(Enumerations.AdministrativeGender.FEMALE);
    patientToCopy.setBirthDateElement(new DateType("1980-01-01"));
    patientToCopy.addName().setFamily("Muster").addGiven("Max");
    patientToCopy.addTelecom().setValue("123456789");
    patientToCopy.addAddress().setCity("Berlin");

    final Patient patient = NotifiedPersonAnonymousDataBuilder.deepCopy(patientToCopy);

    assertThat(patient.getId()).isEqualTo("Patient/67890");
    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
    assertThat(patient.getBirthDateElement()).usingRecursiveComparison().isEqualTo(new DateType());
    assertThat(patient.getName()).hasSize(0);
    assertThat(patient.getTelecom()).hasSize(0);
    assertThat(patient.getAddress()).hasSize(0);
  }

  @Test
  void shouldHandleNullId() {
    NotifiedPersonAnonymousDataBuilder builder = new NotifiedPersonAnonymousDataBuilder();
    builder.setGender(Enumerations.AdministrativeGender.OTHER);

    Patient patient = builder.build();

    assertThat(patient.getId()).isNotNull();
    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.OTHER);
  }

  @Test
  void shouldHandleNullGender() {
    NotifiedPersonAnonymousDataBuilder builder = new NotifiedPersonAnonymousDataBuilder();
    builder.setId("54321");

    Patient patient = builder.build();

    assertThat(patient.getId()).isEqualTo("Patient/54321");
    assertThat(patient.getGender()).isNull();
  }
}
