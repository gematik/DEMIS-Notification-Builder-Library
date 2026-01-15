package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_ANONYMOUS;
import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
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

  private NotifiedPersonAnonymousDataBuilder builder;

  @Test
  void setDefault_setsIdIfNull() {
    builder = new NotifiedPersonAnonymousDataBuilder();
    builder.setId(null);
    builder.setDefault();
    Patient actualPatient = builder.build();
    assertThat(actualPatient.getId()).as("ID sollte nach setDefault nicht null sein").isNotNull();
  }

  @Test
  void setDefault_doesNotOverwriteExistingId() {
    builder = new NotifiedPersonAnonymousDataBuilder();
    String existingId = "test-id";
    builder.setId(existingId);
    builder.setDefault();
    Patient actualPatient = builder.build();
    assertThat(actualPatient.getId())
        .as("ID sollte nicht überschrieben werden")
        .isEqualTo("Patient/" + existingId);
  }

  @Test
  void setDefault_setsProfileUrl() {
    builder = new NotifiedPersonAnonymousDataBuilder();
    builder.setDefault();
    Patient actualPatient = builder.build();
    assertThat(actualPatient.getMeta().getProfile().getFirst().getValueAsString())
        .as("ProfileUrl sollte korrekt gesetzt werden")
        .isEqualTo(PROFILE_NOTIFIED_PERSON_ANONYMOUS);
  }

  @Test
  void createAnonymousPatientForExcerpt() {
    final Patient patient = new Patient();
    patient.setId("67890");
    patient.setGender(Enumerations.AdministrativeGender.MALE);
    patient.setBirthDateElement(new DateType("1980-01-01"));
    patient.addName().setFamily("Muster").addGiven("Max");
    patient.addAddress().setCity("Berlin").setPostalCode("12345");
    final String pseudonymExtensionUrl =
        "https://demis.rki.de/fhir/StructureDefinition/PseudonymRecordType";
    patient.addExtension().setUrl(pseudonymExtensionUrl).setValue(new StringType("SomeValue"));
    final Patient anonymousPatient =
        NotifiedPersonAnonymousDataBuilder.createAnonymousPatientForExcerpt(patient);
    assertThat(anonymousPatient.getMeta().getProfile().getFirst().getValueAsString())
        .isEqualTo(PROFILE_NOTIFIED_PERSON_ANONYMOUS);
    assertThat(anonymousPatient.getAddress().getFirst().getPostalCode()).isEqualTo("123");
    assertThat(anonymousPatient.getAddress().getFirst().getCity()).isNull();
    assertThat(anonymousPatient.getGender()).isEqualTo(Enumerations.AdministrativeGender.MALE);
    assertThat(anonymousPatient.getName()).isEmpty();
    assertThat(anonymousPatient.getBirthDateElement().getValueAsString()).isEqualTo("1980-01");
    assertThat(anonymousPatient.getExtensionByUrl(pseudonymExtensionUrl)).isNull();
  }
}
