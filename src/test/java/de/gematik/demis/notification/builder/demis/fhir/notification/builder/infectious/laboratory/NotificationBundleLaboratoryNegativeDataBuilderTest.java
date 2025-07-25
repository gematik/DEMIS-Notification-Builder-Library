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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class NotificationBundleLaboratoryNegativeDataBuilderTest {

  @Test
  void shouldCreateBundleWithNegativeUrl() {
    NotificationBundleLaboratoryNegativeDataBuilder builder =
        new NotificationBundleLaboratoryNegativeDataBuilder();
    builder.setDefaults();

    builder.setNotificationLaboratory(TestObjects.composition());
    builder.setLaboratoryReport(TestObjects.laboratoryReport());
    builder.setPathogenDetection(singletonList(TestObjects.pathogenDetection()));
    builder.setSpecimen(singletonList(TestObjects.specimen()));
    builder.setNotifierRole(TestObjects.notifier());
    builder.setSubmitterRole(TestObjects.submitter());
    builder.setNotifiedPerson(TestObjects.notifiedPerson());

    Bundle bundle = builder.build();

    assertThat(bundle.getMeta().getProfile()).hasSize(1);
    assertThat(bundle.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(
            "https://demis.rki.de/fhir/StructureDefinition/NotificationBundleLaboratoryNegative");
  }
}
