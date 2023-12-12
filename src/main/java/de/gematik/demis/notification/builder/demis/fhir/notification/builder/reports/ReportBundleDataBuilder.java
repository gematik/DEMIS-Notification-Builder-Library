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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils;
import java.util.Date;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class ReportBundleDataBuilder {

  private Composition reportBedOccupancy;
  private PractitionerRole notifierRole;
  private QuestionnaireResponse statisticInformationBedOccupancy;
  private String profileReportBundle;
  private Date currentDate;
  private String identifierValue;

  public Bundle buildReportBundle() {

    Bundle bundle = new Bundle();
    currentDate = requireNonNullElse(currentDate, getCurrentDate());

    profileReportBundle =
        requireNonNullElse(profileReportBundle, DemisConstants.PROFILE_REPORT_BUNDLE);
    bundle.setMeta(new Meta().addProfile(profileReportBundle).setLastUpdated(currentDate));

    bundle
        .addEntry()
        .setResource(reportBedOccupancy)
        .setFullUrl(ReferenceUtils.getFullUrl(reportBedOccupancy));
    bundle
        .addEntry()
        .setResource((Resource) notifierRole.getOrganization().getResource())
        .setFullUrl(
            ReferenceUtils.getFullUrl((Resource) notifierRole.getOrganization().getResource()));
    bundle.addEntry().setResource(notifierRole).setFullUrl(ReferenceUtils.getFullUrl(notifierRole));
    bundle
        .addEntry()
        .setResource(statisticInformationBedOccupancy)
        .setFullUrl(ReferenceUtils.getFullUrl(statisticInformationBedOccupancy));

    bundle.setId(generateUuidString());
    Identifier identifier = new Identifier();
    identifier.setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    identifier.setValue(identifierValue);
    bundle.setIdentifier(identifier);
    bundle.setType(Bundle.BundleType.DOCUMENT);
    bundle.setTimestamp(currentDate);

    return bundle;
  }

  public Bundle buildExampleReportBundle() {
    profileReportBundle =
        requireNonNullElse(profileReportBundle, DemisConstants.PROFILE_REPORT_BUNDLE);

    statisticInformationBedOccupancy =
        requireNonNullElse(
            statisticInformationBedOccupancy,
            new StatisticInformationBedOccupancyDataBuilder()
                .buildExampleStatisticInformationBedOccupancy());

    notifierRole =
        requireNonNullElse(
            notifierRole, new NotifierDataBuilder().buildReportExampleNotifierData());

    reportBedOccupancy =
        requireNonNullElse(
            reportBedOccupancy,
            new ReportBedOccupancyDataBuilder()
                .buildExampleReportBedOccupancy(notifierRole, statisticInformationBedOccupancy));

    identifierValue = requireNonNullElse(identifierValue, generateUuidString());

    return buildReportBundle();
  }
}
