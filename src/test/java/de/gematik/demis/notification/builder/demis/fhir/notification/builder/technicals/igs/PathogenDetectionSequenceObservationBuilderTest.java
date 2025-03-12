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
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.MolecularSequence;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.Test;

class PathogenDetectionSequenceObservationBuilderTest {

  private static final String PATH_TO_EXPECTED_ENTRY_FILE =
      "src/test/resources/igs/observationPathogenDetectionSequence.json";

  private static final String SUBJECT_ID = "3661f735-d3db-3c22-909d-3a16a7334c1d";
  private static final String SPECIMEN_ID = "43b3b82f-656f-3e3d-a510-0109771c9e97";
  private static final String DEVICE_ID = "b075aba8-edcb-38f7-973a-6ba64b240a3e";
  private static final String DERIVED_FROM_ID = "26038f18-8c2d-3ad7-90e6-f52cfd779262";
  private static final String SPECIES_DISPLAY =
      "Severe acute respiratory syndrome coronavirus 2 (organism)";
  private static final String SPECIES_CODE = "96741-4";

  @Test
  void shouldBuildSequenceObservation() throws IOException {
    PathogenDetectionSequenceObservationBuilder builder = configureBuilderWithTestData();

    Observation observation = builder.buildResource().orElseThrow();
    String actualCompositionAsString = getJsonParser().encodeResourceToString(observation);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_ENTRY_FILE), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  private PathogenDetectionSequenceObservationBuilder configureBuilderWithTestData() {

    IgsOverviewData data =
        IgsOverviewData.builder().speciesCode(SPECIES_CODE).species(SPECIES_DISPLAY).build();
    return PathogenDetectionSequenceObservationBuilder.builder()
        .subjectReference(createOptionalResourceWithId(Patient.class, SUBJECT_ID))
        .deviceReference(createOptionalResourceWithId(Device.class, DEVICE_ID))
        .molecularReference(createOptionalResourceWithId(MolecularSequence.class, DERIVED_FROM_ID))
        .specimenReference(createOptionalResourceWithId(Specimen.class, SPECIMEN_ID))
        .data(data)
        .build();
  }
}
