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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class DiseaseDataBuilderTest {

  @Test
  void shouldCreateExampleCVDDDiseaseConditionObject() {

    Patient notifiedPerson = new Patient();
    Condition exampleDisease = new DiseaseDataBuilder().buildCVDDexampleDisease(notifiedPerson);

    assertThat(exampleDisease).isNotNull();

    assertThat(exampleDisease.getSubject().getResource()).isEqualTo(notifiedPerson);

    assertThat(exampleDisease.getMeta().getProfile()).hasSize(1);
    assertThat(exampleDisease.getMeta().getProfile().get(0).getValue())
        .isEqualTo(PROFILE_DISEASE_CVDD);

    assertThat(exampleDisease.getVerificationStatus().getCoding()).hasSize(1);
    assertThat(exampleDisease.getVerificationStatus().getCodingFirstRep().getSystem())
        .isEqualTo(CODE_SYSTEM_CONDITION_VERIFICATION_STATUS);
    assertThat(exampleDisease.getVerificationStatus().getCodingFirstRep().getCode())
        .isEqualTo("confirmed");

    assertThat(exampleDisease.getCode()).isNotNull();
    assertThat(exampleDisease.getCode().getCoding()).hasSize(1);
    assertThat(exampleDisease.getCode().getCoding().get(0).getCode()).isEqualTo("cvdd");
    assertThat(exampleDisease.getCode().getCoding().get(0).getDisplay())
        .isEqualTo("Coronavirus-Krankheit-2019 (COVID-19)");
    assertThat(exampleDisease.getCode().getCoding().get(0).getSystem())
        .isEqualTo(CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY);
  }

  @Test
  void shouldCreateExampleCVDDDiseaseWithRecordDate() {
    Patient notifiedPerson = new Patient();
    Condition exampleDisease =
        new DiseaseDataBuilder()
            .setRecordedDate("2022-01-22")
            .buildCVDDexampleDisease(notifiedPerson);

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(0);
    cal.set(2022, 0, 22, 0, 0, 0);

    assertThat(exampleDisease.getRecordedDate()).isEqualTo(cal.getTime());
  }
}
