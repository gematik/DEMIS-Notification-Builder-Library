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
public class NotificationDiseaseDataBuilder {

  private String id;
  private String profileUrl;
  private String status;
  private String typeSystem;
  private String typeCode;
  private String typeDisplay;
  private String title;
  private DateTimeType date;
  private String diseaseSystem;
  private String diseaseCode;
  private String diseaseDisplay;
  private String commonDiseaseInformationSystem;
  private String commonDiseaseInformationCode;
  private String commonDiseaseInformationDisplay;
  private String specificDiseaseInformationSystem;
  private String specificDiseaseInformationCode;
  private String specificDiseaseInformationDisplay;
  private String identifierSystem;
  private String identifierValue;
  private String categorySystem;
  private String categoryCode;
  private String categoryDisplay;

  public Composition buildNotificationDisease(
      Patient notifiedPerson,
      PractitionerRole notifierRole,
      Condition disease,
      QuestionnaireResponse commonQuestionnaireResponse,
      QuestionnaireResponse specificQuestionnaireResponse) {

    Composition composition = new Composition();

    id = requireNonNullElse(id, generateUuidString());
    composition.setId(id);

    addTitle(composition);
    addDate(composition);
    addIdentifier(composition);

    addMetaProfileIfSet(composition);
    addStatusIfSet(composition);
    addType(composition);
    addCategory(composition);

    composition.setSubject(new Reference(notifiedPerson));
    composition.addAuthor(new Reference(notifierRole));

    addDiseaseSection(disease, composition);
    addCommonQuestionnaireResponse(commonQuestionnaireResponse, composition);
    addSpecificQuestionnaireResponse(specificQuestionnaireResponse, composition);

    return composition;
  }

  private void addCategory(Composition composition) {
    if (isNotBlank(categoryCode) || isNotBlank(categoryDisplay) || isNotBlank(categorySystem)) {
      composition.addCategory(
          new CodeableConcept(new Coding(categorySystem, categoryCode, categoryDisplay)));
    }
  }

  private void addIdentifier(Composition composition) {
    if (isNotBlank(identifierSystem) || isNotBlank(identifierValue)) {
      composition.setIdentifier(
          new Identifier().setValue(identifierValue).setSystem(identifierSystem));
    }
  }

  private void addDate(Composition composition) {
    if (date != null) {
      composition.setDate(date.getValue());
    }
  }

  private void addTitle(Composition composition) {
    if (isNotBlank(title)) {
      composition.setTitle(title);
    }
  }

  private void addSpecificQuestionnaireResponse(
      QuestionnaireResponse specificQuestionnaireResponse, Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("Meldetatbestandsspezifische klinische und epidemiologische Angaben")
            .setCode(
                new CodeableConcept(
                    new Coding(
                        specificDiseaseInformationSystem,
                        specificDiseaseInformationCode,
                        specificDiseaseInformationDisplay)))
            .addEntry(new Reference(specificQuestionnaireResponse)));
  }

  private void addCommonQuestionnaireResponse(
      QuestionnaireResponse commonQuestionnaireResponse, Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("Meldetatbestandsübergreifende klinische und epidemiologische Angaben")
            .setCode(
                new CodeableConcept(
                    new Coding(
                        commonDiseaseInformationSystem,
                        commonDiseaseInformationCode,
                        commonDiseaseInformationDisplay)))
            .addEntry(new Reference(commonQuestionnaireResponse)));
  }

  private void addDiseaseSection(Condition disease, Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("disease")
            .setCode(new CodeableConcept(new Coding(diseaseSystem, diseaseCode, diseaseDisplay)))
            .addEntry(new Reference(disease)));
  }

  private void addType(Composition composition) {
    Coding typeCoding = new Coding(typeSystem, typeCode, typeDisplay);
    composition.setType(new CodeableConcept(typeCoding));
  }

  private void addStatusIfSet(Composition composition) {
    if (isNotBlank(status)) {
      composition.setStatus(Composition.CompositionStatus.valueOf(status.toUpperCase()));
    }
  }

  private void addMetaProfileIfSet(Composition composition) {
    if (isNotBlank(profileUrl)) {
      composition.setMeta(new Meta().addProfile(profileUrl));
    }
  }

  public Composition buildExampleCVDDNotificationDisease(
      Patient notifiedPerson,
      PractitionerRole notifierRole,
      Condition disease,
      QuestionnaireResponse commonQuestionnaireResponse,
      QuestionnaireResponse specificQuestionnaireResponse) {

    profileUrl = requireNonNullElse(profileUrl, PROFILE_NOTIFICATION_DISEASE);
    status = requireNonNullElse(status, "final");
    typeSystem = requireNonNullElse(typeSystem, "http://loinc.org");
    typeCode = requireNonNullElse(typeCode, "34782-3");
    typeDisplay = requireNonNullElse(typeDisplay, "Infectious disease Note");

    title = requireNonNullElse(title, "Meldung gemäß §6 Absatz 1, 2 IfSG");
    date = requireNonNullElse(date, new DateTimeType("2022-03-10"));

    identifierSystem = requireNonNullElse(identifierSystem, NAMING_SYSTEM_NOTIFICATION_ID);
    identifierValue = requireNonNullElse(identifierValue, generateUuidString());

    categorySystem = requireNonNullElse(categorySystem, CODE_SYSTEM_NOTIFICATION_TYPE);
    categoryCode = requireNonNullElse(categoryCode, "6.1_2");
    categoryDisplay = requireNonNullElse(categoryDisplay, "Meldung gemäß IfSG §6 Absatz 1, 2");

    disease =
        requireNonNullElse(
            disease, new DiseaseDataBuilder().buildCVDDexampleDisease(notifiedPerson));

    title = requireNonNullElse(title, "Meldung gemäß Absatz 1, 2 IfSG");

    diseaseSystem = CODE_SYSTEM_SECTION_CODE;
    diseaseCode = "diagnosis";
    diseaseDisplay = "Diagnose";

    commonDiseaseInformationSystem = CODE_SYSTEM_SECTION_CODE;
    commonDiseaseInformationCode = "generalClinAndEpiInformation";
    commonDiseaseInformationDisplay =
        "Meldetatbestandsübergreifende klinische und epidemiologische Angaben";

    specificDiseaseInformationSystem = CODE_SYSTEM_SECTION_CODE;
    specificDiseaseInformationCode = "specificClinAndEpiInformation";
    specificDiseaseInformationDisplay =
        "Meldetatbestandsspezifische klinische und epidemiologische Angaben";

    return buildNotificationDisease(
        notifiedPerson,
        notifierRole,
        disease,
        commonQuestionnaireResponse,
        specificQuestionnaireResponse);
  }
}
