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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNotByNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;

public class NonNominalBundleBuilder extends NotificationBundleLaboratoryDataBuilder {

  /**
   * Create a copy of the bundle and a copy of all ressources.
   *
   * <p>This method expects a valid bundle with:
   *
   * <ul>
   *   <li>Composition
   *   <li>DiagnosticReport
   *   <li>Submitter
   *   <li>Notifier
   *   <li>Subject
   *   <li>Specimen
   *   <li>Observation
   * </ul>
   *
   * Relationships between these must be correctly established. i.e. the observation must reference
   * the specimen for this to work. Missing elements will cause the copy to fail. No validation is
   * performed.
   */
  public static Bundle deepCopy(@Nonnull final Bundle originalBundle) {
    final NonNominalBundleBuilder builder = new NonNominalBundleBuilder();
    builder.setProfileUrl(originalBundle.getMeta().getProfile().getFirst().getValueAsString());
    builder.setId(originalBundle.getId());
    originalBundle
        .getMeta()
        .getTag()
        .forEach(
            tag -> builder.addTag(new Coding(tag.getSystem(), tag.getCode(), tag.getDisplay())));

    final Identifier identifier = originalBundle.getIdentifier();
    builder.setIdentifier(
        new Identifier().setSystem(identifier.getSystem()).setValue(identifier.getValue()));

    builder.setType(originalBundle.getType());
    builder.setLastUpdated(originalBundle.getMeta().getLastUpdated());
    builder.setTimestamp(originalBundle.getTimestamp());

    BundleBuilderContext.from(originalBundle.getEntry())
        .copyWith(
            NotifiedPersonNotByNameDataBuilder::deepCopy,
            (composition, bundleBuilderContext) ->
                NonNominalCompositionBuilder.deepCopy(
                    bundleBuilderContext.composition(),
                    bundleBuilderContext.notifier(),
                    bundleBuilderContext.subject(),
                    bundleBuilderContext.diagnosticReport()))
        .applyTo(builder);

    return builder.build();
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL;
  }
}
