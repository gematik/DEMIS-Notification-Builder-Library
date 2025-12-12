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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SubstanceBuilder.Kind.ADAPTER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.SubstanceBuilder.Kind.PRIMER;
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
import org.hl7.fhir.r4.model.Substance;
import org.junit.jupiter.api.Test;

class SubstanceBuilderTest {

  private static final String PATH_TO_EXPECTED_ADAPTER_SUBSTANCE =
      "src/test/resources/igs/substanceAdapterSubstance.json";
  private static final String PATH_TO_EXPECTED_PRIMER_SCHEME_SUBSTANCE =
      "src/test/resources/igs/substancePrimerSchemeSubstance.json";
  private static final String DESCRIPTION = "TAGCGAGT";

  @Test
  void shouldBuildAdapterSubstance() throws IOException {
    IgsOverviewData data = IgsOverviewData.builder().adapterSubstance(DESCRIPTION).build();

    Substance substance =
        SubstanceBuilder.builder().data(data).kind(ADAPTER).build().buildResource().orElseThrow();
    String actualCompositionAsString = getJsonParser().encodeResourceToString(substance);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_ADAPTER_SUBSTANCE), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  @Test
  void shouldReturnEmptyOptionalForAdapterIfNeededValueIsNull() {
    IgsOverviewData data = IgsOverviewData.builder().build();

    Optional<Substance> substance =
        SubstanceBuilder.builder().data(data).kind(ADAPTER).build().buildResource();
    assertThat(substance).isEmpty();
  }

  @Test
  void shouldBuildPrimerSchemeSubstance() throws IOException {
    IgsOverviewData data = IgsOverviewData.builder().primerSchemeSubstance(DESCRIPTION).build();

    Substance substance =
        SubstanceBuilder.builder().data(data).kind(PRIMER).build().buildResource().orElseThrow();
    String actualCompositionAsString = getJsonParser().encodeResourceToString(substance);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_PRIMER_SCHEME_SUBSTANCE), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  @Test
  void shouldReturnEmptyOptionalForPrimerSchemeIfNeededValueIsNull() {
    IgsOverviewData data = IgsOverviewData.builder().build();

    Optional<Substance> substance =
        SubstanceBuilder.builder().data(data).kind(PRIMER).build().buildResource();
    assertThat(substance).isEmpty();
  }
}
