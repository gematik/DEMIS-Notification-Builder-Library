package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON;
import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

public final class SpecificInformationDataBuilderTest {

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
}
