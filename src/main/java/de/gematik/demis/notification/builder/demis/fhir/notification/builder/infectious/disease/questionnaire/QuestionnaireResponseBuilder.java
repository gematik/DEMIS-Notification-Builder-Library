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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.FhirObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;

@Data
public abstract class QuestionnaireResponseBuilder implements FhirObjectBuilder {

  private final List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
      new ArrayList<>();

  /**
   * A business identifier assigned to a particular completed (or partially completed)
   * questionnaire.
   */
  private String id;

  /**
   * RKI specific type of questionnaire response: common information, CVDD, etc. - to be placed in
   * the meta information.
   */
  private String profileUrl;

  /**
   * The Questionnaire that defines and organizes the questions for which answers are being
   * provided.
   */
  private String questionnaireUrl;

  /**
   * The position of the questionnaire response within its overall lifecycle: in-progress |
   * completed | amended | entered-in-error | stopped
   */
  private String status;

  private Patient notifiedPerson;

  @Override
  public final QuestionnaireResponse build() {
    QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
    addId(questionnaireResponse);
    addProfileUrlIfSet(questionnaireResponse);
    addQuestionnaireUrlIfSet(questionnaireResponse);
    addStatusIfSet(questionnaireResponse);
    addNotifiedPerson(questionnaireResponse);
    addItems(questionnaireResponse);
    return questionnaireResponse;
  }

  public final QuestionnaireResponseBuilder setStandardStatus(
      QuestionnaireResponse.QuestionnaireResponseStatus status) {
    this.status = status.toCode();
    return this;
  }

  public final QuestionnaireResponseBuilder addItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    this.items.add(item);
    return this;
  }

  public final QuestionnaireResponseBuilder addItems(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    items.forEach(this::addItem);
    return this;
  }

  final List<QuestionnaireResponse.QuestionnaireResponseItemComponent> getItems() {
    return this.items;
  }

  public final QuestionnaireResponseBuilder setItems(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    this.items.clear();
    this.items.addAll(items);
    return this;
  }

  private void addItems(QuestionnaireResponse questionnaireResponse) {
    questionnaireResponse.setItem(this.items);
  }

  private void addNotifiedPerson(QuestionnaireResponse questionnaireResponse) {
    questionnaireResponse.setSubject(new Reference(this.notifiedPerson));
  }

  private void addId(QuestionnaireResponse questionnaireResponse) {
    if (this.id == null) {
      this.id = generateUuidString();
    }
    questionnaireResponse.setId(this.id);
  }

  private void addStatusIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(this.status)) {
      questionnaireResponse.setStatus(
          QuestionnaireResponse.QuestionnaireResponseStatus.fromCode(this.status.toLowerCase()));
    }
  }

  private void addQuestionnaireUrlIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(this.questionnaireUrl)) {
      questionnaireResponse.setQuestionnaire(this.questionnaireUrl);
    }
  }

  private void addProfileUrlIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(this.profileUrl)) {
      questionnaireResponse.setMeta(new Meta().addProfile(this.profileUrl));
    }
  }
}
