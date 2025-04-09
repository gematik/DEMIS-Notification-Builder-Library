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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ACT_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_HOSPITALIZATION;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

public final class EncounterDataBuilderTest {

  private static final String EXPECTED_ENCOUNTER =
      "{\"resourceType\":\"Encounter\",\"id\":\"cf424cfb-8809-3b51-8ab2-d8bf979cbe8d\",\"meta\":{\"profile\":[\"https://demis.rki.de/fhir/StructureDefinition/Hospitalization\"]},\"extension\":[{\"url\":\"https://demis.rki.de/fhir/StructureDefinition/HospitalizationRegion\",\"valueCodeableConcept\":{\"coding\":[{\"system\":\"https://demis.rki.de/fhir/CodeSystem/geographicRegion\",\"code\":\"13000016\",\"display\":\"Thüringen\"}]}}],\"status\":\"in-progress\",\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\"IMP\",\"display\":\"inpatient encounter\"},\"serviceType\":{\"coding\":[{\"system\":\"https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType\",\"code\":\"0800\",\"display\":\"Pneumologie\"}]},\"subject\":{\"reference\":\"Patient/ac163863-fefa-3ce2-8511-7a4150d6ad43\"},\"period\":{\"start\":\"2021-03-04\"},\"reasonCode\":[{\"coding\":[{\"system\":\"https://demis.rki.de/fhir/ValueSet/answerSetHospitalizationReason\",\"code\":\"becauseOfDisease\",\"display\":\"Hospitalisiert aufgrund der gemeldeten Krankheit\"}]}],\"serviceProvider\":{\"reference\":\"Organization/7b1cb8d3-f4bb-3cf6-8c0e-01e6657a13ea\"}}";

  public static EncounterDataBuilder createCvddBuilder() {
    EncounterDataBuilder builder = new EncounterDataBuilder();
    builder.setProfileUrl(PROFILE_HOSPITALIZATION);
    builder.setStatus("inprogress");
    builder.setClassification(new Coding(CODE_SYSTEM_ACT_CODE, "IMP", "inpatient encounter"));
    builder.setPeriodStart("22.01.2022");
    return builder;
  }

  @Test
  void shouldCreateExpectedEncounter() {
    Patient patient = new Patient();
    patient.setId("ac163863-fefa-3ce2-8511-7a4150d6ad43");

    Organization organization = new Organization();
    organization.setId("7b1cb8d3-f4bb-3cf6-8c0e-01e6657a13ea");

    Encounter encounter =
        createCvddBuilder()
            .setStatus("in-progress")
            .setPeriodStart("04.03.2021")
            .setId("cf424cfb-8809-3b51-8ab2-d8bf979cbe8d")
            .setProfileUrl("https://demis.rki.de/fhir/StructureDefinition/Hospitalization")
            .setHospitalizationRegion(
                new Coding(
                    "https://demis.rki.de/fhir/CodeSystem/geographicRegion",
                    "13000016",
                    "Thüringen"))
            .setServiceType(
                new Coding(
                    "https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType",
                    "0800",
                    "Pneumologie"))
            .setNotifiedPerson(patient)
            .setServiceProvider(organization)
            .setReason(
                new Coding(
                    "https://demis.rki.de/fhir/ValueSet/answerSetHospitalizationReason",
                    "becauseOfDisease",
                    "Hospitalisiert aufgrund der gemeldeten Krankheit"))
            .build();

    FhirParser fhirParser = new FhirParser(FhirContext.forR4Cached());
    String s = fhirParser.encodeToJson(encounter);

    assertThat(s).isEqualTo(EXPECTED_ENCOUNTER);
  }

  @Test
  void setDefaults_shouldSetValues() {
    Encounter encounter = new EncounterDataBuilder().setDefaults().build();
    assertThat(encounter.getId()).isNotNull();
    assertThat(encounter.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(PROFILE_HOSPITALIZATION);
    Coding classification = encounter.getClass_();
    assertThat(classification.getSystem()).isEqualTo(CODE_SYSTEM_ACT_CODE);
    assertThat(classification.getCode()).isEqualTo("IMP");
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    String profileUrl = "init-profile-url";
    String classificationCode = "init-classification-code";
    Encounter encounter =
        new EncounterDataBuilder()
            .setId(id)
            .setProfileUrl(profileUrl)
            .setClassification(new Coding().setCode(classificationCode))
            .setDefaults()
            .build();
    assertThat(encounter.getId()).isEqualTo(id);
    assertThat(encounter.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
    assertThat(encounter.getClass_().getCode()).isEqualTo(classificationCode);
  }
}
