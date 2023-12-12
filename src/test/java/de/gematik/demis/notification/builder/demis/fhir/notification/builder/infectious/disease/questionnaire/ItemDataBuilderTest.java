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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

class ItemDataBuilderTest {

  @Test
  void shouldCreateItemWithGivenAnswerWithLinkId() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new ItemDataBuilder().setLinkId("linkId").buildEntryItem(answer);

    assertThat(item).isNotNull();
    assertThat(item.getLinkId()).isEqualTo("linkId");
    assertThat(item.getAnswerFirstRep()).isEqualTo(answer);
  }

  @Test
  void shouldCreateItemWithItem() {
    QuestionnaireResponse.QuestionnaireResponseItemComponent anotherItem =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().buildItemWithItem(anotherItem);

    assertThat(questionnaireResponseItemComponent.getItem()).hasSize(1);
    assertThat(questionnaireResponseItemComponent.getItem().get(0)).isEqualTo(anotherItem);
  }

  @Test
  void shouldCreateItemWithItemList() {
    QuestionnaireResponse.QuestionnaireResponseItemComponent anotherItem1 =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();
    QuestionnaireResponse.QuestionnaireResponseItemComponent anotherItem2 =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().buildItemWithItem(asList(anotherItem1, anotherItem2));

    assertThat(questionnaireResponseItemComponent.getItem())
        .containsExactly(anotherItem1, anotherItem2);
  }

  @Test
  void shouldCreateItemWithAnswer() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent anotherItem =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().buildItemWithAnswer(anotherItem);

    assertThat(questionnaireResponseItemComponent.getAnswer()).hasSize(1);
    assertThat(questionnaireResponseItemComponent.getAnswer().get(0)).isEqualTo(anotherItem);
  }

  @Test
  void shouldCreateItemWithAnswerList() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent anotherItem1 =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent anotherItem2 =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().buildItemWithAnswer(asList(anotherItem1, anotherItem2));

    assertThat(questionnaireResponseItemComponent.getAnswer())
        .containsExactly(anotherItem1, anotherItem2);
  }
}
