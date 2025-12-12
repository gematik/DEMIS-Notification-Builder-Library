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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import java.time.LocalDate;
import java.time.YearMonth;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class PatientBuilderTest {

  @Test
  void shouldSetIdWithResourceType() {
    final PatientBuilder patientBuilder = new PatientBuilder();
    patientBuilder.setId("test");
    final Patient actual = patientBuilder.build();
    assertThat(actual.getId()).isEqualTo("Patient/test");
  }

  @Test
  void shouldConvertYearMonthToString() {
    YearMonth yearMonth = YearMonth.of(2023, 5);
    String result = PatientBuilder.yearMonthToString(yearMonth);
    assertThat(result).isEqualTo("2023-05");
  }

  @Test
  void shouldConvertLocalDateToYearMonth() {
    LocalDate date = LocalDate.of(2023, 5, 15);
    YearMonth result = PatientBuilder.convertToYearMonth(date);
    assertThat(result).isEqualTo(YearMonth.of(2023, 5));
  }

  @Test
  void shouldHandleGenderOtherAndAddExtension() {
    final PatientBuilder patientBuilder = new PatientBuilder();
    patientBuilder.setGender(Enumerations.AdministrativeGender.OTHER);

    final Patient actual = patientBuilder.build();

    FhirContext fhirContext = FhirContext.forR4Cached();
    String actualAsString = fhirContext.newXmlParser().encodeResourceToString(actual);

    assertThat(actualAsString)
        .containsIgnoringWhitespaces(
            """
                  <gender value="other">
                      <extension url="http://fhir.de/StructureDefinition/gender-amtlich-de">
                          <valueCoding>
                              <system value="http://fhir.de/CodeSystem/gender-amtlich-de"></system>
                              <code value="D"></code>
                                <display value="divers"></display>
                          </valueCoding>
                      </extension>
                  </gender>""");
  }
}
