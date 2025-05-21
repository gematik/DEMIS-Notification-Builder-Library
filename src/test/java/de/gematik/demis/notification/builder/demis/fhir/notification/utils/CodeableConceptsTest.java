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
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.junit.jupiter.api.Test;

class CodeableConceptsTest {

  @Test
  void matchesCodeAndSystem() {
    final Composition.SectionComponent expected =
        new Composition.SectionComponent()
            .setCode(new CodeableConcept(new Coding("system", "code", "")));
    final Composition.SectionComponent[] inputs =
        new Composition.SectionComponent[] {
          expected,
          new Composition.SectionComponent()
              .setCode(new CodeableConcept(new Coding("system", "", ""))),
          new Composition.SectionComponent().setCode(new CodeableConcept(new Coding("", "", ""))),
          new Composition.SectionComponent().setCode(new CodeableConcept()),
          new Composition.SectionComponent()
        };

    final Predicate<Composition.SectionComponent> hasCode =
        CodeableConcepts.hasCode("system", "code");
    final List<Composition.SectionComponent> actual = Stream.of(inputs).filter(hasCode).toList();

    assertThat(actual).containsExactly(expected);
  }
}
