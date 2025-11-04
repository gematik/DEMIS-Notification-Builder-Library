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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateIdentifier;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.OrganizationBuilder.OrganizationType.LABOR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.OrganizationBuilder.OrganizationType.SUBMITTING;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PractitionerRoleBuilder.PractitionerRoleProfile.NOTIFIER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PractitionerRoleBuilder.PractitionerRoleProfile.SUBMITTER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SubstanceBuilder.Kind.ADAPTER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SubstanceBuilder.Kind.PRIMER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DEMIS_RKI_IGS_DE_FHIR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_IGS_NOTIFICATION_BUNDLE_SEQUENCE;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.LaboratoryReportSequenceDiagnosticReportBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.MetaBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.MolecularSequenceBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.NotificationIdCompositionBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PathogenDetectionSequenceObservationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PatientBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SequencingDeviceBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SpecimenSequenceBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SubstanceBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.VersionInfos;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Substance;

/**
 * Builder for an IGS notification (FHIR profile
 * https://demis.rki.de/fhir/StructureDefinition/NotificationBundleSequence)
 */
public class ProcessNotificationSequenceRequestParametersBuilder {

  private final Bundle document;
  private final IgsOverviewData data;
  private final VersionInfos versionInfos;

  public ProcessNotificationSequenceRequestParametersBuilder(
      IgsOverviewData data, VersionInfos versionInfos) {
    this.data = data;
    this.document = new Bundle();
    validateVersionInfos(versionInfos);
    this.versionInfos = versionInfos;
  }

  /**
   * Generates the object representing the notification.
   *
   * @return The object representing the notification.
   */
  public final Bundle build() {
    document.setMeta(
        MetaBuilder.builder()
            .metaProfile(PROFILE_IGS_NOTIFICATION_BUNDLE_SEQUENCE)
            .lastUpdated(new Date())
            .initialize()
            .build());
    document.setIdentifier(
        generateIdentifier(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID, UUID.randomUUID().toString()));
    document.setType(Bundle.BundleType.DOCUMENT);
    document.setTimestamp(DateTimeType.now().getValue());
    document.setEntry(generateEntries());
    return this.document;
  }

  private void validateVersionInfos(VersionInfos versionInfos) {
    if (versionInfos == null) {
      throw new IllegalArgumentException("VersionInfos must not be null");
    }
    if (versionInfos.loincVersion() == null || versionInfos.loincVersion().isBlank()) {
      throw new IllegalArgumentException("LOINC version must not be null or blank");
    }
    if (versionInfos.snomedVersion() == null || versionInfos.snomedVersion().isBlank()) {
      throw new IllegalArgumentException("SNOMED version must not be null or blank");
    }
  }

  private List<Bundle.BundleEntryComponent> generateEntries() {
    List<Optional<? extends Resource>> entries = new ArrayList<>();

    Optional<Patient> notifiedPersonNotByName =
        PatientBuilder.builder().data(data).build().buildResource();
    Optional<Organization> demisLaboratory =
        OrganizationBuilder.builder().organizationType(LABOR).data(data).build().buildResource();
    Optional<Organization> submittingFacility =
        OrganizationBuilder.builder()
            .organizationType(SUBMITTING)
            .data(data)
            .build()
            .buildResource();
    Optional<PractitionerRole> notifierRole =
        PractitionerRoleBuilder.builder()
            .organizationReference(demisLaboratory)
            .profile(NOTIFIER_ROLE)
            .data(data)
            .build()
            .buildResource();
    Optional<PractitionerRole> submittingRole =
        PractitionerRoleBuilder.builder()
            .organizationReference(submittingFacility)
            .profile(SUBMITTER_ROLE)
            .data(data)
            .build()
            .buildResource();
    Optional<Device> sequencingDevice =
        SequencingDeviceBuilder.builder().data(data).build().buildResource();
    Optional<Substance> adapterSubstance =
        SubstanceBuilder.builder().kind(ADAPTER).data(data).build().buildResource();
    Optional<Substance> primerSubstance =
        SubstanceBuilder.builder().kind(PRIMER).data(data).build().buildResource();
    Optional<Specimen> specimen =
        SpecimenSequenceBuilder.builder()
            .subjectReference(notifiedPersonNotByName)
            .adapterReference(adapterSubstance)
            .primerSchemeReference(primerSubstance)
            .collectionCollectorReference(submittingRole)
            .data(data)
            .snomedVersion(versionInfos.snomedVersion())
            .build()
            .buildResource();
    Optional<MolecularSequence> molecularSequence =
        MolecularSequenceBuilder.builder()
            .specimenReference(specimen)
            .deviceReference(sequencingDevice)
            .performerReference(demisLaboratory)
            .data(data)
            .snomedVersion(versionInfos.snomedVersion())
            .build()
            .buildResource();
    Optional<Observation> observation =
        PathogenDetectionSequenceObservationBuilder.builder()
            .subjectReference(notifiedPersonNotByName)
            .specimenReference(specimen)
            .molecularReference(molecularSequence)
            .deviceReference(sequencingDevice)
            .data(data)
            .versionInfos(versionInfos)
            .build()
            .buildResource();
    Optional<DiagnosticReport> laboratoryReportSequence =
        LaboratoryReportSequenceDiagnosticReportBuilder.builder()
            .subjectReference(notifiedPersonNotByName)
            .resultReference(observation)
            .data(data)
            .build()
            .buildResource();
    Optional<Composition> composition =
        NotificationIdCompositionBuilder.builder()
            .sectionEntryReference(laboratoryReportSequence)
            .subjectReference(notifiedPersonNotByName)
            .authorReference(notifierRole)
            .data(data)
            .loincVersion(versionInfos.loincVersion())
            .build()
            .buildResource();

    entries.addAll(
        List.of(
            composition,
            notifiedPersonNotByName,
            notifierRole,
            demisLaboratory,
            submittingRole,
            submittingFacility,
            laboratoryReportSequence,
            sequencingDevice,
            specimen,
            adapterSubstance,
            primerSubstance,
            observation,
            molecularSequence));

    return entries.stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::createBundleEntry)
        .toList();
  }

  private BundleEntryComponent createBundleEntry(Resource resource) {
    BundleEntryComponent component = new BundleEntryComponent();
    component.setFullUrl(generateFullUrl(resource));
    component.setResource(resource);
    return component;
  }

  private String generateFullUrl(Resource resource) {
    return DEMIS_RKI_IGS_DE_FHIR + resource.getResourceType() + "/" + resource.getId();
  }
}
