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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsConstants.LOINC_VERSION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsConstants.SNOMED_VERSION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.RECEIVED_DATE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.REPOSITORY_LINK;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.SAMPLING_DATE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.SEQUENCING_DATE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.getIgsTestOverviewData;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_OBSERVATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_OBSERVATION_INTERPRETATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ORGANIZATION_TYPE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_PLATFORM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_STRATEGY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_SUBSTANCES;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DEVICE_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DOCUMENT_REFERENCE_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LOINC_ORG_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.MOLECULAR_SEQUENCE_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_LABORATORY_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.ORGANIZATION_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PARTICIPANT_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PRACTITIONER_ROLE_BASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_DEVICE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_DIAGNOSTIC_REPORT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_MOLECULAR_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_OBSERVATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_ORGANIZATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PATIENT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PRACTITIONER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_SPECIMEN;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_ADAPTER_SUBSTANCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_MOLECULAR_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_ANONYMOUS;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SEQUENCING_DEVICE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hl7.fhir.r4.model.MolecularSequence.RepositoryType.LOGIN;
import static org.junit.jupiter.api.Assertions.assertAll;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.VersionInfos;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Substance;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.codesystems.SequenceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ProcessNotificationSequenceRequestParametersBuilderTest {

  private static final String REGULAR_EXPRESSION_UUID_SUFFIX =
      "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
  private static final String REGULAR_EXPRESSION_UUID_EXACTLY =
      "^" + REGULAR_EXPRESSION_UUID_SUFFIX;
  private static final boolean WITH_REPOSITORY = true;
  private static final boolean WITH_PRIME_DIAGNOSTIC_LAB = true;

  @ParameterizedTest
  @CsvSource({
    WITH_REPOSITORY + "," + WITH_PRIME_DIAGNOSTIC_LAB,
    !WITH_REPOSITORY + "," + WITH_PRIME_DIAGNOSTIC_LAB,
    WITH_REPOSITORY + "," + !WITH_PRIME_DIAGNOSTIC_LAB
  })
  void shouldBuildProcessNotificationSequenceRequestParameters(
      boolean withRepository, boolean withPrimeDiagnosticLab) {
    VersionInfos versionInfos = new VersionInfos(LOINC_VERSION, SNOMED_VERSION);
    ProcessNotificationSequenceRequestParametersBuilder builder =
        new ProcessNotificationSequenceRequestParametersBuilder(
            getIgsTestOverviewData(withRepository, withPrimeDiagnosticLab), versionInfos);
    Bundle bundle = builder.build();

    assertBundle(bundle, withRepository, withPrimeDiagnosticLab);
  }

  private void assertBundle(Bundle bundle, boolean withRepository, boolean withPrimeDiagnosticLab) {
    assertMeta(bundle);
    Identifier identifier = bundle.getIdentifier();
    assertThat(identifier).isNotNull();
    assertThat(identifier.getSystem())
        .isNotNull()
        .isEqualTo(DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    assertThat(identifier.getValue()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);
    assertThat(bundle.getType()).isNotNull().isEqualTo(Bundle.BundleType.DOCUMENT);
    assertNowInRange(bundle.getTimestamp());
    assertThat(bundle.getResourceType()).isNotNull();
    assertThat(bundle.getResourceType().name()).isNotNull().isEqualTo("Bundle");

    if (withPrimeDiagnosticLab) {
      assertEntries(bundle.getEntry(), withRepository);
    } else {
      assertEntriesWithoutPrimeDiagnosticLab(bundle.getEntry());
    }
  }

  private void assertMeta(Bundle bundle) {
    Meta meta = bundle.getMeta();
    List<CanonicalType> profiles = meta.getProfile();
    assertThat(profiles).isNotNull().hasSize(1);
    assertThat(profiles.getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_IGS_NOTIFICATION_BUNDLE_SEQUENCE);
    assertNowInRange(meta.getLastUpdated());
  }

  private void assertNowInRange(Date date) {
    assertThat(date).isNotNull().isCloseTo(new Date(), 10000);
  }

  private void assertEntries(List<BundleEntryComponent> entries, boolean withRepository) {
    assertThat(entries).isNotNull().hasSize(12);
    assertCompositionNotificationId(entries.get(0));
    assertPatient(entries.get(1));
    assertNotifierRole(entries.get(2));
    assertDemisLaboratoryIdOrganization(entries.get(3));
    assertSubmittingRole(entries.get(4));
    assertSubmittingFacilityOrganization(entries.get(5));
    assertLaboratoryReportSequenceDiagnosticReport(entries.get(6));
    assertSequencingDevice(entries.get(7));
    assertSpecimenSequence(entries.get(8), WITH_PRIME_DIAGNOSTIC_LAB);
    assertAdapterSubstance(entries.get(9));
    assertPathogenDetectionSequenceObservation(entries.get(10));
    assertMolecularSequence(entries.get(11), withRepository);
  }

  private void assertEntriesWithoutPrimeDiagnosticLab(List<BundleEntryComponent> entries) {
    assertThat(entries).isNotNull().hasSize(10);
    assertCompositionNotificationId(entries.get(0));
    assertPatient(entries.get(1));
    assertNotifierRole(entries.get(2));
    assertDemisLaboratoryIdOrganization(entries.get(3));
    assertLaboratoryReportSequenceDiagnosticReport(entries.get(4));
    assertSequencingDevice(entries.get(5));
    assertSpecimenSequence(entries.get(6), !WITH_PRIME_DIAGNOSTIC_LAB);
    assertAdapterSubstance(entries.get(7));
    assertPathogenDetectionSequenceObservation(entries.get(8));
    assertMolecularSequence(entries.get(9), true);
  }

  private void assertAdapterSubstance(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DemisConstants.SUBSTANCE_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);
    String id = extractIdFromFullUrl(entry.getFullUrl());

    Substance substance = (Substance) entry.getResource();
    Meta meta = substance.getMeta();
    assertAll(
        () -> assertThat(entry.getResource()).isNotNull().isInstanceOf(Substance.class),
        () -> assertThat(substance.getId()).isEqualTo(id),
        () -> assertThat(substance.getDescription()).isEqualTo("TAGCGAGT"),
        () -> assertThat(meta.getProfile()).isNotNull().hasSize(1),
        () ->
            assertThat(meta.getProfile().getFirst().getValue())
                .isEqualTo(PROFILE_ADAPTER_SUBSTANCE),
        () -> assertThat(substance.getCode()).isNotNull(),
        () -> assertThat(substance.getCode().getCoding()).isNotNull().hasSize(1),
        () ->
            assertThat(substance.getCode().getCoding().getFirst().getSystem())
                .isNotNull()
                .isEqualTo(CODE_SYSTEM_SEQUENCING_SUBSTANCES),
        () ->
            assertThat(substance.getCode().getCoding().getFirst().getCode())
                .isNotNull()
                .isEqualTo("adapter"),
        () ->
            assertThat(substance.getCode().getCoding().getFirst().getDisplay())
                .isNotNull()
                .isEqualTo("Adapter"));
  }

  private void assertCompositionNotificationId(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DemisConstants.COMPOSITION_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);
    String id = extractIdFromFullUrl(entry.getFullUrl());

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Composition.class);
    Composition composition = (Composition) entry.getResource();

    assertThat(composition.getIdentifier()).isNotNull();
    assertThat(composition.getIdentifier().getSystem())
        .isNotNull()
        .isEqualTo(NAMING_SYSTEM_NOTIFICATION_ID);
    assertThat(composition.getIdentifier().getValue())
        .isNotNull()
        .matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(composition.getMeta()).isNotNull();
    Meta meta = composition.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_NOTIFICATION_SEQUENCE);

    assertThat(composition.getRelatesTo()).isNotNull().hasSize(1);
    Composition.CompositionRelatesToComponent relatesTo = composition.getRelatesTo().getFirst();
    Reference reference = (Reference) relatesTo.getTarget();
    assertThat(reference.getIdentifier()).isNotNull();
    assertThat(reference.getIdentifier().getSystem())
        .isNotNull()
        .isEqualTo(NAMING_SYSTEM_NOTIFICATION_ID);
    assertThat(reference.getIdentifier().getValue()).matches(REGULAR_EXPRESSION_UUID_EXACTLY);
    assertThat(reference.getType()).isNotNull().isEqualTo("Composition");
    assertThat(relatesTo.getCode())
        .isNotNull()
        .isEqualTo(Composition.DocumentRelationshipType.APPENDS);

    assertThat(composition.getSubject()).isNotNull();
    assertThat(composition.getSubject().getReference())
        .isNotNull()
        .matches(PREFIX_PATIENT + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(composition.getCategory()).isNotNull().hasSize(1);
    CodeableConcept category = composition.getCategory().getFirst();
    assertThat(category.getCoding()).isNotNull().hasSize(1);
    Coding categoryCoding = category.getCoding().getFirst();
    assertThat(categoryCoding.getSystem()).isNotNull().isEqualTo(LOINC_ORG_SYSTEM);
    assertThat(categoryCoding.getVersion()).isNotNull().isEqualTo(LOINC_VERSION);
    assertThat(categoryCoding.getDisplay()).isNotNull().isEqualTo("Laboratory report");
    assertThat(categoryCoding.getCode()).isNotNull().isEqualTo("11502-2");

    assertThat(composition.getId()).isEqualTo(id);
    assertThat(composition.getTitle()).isNotNull().isEqualTo("Sequenzmeldung");
    assertNowInRange(composition.getDate());

    assertThat(composition.getSection()).isNotNull().hasSize(1);
    Composition.SectionComponent section = composition.getSection().getFirst();
    assertThat(section.getEntry()).isNotNull().hasSize(1);
    Reference sectionEntry = section.getEntry().getFirst();
    assertThat(sectionEntry.getReference())
        .isNotNull()
        .matches(PREFIX_DIAGNOSTIC_REPORT + REGULAR_EXPRESSION_UUID_SUFFIX);
    assertThat(section.getCode()).isNotNull();
    assertThat(section.getCode().getCoding()).isNotNull().hasSize(1);
    Coding sectionCoding = section.getCode().getCoding().getFirst();
    assertThat(sectionCoding.getSystem()).isNotNull().isEqualTo(LOINC_ORG_SYSTEM);
    assertThat(sectionCoding.getVersion()).isNotNull().isEqualTo(LOINC_VERSION);
    assertThat(sectionCoding.getDisplay()).isNotNull().isEqualTo("Laboratory report");
    assertThat(sectionCoding.getCode()).isNotNull().isEqualTo("11502-2");

    assertThat(composition.getStatus()).isNotNull().isEqualTo(Composition.CompositionStatus.FINAL);

    assertThat(composition.getType()).isNotNull();
    assertThat(composition.getType().getCoding()).isNotNull().hasSize(1);
    Coding typeCoding = composition.getType().getCoding().getFirst();
    assertThat(typeCoding.getSystem()).isNotNull().isEqualTo(LOINC_ORG_SYSTEM);
    assertThat(typeCoding.getVersion()).isNotNull().isEqualTo(LOINC_VERSION);
    assertThat(typeCoding.getDisplay()).isNotNull().isEqualTo("Infectious disease Note");
    assertThat(typeCoding.getCode()).isNotNull().isEqualTo("34782-3");

    assertThat(composition.getAuthor()).isNotNull().hasSize(1);
    Reference author = composition.getAuthor().getFirst();
    assertThat(author.getReference())
        .isNotNull()
        .matches(PREFIX_PRACTITIONER_ROLE + REGULAR_EXPRESSION_UUID_SUFFIX);
  }

  private void assertPatient(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(PARTICIPANT_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);
    assertThat(entry.getResource()).isNotNull().isInstanceOf(Patient.class);
    Patient patient = (Patient) entry.getResource();

    assertThat(patient.getMeta()).isNotNull();
    assertThat(patient.getMeta().getProfile()).isNotNull().hasSize(1);
    CanonicalType metaProfile = patient.getMeta().getProfile().getFirst();
    assertThat(metaProfile.getValue()).isNotNull().isEqualTo(PROFILE_NOTIFIED_PERSON_ANONYMOUS);

    assertThat(patient.getId()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(patient.getAddress()).isNotNull().hasSize(1);
    Address address = patient.getAddress().getFirst();
    assertThat(address.getExtension()).isNotNull().hasSize(1);
    Extension extension = address.getExtension().getFirst();
    assertThat(extension.getUrl())
        .isNotNull()
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/AddressUse");
    assertThat(extension.getValue()).isNotNull().isInstanceOf(Coding.class);
    Coding valueCoding = (Coding) extension.getValue();
    assertThat(valueCoding.getSystem())
        .isNotNull()
        .isEqualTo("https://demis.rki.de/fhir/CodeSystem/addressUse");
    assertThat(valueCoding.getCode()).isNotNull().isEqualTo("primary");
    assertThat(address.getPostalCode()).isNotNull().isEqualTo("104");
  }

  private void assertNotifierRole(BundleEntryComponent entry) {
    assertPractitionerRole(entry, DemisConstants.PROFILE_NOTIFIER_ROLE);
  }

  private void assertPractitionerRole(BundleEntryComponent entry, String metaProfileString) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(PRACTITIONER_ROLE_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(PractitionerRole.class);
    PractitionerRole practitionerRole = (PractitionerRole) entry.getResource();

    assertThat(practitionerRole.getMeta()).isNotNull();
    assertThat(practitionerRole.getMeta().getProfile()).isNotNull().hasSize(1);
    CanonicalType metaProfile = practitionerRole.getMeta().getProfile().getFirst();
    assertThat(metaProfile.getValue()).isNotNull().isEqualTo(metaProfileString);

    assertThat(practitionerRole.getId()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(practitionerRole.getOrganization()).isNotNull();
    String organizationReference = practitionerRole.getOrganization().getReference();
    assertThat(organizationReference)
        .isNotNull()
        .matches("Organization/" + REGULAR_EXPRESSION_UUID_SUFFIX);
  }

  private void assertSubmittingRole(BundleEntryComponent entry) {
    assertPractitionerRole(entry, DemisConstants.PROFILE_SUBMITTING_ROLE);
  }

  private void assertLaboratoryReportSequenceDiagnosticReport(Bundle.BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DemisConstants.DIAGNOSTIC_REPORT_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);
    String id = extractIdFromFullUrl(entry.getFullUrl());

    assertThat(entry.getResource()).isNotNull().isInstanceOf(DiagnosticReport.class);
    DiagnosticReport diagnosticReport = (DiagnosticReport) entry.getResource();

    assertThat(diagnosticReport.getId()).isEqualTo(id);

    assertThat(diagnosticReport.getMeta()).isNotNull();
    Meta meta = diagnosticReport.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_LABORATORY_REPORT_SEQUENCE);

    assertThat(diagnosticReport.getStatus())
        .isNotNull()
        .isEqualTo(DiagnosticReport.DiagnosticReportStatus.FINAL);

    assertThat(diagnosticReport.getCode()).isNotNull();
    assertThat(diagnosticReport.getCode().getCoding()).isNotNull().hasSize(1);
    Coding codeCoding = diagnosticReport.getCode().getCoding().getFirst();
    assertThat(codeCoding.getSystem())
        .isNotNull()
        .isEqualTo(DemisConstants.CODE_SYSTEM_NOTIFICATION_CATEGORY);
    assertThat(codeCoding.getCode()).isNotNull().isEqualTo("cvdp");

    assertThat(diagnosticReport.getSubject()).isNotNull();
    assertThat(diagnosticReport.getSubject().getReference())
        .isNotNull()
        .matches(PREFIX_PATIENT + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertNowInRange(diagnosticReport.getIssued());

    assertThat(diagnosticReport.getResult()).isNotNull();
    assertThat(diagnosticReport.getResult().getFirst().getReference())
        .isNotNull()
        .matches(PREFIX_OBSERVATION + REGULAR_EXPRESSION_UUID_SUFFIX);
  }

  private void assertDemisLaboratoryIdOrganization(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(ORGANIZATION_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Organization.class);
    Organization demisLaboratoryId = (Organization) entry.getResource();
    assertThat(demisLaboratoryId.getId()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(demisLaboratoryId.getMeta()).isNotNull();
    Meta meta = demisLaboratoryId.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isNotNull()
        .isEqualTo(PROFILE_NOTIFIER_FACILITY);

    assertThat(demisLaboratoryId.getIdentifier()).isNotNull().hasSize(1);
    Identifier identifier = demisLaboratoryId.getIdentifier().getFirst();
    assertThat(identifier.getSystem()).isNotNull().isEqualTo(NAMING_SYSTEM_LABORATORY_ID);
    assertThat(identifier.getValue()).isNotNull().isEqualTo("10285");

    assertThat(demisLaboratoryId.getType()).isNotNull().hasSize(1);
    CodeableConcept typeCoding = demisLaboratoryId.getType().getFirst();
    assertThat(typeCoding.getCoding()).isNotNull().hasSize(1);
    Coding coding = typeCoding.getCoding().getFirst();
    assertThat(coding.getSystem()).isNotNull().isEqualTo(CODE_SYSTEM_ORGANIZATION_TYPE);
    assertThat(coding.getCode()).isNotNull().isEqualTo("refLab");
    assertThat(coding.getDisplay()).isNotNull().isEqualTo("Einrichtung der Spezialdiagnostik");

    assertThat(demisLaboratoryId.getName())
        .isNotNull()
        .isEqualTo("Nationales Referenzzentrum für Influenza, FG17, RKI");

    assertThat(demisLaboratoryId.getTelecom()).isNotNull().hasSize(1);
    ContactPoint telecom = demisLaboratoryId.getTelecom().getFirst();
    assertThat(telecom.getSystem()).isNotNull().isEqualTo(ContactPoint.ContactPointSystem.EMAIL);
    assertThat(telecom.getValue()).isNotNull().isEqualTo("NRZ-Influenza@rki.de");
    assertThat(telecom.getUse()).isNotNull().isEqualTo(ContactPoint.ContactPointUse.WORK);

    assertThat(demisLaboratoryId.getAddress()).isNotNull().hasSize(1);
    Address address = demisLaboratoryId.getAddress().getFirst();
    assertThat(address.getLine()).isNotNull().hasSize(1);
    assertThat(address.getLine().getFirst().getValue()).isNotNull().isEqualTo("Seestr. 10");
    assertThat(address.getCity()).isNotNull().isEqualTo("Berlin");
    assertThat(address.getPostalCode()).isNotNull().isEqualTo("13353");
    assertThat(address.getCountry()).isNotNull().isEqualTo("DE");
  }

  private void assertSubmittingFacilityOrganization(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(ORGANIZATION_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Organization.class);
    Organization submittingFacility = (Organization) entry.getResource();
    assertThat(submittingFacility.getId()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(submittingFacility.getMeta()).isNotNull();
    assertThat(submittingFacility.getMeta().getProfile()).isNotNull().hasSize(1);
    CanonicalType metaProfile = submittingFacility.getMeta().getProfile().getFirst();
    assertThat(metaProfile.getValue())
        .isNotNull()
        .isEqualTo(DemisConstants.PROFILE_SUBMITTING_FACILITY);

    assertThat(submittingFacility.getIdentifier()).isNotNull().hasSize(1);
    Identifier identifier = submittingFacility.getIdentifier().getFirst();
    assertThat(identifier.getSystem()).isNotNull().isEqualTo(DemisConstants.NAMING_SYSTEM_BSNR);
    assertThat(identifier.getValue()).isNotNull().isEqualTo("987654321");

    assertThat(submittingFacility.getName()).isNotNull().isEqualTo("Primärlabor");

    assertSubmittingFacilityTelecoms(submittingFacility.getTelecom());

    assertThat(submittingFacility.getAddress()).isNotNull().hasSize(1);
    Address address = submittingFacility.getAddress().getFirst();
    assertThat(address.getLine()).isNotNull().hasSize(1);
    assertThat(address.getLine().getFirst().getValue()).isNotNull().isEqualTo("Dingsweg 321");
    assertThat(address.getCity()).isNotNull().isEqualTo("Berlin");
    assertThat(address.getPostalCode()).isNotNull().isEqualTo("13055");
    assertThat(address.getCountry()).isNotNull().isEqualTo("DE");
  }

  private void assertSubmittingFacilityTelecoms(List<ContactPoint> telecoms) {
    assertThat(telecoms).isNotNull().hasSize(1);

    ContactPoint telecom = telecoms.getFirst();
    assertThat(telecom.getSystem()).isNotNull().isEqualTo(ContactPoint.ContactPointSystem.EMAIL);
    assertThat(telecom.getValue()).isNotNull().isEqualTo("ifsg@primaerlabor-gibt-es-nicht.de");
    assertThat(telecom.getUse()).isNotNull().isEqualTo(ContactPoint.ContactPointUse.WORK);
  }

  private void assertSequencingDevice(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DEVICE_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Device.class);
    Device device = (Device) entry.getResource();
    assertThat(device.getId()).isNotNull().matches(REGULAR_EXPRESSION_UUID_EXACTLY);

    assertThat(device.getMeta()).isNotNull();
    Meta meta = device.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isNotNull()
        .isEqualTo(PROFILE_SEQUENCING_DEVICE);

    assertThat(device.getDeviceName()).isNotNull().hasSize(1);
    Device.DeviceDeviceNameComponent deviceNameComponent = device.getDeviceName().getFirst();
    assertThat(deviceNameComponent.getName()).isEqualTo("GridION");
    assertThat(deviceNameComponent.getType()).isEqualTo(Device.DeviceNameType.MODELNAME);

    assertThat(device.getType()).isNotNull();
    CodeableConcept typeCoding = device.getType();
    assertThat(typeCoding.getCoding()).isNotNull().hasSize(1);
    Coding coding = typeCoding.getCoding().getFirst();
    assertThat(coding.getSystem()).isNotNull().isEqualTo(CODE_SYSTEM_SEQUENCING_PLATFORM);
    assertThat(coding.getCode()).isNotNull().isEqualTo("oxford_nanopore");
  }

  private void assertSpecimenSequence(
      BundleEntryComponent entry, boolean withPrimaryDiagnosticLab) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DemisConstants.SPECIMEN_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Specimen.class);
    Specimen specimen = (Specimen) entry.getResource();
    String id = extractIdFromFullUrl(entry.getFullUrl());
    assertThat(specimen.getId()).isEqualTo(id);

    assertThat(specimen.getMeta()).isNotNull();
    Meta meta = specimen.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_SPECIMEN_SEQUENCE);

    assertThat(specimen.getExtension()).isNotNull().hasSize(1);
    Extension extension = specimen.getExtension().get(0);
    assertThat(extension.getUrl())
        .isNotNull()
        .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/Isolate");
    assertThat(extension.getValue()).isNotNull().isInstanceOf(StringType.class);
    assertThat(((StringType) extension.getValue()).getValue()).isEqualTo("119334006");

    assertThat(specimen.getStatus()).isNotNull().isEqualTo(Specimen.SpecimenStatus.AVAILABLE);

    assertThat(specimen.getType()).isNotNull();
    assertThat(specimen.getType().getCoding()).isNotNull().hasSize(1);
    Coding typeCoding = specimen.getType().getCoding().getFirst();
    assertThat(typeCoding.getSystem()).isNotNull().isEqualTo(SYSTEM_SNOMED);
    assertThat(typeCoding.getVersion()).isNotNull().isEqualTo(SNOMED_VERSION);
    assertThat(typeCoding.getCode()).isNotNull().isEqualTo("258604001");
    assertThat(typeCoding.getDisplay())
        .isNotNull()
        .isEqualTo("Upper respiratory swab specimen (specimen)");

    assertThat(specimen.getSubject()).isNotNull();
    assertThat(specimen.getSubject().getReference())
        .isNotNull()
        .matches(PREFIX_PATIENT + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(specimen.getReceivedTime()).isEqualTo(RECEIVED_DATE);

    assertThat(specimen.getCollection()).isNotNull();
    if (withPrimaryDiagnosticLab) {
      assertThat(specimen.getCollection().getCollector()).isNotNull();
      assertThat(specimen.getCollection().getCollector().getReference())
          .isNotNull()
          .matches(PREFIX_PRACTITIONER_ROLE + REGULAR_EXPRESSION_UUID_SUFFIX);
    } else {
      assertThat(specimen.getCollection().getCollector().getReference()).isNull();
    }
    assertThat(specimen.getCollection().getCollected()).isNotNull();
    assertThat(specimen.getCollection().getCollectedDateTimeType().getValue())
        .isEqualTo(SAMPLING_DATE);

    assertThat(specimen.getProcessing()).isNotNull().hasSize(1);
    Specimen.SpecimenProcessingComponent processing = specimen.getProcessing().getFirst();
    assertThat(processing.getDescription()).isNotNull().isEqualTo("ARTICv4");
    assertThat(processing.getProcedure()).isNotNull();
    assertThat(processing.getProcedure().getCoding()).isNotNull().hasSize(1);
    Coding procedureCoding = processing.getProcedure().getCoding().getFirst();
    assertThat(procedureCoding.getSystem()).isNotNull().isEqualTo(CODE_SYSTEM_SEQUENCING_STRATEGY);
    assertThat(procedureCoding.getCode()).isNotNull().isEqualTo("amplicon");
    assertThat(procedureCoding.getDisplay()).isNull();
    assertThat(processing.getTimeDateTimeType().getValue()).isEqualTo(SEQUENCING_DATE);
  }

  private void assertPathogenDetectionSequenceObservation(BundleEntryComponent entry) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(DemisConstants.OBSERVATION_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(Observation.class);
    Observation observation = (Observation) entry.getResource();
    String id = extractIdFromFullUrl(entry.getFullUrl());
    assertThat(observation.getId()).isEqualTo(id);

    assertThat(observation.getMeta()).isNotNull();
    Meta meta = observation.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_PATHOGEN_DETECTION_SEQUENCE);

    assertThat(observation.getStatus()).isNotNull().isEqualTo(Observation.ObservationStatus.FINAL);

    assertThat(observation.getCategory()).isNotNull().hasSize(1);
    Coding categoryCoding = observation.getCategory().getFirst().getCoding().getFirst();
    assertThat(categoryCoding.getSystem()).isNotNull().isEqualTo(CODE_SYSTEM_OBSERVATION_CATEGORY);
    assertThat(categoryCoding.getCode()).isNotNull().isEqualTo("laboratory");
    assertThat(categoryCoding.getDisplay()).isNull();

    assertThat(observation.getCode()).isNotNull();
    Coding codeCoding = observation.getCode().getCoding().getFirst();
    assertThat(codeCoding.getSystem()).isNotNull().isEqualTo(LOINC_ORG_SYSTEM);
    assertThat(codeCoding.getVersion()).isNotNull().isEqualTo(LOINC_VERSION);
    assertThat(codeCoding.getCode()).isNotNull().isEqualTo("41852-5");

    assertThat(observation.getSubject()).isNotNull();
    assertThat(observation.getSubject().getReference())
        .isNotNull()
        .matches(PREFIX_PATIENT + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(observation.getValue()).isNotNull().isInstanceOf(CodeableConcept.class);
    CodeableConcept codeableConcept = (CodeableConcept) observation.getValue();
    assertThat(codeableConcept.getCoding()).isNotNull().hasSize(1);
    Coding valueCoding = codeableConcept.getCoding().getFirst();
    assertThat(valueCoding.getSystem()).isNotNull().isEqualTo(SYSTEM_SNOMED);
    assertThat(valueCoding.getVersion()).isNotNull().isEqualTo(SNOMED_VERSION);
    assertThat(valueCoding.getCode()).isNotNull().isEqualTo("96741-4");
    assertThat(valueCoding.getDisplay())
        .isNotNull()
        .isEqualTo("Severe acute respiratory syndrome coronavirus 2 (organism)");

    assertThat(observation.getInterpretation()).isNotNull().hasSize(1);
    Coding interpretationCoding = observation.getInterpretation().getFirst().getCoding().getFirst();
    assertThat(interpretationCoding.getSystem())
        .isNotNull()
        .isEqualTo(CODE_SYSTEM_OBSERVATION_INTERPRETATION);
    assertThat(interpretationCoding.getCode()).isNotNull().isEqualTo("POS");
    assertThat(interpretationCoding.getDisplay()).isNull();

    assertThat(observation.getMethod()).isNotNull();
    Coding methodCoding = observation.getMethod().getCoding().getFirst();
    assertThat(methodCoding.getSystem()).isNotNull().isEqualTo(SYSTEM_SNOMED);
    assertThat(methodCoding.getVersion()).isNotNull().isEqualTo(SNOMED_VERSION);
    assertThat(methodCoding.getCode()).isNotNull().isEqualTo("117040002");
    assertThat(methodCoding.getDisplay())
        .isNotNull()
        .isEqualTo("Nucleic acid sequencing (procedure)");

    assertThat(observation.getSpecimen()).isNotNull();
    assertThat(observation.getSpecimen().getReference())
        .isNotNull()
        .matches(PREFIX_SPECIMEN + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(observation.getDevice()).isNotNull();
    assertThat(observation.getDevice().getReference())
        .isNotNull()
        .matches(PREFIX_DEVICE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(observation.getDerivedFrom()).isNotNull().hasSize(1);
    assertThat(observation.getDerivedFrom().getFirst().getReference())
        .isNotNull()
        .matches(PREFIX_MOLECULAR_SEQUENCE + REGULAR_EXPRESSION_UUID_SUFFIX);
  }

  private void assertMolecularSequence(BundleEntryComponent entry, boolean withRepository) {
    assertThat(entry).isNotNull();
    assertThat(entry.getFullUrl())
        .isNotNull()
        .matches(MOLECULAR_SEQUENCE_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertThat(entry.getResource()).isNotNull().isInstanceOf(MolecularSequence.class);
    MolecularSequence molecularSequence = (MolecularSequence) entry.getResource();
    String id = extractIdFromFullUrl(entry.getFullUrl());
    assertThat(molecularSequence.getId()).isEqualTo(id);

    assertThat(molecularSequence.getMeta()).isNotNull();
    Meta meta = molecularSequence.getMeta();
    assertThat(meta.getProfile()).isNotNull().hasSize(1);
    assertThat(meta.getProfile().getFirst().getValue()).isEqualTo(PROFILE_MOLECULAR_SEQUENCE);

    assertThat(molecularSequence.getExtension()).hasSize(3);
    assertThat(molecularSequence.getExtension().get(0).getUrl())
        .isNotNull()
        .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/SequenceAuthor");
    assertThat(molecularSequence.getExtension().get(0).getValue().toString())
        .isNotNull()
        .isEqualTo(
            "Ralf Dürrwald, Stephan Fuchs, Stefan Kroeger, Marianne Wedde, Oliver Drechsel, Aleksandar Radonic, Rene Kmiecinski, Thorsten Wolff");

    assertThat(molecularSequence.getExtension().get(1).getUrl())
        .isNotNull()
        .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/SequencingReason");
    assertThat(molecularSequence.getExtension().get(1).getValue()).isNotNull();
    assertThat(molecularSequence.getExtension().get(1).getValue().getClass())
        .isEqualTo(Coding.class);
    Coding extensionCoding = (Coding) molecularSequence.getExtension().get(1).getValue();
    assertThat(extensionCoding.getSystem()).isNotNull().isEqualTo(SYSTEM_SNOMED);
    assertThat(extensionCoding.getVersion()).isNotNull().isEqualTo(SNOMED_VERSION);
    assertThat(extensionCoding.getCode()).isNotNull().isEqualTo("255226008");
    assertThat(molecularSequence.getExtension().get(2).getValue()).isNotNull();
    assertThat(molecularSequence.getExtension().get(2).getValue().getClass())
        .isEqualTo(Reference.class);
    Reference ref = (Reference) molecularSequence.getExtension().get(2).getValue();
    assertThat(ref.getReference())
        .isNotNull()
        .matches(DOCUMENT_REFERENCE_BASE + REGULAR_EXPRESSION_UUID_SUFFIX);
    assertThat(molecularSequence.getIdentifier()).hasSize(1);
    assertThat(molecularSequence.getIdentifier().getFirst().getValue()).isEqualTo("A384");
    assertThat(molecularSequence.getType()).isNotNull();
    assertThat(molecularSequence.getType().getSystem()).isEqualTo(SequenceType.DNA.getSystem());
    assertThat(molecularSequence.getCoordinateSystem()).isZero();
    assertThat(molecularSequence.getSpecimen()).isNotNull();
    assertThat(molecularSequence.getSpecimen().getReference())
        .matches(PREFIX_SPECIMEN + REGULAR_EXPRESSION_UUID_SUFFIX);
    assertThat(molecularSequence.getDevice()).isNotNull();
    assertThat(molecularSequence.getDevice().getReference())
        .matches(PREFIX_DEVICE + REGULAR_EXPRESSION_UUID_SUFFIX);
    assertThat(molecularSequence.getPerformer()).isNotNull();
    assertThat(molecularSequence.getPerformer().getReference())
        .matches(PREFIX_ORGANIZATION + REGULAR_EXPRESSION_UUID_SUFFIX);

    assertRepository(molecularSequence, withRepository);
  }

  private void assertRepository(MolecularSequence molecularSequence, boolean withRepository) {
    if (!withRepository) {
      assertThat(molecularSequence.getRepository()).isEmpty();
    } else {
      assertThat(molecularSequence.getRepository()).hasSize(1);
      MolecularSequenceRepositoryComponent repository =
          molecularSequence.getRepository().getFirst();
      assertThat(repository.getType()).isEqualTo(LOGIN);
      assertThat(repository.getUrl()).isEqualTo(REPOSITORY_LINK);
      assertThat(repository.getName()).isEqualTo("gisaid");
      assertThat(repository.getDatasetId()).isEqualTo("EPI_ISL_16883504");
      assertThat(repository.getExtension()).hasSize(3);

      assertThat(repository.getExtension().getFirst())
          .extracting("url")
          .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/SequenceUploadDate");
      assertThat(repository.getExtension().getFirst())
          .extracting("value")
          .extracting(Object::getClass)
          .isEqualTo(DateTimeType.class);
      assertThat(repository.getExtension().get(1).getUrl())
          .isNotNull()
          .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/SequenceUploadSubmitter");
      assertThat(repository.getExtension().get(1).getValue())
          .isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(new StringType("O Drechsel"));
      assertThat(repository.getExtension().get(2).getUrl())
          .isNotNull()
          .isEqualTo("https://demis.rki.de/fhir/igs/StructureDefinition/SequenceUploadStatus");
      Type extensionValue = repository.getExtension().get(2).getValue();
      assertThat(extensionValue).isNotNull().extracting("system").isEqualTo(SYSTEM_SNOMED);
      assertThat(extensionValue).isNotNull().extracting("version").isEqualTo(SNOMED_VERSION);
      assertThat(repository.getExtension().get(2).getValue())
          .isNotNull()
          .extracting("code")
          .isEqualTo("385645004");
    }
  }

  private String extractIdFromFullUrl(String fullUrl) {
    if (fullUrl != null && !fullUrl.isBlank()) {
      return fullUrl.substring(fullUrl.lastIndexOf("/") + 1);
    }
    return "";
  }

  @Test
  void shouldThrowExceptionOnNoVersionInfos() {
    IgsOverviewData igsOverviewData = getIgsTestOverviewData(true, true);
    assertThatThrownBy(
            () -> new ProcessNotificationSequenceRequestParametersBuilder(igsOverviewData, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("VersionInfos must not be null");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionOnNoLoincVersion(String loincVersion) {
    IgsOverviewData igsOverviewData = getIgsTestOverviewData(true, true);
    VersionInfos versionInfos = new VersionInfos(loincVersion, SNOMED_VERSION);
    assertThatThrownBy(
            () ->
                new ProcessNotificationSequenceRequestParametersBuilder(
                    igsOverviewData, versionInfos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("LOINC version must not be null or blank");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowExceptionOnNoSnomedVersion(String snomedVersion) {
    IgsOverviewData igsOverviewData = getIgsTestOverviewData(true, true);
    VersionInfos versionInfos = new VersionInfos(LOINC_VERSION, snomedVersion);
    assertThatThrownBy(
            () ->
                new ProcessNotificationSequenceRequestParametersBuilder(
                    igsOverviewData, versionInfos))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("SNOMED version must not be null or blank");
  }
}
