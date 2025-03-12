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
 * #L%
 */

import com.google.common.collect.ImmutableSet;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonAnonymousDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;

public class AnonymousBundleBuilder extends NotificationBundleLaboratoryDataBuilder {

  /**
   * Create a copy of the given bundle and it's entries.
   *
   * <p>The subject will be replaced by the anonymous flavour when copied.
   */
  public static Bundle deepCopy(final Bundle originalBundle) {
    final AnonymousBundleBuilder builder = new AnonymousBundleBuilder();
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

    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle.getEntry());
    final Patient subject = NotifiedPersonAnonymousDataBuilder.deepCopy(ctx.subject());
    final PractitionerRole submitter = PractitionerRoleBuilder.deepCopy(ctx.submitter());
    final PractitionerRole notifier = PractitionerRoleBuilder.deepCopy(ctx.notifier());

    final Bundles.ObservationCopyResult observationCopyResult =
        Bundles.copyObservations(ctx.observations(), subject, submitter);
    final ImmutableSet<Observation> copiedObservations = observationCopyResult.observations();
    final ImmutableSet<Specimen> copiedSpecimen = observationCopyResult.specimen();

    final DiagnosticReport diagnosticReport =
        LaboratoryReportDataBuilder.deepCopy(ctx.diagnosticReport(), subject, copiedObservations);
    final Composition composition =
        AnonymousCompositionBuilder.deepCopy(
            ctx.composition(), notifier, subject, diagnosticReport);

    builder.setSpecimen(copiedSpecimen);
    builder.setPathogenDetection(copiedObservations);
    builder.setLaboratoryReport(diagnosticReport);
    builder.setNotificationLaboratory(composition);
    builder.setNotifiedPerson(subject);
    builder.setNotifierRole(notifier);
    builder.setSubmitterRole(submitter);

    return builder.build();
  }
}
