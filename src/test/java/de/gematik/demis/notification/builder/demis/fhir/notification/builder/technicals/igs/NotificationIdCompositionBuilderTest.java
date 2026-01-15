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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsConstants.LOINC_VERSION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.createOptionalResourceWithId;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.parseDateByDateTimeString;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.Test;

class NotificationIdCompositionBuilderTest {

  private static final String DEMIS_NOTIFICATION_ID = "f8585efb-1872-4a4f-b88d-8c889e93487b";
  private static final String SUBJECT_ID = "3661f735-d3db-3c22-909d-3a16a7334c1d";
  private static final String DATE_TIME = "2023-01-14T08:16:00+01:00";
  private static final String SECTION_ENTRY_ID = "2e24f26e-c450-301c-ac1e-da2205a69e39";
  private static final String AUTHOR_ID = "c92c9646-e0c8-3c3d-840c-33e505648a5c";
  private static final String PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID =
      "src/test/resources/igs/compositionNotificationId.json";
  public static final String STATUS = "final";

  @Test
  void shouldBuildNotificationIdComposition() throws IOException {
    NotificationIdCompositionBuilder builder = configureBuilderWithTestData();

    Composition composition = builder.buildResource().orElseThrow();

    String actualCompositionAsString = getJsonParser().encodeResourceToString(composition);
    String expectedCompositionAsString =
        Files.readString(
            Path.of(PATH_TO_EXPECTED_COMPOSITION_NOTIFICATION_ID), StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualComposition = objectMapper.readTree(actualCompositionAsString);
    JsonNode expectedComposition = objectMapper.readTree(expectedCompositionAsString);

    assertThat(actualComposition)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .ignoringFields("_children.date._value")
        .isEqualTo(expectedComposition);
  }

  private NotificationIdCompositionBuilder configureBuilderWithTestData() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .demisNotificationId(DEMIS_NOTIFICATION_ID)
            .dateOfSubmission(parseDateByDateTimeString(DATE_TIME))
            .status(STATUS)
            .build();
    return NotificationIdCompositionBuilder.builder()
        .subjectReference(createOptionalResourceWithId(Patient.class, SUBJECT_ID))
        .authorReference(createOptionalResourceWithId(PractitionerRole.class, AUTHOR_ID))
        .sectionEntryReference(
            createOptionalResourceWithId(DiagnosticReport.class, SECTION_ENTRY_ID))
        .data(data)
        .loincVersion(LOINC_VERSION)
        .build();
  }
}
