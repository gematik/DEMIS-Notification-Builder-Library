package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateTimeTypeBuilderTest {

  @ParameterizedTest
  @CsvSource({
    "2001,2001",
    "07.2001,2001-07",
    "2001-07,2001-07",
    "01.02.2003,2003-02-01",
    "29.02.2024,2024-02-29",
    "2024-02-29,2024-02-29",
    "01.02.2003 04:05,2003-02-01T04:05",
    "29.02.2020 16:42,2020-02-29T16:42",
    "2020-02-29T16:42:00,2020-02-29T16:42:00",
  })
  void shouldBuildDateTimeType(String input, String expected) {
    DateTimeType actual = new DateTimeTypeBuilder().setText(input).build();
    assertThat(actual.asStringValue()).as("FHIR date time ISO 8601 printout").isEqualTo(expected);
  }

  @Test
  void shouldBeNullSafe() {
    assertThat(new DateTimeTypeBuilder().setText(null).build())
        .as("null input produces null output")
        .isNull();
  }
}
