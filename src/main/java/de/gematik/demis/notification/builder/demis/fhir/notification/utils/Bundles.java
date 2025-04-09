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

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

/**
 * Methods to simplify extracting data and processing Bundles.
 *
 * <p>The methods generally try to be as safe as possible: if the bundle structure is not following
 * the well-defined structure you can expect empty Optionals instead of exceptions.
 *
 * <p><b>Well-defined Bundle structure</b>
 *
 * <p>For the most part this class expects arguments to follow a well-defined structure. For
 * example: the first element in the entries section of a Bundle should be a composition. This
 * composition should reference the subject/notified person.
 */
public class Bundles {

  /**
   * Extract the composition of a bundle in a null-safe manner. Expect the first entry's resource to
   * be the composition. If the first entry can't be safely cast to a {@link Composition} an empty
   * Optional is returned.
   */
  public static Optional<Composition> compositionFrom(@Nonnull final Bundle bundle) {
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

  /**
   * Extract the subject (aka notified person) of a bundle in a null-safe manner. Expects a
   * well-defined
   */
  public static Optional<Patient> subjectFrom(@Nonnull final Bundle bundle) {
    final List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
    if (entries.isEmpty()) {
      return Optional.empty();
    }

    return compositionFrom(bundle)
        .map(
            c -> {
              if (!c.hasSubject()) {
                return null;
              }

              final IBaseResource candidate = c.getSubject().getResource();
              if (candidate instanceof Patient patient) {
                return patient;
              }

              return null;
            });
  }
}
