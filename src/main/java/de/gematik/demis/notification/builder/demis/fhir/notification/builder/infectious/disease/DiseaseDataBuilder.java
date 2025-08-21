package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

/** Condition builder of disease notification */
@Setter
public class DiseaseDataBuilder implements InitializableFhirObjectBuilder {

  private static final String PROFILE_URL = "https://demis.rki.de/fhir/StructureDefinition/Disease";

  private final List<Coding> evidences = new ArrayList<>();
  private final List<String> notes = new ArrayList<>();
  private String id;
  private String profileUrl;
  private Coding disease;
  private Coding clinicalStatus;
  private Coding verificationStatus;
  private DateTimeType recordedDate;
  private DateTimeType onset;
  private Patient notifiedPerson;

  /**
   * Copy the given condition and keep only the required fields.
   *
   * @param notifiedPerson Caller ensures the given resource was copied correctly.
   */
  @Nonnull
  public static Condition deepCopy(
      @Nonnull final Condition condition, @Nonnull final Patient notifiedPerson) {
    final Coding verificationStatus = condition.getVerificationStatus().getCodingFirstRep().copy();
    final Coding clinicalStatus = condition.getClinicalStatus().getCodingFirstRep().copy();
    final Coding disease = condition.getCode().getCodingFirstRep().copy();
    final List<Coding> evidence =
        condition.getEvidence().stream()
            .flatMap(e -> e.getCode().stream())
            .flatMap(concept -> concept.getCoding().stream())
            .map(Coding::copy)
            .toList();

    final Set<String> profiles = Metas.profilesFrom(condition);
    final DiseaseDataBuilder diseaseDataBuilder =
        new DiseaseDataBuilder()
            // we assume the caller has verified the correctness of the condition, we can't take
            // care of that in the builder-library
            .setProfileUrl(profiles.stream().findFirst().orElseThrow())
            .setNotifiedPerson(notifiedPerson)
            .setVerificationStatus(verificationStatus)
            .setClinicalStatus(clinicalStatus)
            .setRecordedDate(condition.getRecordedDateElement())
            .setDisease(disease)
            .setId(condition.getId())
            .setOnset(condition.getOnsetDateTimeType())
            .setEvidences(evidence);

    condition.getNote().stream().map(Annotation::getText).forEach(diseaseDataBuilder::addNote);

    return diseaseDataBuilder.build();
  }

  private static Condition.ConditionEvidenceComponent toEvidence(Coding coding) {
    return new Condition.ConditionEvidenceComponent()
        .addCode(
            new CodeableConcept(
                new Coding(coding.getSystem(), coding.getCode(), coding.getDisplay())));
  }

  @Override
  public Condition build() {
    Condition condition = new Condition();
    addId(condition);
    addProfileUrl(condition);
    addSubject(condition);
    addClinicalStatus(condition);
    addVerificationStatus(condition);
    addCode(condition);
    addRecordedDate(condition);
    addOnset(condition);
    addEvidences(condition);
    addNote(condition);
    return condition;
  }

  public DiseaseDataBuilder addEvidence(Coding evidence) {
    this.evidences.add(evidence);
    return this;
  }

  public DiseaseDataBuilder setEvidences(Collection<Coding> evidences) {
    this.evidences.clear();
    this.evidences.addAll(evidences);
    return this;
  }

  /**
   * Sets:
   *
   * <ul>
   *   <li>ID
   *   <li>verification status: confirmed
   * </ul>
   *
   * @return builder
   */
  @Override
  public DiseaseDataBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    if (this.verificationStatus == null) {
      setVerificationStatus(
          new Coding(DemisConstants.CODE_SYSTEM_CONDITION_VERIFICATION_STATUS, "confirmed", null));
    }
    return this;
  }

  /**
   * Set disease category coding and profile URL by disease category as four-letter code.
   *
   * @param diseaseCategory disease category, aka. code, like: <code>cvdd</code> or <code>msvd
   *                        </code>
   * @return builder
   */
  public DiseaseDataBuilder setDiseaseAndProfileUrl(String diseaseCategory) {
    setDisease(new Coding(CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY, diseaseCategory, null));
    setProfileUrlByDisease(diseaseCategory);
    return this;
  }

  /**
   * Sets recorded date
   *
   * @param recordedDate ISO 6801 compatible value, like: <code>2023-11-15</code> or <code>
   *                     2023-11-15T14:45:00</code>
   * @return builder
   */
  public DiseaseDataBuilder setRecordedDate(String recordedDate) {
    setRecordedDate(new DateTimeType(recordedDate));
    return this;
  }

  public DiseaseDataBuilder setRecordedDate(DateTimeType recordedDate) {
    this.recordedDate = recordedDate;
    return this;
  }

  /**
   * Set onset
   *
   * @param onset ISO 6801 compatible value, like: <code>2023-11-15</code> or <code>
   *              2023-11-15T14:45:00</code>
   * @return builder
   */
  public DiseaseDataBuilder setOnset(String onset) {
    setOnset(new DateTimeType(onset));
    return this;
  }

  public DiseaseDataBuilder setOnset(DateTimeType onset) {
    this.onset = onset;
    return this;
  }

  DiseaseDataBuilder setProfileUrlByDisease(String disease) {
    setProfileUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(PROFILE_URL, disease));
    return this;
  }

  private void addClinicalStatus(Condition condition) {
    if (this.clinicalStatus != null) {
      condition.setClinicalStatus(new CodeableConcept(this.clinicalStatus));
    }
  }

  private void addNote(Condition condition) {
    this.notes.stream()
        .filter(StringUtils::isNotBlank)
        .forEach(note -> condition.addNote().setText(note));
  }

  private void addEvidences(Condition condition) {
    this.evidences.stream().map(DiseaseDataBuilder::toEvidence).forEach(condition::addEvidence);
  }

  private void addSubject(Condition condition) {
    if (this.notifiedPerson != null) {
      condition.setSubject(new Reference(this.notifiedPerson));
    }
  }

  private void addRecordedDate(Condition condition) {
    condition.setRecordedDateElement(this.recordedDate);
  }

  private void addOnset(Condition condition) {
    condition.setOnset(this.onset);
  }

  private void addId(Condition condition) {
    condition.setId(this.id);
  }

  private void addCode(Condition condition) {
    if (this.disease != null) {
      condition.setCode(new CodeableConcept(this.disease));
    }
  }

  private void addVerificationStatus(Condition condition) {
    if (this.verificationStatus != null) {
      condition.setVerificationStatus(new CodeableConcept(this.verificationStatus));
    }
  }

  private void addProfileUrl(Condition condition) {
    if (StringUtils.isNotBlank(this.profileUrl)) {
      condition.setMeta(new Meta().addProfile(this.profileUrl));
    }
  }

  /**
   * Add note to condition
   *
   * @param note note
   * @return builder
   * @deprecated use {@link #addNote(String)} instead
   */
  @Deprecated(forRemoval = true)
  public DiseaseDataBuilder setNote(String note) {
    addNote(note);
    return this;
  }

  /**
   * Add note to condition
   *
   * @param note note
   * @return builder
   */
  public DiseaseDataBuilder addNote(String note) {
    this.notes.add(note);
    return this;
  }
}
