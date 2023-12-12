/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;

@Setter
public class NotifiedPersonDataBuilder {

  private HumanName humanName;
  private AdministrativeGender gender;
  private String id;
  private List<Address> address = new ArrayList<>();
  private Date birthdate;
  private List<ContactPoint> telecom = new ArrayList<>();

  /**
   * @deprecated use {@link #build()} instead.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public Patient buildNotifiedPerson() {
    id = requireNonNullElse(id, generateUuidString());
    return build();
  }

  public void setDefaultValues() {
    id = generateUuidString();
  }

  public Patient build() {

    Patient newPatient = new Patient();

    newPatient.addName(humanName);
    newPatient.setGender(gender);
    newPatient.setId(id);
    newPatient.setBirthDate(this.birthdate);
    newPatient.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFIED_PERSON));
    newPatient.setTelecom(telecom);

    newPatient.setAddress(address);

    return newPatient;
  }

  /**
   * @deprecated use {@link #addAddress(Address)} instead. please add any extension for object of
   *     type Address through {@link AddressDataBuilder} methods.
   * @param newAddress
   * @param addressUseSystem
   * @param addressUseCode
   * @param addressUseDisplay
   * @return
   */
  @Deprecated(since = "1.2.1")
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

  /**
   * @deprecated use {@link #build()} instead. no examples will be provided in future.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public Patient buildExampleNotifiedPerson() {

    humanName = requireNonNullElse(humanName, new HumanNameDataBuilder().buildExampleHumanName());
    gender = requireNonNullElse(gender, AdministrativeGender.OTHER);
    LocalDate birthdateAsLocalDate = LocalDate.of(1980, 1, 1);
    this.birthdate =
        requireNonNullElse(
            this.birthdate,
            Date.from(birthdateAsLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

    if (address.isEmpty()) {
      addAddress(
          new AddressDataBuilder().buildExampleAddress(),
          "https://demis.rki.de/fhir/CodeSystem/addressUse",
          "primary",
          "Hauptwohnsitz");
    }

    return buildNotifiedPerson();
  }
}
