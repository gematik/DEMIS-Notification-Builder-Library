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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.NotificationBundleDiseaseDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

/**
 * Builds disease specific questionnaire response of <a
 * href="https://simplifier.net/demisarztmeldung/diseaseinformation">disease specific
 * information</a> as entry of a disease information
 */
@Slf4j
public class SpecificInformationDataBuilder extends QuestionnaireResponseBuilder
    implements InitializableFhirObjectBuilder {

  private static final String PROFILE_URL =
      "https://demis.rki.de/fhir/StructureDefinition/DiseaseInformation";
  private static final String QUESTIONNAIRE_URL =
      "https://demis.rki.de/fhir/Questionnaire/DiseaseQuestions";
  private static final String IMMUNIZATION_LINK_ID = "immunization";
  private static final String IMMUNIZATION_REFERENCES_LINK_ID = "immunizationRef";

  /**
   * @param notifiedPerson Ensure that only a copy of the resource is passed.
   */
  @Nonnull
  public static QuestionnaireResponse deepCopy(
      @Nonnull final QuestionnaireResponse original, @Nonnull final Patient notifiedPerson) {
    final QuestionnaireResponseBuilder result =
        new SpecificInformationDataBuilder()
            .setQuestionnaireUrl(original.getQuestionnaire())
            .setId(original.getId())
            .setItems(
                original.getItem().stream()
                    .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::copy)
                    .toList())
            .setStatus(original.getStatus().toCode())
            .setNotifiedPerson(notifiedPerson);

    final Optional<String> anyProfile = Metas.profilesFrom(original).stream().findFirst();
    anyProfile.ifPresent(result::setProfileUrl);
    return result.build();
  }

  private static void addImmunization(
      Immunization immunization,
      QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationItem) {
    var reference = new AnswerDataBuilder().setValueReference(immunization).build();
    var immunizationRef =
        new ItemDataBuilder()
            .setLinkId(IMMUNIZATION_REFERENCES_LINK_ID)
            .addAnswer(reference)
            .build();
    immunizationItem.getAnswerFirstRep().addItem(immunizationRef);
  }

  /**
   * Sets: status
   *
   * @return builder
   */
  @Override
  public final SpecificInformationDataBuilder setDefaults() {
    if (getStatus() == null) {
      setStandardStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
    }
    return this;
  }

  /**
   * Add immunization
   *
   * @param immunization immunization
   * @return builder
   */
  public final SpecificInformationDataBuilder addImmunization(Immunization immunization) {
    addImmunization(immunization, getInitializedImmunization());
    return this;
  }

  /**
   * Adds all given immunizations
   *
   * @param immunizations immunizations
   * @return builder
   */
  public final SpecificInformationDataBuilder addImmunizations(
      Collection<Immunization> immunizations) {
    var immunizationItem = getInitializedImmunization();
    immunizations.forEach(i -> addImmunization(i, immunizationItem));
    return this;
  }

  public SpecificInformationDataBuilder setProfileUrlByDisease(String disease) {
    setProfileUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(PROFILE_URL, disease));
    return this;
  }

  public SpecificInformationDataBuilder setQuestionnaireUrlByDisease(String disease) {
    setQuestionnaireUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(QUESTIONNAIRE_URL, disease));
    return this;
  }

  private QuestionnaireResponse.QuestionnaireResponseItemComponent getInitializedImmunization() {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> item =
        getItems().stream().filter(i -> IMMUNIZATION_LINK_ID.equals(i.getLinkId())).findFirst();
    if (item.isPresent()) {
      return item.get();
    }
    return createImmunization();
  }

  private QuestionnaireResponse.QuestionnaireResponseItemComponent createImmunization() {
    var answerYes = new AnswerDataBuilder().setValueCodingYes().build();
    var immunization =
        new ItemDataBuilder().setLinkId(IMMUNIZATION_LINK_ID).addAnswer(answerYes).build();
    addItem(immunization);
    return immunization;
  }
}
