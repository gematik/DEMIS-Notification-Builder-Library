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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = false)
@NoArgsConstructor
@AllArgsConstructor
public class IgsOverviewData {

  private String meldetatbestand;
  private String speciesCode;
  private String species;
  private String labSequenceId;
  private String demisNotificationId;
  private String status;
  private String demisSequenceId;
  private Date dateOfSampling;
  private Date dateOfReceiving;
  private Date dateOfSequencing;
  private Date dateOfSubmission;
  private String sequencingInstrument;
  private String sequencingPlatform;
  private String adapterSubstance;
  private String primerSchemeSubstance;
  private String sequencingStrategy;
  private String isolationSourceCode;
  private String isolationSource;
  private String hostSex;
  private String hostBirthMonth;
  private String hostBirthYear;
  private String sequencingReason;
  private String geographicLocation;
  private String isolate;
  private String author;
  private String nameAmpProtocol;
  private String primeDiagnosticLabDemisLabId;
  private String primeDiagnosticLabName;
  private String primeDiagnosticLabEmail;
  private String primeDiagnosticLabAddress;
  private String primeDiagnosticLabPostalCode;
  private String primeDiagnosticLabCity;
  private String primeDiagnosticLabFederalState;
  private String primeDiagnosticLabCountry;
  private String sequencingLabDemisLabId;
  private String sequencingLabName;
  private String sequencingLabEmail;
  private String sequencingLabAddress;
  private String sequencingLabPostalCode;
  private String sequencingLabCity;
  private String sequencingLabFederalState;
  private String sequencingLabCountry;
  private String repositoryName;
  private String repositoryLink;
  private String repositoryId;
  private Date uploadDate;
  private String uploadStatus;
  private String uploadSubmitter;
  private String fileOneName;
  private String fileOneSha256Sum;
  private String fileOneDocumentReference;
  private String fileTwoName;
  private String fileTwoSha256Sum;
  private String fileTwoDocumentReference;
}
