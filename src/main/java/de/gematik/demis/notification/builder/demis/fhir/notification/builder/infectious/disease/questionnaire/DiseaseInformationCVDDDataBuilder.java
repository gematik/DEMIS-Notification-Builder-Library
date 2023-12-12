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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.*;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;

@Setter
public class DiseaseInformationCVDDDataBuilder {

  private String id;
  private String profileUrl;
  private String questionnaireUrl;
  private String status;

  public QuestionnaireResponse buildExampleCVDDInformationSpecificData(
      Patient notifiedPerson,
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> answerList) {

    profileUrl = requireNonNullElse(profileUrl, PROFILE_DISEASE_INFORMATION_CVDD);
    questionnaireUrl = requireNonNullElse(questionnaireUrl, QUESTIONAIRE_DISEASE_QUESTIONS_CVDD);
    status = requireNonNullElse(status, "completed");

    return buildExampleCVDDDiseaseInformationSpecificData(notifiedPerson, answerList);
  }

  public QuestionnaireResponse buildExampleCVDDDiseaseInformationSpecificData(
      Patient notifiedPerson,
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> answerList) {
    QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();

    id = requireNonNullElse(id, generateUuidString());
    questionnaireResponse.setId(id);

    addProfileUrlIfSet(questionnaireResponse);
    addQuestionnaireUrlIfSet(questionnaireResponse);
    addStatusIfSet(questionnaireResponse);

    questionnaireResponse.setSubject(new Reference(notifiedPerson));
    questionnaireResponse.setItem(answerList);

    return questionnaireResponse;
  }

  private void addStatusIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(status)) {
      questionnaireResponse.setStatus(
          QuestionnaireResponse.QuestionnaireResponseStatus.valueOf(status.toUpperCase()));
    }
  }

  private void addQuestionnaireUrlIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(questionnaireUrl)) {
      questionnaireResponse.setQuestionnaire(questionnaireUrl);
    }
  }

  private void addProfileUrlIfSet(QuestionnaireResponse questionnaireResponse) {
    if (isNotBlank(profileUrl)) {
      questionnaireResponse.setMeta(new Meta().addProfile(profileUrl));
    }
  }
}
