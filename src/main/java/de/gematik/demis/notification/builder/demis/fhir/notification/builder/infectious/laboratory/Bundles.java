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
import com.google.common.collect.Multimaps;
import java.util.Collection;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;

/** Internal helper methods to avoid duplicating code and unnecessarily polluting the Builders. */
class Bundles {
  static Specimen specimenFromObservation(final Observation observation) {
    return (Specimen) observation.getSpecimen().getResource();
  }

  record ObservationCopyResult(
      ImmutableSet<Observation> observations, ImmutableSet<Specimen> specimen) {}

  static ObservationCopyResult copyObservations(
      final Collection<Observation> observations,
      final Patient subject,
      final PractitionerRole submitter) {
    final ImmutableSet.Builder<Specimen> copiedSpecimenBuilder = ImmutableSet.builder();
    final ImmutableSet.Builder<Observation> copiedObservationsBuilder = ImmutableSet.builder();
    Multimaps.index(observations, Bundles::specimenFromObservation)
        .asMap()
        .forEach(
            (originalSpecimen, observationsForSpecimen) -> {
              final Specimen copiedSpecimen =
                  SpecimenDataBuilder.deepCopy(originalSpecimen, subject, submitter);
              observationsForSpecimen.forEach(
                  obs ->
                      copiedObservationsBuilder.add(
                          PathogenDetectionDataBuilder.deepCopy(obs, subject, copiedSpecimen)));
              copiedSpecimenBuilder.add(copiedSpecimen);
            });

    return new ObservationCopyResult(
        copiedObservationsBuilder.build(), copiedSpecimenBuilder.build());
  }
}
