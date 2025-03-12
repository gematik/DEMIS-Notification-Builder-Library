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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;

/** Simplify reading known components from the Entries list */
record BundleBuilderContext(
    Composition composition,
    Patient subject,
    PractitionerRole submitter,
    PractitionerRole notifier,
    DiagnosticReport diagnosticReport,
    Set<Observation> observations) {
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

    return new BundleBuilderContext(
        composition, subject, submitter, notifier, diagnosticReport, observations);
  }
}
