package de.gematik.demis.notification.builder.demis.fhir.testUtils;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;

public class TestObjects {

  public static Date mockDate() {
    LocalDateTime ldt = LocalDateTime.of(2025, 9, 10, 14, 23, 28);
    return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Coding sectionCoding() {
    return new Coding()
        .setDisplay("Laboratory report")
        .setCode("11502-2")
        .setSystem("http://loinc.org");
  }

  public static Composition composition() {
    Composition composition = new Composition();
    composition.setId("compositionId");
    return composition;
  }

  public static Composition composition(
      final DiagnosticReport section, final Patient subject, final PractitionerRole author) {
    Composition composition = new Composition();
    composition.setMeta(new Meta().addProfile("anything"));
    composition.setId("compositionId");
    composition.setAuthor(List.of(new Reference(author)));
    composition.setSubject(new Reference(subject));
    composition.setSection(
        List.of(new Composition.SectionComponent().setEntry(List.of(new Reference(section)))));
    return composition;
  }

  public static Patient notifiedPerson() {
    Patient patient = new Patient();
    patient.setId("patientId");
    return patient;
  }

  public static PractitionerRole notifier() {
    PractitionerRole practitionerRole = new PractitionerRole();
    practitionerRole.setId("notifierRoleId");
    Practitioner practitioner = new Practitioner();
    practitioner.setId("notifierId");
    practitionerRole.setPractitioner(new Reference(practitioner));
    return practitionerRole;
  }

  public static PractitionerRole submitter() {
    final Practitioner practitioner = new Practitioner();
    practitioner.setId("submitterId");

    return new PractitionerRoleBuilder()
        .setDefaults()
        .asSubmittingRole()
        .setId("submitterRoleId")
        .withPractitioner(practitioner)
        .build();
  }

  public static Specimen specimen() {
    Specimen specimen = new Specimen();
    specimen.setMeta(new Meta().addProfile(DemisConstants.SPECIMEN_BASE_URL));
    specimen.setId("specimenId");
    return specimen;
  }

  public static Observation pathogenDetection() {
    Observation observation = new Observation();
    observation.setMeta(new Meta().addProfile(DemisConstants.OBSERVATION_BASE));
    observation.setId("pathogenDetectionId");
    return observation;
  }

  public static Observation pathogenDetection(final Specimen specimen) {
    return pathogenDetection().setSpecimen(new Reference(specimen));
  }

  public static DiagnosticReport laboratoryReport() {
    DiagnosticReport diagnosticReport = new DiagnosticReport();
    diagnosticReport.setId("laboratoryReportId");
    return diagnosticReport;
  }

  public static DiagnosticReport laboratoryReport(final Observation observation) {
    DiagnosticReport diagnosticReport = new DiagnosticReport();
    diagnosticReport.setId("laboratoryReportId");
    diagnosticReport.setMeta(new Meta().addProfile("anything"));
    diagnosticReport.addResult(new Reference(observation));
    return diagnosticReport;
  }
}
