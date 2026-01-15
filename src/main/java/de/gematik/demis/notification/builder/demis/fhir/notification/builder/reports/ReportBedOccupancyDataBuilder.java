package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.Date;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

/** Create composition for bed occupancy report bundle. */
@Setter
public class ReportBedOccupancyDataBuilder implements InitializableFhirObjectBuilder {

  private Identifier subject;
  private Identifier identifier;
  private PractitionerRole notifierRole;
  private QuestionnaireResponse statisticInformationBedOccupancy;

  private Coding type;
  private Coding category;
  private String status;
  private String title;
  private String id;
  private Date currentDate;

  /**
   * Set default values for composition:
   *
   * <ul>
   *   <li>id
   *   <li>identifier as notification ID
   *   <li>currentDate
   *   <li>type
   *   <li>category
   *   <li>title
   *   <li>status
   * </ul>
   *
   * @return builder
   */
  @Override
  public ReportBedOccupancyDataBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    if (this.identifier == null) {
      setIdentifierAsNotificationId(generateUuidString());
    }
    if (this.currentDate == null) {
      setCurrentDate(getCurrentDate());
    }
    if (this.type == null) {
      setType(new Coding(DemisConstants.LOINC_ORG_SYSTEM, "80563-0", "Report"));
    }
    if (this.category == null) {
      setCategory(
          new Coding(
              "https://demis.rki.de/fhir/CodeSystem/reportCategory",
              "bedOccupancyReport",
              "Bettenbelegungsstatistik"));
    }
    if (this.title == null) {
      setTitle("Bericht (Krankenhausbettenbelegungsstatistik)");
    }
    if (this.status == null) {
      setStatusStandard(Composition.CompositionStatus.FINAL);
    }
    return this;
  }

  public ReportBedOccupancyDataBuilder setStatusStandard(Composition.CompositionStatus status) {
    this.status = status.toCode();
    return this;
  }

  public ReportBedOccupancyDataBuilder setIdentifierAsNotificationId(String notificationId) {
    this.identifier = new Identifier();
    this.identifier.setSystem(DemisConstants.NOTIFICATION_ID_SYSTEM);
    this.identifier.setValue(notificationId);
    return this;
  }

  public ReportBedOccupancyDataBuilder setSubjectAsInekStandortId(String inekStandortId) {
    this.subject =
        new Identifier()
            .setSystem(DemisConstants.PROFILE_INEK_NAMING_SYSTEM)
            .setValue(inekStandortId);
    return this;
  }

  @Override
  public Composition build() {
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

  private void addSubjectIdentifier(Composition composition) {
    Reference subjectReference = new Reference();
    subjectReference.setIdentifier(this.subject);
    composition.setSubject(subjectReference);
  }

  private void addIdentifier(Composition composition) {
    composition.setIdentifier(this.identifier);
  }

  private void addMeta(Composition composition) {
    Meta metaValue = new Meta();
    metaValue.addProfile(DemisConstants.PROFILE_REPORT_BED_OCCUPANDY);
    composition.setMeta(metaValue);
  }

  private void addCategoryCoding(Composition composition) {
    composition.addCategory(new CodeableConcept(this.category));
  }

  private void addTypeCoding(Composition composition) {
    composition.setType(new CodeableConcept(this.type));
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
