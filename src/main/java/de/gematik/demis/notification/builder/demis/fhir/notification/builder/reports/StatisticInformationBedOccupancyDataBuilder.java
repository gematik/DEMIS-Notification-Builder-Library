package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import lombok.Setter;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Setter
public class StatisticInformationBedOccupancyDataBuilder implements InitializableFhirObjectBuilder {

  static final String QUESTIONNAIRE_URL =
      "https://demis.rki.de/fhir/Questionnaire/StatisticQuestionsBedOccupancy";

  private String id;
  private String questionnaire;
  private String status;
  private String profileUrl;
  private Integer numberOperableBedsGeneralWardAdultsValue;
  private Integer numberOccupiedBedsGeneralWardAdultsValue;
  private Integer numberOperableBedsGeneralWardChildrenValue;
  private Integer numberOccupiedBedsGeneralWardChildrenValue;

  /**
   * Set default values for bed occupancy FHIR object, questionnaire response:
   *
   * <ul>
   *   <li>id
   *   <li>questionnaire
   *   <li>status
   *   <li>profileUrl
   * </ul>
   *
   * @return builder
   */
  @Override
  public StatisticInformationBedOccupancyDataBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    if (this.profileUrl == null) {
      setProfileUrl(DemisConstants.PROFILE_STATISTIC_INFORMATION_BED_OCCUPANCY);
    }
    if (this.questionnaire == null) {
      setQuestionnaire(QUESTIONNAIRE_URL);
    }
    if (this.status == null) {
      setStatus("completed");
    }
    return this;
  }

  public QuestionnaireResponse buildExampleStatisticInformationBedOccupancy() {
    setDefaults();
    numberOperableBedsGeneralWardAdultsValue =
        requireNonNullElse(numberOperableBedsGeneralWardAdultsValue, 250);
    numberOccupiedBedsGeneralWardAdultsValue =
        requireNonNullElse(numberOccupiedBedsGeneralWardAdultsValue, 221);
    numberOperableBedsGeneralWardChildrenValue =
        requireNonNullElse(numberOperableBedsGeneralWardChildrenValue, 50);
    numberOccupiedBedsGeneralWardChildrenValue =
        requireNonNullElse(numberOccupiedBedsGeneralWardChildrenValue, 37);
    return build();
  }

  @Override
  public QuestionnaireResponse build() {
    QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
    questionnaireResponse.setId(id);
    questionnaireResponse.setMeta(new Meta().addProfile(profileUrl));
    questionnaireResponse.setQuestionnaire(questionnaire);
    questionnaireResponse.setStatus(
        QuestionnaireResponse.QuestionnaireResponseStatus.fromCode(status));
    addStatistics(questionnaireResponse);
    return questionnaireResponse;
  }

  public StatisticInformationBedOccupancyDataBuilder setStatusStandard(
      QuestionnaireResponse.QuestionnaireResponseStatus status) {
    this.status = status.toCode();
    return this;
  }

  private void addStatistics(QuestionnaireResponse questionnaireResponse) {
    if (numberOperableBedsGeneralWardAdultsValue != null) {
      questionnaireResponse.addItem(
          createItem(
              "numberOperableBedsGeneralWardAdults", numberOperableBedsGeneralWardAdultsValue));
    }
    if (numberOccupiedBedsGeneralWardAdultsValue == null) {
      throw new IllegalStateException("number of occupied beds in general ward for adults is null");
    }
    questionnaireResponse.addItem(
        createItem(
            "numberOccupiedBedsGeneralWardAdults", numberOccupiedBedsGeneralWardAdultsValue));
    if (numberOperableBedsGeneralWardChildrenValue != null) {
      questionnaireResponse.addItem(
          createItem(
              "numberOperableBedsGeneralWardChildren", numberOperableBedsGeneralWardChildrenValue));
    }
    if (numberOccupiedBedsGeneralWardChildrenValue == null) {
      throw new IllegalStateException(
          "number of occupied beds in general ward for children is null");
    }
    questionnaireResponse.addItem(
        createItem(
            "numberOccupiedBedsGeneralWardChildren", numberOccupiedBedsGeneralWardChildrenValue));
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
