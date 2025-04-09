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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.IgsOverviewData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import org.hl7.fhir.r4.model.Resource;

public class IgsTestDataUtils {

  public static final String AUTHOR =
      "Ralf Dürrwald, Stephan Fuchs, Stefan Kroeger, Marianne Wedde, Oliver Drechsel, Aleksandar Radonic, Rene Kmiecinski, Thorsten Wolff";
  public static final String SEQUENCING_REASON = "255226008";
  public static final String FILE_ONE_ID =
      "https://demis.rki.de/fhir/igs/DocumentReference/ecd3f1f0-b6b6-46e0-b721-2d9869ab8195";
  public static final String NOTIFICATION_ID = "f8585efb-1872-4a4f-b88d-8c889e93487b";
  public static final String LAB_IDENTIFIER_VALUE = "A384";
  public static final String SEQUENCING_LAB_IDENTIFIER_VALUE = "10285";
  public static final String SEQUENCING_LAB_NAME =
      "Nationales Referenzzentrum für Influenza, FG17, RKI";
  public static final String SEQUENCING_LAB_ADDRESS_LINE = "Seestr. 10";
  public static final String SEQUENCING_LAB_ADDRESS_POSTAL_CODE = "13353";
  public static final String SEQUENCING_LAB_ADDRESS_CITY = "Berlin";
  public static final String REPOSITORY_EXTENSION_VALUE_DATE_TIME = "2023-02-10";
  public static final String REPOSITORY_EXTENSION_VALUE_STRING = "O Drechsel";
  public static final String REPOSITORY_EXTENSION_VALUE_CODING_CODE = "385645004";
  public static final String REPOSITORY_NAME = "gisaid";
  public static final String REPOSITORY_DATASET_ID = "EPI_ISL_16883504";
  public static final String REPOSITORY_LINK = "https://pubmlst.org/1230423";
  public static final String PRIME_DIAGNOSTIC_LAB_DEMIS_LAB_ID = "987654321";
  public static final String PRIME_DIAGNOSTIC_LAB_ADDRESS = "Dingsweg 321";
  public static final String PRIME_DIAGNOSTIC_LAB_POSTAL_CODE = "13055";
  public static final String PRIME_DIAGNOSTIC_LAB_FEDERAL_STATE = "Berlin";
  public static final String PRIME_DIAGNOSTIC_LAB_NAME = "Primärlabor";
  public static final String DEVICE_NAME_NAME = "GridION";
  public static final String SAMPLING_DATE = "2022-12-29T09:50:00+01:00";
  public static final String RECEIVED_DATE = "2022-12-29T15:40:00+01:00";
  public static final String SEQUENCING_DATE = "2023-01-13T10:40:00+01:00";
  public static final String STATUS = "final";
  public static final String ADAPTER_SUBSTANCE = "TAGCGAGT";
  public static final String SEQUENCING_PLATFORM = "oxford_nanopore";
  public static final String ISOLATION_SOURCE_CODE = "258604001";
  public static final String ISOLATION_SOURCE = "Upper respiratory swab specimen (specimen)";
  public static final String ISOLATE = "119334006";
  public static final String NAME_AMP_PROTOCOL = "ARTICv4";
  public static final String SEQUENCING_STRATEGY = "amplicon";

  private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
  public static final String MELDETATBESTAND = "cvdp";
  public static final String SEQUENCING_LAB_EMAIL = "NRZ-Influenza@rki.de";
  public static final String SEQUENCING_LAB_COUNTRY = "DE";
  public static final String PRIME_DIAGNOSTIC_LAB_EMAIL = "ifsg@primaerlabor-gibt-es-nicht.de";
  public static final String PRIME_DIAGNOSTIC_LAB_COUNTRY = "DE";
  public static final String SPECIES_CODE = "96741-4";
  public static final String SPECIES = "Severe acute respiratory syndrome coronavirus 2 (organism)";
  private static final String GEOGRAPHIC_LOCATION = "104";
  private static DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

  public static IgsOverviewData getIgsTestOverviewData(
      boolean withRepository, boolean withPrimeDiagnosticLab) {
    IgsOverviewData.IgsOverviewDataBuilder igsOverviewDataBuilder =
        IgsOverviewData.builder()
            .sequencingLabDemisLabId(SEQUENCING_LAB_IDENTIFIER_VALUE)
            .sequencingLabName(SEQUENCING_LAB_NAME)
            .sequencingLabAddress(SEQUENCING_LAB_ADDRESS_LINE)
            .sequencingLabPostalCode(SEQUENCING_LAB_ADDRESS_POSTAL_CODE)
            .sequencingLabCity(SEQUENCING_LAB_ADDRESS_CITY)
            .sequencingLabEmail(SEQUENCING_LAB_EMAIL)
            .sequencingLabCountry(SEQUENCING_LAB_COUNTRY)
            .author(AUTHOR)
            .fileOneDocumentReference(FILE_ONE_ID)
            .labSequenceId(LAB_IDENTIFIER_VALUE)
            .sequencingReason(SEQUENCING_REASON)
            .geographicLocation(GEOGRAPHIC_LOCATION)
            .sequencingInstrument(DEVICE_NAME_NAME)
            .sequencingPlatform(SEQUENCING_PLATFORM)
            .demisNotificationId(NOTIFICATION_ID)
            .status(STATUS)
            .dateOfReceiving(parseDateByDateTimeString(RECEIVED_DATE))
            .dateOfSampling(parseDateByDateTimeString(SAMPLING_DATE))
            .dateOfSequencing(parseDateByDateTimeString(SEQUENCING_DATE))
            .adapterSubstance(ADAPTER_SUBSTANCE)
            .meldetatbestand(MELDETATBESTAND)
            .isolationSourceCode(ISOLATION_SOURCE_CODE)
            .isolationSource(ISOLATION_SOURCE)
            .isolate(ISOLATE)
            .nameAmpProtocol(NAME_AMP_PROTOCOL)
            .sequencingStrategy(SEQUENCING_STRATEGY)
            .speciesCode(SPECIES_CODE)
            .species(SPECIES);
    if (withRepository) {
      igsOverviewDataBuilder
          .repositoryName(REPOSITORY_NAME)
          .repositoryId(REPOSITORY_DATASET_ID)
          .repositoryLink(REPOSITORY_LINK)
          .uploadSubmitter(REPOSITORY_EXTENSION_VALUE_STRING)
          .uploadDate(parseDateByDateString(REPOSITORY_EXTENSION_VALUE_DATE_TIME))
          .uploadStatus(REPOSITORY_EXTENSION_VALUE_CODING_CODE);
    }
    if (withPrimeDiagnosticLab) {
      igsOverviewDataBuilder
          .primeDiagnosticLabDemisLabId(PRIME_DIAGNOSTIC_LAB_DEMIS_LAB_ID)
          .primeDiagnosticLabAddress(PRIME_DIAGNOSTIC_LAB_ADDRESS)
          .primeDiagnosticLabPostalCode(PRIME_DIAGNOSTIC_LAB_POSTAL_CODE)
          .primeDiagnosticLabCity(PRIME_DIAGNOSTIC_LAB_FEDERAL_STATE)
          .primeDiagnosticLabName(PRIME_DIAGNOSTIC_LAB_NAME)
          .primeDiagnosticLabEmail(PRIME_DIAGNOSTIC_LAB_EMAIL)
          .primeDiagnosticLabCountry(PRIME_DIAGNOSTIC_LAB_COUNTRY);
    }
    return igsOverviewDataBuilder.build();
  }

  public static <T extends Resource> Optional<T> createOptionalResourceWithId(
      Class<T> clazz, String id) {
    return Optional.of(createResourceWithId(clazz, id));
  }

  public static <T extends Resource> T createResourceWithId(Class<T> clazz, String id) {
    T resource;
    try {
      resource = (T) clazz.getDeclaredConstructor().newInstance();
    } catch (Exception ex) {
      throw new RuntimeException("Error while generating " + clazz.getSimpleName());
    }
    resource.setId(id);
    return resource;
  }

  public static Date parseDateByDateTimeString(String dateTime) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date parseDateByDateString(String dateTime) {
    LocalDate localDate = LocalDate.parse(dateTime, dateFormatter);
    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }
}
