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
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Resource;

public class Compositions {
  private Compositions() {}

  /**
   * Extract the composition of a bundle in a null-safe manner. Expect the first entry's resource to
   * be the composition. If the first entry can't be safely cast to a {@link Composition} an empty
   * Optional is returned.
   */
  @Nonnull
  public static Optional<Composition> from(@Nonnull final Bundle bundle) {
    final List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
    if (entries.isEmpty()) {
      return Optional.empty();
    }

    final Resource candidate = entries.getFirst().getResource();
    if (candidate instanceof Composition composition) {
      return Optional.of(composition);
    }

    return Optional.empty();
  }
}
