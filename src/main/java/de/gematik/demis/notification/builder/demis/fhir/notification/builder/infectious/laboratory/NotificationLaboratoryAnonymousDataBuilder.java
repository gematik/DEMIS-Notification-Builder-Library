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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_7_3_COMPOSITION_TITLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_LABORATORY_ANONYMOUS;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.RECEPTION_TIME_STAMP_TYPE;

import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;

public class NotificationLaboratoryAnonymousDataBuilder extends NotificationLaboratoryDataBuilder {

  public static Composition deepCopy(
      @Nonnull final Composition original,
      @Nonnull final PractitionerRole author,
      @Nonnull final Patient subject,
      @Nonnull final DiagnosticReport diagnosticReport) {
    final NotificationLaboratoryAnonymousDataBuilder builder =
        new NotificationLaboratoryAnonymousDataBuilder();
    builder.deepCopyFields(original, author, subject, diagnosticReport);

    Extension extensionByUrl = original.getExtensionByUrl(RECEPTION_TIME_STAMP_TYPE);
    if (extensionByUrl != null) {
      builder.addExtension(
          new Extension().setUrl(extensionByUrl.getUrl()).setValue(extensionByUrl.getValue()));
    }

    return builder.build();
  }

  @Override
  protected String getDefaultProfileUrl() {
    return PROFILE_NOTIFICATION_LABORATORY_ANONYMOUS;
  }

  @Override
  protected String getDefaultTitle() {
    return NOTIFICATION_7_3_COMPOSITION_TITLE;
  }
}
