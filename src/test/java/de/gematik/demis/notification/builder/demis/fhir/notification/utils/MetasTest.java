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

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Meta;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MetasTest {

  @Nested
  class ProfilesFrom {
    @Test
    void thatProfilesExtractsASingleProfile() {
      final Bundle bundle = new Bundle();
      bundle.setMeta(new Meta().addProfile("test"));

      assertThat(Metas.profilesFrom(bundle)).containsExactly("test");
    }

    @Test
    void thatItCanDealWithDuplicateProfiles() {
      final Bundle bundle = new Bundle();
      bundle.setMeta(new Meta().addProfile("test").addProfile("test").addProfile("more"));

      assertThat(Metas.profilesFrom(bundle)).containsExactlyInAnyOrder("test", "more");
    }

    @Test
    void thatItCanDealWithMissingMeta() {
      final Bundle bundle = new Bundle();

      assertThat(Metas.profilesFrom(bundle)).isEmpty();
    }

    @Test
    void thatNoNullValuesArePropagated() {
      final Bundle bundle = new Bundle();
      bundle.setMeta(new Meta().addProfile("test").addProfile(null).addProfile("more"));

      assertThat(Metas.profilesFrom(bundle)).containsExactlyInAnyOrder("test", "more");
    }
  }
}
