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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.createOptionalResourceWithId;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.parseDateByDateString;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MolecularSequenceBuilderTest {

  public static final String EXTENSION_VALUE_STRING =
      "Ralf Dürrwald, Stephan Fuchs, Stefan Kroeger, Marianne Wedde, Oliver Drechsel, Aleksandar Radonic, Rene Kmiecinski, Thorsten Wolff";
  public static final String EXTENSION_VALUE_CODING_CODE = "255226008";
  public static final String EXTENSION_VALUE_REFERENCE_REFERENCE =
      "https://demis.rki.de/fhir/DocumentReference/ecd3f1f0-b6b6-46e0-b721-2d9869ab8195";
  public static final String IDENTIFIER_VALUE = "A384";
  public static final String SPECIMEN_ID = "43b3b82f-656f-3e3d-a510-0109771c9e97";
  public static final String DEVICE_ID = "b075aba8-edcb-38f7-973a-6ba64b240a3e";
  public static final String PERFORMER_ID = "227a66a4-a2fe-34b9-ad24-55981d0d1d82";
  public static final String REPOSITORY_EXTENSION_VALUE_DATE_TIME = "2023-02-10";
  public static final String REPOSITORY_EXTENSION_VALUE_STRING = "O Drechsel";
  public static final String REPOSITORY_EXTENSION_VALUE_CODING_CODE = "385645004";
  public static final String REPOSITORY_NAME = "gisaid";
  public static final String REPOSITORY_LINK = "https://pubmlst.org/2348234";
  public static final String REPOSITORY_DATASET_ID = "EPI_ISL_16883504";
  public static final String URL_SEQUENCE_UPLOAD_DATE =
      "https://demis.rki.de/fhir/StructureDefinition/SequenceUploadDate";
  public static final String URL_SEQUENCE_UPLOAD_STATUS =
      "https://demis.rki.de/fhir/StructureDefinition/SequenceUploadStatus";
  public static final String URL_SEQUENCE_UPLOAD_SUBMITTER =
      "https://demis.rki.de/fhir/StructureDefinition/SequenceUploadSubmitter";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE =
      "src/test/resources/igs/molecularSequence.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY =
      "src/test/resources/igs/molecularSequenceWithoutRepository.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY_NAME =
      "src/test/resources/igs/molecularSequenceWithoutRepositoryName.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_DATE =
      "src/test/resources/igs/molecularSequenceWithoutUploadDate.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_STATUS =
      "src/test/resources/igs/molecularSequenceWithoutUploadStatus.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_SUBMITTER =
      "src/test/resources/igs/molecularSequenceWithoutUploadSubmitter.json";
  private static final String PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY_EXTENSIONS =
      "src/test/resources/igs/molecularSequenceWithoutRepositoryExtensions.json";
  private Date uploadDate;
  private String uploadStatus;
  private String uploadSubmitter;

  @BeforeEach
  void beforeEach() {
    this.uploadDate = parseDateByDateString(REPOSITORY_EXTENSION_VALUE_DATE_TIME);
    this.uploadSubmitter = REPOSITORY_EXTENSION_VALUE_STRING;
    this.uploadStatus = REPOSITORY_EXTENSION_VALUE_CODING_CODE;
  }

  @Test
  void shouldBuildLaboratoryReportSequenceDiagnosticReport() {
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();
    assertMolecularSequenceData(molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE);
  }

  private MolecularSequenceBuilder configureBuilderWithTestData(boolean includeRepository) {
    IgsOverviewData.IgsOverviewDataBuilder igsOverviewDataBuilder =
        IgsOverviewData.builder()
            .fileOneDocumentReference(EXTENSION_VALUE_REFERENCE_REFERENCE)
            .author(EXTENSION_VALUE_STRING)
            .labSequenceId(IDENTIFIER_VALUE)
            .sequencingReason(EXTENSION_VALUE_CODING_CODE);
    if (includeRepository) {
      igsOverviewDataBuilder
          .repositoryName(REPOSITORY_NAME)
          .repositoryLink(REPOSITORY_LINK)
          .repositoryId(REPOSITORY_DATASET_ID)
          .uploadSubmitter(this.uploadSubmitter)
          .uploadDate(this.uploadDate)
          .uploadStatus(this.uploadStatus);
    }
    IgsOverviewData data = igsOverviewDataBuilder.build();

    return MolecularSequenceBuilder.builder()
        .specimenReference(createOptionalResourceWithId(Specimen.class, SPECIMEN_ID))
        .deviceReference(createOptionalResourceWithId(Device.class, DEVICE_ID))
        .performerReference(createOptionalResourceWithId(Organization.class, PERFORMER_ID))
        .data(data)
        .build();
  }

  @Test
  void shouldBuildMolecularSequenceWithoutRepository() {
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(false).buildResource().orElseThrow();
    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY);
  }

  @SneakyThrows
  private void assertMolecularSequenceData(
      MolecularSequence actualData, String pathToExpectedData) {
    String molecularSequenceAsString = getJsonParser().encodeResourceToString(actualData);
    String expectedDiagnosticReportAsString =
        Files.readString(Path.of(pathToExpectedData), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualDiagnosticReport = objectMapper.readTree(molecularSequenceAsString);
    JsonNode expectedDiagnosticReport = objectMapper.readTree(expectedDiagnosticReportAsString);

    assertThat(actualDiagnosticReport)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedDiagnosticReport);
  }

  @Test
  void shouldBuildMolecularSequenceWithoutRepositoryName() {
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();
    molecularSequence.getRepository().getFirst().setName(null);

    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY_NAME);
  }

  @Test
  void shouldBuildMolecularSequenceWithoutUploadDate() {
    this.uploadDate = null;
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();

    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_DATE);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldBuildMolecularSequenceWithoutUploadStatus(String uploadStatus) {
    this.uploadStatus = uploadStatus;
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();

    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_STATUS);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldBuildMolecularSequenceWithoutUploadSubmitter(String uploadSubmitter) {
    this.uploadSubmitter = null;
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();

    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_UPLOAD_SUBMITTER);
  }

  @Test
  void shouldBuildMolecularSequenceWithoutRepositoryExtensions() {
    this.uploadDate = null;
    this.uploadStatus = null;
    this.uploadSubmitter = null;
    MolecularSequence molecularSequence =
        configureBuilderWithTestData(true).buildResource().orElseThrow();

    assertMolecularSequenceData(
        molecularSequence, PATH_TO_EXPECTED_MOLECULAR_SEQUENCE_WITHOUT_REPOSITORY_EXTENSIONS);
  }

  @Test
  void shouldKeepDocumentReferenceIfStartsWithHttps() {
    String docRef1 = "https://storage.dev.fra.demis.rki.de/DocumentReference/123";
    String docRef2 = "321";
    IgsOverviewData data =
        IgsOverviewData.builder()
            .fileOneDocumentReference(docRef1)
            .fileTwoDocumentReference(docRef2)
            .author(EXTENSION_VALUE_STRING)
            .labSequenceId(IDENTIFIER_VALUE)
            .sequencingReason(EXTENSION_VALUE_CODING_CODE)
            .build();
    MolecularSequence sequence =
        MolecularSequenceBuilder.builder()
            .specimenReference(createOptionalResourceWithId(Specimen.class, SPECIMEN_ID))
            .deviceReference(createOptionalResourceWithId(Device.class, DEVICE_ID))
            .performerReference(createOptionalResourceWithId(Organization.class, PERFORMER_ID))
            .data(data)
            .build()
            .buildResource()
            .orElseThrow();
    List<Extension> docRefExtenstions =
        sequence.getExtension().stream().filter(e -> e.getValue() instanceof Reference).toList();
    assertThat(docRefExtenstions).hasSize(2);
    assertThat(((Reference) docRefExtenstions.get(0).getValue()).getReference()).isEqualTo(docRef1);
    assertThat(((Reference) docRefExtenstions.get(1).getValue()).getReference()).isEqualTo(docRef2);
  }
}
