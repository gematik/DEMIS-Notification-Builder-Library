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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.hl7.fhir.r4.model.DateTimeType;

@Setter
public class DateTimeTypeBuilder implements FhirObjectBuilder {

  public static final DateTimeFormatter DATE_FORMATTER_GERMAN =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");
  public static final DateTimeFormatter DATE_TIME_FORMATTER_GERMAN =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private String text;

  @Override
  public DateTimeType build() {
    final String dateTime = StringUtils.trimToNull(this.text);
    if (dateTime == null) {
      return null;
    }
    return toDateTimeType(dateTime);
  }

  /**
   * Create FHIR timestamp
   *
   * @param dateTime textual timestamp
   * @return FHIR timestamp
   */
  private static DateTimeType toDateTimeType(String dateTime) {
    if (isIso8601(dateTime)) {
      return new DateTimeType(dateTime);
    }
    return parseGermanTimestamp(dateTime);
  }

  private static DateTimeType parseGermanTimestamp(String dateTime) {
    int fullStops = StringUtils.countMatches(dateTime, '.');
    int colons = StringUtils.countMatches(dateTime, ':');
    DateTimeType fhirTimestamp = new DateTimeType();
    switch (colons) {
      case 0:
        {
          parseGermanLocalDate(dateTime, fullStops, fhirTimestamp);
          break;
        }
      case 1:
        {
          parseGermanLocalDateTime(dateTime, fhirTimestamp);
          break;
        }
      default:
        {
          throw new IllegalArgumentException("Unsupported local date and time format: " + dateTime);
        }
    }
    return fhirTimestamp;
  }

  private static boolean isIso8601(String dateTime) {
    return Strings.CS.containsAny(dateTime, "T", "+", "-");
  }

  private static void parseGermanLocalDateTime(String dateTime, DateTimeType fhirTimestamp) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_GERMAN);
    fhirTimestamp.setValue(toDate(localDateTime), TemporalPrecisionEnum.MINUTE);
    fhirTimestamp.setTimeZone(null);
  }

  private static void parseGermanLocalDate(String date, int fullStops, DateTimeType fhirTimestamp) {
    switch (fullStops) {
      case 0:
        {
          fhirTimestamp.setPrecision(TemporalPrecisionEnum.YEAR);
          fhirTimestamp.setYear(Integer.parseInt(date));
          break;
        }
      case 1:
        {
          fhirTimestamp.setPrecision(TemporalPrecisionEnum.MONTH);
          fhirTimestamp.setDay(1);
          String[] monthAndYear = date.split("\\.");
          fhirTimestamp.setMonth(Integer.parseInt(monthAndYear[0]) - 1);
          fhirTimestamp.setYear(Integer.parseInt(monthAndYear[1]));
          break;
        }
      case 2:
        {
          LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER_GERMAN);
          fhirTimestamp.setValue(toDate(localDate), TemporalPrecisionEnum.DAY);
          break;
        }
      default:
        {
          throw new IllegalArgumentException("Unsupported local date format: " + date);
        }
    }
  }

  private static Date toDate(final LocalDate date) {
    if (date == null) {
      return null;
    }
    return toDate(date.atStartOfDay());
  }

  private static Date toDate(final LocalDateTime time) {
    if (time == null) {
      return null;
    }
    return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
  }
}
