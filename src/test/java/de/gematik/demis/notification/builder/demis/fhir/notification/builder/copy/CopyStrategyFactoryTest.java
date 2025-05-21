package de.gematik.demis.notification.builder.demis.fhir.notification.builder.copy;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.AnonymousBundleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.AnonymousCopyStrategy;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NonNominalBundleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NonNominalCopyStrategy;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class CopyStrategyFactoryTest {

  @Test
  void thatNonNominalLaboratoryBundlesAreRecognized() {
    final Bundle nonNominalBundle = new NonNominalBundleBuilder().setDefaults().build();
    final Optional<CopyStrategy<Bundle>> result = CopyStrategyFactory.getInstance(nonNominalBundle);
    assertThat(result).containsInstanceOf(NonNominalCopyStrategy.class);
  }

  @Test
  void thatNonNominalDiseaseBundlesAreRecognized() {
    final Bundle nonNominalBundle =
        new de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease
                .NonNominalBundleBuilder()
            .setDefaults()
            .build();
    final Optional<CopyStrategy<Bundle>> result = CopyStrategyFactory.getInstance(nonNominalBundle);
    assertThat(result)
        .containsInstanceOf(
            de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease
                .NonNominalCopyStrategy.class);
  }

  @Test
  void thatAnonymousBundlesAreRecognized() {
    final Bundle anonymousBundle = new AnonymousBundleBuilder().setDefaults().build();
    final Optional<CopyStrategy<Bundle>> result = CopyStrategyFactory.getInstance(anonymousBundle);
    assertThat(result).containsInstanceOf(AnonymousCopyStrategy.class);
  }

  @Test
  void thatOtherBundlesReturnEmpty() {
    final Optional<CopyStrategy<Bundle>> result = CopyStrategyFactory.getInstance(new Bundle());
    assertThat(result).isEmpty();
  }
}
