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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.HumanName;

@Setter
public class HumanNameDataBuilder {
  private String familyName;
  private List<String> givenEntry;
  private HumanName.NameUse use;
  private List<String> prefixList;

  private String text;

  private Salutation salutation;

  public HumanName buildExampleHumanName() {
    familyName = "Mustermann";
    addGivenName("Maxime");
    return buildHumanName();
  }

  public HumanName buildHumanName() {
    HumanName humanName = new HumanName();

    if (familyName != null) {
      humanName.setFamily(familyName);
    }
    if (givenEntry != null) {
      givenEntry.forEach(humanName::addGiven);
    }

    if (use != null) {
      humanName.setUse(use);
    }

    if (prefixList != null) {
      prefixList.forEach(humanName::addPrefix);
    }

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
    if (prefixList != null) {
      prefixList.forEach(allStrings::add);
    }

    if (givenEntry != null) {
      givenEntry.forEach(allStrings::add);
    }

    if (familyName != null) {
      allStrings.add(familyName);
    }

    text = String.join(" ", allStrings);
  }

  public enum Salutation {
    MR,
    MRS
  }
}
