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
 * #L%
 */

import java.util.Objects;
import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public final class ReferenceUtils {

  private ReferenceUtils() {
    throw new NotImplementedException("you shall not use this constructor");
  }

  public static String getFullUrl(Resource resource) {
    return DemisConstants.DEMIS_RKI_DE_FHIR + createText(resource);
  }

  private static String createText(Resource resource) {
    return resource.getResourceType().toString() + "/" + resource.getId();
  }

  public static Reference internalReference(final Resource toReference) {
    final IIdType idElement = toReference.getIdElement();
    /*
     Some builders only set the idPart and previously extracted the resource type from the resource directly
     it's probably a better id to take all id elements from a single source (the IdType). But adjusting the builders
     might break too much client code.
    */
    final String resourceType =
        String.valueOf(
            Objects.requireNonNullElse(idElement.getResourceType(), toReference.getResourceType()));
    final String join = String.join("/", resourceType, idElement.getIdPart());
    final Reference result = new Reference(join);
    result.setResource(toReference);
    return result;
  }
}
