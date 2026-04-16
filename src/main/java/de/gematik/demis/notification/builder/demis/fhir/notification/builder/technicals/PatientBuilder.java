package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_PSEUDONYM;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Type;

@Setter
public class PatientBuilder implements InitializableFhirObjectBuilder {

  public static final String RESOURCE_TYPE = "Patient";
  private HumanName humanName;
  private Enumerations.AdministrativeGender gender;
  private Extension genderExtension;
  private String id;
  private List<Address> address = new ArrayList<>();
  private DateType birthdate;
  private List<ContactPoint> telecom = new ArrayList<>();
  private String profileUrl;
  private Type pseudonym;
  private Date lastUpdated;
  private Type deceased;
  private List<Extension> extensions;

  /**
   * Adds an additional profile URL to the meta of a patient resource.
   *
   * @param profileUrl the canonical URL of the FHIR profile to add
   * @param patient the patient resource to which the profile URL is added
   */
  public static void addProfileUrlToPatient(String profileUrl, Patient patient) {
    patient.getMeta().addProfile(profileUrl);
  }

  /**
   * Converts a YearMonth to a string in the format "yyyy-MM".
   *
   * @param yearMonth the year-month value to format
   * @return the formatted string, e.g. {@code "1975-10"}
   */
  public static String yearMonthToString(YearMonth yearMonth) {
    return yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
  }

  /**
   * Converts a LocalDate to a YearMonth, discarding the day component.
   *
   * @param date the local date to convert
   * @return the corresponding {@link YearMonth}
   */
  public static YearMonth convertToYearMonth(LocalDate date) {
    return YearMonth.of(date.getYear(), date.getMonth());
  }

  @Override
  public PatientBuilder setDefaults() {
    return this;
  }

  @Override
  public Patient build() {
    Patient patient = new Patient();
    if (profileUrl != null) {
      addProfileUrlToPatient(profileUrl, patient);
    }
    if (humanName != null) {
      patient.addName(humanName);
    }
    if (gender != null) {
      patient.setGender(gender);
    }
    if (genderExtension != null) {
      patient.getGenderElement().addExtension(genderExtension);
    }
    if (id != null) {
      final IdType idType = new IdType(RESOURCE_TYPE, id);
      patient.setId(idType);
    } else {
      patient.setId(Utils.generateUuidString());
    }
    patient.setAddress(address);
    patient.setTelecom(telecom);
    if (birthdate != null) {
      patient.setBirthDateElement(birthdate);
    }
    if (pseudonym != null) {
      patient.addExtension(new Extension(EXTENSION_URL_PSEUDONYM).setValue(pseudonym));
    }
    if (lastUpdated != null) {
      patient.getMeta().setLastUpdated(lastUpdated);
    }
    if (deceased != null) {
      patient.setDeceased(deceased);
    }
    if (extensions != null) {
      patient.setExtension(extensions);
    }
    return patient;
  }
}
