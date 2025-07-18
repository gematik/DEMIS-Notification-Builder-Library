package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_PSEUDONYM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.Year;
import java.time.YearMonth;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotifiedPersonNotByNameDataBuilderTest {

  private NotifiedPersonNotByNameDataBuilder notifiedPersonNotByNameDataBuilder;

  @BeforeEach
  void setUp() {
    notifiedPersonNotByNameDataBuilder = new NotifiedPersonNotByNameDataBuilder();
  }

  @Test
  void shouldSetGender() {
    notifiedPersonNotByNameDataBuilder.setGender(Enumerations.AdministrativeGender.FEMALE);
    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
  }

  @Test
  void shouldSetNotifiedPersonId() {
    notifiedPersonNotByNameDataBuilder.setId("SomeId");

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldSetBirthdateYearPrecision() {
    var enteredDate = "1975";
    notifiedPersonNotByNameDataBuilder.setBirthdate(enteredDate);

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType(enteredDate));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.YEAR);
  }

  @Test
  void shouldSetBirthdateMonthPrecision() {
    var enteredDate = "1975-10";
    notifiedPersonNotByNameDataBuilder.setBirthdate(enteredDate);

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType(enteredDate));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldThrowExceptionIfDayIsEntered() {
    var enteredDate = "1975-10-10";
    assertThatThrownBy(() -> notifiedPersonNotByNameDataBuilder.setBirthdate(enteredDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Birthdate must be in the format yyyy or yyyy-MM");
  }

  @Test
  void shouldAddIdWhenNonIsGiven() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("SomeId");
      Patient patient = notifiedPersonNotByNameDataBuilder.build();
      assertThat(patient.getId()).isEqualTo("SomeId");
    }
  }

  @Test
  void shouldUseGivenId() {
    String id = "init-id";
    IBase resource = new NotifiedPersonByNameDataBuilder().setId(id).build();
    if (resource instanceof Patient patient) {
      assertThat(patient.getId()).isEqualTo("Patient/init-id");
    } else {
      fail("Resource is not a Patient");
    }
  }

  @Test
  void shouldUseSetId() {
    String id = "SomeId";
    notifiedPersonNotByNameDataBuilder.setId(id);

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldSetBirthdateWithYearPrecision() {
    Year year = Year.of(1975);
    notifiedPersonNotByNameDataBuilder.setBirthdate(year);

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType("1975"));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.YEAR);
  }

  @Test
  void shouldSetBirthdateWithYearMonthPrecision() {
    YearMonth yearMonth = YearMonth.of(1975, 10);
    notifiedPersonNotByNameDataBuilder.setBirthdate(yearMonth);

    Patient patient = notifiedPersonNotByNameDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType("1975-10"));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldCopyId() {
    Patient patientToCopy = new Patient();
    patientToCopy.setId("12345");

    final Patient patient = NotifiedPersonNotByNameDataBuilder.deepCopy(patientToCopy);
    assertThat(patient.getId()).isEqualTo("Patient/12345");
  }

  @Test
  void shouldCopyGender() {
    Patient patientToCopy = new Patient();
    patientToCopy.setGender(Enumerations.AdministrativeGender.MALE);

    final Patient patient = NotifiedPersonNotByNameDataBuilder.deepCopy(patientToCopy);
    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.MALE);
  }

  @Test
  void shouldCopyBirthdate() {
    Patient patientToCopy = new Patient();
    patientToCopy.setBirthDateElement(new DateType("1975-10"));

    final Patient patient = NotifiedPersonNotByNameDataBuilder.deepCopy(patientToCopy);
    assertThat(patient.getBirthDateElement().getValueAsString()).isEqualTo("1975-10");
    assertThat(patient.getBirthDateElement().getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldCopyAddress() {
    Patient patientToCopy = new Patient();
    Address address = new Address();
    address.setPostalCode("12345");
    patientToCopy.addAddress(address);

    final Patient patient = NotifiedPersonNotByNameDataBuilder.deepCopy(patientToCopy);
    assertThat(patient.getAddress()).hasSize(1);
    assertThat(patient.getAddress().get(0).getPostalCode()).isEqualTo("123");
  }

  @Test
  void shouldCopyPseudonym() {
    Patient patientToCopy = new Patient();
    Base64BinaryType actualPseudonym = new Base64BinaryType("pseudonym");
    patientToCopy.addExtension(EXTENSION_URL_PSEUDONYM, actualPseudonym);

    final Patient result = NotifiedPersonNotByNameDataBuilder.deepCopy(patientToCopy);
    assertThat(result.getExtensionByUrl(EXTENSION_URL_PSEUDONYM).getValue())
        .isEqualTo(actualPseudonym);
  }
}
