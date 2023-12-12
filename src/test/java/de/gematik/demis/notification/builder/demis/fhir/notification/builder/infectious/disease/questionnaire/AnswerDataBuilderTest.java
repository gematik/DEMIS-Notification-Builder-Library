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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

class AnswerDataBuilderTest {

  @Test
  void shouldCreateAnswerWithAnswerItem() {

    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder().buildAnswerWithItem(item);

    assertThat(answer).isNotNull();
    assertThat(answer.getItem()).hasSize(1);
    assertThat(answer.getItemFirstRep()).isEqualTo(item);
  }

  @Test
  void shouldCreateAnswerWithCoding() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem("System")
            .setAnswerCodingCode("code")
            .setAnswerCodingDisplay("display")
            .buildAnswer();

    assertThat(answer).isNotNull();
    assertThat(answer.getValue()).isInstanceOf(Coding.class);
    assertThat(answer.getValueCoding().getDisplay()).isEqualTo("display");
    assertThat(answer.getValueCoding().getCode()).isEqualTo("code");
    assertThat(answer.getValueCoding().getSystem()).isEqualTo("System");
  }

  @Test
  void shouldCreateAnswerWithCodingOnlyCode() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder().setAnswerCodingCode("code").buildAnswer();

    assertThat(answer).isNotNull();
    assertThat(answer.getValue()).isInstanceOf(Coding.class);
    assertThat(answer.getValueCoding().getDisplay()).isNull();
    assertThat(answer.getValueCoding().getCode()).isEqualTo("code");
    assertThat(answer.getValueCoding().getSystem()).isNull();
  }

  @Test
  void shouldCreateAnswerWithCodingOnlySystem() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder().setAnswerCodingSystem("System").buildAnswer();

    assertThat(answer).isNotNull();
    assertThat(answer.getValue()).isInstanceOf(Coding.class);
    assertThat(answer.getValueCoding().getDisplay()).isNull();
    assertThat(answer.getValueCoding().getCode()).isNull();
    assertThat(answer.getValueCoding().getSystem()).isEqualTo("System");
  }

  @Test
  void shouldCreateAnswerWithCodingOnlyDisplay() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder().setAnswerCodingDisplay("display").buildAnswer();

    assertThat(answer).isNotNull();
    assertThat(answer.getValue()).isInstanceOf(Coding.class);
    assertThat(answer.getValueCoding().getDisplay()).isEqualTo("display");
    assertThat(answer.getValueCoding().getCode()).isNull();
    assertThat(answer.getValueCoding().getSystem()).isNull();
  }
}
