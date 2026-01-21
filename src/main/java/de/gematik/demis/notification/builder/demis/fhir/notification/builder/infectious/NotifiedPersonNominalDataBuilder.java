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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

@Setter
@Getter
public class NotifiedPersonNominalDataBuilder {

  private HumanName humanName;
  private Enumerations.AdministrativeGender gender;
  private Extension genderExtension;
  private String id;
  private List<Address> address = new ArrayList<>();
  private DateType birthdate;
  private List<ContactPoint> telecom = new ArrayList<>();
  private String profileUrl;

  public Patient build() {
    return new PatientBuilder()
        .setProfileUrl(profileUrl)
        .setHumanName(humanName)
        .setGender(gender)
        .setGenderExtension(genderExtension)
        .setId(id)
        .setAddress(address)
        .setBirthdate(birthdate)
        .setTelecom(telecom)
        .build();
  }

  public NotifiedPersonNominalDataBuilder addAddress(Address newAddress) {
    this.address.add(newAddress);
    return this;
  }

  public NotifiedPersonNominalDataBuilder addTelecom(ContactPoint newTelecom) {
    telecom.add(newTelecom);
    return this;
  }

  public NotifiedPersonNominalDataBuilder setDefault() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    profileUrl = PROFILE_NOTIFIED_PERSON;
    return this;
  }
}
