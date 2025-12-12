package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotificationBundleDiseaseNonNominalDataBuilderTest {

  @MethodSource("bundleTestData")
  @ParameterizedTest
  void thatBundlesCanBeCopied(final Path inputPath, final Path expectedPath) throws IOException {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::getCurrentDate).thenReturn(TestObjects.mockDate());
      utilities.when(() -> Utils.getShortReferenceOrUrnUuid(Mockito.any())).thenCallRealMethod();
      final IParser iParser = FhirContext.forR4().newJsonParser();
      iParser.setPrettyPrint(true);

      final Bundle original = createBundle(inputPath);
      final Bundle copy = NotificationBundleDiseaseNonNominalDataBuilder.deepCopy(original);

      final String expected = Files.readString(expectedPath);
      final String result = iParser.encodeResourceToString(copy);
      assertThat(result).isEqualToIgnoringWhitespace(expected);
    }
  }

  private static Stream<Arguments> bundleTestData() {
    return Stream.of(
        // Valid bundle
        Arguments.of(
            Path.of("src/test/resources/disease/7_3/generic.json"),
            Path.of("src/test/resources/disease/7_3/generic-expected.json")),
        // with urn-uuids
        Arguments.of(
            Path.of("src/test/resources/disease/7_3/urn_uuid-mixed.json"),
            Path.of("src/test/resources/disease/7_3/urn_uuid-mixed-expected.json")),
        // don't copy current address and NotifiedPersonFacility
        Arguments.of(
            Path.of("src/test/resources/disease/7_3/other-facility.json"),
            Path.of("src/test/resources/disease/7_3/other-facility-expected.json")),
        // keep symptoms
        Arguments.of(
            Path.of("src/test/resources/disease/7_3/with-symptoms.json"),
            Path.of("src/test/resources/disease/7_3/with-symptoms-expected.json")));
  }

  @Test
  void shouldSetPractitionerReferenceWithoutUrnUuidWhenParametersNotificationIsReceived()
      throws IOException {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::getCurrentDate).thenReturn(TestObjects.mockDate());
      utilities.when(() -> Utils.getShortReferenceOrUrnUuid(Mockito.any())).thenCallRealMethod();
      final String source =
          Files.readString(Path.of("src/test/resources/disease/7_3/parameters-notification.json"));

      final IParser iParser = FhirContext.forR4().newJsonParser();
      iParser.setPrettyPrint(true);

      final Parameters original = (Parameters) iParser.parseResource(source);
      Bundle originalBundle = (Bundle) original.getParameter().getFirst().getResource();

      Bundle copy = NotificationBundleDiseaseNonNominalDataBuilder.deepCopy(originalBundle);

      assertThat(copy.getEntryFirstRep().getFullUrl())
          .isEqualTo("https://demis.rki.de/fhir/Composition/9bb7aeba-581a-47ce-8791-5cdb319d6267");
      Resource resource = copy.getEntry().get(3).getResource();
      assertThat(resource).isInstanceOf(PractitionerRole.class);
      PractitionerRole practitionerRole = (PractitionerRole) resource;
      assertThat(practitionerRole.getOrganization().getReference())
          .hasToString("Organization/2ac04fbc-e807-4eb1-a07e-8a941ae1b7d7");

      final String expected =
          Files.readString(
              Path.of("src/test/resources/disease/7_3/parameters-notification-expected.json"));

      assertThat(iParser.encodeResourceToString(copy)).isEqualToIgnoringWhitespace(expected);
    }
  }

  @Nonnull
  private Bundle createBundle(@Nonnull final Path path) {
    try {
      final String source = Files.readString(path);
      final IParser iParser = FhirContext.forR4().newJsonParser();
      iParser.setPrettyPrint(true);
      return (Bundle) iParser.parseResource(source);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testBundleNonNominalBuilderProfileUrl() {
    NotificationBundleDiseaseNonNominalDataBuilder builder =
        new NotificationBundleDiseaseNonNominalDataBuilder();
    String expected = DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE_NON_NOMINAL;
    assertThat(builder.getDefaultProfileUrl()).isEqualTo(expected);
  }

  @Test
  void testBundleNonNominalBuilderCompositionCreation() {
    NotificationBundleDiseaseNonNominalDataBuilder builder =
        new NotificationBundleDiseaseNonNominalDataBuilder();
    Condition resource = new Condition();
    resource.setMeta(
        new Meta().addProfile("https://demis.rki.de/fhir/StructureDefinition/DiseaseHIVD"));
    builder.setDisease(resource);
    var actualBuilder = builder.createComposition();
    var actual = actualBuilder.build();

    assertThat(actualBuilder).isInstanceOf(NotificationDiseaseNonNominalDataBuilder.class);
    assertThat(actual.getMeta().getProfile().getFirst().asStringValue())
        .isEqualTo(
            "https://demis.rki.de/fhir/StructureDefinition/NotificationDiseaseNonNominalHIVD");
  }
}
