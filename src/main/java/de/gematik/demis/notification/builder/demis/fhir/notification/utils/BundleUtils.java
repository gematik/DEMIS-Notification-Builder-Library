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

package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

import static java.util.Objects.requireNonNullElse;

import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

/**
 * @deprecated will be removed. new structure for builder incoming.
 * @return
 */
@Deprecated(since = "1.2.1")
public class BundleUtils {

  private BundleUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static void addEntry(Bundle bundle, List<? extends Resource> resources) {
    resources.forEach(resource -> addEntry(bundle, resource));
  }

  public static void addEntry(Bundle bundle, Resource resource) {
    if (resource != null) {
      String fullUrl = requireNonNullElse(ReferenceUtils.getFullUrl(resource), null);
      bundle.addEntry().setResource(resource).setFullUrl(fullUrl);
    }
  }
}
