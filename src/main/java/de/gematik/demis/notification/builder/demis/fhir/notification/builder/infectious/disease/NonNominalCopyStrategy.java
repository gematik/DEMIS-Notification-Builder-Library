package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.copy.CopyStrategy;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;

/**
 * The disease part for NonNominal bundles. See {@link
 * de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NonNominalCopyStrategy}
 * for the laboratory version.
 */
public class NonNominalCopyStrategy implements CopyStrategy<Bundle> {

  @Nonnull private final Bundle resource;

  public NonNominalCopyStrategy(@Nonnull final Bundle resource) {
    this.resource = resource;
  }

  public static boolean isApplicableTo(@Nonnull final Bundle resource) {
    return Metas.profilesFrom(resource)
        .contains(DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE_NON_NOMINAL);
  }

  @Override
  @Nonnull
  public Bundle copy() {
    return NotificationBundleDiseaseNonNominalDataBuilder.deepCopy(resource);
  }
}
