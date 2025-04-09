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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CONCLUSION_CODE_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_REPORT_BASE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.REASON_FOR_TESTING_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils.internalReference;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNullElse;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.AMENDED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.CORRECTED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

@Setter
public class LaboratoryReportDataBuilder {

  public static final String HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE =
      "https://demis.rki.de/fhir/CodeSystem/conclusionCode";
  public static final String PATHOGEN_DETECTED = "pathogenDetected";
  private static final String REASON_CODE_EXTENSION_URL =
      "http://hl7.org/fhir/StructureDefinition/workflow-reasonCode";

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
  @CheckForNull private Patient notifiedPerson;
  private String conclusion;
  private Collection<Observation> pathogenDetections = new ArrayList<>();
  private List<Reference> basedOnReferenceList = new ArrayList<>();
  private List<Extension> extensionList = new ArrayList<>();

  /** Copy the given diagnostic report and add the given subject. */
  public static DiagnosticReport deepCopy(
      @Nonnull final DiagnosticReport original,
      @Nonnull final Patient subject,
      @Nonnull final Collection<Observation> observations) {
    final LaboratoryReportDataBuilder result = new LaboratoryReportDataBuilder();
    result.setLaboratoryId(original.getId());

    final Coding codingFirstRep = original.getCode().getCodingFirstRep();
    result.setCodeCode(codingFirstRep.getCode());
    result.setCodeDisplay(codingFirstRep.getDisplay());
    result.setCodeSystem(codingFirstRep.getSystem());

    result.setStatus(original.getStatus());
    result.setIssued(original.getIssued());

    if (original.getConclusion() != null) {
      result.setConclusion(original.getConclusion());
    }
    final Coding conclusionCodeFirstRep = original.getConclusionCodeFirstRep().getCodingFirstRep();
    result.setConclusionCodeCode(conclusionCodeFirstRep.getCode());
    result.setConclusionCodeDisplay(conclusionCodeFirstRep.getDisplay());
    result.setConclusionCodeSystem(conclusionCodeFirstRep.getSystem());

    final List<CanonicalType> profiles = original.getMeta().getProfile();
    Objects.checkIndex(0, profiles.size());
    result.setMetaProfileUrl(profiles.getFirst().getValue());

    result.setNotifiedPerson(subject);
    result.setPathogenDetections(observations);

    result.setExtensionList(getExtensions(original));
    result.setBasedOnReferenceList(getBasedOnReferences(original));

    return result.build();
  }

  private static List<Extension> getExtensions(final DiagnosticReport original) {
    final Extension extension = original.getExtensionByUrl(REASON_CODE_EXTENSION_URL);
    if (extension == null) {
      return List.of();
    }

    return List.of(new Extension(extension.getUrl()).setValue(extension.getValue()));
  }

  private static List<Reference> getBasedOnReferences(final DiagnosticReport original) {
    if (original.getBasedOn().isEmpty()) {
      return List.of();
    }

    final Reference basedOnFirstRep = original.getBasedOnFirstRep();
    final Identifier oldIdentifier = basedOnFirstRep.getIdentifier();
    final Identifier value =
        new Identifier().setSystem(oldIdentifier.getSystem()).setValue(oldIdentifier.getValue());
    final Reference newBasedOn =
        new Reference().setType(basedOnFirstRep.getType()).setIdentifier(value);
    return List.of(newBasedOn);
  }

  /**
   * based on <a href="https://simplifier.net/rki.demis.laboratory/laboratoryreport">simplifier
   * laboratory report in rki.demis.laboratory</a>
   *
   * @param diagnosticReport
   * @return
   */
  public LaboratoryReportDataBuilder copyOf(final DiagnosticReport diagnosticReport) {

    // meta
    this.metaProfileUrl = diagnosticReport.getMeta().getProfile().getFirst().getValueAsString();

    // extension -> reason for testing
    Extension extensionByUrl = diagnosticReport.getExtensionByUrl(REASON_CODE_EXTENSION_URL);
    if (extensionByUrl != null) {
      extensionList.add(new Extension(extensionByUrl.getUrl()).setValue(extensionByUrl.getValue()));
    }
    //    identifier
    // is left out on purpose. field is not must support or mandatory

    // based on
    if (!diagnosticReport.getBasedOn().isEmpty()) {
      Reference basedOnFirstRep = diagnosticReport.getBasedOnFirstRep();
      Identifier oldIdentifier = basedOnFirstRep.getIdentifier();
      Identifier value =
          new Identifier().setSystem(oldIdentifier.getSystem()).setValue(oldIdentifier.getValue());
      Reference newBasedOn =
          new Reference().setType(basedOnFirstRep.getType()).setIdentifier(value);
      basedOnReferenceList.add(newBasedOn);
    }
    // status
    this.status = diagnosticReport.getStatus();
    // code
    Coding code = diagnosticReport.getCode().getCodingFirstRep();
    this.codeCode = code.getCode();
    this.codeSystem = code.getSystem();
    this.codeDisplay = code.getDisplay();
    // subject
    //    needs to be added later
    // issued
    issued = diagnosticReport.getIssued();
    // result
    //    needs to be added later
    // conclusion Text
    if (diagnosticReport.getConclusion() != null) {
      this.conclusion = diagnosticReport.getConclusion();
    }
    // conclusionCode
    Coding conclusionCode = diagnosticReport.getConclusionCode().getFirst().getCodingFirstRep();
    this.conclusionCodeCode = conclusionCode.getCode();
    this.conclusionCodeSystem = conclusionCode.getSystem();
    this.conclusionCodeDisplay = conclusionCode.getDisplay();
    return this;
  }

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

  public LaboratoryReportDataBuilder addBasedOn(String laboratoryOrderId) {
    Reference serviceRequest = new Reference();
    serviceRequest.setType("ServiceRequest");
    Identifier value = new Identifier();
    value.setSystem("https://demis.rki.de/fhir/NamingSystem/ServiceRequestId");
    value.setValue(laboratoryOrderId);
    serviceRequest.setIdentifier(value);
    basedOnReferenceList.add(serviceRequest);
    return this;
  }

  public LaboratoryReportDataBuilder setDefaultData() {
    codeSystem = CODE_SYSTEM_NOTIFICATION_CATEGORY;
    conclusionCodeSystem = CONCLUSION_CODE_SYSTEM;
    laboratoryId = generateUuidString();
    return this;
  }

  /**
   * Add the reason for testing to the laboratory report.
   *
   * @param reasonCode a SNOMED code
   * @param reasonDisplay a human readable display
   * @return this builder
   */
  public LaboratoryReportDataBuilder withReasonForTesting(
      final String reasonCode, final String reasonDisplay) {
    Extension reasonForTesting = new Extension();
    reasonForTesting.setUrl(REASON_FOR_TESTING_SYSTEM);
    reasonForTesting.setValue(
        new CodeableConcept(new Coding(SYSTEM_SNOMED, reasonCode, reasonDisplay)));
    extensionList.add(reasonForTesting);
    return this;
  }

  /**
   * creates a url and adds the given code
   *
   * @param pathogenCode the pathogen code, e.g. cvdp
   * @return this builder
   */
  public LaboratoryReportDataBuilder setProfileUrlHelper(String pathogenCode) {
    metaProfileUrl = LABORATORY_REPORT_BASE_URL + pathogenCode.toUpperCase();
    return this;
  }

  public DiagnosticReport build() {
    DiagnosticReport diagnosticReport = new DiagnosticReport();

    if (notifiedPerson != null) {
      diagnosticReport.setSubject(internalReference(notifiedPerson));
    }
    diagnosticReport.setIssued(issued);
    diagnosticReport.setStatus(status);
    diagnosticReport.setCode(new CodeableConcept(new Coding(codeSystem, codeCode, codeDisplay)));
    diagnosticReport.setId(laboratoryId);
    diagnosticReport.setBasedOn(basedOnReferenceList);
    diagnosticReport.setExtension(extensionList);
    diagnosticReport.setConclusion(conclusion);
    diagnosticReport.setConclusionCode(
        singletonList(
            new CodeableConcept(
                new Coding(conclusionCodeSystem, conclusionCodeCode, conclusionCodeDisplay))));
    diagnosticReport.setResult(
        pathogenDetections.stream().map(ReferenceUtils::internalReference).toList());

    diagnosticReport.setMeta(new Meta().addProfile(metaProfileUrl));

    return diagnosticReport;
  }

  /**
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   * @deprecated please use {@link #build()} instead.
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildLaboratoryReport(
      Patient notifiedPerson, Observation pathogenDetection) {
    return buildLaboratoryReport(notifiedPerson, singletonList(pathogenDetection));
  }

  /**
   * @param notifiedPerson
   * @param pathogenDetectionList
   * @return
   * @deprecated please use {@link #build()} instead.
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
   * @return
   * @deprecated please use {@link #setDefaultData()} instead.
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder addLaboratoryId() {
    return setLaboratoryId(generateUuidString());
  }

  /**
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   */
  @Deprecated(since = "1.2.1")
  public DiagnosticReport buildExampleCVDPLaboratoryReport(
      Patient notifiedPerson, Observation pathogenDetection) {
    return buildExampleCVDPLaboratoryReport(notifiedPerson, singletonList(pathogenDetection));
  }

  /**
   * @param notifiedPerson
   * @param pathogenDetection
   * @return
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
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
   * @return
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
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
   * @return
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
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
   * @return
   * @deprecated use {@link #setConclusionCodeStatusToDetected()} instead.
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder setConclusionCodeToNachgewiesen() {
    conclusionCodeCode = PATHOGEN_DETECTED;
    conclusionCodeDisplay = "Meldepflichtiger Erreger nachgewiesen";
    conclusionCodeSystem = HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE;
    return this;
  }

  /**
   * @return
   * @deprecated use {@link #setConclusionCodeStatusToNotDetected()} instead.
   */
  @Deprecated(since = "1.2.1")
  public LaboratoryReportDataBuilder setConclusionCodeToNichtNachgewiesen() {
    conclusionCodeCode = "pathogenNotDetected";
    conclusionCodeDisplay = "Meldepflichtiger Erreger nicht nachgewiesen";
    conclusionCodeSystem = HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_CONCLUSION_CODE;
    return this;
  }
}
