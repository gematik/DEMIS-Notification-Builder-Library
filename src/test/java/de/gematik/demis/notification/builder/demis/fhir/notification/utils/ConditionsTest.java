package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class ConditionsTest {

  @Test
  void thatConditionIsFound() {
    final Condition expected = new Condition();
    final Composition composition = new Composition();
    final Reference iBaseReference = new Reference();
    iBaseReference.setResource(expected);
    composition.addSection(new Composition.SectionComponent().setEntry(List.of(iBaseReference)));

    assertThat(Conditions.from(composition)).contains(expected);
  }

  @Test
  void thatEmptyReferenceAreNotAProblem() {
    final Composition composition = new Composition();
    final Reference iBaseReference = new Reference();
    assertThatNoException()
        .isThrownBy(
            () -> {
              composition.addSection(
                  new Composition.SectionComponent().setEntry(List.of(iBaseReference)));
              assertThat(Conditions.from(composition)).isEmpty();
            });
  }

  @Test
  void thatEmptyIsReturnedForMultipleConditions() {
    final Condition expected = new Condition();
    final Composition composition = new Composition();
    final Reference iBaseReference = new Reference();
    iBaseReference.setResource(expected);
    // Not strictly speaking two separate instance, but it works for us here
    composition.addSection(new Composition.SectionComponent().setEntry(List.of(iBaseReference)));
    composition.addSection(new Composition.SectionComponent().setEntry(List.of(iBaseReference)));

    assertThat(Conditions.from(composition)).isEmpty();
  }
}
