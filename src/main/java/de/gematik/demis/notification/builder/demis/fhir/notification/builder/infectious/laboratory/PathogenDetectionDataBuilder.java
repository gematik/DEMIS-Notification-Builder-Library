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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LOINC_ORG_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PATHOGEN_DETECTION_BASE_URL;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.SYSTEM_SNOMED;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;

import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class PathogenDetectionDataBuilder {

  public static final String LABORATORY_CATEGORY_CODE = "laboratory";
  public static final String LABORATORY_CATEGROY_SYSTEM =
      "http://terminology.hl7.org/CodeSystem/observation-category";
  public static final String LABORATORY_CATEGORY_DISPLAY = "Laboratory";
  public static final String OBSERVATION_INTERPRETATION_SYSTEM =
      "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation";
  Patient notifiedPerson;
  Specimen specimen;
  private String pathogenDetectionId;
  private String observationCodeSystem;
  private String observationCodeCode;
  private String observationCodeDisplay;
  private String methodSystem;
  private String methodCode;
  private String methodDisplay;
  private String interpretationSystem;
  private String interpretationCode;
  private String interpretationDisplay;
  private String categorySystem;
  private String categoryCode;
  private String categoryDisplay;
  private String valueSystem;
  private String valueCode;
  private String valueDisplay;
  private Observation.ObservationStatus status;
  private Type value;
  private String profileUrl;

  public PathogenDetectionDataBuilder setDefaultData() {
    categoryCode = LABORATORY_CATEGORY_CODE;
    categorySystem = LABORATORY_CATEGROY_SYSTEM;
    categoryDisplay = LABORATORY_CATEGORY_DISPLAY;
    observationCodeSystem = LOINC_ORG_SYSTEM;
    pathogenDetectionId = generateUuidString();
    interpretationSystem = OBSERVATION_INTERPRETATION_SYSTEM;
    methodSystem = SYSTEM_SNOMED;
    return this;
  }

  public PathogenDetectionDataBuilder setStatusFinal() {
    status = Observation.ObservationStatus.FINAL;
    return this;
  }

  public PathogenDetectionDataBuilder setStatusAmended() {
    status = Observation.ObservationStatus.AMENDED;
    return this;
  }

  public PathogenDetectionDataBuilder setStatusCorrected() {
    status = Observation.ObservationStatus.CORRECTED;
    return this;
  }

  public PathogenDetectionDataBuilder setInterpretationPositiv() {
    interpretationCode = "POS";
    interpretationDisplay = "Positive";
    interpretationSystem = OBSERVATION_INTERPRETATION_SYSTEM;
    return this;
  }

  public PathogenDetectionDataBuilder setInterpretationNegativ() {
    interpretationCode = "NEG";
    interpretationDisplay = "Negative";
    interpretationSystem = OBSERVATION_INTERPRETATION_SYSTEM;
    return this;
  }

  /**
   * creates a url and adds the given code
   *
   * @param pathogenCode the pathogen code, e.g. cvdp
   * @return
   */
  public PathogenDetectionDataBuilder setProfileUrlHelper(String pathogenCode) {
    profileUrl = PATHOGEN_DETECTION_BASE_URL + pathogenCode.toUpperCase();
    return this;
  }

  public Observation build() {
    Observation observation = new Observation();
    observation.setStatus(status);

    observation.setSubject(new Reference(notifiedPerson));
    observation.setSpecimen(new Reference(specimen));
    observation.setId(pathogenDetectionId);
    observation.setStatus(status);

    observation.setMeta(new Meta().addProfile(profileUrl));

    observation.setValue(value);

    CodeableConcept codeableConceptLaboratory = new CodeableConcept();
    codeableConceptLaboratory.addCoding(new Coding(categorySystem, categoryCode, categoryDisplay));
    observation.addCategory(codeableConceptLaboratory);

    CodeableConcept codeableConceptInterpretation = new CodeableConcept();
    codeableConceptInterpretation.addCoding(
        new Coding(interpretationSystem, interpretationCode, interpretationDisplay));
    observation.addInterpretation(codeableConceptInterpretation);

    Coding coding = new Coding(observationCodeSystem, observationCodeCode, observationCodeDisplay);
    observation.setCode(new CodeableConcept().addCoding(coding));

    CodeableConcept codeableConceptMethod = new CodeableConcept();
    codeableConceptMethod.addCoding(new Coding(methodSystem, methodCode, methodDisplay));
    observation.setMethod(codeableConceptMethod);

    return observation;
  }

  @Deprecated(since = "1.2.1")
  public Observation buildPathogenDetection(Patient notifiedPerson, Specimen specimen) {
    Observation observation = new Observation();
    observation.setStatus(status);

    observation.setSubject(new Reference(notifiedPerson));
    observation.setSpecimen(new Reference(specimen));
    observation.setId(pathogenDetectionId);
    observation.setStatus(status);

    observation.setMeta(new Meta().addProfile(profileUrl));

    observation.setValue(value);

    CodeableConcept codeableConceptLaboratory = new CodeableConcept();
    codeableConceptLaboratory.addCoding(new Coding(categorySystem, categoryCode, categoryDisplay));
    observation.addCategory(codeableConceptLaboratory);

    CodeableConcept codeableCOnceptInterpretation = new CodeableConcept();
    codeableCOnceptInterpretation.addCoding(
        new Coding(interpretationSystem, interpretationCode, interpretationDisplay));
    observation.addInterpretation(codeableCOnceptInterpretation);

    Coding coding = new Coding(observationCodeSystem, observationCodeCode, observationCodeDisplay);
    observation.setCode(new CodeableConcept().addCoding(coding));

    CodeableConcept codeableConceptMethod = new CodeableConcept();
    codeableConceptMethod.addCoding(new Coding(methodSystem, methodCode, methodDisplay));
    observation.setMethod(codeableConceptMethod);

    return observation;
  }

  @Deprecated(since = "1.2.1")
  public Observation buildExamplePathogenDetection(Patient notifiedPerson, Specimen specimen) {

    checkAndSetOberservationCode();
    checkAndSetCategoryCode();
    checkAndSetInterpretationCode();
    checkAndSetMethodCode();
    checkAndSetValueCode();

    pathogenDetectionId = requireNonNullElse(pathogenDetectionId, generateUuidString());
    status = Observation.ObservationStatus.FINAL;

    return buildPathogenDetection(notifiedPerson, specimen);
  }

  @Deprecated(since = "1.2.1")
  private void checkAndSetValueCode() {

    valueSystem = requireNonNullElse(valueSystem, "http://loinc.org");
    valueCode = requireNonNullElse(valueCode, "100156-9");
    valueDisplay =
        requireNonNullElse(
            valueDisplay,
            "SARS-CoV-2 (COVID-19) variant [Type] in Specimen by NAA with probe detection");
  }

  @Deprecated(since = "1.2.1")
  private void checkAndSetCategoryCode() {
    categorySystem =
        requireNonNullElse(
            categorySystem, "http://terminology.hl7.org/CodeSystem/observation-category");
    categoryCode = requireNonNullElse(categoryCode, "laboratory");
    categoryDisplay = requireNonNullElse(categoryDisplay, "Laboratory");
  }

  @Deprecated(since = "1.2.1")
  private void checkAndSetOberservationCode() {
    observationCodeSystem = requireNonNullElse(observationCodeSystem, "http://loinc.org");
    observationCodeCode = requireNonNullElse(observationCodeCode, "100156-9");
    observationCodeDisplay =
        requireNonNullElse(
            observationCodeDisplay,
            "ARS-CoV-2 (COVID-19) variant [Type] in Specimen by NAA with probe detection");
  }

  @Deprecated(since = "1.2.1")
  private void checkAndSetInterpretationCode() {
    interpretationSystem =
        requireNonNullElse(
            interpretationSystem,
            "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation");
    interpretationCode = requireNonNullElse(interpretationCode, "POS");
    interpretationDisplay = requireNonNullElse(interpretationDisplay, "Positive");
  }

  @Deprecated(since = "1.2.1")
  private void checkAndSetMethodCode() {
    methodSystem = requireNonNullElse(methodSystem, "http://snomed.info/sct");
    methodCode = requireNonNullElse(methodCode, "398545005");
    methodDisplay = requireNonNullElse(methodDisplay, "Nucleic acid assay (procedure)");
  }

  @Deprecated(since = "1.2.1")
  public PathogenDetectionDataBuilder addId() {
    return setPathogenDetectionId(generateUuidString());
  }
}
