package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Resource;
import org.junit.jupiter.api.Test;

class ProvenancesTest {

  @Test
  void thatMultipleProvenancesResultInEmpty() {
    final Resource provenance =
        new Provenance().setMeta(new Meta().addProfile(DemisConstants.PROFILE_PROVENANCE));

    final Bundle.BundleEntryComponent entry =
        new Bundle.BundleEntryComponent().setResource(provenance);
    final List<Bundle.BundleEntryComponent> entries = List.of(entry, entry);

    final Optional<Provenance> actual = Provenances.from(entries);
    assertThat(actual).isEmpty();
  }

  @Test
  void thatASingleProvenanceIsFound() {
    final Provenance provenance = new Provenance();
    provenance.setMeta(new Meta().addProfile(DemisConstants.PROFILE_PROVENANCE));
    final Bundle.BundleEntryComponent entry =
        new Bundle.BundleEntryComponent().setResource(provenance);

    final Optional<Provenance> actual = Provenances.from(List.of(entry));
    assertThat(actual).containsSame(provenance);
  }

  @Test
  void thatAMissingProvenanceResultsInEmpty() {
    final Optional<Provenance> actual = Provenances.from(List.of());
    assertThat(actual).isEmpty();
  }

  @Test
  void thatAProfileMismatchResultsInEmpty() {
    // GIVEN a resource with an unexpected Profile
    final Patient provenance = new Patient();
    provenance.setMeta(new Meta().addProfile(DemisConstants.PROFILE_PROVENANCE));

    final Bundle.BundleEntryComponent entry =
        new Bundle.BundleEntryComponent().setResource(provenance);
    final List<Bundle.BundleEntryComponent> entries = List.of(entry);

    final Optional<Provenance> actual = Provenances.from(entries);
    assertThat(actual).isEmpty();
  }
}
