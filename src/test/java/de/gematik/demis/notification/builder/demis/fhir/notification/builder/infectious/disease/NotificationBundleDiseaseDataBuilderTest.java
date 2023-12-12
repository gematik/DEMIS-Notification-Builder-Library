/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.ParserHelper;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class NotificationBundleDiseaseDataBuilderTest {

  @Test
  void shouldCreateCVDDDiseaseNotification() {

    NotificationBundleDiseaseDataBuilder builder = new NotificationBundleDiseaseDataBuilder();
    Bundle bundle = builder.buildExampleCVDDDiseaseBundle();
    ParserHelper parserHelper = new ParserHelper();
    String s = parserHelper.parseNotificationToJson(bundle);
    assertThat(s)
        .contains(
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotificationBundleDisease\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotificationDiseaseCVDD\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotifiedPerson\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/DiseaseCVDD\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotifierRole\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotifierFacility\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/Hospitalization\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/DiseaseInformationCommon\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/ImmunizationInformationCVDD\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/DiseaseInformationCVDD\" ]",
            "\"profile\": [ \"https://demis.rki.de/fhir/StructureDefinition/NotificationBundleDisease\" ]");
  }
}
