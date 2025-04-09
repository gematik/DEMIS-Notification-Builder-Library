package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Resource;

/** Methods to simplify working with {@link org.hl7.fhir.r4.model.Meta}. */
public class Metas {

  /**
   * @return A set of profiles defined for the Resource's Meta. Null values are removed. An empty
   *     set if no Meta has been set.
   */
  public static Set<String> profilesFrom(@Nonnull final Resource resource) {
    if (!resource.hasMeta()) {
      return Set.of();
    }

    return resource.getMeta().getProfile().stream()
        .map(IPrimitiveType::getValue)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
  }
}
