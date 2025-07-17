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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SPECIMEN_BASE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils.internalReference;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.AVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.ENTEREDINERROR;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNAVAILABLE;
import static org.hl7.fhir.r4.model.Specimen.SpecimenStatus.UNSATISFACTORY;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Type;

@Setter
public class SpecimenDataBuilder {

  private String specimenId;
  private Specimen.SpecimenStatus specimenStatus;

  private String typeSystem;
  private String typeCode;
  private String typeDisplay;
  private String typeCodingVersion;

  /**
   * @deprecated Use {@code dateTimeElement} instead. This field uses {@link java.util.Date}, which
   *     does not reliably preserve millisecond precision when serializing or deserializing FHIR
   *     resources. For full precision and to ensure the original timestamp (including milliseconds)
   *     is retained in FHIR JSON/XML, use the new {@link org.hl7.fhir.r4.model.DateTimeType} field
   *     and set it via {@code setDateElement()}.
   */
  @Deprecated(forRemoval = true)
  private Date collectedDate;

  private DateTimeType collectedDateTime;

  /**
   * @deprecated Use {@code dateTimeElement} instead. This field uses {@link java.util.Date}, which
   *     does not reliably preserve millisecond precision when serializing or deserializing FHIR
   *     resources. For full precision and to ensure the original timestamp (including milliseconds)
   *     is retained in FHIR JSON/XML, use the new {@link org.hl7.fhir.r4.model.DateTimeType} field
   *     and set it via {@code setDateElement()}.
   */
  @Deprecated(forRemoval = true)
  private Date receivedTime;

  private DateTimeType receivedDateTime;
  private String metaProfileUrl;
  @CheckForNull private Patient notifiedPerson;
  @CheckForNull private PractitionerRole submittingRole;
  private List<Specimen.SpecimenProcessingComponent> processing = new ArrayList<>();
  private List<Annotation> notes = new ArrayList<>();

  public static Specimen deepCopy(
      @Nonnull final Specimen original,
      @Nonnull final Patient subject,
      @Nonnull final PractitionerRole collection) {

    final SpecimenDataBuilder resultBuilder =
        new SpecimenDataBuilder()
            .setMetaProfileUrl(original.getMeta().getProfile().getFirst().getValueAsString())
            .setSpecimenStatus(original.getStatus())
            .setSpecimenId(original.getId());

    // type
    if (original.hasType()) {
      Coding codingFirstRep = original.getType().getCodingFirstRep();
      resultBuilder
          .setTypeCode(codingFirstRep.getCode())
          .setTypeDisplay(codingFirstRep.getDisplay())
          .setTypeSystem(codingFirstRep.getSystem())
          .setTypeCodingVersion(codingFirstRep.getVersion());
    }

    resultBuilder.setNotifiedPerson(subject);
    resultBuilder.setReceivedDateTime(original.getReceivedTimeElement());
    resultBuilder.setSubmittingRole(collection);
    resultBuilder.setCollectedDateTime(original.getCollection().getCollectedDateTimeType());

    // processing
    if (original.hasProcessing()) {
      final List<Specimen.SpecimenProcessingComponent> processings =
          original.getProcessing().stream()
              .map(Specimen.SpecimenProcessingComponent::copy)
              .toList();
      resultBuilder.setProcessing(processings);
    }

    // note
    if (original.hasNote()) {
      final List<Annotation> notes = original.getNote().stream().map(Annotation::copy).toList();
      resultBuilder.setNotes(notes);
    }

    return resultBuilder.build();
  }

  public Specimen build() {
    Specimen specimen = new Specimen();

    specimen.setId(specimenId);
    specimen.setStatus(specimenStatus);
    Coding typeCoding = new Coding(typeSystem, typeCode, typeDisplay);
    if (typeCodingVersion != null) {
      typeCoding.setVersion(typeCodingVersion);
    }
    specimen.setType(new CodeableConcept(typeCoding));
    if (notifiedPerson != null) {
      specimen.setSubject(internalReference(notifiedPerson));
    }

    if (receivedDateTime != null) {
      specimen.setReceivedTimeElement(receivedDateTime);
    } else if (receivedTime != null) {
      specimen.setReceivedTime(receivedTime);
    }

    Specimen.SpecimenCollectionComponent specimenCollectionComponent =
        new Specimen.SpecimenCollectionComponent();
    if (submittingRole != null) {
      specimenCollectionComponent.setCollector(internalReference(submittingRole));
    }
    if (collectedDateTime != null) {
      specimenCollectionComponent.setCollected(collectedDateTime);
    } else if (collectedDate != null) {
      specimenCollectionComponent.setCollected(new DateTimeType(collectedDate));
    }
    specimen.setCollection(specimenCollectionComponent);
    specimen.setMeta(new Meta().addProfile(metaProfileUrl));
    specimen.setNote(notes);
    specimen.setProcessing(processing);

    return specimen;
  }

  public SpecimenDataBuilder setDefaultData() {
    typeSystem = SYSTEM_SNOMED;
    specimenId = generateUuidString();
    return this;
  }

  /**
   * creates a url and adds the given code
   *
   * @param pathogenCode the pathogen code, e.g. cvdp
   * @return
   */
  public SpecimenDataBuilder setProfileUrlHelper(String pathogenCode) {
    metaProfileUrl = SPECIMEN_BASE_URL + pathogenCode.toUpperCase();
    return this;
  }

  public SpecimenDataBuilder setStatusToAvailable() {
    specimenStatus = AVAILABLE;
    return this;
  }

  public SpecimenDataBuilder setStatusToUnavailable() {
    specimenStatus = UNAVAILABLE;
    return this;
  }

  public SpecimenDataBuilder setStatusToUnsatisfactory() {
    specimenStatus = UNSATISFACTORY;
    return this;
  }

  public SpecimenDataBuilder setStatusToEnteredinerror() {
    specimenStatus = ENTEREDINERROR;
    return this;
  }

  @Deprecated(since = "1.2.1")
  public Specimen buildSpecimen(Patient patient, PractitionerRole submittingRole) {

    Coding coding = new Coding(typeSystem, typeCode, typeDisplay);

    Specimen specimen = new Specimen();

    specimen.setSubject(new Reference(patient));
    specimen.setType(new CodeableConcept().addCoding(coding));
    specimen.setReceivedTime(getCurrentDate());
    Specimen.SpecimenCollectionComponent value = new Specimen.SpecimenCollectionComponent();

    value.setCollector(new Reference(submittingRole));
    Type collected = new DateTimeType(collectedDate);
    value.setCollected(collected);

    specimen.setCollection(value);
    specimen.setId(specimenId);
    specimen.setStatus(specimenStatus);

    Meta meta = new Meta().addProfile(metaProfileUrl);
    specimen.setMeta(meta);

    specimen.setReceivedTime(receivedTime);

    return specimen;
  }

  @Deprecated(since = "1.2.1")
  public SpecimenDataBuilder addSpecimenId() {
    return setSpecimenId(generateUuidString());
  }

  public Specimen buildExampleSpecimen(Patient patient, PractitionerRole submittingRole) {

    specimenId = requireNonNullElse(specimenId, generateUuidString());
    specimenStatus = requireNonNullElse(specimenStatus, AVAILABLE);

    typeSystem = requireNonNullElse(typeSystem, "http://snomed.info/sct");
    typeCode = requireNonNullElse(typeCode, "309164002");
    typeDisplay = requireNonNullElse(typeDisplay, "Upper respiratory swab sample (specimen)");

    LocalDate localDate = LocalDate.of(1980, 1, 1);
    collectedDate =
        requireNonNullElse(
            collectedDate, Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

    return buildSpecimen(patient, submittingRole);
  }
}
