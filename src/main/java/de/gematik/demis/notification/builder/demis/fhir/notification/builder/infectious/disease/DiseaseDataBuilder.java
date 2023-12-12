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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.*;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class DiseaseDataBuilder {

  private String id;
  private String profileUrl;
  private String verificationStatusSystem;
  private String verificationStatusCode;
  private String verificationStatusDisplay;
  private String diseaseCodeSystem;
  private String diseaseCode;
  private String diseaseCodeDispay;
  private String recordedDate;

  public Condition buildDisease(Patient notifiedPerson) {
    Condition disease = new Condition();

    id = requireNonNullElse(id, generateUuidString());
    disease.setId(id);

    addProfileIfSet(disease);
    addVerificationStatus(disease);
    addCode(disease);

    disease.setRecordedDateElement(new DateTimeType(recordedDate));

    disease.setSubject(new Reference(notifiedPerson));

    return disease;
  }

  private void addCode(Condition disease) {
    disease.setCode(
        new CodeableConcept(new Coding(diseaseCodeSystem, diseaseCode, diseaseCodeDispay)));
  }

  private void addVerificationStatus(Condition disease) {
    disease.setVerificationStatus(
        new CodeableConcept(
            new Coding(
                verificationStatusSystem, verificationStatusCode, verificationStatusDisplay)));
  }

  private void addProfileIfSet(Condition disease) {
    if (isNotBlank(profileUrl)) {
      disease.setMeta(new Meta().addProfile(profileUrl));
    }
  }

  public Condition buildCVDDexampleDisease(Patient notifiedPerson) {

    profileUrl = requireNonNullElse(profileUrl, PROFILE_DISEASE_CVDD);
    verificationStatusSystem =
        requireNonNullElse(verificationStatusSystem, CODE_SYSTEM_CONDITION_VERIFICATION_STATUS);
    verificationStatusCode = requireNonNullElse(verificationStatusCode, "confirmed");

    diseaseCodeSystem =
        requireNonNullElse(diseaseCodeSystem, CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY);
    diseaseCode = requireNonNullElse(diseaseCode, "cvdd");
    diseaseCodeDispay =
        requireNonNullElse(diseaseCodeDispay, "Coronavirus-Krankheit-2019 (COVID-19)");

    return buildDisease(notifiedPerson);
  }
}
