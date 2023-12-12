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

package de.gematik.demis.notification.builder.demis.fhir.notification;

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Parameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LaboratoryNotificationCreationServiceTest {

  private static LaboratoryNotificationCreationService laboratoryNotificationCreationService;

  @BeforeAll
  static void setUp() {
    laboratoryNotificationCreationService = new LaboratoryNotificationCreationService();
  }

  @Test
  void shouldReturnBundle() {
    Bundle standardLaboratoryNotificationBundle =
        laboratoryNotificationCreationService.createExampleLaboratoryNotificationBundle();

    assertThat(standardLaboratoryNotificationBundle).isNotNull().isInstanceOf(Bundle.class);
  }

  @Test
  void shouldReturnParameters() {
    Parameters standardLaboratoryParameters =
        laboratoryNotificationCreationService.createStandardLaboratoryParameters();

    assertThat(standardLaboratoryParameters).isNotNull().isInstanceOf(Parameters.class);
    assertThat(standardLaboratoryParameters.getParameter()).hasSize(1);
    assertThat(standardLaboratoryParameters.getParameter().get(0).getResource())
        .isInstanceOf(Bundle.class);
  }

  @Test
  void shouldReturnBundleAsString() {
    String standardNotificationBundleJson =
        laboratoryNotificationCreationService.createStandardNotificationBundleJson();
    String standardNotificationBundleXml =
        laboratoryNotificationCreationService.createStandardNotificationBundleXml();

    assertThat(standardNotificationBundleJson).isNotEmpty();
    assertThat(standardNotificationBundleXml).isNotEmpty();
  }

  @Test
  void shouldReturnParametersAsString() {
    String standardNotificationParametersJson =
        laboratoryNotificationCreationService.createStandardNotificationParametersJson();
    String standardNotificationParametersXml =
        laboratoryNotificationCreationService.createStandardNotificationParametersXml();

    assertThat(standardNotificationParametersJson).isNotEmpty();
    assertThat(standardNotificationParametersXml).isNotEmpty();
  }
}
