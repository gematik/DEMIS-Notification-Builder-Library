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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateCodeableConcept;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateIdentifier;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_DIAGNOSTIC_REPORT;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_PRACTITIONER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_SEQUENCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_LOINC;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils.internalReference;
import static org.hl7.fhir.r4.model.Composition.DocumentRelationshipType.APPENDS;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Composition.CompositionRelatesToComponent;
import org.hl7.fhir.r4.model.Composition.CompositionStatus;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

/** Builder for the entry Composition/NotificationId in an IGS NotificationBundleSequence. */
@Setter
@SuperBuilder
public class NotificationIdCompositionBuilder extends AbstractIgsResourceBuilder<Composition> {

  private static final String CODING_CODE = "11502-2";
  private static final String LABORATORY_REPORT = "Laboratory report";
  private static final String TYPE_CODE = "34782-3";
  private static final String TYPE_DISPLAY = "Infectious disease Note";
  private static final String TITLE = "Sequenzmeldung";
  private static final String RELATES_TO_TYPE = "Composition";
  private Optional<Patient> subjectReference;
  private Optional<DiagnosticReport> sectionEntryReference;
  private Optional<PractitionerRole> authorReference;
  private String loincVersion;

  /**
   * Builds the FHIR object representing the entry Composition/NotificationId.
   *
   * @return The FHIR object representing the entry Composition/NotificationId.
   */
  @Override
  public Optional<Composition> buildResource() {
    Composition composition = new Composition();

    composition.setIdentifier(
        generateIdentifier(NAMING_SYSTEM_NOTIFICATION_ID, "d1c9252a-9809-4f17-bc59-16d6b2abaaae"));

    composition.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_NOTIFICATION_SEQUENCE).initialize().build());

    CompositionRelatesToComponent relatesToComponent = getCompositionRelatesToComponent();
    composition.setRelatesTo(List.of(relatesToComponent));

    subjectReference.ifPresent(resource -> composition.setSubject(internalReference(resource)));

    CodeableConcept categoryCodeableConcept =
        generateCodeableConcept(SYSTEM_LOINC, CODING_CODE, LABORATORY_REPORT, loincVersion);
    composition.setCategory(List.of(categoryCodeableConcept));

    composition.setId(UUID.randomUUID().toString());
    composition.setTitle(TITLE);
    composition.setDate(new Date());

    sectionEntryReference.ifPresent(
        resource -> {
          Composition.SectionComponent componentSection = new Composition.SectionComponent();
          componentSection.setEntry(
              List.of(new Reference(PREFIX_DIAGNOSTIC_REPORT + resource.getId())));
          componentSection.setCode(
              generateCodeableConcept(SYSTEM_LOINC, CODING_CODE, LABORATORY_REPORT, loincVersion));
          composition.setSection(List.of(componentSection));
        });

    composition.setStatus(CompositionStatus.fromCode(data.getStatus()));
    composition.setType(
        generateCodeableConcept(SYSTEM_LOINC, TYPE_CODE, TYPE_DISPLAY, loincVersion));

    authorReference.ifPresent(
        resource ->
            composition.setAuthor(
                List.of(new Reference(PREFIX_PRACTITIONER_ROLE + resource.getId()))));

    return Optional.of(composition);
  }

  private CompositionRelatesToComponent getCompositionRelatesToComponent() {
    CompositionRelatesToComponent relatesToComponent = new CompositionRelatesToComponent();
    Reference relatesToReference = new Reference();
    relatesToReference.setIdentifier(
        generateIdentifier(NAMING_SYSTEM_NOTIFICATION_ID, data.getDemisNotificationId()));
    relatesToReference.setType(RELATES_TO_TYPE);
    relatesToComponent.setTarget(relatesToReference);
    relatesToComponent.setCode(APPENDS);
    return relatesToComponent;
  }
}
