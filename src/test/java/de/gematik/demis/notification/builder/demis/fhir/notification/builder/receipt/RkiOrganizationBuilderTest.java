package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

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

import com.google.common.io.Resources;
import de.gematik.demis.notification.builder.demis.fhir.notification.test.FhirJsonTestsUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;

class RkiOrganizationBuilderTest {

  @Test
  void shouldCreateRKIOrganization() throws IOException {
    Organization organization = new RkiOrganizationBuilder().createRkiOrganization();
    String expectedJson =
        Resources.toString(Resources.getResource("RKIOrganization.json"), StandardCharsets.UTF_8);
    FhirJsonTestsUtil.assertEqualJson(organization, expectedJson, "Built RKI organization as JSON");
  }
}
