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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class PatientBuilderTest {

  private static final String PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID =
      "src/test/resources/igs/patient.json";

  @Test
  void shouldBuildPatient() throws IOException {
    PatientBuilder builder = configureBuilderWithTestData();

    Patient patient = builder.buildResource().get();
    String actualCompositionAsString = getJsonParser().encodeResourceToString(patient);
    String expectedCompositionAsString =
        Files.readString(
            Path.of(PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  private PatientBuilder configureBuilderWithTestData() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .hostBirthMonth("9")
            .hostBirthYear("1978")
            .hostSex("male")
            .geographicLocation("130")
            .build();
    return PatientBuilder.builder().data(data).build();
  }
}
