package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_STRATEGY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PATIENT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PRACTITIONER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_SUBSTANCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SPECIMEN_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_ISOLATE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.AVAILABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Substance;

/** Builder for the entry Specimen/SpecimenSequence in an IGS SpecimenSequence. */
@SuperBuilder
public class SpecimenSequenceBuilder extends AbstractIgsResourceBuilder<Specimen> {

  private Optional<Patient> subjectReference;
  private Optional<PractitionerRole> collectionCollectorReference;
  private Optional<Substance> adapterReference;
  private Optional<Substance> primerSchemeReference;
  private String snomedVersion;

  /**
   * Builds the FHIR object representing the entry Specimen/SpecimenSequence.
   *
   * @return The FHIR object representing the entry Specimen/SpecimenSequence.
   */
  @Override
  public Optional<Specimen> buildResource() {
    Specimen specimen = new Specimen();

    specimen.setId(UUID.randomUUID().toString());
    specimen.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_SPECIMEN_SEQUENCE).initialize().build());

    specimen.setStatus(AVAILABLE);
    specimen.setType(
        IgsBuilderUtils.generateCodeableConcept(
            SYSTEM_SNOMED,
            data.getIsolationSourceCode(),
            data.getIsolationSource(),
            snomedVersion));

    specimen.setExtension(
        List.of(
            ExtensionBuilder.builder()
                .url(STRUCTURE_DEFINITION_ISOLATE)
                .valueString(data.getIsolate())
                .initialize()
                .build()));

    subjectReference.ifPresent(
        resource -> specimen.setSubject(new Reference(PREFIX_PATIENT + resource.getId())));

    specimen.setReceivedTime(data.getDateOfReceiving());

    specimen.setCollection(generateSpecimenCollectionComponent());

    specimen.setProcessing(
        List.of(
            new Specimen.SpecimenProcessingComponent()
                .setDescription(data.getNameAmpProtocol())
                .setProcedure(
                    IgsBuilderUtils.generateCodeableConcept(
                        CODE_SYSTEM_SEQUENCING_STRATEGY, data.getSequencingStrategy(), null))
                .setAdditive(generateAdditiveReferences())
                .setTime(new DateTimeType(data.getDateOfSequencing()))));

    return Optional.of(specimen);
  }

  private Specimen.SpecimenCollectionComponent generateSpecimenCollectionComponent() {
    // always generate the collection element
    Specimen.SpecimenCollectionComponent specimenCollectionComponent =
        new Specimen.SpecimenCollectionComponent();
    collectionCollectorReference.ifPresent(
        resource ->
            specimenCollectionComponent.setCollector(
                new Reference(PREFIX_PRACTITIONER_ROLE + resource.getId())));
    if (data.getDateOfSampling() != null) {
      specimenCollectionComponent.setCollected(new DateTimeType(data.getDateOfSampling()));
    }
    return specimenCollectionComponent;
  }

  private List<Reference> generateAdditiveReferences() {
    List<Reference> result = new ArrayList<>();
    adapterReference.ifPresent(
        resource -> result.add(new Reference(PREFIX_SUBSTANCE + resource.getId())));
    primerSchemeReference.ifPresent(
        resource -> result.add(new Reference(PREFIX_SUBSTANCE + resource.getId())));
    return result;
  }
}
