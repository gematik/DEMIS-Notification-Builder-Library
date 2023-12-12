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
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.AMENDED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.CORRECTED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LaboratoryReportDataBuilderTest {

  private static Patient notifiedPerson;
  private static Observation pathogenDetection;
  private LaboratoryReportDataBuilder laboratoryReportDataBuilder;

  @BeforeAll
  static void setUp() {
    notifiedPerson = new Patient();
    pathogenDetection = new Observation();
  }

  @BeforeEach
  void setUpEach() {
    laboratoryReportDataBuilder = new LaboratoryReportDataBuilder();
  }

  @Test
  void shouldSetMetaProfileUrl() {
    LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
    laboratoryReportDataBuilder1.setProfileUrlHelper("CVDP");
    DiagnosticReport diagnosticReport = laboratoryReportDataBuilder1.build();
    assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportCVDP");
  }

  @Test
  void shouldSetMetaProfileUrlUpperCase() {
    LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
    laboratoryReportDataBuilder1.setProfileUrlHelper("cvdp");
    DiagnosticReport diagnosticReport = laboratoryReportDataBuilder1.build();
    assertThat(diagnosticReport.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportCVDP");
  }

  @Test
  void shouldSetDefaultData() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setDefaultData();
      DiagnosticReport diagnosticReport = laboratoryReportDataBuilder1.build();
      assertThat(diagnosticReport.getCode().getCodingFirstRep().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/notificationCategory");
      assertThat(diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/conclusionCode");
      assertThat(diagnosticReport.getId()).isEqualTo("1");
    }
  }

  @Test
  void shouldBuildWithGivenData() {
    LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
    Patient patient = new Patient();

    laboratoryReportDataBuilder1.setNotifiedPerson(patient);
    Date date = new Date();
    laboratoryReportDataBuilder1.setIssued(date);
    laboratoryReportDataBuilder1.setStatus(CORRECTED);
    laboratoryReportDataBuilder1.setCodeCode("codeCode");
    laboratoryReportDataBuilder1.setCodeDisplay("codeDisplay");
    laboratoryReportDataBuilder1.setConclusion("conclusionText");
    laboratoryReportDataBuilder1.setConclusionCodeCode("conclusionCode");
    laboratoryReportDataBuilder1.setConclusionCodeDisplay("conclusionDisplay");
    Observation pathogenDetection1 = new Observation();
    laboratoryReportDataBuilder1.setPathogenDetections(
        Collections.singletonList(pathogenDetection1));

    DiagnosticReport diagnosticReport = laboratoryReportDataBuilder1.build();

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

    LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();

    Observation observation1 = new Observation();
    observation1.setId("observation1");

    Observation observation2 = new Observation();
    observation2.setId("observation2");

    DiagnosticReport diagnosticReport =
        laboratoryReportDataBuilder1.buildLaboratoryReport(
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
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setReportStatusToPreliminary();
      assertThat(laboratoryReportDataBuilder1.build().getStatus()).isEqualTo(PRELIMINARY);
    }

    @Test
    void shouldSetStatusToFinal() {
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setReportStatusToFinal();
      assertThat(laboratoryReportDataBuilder1.build().getStatus()).isEqualTo(FINAL);
    }

    @Test
    void shouldSetStatusToAmended() {
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setReportStatusToAmended();
      assertThat(laboratoryReportDataBuilder1.build().getStatus()).isEqualTo(AMENDED);
    }

    @Test
    void shouldSetStatusToCorrected() {
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setReportStatusToCorrected();
      assertThat(laboratoryReportDataBuilder1.build().getStatus()).isEqualTo(CORRECTED);
    }

    @Test
    void shouldSetConclusionCodeToDetected() {
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setConclusionCodeStatusToDetected();
      assertThat(
              laboratoryReportDataBuilder1
                  .build()
                  .getConclusionCodeFirstRep()
                  .getCodingFirstRep()
                  .getCode())
          .isEqualTo("pathogenDetected");
    }

    @Test
    void shouldSetConclusionCodeToNotDetected() {
      LaboratoryReportDataBuilder laboratoryReportDataBuilder1 = new LaboratoryReportDataBuilder();
      laboratoryReportDataBuilder1.setConclusionCodeStatusToNotDetected();
      assertThat(
              laboratoryReportDataBuilder1
                  .build()
                  .getConclusionCodeFirstRep()
                  .getCodingFirstRep()
                  .getCode())
          .isEqualTo("pathogenNotDetected");
    }
  }
}
