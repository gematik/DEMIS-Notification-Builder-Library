package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_GENDER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_PSEUDONYM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_NOT_BY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.types.AddressUse;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.SequencedCollection;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotifiedPersonNonNominalDataBuilderTest {

  private NotifiedPersonNonNominalDataBuilder notifiedPersonNonNominalDataBuilder;

  @BeforeEach
  void setUp() {
    notifiedPersonNonNominalDataBuilder = new NotifiedPersonNonNominalDataBuilder();
  }

  @Test
  void shouldSetGender() {
    notifiedPersonNonNominalDataBuilder.setGender(Enumerations.AdministrativeGender.FEMALE);
    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
  }

  @Test
  void shouldSetNotifiedPersonId() {
    notifiedPersonNonNominalDataBuilder.setId("SomeId");

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldSetBirthdateYearPrecision() {
    var enteredDate = "1975";
    notifiedPersonNonNominalDataBuilder.setBirthdate(enteredDate);

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType(enteredDate));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.YEAR);
  }

  @Test
  void shouldSetBirthdateMonthPrecision() {
    var enteredDate = "1975-10";
    notifiedPersonNonNominalDataBuilder.setBirthdate(enteredDate);

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType(enteredDate));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldThrowExceptionIfDayIsEntered() {
    var enteredDate = "1975-10-10";
    assertThatThrownBy(() -> notifiedPersonNonNominalDataBuilder.setBirthdate(enteredDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Birthdate must be in the format yyyy or yyyy-MM");
  }

  @Test
  void shouldAddIdWhenNonIsGiven() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("SomeId");
      Patient patient = notifiedPersonNonNominalDataBuilder.build();
      assertThat(patient.getId()).isEqualTo("SomeId");
    }
  }

  @Test
  void shouldUseGivenId() {
    String id = "init-id";
    IBase resource = new NotifiedPersonNominalDataBuilder().setId(id).build();
    if (resource instanceof Patient patient) {
      assertThat(patient.getId()).isEqualTo("Patient/init-id");
    } else {
      fail("Resource is not a Patient");
    }
  }

  @Test
  void shouldUseSetId() {
    String id = "SomeId";
    notifiedPersonNonNominalDataBuilder.setId(id);

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    assertThat(patient.getId()).isEqualTo("Patient/SomeId");
  }

  @Test
  void shouldSetBirthdateWithYearPrecision() {
    Year year = Year.of(1975);
    notifiedPersonNonNominalDataBuilder.setBirthdate(year);

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType("1975"));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.YEAR);
  }

  @Test
  void shouldSetBirthdateWithYearMonthPrecision() {
    YearMonth yearMonth = YearMonth.of(1975, 10);
    notifiedPersonNonNominalDataBuilder.setBirthdate(yearMonth);

    Patient patient = notifiedPersonNonNominalDataBuilder.build();

    DateType actualDate = patient.getBirthDateElement();
    assertThat(actualDate).usingRecursiveComparison().isEqualTo(new DateType("1975-10"));
    assertThat(actualDate.getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldCopyId() {
    Patient patientToCopy = new Patient();
    patientToCopy.setId("12345");

    final Patient patient = NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of());
    assertThat(patient.getId()).isEqualTo("Patient/12345");
  }

  @Test
  void shouldCopyGender() {
    Patient patientToCopy = new Patient();
    patientToCopy.setGender(Enumerations.AdministrativeGender.MALE);

    final Patient patient = NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of());
    assertThat(patient.getGender()).isEqualTo(Enumerations.AdministrativeGender.MALE);
  }

  @Test
  void shouldCopyBirthdate() {
    Patient patientToCopy = new Patient();
    patientToCopy.setBirthDateElement(new DateType("1975-10"));

    final Patient patient = NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of());
    assertThat(patient.getBirthDateElement().getValueAsString()).isEqualTo("1975-10");
    assertThat(patient.getBirthDateElement().getPrecision()).isEqualTo(TemporalPrecisionEnum.MONTH);
  }

  @Test
  void shouldCopyAddress() {
    Patient patientToCopy = new Patient();
    Address address = new Address();
    address.setPostalCode("12345");
    patientToCopy.addAddress(address);

    final Patient patient =
        NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of(address));
    assertThat(patient.getAddress()).hasSize(1);
    assertThat(patient.getAddress().get(0).getPostalCode()).isEqualTo("123");
  }

  @Test
  void thatOnlyAddressesPassedAsParameterAreCopied() {
    // GIVEN a patient with an assigned address
    final Patient patientToCopy = new Patient().addAddress(new Address().setPostalCode("12345"));

    // WHEN we copy without passing the address
    final Patient patient = NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of());

    // THEN no address is present
    assertThat(patient.getAddress()).isEmpty();
  }

  @Test
  void shouldCopyPseudonym() {
    Patient patientToCopy = new Patient();
    Base64BinaryType actualPseudonym = new Base64BinaryType("pseudonym");
    patientToCopy.addExtension(EXTENSION_URL_PSEUDONYM, actualPseudonym);

    final Patient result = NotifiedPersonNonNominalDataBuilder.deepCopy(patientToCopy, List.of());
    assertThat(result.getExtensionByUrl(EXTENSION_URL_PSEUDONYM).getValue())
        .isEqualTo(actualPseudonym);
  }

  @Nested
  class GetAddressesToCopy {
    @Test
    void thatAddressesAreClonedToAvoidMutatingSharedReferences() {
      final Address primary =
          new AddressDataBuilder()
              .withAddressUseExtension(AddressUse.PRIMARY)
              .setPostalCode("12345")
              .build();
      primary.setId("test-id-keep");

      final Patient toCopy = new Patient().addAddress(primary);

      final SequencedCollection<Address> addressesToCopy =
          NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(toCopy);
      assertThat(addressesToCopy)
          .doesNotContain(primary) // can't find it with equals due to copy
          .extracting(Element::getId)
          .containsExactly("test-id-keep")
          .hasSize(1); // but we still have one address
    }

    @Test
    void thatCurrentAddressesAreNotReturned() {
      final Address toIgnore =
          new AddressDataBuilder()
              .withAddressUseExtension(AddressUse.CURRENT)
              .setPostalCode("12345")
              .build();
      toIgnore.setId("test-id-ignore");

      final Address toKeep =
          new AddressDataBuilder()
              .withAddressUseExtension(AddressUse.PRIMARY)
              .setPostalCode("12345")
              .build();
      toKeep.setId("test-id-keep");

      final Patient toCopy = new Patient().addAddress(toIgnore).addAddress(toKeep);

      final SequencedCollection<Address> addressesToCopy =
          NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(toCopy);
      assertThat(addressesToCopy)
          .extracting((Element::getId))
          .containsExactlyInAnyOrder("test-id-keep");
    }

    @Test
    void thatCurrentIsRemovedFromAddressWithMixedExtension() {
      final Address mixedExtension =
          new AddressDataBuilder()
              .withAddressUseExtension(AddressUse.CURRENT)
              .withAddressUseExtension(AddressUse.PRIMARY)
              .setPostalCode("12345")
              .build();

      final Patient toCopy = new Patient().addAddress(mixedExtension);

      final SequencedCollection<Address> addressesToCopy =
          NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(toCopy);
      final Address first = addressesToCopy.getFirst();
      assertThat(first.getExtension())
          .extracting(
              extension -> {
                final Type value = extension.getValue();
                if (value instanceof Coding code) {
                  return code.getCode();
                }
                return "";
              })
          .contains("primary");
    }
  }

  @Test
  void thatNotifiedPersonFacilityIsRemoved() {
    final Organization remove =
        new OrganizationBuilder().addNotifiedPersonFacilityProfile().build();
    remove.setId("test-id-remove");
    final Organization keep = new Organization();
    keep.setId("test-id-keep");

    final Address mixedExtension =
        new AddressDataBuilder()
            .withAddressUseExtension(AddressUse.PRIMARY)
            .withOrganizationReferenceExtension(remove)
            .withOrganizationReferenceExtension(keep)
            .setPostalCode("12345")
            .build();

    final Patient toCopy = new Patient().addAddress(mixedExtension);

    final SequencedCollection<Address> addressesToCopy =
        NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(toCopy);
    final Address first = addressesToCopy.getFirst();
    assertThat(first.getExtension())
        .extracting(
            extension -> {
              final Type value = extension.getValue();
              if (value instanceof Coding code) {
                return code.getCode();
              }
              if (value instanceof Reference ref) {
                return ref.getResource().getIdElement().getValue();
              }

              return "";
            })
        .containsExactly("primary", "test-id-keep");
  }

  @Test
  void createNonNominalPatientForExcerpt() {
    final Patient patient = new Patient();
    patient.setId("67890");
    patient.setGender(Enumerations.AdministrativeGender.OTHER);
    final Extension genderExtension = new Extension(EXTENSION_URL_GENDER);
    genderExtension.setValue(new Coding(EXTENSION_URL_GENDER, "X", "Kein Geschlechtseintrag"));
    patient.getGenderElement().addExtension(genderExtension);
    patient.setBirthDateElement(new DateType("1980-01-01"));
    patient.addName().setFamily("Muster").addGiven("Max");
    patient.addAddress().setCity("Berlin").setPostalCode("12345");
    final String pseudonymExtensionUrl =
        "https://demis.rki.de/fhir/StructureDefinition/PseudonymRecordType";
    patient.addExtension().setUrl(pseudonymExtensionUrl).setValue(new StringType("SomeValue"));
    final Patient anonymousPatient =
        NotifiedPersonNonNominalDataBuilder.createExcerptNotByNamePatient(patient);
    assertThat(anonymousPatient.getMeta().getProfile().getFirst().getValueAsString())
        .isEqualTo(PROFILE_NOTIFIED_PERSON_NOT_BY_NAME);
    assertThat(anonymousPatient.getAddress().getFirst().getPostalCode()).isEqualTo("123");
    assertThat(anonymousPatient.getAddress().getFirst().getCity()).isNull();
    assertThat(anonymousPatient.getGender()).isEqualTo(Enumerations.AdministrativeGender.OTHER);
    assertThat(anonymousPatient.getGenderElement().getExtensionByUrl(EXTENSION_URL_GENDER))
        .isEqualTo(genderExtension);
    assertThat(anonymousPatient.getName()).isEmpty();
    assertThat(anonymousPatient.getBirthDateElement().getValueAsString()).isEqualTo("1980-01");
    assertThat(anonymousPatient.getExtensionByUrl(pseudonymExtensionUrl).getValue())
        .hasToString("SomeValue");
  }
}
