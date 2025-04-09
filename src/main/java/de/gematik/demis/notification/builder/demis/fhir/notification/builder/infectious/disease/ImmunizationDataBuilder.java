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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

/** still missing are performer and reason code */
@Setter
public class ImmunizationDataBuilder implements InitializableFhirObjectBuilder {

  private static final String PROFILE_URL =
      "https://demis.rki.de/fhir/StructureDefinition/ImmunizationInformation";

  private final List<String> notes = new ArrayList<>();
  private String id;
  private String profileUrl;
  private String status;
  private Coding vaccineCode;
  private DateTimeType occurrence;
  private String lotNumber;
  private Patient notifiedPerson;

  @Override
  public Immunization build() {
    Immunization immunization = new Immunization();
    addId(immunization);
    addStatus(immunization);
    addProfileUrl(immunization);
    addPatient(immunization);
    addVaccineCode(immunization);
    addLotNumber(immunization);
    addOccurrence(immunization);
    addNotes(immunization);
    return immunization;
  }

  /**
   * Sets:
   *
   * <ul>
   *   <li>ID
   *   <li>status
   * </ul>
   *
   * @return builder
   */
  @Override
  public ImmunizationDataBuilder setDefaults() {
    if (this.id == null) {
      setId(Utils.generateUuidString());
    }
    if (this.status == null) {
      setStatusStandard(Immunization.ImmunizationStatus.COMPLETED);
    }
    return this;
  }

  public ImmunizationDataBuilder setProfileUrlByDisease(String disease) {
    setProfileUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(PROFILE_URL, disease));
    return this;
  }

  public ImmunizationDataBuilder addNote(String note) {
    this.notes.add(note);
    return this;
  }

  public ImmunizationDataBuilder setNotes(List<String> notes) {
    this.notes.clear();
    this.notes.addAll(notes);
    return this;
  }

  public ImmunizationDataBuilder setStatusStandard(Immunization.ImmunizationStatus status) {
    this.status = status.toCode();
    return this;
  }

  private void addNotes(Immunization immunization) {
    this.notes.stream().map(t -> new Annotation().setText(t)).forEach(immunization::addNote);
  }

  private void addProfileUrl(Immunization immunization) {
    if (StringUtils.isNotBlank(this.profileUrl)) {
      immunization.setMeta(new Meta().addProfile(this.profileUrl));
    }
  }

  private void addPatient(Immunization immunization) {
    if (this.notifiedPerson != null) {
      immunization.setPatient(new Reference(this.notifiedPerson));
    }
  }

  private void addOccurrence(Immunization immunization) {
    if (this.occurrence != null) {
      immunization.setOccurrence(this.occurrence);
    }
  }

  private void addLotNumber(Immunization immunization) {
    if (isNotBlank(this.lotNumber)) {
      immunization.setLotNumber(this.lotNumber);
    }
  }

  private void addVaccineCode(Immunization immunization) {
    if (this.vaccineCode != null) {
      immunization.setVaccineCode(new CodeableConcept(this.vaccineCode));
    }
  }

  private void addStatus(Immunization immunization) {
    if (isNotBlank(this.status)) {
      immunization.setStatus(Immunization.ImmunizationStatus.valueOf(this.status.toUpperCase()));
    }
  }

  private void addId(Immunization immunization) {
    immunization.setId(this.id);
  }
}
