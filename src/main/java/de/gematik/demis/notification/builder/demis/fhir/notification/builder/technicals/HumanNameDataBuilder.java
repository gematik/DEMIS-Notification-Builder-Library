package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import org.hl7.fhir.r4.model.HumanName;

@Setter
public class HumanNameDataBuilder implements FhirObjectBuilder {

  private String familyName;
  private List<String> givenEntry;
  private HumanName.NameUse use;
  private List<String> prefixList;
  private String text;
  private Salutation salutation;

  public static HumanName with(
      final String prefix, final String givenName, final String familyName) {
    return new HumanNameDataBuilder()
        .setFamilyName(familyName)
        .addPrefix(prefix)
        .addGivenName(givenName)
        .build();
  }

  @Override
  public HumanName build() {
    final var humanName = new HumanName();
    if (familyName != null) {
      humanName.setFamily(familyName);
    }
    givenNames().forEach(humanName::addGiven);
    if (use != null) {
      humanName.setUse(use);
    }
    prefixes().forEach(humanName::addPrefix);
    createText();
    humanName.setText(text);
    return humanName;
  }

  public HumanNameDataBuilder addPrefix(String prefix) {
    if (this.prefixList == null) {
      this.prefixList = new ArrayList<>();
    }
    this.prefixList.add(prefix);
    return this;
  }

  public HumanNameDataBuilder addGivenName(String givenName) {
    if (this.givenEntry == null) {
      this.givenEntry = new ArrayList<>();
    }
    this.givenEntry.add(givenName);
    return this;
  }

  private void createText() {
    List<String> allStrings = new ArrayList<>();
    if (salutation != null) {
      switch (salutation) {
        case MR:
          allStrings.add("Herr");
          break;
        case MRS:
          allStrings.add("Frau");
          break;
        default:
          throw new IllegalStateException("unkown salutation type: " + salutation);
      }
    }
    prefixes().forEach(allStrings::add);
    givenNames().forEach(allStrings::add);
    if (familyName != null) {
      allStrings.add(familyName);
    }
    text = String.join(" ", allStrings);
  }

  private List<String> prefixes() {
    if (this.prefixList == null) {
      return Collections.emptyList();
    }
    return this.prefixList.stream().filter(Objects::nonNull).toList();
  }

  private List<String> givenNames() {
    if (this.givenEntry == null) {
      return Collections.emptyList();
    }
    return this.givenEntry.stream().filter(Objects::nonNull).toList();
  }

  public enum Salutation {
    MR,
    MRS
  }
}
