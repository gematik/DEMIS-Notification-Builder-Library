package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class AnonymousBundleBuilderTest {

  @Test
  void thatExistingAnonymousBundleRemainsUnchanged() throws IOException {
    // GIVEN a bundle with NonNominal profiles and a notified person

    // THEN
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-anonymous.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = AnonymousBundleBuilder.deepCopy(original);

    final String expected =
        Files.readString(Path.of("src/test/resources/laboratory/73-anonymous-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }
}
