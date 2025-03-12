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
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.parseDateByDateTimeString;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Substance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpecimenSequenceBuilderTest {

  private static final String PATH_TO_EXPECTED_SPECIMEN_SEQUENCE =
      "src/test/resources/igs/specimenSequence.json";
  private static final String SUBJECT_ID = "3661f735-d3db-3c22-909d-3a16a7334c1d";
  public static final String PRACTITIONER_ROLE_ID = "d1b92d45-3811-3064-bad7-b3336e70e08c";
  private static final String SAMPLING_DATE = "2022-12-29T09:50:00+01:00";
  private static final String RECEIVED_DATE = "2022-12-29T15:40:00+01:00";
  public static final String SEQUENCING_DATE = "2023-01-13T10:40:00+01:00";
  public static final String ADAPTER_SUBSTANCE = "8bdf9eb0-ba69-3d54-a228-ecf5f21036e2";
  public static final String PRIMER_SCHEME_SUBSTANCE = "3c2a47c9-2901-324d-9b4a-98598e34fe35";
  public static final String ISOLATION_SOURCE = "Upper respiratory swab specimen (specimen)";
  public static final String ISOLATION_SOURCE_CODE = "258604001";
  public static final String ISOLATE = "119334006";
  public static final String NAME_AMP_PROTOCOL = "ARTICv4";
  public static final String SEQUENCING_STRATEGY = "amplicon";
  private static final boolean INCLUDE_DATE_OF_SAMPLING = true;
  private static final boolean INCLUDE_PRCTITIONER_ROLE = true;
  private static final String
      PATH_TO_EXPECTED_SPECIMEN_SEQUENCE_NO_DATE_OF_SAMPLING_NO_PRACTITIONER_ROLE =
          "src/test/resources/igs/specimenSequenceNoSamplingDateNoPractitionerRole.json";
  private static final String PATH_TO_EXPECTED_SPECIMEN_SEQUENCE_NO_SAMPLING_DATE =
      "src/test/resources/igs/specimenSequenceNoSamplingDate.json";
  private static final String PATH_TO_EXPECTED_SPECIMEN_SEQUENCE_NO_PRACTITIONER_ROLE =
      "src/test/resources/igs/specimenSequenceNoPractitionerRole.json";

  @ParameterizedTest
  @CsvSource({
    INCLUDE_DATE_OF_SAMPLING
        + ","
        + INCLUDE_PRCTITIONER_ROLE
        + ","
        + PATH_TO_EXPECTED_SPECIMEN_SEQUENCE,
    !INCLUDE_DATE_OF_SAMPLING
        + ","
        + INCLUDE_PRCTITIONER_ROLE
        + ","
        + PATH_TO_EXPECTED_SPECIMEN_SEQUENCE_NO_SAMPLING_DATE,
    INCLUDE_DATE_OF_SAMPLING
        + ","
        + !INCLUDE_PRCTITIONER_ROLE
        + ","
        + PATH_TO_EXPECTED_SPECIMEN_SEQUENCE_NO_PRACTITIONER_ROLE
  })
  void shouldBuildSpecimenSequence(
      boolean includeDateOfSampling,
      boolean includePractitionerRole,
      String pathToExpectedSpecimenSequence)
      throws IOException {
    SpecimenSequenceBuilder builder =
        configureBuilderWithTestData(includeDateOfSampling, includePractitionerRole);
    Specimen specimen = builder.buildResource().orElseThrow();
    String actualCompositionAsString = getJsonParser().encodeResourceToString(specimen);
    String expectedCompositionAsString =
        Files.readString(Path.of(pathToExpectedSpecimenSequence), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  private SpecimenSequenceBuilder configureBuilderWithTestData(
      boolean includeDateOfSampling, boolean includePrctitionerRole) {
    IgsOverviewData.IgsOverviewDataBuilder igsOverviewDataBuilder =
        IgsOverviewData.builder()
            .dateOfReceiving(parseDateByDateTimeString(RECEIVED_DATE))
            .dateOfSequencing(parseDateByDateTimeString(SEQUENCING_DATE))
            .isolationSource(ISOLATION_SOURCE)
            .isolationSourceCode(ISOLATION_SOURCE_CODE)
            .isolate(ISOLATE)
            .nameAmpProtocol(NAME_AMP_PROTOCOL)
            .sequencingStrategy(SEQUENCING_STRATEGY);
    if (includeDateOfSampling) {
      igsOverviewDataBuilder.dateOfSampling(parseDateByDateTimeString(SAMPLING_DATE));
    }
    IgsOverviewData data = igsOverviewDataBuilder.build();

    SpecimenSequenceBuilder.SpecimenSequenceBuilderBuilder<?, ?> specimenSequenceBuilder =
        SpecimenSequenceBuilder.builder()
            .subjectReference(createOptionalResourceWithId(Patient.class, SUBJECT_ID))
            .adapterReference(createOptionalResourceWithId(Substance.class, ADAPTER_SUBSTANCE))
            .primerSchemeReference(
                createOptionalResourceWithId(Substance.class, PRIMER_SCHEME_SUBSTANCE))
            .data(data);
    if (includePrctitionerRole) {
      specimenSequenceBuilder.collectionCollectorReference(
          createOptionalResourceWithId(PractitionerRole.class, PRACTITIONER_ROLE_ID));
    } else {
      specimenSequenceBuilder.collectionCollectorReference(Optional.empty());
    }
    return specimenSequenceBuilder.build();
  }
}
