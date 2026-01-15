package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import org.hl7.fhir.r4.model.HumanName;
import org.junit.jupiter.api.Test;

class HumanNameDataBuilderTest {

  @Test
  void shouldSetAllGivenData() {
    HumanName humanName =
        new HumanNameDataBuilder()
            .addPrefix("Dr.")
            .setSalutation(HumanNameDataBuilder.Salutation.MR)
            .setFamilyName("someFamilyName")
            .addGivenName("someGivenName")
            .addGivenName("RomyO")
            .setUse(HumanName.NameUse.OFFICIAL)
            .build();

    assertThat(humanName.getFamily()).isEqualTo("someFamilyName");
    assertThat(humanName.getGiven()).hasSize(2);
    assertThat(humanName.getGiven().get(0)).hasToString("someGivenName");
    assertThat(humanName.getUse()).isEqualTo(HumanName.NameUse.OFFICIAL);
    assertThat(humanName.getText()).isEqualTo("Herr Dr. someGivenName RomyO someFamilyName");
  }

  @Test
  void addPrefix_shouldIgnoreNull() {
    HumanName humanName =
        new HumanNameDataBuilder()
            .addPrefix(null)
            .addGivenName("Peter")
            .setFamilyName("Parker")
            .build();
    String text = humanName.getText();
    assertThat(text).isEqualTo("Peter Parker");
  }

  @Test
  void addGivenName_shouldIgnoreNull() {
    HumanName humanName =
        new HumanNameDataBuilder()
            .addPrefix("Herr")
            .addGivenName(null)
            .setFamilyName("Parker")
            .build();
    String text = humanName.getText();
    assertThat(text).isEqualTo("Herr Parker");
  }

  @Test
  void setPrefixList_shouldIgnoreNullElements() {
    List<String> prefixes = new LinkedList<>();
    prefixes.add("Frau");
    prefixes.add(null);
    prefixes.add("Dr.");
    HumanName humanName =
        new HumanNameDataBuilder()
            .setPrefixList(prefixes)
            .addGivenName("Petra")
            .setFamilyName("Parker")
            .build();
    String text = humanName.getText();
    assertThat(text).isEqualTo("Frau Dr. Petra Parker");
  }

  @Test
  void setGivenEntry_shouldIgnoreNullElements() {
    List<String> givenNames = new LinkedList<>();
    givenNames.add(null);
    givenNames.add(null);
    givenNames.add("Hans");
    givenNames.add(null);
    givenNames.add("Jürgen");
    HumanName humanName =
        new HumanNameDataBuilder().setGivenEntry(givenNames).setFamilyName("Parker").build();
    String text = humanName.getText();
    assertThat(text).isEqualTo("Hans Jürgen Parker");
  }
}
