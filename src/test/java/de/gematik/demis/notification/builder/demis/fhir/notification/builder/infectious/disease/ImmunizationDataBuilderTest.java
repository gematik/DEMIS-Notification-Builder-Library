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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Immunization;
import org.junit.jupiter.api.Test;

public final class ImmunizationDataBuilderTest {

  public static ImmunizationDataBuilder createCvddBuilder() {
    ImmunizationDataBuilder builder = new ImmunizationDataBuilder();
    builder.setId(generateUuidString());
    builder.setProfileUrl(PROFILE_IMMUNIZATION_INFORMATION_CVDD);
    builder.setStatus("completed");
    builder.setVaccineCode(
        new Coding(
            "https://ec.europa.eu/health/documents/community-register/html/",
            "EU/1/20/1528",
            "Comirnaty"));
    builder.setOccurrence(new DateTimeType("2021-03-15"));
    return builder;
  }

  @Test
  void setDefaults_shouldSetValues() {
    Immunization immunization = new ImmunizationDataBuilder().setDefaults().build();
    assertThat(immunization.getId()).isNotNull();
    assertThat(immunization.getStatus()).isSameAs(Immunization.ImmunizationStatus.COMPLETED);
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    Immunization.ImmunizationStatus status = Immunization.ImmunizationStatus.COMPLETED;
    Immunization immunization =
        new ImmunizationDataBuilder().setId(id).setStatusStandard(status).setDefaults().build();
    assertThat(immunization.getId()).isEqualTo(id);
    assertThat(immunization.getStatus()).isSameAs(status);
  }
}
