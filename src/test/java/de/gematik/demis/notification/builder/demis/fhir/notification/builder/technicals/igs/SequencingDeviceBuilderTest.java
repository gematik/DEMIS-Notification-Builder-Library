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

import static de.gematik.demis.notification.builder.demis.fhir.testUtils.TestFhirParser.getJsonParser;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Device;
import org.junit.jupiter.api.Test;

class SequencingDeviceBuilderTest {

  private static final String DEVICE_NAME_NAME = "GridION";
  private static final String PATH_TO_SEQUENCING_DEVICE =
      "src/test/resources/igs/sequencingDevice.json";
  public static final String SEQUENCING_PLATFORM = "oxford_nanopore";

  @Test
  void shouldBuildSequencingDevice() throws IOException {
    SequencingDeviceBuilder builder = configureBuilderWithTestData();

    Device device = builder.buildResource().orElseThrow();

    String deviceAsString = getJsonParser().encodeResourceToString(device);
    String expectedDeviceAsString = Files.readString(Path.of(PATH_TO_SEQUENCING_DEVICE));

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode actualDevice = objectMapper.readTree(deviceAsString);
    JsonNode expectedDevice = objectMapper.readTree(expectedDeviceAsString);

    assertThat(actualDevice)
        .usingRecursiveComparison()
        .ignoringFields("_children.id._value")
        .isEqualTo(expectedDevice);
  }

  private SequencingDeviceBuilder configureBuilderWithTestData() {
    IgsOverviewData data =
        IgsOverviewData.builder()
            .sequencingInstrument(DEVICE_NAME_NAME)
            .sequencingPlatform(SEQUENCING_PLATFORM)
            .build();
    return SequencingDeviceBuilder.builder().data(data).build();
  }
}
