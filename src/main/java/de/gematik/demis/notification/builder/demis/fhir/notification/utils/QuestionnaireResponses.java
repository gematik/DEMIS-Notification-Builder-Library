package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;

public class QuestionnaireResponses {
  private QuestionnaireResponses() {}

  /**
   * Find the first specific questionnaire among the section entries of the given {@link
   * Composition}.
   *
   * @return Empty optional if no, or multiple conditions found
   */
  @Nonnull
  public static Optional<QuestionnaireResponse> specificFrom(
      @Nonnull final Composition composition) {
    if (!composition.hasSection()) {
      return Optional.empty();
    }

    final List<Composition.SectionComponent> section = composition.getSection();
    final List<QuestionnaireResponse> result =
        section.stream()
            .filter(
                CodeableConcepts.hasCode(
                    DemisConstants.CODE_SYSTEM_SECTION_CODE,
                    DemisConstants.DISEASE_SECTION_SPECIFIC_CODE))
            .map(Composition.SectionComponent::getEntry)
            .flatMap(Collection::stream)
            .map(Reference::getResource)
            .filter(Predicate.not(IBase::isEmpty))
            .filter(QuestionnaireResponse.class::isInstance)
            .map(QuestionnaireResponse.class::cast)
            .toList();

    if (result.size() != 1) {
      return Optional.empty();
    }

    return Optional.of(result.getFirst());
  }

  @Nonnull
  public static Optional<QuestionnaireResponse> commonForm(@Nonnull final Composition composition) {
    if (!composition.hasSection()) {
      return Optional.empty();
    }

    final List<Composition.SectionComponent> section = composition.getSection();
    final List<QuestionnaireResponse> result =
        section.stream()
            .filter(
                CodeableConcepts.hasCode(
                    DemisConstants.CODE_SYSTEM_SECTION_CODE,
                    DemisConstants.DISEASE_SECTION_COMMON_CODE))
            .map(Composition.SectionComponent::getEntry)
            .flatMap(Collection::stream)
            .map(Reference::getResource)
            .filter(Predicate.not(IBase::isEmpty))
            .filter(QuestionnaireResponse.class::isInstance)
            .map(QuestionnaireResponse.class::cast)
            .toList();

    if (result.size() != 1) {
      return Optional.empty();
    }

    return Optional.of(result.getFirst());
  }
}
