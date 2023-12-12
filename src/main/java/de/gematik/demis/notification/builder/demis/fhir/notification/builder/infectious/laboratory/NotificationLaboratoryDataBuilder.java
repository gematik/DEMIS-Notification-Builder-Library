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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORAOTRY_CATEGORY_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_CATEGORY_DISPLAY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LOINC_ORG_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_ID_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.CompositionBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class NotificationLaboratoryDataBuilder
    extends CompositionBuilder<NotificationLaboratoryDataBuilder> {

  private String sectionComponentSystem;
  private String sectionComponentCode;
  private String sectionComponentDisplay;

  private DiagnosticReport laboratoryReport;

  public NotificationLaboratoryDataBuilder setDefault() {

    metaUrl = DemisConstants.PROFILE_NOTIFICATION_LABORATORY;
    identifierSystem = NOTIFICATION_ID_SYSTEM;
    sectionComponentSystem = LOINC_ORG_SYSTEM;
    codeAndCategorySystem = LOINC_ORG_SYSTEM;
    codeAndCategoryCode = LABORAOTRY_CATEGORY_CODE;
    codeAndCategoryDisplay = LABORATORY_CATEGORY_DISPLAY;

    super.setDefaultdData();
    return this;
  }

  @Override
  public Composition build() {
    Composition newComposition = super.build();

    Identifier compositionIdentifier =
        new Identifier().setSystem(identifierSystem).setValue(identifierValue);
    newComposition.setIdentifier(compositionIdentifier);

    Reference laboratoryReportReference = new Reference(laboratoryReport);
    Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
    sectionComponent.addEntry(laboratoryReportReference);
    sectionComponent.setCode(
        new CodeableConcept(
            new Coding(sectionComponentSystem, sectionComponentCode, sectionComponentDisplay)));
    newComposition.addSection(sectionComponent);

    if (relatesTo != null) {
      Reference reference = new Reference();
      Identifier identifier =
          new Identifier().setSystem(NOTIFICATION_ID_SYSTEM).setValue(relatesTo);
      reference.setIdentifier(identifier);
      reference.setType("Composition");
      Composition.CompositionRelatesToComponent compositionRelatesToComponent =
          new Composition.CompositionRelatesToComponent();
      compositionRelatesToComponent.setCode(Composition.DocumentRelationshipType.APPENDS);
      compositionRelatesToComponent.setTarget(reference);
      newComposition.addRelatesTo(compositionRelatesToComponent);
    }

    return newComposition;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public Composition buildNotificationLaboratory(
      Patient notifiedPerson, PractitionerRole notifierRole, DiagnosticReport laboratoryReport) {
    Composition newComposition = new Composition();
    newComposition.setId(notificationId);
    newComposition.setStatus(compositionStatus);

    Composition.SectionComponent sectionComponent =
        setMandatoryFields(notifiedPerson, notifierRole, laboratoryReport, newComposition);

    Coding codeAndCategroyCoding =
        new Coding(codeAndCategorySystem, codeAndCategoryCode, codeAndCategoryDisplay);
    CodeableConcept categoryCodeableConcept = new CodeableConcept(codeAndCategroyCoding);
    newComposition.addCategory(categoryCodeableConcept);
    sectionComponent.setCode(categoryCodeableConcept);

    newComposition.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFICATION_LABORATORY));
    newComposition.setDate(date);
    newComposition.setTitle(title);

    Identifier compositionIdentifier =
        new Identifier().setSystem(identifierSystem).setValue(identifierValue);
    newComposition.setIdentifier(compositionIdentifier);

    Coding typeCoding = new Coding(typeSystem, typeCode, typeDisplay);
    CodeableConcept typeCodeableConcept = new CodeableConcept(typeCoding);
    newComposition.setType(typeCodeableConcept);

    return newComposition;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public Composition buildExampleComposition(
      Patient notifiedPerson, PractitionerRole notifierRole, DiagnosticReport laboratoryReport) {
    notificationId = requireNonNullElse(notificationId, generateUuidString());
    setExampleCompositionStatus();
    setExampleType();
    setExampleCodeAndCategory();
    setExampleIdentifier();
    date = requireNonNullElse(date, getCurrentDate());
    title = requireNonNullElse(title, "Erregernachweismeldung");
    return buildNotificationLaboratory(notifiedPerson, notifierRole, laboratoryReport);
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public NotificationLaboratoryDataBuilder addIdentifier() {
    return addIdentifier(
        generateUuidString(), "https://demis.rki.de/fhir/NamingSystem/NotificationId");
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private NotificationLaboratoryDataBuilder addIdentifier(String value, String system) {
    identifierSystem = system;
    identifierValue = value;
    return this;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleIdentifier() {
    identifierSystem =
        requireNonNullElse(
            identifierSystem, "https://demis.rki.de/fhir/NamingSystem/NotificationId");
    identifierValue = requireNonNullElse(identifierValue, generateUuidString());
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleCodeAndCategory() {
    codeAndCategorySystem = requireNonNullElse(codeAndCategorySystem, "http://loinc.org");
    codeAndCategoryCode = requireNonNullElse(codeAndCategoryCode, "11502-2");
    codeAndCategoryDisplay = requireNonNullElse(codeAndCategoryDisplay, "Laboratory report");
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleType() {
    typeSystem = requireNonNullElse(typeSystem, "http://loinc.org");
    typeCode = requireNonNullElse(typeCode, "34782-3");
    typeDisplay = requireNonNullElse(typeDisplay, "Infectious disease Note");
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private Composition.SectionComponent setMandatoryFields(
      Patient notifiedPerson,
      PractitionerRole notifierRole,
      DiagnosticReport laboratoryReport,
      Composition newComposition) {
    newComposition.setSubject(new Reference(notifiedPerson));
    newComposition.addAuthor(new Reference(notifierRole));
    Reference laboratoryReportReference = new Reference(laboratoryReport);
    Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
    sectionComponent.addEntry(laboratoryReportReference);
    sectionComponent.setCode(
        new CodeableConcept(
            new Coding(sectionComponentSystem, sectionComponentCode, sectionComponentDisplay)));
    newComposition.addSection(sectionComponent);
    return sectionComponent;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleCompositionStatus() {
    compositionStatus = requireNonNullElse(compositionStatus, Composition.CompositionStatus.FINAL);
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public NotificationLaboratoryDataBuilder addNotificationLaboratoryId() {
    setNotificationId(generateUuidString());
    return this;
  }
}
