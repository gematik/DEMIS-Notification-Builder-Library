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
 * #L%
 */

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.FhirObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Setter
public class ItemDataBuilder implements FhirObjectBuilder {

  private final List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answers =
      new ArrayList<>();
  private final List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
      new ArrayList<>();
  private String linkId;

  @Override
  public QuestionnaireResponse.QuestionnaireResponseItemComponent build() {
    QuestionnaireResponse.QuestionnaireResponseItemComponent item =
        new QuestionnaireResponse.QuestionnaireResponseItemComponent();
    addLinkedIdIfSet(item);
    item.setAnswer(this.answers);
    item.setItem(this.items);
    return item;
  }

  public ItemDataBuilder addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    this.items.add(item);
    return this;
  }

  public ItemDataBuilder setItems(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    this.items.clear();
    this.items.addAll(items);
    return this;
  }

  public ItemDataBuilder addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    this.answers.add(answer);
    return this;
  }

  public ItemDataBuilder setAnswers(
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answers) {
    this.answers.clear();
    this.answers.addAll(answers);
    return this;
  }

  private void addLinkedIdIfSet(QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    if (isNotBlank(this.linkId)) {
      item.setLinkId(this.linkId);
    }
  }
}
