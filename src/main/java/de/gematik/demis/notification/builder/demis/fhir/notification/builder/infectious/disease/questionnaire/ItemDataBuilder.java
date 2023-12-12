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
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Setter
public class ItemDataBuilder {

  private String linkId;

  public QuestionnaireResponse.QuestionnaireResponseItemComponent buildEntryItem(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    addLinkedIdIfSet(item);
    item.addAnswer(answer);

    return item;
  }

  private void addLinkedIdIfSet(QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    if (isNotBlank(linkId)) {
      item.setLinkId(linkId);
    }
  }

  public QuestionnaireResponse.QuestionnaireResponseItemComponent buildItemWithItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    return buildItemWithItem(Collections.singletonList(item));
  }

  public QuestionnaireResponse.QuestionnaireResponseItemComponent buildItemWithItem(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> itemList) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    addLinkedIdIfSet(item);
    item.setItem(itemList);

    return item;
  }

  public QuestionnaireResponse.QuestionnaireResponseItemComponent buildItemWithAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    return buildItemWithAnswer(Collections.singletonList(answer));
  }

  public QuestionnaireResponse.QuestionnaireResponseItemComponent buildItemWithAnswer(
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answer) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();

    addLinkedIdIfSet(item);
    item.setAnswer(answer);

    return item;
  }
}
