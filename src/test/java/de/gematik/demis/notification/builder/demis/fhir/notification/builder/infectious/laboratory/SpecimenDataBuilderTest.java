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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.AVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.ENTEREDINERROR;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNAVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNSATISFACTORY;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class SpecimenDataBuilderTest {

  private static Patient notifiedPerson;
  private static PractitionerRole submittingRole;
  private SpecimenDataBuilder specimenDataBuilder;

  @BeforeAll
  static void setUpAll() {
    notifiedPerson = new Patient();
    submittingRole = new PractitionerRole();
  }

  @BeforeEach
  void setUp() {

    specimenDataBuilder = new SpecimenDataBuilder();
  }

  @Test
  void shouldSetProfileUrlWithGivenCode() {
    SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
    specimenDataBuilder1.setProfileUrlHelper("cvdp");
    assertThat(specimenDataBuilder1.build().getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/SpecimenCVDP");
  }

  @Test
  void shouldSetProfileUrlWithGivenCodeUpperCase() {
    SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
    specimenDataBuilder1.setProfileUrlHelper("CVDP");
    assertThat(specimenDataBuilder1.build().getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/SpecimenCVDP");
  }

  @Test
  void shouldSetDefaultData() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
      specimenDataBuilder1.setDefaultData();
      Specimen specimen = specimenDataBuilder1.build();
      assertThat(specimen.getType().getCodingFirstRep().getSystem())
          .isEqualTo("http://snomed.info/sct");
      assertThat(specimen.getId()).isEqualTo("1");
    }
  }

  @Test
  void shouldSetMandatoryData() {
    Specimen specimen = specimenDataBuilder.buildExampleSpecimen(notifiedPerson, submittingRole);

    assertThat(specimen.getSubject().getResource()).isEqualTo(notifiedPerson);
    assertThat(specimen.getCollection().getCollector().getResource()).isEqualTo(submittingRole);
  }

  @Nested
  @DisplayName("convenient status setter")
  class StatusSetterTests {
    @Test
    void shouldSetStatusToAvailable() {
      SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
      specimenDataBuilder1.setStatusToAvailable();
      assertThat(specimenDataBuilder1.build().getStatus()).isEqualTo(AVAILABLE);
    }

    @Test
    void shouldSetStatusToUnavailable() {
      SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
      specimenDataBuilder1.setStatusToUnavailable();
      assertThat(specimenDataBuilder1.build().getStatus()).isEqualTo(UNAVAILABLE);
    }

    @Test
    void shouldSetStatusToUnsatisfactory() {
      SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
      specimenDataBuilder1.setStatusToUnsatisfactory();
      assertThat(specimenDataBuilder1.build().getStatus()).isEqualTo(UNSATISFACTORY);
    }

    @Test
    void shouldSetStatusToEnteredInError() {
      SpecimenDataBuilder specimenDataBuilder1 = new SpecimenDataBuilder();
      specimenDataBuilder1.setStatusToEnteredinerror();
      assertThat(specimenDataBuilder1.build().getStatus()).isEqualTo(ENTEREDINERROR);
    }
  }
}
