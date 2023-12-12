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

import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BedOccupancyExampleCreationServiceTest {

  private BedOccupancyExampleCreationService bedOccupancyExampleCreationService;

  @BeforeEach
  void setUp() {
    bedOccupancyExampleCreationService = new BedOccupancyExampleCreationService();
  }

  @Test
  void shouldCreateExampleBundle() {
    Bundle testBedOccupancyBundle =
        bedOccupancyExampleCreationService.createTestBedOccupancyBundle();

    assertThat(testBedOccupancyBundle).isInstanceOf(Bundle.class);
    assertThat(testBedOccupancyBundle.getEntry()).hasSize(4);
    assertThat(testBedOccupancyBundle.getEntry().get(0).getResource())
        .isInstanceOf(Composition.class);
    assertThat(testBedOccupancyBundle.getEntry().get(1).getResource())
        .isInstanceOf(Organization.class);
    assertThat(testBedOccupancyBundle.getEntry().get(2).getResource())
        .isInstanceOf(PractitionerRole.class);
    assertThat(testBedOccupancyBundle.getEntry().get(3).getResource())
        .isInstanceOf(QuestionnaireResponse.class);
  }

  @Test
  void shouldCreateBundleAsJsonString() {
    String testBedOccupancyBundleJson =
        bedOccupancyExampleCreationService.createTestBedOccupancyBundleJson();

    assertThat(testBedOccupancyBundleJson)
        .isInstanceOf(String.class)
        .contains("{")
        .contains("}")
        .doesNotContain("<")
        .doesNotContain(">");
  }

  @Test
  void shouldCreateBundleAsXmlString() {
    String testBedOccupancyBundleXml =
        bedOccupancyExampleCreationService.createTestBedOccupancyBundleXml();

    assertThat(testBedOccupancyBundleXml)
        .isInstanceOf(String.class)
        .contains("<")
        .contains(">")
        .doesNotContain("{")
        .doesNotContain("}");
  }

  @Test
  void shouldCreateParameterWithBundle() {

    Parameters standardLaboratoryParameters =
        bedOccupancyExampleCreationService.createStandardLaboratoryParameters();

    Resource resource = standardLaboratoryParameters.getParameter().get(0).getResource();

    assertThat(resource).isInstanceOf(Bundle.class);

    Bundle testBedOccupancyBundle = (Bundle) resource;

    assertThat(testBedOccupancyBundle.getEntry()).hasSize(4);
    assertThat(testBedOccupancyBundle.getEntry().get(0).getResource())
        .isInstanceOf(Composition.class);
    assertThat(testBedOccupancyBundle.getEntry().get(1).getResource())
        .isInstanceOf(Organization.class);
    assertThat(testBedOccupancyBundle.getEntry().get(2).getResource())
        .isInstanceOf(PractitionerRole.class);
    assertThat(testBedOccupancyBundle.getEntry().get(3).getResource())
        .isInstanceOf(QuestionnaireResponse.class);
  }

  @Test
  void shouldCreateParameterAsJsonString() {
    String testBedOccupancyParameterJson =
        bedOccupancyExampleCreationService.createTestBedOccupancyParametersJson();

    assertThat(testBedOccupancyParameterJson)
        .isInstanceOf(String.class)
        .contains("{")
        .contains("}")
        .doesNotContain("<")
        .doesNotContain(">");
  }

  @Test
  void shouldCreateParameterAsXmlString() {
    String testBedOccupancyParameterXml =
        bedOccupancyExampleCreationService.createTestBedOccupancyParametersXml();

    assertThat(testBedOccupancyParameterXml)
        .isInstanceOf(String.class)
        .contains("<")
        .contains(">")
        .doesNotContain("{")
        .doesNotContain("}");
  }
}
