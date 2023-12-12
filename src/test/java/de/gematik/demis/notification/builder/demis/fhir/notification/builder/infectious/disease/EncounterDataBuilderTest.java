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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class EncounterDataBuilderTest {

  private static final String EXPECTED_ENCOUNTER =
      "{\"resourceType\":\"Encounter\",\"id\":\"cf424cfb-8809-3b51-8ab2-d8bf979cbe8d\",\"meta\":{\"profile\":[\"https://demis.rki.de/fhir/StructureDefinition/Hospitalization\"]},\"extension\":[{\"url\":\"https://demis.rki.de/fhir/StructureDefinition/HospitalizationRegion\",\"valueCodeableConcept\":{\"coding\":[{\"system\":\"https://demis.rki.de/fhir/CodeSystem/geographicRegion\",\"code\":\"13000016\",\"display\":\"Thüringen\"}]}}],\"status\":\"in-progress\",\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\"IMP\",\"display\":\"inpatient encounter\"},\"serviceType\":{\"coding\":[{\"system\":\"https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType\",\"code\":\"0800\",\"display\":\"Pneumologie\"}]},\"subject\":{\"reference\":\"Patient/ac163863-fefa-3ce2-8511-7a4150d6ad43\"},\"period\":{\"start\":\"2021-03-04T00:00:00+01:00\"},\"serviceProvider\":{\"reference\":\"Organization/7b1cb8d3-f4bb-3cf6-8c0e-01e6657a13ea\"}}";

  @Test
  void shouldCreateExpectedEncounter() {
    Patient patient = new Patient();
    patient.setId("ac163863-fefa-3ce2-8511-7a4150d6ad43");

    Organization organization = new Organization();
    organization.setId("7b1cb8d3-f4bb-3cf6-8c0e-01e6657a13ea");

    Encounter encounter =
        new EncounterDataBuilder()
            .setStatus("inprogress")
            .setPeriodStart("04.03.2021")
            .setId("cf424cfb-8809-3b51-8ab2-d8bf979cbe8d")
            .setClassSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
            .setClassCode("IMP")
            .setClassDisplay("inpatient encounter")
            .setProfileUrl("https://demis.rki.de/fhir/StructureDefinition/Hospitalization")
            .setHospitalizationRegionExtensionUrl(
                "https://demis.rki.de/fhir/StructureDefinition/HospitalizationRegion")
            .setHospitalizationRegionCode("13000016")
            .setHospitalizationRegionSystem("https://demis.rki.de/fhir/CodeSystem/geographicRegion")
            .setHospitalizationRegionDisplay("Thüringen")
            .setServiceTypeCode("0800")
            .setServiceTypeDisplay("Pneumologie")
            .setServiceTypeSystem("https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType")
            .buildHospitalization(patient, organization);

    FhirParser fhirParser = new FhirParser(FhirContext.forR4());
    String s = fhirParser.encodeToJson(encounter);

    assertThat(s).isEqualTo(EXPECTED_ENCOUNTER);
  }
}
