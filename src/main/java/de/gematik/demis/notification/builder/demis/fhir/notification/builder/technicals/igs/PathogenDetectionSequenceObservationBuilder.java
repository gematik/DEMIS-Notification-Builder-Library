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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateCodeableConcept;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_OBSERVATION_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_OBSERVATION_INTERPRETATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LOINC_ORG_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_DEVICE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_MOLECULAR_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PATIENT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_SPECIMEN;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_PATHOGEN_DETECTION_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static org.hl7.fhir.r4.model.Observation.ObservationStatus.FINAL;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.VersionInfos;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;

/** Builder for the entry Observation/PathogenDetectionSequence in an IGS Observation. */
@SuperBuilder
public class PathogenDetectionSequenceObservationBuilder
    extends AbstractIgsResourceBuilder<Observation> {

  private static final String LABORATORY_CODE = "laboratory";
  private static final String POSITIVE_CODE = "POS";
  private static final String METHOD_CODE = "117040002";
  private static final String METHOD_DISPLAY = "Nucleic acid sequencing (procedure)";
  private static final String CODING_CODE = "41852-5";
  private Optional<Patient> subjectReference;
  private Optional<Specimen> specimenReference;
  private Optional<Device> deviceReference;
  private Optional<MolecularSequence> molecularReference;
  private VersionInfos versionInfos;

  /**
   * Builds the FHIR object representing the entry Observation/PathogenDetectionSequence.
   *
   * @return The FHIR object representing the entry Observation/PathogenDetectionSequence.
   */
  @Override
  public Optional<Observation> buildResource() {
    Observation observation = new Observation();

    observation.setId(UUID.randomUUID().toString());
    observation.setMeta(
        MetaBuilder.builder()
            .metaProfile(PROFILE_PATHOGEN_DETECTION_SEQUENCE)
            .initialize()
            .build());

    observation.setStatus(FINAL);
    observation.setCategory(
        List.of(generateCodeableConcept(CODE_SYSTEM_OBSERVATION_CATEGORY, LABORATORY_CODE, null)));

    subjectReference.ifPresent(
        resource -> observation.setSubject(new Reference(PREFIX_PATIENT + resource.getId())));

    observation.setValue(
        generateCodeableConcept(
            SYSTEM_SNOMED, data.getSpeciesCode(), data.getSpecies(), versionInfos.snomedVersion()));

    observation.setCode(
        new CodeableConcept()
            .addCoding(
                new Coding()
                    .setCode(CODING_CODE)
                    .setSystem(LOINC_ORG_SYSTEM)
                    .setVersion(versionInfos.loincVersion())));
    observation.setInterpretation(
        List.of(
            generateCodeableConcept(CODE_SYSTEM_OBSERVATION_INTERPRETATION, POSITIVE_CODE, null)));

    observation.setMethod(
        generateCodeableConcept(
            SYSTEM_SNOMED, METHOD_CODE, METHOD_DISPLAY, versionInfos.snomedVersion()));

    specimenReference.ifPresent(
        resource -> observation.setSpecimen(new Reference(PREFIX_SPECIMEN + resource.getId())));
    deviceReference.ifPresent(
        resource -> observation.setDevice(new Reference(PREFIX_DEVICE + resource.getId())));
    molecularReference.ifPresent(
        resource ->
            observation.setDerivedFrom(
                List.of(new Reference(PREFIX_MOLECULAR_SEQUENCE + resource.getId()))));

    return Optional.of(observation);
  }
}
