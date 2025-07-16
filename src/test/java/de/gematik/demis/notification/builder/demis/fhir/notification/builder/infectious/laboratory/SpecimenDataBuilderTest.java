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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.AVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.ENTEREDINERROR;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNAVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNSATISFACTORY;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.util.Date;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Meta;
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

  @Nested
  @DisplayName("copy of method tests")
  class CopyOfTest {
    private Specimen createSpecimen() {
      Specimen specimen = new Specimen();
      specimen.setMeta(new Meta().addProfile("https://example.com/profile"));
      specimen.setStatus(Specimen.SpecimenStatus.AVAILABLE);
      Coding code = new Coding("typeSystem", "typeCode", "typeDisplay");
      code.setVersion("1.0.0");
      specimen.setType(new CodeableConcept(code));
      specimen.setReceivedTime(new Date());
      Specimen.SpecimenCollectionComponent collection = new Specimen.SpecimenCollectionComponent();
      collection.setCollected(new DateTimeType(new Date()));
      specimen.setCollection(collection);
      specimen.addProcessing().setDescription("processing");
      specimen.addNote().setText("note");
      return specimen;
    }

    @Test
    void shouldCopyMetaProfileUrl() {
      Specimen originalSpecimen = createSpecimen();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getMeta().getProfile().getFirst().getValue())
          .isEqualTo("https://example.com/profile");
    }

    @Test
    void shouldCopyStatus() {
      Specimen originalSpecimen = createSpecimen();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getStatus()).isEqualTo(Specimen.SpecimenStatus.AVAILABLE);
    }

    @Test
    void shouldCopyType() {
      Specimen originalSpecimen = createSpecimen();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getType().getCodingFirstRep().getSystem()).isEqualTo("typeSystem");
      assertThat(result.getType().getCodingFirstRep().getCode()).isEqualTo("typeCode");
      assertThat(result.getType().getCodingFirstRep().getDisplay()).isEqualTo("typeDisplay");
      assertThat(result.getType().getCodingFirstRep().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void shouldCopyReceivedTime() {
      Specimen originalSpecimen = createSpecimen();
      Date receivedTime = originalSpecimen.getReceivedTime();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getReceivedTime()).isEqualTo(receivedTime);
    }

    @Test
    void shouldCopyCollectedDate() {
      Specimen originalSpecimen = createSpecimen();
      Date collectedDate = originalSpecimen.getCollection().getCollectedDateTimeType().getValue();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getCollection().getCollectedDateTimeType().getValue())
          .isEqualTo(collectedDate);
    }

    @Test
    void shouldCopyProcessing() {
      Specimen originalSpecimen = createSpecimen();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getProcessingFirstRep().getDescription()).isEqualTo("processing");
    }

    @Test
    void shouldCopyNotes() {
      Specimen originalSpecimen = createSpecimen();

      final Specimen result =
          SpecimenDataBuilder.deepCopy(
              originalSpecimen, TestObjects.notifiedPerson(), TestObjects.submitter());
      assertThat(result.getNoteFirstRep().getText()).isEqualTo("note");
    }
  }
}
