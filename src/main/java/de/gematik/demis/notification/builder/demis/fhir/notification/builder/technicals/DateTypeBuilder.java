package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.DateType;

@Setter
public class DateTypeBuilder implements FhirObjectBuilder {

  private String text;

  public static DateType toDateType(Date date) {
    return new DateType(date);
  }

  public static DateType toDateType(LocalDate date) {
    return toDateType(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
  }

  @Override
  public DateType build() {
    final String date = StringUtils.trimToNull(this.text);
    if (date == null) {
      return null;
    }
    return toDateType(date);
  }

  private static DateType toDateType(String date) {
    final int length = date.length();
    if (length != 10) {
      throw new IllegalArgumentException("Illegal length. Expected: 10 Actual: " + date);
    }
    if (date.contains("-")) {
      return toDateType(LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(date)));
    }
    if (date.contains(".")) {
      return toDateType(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
    throw new IllegalArgumentException("Illegal date input: " + date);
  }
}
