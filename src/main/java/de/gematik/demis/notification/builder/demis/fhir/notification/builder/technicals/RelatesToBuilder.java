package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_ID_SYSTEM;

import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;

/** Build {@link org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent}s. */
public class RelatesToBuilder {

  /**
   * For now we don't offer any methods or instance, if that changes feel free to remove this
   * constructor
   */
  private RelatesToBuilder() {}

  private static final String INITIAL_NOTIFICATION_ID_REFERENCE_TO_TYPE = "Composition";

  @Nonnull
  public static Composition.CompositionRelatesToComponent forInitialNotificationId(
      @Nonnull final String initialNotificationId) {
    final Identifier identifier =
        new Identifier().setSystem(NOTIFICATION_ID_SYSTEM).setValue(initialNotificationId);

    final Reference reference =
        new Reference()
            .setIdentifier(identifier)
            .setType(INITIAL_NOTIFICATION_ID_REFERENCE_TO_TYPE);

    return new Composition.CompositionRelatesToComponent()
        .setCode(Composition.DocumentRelationshipType.APPENDS)
        .setTarget(reference);
  }
}
