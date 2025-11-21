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

import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Type;

public final class Utils {

  private Utils() {}

  public static String generateUuidString() {
    return generateUuid().toString();
  }

  public static UUID generateUuid() {
    return UUID.randomUUID();
  }

  public static Date getCurrentDate() {
    return new Date();
  }

  public static DateTimeType getCurrentDateTime() {
    return new DateTimeType(new Date());
  }

  @Nonnull
  public static Predicate<Type> hasFhirType(@Nonnull final String search) {
    return type -> type.fhirType().equals(search);
  }

  @Nonnull
  @SuppressWarnings("ConstantConditions")
  public static String getShortReferenceOrUrnUuid(@Nonnull IBaseResource resource) {
    if (resource == null) { // NOSONAR
      throw new IllegalArgumentException("Resource must not be null");
    }
    if (resource.getIdElement() == null
        || resource.getIdElement().getValue() == null
        || resource.getIdElement().getIdPart() == null) {
      throw new IllegalStateException("Resource does not have a valid id");
    }
    String value = resource.getIdElement().getValue();
    if (value.startsWith("urn:uuid")) {
      return value;
    } else {
      return resource.fhirType() + "/" + resource.getIdElement().getIdPart();
    }
  }
}
