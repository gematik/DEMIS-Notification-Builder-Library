package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Meta;

public class NonNominalCopyStrategy implements CopyStrategy<Bundle> {

  private static final String APPLICABLE_PROFILE =
      DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL;

  @Nonnull private final Bundle source;

  public NonNominalCopyStrategy(@Nonnull final Bundle source) {
    this.source = source;
  }

  /** Test if the given Bundle can be copied using this strategy */
  public static boolean isApplicableTo(final Bundle bundle) {
    final Meta meta = bundle.getMeta();
    if (meta == null) {
      return false;
    }

    return meta.hasProfile(APPLICABLE_PROFILE);
  }

  /**
   * Copy a bundle according to {@link
   * NotificationBundleLaboratoryNonNominalDataBuilder#deepCopy(Bundle)}
   */
  @Override
  @Nonnull
  public Bundle copy() {
    return NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(source);
  }
}
