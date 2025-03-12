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
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateIdentifier;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_DEVICE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_ORGANIZATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_SPECIMEN;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_MOLECULAR_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCE_AUTHOR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCE_DOCUMENT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_DATE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_STATUS;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_SUBMITTER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_SEQUENCING_REASON;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static org.hl7.fhir.r4.model.MolecularSequence.RepositoryType.LOGIN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.MolecularSequence.MolecularSequenceRepositoryComponent;
import org.hl7.fhir.r4.model.MolecularSequence.SequenceType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;

@SuperBuilder
public class MolecularSequenceBuilder extends AbstractIgsResourceBuilder<MolecularSequence> {

  private static final String DNA = "dna";
  private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
  private static final String DOCUMENT_REFERENCE_TYPE = "DocumentReference";
  private Optional<Specimen> specimenReference;
  private Optional<Device> deviceReference;
  private Optional<Organization> performerReference;

  @Override
  public Optional<MolecularSequence> buildResource() {
    MolecularSequence sequence = new MolecularSequence();
    sequence.setId(UUID.randomUUID().toString());
    sequence.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_MOLECULAR_SEQUENCE).initialize().build());
    sequence.setExtension(generateExtensions());
    sequence.setIdentifier(List.of(generateIdentifier(null, data.getLabSequenceId())));
    sequence.setType(SequenceType.fromCode(DNA));
    sequence.setCoordinateSystem(0);
    specimenReference.ifPresent(
        resource -> sequence.setSpecimen(new Reference(PREFIX_SPECIMEN + resource.getId())));
    deviceReference.ifPresent(
        resource -> sequence.setDevice(new Reference(PREFIX_DEVICE + resource.getId())));
    performerReference.ifPresent(
        resource -> sequence.setPerformer(new Reference(PREFIX_ORGANIZATION + resource.getId())));
    addRepository(sequence);
    return Optional.of(sequence);
  }

  private void addRepository(MolecularSequence sequence) {
    if (StringUtils.isNotBlank(data.getRepositoryName())
        || StringUtils.isNotBlank(data.getRepositoryLink())
        || StringUtils.isNotBlank(data.getRepositoryId())
        || data.getUploadDate() != null
        || StringUtils.isNotBlank(data.getUploadStatus())
        || StringUtils.isNotBlank(data.getUploadSubmitter())) {
      sequence.addRepository(generateRepo());
    }
  }

  private MolecularSequenceRepositoryComponent generateRepo() {
    MolecularSequenceRepositoryComponent repo = new MolecularSequenceRepositoryComponent();
    repo.setExtension(generateRepoExtensions());
    repo.setType(LOGIN);
    repo.setName(data.getRepositoryName());
    repo.setDatasetId(data.getRepositoryId());
    repo.setUrl(data.getRepositoryLink());
    return repo;
  }

  private List<Extension> generateRepoExtensions() {
    SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
    List<Extension> extensions = new ArrayList<>();
    if (data.getUploadDate() != null) {
      extensions.add(
          ExtensionBuilder.builder()
              .url(STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_DATE)
              .dateTime(formatter.format(data.getUploadDate()))
              .initialize()
              .build());
    }
    if (StringUtils.isNotBlank(data.getUploadSubmitter())) {
      extensions.add(
          ExtensionBuilder.builder()
              .url(STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_SUBMITTER)
              .valueString(data.getUploadSubmitter())
              .initialize()
              .build());
    }
    if (StringUtils.isNotBlank(data.getUploadStatus())) {
      extensions.add(
          ExtensionBuilder.builder()
              .url(STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_STATUS)
              .valueCoding(new Coding(SYSTEM_SNOMED, data.getUploadStatus(), null))
              .initialize()
              .build());
    }
    return extensions;
  }

  private List<Extension> generateExtensions() {
    List<Extension> extensions = new ArrayList<>();

    extensions.addAll(
        List.of(
            ExtensionBuilder.builder()
                .url(STRUCTURE_DEFINITION_SEQUENCE_AUTHOR)
                .valueString(data.getAuthor())
                .initialize()
                .build(),
            ExtensionBuilder.builder()
                .url(STRUCTURE_DEFINITION_SEQUENCING_REASON)
                .valueCoding(new Coding(SYSTEM_SNOMED, data.getSequencingReason(), null))
                .initialize()
                .build()));
    if (StringUtils.isNotBlank(data.getFileOneDocumentReference())) {
      addDocumentReference(extensions, data.getFileOneDocumentReference());
    }
    if (StringUtils.isNotBlank(data.getFileTwoDocumentReference())) {
      addDocumentReference(extensions, data.getFileTwoDocumentReference());
    }
    return extensions;
  }

  private void addDocumentReference(List<Extension> extensions, String documentReference) {
    Reference ref = new Reference();
    ref.setType(DOCUMENT_REFERENCE_TYPE);
    ref.setReference(documentReference);
    extensions.add(
        ExtensionBuilder.builder()
            .url(STRUCTURE_DEFINITION_SEQUENCE_DOCUMENT)
            .valueReference(ref)
            .initialize()
            .build());
  }
}
