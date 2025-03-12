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
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/** Categories based on law paragraphs */
public enum NotificationCategory {
  P_6_1("6.1"),
  P_7_1("7.1"),
  P_7_3("7.3"),
  P_7_4("7.4"),
  UNKNOWN("");

  private static final Map<String, NotificationCategory> LOOKUP_MAP = new HashMap<>();

  static {
    for (NotificationCategory category : values()) {
      LOOKUP_MAP.put(category.stringValue, category);
    }
  }

  private final String stringValue;

  NotificationCategory(final String stringValue) {
    this.stringValue = stringValue;
  }

  @JsonCreator
  public static NotificationCategory from(final String value) {
    return LOOKUP_MAP.getOrDefault(value, UNKNOWN);
  }

  @JsonValue
  public String getStringValue() {
    return stringValue;
  }
}
