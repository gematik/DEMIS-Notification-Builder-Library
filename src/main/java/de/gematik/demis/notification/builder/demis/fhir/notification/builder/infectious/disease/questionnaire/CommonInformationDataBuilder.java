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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.QUESTIONNAIRE_DISEASE_QUESTIONS_COMMON;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import java.util.Collection;
import java.util.Optional;
import lombok.Setter;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

/**
 * Build questionnaire response of <a
 * href="https://simplifier.net/demisarztmeldung/diseaseinformationcommon">common information:</a>
 * as entry of a disease notification
 */
@Setter
public class CommonInformationDataBuilder extends QuestionnaireResponseBuilder
    implements InitializableFhirObjectBuilder {

  /** root item for hospitalizations list */
  private static final String HOSPITALIZED_LINK_ID = "hospitalized";

  /** individual hospitalization grouping item */
  private static final String HOSPITALIZED_GROUP_LINK_ID = "hospitalizedGroup";

  /** individual hospitalization reference item */
  private static final String HOSPITALIZED_ENCOUNTER_LINK_ID = "hospitalizedEncounter";

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent
      createHospitalizedEncounter(Encounter hospitalization) {
    var reference = new AnswerDataBuilder().setValueReference(hospitalization).build();
    return new ItemDataBuilder()
        .setLinkId(HOSPITALIZED_ENCOUNTER_LINK_ID)
        .addAnswer(reference)
        .build();
  }

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent createHospitalizedGroup(
      QuestionnaireResponse.QuestionnaireResponseItemComponent hospitalizedEncounter) {
    return new ItemDataBuilder()
        .setLinkId(HOSPITALIZED_GROUP_LINK_ID)
        .addItem(hospitalizedEncounter)
        .build();
  }

  /**
   * Sets:
   *
   * <ul>
   *   <li>profile URL
   *   <li>questionnaire URL
   *   <li>status
   * </ul>
   *
   * @return builder
   */
  @Override
  public CommonInformationDataBuilder setDefaults() {
    if (getProfileUrl() == null) {
      setProfileUrl(PROFILE_DISEASE_INFORMATION_COMMON);
    }
    if (getQuestionnaireUrl() == null) {
      setQuestionnaireUrl(QUESTIONNAIRE_DISEASE_QUESTIONS_COMMON);
    }
    if (getStatus() == null) {
      setStandardStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
    }
    return this;
  }

  /**
   * Add hospitalization
   *
   * @param hospitalization hospitalization
   * @return builder
   */
  public CommonInformationDataBuilder addHospitalization(Encounter hospitalization) {
    var hospitalizedEncounter = createHospitalizedEncounter(hospitalization);
    var hospitalizedGroup = createHospitalizedGroup(hospitalizedEncounter);
    var hospitalized = getInitializedHospitalized();
    hospitalized.getAnswerFirstRep().addItem(hospitalizedGroup);
    return this;
  }

  /**
   * Adds given hospitalizations
   *
   * @param hospitalizations hospitalizations
   * @return builder
   */
  public CommonInformationDataBuilder addHospitalizations(Collection<Encounter> hospitalizations) {
    hospitalizations.forEach(this::addHospitalization);
    return this;
  }

  private QuestionnaireResponse.QuestionnaireResponseItemComponent getInitializedHospitalized() {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> item =
        getItems().stream().filter(i -> HOSPITALIZED_LINK_ID.equals(i.getLinkId())).findFirst();
    if (item.isPresent()) {
      return item.get();
    }
    return createHospitalized();
  }

  private QuestionnaireResponse.QuestionnaireResponseItemComponent createHospitalized() {
    var answerYes = new AnswerDataBuilder().setValueCodingYes().build();
    var hospitalized =
        new ItemDataBuilder().setLinkId(HOSPITALIZED_LINK_ID).addAnswer(answerYes).build();
    addItem(hospitalized);
    return hospitalized;
  }
}
