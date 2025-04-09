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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BundlesTest {

  @Nested
  class CompositionFrom {
    @Test
    void thatItCanExtractAComposition() {
      final Composition composition = new Composition();
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(composition)));

      assertThat(Bundles.compositionFrom(bundle)).contains(composition);
    }

    @Test
    void thatItCanDealWithTheFirstEntryNotBeingTheComposition() {
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent()));

      assertThat(Bundles.compositionFrom(bundle)).isEmpty();
    }

    @Test
    void thatItSafelyCasts() {
      final Bundle otherThanComposition = new Bundle();
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(otherThanComposition)));

      assertThat(Bundles.compositionFrom(bundle)).isEmpty();
    }

    @Test
    void thatItDealsWithNullResource() {
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(null)));

      assertThat(Bundles.compositionFrom(bundle)).isEmpty();
    }
  }

  @Nested
  class SubjectFrom {

    @Test
    void thatItCanExtractASubject() {
      final Patient subject = new Patient();
      final Composition composition = new Composition();
      composition.setSubject(new Reference(subject));
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(composition)));

      assertThat(Bundles.subjectFrom(bundle)).contains(subject);
    }

    @Test
    void thatItCanDealWithAMissingComposition() {
      final Patient subject = new Patient();
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(subject)));

      assertThat(Bundles.subjectFrom(bundle)).isEmpty();
    }

    @Test
    void thatItSafelyCasts() {
      final Bundle notASubject = new Bundle();
      final Composition composition = new Composition();
      composition.setSubject(new Reference(notASubject));
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(composition)));

      assertThat(Bundles.subjectFrom(bundle)).isEmpty();
    }

    @Test
    void thatItDealsWithNullResource() {
      final Composition composition = new Composition();
      final Bundle bundle = new Bundle();
      bundle.setEntry(List.of(new Bundle.BundleEntryComponent().setResource(composition)));

      assertThat(Bundles.subjectFrom(bundle)).isEmpty();
    }
  }
}
