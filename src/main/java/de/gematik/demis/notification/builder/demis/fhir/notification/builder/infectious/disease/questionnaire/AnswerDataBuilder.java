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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class AnswerDataBuilder {

  private String answerCodingSystem;
  private String answerCodingCode;
  private String answerCodingDisplay;

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent buildAnswer() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    addValueCodingIfSet(answer);

    return answer;
  }

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent buildAnswerWithItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem) {
    return buildAnswerWithItem(Collections.singletonList(answerItem));
  }

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent buildAnswerWithItem(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> answerItem) {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    answer.setItem(answerItem);
    addValueCodingIfSet(answer);

    return answer;
  }

  private void addValueCodingIfSet(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (isNotBlank(answerCodingSystem)
        || isNotBlank(answerCodingCode)
        || isNotBlank(answerCodingDisplay)) {
      answer.setValue(new Coding(answerCodingSystem, answerCodingCode, answerCodingDisplay));
    }
  }

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent buildAnswerWithDateValue(
      String date) {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    Type value = new DateType(date);
    answer.setValue(value);

    return answer;
  }

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      buildAnswerWithReferenceValue(Resource resource) {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    Type value = new Reference(resource);
    answer.setValue(value);

    return answer;
  }

  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent buildAnswerWithStringValue(
      String string) {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

    Type value = new StringType(string);
    answer.setValue(value);

    return answer;
  }
}
