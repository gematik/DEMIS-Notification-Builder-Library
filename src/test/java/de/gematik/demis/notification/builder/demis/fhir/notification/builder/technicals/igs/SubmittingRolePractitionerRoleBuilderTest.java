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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.createOptionalResourceWithId;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.PractitionerRoleBuilder.PractitionerRoleProfile.SUBMITTER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.Test;

class SubmittingRolePractitionerRoleBuilderTest {

  private static final String ORGANIZATION_ID = "a42f380e-b23a-3f50-a096-26665a6e4257";
  private static final String PATH_TO_EXPECTED_PRACTITIONER_ROLE_SUBMITTING_ROLE =
      "src/test/resources/igs/practitionerRoleSubmittingRole.json";

  @Test
  void shouldBuildSubmittingRolePractitionerRole() throws IOException {
    PractitionerRoleBuilder builder = configureBuilderWithTestData();

    PractitionerRole practitionerRole = builder.buildResource().orElseThrow();

    String actualCompositionAsString = getJsonParser().encodeResourceToString(practitionerRole);
    String expectedCompositionAsString =
        Files.readString(Path.of(PATH_TO_EXPECTED_PRACTITIONER_ROLE_SUBMITTING_ROLE));

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedComposition);
  }

  private PractitionerRoleBuilder configureBuilderWithTestData() {
    IgsOverviewData data = IgsOverviewData.builder().build();
    return PractitionerRoleBuilder.builder()
        .data(data)
        .profile(SUBMITTER_ROLE)
        .organizationReference(createOptionalResourceWithId(Organization.class, ORGANIZATION_ID))
        .build();
  }
}
