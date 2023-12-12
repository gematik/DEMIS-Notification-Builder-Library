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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_YES_OR_NO_ANSWER;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.AnswerDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.DiseaseInformationCVDDDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.ItemDataBuilder;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiseaseInformationCVDDDataBuilderTest {

  private static FhirContext fhirContext;
  private Patient emptyPatient;
  private List<QuestionnaireResponse.QuestionnaireResponseItemComponent> list;
  private DiseaseInformationCVDDDataBuilder builder;

  @BeforeAll
  static void setUpContext() {
    fhirContext = FhirContext.forR4();
  }

  @BeforeEach
  void setUp() {
    emptyPatient = new Patient();

    builder = new DiseaseInformationCVDDDataBuilder();
    builder.setId("123456");
    builder.setStatus("completed");
    builder.setProfileUrl("profileUrl");
    builder.setQuestionnaireUrl("questionnaireUrl");

    list = new ArrayList<>();
  }

  @Test
  void shouldCreateQuestionnaireResponseWithDeathQuestionItem() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("JA")
            .buildAnswer();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("infectionSource").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildExampleCVDDInformationSpecificData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"infectionSource\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"JA\"}}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithInfectionEnvironment() {
    QuestionnaireResponse.QuestionnaireResponseItemComponent infectionEnvironmentSettingKindItem =
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSettingKind")
            .buildItemWithAnswer(
                new AnswerDataBuilder()
                    .setAnswerCodingSystem(
                        "https://demis.rki.de/fhir/CodeSystem/infectionEnvironmentSetting")
                    .setAnswerCodingCode("3")
                    .setAnswerCodingDisplay("Gesundheitseinrichtung")
                    .buildAnswer());
    QuestionnaireResponse.QuestionnaireResponseItemComponent infectionEnvironmentSettingBeginItem =
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSettingBegin")
            .buildItemWithAnswer(new AnswerDataBuilder().buildAnswerWithDateValue("2021-12-28"));
    QuestionnaireResponse.QuestionnaireResponseItemComponent infectionEnvironmentSettingEndItem =
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSettingEnd")
            .buildItemWithAnswer(new AnswerDataBuilder().buildAnswerWithDateValue("2021-12-30"));
    QuestionnaireResponse.QuestionnaireResponseItemComponent infectionEnvironmentSettingGroupItem =
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSettingGroup")
            .buildItemWithItem(
                asList(
                    infectionEnvironmentSettingKindItem,
                    infectionEnvironmentSettingBeginItem,
                    infectionEnvironmentSettingEndItem));
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(infectionEnvironmentSettingGroupItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSetting")
            .buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildExampleCVDDDiseaseInformationSpecificData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"infectionEnvironmentSetting\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"infectionEnvironmentSettingGroup\",\"item\":[{\"linkId\":\"infectionEnvironmentSettingKind\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/infectionEnvironmentSetting\",\"code\":\"3\",\"display\":\"Gesundheitseinrichtung\"}}]},{\"linkId\":\"infectionEnvironmentSettingBegin\",\"answer\":[{\"valueDate\":\"2021-12-28\"}]},{\"linkId\":\"infectionEnvironmentSettingEnd\",\"answer\":[{\"valueDate\":\"2021-12-30\"}]}]}]}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithImmunization() {

    Immunization immunization1 = new Immunization();
    immunization1.setId("1");
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer1 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(immunization1);
    QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationRef1 =
        new ItemDataBuilder().setLinkId("immunizationRef").buildItemWithAnswer(answer1);

    Immunization immunization2 = new Immunization();
    immunization2.setId("2");
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer2 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(immunization2);
    QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationRef2 =
        new ItemDataBuilder().setLinkId("immunizationRef").buildItemWithAnswer(answer2);

    Immunization immunization3 = new Immunization();
    immunization3.setId("3");
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer3 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(immunization3);
    QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationRef3 =
        new ItemDataBuilder().setLinkId("immunizationRef").buildItemWithAnswer(answer3);

    Immunization immunization4 = new Immunization();
    immunization4.setId("4");
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer4 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(immunization4);
    QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationRef4 =
        new ItemDataBuilder().setLinkId("immunizationRef").buildItemWithAnswer(answer4);

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent immunizationAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .buildAnswerWithItem(
                asList(immunizationRef1, immunizationRef2, immunizationRef3, immunizationRef4));
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("immunization").buildEntryItem(immunizationAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildExampleCVDDDiseaseInformationSpecificData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"immunization\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"immunizationRef\",\"answer\":[{\"valueReference\":{\"reference\":\"Immunization/1\"}}]},{\"linkId\":\"immunizationRef\",\"answer\":[{\"valueReference\":{\"reference\":\"Immunization/2\"}}]},{\"linkId\":\"immunizationRef\",\"answer\":[{\"valueReference\":{\"reference\":\"Immunization/3\"}}]},{\"linkId\":\"immunizationRef\",\"answer\":[{\"valueReference\":{\"reference\":\"Immunization/4\"}}]}]}]}]}";

    assertThat(s).isEqualTo(expected);
  }
}
