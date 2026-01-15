package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;

public final class ImmunizationDataBuilderTest {

  public static ImmunizationDataBuilder createCvddBuilder() {
    ImmunizationDataBuilder builder = new ImmunizationDataBuilder();
    builder.setId(generateUuidString());
    builder.setProfileUrl(PROFILE_IMMUNIZATION_INFORMATION_CVDD);
    builder.setStatus("completed");
    builder.setVaccineCode(
        new Coding(
            "https://ec.europa.eu/health/documents/community-register/html/",
            "EU/1/20/1528",
            "Comirnaty"));
    builder.setOccurrence(new DateTimeType("2021-03-15"));
    return builder;
  }

  @Test
  void setDefaults_shouldSetValues() {
    Immunization immunization = new ImmunizationDataBuilder().setDefaults().build();
    assertThat(immunization.getId()).isNotNull();
    assertThat(immunization.getStatus()).isSameAs(Immunization.ImmunizationStatus.COMPLETED);
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    Immunization.ImmunizationStatus status = Immunization.ImmunizationStatus.COMPLETED;
    Immunization immunization =
        new ImmunizationDataBuilder().setId(id).setStatusStandard(status).setDefaults().build();
    assertThat(immunization.getId()).isEqualTo(id);
    assertThat(immunization.getStatus()).isSameAs(status);
  }

  @Test
  void build_shouldCreateImmunizationWithAllSetProperties_OccuranceAsDate() {
    String id = "test-id";
    String profileUrl = PROFILE_IMMUNIZATION_INFORMATION_CVDD;
    String lotNumber = "AB12345";
    Patient patient = new Patient();
    patient.setId("patient-123");
    Coding vaccineCode =
        new Coding(
            "https://ec.europa.eu/health/documents/community-register/html/",
            "EU/1/20/1528",
            "Comirnaty");
    DateTimeType occurrence = new DateTimeType("2021-03-15");

    Immunization immunization =
        new ImmunizationDataBuilder()
            .setId(id)
            .setProfileUrl(profileUrl)
            .setStatusStandard(Immunization.ImmunizationStatus.COMPLETED)
            .setVaccineCode(vaccineCode)
            .setOccurrence(occurrence)
            .setLotNumber(lotNumber)
            .setNotifiedPerson(patient)
            .build();

    assertThat(immunization.getId()).isEqualTo(id);
    assertThat(immunization.getMeta().getProfile()).hasSize(1);
    assertThat(immunization.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
    assertThat(immunization.getStatus()).isEqualTo(Immunization.ImmunizationStatus.COMPLETED);
    assertThat(immunization.getVaccineCode().getCoding()).contains(vaccineCode);
    assertThat(immunization.getOccurrence()).isEqualTo(occurrence);
    assertThat(immunization.getLotNumber()).isEqualTo(lotNumber);
  }

  @Test
  void build_shouldCreateImmunizationWithAllSetProperties_OccurenceAsString() {
    String id = "test-id";
    String profileUrl = PROFILE_IMMUNIZATION_INFORMATION_CVDD;
    String lotNumber = "AB12345";
    Patient patient = new Patient();
    patient.setId("patient-123");
    Coding vaccineCode =
        new Coding(
            "https://ec.europa.eu/health/documents/community-register/html/",
            "EU/1/20/1528",
            "Comirnaty");
    StringType occurrence = new StringType("ASKU");

    Immunization immunization =
        new ImmunizationDataBuilder()
            .setId(id)
            .setProfileUrl(profileUrl)
            .setStatusStandard(Immunization.ImmunizationStatus.COMPLETED)
            .setVaccineCode(vaccineCode)
            .setOccurrence(occurrence)
            .setLotNumber(lotNumber)
            .setNotifiedPerson(patient)
            .build();

    assertThat(immunization.getId()).isEqualTo(id);
    assertThat(immunization.getMeta().getProfile()).hasSize(1);
    assertThat(immunization.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
    assertThat(immunization.getStatus()).isEqualTo(Immunization.ImmunizationStatus.COMPLETED);
    assertThat(immunization.getVaccineCode().getCoding()).contains(vaccineCode);
    assertThat(immunization.getOccurrence()).isEqualTo(occurrence);
    assertThat(immunization.getLotNumber()).isEqualTo(lotNumber);
  }

  @Test
  void addNote_shouldAddSingleNoteToImmunization() {
    String note = "Test note";
    Immunization immunization = new ImmunizationDataBuilder().addNote(note).setDefaults().build();

    assertThat(immunization.getNote()).hasSize(1);
    assertThat(immunization.getNote().getFirst().getText()).isEqualTo(note);
  }

  @Test
  void addNote_shouldAddMultipleNotesToImmunization() {
    String note1 = "First note";
    String note2 = "Second note";
    Immunization immunization =
        new ImmunizationDataBuilder().addNote(note1).addNote(note2).setDefaults().build();

    assertThat(immunization.getNote()).hasSize(2);
    assertThat(immunization.getNote().getFirst().getText()).isEqualTo(note1);
    assertThat(immunization.getNote().get(1).getText()).isEqualTo(note2);
  }

  @Test
  void setNotes_shouldReplaceAllNotes() {
    String initialNote = "Initial note";
    String note1 = "First note";
    String note2 = "Second note";
    Immunization immunization =
        new ImmunizationDataBuilder()
            .addNote(initialNote)
            .setNotes(List.of(note1, note2))
            .setDefaults()
            .build();

    assertThat(immunization.getNote()).hasSize(2);
    assertThat(immunization.getNote().getFirst().getText()).isEqualTo(note1);
    assertThat(immunization.getNote().get(1).getText()).isEqualTo(note2);
  }

  @Test
  void setProfileUrlByDisease_shouldSetDiseaseSpecificUrl() {
    String disease = "CVDD";
    Immunization immunization =
        new ImmunizationDataBuilder().setProfileUrlByDisease(disease).setDefaults().build();

    assertThat(immunization.getMeta().getProfile()).hasSize(1);
    assertThat(immunization.getMeta().getProfile().getFirst().getValue()).contains(disease);
  }

  @Test
  void deepyCopy_shouldCopyAllPropertiesFromOriginal() {
    String originalId = "original-id";
    Patient notifiedPerson = new Patient();
    notifiedPerson.setId("patient-id");
    Coding vaccineCode =
        new Coding(
            "https://ec.europa.eu/health/documents/community-register/html/",
            "EU/1/20/1528",
            "Comirnaty");

    Immunization originalImmunization =
        new ImmunizationDataBuilder()
            .setId(originalId)
            .setProfileUrl(PROFILE_IMMUNIZATION_INFORMATION_CVDD)
            .setStatusStandard(Immunization.ImmunizationStatus.COMPLETED)
            .setVaccineCode(vaccineCode)
            .setOccurrence(new DateTimeType("2021-03-15"))
            .addNote("Original note")
            .setDefaults()
            .build();

    Immunization copiedImmunization =
        ImmunizationDataBuilder.deepyCopy(originalImmunization, notifiedPerson);

    assertThat(copiedImmunization.getId()).contains(originalId);
    assertThat(copiedImmunization.getStatus()).isEqualTo(originalImmunization.getStatus());
    assertThat(copiedImmunization.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(originalImmunization.getMeta().getProfile().getFirst().getValue());
    assertThat(copiedImmunization.getVaccineCode().getCoding().getFirst().getCode())
        .isEqualTo(vaccineCode.getCode());
    assertThat(copiedImmunization.getOccurrence()).isEqualTo(originalImmunization.getOccurrence());
    assertThat(copiedImmunization.getNote()).hasSize(1);
    assertThat(copiedImmunization.getNote().getFirst().getText()).isEqualTo("Original note");
  }
}
