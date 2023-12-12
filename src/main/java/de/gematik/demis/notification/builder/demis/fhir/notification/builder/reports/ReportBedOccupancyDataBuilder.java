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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.Date;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class ReportBedOccupancyDataBuilder {

  private String typeCode;
  private String typeSystem;
  private String typeDisplay;

  private String categoryCode;
  private String categorySystem;
  private String categoryDisplay;

  private String subjectSystem;
  private String subjectValue;

  private String identifierSystem;
  private String identifierValue;

  private String status;

  private String title;
  private String id;

  private Date currentDate;

  public Composition buildReportBedOccupancy(
      PractitionerRole notifierRole, QuestionnaireResponse statisticInformationBedOccupancy) {
    Composition composition = new Composition();
    composition.setId(id);
    addMeta(composition);
    addIdentifier(composition);
    composition.setStatus(Composition.CompositionStatus.fromCode(status));
    addTypeCoding(composition);
    addCategoryCoding(composition);
    addSubjectIdentifier(composition);
    composition.addAuthor(new Reference(notifierRole));
    composition.setTitle(title);
    composition.setDate(currentDate);

    Composition.SectionComponent sectionForQuestionnaireResponse =
        new Composition.SectionComponent();
    sectionForQuestionnaireResponse.addEntry(new Reference(statisticInformationBedOccupancy));
    addSectionCoding(composition, sectionForQuestionnaireResponse);

    return composition;
  }

  /**
   * this method contains hardcoded values for gateway use to shorten implementation time. it is not
   * supposed to use it in any other case.
   *
   * @param notifierRole notifierRole object
   * @param statisticInformationBedOccupancy questionnaire object
   * @return complete composition with example values and the given objects
   */
  public Composition buildReportBedOccupancyForGateway(
      PractitionerRole notifierRole, QuestionnaireResponse statisticInformationBedOccupancy) {
    id = requireNonNullElse(id, generateUuidString());

    identifierSystem =
        requireNonNullElse(
            identifierSystem, "https://demis.rki.de/fhir/NamingSystem/NotificationId");
    identifierValue = requireNonNullElse(identifierValue, generateUuidString());

    status = requireNonNullElse(status, "final");

    typeCode = requireNonNullElse(typeCode, "80563-0");
    typeSystem = requireNonNullElse(typeSystem, "http://loinc.org");
    typeDisplay = requireNonNullElse(typeDisplay, "Report");

    categoryCode = requireNonNullElse(categoryCode, "bedOccupancyReport");
    categorySystem =
        requireNonNullElse(categorySystem, "https://demis.rki.de/fhir/CodeSystem/reportCategory");
    categoryDisplay = requireNonNullElse(categoryDisplay, "Bettenbelegungsstatistik");

    subjectSystem = requireNonNullElse(subjectSystem, DemisConstants.PROFILE_INEK_NAMING_SYSTEM);

    title = requireNonNullElse(title, "Bericht (Krankenhausbettenbelegungsstatistik)");
    currentDate = requireNonNullElse(currentDate, getCurrentDate());

    return buildReportBedOccupancy(notifierRole, statisticInformationBedOccupancy);
  }

  public Composition buildExampleReportBedOccupancy(
      PractitionerRole notifierRole, QuestionnaireResponse statisticInformationBedOccupancy) {
    typeCode = requireNonNullElse(typeCode, "80563-0");
    typeSystem = requireNonNullElse(typeSystem, "http://loinc.org");
    typeDisplay = requireNonNullElse(typeDisplay, "Report");

    categoryCode = requireNonNullElse(categoryCode, "bedOccupancyReport");
    categorySystem =
        requireNonNullElse(categorySystem, "https://demis.rki.de/fhir/CodeSystem/reportCategory");
    categoryDisplay = requireNonNullElse(categoryDisplay, "Bettenbelegungsstatistik");

    identifierSystem =
        requireNonNullElse(
            identifierSystem, "https://demis.rki.de/fhir/NamingSystem/NotificationId");
    identifierValue = requireNonNullElse(identifierValue, generateUuidString());

    status = requireNonNullElse(status, "final");

    subjectSystem =
        requireNonNullElse(subjectSystem, "https://demis.rki.de/fhir/NamingSystem/InekStandortId");
    subjectValue = requireNonNullElse(subjectValue, "772557");

    title = requireNonNullElse(title, "Bericht (Krankenhausbettenbelegungsstatistik)");

    id = requireNonNullElse(id, generateUuidString());
    currentDate = requireNonNullElse(currentDate, getCurrentDate());

    return buildReportBedOccupancy(notifierRole, statisticInformationBedOccupancy);
  }

  private void addSubjectIdentifier(Composition composition) {
    Identifier subjectIdentifier = new Identifier();
    subjectIdentifier.setSystem(subjectSystem);
    subjectIdentifier.setValue(subjectValue);
    Reference subjectReference = new Reference();
    subjectReference.setIdentifier(subjectIdentifier);
    composition.setSubject(subjectReference);
  }

  private void addIdentifier(Composition composition) {
    Identifier identifier = new Identifier();
    identifier.setSystem(identifierSystem);
    identifier.setValue(identifierValue);
    composition.setIdentifier(identifier);
  }

  private void addMeta(Composition composition) {
    Meta metaValue = new Meta();
    metaValue.addProfile(DemisConstants.PROFILE_REPORT_BED_OCCUPANDY);
    composition.setMeta(metaValue);
  }

  private void addCategoryCoding(Composition composition) {
    Coding categoryCoding = new Coding(categorySystem, categoryCode, categoryDisplay);
    CodeableConcept categoryCodeableConcept = new CodeableConcept(categoryCoding);
    composition.addCategory(categoryCodeableConcept);
  }

  private void addTypeCoding(Composition composition) {
    Coding typeCoding = new Coding(typeSystem, typeCode, typeDisplay);
    CodeableConcept typeCodeableConcept = new CodeableConcept(typeCoding);
    composition.setType(typeCodeableConcept);
  }

  private void addSectionCoding(
      Composition composition, Composition.SectionComponent sectionForQuestionnaireResponce) {
    Coding sectionCode =
        new Coding(
            "https://demis.rki.de/fhir/CodeSystem/reportSection",
            "statisticInformationBedOccupancySection",
            "Abschnitt 'Statistische Informationen zur Krankenhausbettenbelegung'");
    CodeableConcept sectionCodeableConcept = new CodeableConcept(sectionCode);
    sectionForQuestionnaireResponce.setCode(sectionCodeableConcept);
    composition.addSection(sectionForQuestionnaireResponce);
  }
}
