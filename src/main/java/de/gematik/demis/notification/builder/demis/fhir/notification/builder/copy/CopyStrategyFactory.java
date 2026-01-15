package de.gematik.demis.notification.builder.demis.fhir.notification.builder.copy;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.AnonymousCopyStrategy;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;

public class CopyStrategyFactory {

  private CopyStrategyFactory() {}

  /**
   * Guess which strategy to use for the given Bundle. If none could be determined an {@link
   * Optional#empty()} is returned.
   */
  @Nonnull
  public static Optional<CopyStrategy<Bundle>> getInstance(@Nonnull final Bundle resource) {
    if (de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory
        .NonNominalCopyStrategy.isApplicableTo(resource)) {
      return Optional.of(
          new de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious
              .laboratory.NonNominalCopyStrategy(resource));
    }

    if (de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease
        .NonNominalCopyStrategy.isApplicableTo(resource)) {
      return Optional.of(
          new de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious
              .disease.NonNominalCopyStrategy(resource));
    }

    if (AnonymousCopyStrategy.isApplicableTo(resource)) {
      return Optional.of(new AnonymousCopyStrategy(resource));
    }

    return Optional.empty();
  }
}
