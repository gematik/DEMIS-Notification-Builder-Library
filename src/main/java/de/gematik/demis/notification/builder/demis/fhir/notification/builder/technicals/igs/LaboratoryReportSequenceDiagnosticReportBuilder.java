package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_CONCLUSION_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_OBSERVATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PATIENT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_LABORATORY_REPORT_SEQUENCE;
import static org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.FINAL;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

/**
 * Builder for the entry DiagnosticReport/LaboratoryReportSequence in an IGS
 * NotificationBundleSequence.
 */
@SuperBuilder
public class LaboratoryReportSequenceDiagnosticReportBuilder
    extends AbstractIgsResourceBuilder<DiagnosticReport> {

  public static final String PATOGEN_DETECTED = "pathogenDetected";
  private Optional<Patient> subjectReference;
  private Optional<Observation> resultReference;

  /**
   * Builds the FHIR object representing the entry DiagnosticReport/LaboratoryReportSequence.
   *
   * @return The FHIR object representing the entry DiagnosticReport/LaboratoryReportSequence.
   */
  @Override
  public Optional<DiagnosticReport> buildResource() {
    DiagnosticReport diagnosticReport = new DiagnosticReport();

    diagnosticReport.setId(UUID.randomUUID().toString());

    diagnosticReport.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_LABORATORY_REPORT_SEQUENCE).initialize().build());

    diagnosticReport.setStatus(FINAL);
    diagnosticReport.setCode(
        IgsBuilderUtils.generateCodeableConcept(
            CODE_SYSTEM_NOTIFICATION_CATEGORY, data.getMeldetatbestand(), null));

    diagnosticReport.setConclusionCode(
        List.of(
            new CodeableConcept()
                .addCoding(
                    new Coding()
                        .setSystem(CODE_SYSTEM_CONCLUSION_CODE)
                        .setCode(PATOGEN_DETECTED))));

    subjectReference.ifPresent(
        resource -> diagnosticReport.setSubject(new Reference(PREFIX_PATIENT + resource.getId())));

    diagnosticReport.setIssued(new Date());

    resultReference.ifPresent(
        resource ->
            diagnosticReport.setResult(
                List.of(new Reference(PREFIX_OBSERVATION + resource.getId()))));

    return Optional.of(diagnosticReport);
  }
}
