package de.gematik.demis.notification.builder.demis.fhir.notification.types;

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

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;

public enum AddressUse {
  CURRENT("current"),
  ORDINARY("ordinary"),
  PRIMARY("primary");

  public static final String ADDRESS_USE_EXTENSION_URL =
      "https://demis.rki.de/fhir/StructureDefinition/AddressUse";
  public static final String ADDRESS_USE_SYSTEM_URL =
      "https://demis.rki.de/fhir/CodeSystem/addressUse";
  @Nonnull private final String code;

  AddressUse(@Nonnull final String primary) {
    this.code = primary;
  }

  /**
   * @return a new instance of this AddressExtension as FHIR Extension.
   */
  @Nonnull
  public Extension asExtension() {
    return new Extension()
        .setUrl(ADDRESS_USE_EXTENSION_URL)
        .setValue(new Coding(ADDRESS_USE_SYSTEM_URL, code, null));
  }

  /**
   * @return predicate that compares the given extension with the current representation
   */
  public Predicate<Extension> matchesExtension() {
    return extension -> {
      if (extension.getValue() instanceof Coding coding) {
        return ADDRESS_USE_EXTENSION_URL.equals(extension.getUrl())
            && ADDRESS_USE_SYSTEM_URL.equals(coding.getSystem())
            && this.code.equals(coding.getCode());
      }
      return false;
    };
  }
}
