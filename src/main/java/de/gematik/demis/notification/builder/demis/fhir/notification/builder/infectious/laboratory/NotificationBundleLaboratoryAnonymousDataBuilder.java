package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonAnonymousDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;

public class NotificationBundleLaboratoryAnonymousDataBuilder
    extends NotificationBundleLaboratoryDataBuilder {

  /**
   * Create a copy of the given bundle and it's entries.
   *
   * <p>The subject will be replaced by the anonymous flavour when copied.
   */
  public static Bundle deepCopy(final Bundle originalBundle) {
    final NotificationBundleLaboratoryAnonymousDataBuilder builder =
        new NotificationBundleLaboratoryAnonymousDataBuilder();
    builder.setId(originalBundle.getId());
    builder.setProfileUrl(originalBundle.getMeta().getProfile().getFirst().getValueAsString());
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
            NotifiedPersonAnonymousDataBuilder::deepCopy,
            (composition, copy) ->
                NotificationLaboratoryAnonymousDataBuilder.deepCopy(
                    composition, copy.notifier(), copy.subject(), copy.diagnosticReport()))
        .applyTo(builder);

    return builder.build();
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_ANONYMOUS;
  }
}
