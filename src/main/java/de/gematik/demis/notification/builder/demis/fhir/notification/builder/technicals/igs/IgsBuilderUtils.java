package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;

/** Utility class for IGS Builder */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IgsBuilderUtils {

  /**
   * Generate an Identifier with system and value
   *
   * @param system - the system of the identifier
   * @param value - the value of the identifier
   * @return the generated Identifier
   */
  public static Identifier generateIdentifier(String system, String value) {
    Identifier identifier = new Identifier();
    if (StringUtils.isNotBlank(system)) {
      identifier.setSystem(system);
    }
    if (StringUtils.isNotBlank(value)) {
      identifier.setValue(value);
    }
    return identifier;
  }

  /**
   * Generate a CodeableConcept with a single Coding
   *
   * @param codingSystem - the system of the coding
   * @param codingCode - the code of the coding
   * @param codingDisplay - the display of the coding
   * @return the generated CodeableConcept
   */
  public static CodeableConcept generateCodeableConcept(
      String codingSystem, String codingCode, String codingDisplay) {
    Coding codeCoding = new Coding(codingSystem, codingCode, codingDisplay);
    CodeableConcept codeCodingCodeableConcept = new CodeableConcept();
    codeCodingCodeableConcept.setCoding(List.of(codeCoding));
    return codeCodingCodeableConcept;
  }
}
