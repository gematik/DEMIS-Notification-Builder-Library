package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BundleBuilderContextTest {

  private Bundle createBundleFromString(final String source) {
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);
    return (Bundle) iParser.parseResource(source);
  }

  private String readFile() {
    try {
      return Files.readString(
          Path.of("src/test/resources/laboratory/cvdp-bundle-replaceRefs.json"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static Stream<Arguments> invalidRefs() {
    return Stream.of(
        Arguments.of("Patient/validRef", "Patient/Patient-invalidRef"),
        Arguments.of("DiagnosticReport/validRef", "DiagnosticReport/invalidRef"),
        Arguments.of("PractitionerRole/validRef", "PractitionerRole/invalidRef"),
        Arguments.of("Observation/validRef", "Observation/invalidRef"),
        Arguments.of("Specimen/validRef", "Specimen/invalidRef"));
  }

  @Test
  void thatValidRefsWontThrowException() {
    final Bundle bundle = createBundleFromString(readFile());
    assertDoesNotThrow(() -> BundleBuilderContext.from(bundle.getEntry()));
  }

  @ParameterizedTest
  @MethodSource("invalidRefs")
  void thatInvalidRefThrowsException(final String validRef, final String invalidRef) {
    String source = readFile();
    source =
        source.replace(
            "\"reference\": \"" + validRef + "\"", "\"reference\": \"" + invalidRef + "\"");
    final Bundle bundle = createBundleFromString(source);
    assertThatThrownBy(() -> BundleBuilderContext.from(bundle.getEntry()))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Reference '" + invalidRef + "' is not resolvable");
  }
}
