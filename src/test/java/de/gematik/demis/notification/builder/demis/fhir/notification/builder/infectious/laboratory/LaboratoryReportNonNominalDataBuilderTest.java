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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.REASON_FOR_TESTING_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.AMENDED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.CORRECTED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LaboratoryReportNonNominalDataBuilderTest {

  private static Patient notifiedPerson;
  private static Observation pathogenDetection;
  private LaboratoryReportNonNominalDataBuilder laboratoryReportDataBuilder;

  @BeforeAll
  static void setUp() {
    notifiedPerson = new Patient();
    pathogenDetection = new Observation();
  }

  @BeforeEach
  void setUpEach() {
    laboratoryReportDataBuilder = new LaboratoryReportNonNominalDataBuilder();
  }

  @Test
  void shouldSetMetaProfileUrl() {
    LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
        new LaboratoryReportNonNominalDataBuilder();
    laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
    laboratoryReportNonNomialDataBuilder1.setProfileUrlHelper("CVDP");
    DiagnosticReport diagnosticReport = laboratoryReportNonNomialDataBuilder1.build();
    assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportNonNominalCVDP");
  }

  @Test
  void shouldSetMetaProfileUrlUpperCase() {
    LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
        new LaboratoryReportNonNominalDataBuilder();
    laboratoryReportNonNomialDataBuilder1.setProfileUrlHelper("cvdp");
    final Patient patient = new Patient();
    patient.setId("123");
    laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(patient);
    DiagnosticReport diagnosticReport = laboratoryReportNonNomialDataBuilder1.build();
    assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportNonNominalCVDP");
  }

  @Test
  void shouldSetDefaultData() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setDefaultData();
      final Patient notifiedPerson1 = new Patient();
      notifiedPerson1.setId("123");
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(notifiedPerson1);
      DiagnosticReport diagnosticReport = laboratoryReportNonNomialDataBuilder1.build();
      assertThat(diagnosticReport.getCode().getCodingFirstRep().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/notificationCategory");
      assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/conclusionCode");
      assertThat(diagnosticReport.getId()).isEqualTo("1");
    }
  }

  @Test
  void shouldBuildWithGivenData() {
    LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
        new LaboratoryReportNonNominalDataBuilder();
    Patient patient = new Patient();
    patient.setId("123");

    laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(patient);
    Date date = new Date();
    laboratoryReportNonNomialDataBuilder1.setIssued(date);
    laboratoryReportNonNomialDataBuilder1.setStatus(CORRECTED);
    laboratoryReportNonNomialDataBuilder1.setCodeCode("codeCode");
    laboratoryReportNonNomialDataBuilder1.setCodeDisplay("codeDisplay");
    laboratoryReportNonNomialDataBuilder1.setConclusion("conclusionText");
    laboratoryReportNonNomialDataBuilder1.setConclusionCodeCode("conclusionCode");
    laboratoryReportNonNomialDataBuilder1.setConclusionCodeDisplay("conclusionDisplay");
    Observation pathogenDetection1 = new Observation();
    pathogenDetection1.setId("321");
    laboratoryReportNonNomialDataBuilder1.setPathogenDetections(
        Collections.singletonList(pathogenDetection1));

    DiagnosticReport diagnosticReport = laboratoryReportNonNomialDataBuilder1.build();

    assertThat(diagnosticReport.getSubject().getResource()).isEqualTo(patient);
    assertThat(diagnosticReport.getIssued()).isEqualTo(date);
    assertThat(diagnosticReport.getStatus()).isEqualTo(CORRECTED);
    assertThat(diagnosticReport.getCode().getCodingFirstRep().getCode()).isEqualTo("codeCode");
    assertThat(diagnosticReport.getCode().getCodingFirstRep().getDisplay())
        .isEqualTo("codeDisplay");
    assertThat(diagnosticReport.getConclusion()).isEqualTo("conclusionText");
    assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("conclusionCode");
    assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("conclusionDisplay");
    assertThat(diagnosticReport.getResult())
        .extracting("resource")
        .containsOnly(pathogenDetection1);
  }

  @Test
  void shouldGivenDataOnLaboratoryReportNotifiedPersonAndPathogenDetection() {
    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getSubject().getResource()).isEqualTo(notifiedPerson);
    assertThat(diagnosticReport.getResult()).hasSize(1);
    assertThat(diagnosticReport.getResult().get(0).getResource()).isEqualTo(pathogenDetection);
  }

  @Test
  void shouldSetAllGivenDataOnLaboratoryReportCodeData() {
    laboratoryReportDataBuilder.setCodeDisplay("codeDisplay");
    laboratoryReportDataBuilder.setCodeSystem("codeSystem");
    laboratoryReportDataBuilder.setCodeCode("codeCode");

    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getCode().getCoding()).hasSize(1);
    assertThat(diagnosticReport.getCode().getCoding().get(0).getCode()).isEqualTo("codeCode");
    assertThat(diagnosticReport.getCode().getCoding().get(0).getSystem()).isEqualTo("codeSystem");
    assertThat(diagnosticReport.getCode().getCoding().get(0).getDisplay()).isEqualTo("codeDisplay");
  }

  @Test
  void shouldSetAllGivenDataOnLaboratoryReportConclusionCodeData() {
    laboratoryReportDataBuilder.setConclusionCodeCode("concludSionCodeCode");
    laboratoryReportDataBuilder.setConclusionCodeDisplay("concludSionCodeDisplay");
    laboratoryReportDataBuilder.setConclusionCodeSystem("concludSionCodeSystem");

    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getConclusionCode()).hasSize(1);
    assertThat(diagnosticReport.getConclusionCode().get(0).getCoding()).hasSize(1);
    assertThat(diagnosticReport.getConclusionCode().get(0).getCoding().get(0).getCode())
        .isEqualTo("concludSionCodeCode");
    assertThat(diagnosticReport.getConclusionCode().get(0).getCoding().get(0).getSystem())
        .isEqualTo("concludSionCodeSystem");
    assertThat(diagnosticReport.getConclusionCode().get(0).getCoding().get(0).getDisplay())
        .isEqualTo("concludSionCodeDisplay");
  }

  @Test
  void shouldSetAllGivenDataOnLaboratoryReportStatus() {
    laboratoryReportDataBuilder.setStatus(FINAL);

    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getStatus()).isEqualTo(FINAL);
  }

  @Test
  void shouldSetGeneratedDataForIssued() {
    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getIssued()).isNotNull();
    assertThat(diagnosticReport.getIssued()).isCloseTo(Date.from(Instant.now()), 1000L);
  }

  @Test
  void shouldSetMeta() {
    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder.buildExampleCVDPLaboratoryReport(
            notifiedPerson, pathogenDetection);

    assertThat(diagnosticReport.getMeta().getProfile()).hasSize(1);
    assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
        .isEqualTo(DemisConstants.PROFILE_LABORATORY_REPORT_CVDP);
  }

  @Test
  void shouldSetObservationList() {

    LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
        new LaboratoryReportNonNominalDataBuilder();

    Observation observation1 = new Observation();
    observation1.setId("observation1");

    Observation observation2 = new Observation();
    observation2.setId("observation2");

    DiagnosticReport diagnosticReport =
        laboratoryReportNonNomialDataBuilder1.buildLaboratoryReport(
            notifiedPerson, Arrays.asList(observation1, observation2));

    assertThat(diagnosticReport.getResult())
        .hasSize(2)
        .extracting("resource")
        .containsExactlyInAnyOrder(observation1, observation2);
  }

  @Nested
  @DisplayName("convenient setter tests")
  class SetterTests {
    @Test
    void shouldSetStatusToPrelimnary() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setReportStatusToPreliminary();
      assertThat(laboratoryReportNonNomialDataBuilder1.build().getStatus()).isEqualTo(PRELIMINARY);
    }

    @Test
    void shouldSetStatusToFinal() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setReportStatusToFinal();
      assertThat(laboratoryReportNonNomialDataBuilder1.build().getStatus()).isEqualTo(FINAL);
    }

    @Test
    void shouldSetStatusToAmended() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setReportStatusToAmended();
      assertThat(laboratoryReportNonNomialDataBuilder1.build().getStatus()).isEqualTo(AMENDED);
    }

    @Test
    void shouldSetStatusToCorrected() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setReportStatusToCorrected();
      assertThat(laboratoryReportNonNomialDataBuilder1.build().getStatus()).isEqualTo(CORRECTED);
    }

    @Test
    void shouldSetConclusionCodeToDetected() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setConclusionCodeStatusToDetected();
      assertThat(
              laboratoryReportNonNomialDataBuilder1
                  .build()
                  .getConclusionCodeFirstRep()
                  .getCodingFirstRep()
                  .getCode())
          .isEqualTo("pathogenDetected");
    }

    @Test
    void shouldSetConclusionCodeToNotDetected() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.setConclusionCodeStatusToNotDetected();
      assertThat(
              laboratoryReportNonNomialDataBuilder1
                  .build()
                  .getConclusionCodeFirstRep()
                  .getCodingFirstRep()
                  .getCode())
          .isEqualTo("pathogenNotDetected");
    }

    @Test
    void shouldSetExtensionReasonForTestingToMicrobiologyScreeningTest() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.withReasonForTesting(
          "314074007", "Microbiology screening test");

      final Extension extension =
          laboratoryReportNonNomialDataBuilder1
              .build()
              .getExtensionByUrl(REASON_FOR_TESTING_SYSTEM);
      assertThat(extension).isNotNull();
      assertThat(extension.hasValue()).isTrue();
      if (extension.getValue() instanceof CodeableConcept codeableConcept) {
        assertThat(codeableConcept.getCodingFirstRep().getCode()).isEqualTo("314074007");
        assertThat(codeableConcept.getCodingFirstRep().getDisplay())
            .isEqualTo("Microbiology screening test");
      } else {
        fail("Extension value is not a CodeableConcept");
      }
    }

    @Test
    void shouldAddBasedOnToLaboratoryReport() {
      LaboratoryReportNonNominalDataBuilder laboratoryReportNonNomialDataBuilder1 =
          new LaboratoryReportNonNominalDataBuilder();
      laboratoryReportNonNomialDataBuilder1.setNotifiedPerson(TestObjects.notifiedPerson());
      laboratoryReportNonNomialDataBuilder1.addBasedOn("basedOnId");
      DiagnosticReport diagnosticReport = laboratoryReportNonNomialDataBuilder1.build();
      assertThat(diagnosticReport.getBasedOnFirstRep().getIdentifier().getValue())
          .isEqualTo("basedOnId");

      var parser = FhirContext.forR4Cached().newJsonParser();
      var diagnosticReportJson = parser.encodeToString(diagnosticReport.getBasedOnFirstRep());
      assertThat(diagnosticReportJson)
          .isEqualTo(
              "{\"type\":\"ServiceRequest\",\"identifier\":{\"system\":\"https://demis.rki.de/fhir/NamingSystem/ServiceRequestId\",\"value\":\"basedOnId\"}}");
    }
  }

  @Nested
  @DisplayName("copyOf method tests")
  class CopyOfTests {
    private DiagnosticReport createDiagnosticReport() {
      DiagnosticReport diagnosticReport = new DiagnosticReport();
      diagnosticReport.setMeta(new Meta().addProfile("https://example.com/profile"));
      diagnosticReport.addExtension(
          new Extension(
              "http://hl7.org/fhir/StructureDefinition/workflow-reasonCode",
              new CodeableConcept().setText("Reason for testing")));
      diagnosticReport.addBasedOn(
          new Reference()
              .setType("ServiceRequest")
              .setIdentifier(new Identifier().setSystem("system").setValue("value")));
      diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
      diagnosticReport.setCode(
          new CodeableConcept()
              .addCoding(new Coding().setSystem("system").setCode("code").setDisplay("display")));
      diagnosticReport.setIssued(new Date());
      diagnosticReport.setConclusion("Conclusion text");
      diagnosticReport.setConclusionCode(
          Collections.singletonList(
              new CodeableConcept()
                  .addCoding(
                      new Coding().setSystem("system").setCode("code").setDisplay("display"))));
      return diagnosticReport;
    }

    @Test
    void shouldCopyMetaProfileUrl() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());
      assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
          .isEqualTo("https://example.com/profile");
    }

    @Test
    void shouldCopyExtensionReasonForTesting() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());
      assertThat(
              diagnosticReport.getExtensionByUrl(
                  "http://hl7.org/fhir/StructureDefinition/workflow-reasonCode"))
          .usingRecursiveComparison()
          .isEqualTo(diagnosticReport.getExtension().get(0));
    }

    @Test
    void shouldCopyBasedOnReference() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());
      assertThat(diagnosticReport.getBasedOnFirstRep().getIdentifier().getSystem())
          .isEqualTo("system");
      assertThat(diagnosticReport.getBasedOnFirstRep().getIdentifier().getValue())
          .isEqualTo("value");
    }

    @Test
    void shouldCopyStatus() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());
      assertThat(diagnosticReport.getStatus())
          .isEqualTo(DiagnosticReport.DiagnosticReportStatus.FINAL);
    }

    @Test
    void shouldCopyCode() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());

      assertThat(diagnosticReport.getCode().getCodingFirstRep().getSystem()).isEqualTo("system");
      assertThat(diagnosticReport.getCode().getCodingFirstRep().getCode()).isEqualTo("code");
      assertThat(diagnosticReport.getCode().getCodingFirstRep().getDisplay()).isEqualTo("display");
    }

    @Test
    void shouldCopyIssuedDate() {
      final DiagnosticReport original = createDiagnosticReport();
      Date issuedDate = original.getIssued();

      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              original, TestObjects.notifiedPerson(), List.of());
      assertThat(diagnosticReport.getIssued()).isEqualTo(issuedDate);
    }

    @Test
    void shouldCopyConclusion() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());

      assertThat(diagnosticReport.getConclusion()).isEqualTo("Conclusion text");
    }

    @Test
    void shouldCopyConclusionCode() {
      final DiagnosticReport diagnosticReport =
          LaboratoryReportNonNominalDataBuilder.deepCopy(
              createDiagnosticReport(), TestObjects.notifiedPerson(), List.of());
      assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("system");
      assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getCode())
          .isEqualTo("code");
      assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getDisplay())
          .isEqualTo("display");
    }
  }
}
