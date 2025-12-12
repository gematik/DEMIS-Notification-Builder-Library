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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CONCLUSION_CODE_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_REPORT_BASE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.REASON_FOR_TESTING_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils.internalReference;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Collections.singletonList;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.AMENDED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.CORRECTED;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;

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
  private String codeVersion;
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
    result
        .setCodeCode(codingFirstRep.getCode())
        .setCodeDisplay(codingFirstRep.getDisplay())
        .setCodeSystem(codingFirstRep.getSystem())
        .setCodeVersion(codingFirstRep.getVersion());

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
    issued = new Date();
    return this;
  }

  protected String getDefaultProfileUrl() {
    return LABORATORY_REPORT_BASE_URL;
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
    metaProfileUrl = getDefaultProfileUrl() + pathogenCode.toUpperCase();
    return this;
  }

  public DiagnosticReport build() {
    DiagnosticReport diagnosticReport = new DiagnosticReport();

    if (notifiedPerson != null) {
      diagnosticReport.setSubject(internalReference(notifiedPerson));
    }
    diagnosticReport.setIssued(issued);
    diagnosticReport.setStatus(status);
    Coding notificationCategoryCode = new Coding(codeSystem, codeCode, codeDisplay);
    if (codeVersion != null) {
      notificationCategoryCode.setVersion(codeVersion);
    }
    diagnosticReport.setCode(new CodeableConcept(notificationCategoryCode));
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

  public LaboratoryReportDataBuilder addPathogenDetection(Observation pathogenDetection) {
    if (pathogenDetections == null) {
      pathogenDetections = new ArrayList<>();
    }
    pathogenDetections.add(pathogenDetection);
    return this;
  }
}
