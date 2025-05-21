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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class QuestionnaireResponsesTest {

  @Test
  void thatSpecificQuestionnaireResponseIsFound() {
    final QuestionnaireResponse expected = new QuestionnaireResponse();
    expected.setId("1"); // to avoid IBase#empty == true
    final Reference reference = new Reference();
    reference.setResource(expected);

    final Composition composition = new Composition();
    final Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
    sectionComponent.setCode(
        new CodeableConcept(
            new Coding(
                DemisConstants.CODE_SYSTEM_SECTION_CODE,
                DemisConstants.DISEASE_SECTION_SPECIFIC_CODE,
                "")));
    composition.addSection(sectionComponent.setEntry(List.of(reference)));

    final Optional<QuestionnaireResponse> actual = QuestionnaireResponses.specificFrom(composition);
    assertThat(actual).contains(expected);
  }

  @Test
  void thatEmptyIsReturnedForMultipleSpecificQuestionnaireResponses() {
    final QuestionnaireResponse expected = new QuestionnaireResponse();
    expected.setId("1"); // to avoid IBase#empty == true
    final Reference reference = new Reference();
    reference.setResource(expected);

    final Composition composition = new Composition();
    final Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
    sectionComponent.setCode(
        new CodeableConcept(
            new Coding(
                DemisConstants.CODE_SYSTEM_SECTION_CODE,
                DemisConstants.DISEASE_SECTION_SPECIFIC_CODE,
                "")));
    composition.addSection(sectionComponent.setEntry(List.of(reference)));
    composition.addSection(sectionComponent.setEntry(List.of(reference)));

    final Optional<QuestionnaireResponse> actual = QuestionnaireResponses.specificFrom(composition);
    assertThat(actual).isEmpty();
  }
}
