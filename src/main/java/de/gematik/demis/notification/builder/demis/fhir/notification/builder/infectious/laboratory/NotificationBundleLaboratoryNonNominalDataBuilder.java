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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder.copyReferencedOrganizations;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL;

import com.google.common.collect.ImmutableSet;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Specimen;

public class NotificationBundleLaboratoryNonNominalDataBuilder
    extends NotificationBundleLaboratoryDataBuilder {

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
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle.getEntry());

    final Patient notifiedPerson = NotifiedPersonNonNominalDataBuilder.deepCopy(ctx.subject());
    final List<Organization> referencedOrganizations = copyReferencedOrganizations(notifiedPerson);

    final PractitionerRole submitter = PractitionerRoleBuilder.deepCopy(ctx.submitter());
    final PractitionerRole notifier = PractitionerRoleBuilder.deepCopy(ctx.notifier());

    final Bundles.ObservationCopyResult observationCopyResult =
        Bundles.copyObservations(ctx.observations(), notifiedPerson, submitter);
    final ImmutableSet<Observation> observations = observationCopyResult.observations();
    final ImmutableSet<Specimen> specimen = observationCopyResult.specimen();
    final DiagnosticReport diagnosticReport =
        LaboratoryReportDataBuilder.deepCopy(ctx.diagnosticReport(), notifiedPerson, observations);

    final Composition composition =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            ctx.composition(), notifier, notifiedPerson, diagnosticReport);

    final NotificationBundleLaboratoryDataBuilder builder =
        new NotificationBundleLaboratoryNonNominalDataBuilder()
            .setNotificationLaboratory(composition)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifier)
            .setSubmitterRole(submitter)
            .setLaboratoryReport(diagnosticReport)
            .setSpecimen(specimen)
            .setPathogenDetection(observations);

    referencedOrganizations.forEach(builder::addAdditionalEntry);
    final Optional<Provenance> provenance = ctx.provenance().map(Provenance::copy);
    provenance.ifPresent(builder::addAdditionalEntry);

    Bundle bundle =
        builder
            .setId(originalBundle.getId())
            .setProfileUrl(builder.getDefaultProfileUrl())
            .setIdentifierAsNotificationBundleId(originalBundle.getIdentifier().getValue())
            .setType(Bundle.BundleType.DOCUMENT)
            .setTimestamp(originalBundle.getTimestamp())
            .setLastUpdated(originalBundle.getMeta().getLastUpdated())
            .build();

    originalBundle.getMeta().getTag().forEach(builder::addTag);

    return bundle;
  }

  @Override
  protected String getDefaultProfileUrl() {
    return PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL;
  }
}
