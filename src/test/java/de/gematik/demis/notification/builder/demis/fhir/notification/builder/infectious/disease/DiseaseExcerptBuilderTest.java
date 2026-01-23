package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Immunization;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class DiseaseExcerptBuilderTest {
  @Nested
  class BundleToExcerptNotifiedPersonNotByNameTests {
    @Test
    void shouldCreateNewBundleFollowingSpecifications() throws IOException {
      int[] idHelper = {50};
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils
            .when(Utils::generateUuidString)
            .thenAnswer(invocation -> Integer.toString(idHelper[0]++));
        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();

        String json =
            Files.readString(
                Path.of(
                    "src/test/resources/disease/DiseaseNotificationTestcaseForNotByNameExcerpt.json"));
        IParser iParser = FhirContext.forR4().newJsonParser().setPrettyPrint(true);
        Bundle bundle = iParser.parseResource(Bundle.class, json);

        Bundle excerpt =
            DiseaseExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(bundle);
        String excerptJson = iParser.encodeResourceToString(excerpt);

        String expected =
            Files.readString(
                Path.of(
                    "src/test/resources/disease/DiseaseNotificationTestcaseForNotByNameExcerptExpected.json"));

        assertThat(excerptJson).isEqualTo(expected);
      }
    }

    @Test
    void shouldCreateExcerptWithOccuranceAsStringCase() throws IOException {
      int[] idHelper = {50};
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils
            .when(Utils::generateUuidString)
            .thenAnswer(invocationOnMock -> Integer.toString(idHelper[0]++));
        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();

        String json =
            Files.readString(Path.of("src/test/resources/disease/occuranceCaseExcerpt.json"));
        IParser iParser = FhirContext.forR4Cached().newJsonParser().setPrettyPrint(true);

        Bundle bundle = iParser.parseResource(Bundle.class, json);

        Bundle excerpt =
            DiseaseExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(bundle);

        assertThat(((Immunization) excerpt.getEntry().get(8).getResource()).getOccurrence())
            .hasToString("ASKU");
      }
    }
  }

  @Nested
  class NonNominalBundleToExcerptNotifiedPersonAnonymousTests {
    @Test
    void createExcerpt_shouldontainsnotifiedpersonanonymous() throws IOException {
      int[] idHelper = {50};
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils
            .when(Utils::generateUuidString)
            .thenAnswer(invocation -> Integer.toString(idHelper[0]++));
        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();

        String json = Files.readString(Path.of("src/test/resources/disease/7_3/basic.json"));
        IParser iParser = FhirContext.forR4().newJsonParser().setPrettyPrint(true);
        Bundle bundle = iParser.parseResource(Bundle.class, json);

        Bundle excerpt =
            DiseaseExcerptBuilder.createExcerptNotifiedPersonAnonymousFromNonNominalBundle(bundle);
        String excerptJson = iParser.encodeResourceToString(excerpt);

        String expected =
            Files.readString(Path.of("src/test/resources/disease/7_3/basic_expected.json"));

        assertThat(excerptJson).isEqualTo(expected);
      }
    }
  }
}
