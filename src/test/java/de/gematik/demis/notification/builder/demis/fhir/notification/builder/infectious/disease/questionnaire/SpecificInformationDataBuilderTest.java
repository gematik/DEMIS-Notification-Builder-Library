package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

public final class SpecificInformationDataBuilderTest {

  private static final FhirContext FHIR_CONTEXT = FhirContext.forR4Cached();

  public static SpecificInformationDataBuilder createCvddBuilder() {
    return createCvddBuilder(null);
  }

  public static SpecificInformationDataBuilder createCvddBuilder(Immunization immunization) {
    SpecificInformationDataBuilder questionnaire = new SpecificInformationDataBuilder();
    setMetadata(questionnaire);
    setInfectionSource(questionnaire);
    setImmunization(immunization, questionnaire);
    setInfectionRisks(questionnaire);
    return questionnaire;
  }

  private static void setMetadata(SpecificInformationDataBuilder questionnaire) {
    questionnaire.setDefaults();
    questionnaire.setProfileUrlByDisease("cvdd");
    questionnaire.setQuestionnaireUrlByDisease("cvdd");
  }

  private static void setImmunization(
      Immunization immunization, SpecificInformationDataBuilder questionnaire) {
    if (immunization == null) {
      questionnaire.addItem(
          new ItemDataBuilder()
              .setLinkId("immunization")
              .addAnswer(
                  new AnswerDataBuilder()
                      .setValueCoding(
                          new Coding(
                              "https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer", "no", "Nein"))
                      .build())
              .build());
    } else {
      questionnaire.addImmunization(immunization);
    }
  }

  private static void setInfectionSource(SpecificInformationDataBuilder questionnaire) {
    questionnaire.addItem(
        new ItemDataBuilder()
            .setLinkId("infectionSource")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding(
                            "https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer", "no", "Nein"))
                    .build())
            .build());
  }

  private static void setInfectionRisks(SpecificInformationDataBuilder questionnaire) {
    questionnaire.addItem(
        new ItemDataBuilder()
            .setLinkId("infectionRiskKind")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding(
                                "http://snomed.info/sct",
                                "49601007",
                                "Herz-Kreislauf-Erkrankung (inkl. Bluthochdruck)")
                            .setVersion(
                                "http://snomed.info/sct/900000000000207008/version/20230331"))
                    .build())
            .build());
    questionnaire.addItem(
        new ItemDataBuilder()
            .setLinkId("infectionRiskKind")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding("http://snomed.info/sct", "235856003", "Lebererkrankung")
                            .setVersion(
                                "http://snomed.info/sct/900000000000207008/version/20230331"))
                    .build())
            .build());
  }

  private SpecificInformationDataBuilder builder = new SpecificInformationDataBuilder();

  @Test
  void setDefaults_shouldSetValues() {
    this.builder.setDefaults();
    this.builder.setNotifiedPerson(new Patient());
    QuestionnaireResponse response = this.builder.build();
    assertThat(response.getQuestionnaire()).as("empty questionnaire URL").isNull();
    assertThat(response.getMeta().hasProfile(PROFILE_DISEASE_INFORMATION_COMMON))
        .as("empty profile URL")
        .isFalse();
    assertThat(response.getStatus())
        .as("status")
        .isSameAs(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
  }

  @Test
  void setDefaults_shouldKeepValues() {
    QuestionnaireResponse.QuestionnaireResponseStatus status =
        QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS;
    this.builder.setStandardStatus(status);
    QuestionnaireResponse response = this.builder.setDefaults().build();
    assertThat(response.getStatus()).as("questionnaire response status").isSameAs(status);
  }

  @Test
  void shouldAddSingleImmunizationSnomed() {
    Immunization immunization = new Immunization();
    immunization.setId("imm-123");

    this.builder.addImmunizationSnomed(immunization);
    this.builder.setNotifiedPerson(new Patient());
    QuestionnaireResponse response = this.builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String json = parser.encodeToJson(response);

    // Should use SNOMED code system for "yes" answer
    assertThat(json).contains("\"system\":\"http://snomed.info/sct\"");
    assertThat(json).contains("\"code\":\"373066001\"");
    assertThat(json).contains("Immunization/imm-123");
  }

  @Test
  void shouldAddMultipleImmunizationsSnomed() {
    Immunization imm1 = new Immunization();
    imm1.setId("imm-1");
    Immunization imm2 = new Immunization();
    imm2.setId("imm-2");
    Immunization imm3 = new Immunization();
    imm3.setId("imm-3");

    this.builder.addImmunizationsSnomed(asList(imm1, imm2, imm3));
    this.builder.setNotifiedPerson(new Patient());
    QuestionnaireResponse response = this.builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String json = parser.encodeToJson(response);

    assertThat(json).contains("\"system\":\"http://snomed.info/sct\"");
    assertThat(json).contains("\"code\":\"373066001\"");
    assertThat(json).contains("Immunization/imm-1");
    assertThat(json).contains("Immunization/imm-2");
    assertThat(json).contains("Immunization/imm-3");
  }

  @Test
  void shouldNotMixRegularAndSnomedImmunizations() {
    Immunization imm1 = new Immunization();
    imm1.setId("regular-imm");
    Immunization imm2 = new Immunization();
    imm2.setId("snomed-imm");

    this.builder.addImmunization(imm1);
    this.builder.addImmunizationSnomed(imm2);
    this.builder.setNotifiedPerson(new Patient());
    QuestionnaireResponse response = this.builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String json = parser.encodeToJson(response);

    // Both immunization references should be present
    assertThat(json).contains("Immunization/regular-imm");
    assertThat(json).contains("Immunization/snomed-imm");
  }
}
