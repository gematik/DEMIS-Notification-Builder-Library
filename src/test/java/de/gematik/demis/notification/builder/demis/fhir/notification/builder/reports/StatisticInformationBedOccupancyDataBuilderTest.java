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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

class StatisticInformationBedOccupancyDataBuilderTest {

  @Test
  void build_shouldThrowExceptionOnMissingAdultsValue() {
    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                new StatisticInformationBedOccupancyDataBuilder()
                    .setDefaults()
                    .setNumberOccupiedBedsGeneralWardChildrenValue(42)
                    .build())
        .withMessageContaining("number of occupied beds in general ward for adults is null");
  }

  @Test
  void build_shouldThrowExceptionOnMissingChildrenValue() {
    assertThatIllegalStateException()
        .isThrownBy(
            () ->
                new StatisticInformationBedOccupancyDataBuilder()
                    .setDefaults()
                    .setNumberOccupiedBedsGeneralWardAdultsValue(42)
                    .build())
        .withMessageContaining("number of occupied beds in general ward for children is null");
  }

  @Test
  void setDefaults_shouldSetValues() {
    QuestionnaireResponse response =
        new StatisticInformationBedOccupancyDataBuilder()
            .setDefaults()
            .setNumberOccupiedBedsGeneralWardAdultsValue(42)
            .setNumberOccupiedBedsGeneralWardChildrenValue(23)
            .build();
    assertThat(response.getId()).isNotNull();
    assertThat(response.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_STATISTIC_INFORMATION_BED_OCCUPANCY);
    assertThat(response.getQuestionnaire())
        .isEqualTo(StatisticInformationBedOccupancyDataBuilder.QUESTIONNAIRE_URL);
    assertThat(response.getStatus())
        .isEqualTo(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    String profileUrl = "init-profile-url";
    String questionnaire = "init-questionnaire";
    QuestionnaireResponse.QuestionnaireResponseStatus status =
        QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS;
    QuestionnaireResponse response =
        new StatisticInformationBedOccupancyDataBuilder()
            .setId(id)
            .setQuestionnaire(questionnaire)
            .setStatusStandard(status)
            .setProfileUrl(profileUrl)
            .setDefaults()
            .setNumberOccupiedBedsGeneralWardAdultsValue(42)
            .setNumberOccupiedBedsGeneralWardChildrenValue(23)
            .build();
    assertThat(response.getId()).isEqualTo(id);
    assertThat(response.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
    assertThat(response.getQuestionnaire()).isEqualTo(questionnaire);
    assertThat(response.getStatus()).isSameAs(status);
  }
}
