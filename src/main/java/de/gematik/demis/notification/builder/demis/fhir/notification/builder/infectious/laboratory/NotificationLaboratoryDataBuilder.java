package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_NOTIFICATION_7_1_COMPOSITION_TITLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LOINC_ORG_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_ID_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_LABORATORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.CompositionBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.RelatesToBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

@Setter
public class NotificationLaboratoryDataBuilder
    extends CompositionBuilder<NotificationLaboratoryDataBuilder> {

  private String sectionComponentSystem;
  private String sectionComponentCode;
  private String sectionComponentDisplay;

  @CheckForNull private DiagnosticReport laboratoryReport;

  protected String getDefaultTitle() {
    return LABORATORY_NOTIFICATION_7_1_COMPOSITION_TITLE;
  }

  public NotificationLaboratoryDataBuilder setDefault() {

    setIdentifierSystem(NOTIFICATION_ID_SYSTEM);
    setSectionComponentSystem(LOINC_ORG_SYSTEM);
    setTitle(getDefaultTitle());

    super.setDefaultData();
    return this;
  }

  // In NotificationLaboratoryDataBuilder.java

  protected void deepCopyFields(
      @Nonnull final Composition original,
      @Nonnull final PractitionerRole author,
      @Nonnull final Patient subject,
      @Nonnull final DiagnosticReport diagnosticReport) {
    setDefault();
    setNotificationId(original.getIdElement().getIdPart());
    setTypeCode(original.getType().getCodingFirstRep());
    setCategoryCode(original.getCategoryFirstRep().getCodingFirstRep());
    setIdentifierSystem(original.getIdentifier().getSystem());
    setIdentifierValue(original.getIdentifier().getValue());
    setCompositionStatus(original.getStatus());
    setTitle(original.getTitle());
    setDateTimeType(original.getDateElement());

    List<Composition.CompositionRelatesToComponent> relatesTo = original.getRelatesTo();
    if (!relatesTo.isEmpty()) {
      final Composition.CompositionRelatesToComponent relatesToElement = relatesTo.get(0);
      final Reference targetReference = relatesToElement.getTargetReference();
      final Identifier identifier = targetReference.getIdentifier();

      setRelatesToCode(relatesToElement.getCode());
      setRelatesToReferenceType(targetReference.getType());
      setRelatesToNotificationId(identifier.getValue());
    }

    setNotifierRole(author);
    setNotifiedPerson(subject);

    setLaboratoryReport(diagnosticReport);

    final Composition.SectionComponent section = original.getSectionFirstRep();
    setSectionCode(section.getCode().getCodingFirstRep());
  }

  @Override
  public Composition build() {
    Composition newComposition = super.build();

    newComposition.setTitle(getTitle());
    Identifier compositionIdentifier =
        new Identifier().setSystem(getIdentifierSystem()).setValue(getIdentifierValue());
    newComposition.setIdentifier(compositionIdentifier);

    if (laboratoryReport != null) {
      Composition.SectionComponent sectionComponent = new Composition.SectionComponent();
      sectionComponent.addEntry(ReferenceUtils.internalReference(laboratoryReport));
      sectionComponent.setCode(
          new CodeableConcept(
              new Coding(sectionComponentSystem, sectionComponentCode, sectionComponentDisplay)));
      newComposition.addSection(sectionComponent);
    }

    String relatesTo = getRelatesTo();
    if (relatesTo != null) {
      Composition.CompositionRelatesToComponent compositionRelatesToComponent =
          RelatesToBuilder.forInitialNotificationId(relatesTo);
      newComposition.addRelatesTo(compositionRelatesToComponent);
    }

    return newComposition;
  }

  protected NotificationLaboratoryDataBuilder setTypeCode(final Coding type) {
    return this.setTypeCode(type.getCode())
        .setTypeDisplay(type.getDisplay())
        .setTypeSystem(type.getSystem());
  }

  protected NotificationLaboratoryDataBuilder setCategoryCode(final Coding category) {
    return this.setCodeAndCategoryCode(category.getCode())
        .setCodeAndCategoryDisplay(category.getDisplay())
        .setCodeAndCategorySystem(category.getSystem());
  }

  protected NotificationLaboratoryDataBuilder setSectionCode(final Coding section) {
    return this.setSectionComponentCode(section.getCode())
        .setSectionComponentDisplay(section.getDisplay())
        .setSectionComponentSystem(section.getSystem());
  }

  @Override
  protected String getDefaultProfileUrl() {
    return PROFILE_NOTIFICATION_LABORATORY;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public Composition buildNotificationLaboratory(
      Patient notifiedPerson, PractitionerRole notifierRole, DiagnosticReport laboratoryReport) {
    Composition newComposition = new Composition();
    newComposition.setId(getNotificationId());
    newComposition.setStatus(getCompositionStatus());

    Composition.SectionComponent sectionComponent =
        setMandatoryFields(notifiedPerson, notifierRole, laboratoryReport, newComposition);

    Coding codeAndCategroyCoding =
        new Coding(
            getCodeAndCategorySystem(), getCodeAndCategoryCode(), getCodeAndCategoryDisplay());
    CodeableConcept categoryCodeableConcept = new CodeableConcept(codeAndCategroyCoding);
    newComposition.addCategory(categoryCodeableConcept);
    sectionComponent.setCode(categoryCodeableConcept);

    newComposition.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFICATION_LABORATORY));
    newComposition.setDate(getDate());
    newComposition.setTitle(getTitle());

    Identifier compositionIdentifier =
        new Identifier().setSystem(getIdentifierSystem()).setValue(getIdentifierValue());
    newComposition.setIdentifier(compositionIdentifier);

    Coding typeCoding = new Coding(getTypeSystem(), getTypeCode(), getTypeDisplay());
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
    setNotificationId(requireNonNullElse(getNotificationId(), generateUuidString()));
    setExampleCompositionStatus();
    setExampleType();
    setExampleCodeAndCategory();
    setExampleIdentifier();
    setDate(requireNonNullElse(getDate(), getCurrentDate()));
    setTitle(requireNonNullElse(getTitle(), "Erregernachweismeldung"));
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
    setIdentifierSystem(system);
    setIdentifierValue(value);
    return this;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleIdentifier() {
    setIdentifierSystem(
        requireNonNullElse(
            getIdentifierSystem(), "https://demis.rki.de/fhir/NamingSystem/NotificationId"));
    setIdentifierValue(requireNonNullElse(getIdentifierValue(), generateUuidString()));
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleCodeAndCategory() {
    setCodeAndCategorySystem(requireNonNullElse(getCodeAndCategorySystem(), "http://loinc.org"));
    setCodeAndCategoryCode(requireNonNullElse(getCodeAndCategoryCode(), "11502-2"));
    setCodeAndCategoryDisplay(requireNonNullElse(getCodeAndCategoryDisplay(), "Laboratory report"));
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  private void setExampleType() {
    setTypeSystem(requireNonNullElse(getTypeSystem(), "http://loinc.org"));
    setTypeCode(requireNonNullElse(getTypeCode(), "34782-3"));
    setTypeDisplay(requireNonNullElse(getTypeDisplay(), "Infectious disease Note"));
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
    setCompositionStatus(
        requireNonNullElse(getCompositionStatus(), Composition.CompositionStatus.FINAL));
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
