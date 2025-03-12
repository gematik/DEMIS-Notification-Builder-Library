package de.gematik.demis.notification.builder.demis.fhir.notification.test;

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

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.instance.model.api.IBaseResource;

public final class FhirJsonTestsUtil {

  private static final ObjectMapper JSON = new ObjectMapper();

  public static String printPrettyJson(IBaseResource resource) {
    return FhirContext.forR4Cached()
        .newJsonParser()
        .setPrettyPrint(true)
        .encodeResourceToString(resource);
  }

  public static void assertEqualJson(IBaseResource actual, String expected, String message) {
    assertEqualJson(
        FhirContext.forR4Cached()
            .newJsonParser()
            .setPrettyPrint(false)
            .encodeResourceToString(actual),
        expected,
        message);
  }

  public static void assertEqualJson(String actual, String expected, String message) {
    try {
      JsonNode expectedJson = JSON.readTree(expected);
      JsonNode actualJson = JSON.readTree(actual);
      Assertions.assertThat(actualJson).as(message).isEqualTo(expectedJson);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to read JSON data!", e);
    }
  }

  private FhirJsonTestsUtil() {
    // hidden
  }
}
