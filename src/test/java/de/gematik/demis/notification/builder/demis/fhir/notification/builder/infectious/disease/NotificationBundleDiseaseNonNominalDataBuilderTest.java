package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Meta;
import org.junit.jupiter.api.Test;

class NotificationBundleDiseaseNonNominalDataBuilderTest {

  @Test
  void testBundleNonNominalBuilderProfileUrl() {
    NotificationBundleDiseaseNonNominalDataBuilder builder =
        new NotificationBundleDiseaseNonNominalDataBuilder();
    String expected = DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE_NON_NOMINAL;
    assertThat(builder.getDefaultProfileUrl()).isEqualTo(expected);
  }

  @Test
  void testBundleNonNominalBuilderCompositionCreation() {
    NotificationBundleDiseaseNonNominalDataBuilder builder =
        new NotificationBundleDiseaseNonNominalDataBuilder();
    Condition resource = new Condition();
    resource.setMeta(
        new Meta().addProfile("https://demis.rki.de/fhir/StructureDefinition/DiseaseHIVD"));
    builder.setDisease(resource);
    var actualBuilder = builder.createComposition();
    var actual = actualBuilder.build();

    assertThat(actualBuilder).isInstanceOf(NotificationDiseaseNonNominalDataBuilder.class);
    assertThat(actual.getMeta().getProfile().getFirst().asStringValue())
        .isEqualTo(
            "https://demis.rki.de/fhir/StructureDefinition/NotificationDiseaseNonNominalHIVD");
  }
}
