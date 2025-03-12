package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.ADDRESS_USE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ADDRESS_USE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_NOT_BY_NAME;
import static java.lang.String.format;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;

/** Builder for the entry Patient/NotificationId in an IGS NotificationBundleSequence. */
@SuperBuilder
public class PatientBuilder extends AbstractIgsResourceBuilder<Patient> {

  private static final String ADDRESS_CODING_CODE = "primary";

  @Override
  public Optional<Patient> buildResource() {
    Patient patient = new Patient();
    patient.setMeta(
        MetaBuilder.builder()
            .metaProfile(PROFILE_NOTIFIED_PERSON_NOT_BY_NAME)
            .initialize()
            .build());
    patient.setId(UUID.randomUUID().toString());
    setAddress(patient);
    setBirthdate(patient);
    patient.setGender(AdministrativeGender.fromCode(data.getHostSex()));
    return Optional.of(patient);
  }

  private void setBirthdate(Patient patient) {
    if (StringUtils.isNotBlank(data.getHostBirthMonth())
        && StringUtils.isNotBlank(data.getHostBirthYear())) {
      int month = Integer.parseInt(data.getHostBirthMonth());
      String formattedMonth = String.format("%02d", month);
      patient.setBirthDateElement(
          new DateType(format("%s-%s", data.getHostBirthYear(), formattedMonth)));
    }
  }

  private void setAddress(Patient patient) {
    if (StringUtils.isBlank(data.getGeographicLocation())) {
      return;
    }
    Address address = new Address();
    address.setPostalCode(data.getGeographicLocation());
    address.setExtension(buildAddressExtensions());
    patient.setAddress(List.of(address));
  }

  private List<Extension> buildAddressExtensions() {
    Extension extension = new Extension();
    Coding coding = new Coding();
    coding.setSystem(CODE_SYSTEM_ADDRESS_USE);
    coding.setCode(ADDRESS_CODING_CODE);
    extension.setUrl(ADDRESS_USE_URL);
    extension.setValue(coding);
    return List.of(extension);
  }
}
