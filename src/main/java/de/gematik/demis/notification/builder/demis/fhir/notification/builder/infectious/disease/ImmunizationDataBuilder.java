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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Calendar;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

/** still missing are performer and reason code */
@Setter
public class ImmunizationDataBuilder {

  private String id;
  private String profileUrl;
  private String status;
  private String vaccineCodeSystem;
  private String vaccineCodeCode;
  private String vaccineCodeDisplay;
  private DateTimeType occurrenceDateTimeType;
  private List<String> noteText;

  private String lotNumber;

  public Immunization buildExampleImmunization(Patient notifiedPerson) {

    id = requireNonNullElse(id, generateUuidString());
    profileUrl = requireNonNullElse(profileUrl, PROFILE_IMMUNIZATION_INFORMATION_CVDD);
    status = requireNonNullElse(status, "completed");
    vaccineCodeSystem =
        requireNonNullElse(
            vaccineCodeSystem, "https://ec.europa.eu/health/documents/community-register/html/");
    vaccineCodeCode = requireNonNullElse(vaccineCodeCode, "EU/1/20/1528");
    vaccineCodeDisplay = requireNonNullElse(vaccineCodeDisplay, "Comirnaty");
    Calendar cal = Calendar.getInstance();
    cal.set(2022, 01, 01);
    occurrenceDateTimeType =
        requireNonNullElse(occurrenceDateTimeType, new DateTimeType(cal.getTime()));

    return buildImmunization(notifiedPerson);
  }

  private Immunization buildImmunization(Patient notifiedPerson) {
    Immunization immunization = new Immunization();

    addIdIfSet(immunization);
    addStatusIfSet(immunization);
    addVaccineCodeIfSet(immunization);
    setLotNumberIfSet(immunization);
    setOccurrenceDateTimeIfSet(immunization);

    immunization.setMeta(new Meta().addProfile(profileUrl));
    immunization.setPatient(new Reference(notifiedPerson));

    return immunization;
  }

  private void setOccurrenceDateTimeIfSet(Immunization immunization) {
    if (occurrenceDateTimeType != null) {
      immunization.setOccurrence(occurrenceDateTimeType);
    }
  }

  private void setLotNumberIfSet(Immunization immunization) {
    if (isNotBlank(lotNumber)) {
      immunization.setLotNumber(lotNumber);
    }
  }

  private void addVaccineCodeIfSet(Immunization immunization) {
    if (isNotBlank(vaccineCodeCode)
        || isNotBlank(vaccineCodeSystem)
        || isNotBlank(vaccineCodeDisplay)) {
      immunization.setVaccineCode(
          new CodeableConcept(new Coding(vaccineCodeSystem, vaccineCodeCode, vaccineCodeDisplay)));
    }
  }

  private void addStatusIfSet(Immunization immunization) {
    if (isNotBlank(status)) {
      immunization.setStatus(Immunization.ImmunizationStatus.valueOf(status.toUpperCase()));
    }
  }

  private void addIdIfSet(Immunization immunization) {
    if (isNotBlank(id)) {
      immunization.setId(id);
    }
  }
}
