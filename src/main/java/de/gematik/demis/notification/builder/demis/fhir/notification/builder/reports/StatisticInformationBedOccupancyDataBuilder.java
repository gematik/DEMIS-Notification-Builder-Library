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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import lombok.Setter;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Setter
public class StatisticInformationBedOccupancyDataBuilder {

  private String id;
  private String questionnaire;
  private String status;
  private Integer numberOperableBedsGeneralWardAdultsValue;
  private Integer numberOccupiedBedsGeneralWardAdultsValue;
  private Integer numberOperableBedsGeneralWardChildrenValue;
  private Integer numberOccupiedBedsGeneralWardChildrenValue;

  private String profilUrl;

  public QuestionnaireResponse buildExampleStatisticInformationBedOccupancy() {

    id = requireNonNullElse(id, generateUuidString());
    questionnaire =
        requireNonNullElse(
            questionnaire,
            "https://demis.rki.de/fhir/Questionnaire/StatisticQuestionsBedOccupancy");
    status = requireNonNullElse(status, "completed");
    numberOperableBedsGeneralWardAdultsValue =
        requireNonNullElse(numberOperableBedsGeneralWardAdultsValue, 250);
    numberOccupiedBedsGeneralWardAdultsValue =
        requireNonNullElse(numberOccupiedBedsGeneralWardAdultsValue, 221);
    numberOperableBedsGeneralWardChildrenValue =
        requireNonNullElse(numberOperableBedsGeneralWardChildrenValue, 50);
    numberOccupiedBedsGeneralWardChildrenValue =
        requireNonNullElse(numberOccupiedBedsGeneralWardChildrenValue, 37);

    profilUrl =
        requireNonNullElse(profilUrl, DemisConstants.PROFILE_STATISTIC_INFORMATION_BED_OCCUPANCY);

    return buildStatisticInformationBedOccupancy();
  }

  public QuestionnaireResponse buildStatisticInformationBedOccupancyForGateway() {
    id = requireNonNullElse(id, generateUuidString());
    questionnaire =
        requireNonNullElse(
            questionnaire,
            "https://demis.rki.de/fhir/Questionnaire/StatisticQuestionsBedOccupancy");
    status = requireNonNullElse(status, "completed");
    profilUrl =
        requireNonNullElse(profilUrl, DemisConstants.PROFILE_STATISTIC_INFORMATION_BED_OCCUPANCY);
    return buildStatisticInformationBedOccupancy();
  }

  public QuestionnaireResponse buildStatisticInformationBedOccupancy() {
    QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();

    questionnaireResponse.setId(id);
    questionnaireResponse.setMeta(new Meta().addProfile(profilUrl));
    questionnaireResponse.setQuestionnaire(questionnaire);
    questionnaireResponse.setStatus(
        QuestionnaireResponse.QuestionnaireResponseStatus.fromCode(status));

    if (numberOperableBedsGeneralWardAdultsValue != null) {
      questionnaireResponse.addItem(
          createItem(
              "numberOperableBedsGeneralWardAdults", numberOperableBedsGeneralWardAdultsValue));
    }
    questionnaireResponse.addItem(
        createItem(
            "numberOccupiedBedsGeneralWardAdults", numberOccupiedBedsGeneralWardAdultsValue));
    if (numberOperableBedsGeneralWardChildrenValue != null) {
      questionnaireResponse.addItem(
          createItem(
              "numberOperableBedsGeneralWardChildren", numberOperableBedsGeneralWardChildrenValue));
    }
    questionnaireResponse.addItem(
        createItem(
            "numberOccupiedBedsGeneralWardChildren", numberOccupiedBedsGeneralWardChildrenValue));

    return questionnaireResponse;
  }

  private QuestionnaireResponse.QuestionnaireResponseItemComponent createItem(
      String id, Integer responseValue) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();
    item.setLinkId(id);
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(new IntegerType(responseValue));
    item.addAnswer(answer);
    return item;
  }
}
