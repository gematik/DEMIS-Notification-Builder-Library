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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder.yearMonthToString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_PSEUDONYM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_NOT_BY_NAME;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SequencedCollection;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Type;

@Setter
public class NotifiedPersonNotByNameDataBuilder {

  private String id;
  private DateType birthdate;
  private Enumerations.AdministrativeGender gender;
  private List<Address> address = new ArrayList<>();

  @Setter(AccessLevel.PRIVATE)
  private Type pseudonym;

  public static Patient deepCopy(@Nonnull final Patient patientToCopy) {
    final NotifiedPersonNotByNameDataBuilder builder = new NotifiedPersonNotByNameDataBuilder();
    final SequencedCollection<Address> addresses =
        AddressDataBuilder.copyOfRedactedAddress(patientToCopy.getAddress());
    builder.setAddress(List.copyOf(addresses));
    if (patientToCopy.hasBirthDateElement()) {
      copyBirthdate(patientToCopy, builder);
    }
    builder.setGender(patientToCopy.getGender());
    final IIdType idElement = patientToCopy.getIdElement();
    builder.setId(idElement.getIdPart());
    final Extension extensionByUrl = patientToCopy.getExtensionByUrl(EXTENSION_URL_PSEUDONYM);
    final Type pseudonym = extensionByUrl == null ? null : extensionByUrl.getValue();
    builder.setPseudonym(pseudonym);

    return builder.build();
  }

  private static void copyBirthdate(
      final Patient patientToCopy, final NotifiedPersonNotByNameDataBuilder builder) {
    final DateType birthDateElement = patientToCopy.getBirthDateElement();
    final TemporalPrecisionEnum precision = birthDateElement.getPrecision();
    if (TemporalPrecisionEnum.YEAR.equals(precision)
        || TemporalPrecisionEnum.MONTH.equals(precision)) {
      builder.setBirthdate(birthDateElement.getValueAsString());
    } else {
      final Date birthdate = birthDateElement.getValue();
      // SonarQube prevents us from making this a static variable, and using it as instance variable
      // breaks the code, so we just generate a new object for this case
      builder.setBirthdate(new SimpleDateFormat("yyyy-MM").format(birthdate));
    }
  }

  private void addAddress(Address address) {
    this.address.add(address);
  }

  public Patient build() {
    return new PatientBuilder()
        .setProfileUrl(PROFILE_NOTIFIED_PERSON_NOT_BY_NAME)
        .setId(id)
        .setBirthdate(birthdate)
        .setGender(gender)
        .setAddress(address)
        .setPseudonym(pseudonym)
        .build();
  }

  public NotifiedPersonNotByNameDataBuilder setBirthdate(Year year) {
    this.birthdate = new DateType();
    this.birthdate.setValueAsString(year.toString());
    this.birthdate.setPrecision(TemporalPrecisionEnum.YEAR);
    return this;
  }

  public NotifiedPersonNotByNameDataBuilder setBirthdate(YearMonth yearMonth) {
    this.birthdate = new DateType();
    this.birthdate.setValueAsString(yearMonthToString(yearMonth));
    this.birthdate.setPrecision(TemporalPrecisionEnum.MONTH);
    return this;
  }

  /**
   * <b>NOTE:</b> You probably want to use {@link
   * NotifiedPersonNotByNameDataBuilder#setBirthdate(YearMonth)} which won't generate exceptions.
   *
   * <p>Attempt to parse a birthdate from the given string.
   *
   * @param birthdate in the format yyyy or yyyy-MM
   * @throws IllegalArgumentException If the date is in a format other than yyyy-MM or yyyy.
   */
  public NotifiedPersonNotByNameDataBuilder setBirthdate(String birthdate) {
    this.birthdate = new DateType();
    TemporalPrecisionEnum precision;
    if (birthdate == null) {
      return this;
    }

    if (birthdate.length() == 4) {
      precision = TemporalPrecisionEnum.YEAR;
      this.birthdate.setValueAsString(birthdate);
    } else if (birthdate.length() == 7) {
      precision = TemporalPrecisionEnum.MONTH;
      this.birthdate.setValueAsString(birthdate + "-01");
    } else {
      throw new IllegalArgumentException("Birthdate must be in the format yyyy or yyyy-MM");
    }
    this.birthdate.setPrecision(precision);
    return this;
  }
}
