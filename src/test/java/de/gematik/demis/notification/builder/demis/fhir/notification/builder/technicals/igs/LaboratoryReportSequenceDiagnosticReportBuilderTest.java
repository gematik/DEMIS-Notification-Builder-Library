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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsTestDataUtils.createOptionalResourceWithId;
import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class LaboratoryReportSequenceDiagnosticReportBuilderTest {

  private static final String SUBJECT_ID = "3661f735-d3db-3c22-909d-3a16a7334c1d";
  private static final String OBSERVATION_ID = "30aefd63-f71c-39b2-9191-8c162310c9c2";
  private static final String PATH_TO_EXPECTED_DIAGNOSTIC_REPORT_LABORATORY_REPORT_SEQUENCE =
      "src/test/resources/igs/diagnostricReportLaboratoryReportSequence.json";

  @Test
  void shouldBuildLaboratoryReportSequenceDiagnosticReport() throws IOException {
    LaboratoryReportSequenceDiagnosticReportBuilder builder = configureBuilderWithTestData();

    DiagnosticReport diagnosticReport = builder.buildResource().orElseThrow();

    String diagnosticReportAsString = getJsonParser().encodeResourceToString(diagnosticReport);
    String expectedDiagnosticReportAsString =
        Files.readString(
            Path.of(PATH_TO_EXPECTED_DIAGNOSTIC_REPORT_LABORATORY_REPORT_SEQUENCE),
            StandardCharsets.UTF_8);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualDiagnosticReport = objectMapper.readTree(diagnosticReportAsString);
    JsonNode expectedDiagnosticReport = objectMapper.readTree(expectedDiagnosticReportAsString);

    assertThat(actualDiagnosticReport)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .ignoringFields("_children.issued._value")
        .isEqualTo(expectedDiagnosticReport);
  }

  private LaboratoryReportSequenceDiagnosticReportBuilder configureBuilderWithTestData() {

    IgsOverviewData data = IgsOverviewData.builder().meldetatbestand("cvdp").build();
    return LaboratoryReportSequenceDiagnosticReportBuilder.builder()
        .subjectReference(createOptionalResourceWithId(Patient.class, SUBJECT_ID))
        .resultReference(createOptionalResourceWithId(Observation.class, OBSERVATION_ID))
        .data(data)
        .build();
  }
}
