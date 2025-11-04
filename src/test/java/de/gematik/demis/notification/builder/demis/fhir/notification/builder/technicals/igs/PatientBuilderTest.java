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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class PatientBuilderTest {

  private static final String PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID =
      "src/test/resources/igs/patient.json";
  private static final String PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID_GENDER_OTHER =
      "src/test/resources/igs/patientGenderOther.json";

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

  @ParameterizedTest
  @ValueSource(strings = {"0", "13", "November"})
  void shouldNotBuildPatientWithInvalidHostBirthMonth(String hostBirthMonth) {
    PatientBuilder builder = configureBuilderWithTestData(hostBirthMonth, "1978", "male");

    assertThatThrownBy(builder::buildResource)
        .isInstanceOf(InvalidInputDataException.class)
        .hasMessageContaining(String.format("Invalid birthday month: %s", hostBirthMonth));
  }

  @ParameterizedTest
  @MethodSource("invalidBirthYears")
  void shouldNotBuildPatientWithInvalidHostBirthYear(String hostBirthYear) {
    PatientBuilder builder = configureBuilderWithTestData("11", hostBirthYear, "male");

    assertThatThrownBy(builder::buildResource)
        .isInstanceOf(InvalidInputDataException.class)
        .hasMessageContaining(String.format("Invalid birthday year: %s", hostBirthYear));
  }

  static Stream<String> invalidBirthYears() {
    int currentYear = Year.now().getValue();
    return Stream.of("1799", String.valueOf(currentYear + 1), "twenty twenty");
  }

  @Test
  void shouldNotBuildPatientWithInvalidHostSex() {
    PatientBuilder builder = configureBuilderWithTestData("11", "1979", "androgymous");

    assertThatThrownBy(builder::buildResource)
        .isInstanceOf(InvalidInputDataException.class)
        .hasMessageContaining("Unknown AdministrativeGender code 'androgymous'");
  }

  @Test
  void shouldSetExtensionIfHostSexIsOther() throws IOException {
    PatientBuilder builder = configureBuilderWithTestDataGenderOther();

    Patient patient = builder.buildResource().get();

    String actualCompositionAsString = getJsonParser().encodeResourceToString(patient);
    String expectedCompositionAsString =
        Files.readString(
            Path.of(PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID_GENDER_OTHER),
            StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  private PatientBuilder configureBuilderWithTestData() {
    return configureBuilderWithTestData("9", "1978", "male");
  }

  private PatientBuilder configureBuilderWithTestDataGenderOther() {
    return configureBuilderWithTestData("9", "1978", "other");
  }

  private PatientBuilder configureBuilderWithTestData(
      String hostBirthMonth, String hostBirthYear, String hostSex) {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .hostBirthMonth(hostBirthMonth)
            .hostBirthYear(hostBirthYear)
            .hostSex(hostSex)
            .geographicLocation("130")
            .build();
    return PatientBuilder.builder().data(data).build();
  }
}
