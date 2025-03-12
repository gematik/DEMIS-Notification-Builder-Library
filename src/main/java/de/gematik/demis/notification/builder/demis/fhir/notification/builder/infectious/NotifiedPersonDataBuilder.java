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
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.DateTypeBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;

/**
 * @deprecated Use {@link NotifiedPersonNotByNameDataBuilder}, {@link
 *     NotifiedPersonAnonymousDataBuilder}, {@link NotifiedPersonByNameDataBuilder} instead.
 */
@Deprecated(forRemoval = true)
@Setter
public class NotifiedPersonDataBuilder implements InitializableFhirObjectBuilder {

  private HumanName humanName;
  private AdministrativeGender gender;
  private String id;
  private List<Address> address = new ArrayList<>();
  private DateType birthdate;
  private List<ContactPoint> telecom = new ArrayList<>();

  private static void addProfileUrl(Patient newPatient) {
    newPatient.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFIED_PERSON));
  }

  @Override
  public NotifiedPersonDataBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    return this;
  }

  @Override
  public Patient build() {
    Patient newPatient = new Patient();
    newPatient.addName(humanName);
    newPatient.setGender(gender);
    addId(newPatient);
    addBirthdate(newPatient);
    addProfileUrl(newPatient);
    newPatient.setTelecom(telecom);
    newPatient.setAddress(address);
    return newPatient;
  }

  private void addBirthdate(Patient newPatient) {
    newPatient.setBirthDateElement(this.birthdate);
  }

  private void addId(Patient newPatient) {
    newPatient.setId(Objects.requireNonNullElseGet(this.id, Utils::generateUuidString));
  }

  /**
   * @param newAddress
   * @param addressUseSystem
   * @param addressUseCode
   * @param addressUseDisplay
   * @return
   * @deprecated use {@link #addAddress(Address)} instead. please add any extension for object of
   *     type Address through {@link AddressDataBuilder} methods.
   */
  @Deprecated(since = "1.2.1", forRemoval = true)
  public NotifiedPersonDataBuilder addAddress(
      Address newAddress,
      String addressUseSystem,
      String addressUseCode,
      String addressUseDisplay) {
    Extension addressUseExtension = new Extension();
    addressUseExtension.setUrl("https://demis.rki.de/fhir/StructureDefinition/AddressUse");
    Coding addressUseExtensionCode =
        new Coding(addressUseSystem, addressUseCode, addressUseDisplay);
    addressUseExtension.setValue(addressUseExtensionCode);
    newAddress.addExtension(addressUseExtension);
    this.address.add(newAddress);
    return this;
  }

  public NotifiedPersonDataBuilder addAddress(Address newAddress) {
    this.address.add(newAddress);
    return this;
  }

  public NotifiedPersonDataBuilder addTelecom(ContactPoint newTelecom) {
    telecom.add(newTelecom);
    return this;
  }

  public NotifiedPersonDataBuilder setBirthdate(Date birthdate) {
    setBirthdate(new DateType(birthdate));
    return this;
  }

  public NotifiedPersonDataBuilder setBirthdate(DateType birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public NotifiedPersonDataBuilder setBirthdate(String birthdate) {
    setBirthdate(new DateTypeBuilder().setText(birthdate).build());
    return this;
  }

  /**
   * @return
   * @deprecated use {@link #build()} instead. no examples will be provided in future.
   */
  @Deprecated(since = "1.2.1", forRemoval = true)
  public Patient buildExampleNotifiedPerson() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    if (this.humanName == null) {
      setHumanName(new HumanNameDataBuilder().buildExampleHumanName());
    }
    if (this.gender == null) {
      setGender(AdministrativeGender.OTHER);
    }
    if (this.birthdate == null) {
      LocalDate birthdateAsLocalDate = LocalDate.of(1980, 1, 1);
      setBirthdate(
          Date.from(birthdateAsLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
    if (address.isEmpty()) {
      addAddress(
          new AddressDataBuilder().buildExampleAddress(),
          "https://demis.rki.de/fhir/CodeSystem/addressUse",
          "primary",
          "Hauptwohnsitz");
    }
    return build();
  }
}
