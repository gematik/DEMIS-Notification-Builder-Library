package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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

import java.util.List;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Element;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;

public class Organizations {
  private Organizations() {}

  /** Extract organizations referenced by the {@link Extension} on the given {@link Element}. */
  @Nonnull
  public static List<Organization> fromExtension(@Nonnull final Element resource) {
    return resource.getExtension().stream()
        .filter(Extension::hasValue)
        .map(Extension::getValue)
        .filter(Utils.hasFhirType("Reference"))
        .map(Reference.class::cast)
        .map(Reference::getResource)
        .filter(Organization.class::isInstance)
        .map(Organization.class::cast)
        .toList();
  }
}
