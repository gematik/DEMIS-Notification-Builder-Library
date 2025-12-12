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

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Provenance;

/** Utility methods to extract and transform {@link Provenance}. */
public class Provenances {

  private Provenances() {}

  /**
   * Attempt to find a provenance resource in the given entries.
   *
   * @return Empty if none or more than one Provenance resource are found.
   */
  @Nonnull
  public static Optional<Provenance> from(
      @Nonnull final List<Bundle.BundleEntryComponent> entries) {
    final List<Provenance> provenance =
        entries.stream()
            .map(Bundle.BundleEntryComponent::getResource)
            .filter(Metas.hasProfile(DemisConstants.PROFILE_PROVENANCE))
            .filter(Provenance.class::isInstance)
            .map(Provenance.class::cast)
            .toList();

    if (provenance.size() != 1) {
      return Optional.empty();
    }

    return Optional.of(provenance.getFirst());
  }
}
