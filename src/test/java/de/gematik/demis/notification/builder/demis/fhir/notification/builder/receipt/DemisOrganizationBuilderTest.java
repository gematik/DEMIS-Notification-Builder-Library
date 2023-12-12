/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;

class DemisOrganizationBuilderTest {

  @Test
  void shouldCreateDEMISOrganization() throws IOException {

    Organization demisOrg = new DemisOrganizationBuilder().createDemisOrg();

    String actualJson =
        FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(demisOrg);

    String expectedJson =
        new String(
            getClass()
                .getClassLoader()
                .getResourceAsStream("DemisOrganization.json")
                .readAllBytes(),
            StandardCharsets.UTF_8);

    assertThat(actualJson).isEqualToIgnoringNewLines(expectedJson);
  }
}
