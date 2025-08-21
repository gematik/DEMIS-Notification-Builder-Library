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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Provenances;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Specimen;

/**
 * Provide a simplified layer to access resources in a Bundle. Use {@link
 * BundleBuilderContext#copyWith(Function, BiFunction)} to manipulate resources. Use {@link
 * BundleBuilderContext#applyTo(NotificationBundleLaboratoryDataBuilder)} in combination with a
 * Builder to connect the relevant resources correctly and create new Bundles.
 */
record BundleBuilderContext(
    Composition composition,
    Patient subject,
    PractitionerRole submitter,
    PractitionerRole notifier,
    DiagnosticReport diagnosticReport,
    Set<Observation> observations,
    Set<Specimen> specimen,
    Optional<Provenance> provenance) {
  public static BundleBuilderContext from(final List<Bundle.BundleEntryComponent> entries) {
    final Composition composition = (Composition) entries.getFirst().getResource();
    final Patient subject = (Patient) composition.getSubject().getResource();
    final PractitionerRole notifier =
        (PractitionerRole) composition.getAuthorFirstRep().getResource();
    final Optional<Bundle.BundleEntryComponent> submittingFacility =
        entries.stream()
            .filter(
                e -> e.getResource().getMeta().hasProfile(DemisConstants.PROFILE_SUBMITTING_ROLE))
            .findFirst();
    Preconditions.checkArgument(submittingFacility.isPresent());
    final PractitionerRole submitter = (PractitionerRole) submittingFacility.get().getResource();
    final DiagnosticReport diagnosticReport =
        (DiagnosticReport) composition.getSection().getFirst().getEntry().getFirst().getResource();
    final Set<Observation> observations =
        diagnosticReport.getResult().stream()
            .map(reference -> (Observation) reference.getResource())
            .collect(
                ImmutableSet
                    .toImmutableSet()); // We want to keep the sequence so we prefer ImmutableSet!

    final Set<Specimen> specimens =
        observations.stream()
            .map(Bundles::specimenFromObservation)
            .collect(ImmutableSet.toImmutableSet());

    final Optional<Provenance> provenance = Provenances.from(entries);

    return new BundleBuilderContext(
        composition,
        subject,
        submitter,
        notifier,
        diagnosticReport,
        observations,
        specimens,
        provenance);
  }

  /**
   * Copy all resources contained in this context. Some resources like the subject require special
   * transformers.
   *
   * @param subjectTransformer Transformer to apply to the subject
   * @param compositionTransformer Transformer to apply to the composition after all other resource
   *     have been processed. Use the provided context to access the already copied resources.
   */
  public BundleBuilderContext copyWith(
      final UnaryOperator<Patient> subjectTransformer,
      final BiFunction<Composition, BundleBuilderContext, Composition> compositionTransformer) {
    final Patient subject = subjectTransformer.apply(this.subject);
    final PractitionerRole submitter = PractitionerRoleBuilder.deepCopy(this.submitter);
    final PractitionerRole notifier = PractitionerRoleBuilder.deepCopy(this.notifier);

    final Bundles.ObservationCopyResult observationCopyResult =
        Bundles.copyObservations(observations, subject, submitter);
    final ImmutableSet<Observation> observations = observationCopyResult.observations();
    final ImmutableSet<Specimen> specimen = observationCopyResult.specimen();
    final DiagnosticReport diagnosticReport =
        LaboratoryReportDataBuilder.deepCopy(this.diagnosticReport, subject, observations);
    final Optional<Provenance> provenance = this.provenance.map(Provenance::copy);

    final BundleBuilderContext intermediate =
        new BundleBuilderContext(
            composition,
            subject,
            submitter,
            notifier,
            diagnosticReport,
            observations,
            specimen,
            provenance);

    // Pass the intermediate context with the copied resources, otherwise the transformer might
    // access "stale" data
    final Composition composition = compositionTransformer.apply(this.composition, intermediate);

    return new BundleBuilderContext(
        composition,
        subject,
        submitter,
        notifier,
        diagnosticReport,
        observations,
        specimen,
        provenance);
  }

  public void applyTo(final NotificationBundleLaboratoryDataBuilder builder) {
    builder.setSpecimen(specimen);
    builder.setPathogenDetection(observations);
    builder.setLaboratoryReport(diagnosticReport);
    builder.setNotificationLaboratory(composition);
    builder.setNotifiedPerson(subject);
    builder.setNotifierRole(notifier);
    builder.setSubmitterRole(submitter);
    provenance.ifPresent(builder::addAdditionalEntry);
  }
}
