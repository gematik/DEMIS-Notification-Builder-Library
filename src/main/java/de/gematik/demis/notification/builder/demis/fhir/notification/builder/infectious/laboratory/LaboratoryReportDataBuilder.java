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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CONCLUSION_CODE_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_REPORT_BASE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNullElse;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.AMENDED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.CORRECTED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class LaboratoryReportDataBuilder {

  public static final String HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE =
      "https://demis.rki.de/fhir/CodeSystem/conclusionCode";
  public static final String PATHOGEN_DETECTED = "pathogenDetected";

  private String codeSystem;
  private String codeCode;
  private String codeDisplay;
  private String laboratoryId;
  private String conclusionCodeSystem;
  private String conclusionCodeCode;
  private String conclusionCodeDisplay;
  private DiagnosticReport.DiagnosticReportStatus status;
  private Date issued;
  private String metaProfileUrl;
  private Patient notifiedPerson;
  private String conclusion;
  private List<Observation> pathogenDetections = new ArrayList<>();

  public LaboratoryReportDataBuilder setReportStatusToPreliminary() {
    status = PRELIMINARY;
    return this;
  }

  public LaboratoryReportDataBuilder setReportStatusToFinal() {
    status = FINAL;
    return this;
  }

  public LaboratoryReportDataBuilder setReportStatusToAmended() {
    status = AMENDED;
    return this;
  }

  public LaboratoryReportDataBuilder setReportStatusToCorrected() {
    status = CORRECTED;
    return this;
  }

  public LaboratoryReportDataBuilder setConclusionCodeStatusToDetected() {
    conclusionCodeCode = PATHOGEN_DETECTED;
    return this;
  }

  public LaboratoryReportDataBuilder setConclusionCodeStatusToNotDetected() {
    conclusionCodeCode = "pathogenNotDetected";
    return this;
  }

  public LaboratoryReportDataBuilder setDefaultData() {
    codeSystem = CODE_SYSTEM_NOTIFICATION_CATEGORY;
    conclusionCodeSystem = CONCLUSION_CODE_SYSTEM;
    laboratoryId = generateUuidString();
    return this;
  }

  /**
   * creates a url and adds the given code
   *
   * @param pathogenCode the pathogen code, e.g. cvdp
   * @return
   */
  public LaboratoryReportDataBuilder setProfileUrlHelper(String pathogenCode) {
    metaProfileUrl = LABORATORY_REPORT_BASE_URL + pathogenCode.toUpperCase();
    return this;
  }

  public DiagnosticReport build() {
    DiagnosticReport diagnosticReport = new DiagnosticReport();

    diagnosticReport.setSubject(new Reference(notifiedPerson));
    diagnosticReport.setIssued(issued);
    diagnosticReport.setStatus(status);
    diagnosticReport.setCode(new CodeableConcept(new Coding(codeSystem, codeCode, codeDisplay)));
    diagnosticReport.setId(laboratoryId);

    diagnosticReport.setConclusion(conclusion);
    diagnosticReport.setConclusionCode(
        singletonList(
            new CodeableConcept(
                new Coding(conclusionCodeSystem, conclusionCodeCode, conclusionCodeDisplay))));
    diagnosticReport.setResult(
        pathogenDetections.stream()
            .map(Reference::new)
            .collect(Collectors.toList())); // no java 17 because gateway is still java 11

    diagnosticReport.setMeta(new Meta().addProfile(metaProfileUrl));

    return diagnosticReport;
  }

  /**
   * @deprecated please use {@link #build()} instead.
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildLaboratoryReport(
      Patient notifiedPerson, Observation pathogenDetection) {
    return buildLaboratoryReport(notifiedPerson, singletonList(pathogenDetection));
  }

  /**
   * @deprecated please use {@link #build()} instead.
   * @param notifiedPerson
   * @param pathogenDetectionList
   * @return
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildLaboratoryReport(
      Patient notifiedPerson, List<Observation> pathogenDetectionList) {
    DiagnosticReport diagnosticReport = new DiagnosticReport();

    diagnosticReport.setId(laboratoryId);
    diagnosticReport.setStatus(status);

    CodeableConcept codeableConceptConclusionCode = new CodeableConcept();
    Coding codeConclusionCode =
        new Coding(conclusionCodeSystem, conclusionCodeCode, conclusionCodeDisplay);
    codeableConceptConclusionCode.addCoding(codeConclusionCode);
    diagnosticReport.addConclusionCode(codeableConceptConclusionCode);

    diagnosticReport.setSubject(new Reference(notifiedPerson));
    for (Observation pathogenDetection : pathogenDetectionList) {
      diagnosticReport.addResult(new Reference(pathogenDetection));
    }
    diagnosticReport.setIssued(issued);

    Coding coding = new Coding(codeSystem, codeCode, codeDisplay);
    diagnosticReport.setCode(new CodeableConcept().addCoding(coding));

    diagnosticReport.setMeta(new Meta().addProfile(metaProfileUrl));
    return diagnosticReport;
  }

  /**
   * @deprecated please use {@link #setDefaultData()} instead.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder addLaboratoryId() {
    return setLaboratoryId(generateUuidString());
  }

  /**
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildExampleCVDPLaboratoryReport(
      Patient notifiedPerson, Observation pathogenDetection) {
    return buildExampleCVDPLaboratoryReport(notifiedPerson, singletonList(pathogenDetection));
  }

  /**
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildExampleCVDPLaboratoryReport(
      Patient notifiedPerson, List<Observation> pathogenDetection) {
    laboratoryId = requireNonNullElse(laboratoryId, generateUuidString());
    status = requireNonNullElse(status, FINAL);
    setExampleCodeData();
    setExampleConclusionCodeData();
    issued = requireNonNullElse(issued, getCurrentDate());
    metaProfileUrl = DemisConstants.PROFILE_LABORATORY_REPORT_CVDP;
    return buildLaboratoryReport(notifiedPerson, pathogenDetection);
  }

  /**
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   * @return
   */
  @Deprecated(since = "1.2.1")
  private LaboratoryReportDataBuilder setExampleConclusionCodeData() {
    conclusionCodeSystem =
        requireNonNullElse(
            conclusionCodeSystem, HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE);
    conclusionCodeCode = requireNonNullElse(conclusionCodeCode, PATHOGEN_DETECTED);
    conclusionCodeDisplay =
        requireNonNullElse(conclusionCodeDisplay, "Meldepflichtiger Erreger nachgewiesen");
    return this;
  }

  /**
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   * @return
   */
  @Deprecated(since = "1.2.1")
  private LaboratoryReportDataBuilder setExampleCodeData() {
    codeSystem =
        requireNonNullElse(codeSystem, "https://demis.rki.de/fhir/CodeSystem/notificationCategory");
    codeCode = requireNonNullElse(codeCode, "cvdp");
    codeDisplay =
        requireNonNullElse(
            codeDisplay, "Severe-Acute-Respiratory-Syndrome-Coronavirus-2 (SARS-CoV-2)");
    return this;
  }

  /**
   * @deprecated use {@link #setConclusionCodeStatusToDetected()} instead.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder setConclusionCodeToNachgewiesen() {
    conclusionCodeCode = PATHOGEN_DETECTED;
    conclusionCodeDisplay = "Meldepflichtiger Erreger nachgewiesen";
    conclusionCodeSystem = HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE;
    return this;
  }

  /**
   * @deprecated use {@link #setConclusionCodeStatusToNotDetected()} instead.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder setConclusionCodeToNichtNachgewiesen() {
    conclusionCodeCode = "pathogenNotDetected";
    conclusionCodeDisplay = "Meldepflichtiger Erreger nicht nachgewiesen";
    conclusionCodeSystem = HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE;
    return this;
  }
}
