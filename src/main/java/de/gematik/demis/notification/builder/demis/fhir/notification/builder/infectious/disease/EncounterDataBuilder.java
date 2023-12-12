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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
@EqualsAndHashCode
public class EncounterDataBuilder {

  private String id;
  private String profileUrl;

  private String hospitalizationNote;
  private String hospitalizationRegionSystem;
  private String hospitalizationRegionCode;
  private String hospitalizationRegionDisplay;
  private String hospitalizationRegionExtensionUrl;

  private String status;
  private String classSystem;
  private String classCode;
  private String classDisplay;

  private String serviceTypeSystem;
  private String serviceTypeCode;
  private String serviceTypeDisplay;

  private String periodStart;
  private String periodEnd;

  public Encounter buildExampleHospitalization(
      Patient notifiedPerson, Organization serviceProvider) {

    status = requireNonNullElse(status, "inprogress");

    classSystem = requireNonNullElse(classSystem, CODE_SYSTEM_ACT_CODE);
    classCode = requireNonNullElse(classCode, "IMP");
    classDisplay = requireNonNullElse(classDisplay, "inpatient encounter");

    profileUrl = requireNonNullElse(profileUrl, PROFILE_HOSPITALIZATION);

    hospitalizationNote = requireNonNullElse(hospitalizationNote, "wichtige Zusatzinformation");

    periodStart = requireNonNullElse(periodStart, "22.01.2022");

    return buildHospitalization(notifiedPerson, serviceProvider);
  }

  public Encounter buildHospitalization(Patient notifiedPerson, Organization serviceProvider) {
    Encounter encounter = new Encounter();

    id = requireNonNullElse(id, generateUuidString());
    encounter.setId(id);

    encounter.setSubject(new Reference(notifiedPerson));
    encounter.setServiceProvider(new Reference(serviceProvider));

    addClassCodingIfSet(encounter);
    addStatusIfSet(encounter);
    addProfileIfSet(encounter);
    addPeriodStartIfSet(encounter);
    addPeriodEndIfSet(encounter);
    addHospitalizationNotIfSet(encounter);
    addHospitalizationRegionIfSet(encounter);
    addServiceTypeCodingIfSet(encounter);

    return encounter;
  }

  private void addServiceTypeCodingIfSet(Encounter encounter) {
    if (isNotBlank(serviceTypeSystem)
        || isNotBlank(serviceTypeCode)
        || isNotBlank(serviceTypeDisplay)) {
      Coding serviceTypeCoding = new Coding(serviceTypeSystem, serviceTypeCode, serviceTypeDisplay);
      encounter.setServiceType(new CodeableConcept(serviceTypeCoding));
    }
  }

  private void addHospitalizationRegionIfSet(Encounter encounter) {
    if (isNotBlank(hospitalizationRegionSystem)
        || isNotBlank(hospitalizationRegionCode)
        || isNotBlank(hospitalizationRegionDisplay)) {
      hospitalizationRegionExtensionUrl =
          requireNonNullElse(
              hospitalizationRegionExtensionUrl, STRUCTURE_DEFINITION_HOSPITALIZATION_REGION);
      Extension t = new Extension();
      t.setUrl(hospitalizationRegionExtensionUrl);
      Coding coding =
          new Coding(
              hospitalizationRegionSystem, hospitalizationRegionCode, hospitalizationRegionDisplay);
      CodeableConcept codeableConcept = new CodeableConcept(coding);
      t.setValue(codeableConcept);
      encounter.addExtension(t);
    }
  }

  private void addHospitalizationNotIfSet(Encounter encounter) {
    if (isNotBlank(hospitalizationNote)) {
      encounter.addExtension(
          new Extension(STRUCTURE_DEFINITION_HOSPITALIZATION_NOTE)
              .setValue(new StringType(hospitalizationNote)));
    }
  }

  private void addPeriodEndIfSet(Encounter encounter) {
    if (this.periodEnd != null) {
      Date value = parse(this.periodEnd);
      if (encounter.getPeriod() == null) {
        encounter.setPeriod(new Period().setEnd(value));
      } else {
        encounter.getPeriod().setEnd(value);
      }
    }
  }

  private static Date parse(String date) {
    try {
      return new SimpleDateFormat("dd.MM.yyyy").parse(date);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Failed to parse date: " + date, e);
    }
  }

  private void addPeriodStartIfSet(Encounter encounter) {
    if (this.periodStart != null) {
      Date value = parse(this.periodStart);
      if (encounter.getPeriod() == null) {
        encounter.setPeriod(new Period().setStart(value));
      } else {
        encounter.getPeriod().setStart(value);
      }
    }
  }

  private void addProfileIfSet(Encounter encounter) {
    if (isNotBlank(profileUrl)) {
      encounter.setMeta(new Meta().addProfile(profileUrl));
    }
  }

  private void addStatusIfSet(Encounter encounter) {
    if (isNotBlank(status)) {
      encounter.setStatus(Encounter.EncounterStatus.valueOf(status.toUpperCase()));
    }
  }

  private void addClassCodingIfSet(Encounter encounter) {
    if (isNotBlank(classSystem) || isNotBlank(classCode) || isNotBlank(classDisplay)) {
      encounter.setClass_(new Coding(classSystem, classCode, classDisplay));
    }
  }
}
