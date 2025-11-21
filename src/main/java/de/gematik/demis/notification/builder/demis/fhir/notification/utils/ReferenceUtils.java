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
import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public final class ReferenceUtils {

  private ReferenceUtils() {
    throw new NotImplementedException("you shall not use this constructor");
  }

  public static Reference internalReference(final Resource toReference) {
    final IIdType idElement = toReference.getIdElement();
    /*
     It's legal to have a bundle with resources that are only identified by their full url. In that case we don't have
     to have an id. The problem is that we don't have the bundle context available when we build references. Usually
     this shouldn't happen with Bundles build Gematik-internally. However, external providers might send us such Bundles.
     When dealing with these you'll have to ensure that you set an id based on the full url.
    */
    Objects.requireNonNull(
        idElement.getValue(),
        "Can't build a reference when IIdType#value is null. Did you ensure you set an id?");
    final Reference result;
    if (idElement.getValue().startsWith("urn:uuid:")) {
      result = new Reference(idElement.getValue());
    } else {

      /*
       Some builders only set the idPart and previously extracted the resource type from the resource directly
       it's probably a better id to take all id elements from a single source (the IdType). But adjusting the builders
       might break too much client code.
      */
      final String resourceType =
          String.valueOf(
              Objects.requireNonNullElse(
                  idElement.getResourceType(), toReference.getResourceType()));
      final String join = String.join("/", resourceType, idElement.getIdPart());
      result = new Reference(join);
    }
    result.setResource(toReference);
    return result;
  }
}
