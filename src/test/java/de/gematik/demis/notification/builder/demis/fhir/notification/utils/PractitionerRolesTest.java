package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class PractitionerRolesTest {

  @Test
  void thatSingleAuthorIsFound() {
    final PractitionerRole expected = new PractitionerRole();
    final Reference reference = new Reference();
    reference.setResource(expected);

    final Composition composition = new Composition();
    composition.setAuthor(List.of(reference));

    final Optional<PractitionerRole> actual = PractitionerRoles.authorFrom(composition);
    assertThat(actual).contains(expected);
  }

  @Test
  void thatEmptyIsReturnedForMissingAuthor() {
    final Composition composition = new Composition();
    composition.setAuthor(List.of());

    final Optional<PractitionerRole> actual = PractitionerRoles.authorFrom(composition);
    assertThat(actual).isEmpty();
  }

  @Test
  void thatEmptyIsReturnedForMultipleAuthors() {
    final PractitionerRole expected = new PractitionerRole();
    final Reference reference = new Reference();
    reference.setResource(expected);

    final Composition composition = new Composition();
    // Not strictly speaking two separate instance, but it works for us here
    composition.setAuthor(List.of(reference, reference));

    final Optional<PractitionerRole> actual = PractitionerRoles.authorFrom(composition);
    assertThat(actual).isEmpty();
  }
}
