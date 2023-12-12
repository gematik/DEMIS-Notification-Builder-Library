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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_YES_OR_NO_ANSWER;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;

public class ExampleQuestionnaireCVVDData {

  private ExampleQuestionnaireCVVDData() {
    throw new IllegalStateException("Utility class");
  }

  public static List<QuestionnaireResponse.QuestionnaireResponseItemComponent> getCVDDQuestionnaire(
      List<Resource> immunizationList) {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> responseItemComponents =
        new ArrayList<>();
    responseItemComponents.add(exampleForDeathQuestionItem());
    responseItemComponents.add(exampleForInfectionEnvironment());
    responseItemComponents.add(exampleForImmunization(immunizationList));

    return responseItemComponents;
  }

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForDeathQuestionItem() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("JA")
            .buildAnswer();
    return new ItemDataBuilder().setLinkId("infectionSource").buildEntryItem(entryItemAnswer);
  }

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForInfectionEnvironment() {
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
    return new ItemDataBuilder()
        .setLinkId("infectionEnvironmentSetting")
        .buildEntryItem(entryItemAnswer);
  }

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent exampleForImmunization(
      List<Resource> immunizationList) {

    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> answerItem = new ArrayList<>();

    for (Resource immunization : immunizationList) {
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
          new AnswerDataBuilder().buildAnswerWithReferenceValue(immunization);
      answerItem.add(
          new ItemDataBuilder().setLinkId("immunizationRef").buildItemWithAnswer(answer));
    }

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent immunizationAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .buildAnswerWithItem(answerItem);
    return new ItemDataBuilder().setLinkId("immunization").buildEntryItem(immunizationAnswer);
  }
}
