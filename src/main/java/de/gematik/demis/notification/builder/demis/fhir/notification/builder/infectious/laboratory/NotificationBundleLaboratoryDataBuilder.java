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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.Bundles.*;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder.*;

import com.google.common.collect.ImmutableSet;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Specimen;

/**
 * Builder for a FHIR bundle containing a laboratory notification. Special feature: <code>
 * createComposition()</code> creates a composition based on the configured data.
 */
@Setter
@Slf4j
public class NotificationBundleLaboratoryDataBuilder extends BundleDataBuilder {

  private Patient notifiedPerson;
  private PractitionerRole notifierRole;
  private PractitionerRole submitterRole;
  private Collection<Specimen> specimen = new ArrayList<>();
  private Collection<Observation> pathogenDetection = new ArrayList<>();
  private DiagnosticReport laboratoryReport;
  private Composition notificationLaboratory;

  @Override
  protected void addEntries() {
    addComposition();
    addNotifiedPerson();
    addNotifier();
    addSubmitter();
    addSpecimen();
    addPathogenDetection();
    addLaboratoryReport();
  }

  @Override
  public NotificationBundleLaboratoryDataBuilder setDefaults() {
    super.setDefaults();
    return this;
  }

  /**
   * Create composition with configured data
   *
   * @return composition builder
   */
  public NotificationLaboratoryDataBuilder createComposition() {
    log.debug("Laboratory notification bundle builder generates composition.");
    NotificationLaboratoryDataBuilder composition = new NotificationLaboratoryDataBuilder();
    composition.setDefault();
    composition.setLaboratoryReport(this.laboratoryReport);
    composition.setNotifierRole(this.notifierRole);
    composition.setNotifiedPerson(this.notifiedPerson);
    return composition;
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY;
  }

  private void addSpecimen() {
    specimen.forEach(this::addEntry);
  }

  private void addLaboratoryReport() {
    addEntry(laboratoryReport);
  }

  private void addPathogenDetection() {
    addEntries(List.copyOf(pathogenDetection));
  }

  private void addSubmitter() {
    addEntryOfPractitionerRole(this.submitterRole);
  }

  private void addNotifiedPerson() {
    addEntry(notifiedPerson);
  }

  private void addNotifier() {
    addEntryOfPractitionerRole(notifierRole);
  }

  private void addComposition() {
    if (this.notificationLaboratory != null) {
      addEntry(this.notificationLaboratory);
    }
  }

  public static Bundle createNonNominalExcerpt(@Nonnull final Bundle originalBundle) {
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle.getEntry());

    final Patient notifiedPerson =
        NotifiedPersonNonNominalDataBuilder.createExcerptNotByNamePatient(ctx.subject());

    final PractitionerRole submitter = deepCopy73(ctx.submitter());
    final PractitionerRole notifier = deepCopy73(ctx.notifier());

    final ObservationCopyResult observationCopyResult =
        copyObservations(ctx.observations(), notifiedPerson, submitter);
    final ImmutableSet<Observation> observations = observationCopyResult.observations();
    final ImmutableSet<Specimen> specimen = observationCopyResult.specimen();
    final DiagnosticReport diagnosticReport =
        LaboratoryReportDataBuilder.deepCopy(ctx.diagnosticReport(), notifiedPerson, observations);

    final Composition composition =
        NotificationLaboratoryNonNominalDataBuilder.excerptCopy(
            ctx.composition(), notifier, notifiedPerson, diagnosticReport);

    final NotificationBundleLaboratoryDataBuilder builder =
        new NotificationBundleLaboratoryDataBuilder()
            .setNotificationLaboratory(composition)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifier)
            .setSubmitterRole(submitter)
            .setLaboratoryReport(diagnosticReport)
            .setPathogenDetection(observations)
            .setSpecimen(specimen);

    ctx.provenance().map(Provenance::copy).ifPresent(builder::addAdditionalEntry);

    // Ensure this is called before builder.build()!
    originalBundle.getMeta().getTag().forEach(builder::addTag);

    final String originalBundleIdentifier = originalBundle.getIdentifier().getValue();
    final Identifier newIdentifierForCopiedBundle =
        new Identifier()
            .setValue(Utils.generateUuidString())
            .setSystem(DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);

    final Coding referenceTagToOriginalBundle =
        new Coding(
            DemisConstants.RELATED_NOTIFICATION_CODING_SYSTEM,
            originalBundleIdentifier,
            "Relates to message with identifier: " + originalBundleIdentifier);
    return builder
        .setId(originalBundle.getId())
        .setProfileUrl(builder.getDefaultProfileUrl())
        .setIdentifier(newIdentifierForCopiedBundle)
        .setType(Bundle.BundleType.DOCUMENT)
        .setTimestamp(originalBundle.getTimestamp())
        .setLastUpdated(originalBundle.getMeta().getLastUpdated())
        .addTag(referenceTagToOriginalBundle)
        .build();
  }
}
