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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;

class ReportBundleDataBuilderTest {

  @Test
  void shouldCreateBundleExample() {

    Bundle bundle = new ReportBundleDataBuilder().buildExampleReportBundle();

    assertThat(bundle).isNotNull();
  }

  @Test
  void shouldCreateBundleWithGivenData() {
    ReportBundleDataBuilder reportBundleDataBuilder = new ReportBundleDataBuilder();

    reportBundleDataBuilder.setProfileReportBundle(DemisConstants.PROFILE_REPORT_BUNDLE);
    PractitionerRole notiferRole = new NotifierDataBuilder().buildReportExampleNotifierData();
    reportBundleDataBuilder.setNotifierRole(notiferRole);
    QuestionnaireResponse statisticInformationBedOccupancy =
        new StatisticInformationBedOccupancyDataBuilder()
            .buildExampleStatisticInformationBedOccupancy();
    reportBundleDataBuilder.setStatisticInformationBedOccupancy(statisticInformationBedOccupancy);

    Composition reportBedOccupancy =
        new ReportBedOccupancyDataBuilder()
            .buildReportBedOccupancy(notiferRole, statisticInformationBedOccupancy);
    reportBundleDataBuilder.setReportBedOccupancy(reportBedOccupancy);

    reportBundleDataBuilder.setIdentifierValue("ExampleValueForId");

    Bundle bundle = reportBundleDataBuilder.buildReportBundle();

    assertThat(bundle.getIdentifier().getValue()).isEqualTo("ExampleValueForId");

    List<Bundle.BundleEntryComponent> entry = bundle.getEntry();
    Resource expectedComposition = entry.get(0).getResource();
    assertThat(expectedComposition).isInstanceOf(Composition.class).isEqualTo(reportBedOccupancy);
    Resource expectedNotifierOrganization = entry.get(1).getResource();
    assertThat(expectedNotifierOrganization)
        .isInstanceOf(Organization.class)
        .isEqualTo(notiferRole.getOrganization().getResource());
    Resource expectedNotifierRole = entry.get(2).getResource();
    assertThat(expectedNotifierRole).isInstanceOf(PractitionerRole.class).isEqualTo(notiferRole);
    Resource expectedQuestionnaireResponse = entry.get(3).getResource();
    assertThat(expectedQuestionnaireResponse)
        .isInstanceOf(QuestionnaireResponse.class)
        .isEqualTo(statisticInformationBedOccupancy);
  }
}
