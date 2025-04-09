package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_CONDITION_VERIFICATION_STATUS;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_DISEASE_CVDD;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.List;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

public final class DiseaseDataBuilderTest {

  public static DiseaseDataBuilder createCvddBuilder() {
    DiseaseDataBuilder disease = new DiseaseDataBuilder();
    disease.setDefaults();
    disease.setProfileUrl(PROFILE_DISEASE_CVDD);
    disease.setVerificationStatus(
        new Coding().setSystem(CODE_SYSTEM_CONDITION_VERIFICATION_STATUS).setCode("confirmed"));
    disease.setDisease(
        new Coding(
            CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY,
            "cvdd",
            "Coronavirus-Krankheit-2019 (COVID-19)"));
    return disease;
  }

  @Test
  void shouldCreateExampleCvddDiseaseConditionObject() {
    // when
    Patient notifiedPerson = new Patient();
    DiseaseDataBuilder builder = createCvddBuilder();
    String note = "some note";
    Condition actual = builder.setNotifiedPerson(notifiedPerson).setNote(note).build();

    // then
    assertThat(actual).isNotNull();

    assertThat(actual.getSubject().getResource()).isSameAs(notifiedPerson);

    Meta meta = actual.getMeta();
    assertThat(meta).isNotNull();
    List<CanonicalType> list = meta.getProfile();
    assertThat(list).hasSize(1);
    assertThat(list.getFirst().getValue()).isEqualTo(PROFILE_DISEASE_CVDD);

    CodeableConcept verificationStatus = actual.getVerificationStatus();
    assertThat(verificationStatus.getCoding()).hasSize(1);
    Coding verificationStatusCoding = verificationStatus.getCodingFirstRep();
    assertThat(verificationStatusCoding.getSystem())
        .isEqualTo(CODE_SYSTEM_CONDITION_VERIFICATION_STATUS);
    assertThat(verificationStatusCoding.getCode()).isEqualTo("confirmed");

    CodeableConcept code = actual.getCode();
    assertThat(code).isNotNull();
    List<Coding> coding = code.getCoding();
    assertThat(coding).hasSize(1);
    assertThat(coding.getFirst().getCode()).isEqualTo("cvdd");
    assertThat(coding.getFirst().getDisplay()).isEqualTo("Coronavirus-Krankheit-2019 (COVID-19)");
    assertThat(coding.getFirst().getSystem()).isEqualTo(CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY);

    assertThat(actual.getNote()).hasSize(1);
    assertThat(actual.getNoteFirstRep().getText()).isEqualTo(note);
  }

  @Test
  void shouldCreateExampleCvddDiseaseWithRecordDate() {
    Patient notifiedPerson = new Patient();
    Condition exampleDisease =
        createCvddBuilder().setRecordedDate("2022-01-22").setNotifiedPerson(notifiedPerson).build();

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(0);
    cal.set(2022, Calendar.JANUARY, 22, 0, 0, 0);

    assertThat(exampleDisease.getRecordedDate()).isEqualTo(cal.getTime());
  }

  @Test
  void setDiseaseByCategory_shouldSetDiseaseCoding() {
    // when
    Patient notifiedPerson = new Patient();
    DiseaseDataBuilder condition = createCvddBuilder();
    String diseaseCategory = "msvd";
    condition.setDiseaseAndProfileUrl(diseaseCategory);
    Condition actual = condition.setNotifiedPerson(notifiedPerson).build();
    // then
    Coding coding = actual.getCode().getCodingFirstRep();
    assertThat(coding.getCode()).isEqualTo(diseaseCategory);
    assertThat(coding.getSystem()).isEqualTo(CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY);
    assertThat(coding.getDisplay()).isNull();
  }

  @Test
  void setDiseaseAndProfileUrlByDisease_shouldSetBothValues() {
    // when
    String diseaseCategory = "msvd";
    Condition actual = new DiseaseDataBuilder().setDiseaseAndProfileUrl(diseaseCategory).build();
    // then
    Coding coding = actual.getCode().getCodingFirstRep();
    assertThat(coding.getCode()).isEqualTo(diseaseCategory);
    assertThat(coding.getSystem()).isEqualTo(CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY);
    assertThat(coding.getDisplay()).isNull();

    Meta meta = actual.getMeta();
    assertThat(meta).isNotNull();
    List<CanonicalType> profiles = meta.getProfile();
    assertThat(profiles).hasSize(1);
    assertThat(profiles.getFirst().getValue())
        .as("disease specific profile URL")
        .isEqualTo(
            "https://demis.rki.de/fhir/StructureDefinition/Disease"
                + diseaseCategory.toUpperCase());
  }

  @Test
  void setStatus() {
    Condition disease =
        new DiseaseDataBuilder()
            .setVerificationStatus(new Coding().setCode("confirmed"))
            .setClinicalStatus(new Coding().setCode("active"))
            .build();
    assertThat(disease.getVerificationStatus().getCodingFirstRep().getCode())
        .isEqualTo("confirmed");
    assertThat(disease.getClinicalStatus().getCodingFirstRep().getCode()).isEqualTo("active");
  }

  @Test
  void setDefaults_shouldSetValues() {
    Condition disease = new DiseaseDataBuilder().setDefaults().build();
    assertThat(disease).isNotNull();
    assertThat(disease.getId()).isNotEmpty();
    CodeableConcept verificationStatus = disease.getVerificationStatus();
    assertThat(verificationStatus).isNotNull();
    assertThat(verificationStatus.getCoding()).hasSize(1);
    Coding verificationStatusCoding = verificationStatus.getCodingFirstRep();
    assertThat(verificationStatusCoding).as("verification status").isNotNull();
    assertThat(verificationStatusCoding.getSystem())
        .as("verification status system")
        .isEqualTo(CODE_SYSTEM_CONDITION_VERIFICATION_STATUS);
    assertThat(verificationStatusCoding.getCode())
        .as("verification status code")
        .isEqualTo("confirmed");
    assertThat(verificationStatusCoding.getDisplay()).as("verification status display").isNull();
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    String verificationStatusCode = "init-code";
    Condition disease =
        new DiseaseDataBuilder()
            .setId(id)
            .setVerificationStatus(new Coding().setCode(verificationStatusCode))
            .setDefaults()
            .build();
    assertThat(disease.getId()).isEqualTo(id);
    assertThat(disease.getVerificationStatus().getCodingFirstRep().getCode())
        .isEqualTo(verificationStatusCode);
  }

  @Test
  void addNote_shouldSupportMultipleNotes() {
    Condition disease =
        new DiseaseDataBuilder().setDefaults().addNote("1").addNote("2").addNote("3").build();
    assertThat(disease.getNote()).hasSize(3);
    assertThat(disease.getNoteFirstRep().getText()).isEqualTo("1");
  }
}
