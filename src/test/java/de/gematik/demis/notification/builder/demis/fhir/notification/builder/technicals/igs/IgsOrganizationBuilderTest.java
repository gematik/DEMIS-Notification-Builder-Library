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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.OrganizationBuilder.OrganizationType.LABOR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.OrganizationBuilder.OrganizationType.SUBMITTING;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class IgsOrganizationBuilderTest {

  private static final String PATH_TO_EXPECTED_ORGANIZATION_SUBMITTING_FACILITY =
      "src/test/resources/igs/organizationSubmittingFacility.json";
  private static final String PATH_TO_EXPECTED_ORGANIZATION_DEMIS_LABORATORY_ID =
      "src/test/resources/igs/organizationDemisLaboratoryId.json";
  private static final String INVALID_INPUT_DATA_EXCEPTION_MESSAGE =
      "Wenn das Primärlabor angegeben wurde, dann müssen die folgenden Felder alle "
          + "befüllt werden: PRIME_DIAGNOSTIC_LAB.NAME, PRIME_DIAGNOSTIC_LAB.EMAIL, PRIME_DIAGNOSTIC_LAB.ADDRESS, "
          + "PRIME_DIAGNOSTIC_LAB.CITY, PRIME_DIAGNOSTIC_LAB.POSTAL_CODE und PRIME_DIAGNOSTIC_LAB.COUNTRY";

  @Test
  void shouldBuildIgsSubmittingFacilityOrganization() throws IOException {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .build();

    Organization organization =
        OrganizationBuilder.builder()
            .organizationType(SUBMITTING)
            .data(data)
            .build()
            .buildResource()
            .orElseThrow();

    String actualCompositionAsString = getJsonParser().encodeResourceToString(organization);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_ORGANIZATION_SUBMITTING_FACILITY));

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotBuildIgsSubmittingFacilityOrganization(String input) {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName(input)
            .primeDiagnosticLabEmail(input)
            .primeDiagnosticLabAddress(input)
            .primeDiagnosticLabCity(input)
            .primeDiagnosticLabPostalCode(input)
            .primeDiagnosticLabCountry(input)
            .primeDiagnosticLabDemisLabId(input)
            .primeDiagnosticLabFederalState(input)
            .build();

    Optional<Organization> organization =
        OrganizationBuilder.builder()
            .organizationType(SUBMITTING)
            .data(data)
            .build()
            .buildResource();

    assertThat(organization).isEmpty();
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabNameIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName(null)
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  private void assertInvalidInputDataException(IgsOverviewData data) {
    OrganizationBuilder organizationBuilder =
        OrganizationBuilder.builder().organizationType(SUBMITTING).data(data).build();
    assertThatThrownBy(() -> organizationBuilder.buildResource())
        .isInstanceOf(InvalidInputDataException.class)
        .hasMessage(INVALID_INPUT_DATA_EXCEPTION_MESSAGE);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabEmailIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabEmail(null)
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabAddressIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .primeDiagnosticLabAddress(null)
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabCityIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabCity(null)
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabPostalCodeIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabPostalCode(null)
            .primeDiagnosticLabCountry("20422")
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabCountryIsMissing() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName("Primärlabor")
            .primeDiagnosticLabEmail("ifsg@primaerlabor-gibt-es-nicht.de")
            .primeDiagnosticLabAddress("Dingsweg 321")
            .primeDiagnosticLabCity("Berlin")
            .primeDiagnosticLabPostalCode("13055")
            .primeDiagnosticLabCountry(null)
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabOnlyHasDemisLabId() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName(null)
            .primeDiagnosticLabEmail(null)
            .primeDiagnosticLabAddress(null)
            .primeDiagnosticLabCity(null)
            .primeDiagnosticLabPostalCode(null)
            .primeDiagnosticLabCountry(null)
            .primeDiagnosticLabDemisLabId("987654321")
            .primeDiagnosticLabFederalState(null)
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldThrowExeptionWhenPrimeDiagnosticLabHasOnlyFederalState() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .primeDiagnosticLabName(null)
            .primeDiagnosticLabEmail(null)
            .primeDiagnosticLabAddress(null)
            .primeDiagnosticLabCity(null)
            .primeDiagnosticLabPostalCode(null)
            .primeDiagnosticLabCountry(null)
            .primeDiagnosticLabDemisLabId(null)
            .primeDiagnosticLabFederalState("Berlin")
            .build();
    assertInvalidInputDataException(data);
  }

  @Test
  void shouldBuildDemisLaboratoryIdOrganization() throws IOException {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .sequencingLabDemisLabId("10285")
            .sequencingLabName("Nationales Referenzzentrum für Influenza, FG17, RKI")
            .sequencingLabAddress("Seestr. 10")
            .sequencingLabPostalCode("13353")
            .sequencingLabCity("Berlin")
            .sequencingLabCountry("20422")
            .sequencingLabEmail("NRZ-Influenza@rki.de")
            .build();

    Organization organization =
        OrganizationBuilder.builder()
            .organizationType(LABOR)
            .data(data)
            .build()
            .buildResource()
            .orElseThrow();

    String actualCompositionAsString = getJsonParser().encodeResourceToString(organization);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_ORGANIZATION_DEMIS_LABORATORY_ID));

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }
}
