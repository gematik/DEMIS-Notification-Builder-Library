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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

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
        new ItemDataBuilder().setLinkId("linkId").addAnswer(answer).build();

    assertThat(item).isNotNull();
    assertThat(item.getLinkId()).isEqualTo("linkId");
    assertThat(item.getAnswerFirstRep()).isEqualTo(answer);
  }

  @Test
  void shouldCreateItemWithItem() {
    QuestionnaireResponse.QuestionnaireResponseItemComponent anotherItem =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().addItem(anotherItem).build();

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
        new ItemDataBuilder().setItems(asList(anotherItem1, anotherItem2)).build();

    assertThat(questionnaireResponseItemComponent.getItem())
        .containsExactly(anotherItem1, anotherItem2);
  }

  @Test
  void shouldCreateItemWithAnswer() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().addAnswer(answer).build();

    assertThat(questionnaireResponseItemComponent.getAnswer()).hasSize(1);
    assertThat(questionnaireResponseItemComponent.getAnswer().get(0)).isEqualTo(answer);
  }

  @Test
  void shouldCreateItemWithAnswerList() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer1 =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer2 =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireResponseItemComponent =
        new ItemDataBuilder().setAnswers(asList(answer1, answer2)).build();

    assertThat(questionnaireResponseItemComponent.getAnswer()).containsExactly(answer1, answer2);
  }
}
