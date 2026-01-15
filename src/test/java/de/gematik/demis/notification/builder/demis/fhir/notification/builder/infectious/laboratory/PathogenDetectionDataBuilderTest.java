package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
import static org.hl7.fhir.r4.model.Observation.ObservationStatus.AMENDED;
import static org.hl7.fhir.r4.model.Observation.ObservationStatus.CORRECTED;
import static org.hl7.fhir.r4.model.Observation.ObservationStatus.FINAL;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.math.BigDecimal;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class PathogenDetectionDataBuilderTest {

  private static Patient notifiedPerson;
  private static Specimen specimen;
  private PathogenDetectionDataBuilder pathogenDetectionDataBuilder;

  @BeforeAll
  static void setUpAll() {
    notifiedPerson = new Patient();
    notifiedPerson.setId("1");
    specimen = new Specimen();
    specimen.setId("2");
  }

  @BeforeEach
  void setUp() {
    pathogenDetectionDataBuilder = new PathogenDetectionDataBuilder();
  }

  @Test
  @DisplayName("should set default data in result observation")
  void shouldSetDefaultData() {
    PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 = new PathogenDetectionDataBuilder();
    pathogenDetectionDataBuilder1.setDefaultData();

    Observation observation = pathogenDetectionDataBuilder1.build();

    assertThat(observation.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("laboratory");
    assertThat(observation.getCategoryFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("Laboratory");
    assertThat(observation.getCategoryFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("http://terminology.hl7.org/CodeSystem/observation-category");
    assertThat(observation.getCode().getCodingFirstRep().getSystem()).isEqualTo("http://loinc.org");
    assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation");
    assertThat(observation.getMethod().getCodingFirstRep().getSystem())
        .isEqualTo("http://snomed.info/sct");
  }

  @Test
  @DisplayName("should set id to generated uuid through default setter")
  void shouldSetDefaultDataIdTest() {

    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setDefaultData();

      Observation observation = pathogenDetectionDataBuilder1.build();

      assertThat(observation.getId()).isEqualTo("1");
    }
  }

  @Test
  @DisplayName("should create pathogen detection with set data")
  void shouldCreatePathogenDetection() {
    PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 = new PathogenDetectionDataBuilder();

    Patient patient = new Patient();
    patient.setId("123");
    pathogenDetectionDataBuilder1.setNotifiedPerson(patient);

    Specimen specimen1 = new Specimen();
    specimen1.setId("124");
    pathogenDetectionDataBuilder1.setSpecimen(specimen1);

    Coding coding = new Coding();
    Type valueCodeConcept = new CodeableConcept(coding);
    pathogenDetectionDataBuilder1.setValue(valueCodeConcept);

    pathogenDetectionDataBuilder1.setInterpretationCode("POS");
    pathogenDetectionDataBuilder1.setInterpretationDisplay("Positive");
    pathogenDetectionDataBuilder1.setInterpretationSystem("interpretationSystem");

    pathogenDetectionDataBuilder1.setObservationCodeCode("observationCode");
    pathogenDetectionDataBuilder1.setObservationCodeDisplay("observationDisplay");
    pathogenDetectionDataBuilder1.setObservationCodeSystem("observationSystem");

    pathogenDetectionDataBuilder1.setMethodCode("methodCode");
    pathogenDetectionDataBuilder1.setMethodDisplay("methodDisplay");
    pathogenDetectionDataBuilder1.setMethodSystem("methodSystem");

    Observation pathogenDetection = pathogenDetectionDataBuilder1.build();

    assertThat(pathogenDetection.getSubject().getResource()).isEqualTo(patient);
    assertThat(pathogenDetection.getSpecimen().getResource()).isEqualTo(specimen1);
    assertThat(pathogenDetection.getValue()).isEqualTo(valueCodeConcept);
    assertThat(pathogenDetection.getInterpretationFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("POS");
    assertThat(pathogenDetection.getInterpretationFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("Positive");
    assertThat(pathogenDetection.getInterpretationFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("interpretationSystem");
    assertThat(pathogenDetection.getCode().getCodingFirstRep().getCode())
        .isEqualTo("observationCode");
    assertThat(pathogenDetection.getCode().getCodingFirstRep().getDisplay())
        .isEqualTo("observationDisplay");
    assertThat(pathogenDetection.getCode().getCodingFirstRep().getSystem())
        .isEqualTo("observationSystem");
    assertThat(pathogenDetection.getMethod().getCodingFirstRep().getCode()).isEqualTo("methodCode");
    assertThat(pathogenDetection.getMethod().getCodingFirstRep().getDisplay())
        .isEqualTo("methodDisplay");
    assertThat(pathogenDetection.getMethod().getCodingFirstRep().getSystem())
        .isEqualTo("methodSystem");
  }

  @Test
  void shouldSetMandatoryFields() {
    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getSubject().getResource()).isEqualTo(notifiedPerson);
    assertThat(observation.getSpecimen().getResource()).isEqualTo(specimen);
  }

  @Test
  void shouldSetPathogenDetectionId() {
    pathogenDetectionDataBuilder.setPathogenDetectionId("someId");

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getId()).isEqualTo("someId");
  }

  @Test
  void shouldSetObservationCodeData() {
    pathogenDetectionDataBuilder.setObservationCodeCode("obvservationCodeCode");
    pathogenDetectionDataBuilder.setObservationCodeSystem("obvservationCodeSystem");
    pathogenDetectionDataBuilder.setObservationCodeDisplay("obvservationCodeDisplay");

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getCode().getCoding()).hasSize(1);
    assertThat(observation.getCode().getCoding().get(0).getCode())
        .isEqualTo("obvservationCodeCode");
    assertThat(observation.getCode().getCoding().get(0).getSystem())
        .isEqualTo("obvservationCodeSystem");
    assertThat(observation.getCode().getCoding().get(0).getDisplay())
        .isEqualTo("obvservationCodeDisplay");
  }

  @Test
  void shouldSetMethodData() {
    pathogenDetectionDataBuilder.setMethodCode("methodCode");
    pathogenDetectionDataBuilder.setMethodSystem("methodSystem");
    pathogenDetectionDataBuilder.setMethodDisplay("methodDisplay");

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getMethod().getCoding()).hasSize(1);
    assertThat(observation.getMethod().getCoding().get(0).getCode()).isEqualTo("methodCode");
    assertThat(observation.getMethod().getCoding().get(0).getSystem()).isEqualTo("methodSystem");
    assertThat(observation.getMethod().getCoding().get(0).getDisplay()).isEqualTo("methodDisplay");
  }

  @Test
  void shouldSetInterpretationData() {
    pathogenDetectionDataBuilder.setInterpretationCode("interpretationCode");
    pathogenDetectionDataBuilder.setInterpretationSystem("interpretationSystem");
    pathogenDetectionDataBuilder.setInterpretationDisplay("interpretationDisplay");

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getInterpretation()).hasSize(1);
    assertThat(observation.getInterpretation().get(0).getCoding()).hasSize(1);
    assertThat(observation.getInterpretation().get(0).getCoding().get(0).getCode())
        .isEqualTo("interpretationCode");
    assertThat(observation.getInterpretation().get(0).getCoding().get(0).getSystem())
        .isEqualTo("interpretationSystem");
    assertThat(observation.getInterpretation().get(0).getCoding().get(0).getDisplay())
        .isEqualTo("interpretationDisplay");
  }

  @Test
  void shouldSetCategoryData() {
    pathogenDetectionDataBuilder.setCategoryCode("categoryCode");
    pathogenDetectionDataBuilder.setCategorySystem("categorySystem");
    pathogenDetectionDataBuilder.setCategoryDisplay("categoryDisplay");

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getCategory()).hasSize(1);
    assertThat(observation.getCategory().get(0).getCoding()).hasSize(1);
    assertThat(observation.getCategory().get(0).getCoding().get(0).getCode())
        .isEqualTo("categoryCode");
    assertThat(observation.getCategory().get(0).getCoding().get(0).getSystem())
        .isEqualTo("categorySystem");
    assertThat(observation.getCategory().get(0).getCoding().get(0).getDisplay())
        .isEqualTo("categoryDisplay");
  }

  @Test
  void shouldSetValueData() {
    pathogenDetectionDataBuilder.setValue(new Quantity(500));

    Observation observation =
        pathogenDetectionDataBuilder
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .build();

    assertThat(observation.getValue()).isNotNull();
    Type value = observation.getValue();
    assertThat(value).isInstanceOf(Quantity.class);
    assertThat(((Quantity) value).getValue()).isEqualTo(BigDecimal.valueOf(500));
  }

  @Nested
  @DisplayName("Observation/Pathogen detection convenient setter")
  class ConvenientSetter {
    @Test
    void shouldSetStatusToFinal() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setStatusFinal();
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getStatus()).isEqualTo(FINAL);
    }

    @Test
    void shouldSetStatusToCorrected() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setStatusCorrected();
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getStatus()).isEqualTo(CORRECTED);
    }

    @Test
    void shouldSetStatusToAmended() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setStatusAmended();
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getStatus()).isEqualTo(AMENDED);
    }

    @Test
    void shouldSetMetaProfileUrl() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setProfileUrlHelper("CVDP");
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getMeta().getProfile().get(0).getValue())
          .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/PathogenDetectionCVDP");
    }

    @Test
    void shouldSetMetaProfileUrlUpperCase() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setProfileUrlHelper("cvdp");
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getMeta().getProfile().get(0).getValue())
          .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/PathogenDetectionCVDP");
    }

    @Test
    void shouldSetInterpretationToPositiv() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setInterpretationPositiv();
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getCode())
          .isEqualTo("POS");
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getDisplay())
          .isEqualTo("Positive");
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation");
    }

    @Test
    void shouldSetInterpretationToNegativ() {
      PathogenDetectionDataBuilder pathogenDetectionDataBuilder1 =
          new PathogenDetectionDataBuilder();
      pathogenDetectionDataBuilder1.setInterpretationNegativ();
      Observation observation = pathogenDetectionDataBuilder1.build();
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getCode())
          .isEqualTo("NEG");
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getDisplay())
          .isEqualTo("Negative");
      assertThat(observation.getInterpretationFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation");
    }
  }

  @Nested
  @DisplayName("test copy of observation method")
  class CopyOfTests {
    private Observation createObservation() {
      Observation observation = new Observation();
      observation.setId("123");
      observation.setMeta(new Meta().addProfile("https://example.com/profile"));
      observation.setStatus(Observation.ObservationStatus.FINAL);
      observation.addCategory(
          new CodeableConcept()
              .addCoding(new Coding("categorySystem", "categoryCode", "categoryDisplay")));
      Coding code =
          new Coding("observationCodeSystem", "observationCodeCode", "observationCodeDisplay");
      code.setVersion("1.0.0");
      observation.setCode(new CodeableConcept(code));
      observation.setValue(new Quantity().setValue(500).setUnit("unit"));
      observation.addInterpretation(
          new CodeableConcept()
              .addCoding(
                  new Coding(
                      "interpretationSystem", "interpretationCode", "interpretationDisplay")));
      Coding method = new Coding("methodSystem", "methodCode", "methodDisplay");
      method.setVersion("1.0.0");
      observation.setMethod(new CodeableConcept(method));
      observation.addNote().setText("note");
      return observation;
    }

    @Test
    void shouldCopyMetaProfileUrl() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getMeta().getProfile().get(0).getValue())
          .isEqualTo("https://example.com/profile");
    }

    @Test
    void shouldCopyStatus() {
      Observation originalObservation = createObservation();
      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getStatus()).isEqualTo(Observation.ObservationStatus.FINAL);
    }

    @Test
    void shouldCopyCategory() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getCategoryFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("categorySystem");
      assertThat(result.getCategoryFirstRep().getCodingFirstRep().getCode())
          .isEqualTo("categoryCode");
      assertThat(result.getCategoryFirstRep().getCodingFirstRep().getDisplay())
          .isEqualTo("categoryDisplay");
    }

    @Test
    void shouldCopyCode() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getCode().getCodingFirstRep().getSystem())
          .isEqualTo("observationCodeSystem");
      assertThat(result.getCode().getCodingFirstRep().getCode()).isEqualTo("observationCodeCode");
      assertThat(result.getCode().getCodingFirstRep().getDisplay())
          .isEqualTo("observationCodeDisplay");
      assertThat(result.getCode().getCodingFirstRep().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void shouldCopyValueQuantity() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getValueQuantity().getValue().intValue()).isEqualTo(500);
      assertThat(result.getValueQuantity().getUnit()).isEqualTo("unit");
    }

    @Test
    void shouldCopyValueCodeableConcept() {
      Observation originalObservation = createObservation();
      originalObservation.setValue(
          new CodeableConcept().addCoding(new Coding("valueSystem", "valueCode", "valueDisplay")));

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getValueCodeableConcept().getCodingFirstRep().getCode())
          .isEqualTo("valueCode");
    }

    @Test
    void shouldCopyInterpretation() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getInterpretationFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("interpretationSystem");
      assertThat(result.getInterpretationFirstRep().getCodingFirstRep().getCode())
          .isEqualTo("interpretationCode");
      assertThat(result.getInterpretationFirstRep().getCodingFirstRep().getDisplay())
          .isEqualTo("interpretationDisplay");
    }

    @Test
    void shouldCopyMethod() {
      Observation originalObservation = createObservation();

      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getMethod().getCodingFirstRep().getSystem()).isEqualTo("methodSystem");
      assertThat(result.getMethod().getCodingFirstRep().getCode()).isEqualTo("methodCode");
      assertThat(result.getMethod().getCodingFirstRep().getDisplay()).isEqualTo("methodDisplay");
      assertThat(result.getMethod().getCodingFirstRep().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void shouldCopyNote() {
      Observation originalObservation = createObservation();
      final Observation result =
          PathogenDetectionDataBuilder.deepCopy(originalObservation, notifiedPerson, specimen);
      assertThat(result.getNoteFirstRep().getText()).isEqualTo("note");
    }
  }

  @Test
  void shouldAddVersionIfSetByUser() {
    PathogenDetectionDataBuilder pathogenDetectionDataBuilder = new PathogenDetectionDataBuilder();
    pathogenDetectionDataBuilder.setObservationCodeCode("observationCode");
    pathogenDetectionDataBuilder.setObservationCodeSystem("observationCodeSystem");
    pathogenDetectionDataBuilder.setObservationCodeDisplay("observationCodeDisplay");
    pathogenDetectionDataBuilder.setObservationCodeVersion("observationCodeVersion");

    pathogenDetectionDataBuilder.setMethodCode("methodCode");
    pathogenDetectionDataBuilder.setMethodDisplay("methodDisplay");
    pathogenDetectionDataBuilder.setMethodSystem("methodSystem");
    pathogenDetectionDataBuilder.setMethodCodingVersion("methodCodingVersion");

    Observation observation = pathogenDetectionDataBuilder.build();

    assertThat(observation.getCode().getCodingFirstRep().getVersion())
        .isEqualTo("observationCodeVersion");
    assertThat(observation.getMethod().getCodingFirstRep().getVersion())
        .isEqualTo("methodCodingVersion");
  }
}
