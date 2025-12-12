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
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;

public class Conditions {
  private Conditions() {}

  /**
   * Find the first condition among the section entries of the given {@link Composition}.
   *
   * @return Empty optional if no, or multiple conditions found
   */
  @Nonnull
  public static Optional<Condition> from(@Nonnull final Composition composition) {
    if (!composition.hasSection()) {
      return Optional.empty();
    }

    final List<Composition.SectionComponent> section = composition.getSection();
    final List<Condition> list =
        section.stream()
            .map(Composition.SectionComponent::getEntry)
            .flatMap(Collection::stream)
            .map(Reference::getResource)
            .filter(Objects::nonNull) // Reference#getResource can return null
            .filter(Condition.class::isInstance)
            .map(Condition.class::cast)
            .toList();
    if (list.size() != 1) {
      return Optional.empty();
    }
    return Optional.of(list.getFirst());
  }
}
