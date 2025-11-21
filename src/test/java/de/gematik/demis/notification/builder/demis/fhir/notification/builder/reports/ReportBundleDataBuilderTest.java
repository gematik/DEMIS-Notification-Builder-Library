package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;

class ReportBundleDataBuilderTest {

  @Test
  void shouldCreateBundleWithGivenData() {
    ReportBundleDataBuilder builder = new ReportBundleDataBuilder();
    builder.setDefaults();

    String notificationBundleId = "notificationBundleId";
    builder.setIdentifierAsNotificationBundleId(notificationBundleId);

    Organization practitioner =
        new OrganizationBuilder().setDefaults().asNotifierFacility().build();
    PractitionerRole notifierRole =
        new PractitionerRoleBuilder().asNotifierRole().withOrganization(practitioner).build();
    builder.setNotifierRole(notifierRole);

    QuestionnaireResponse bedOccupancy =
        new StatisticInformationBedOccupancyDataBuilder()
            .buildExampleStatisticInformationBedOccupancy();
    builder.setStatisticInformationBedOccupancy(bedOccupancy);

    ReportBedOccupancyDataBuilder composition = new ReportBedOccupancyDataBuilder();
    composition.setDefaults();
    composition.setSubjectAsInekStandortId("subject");
    composition.setStatisticInformationBedOccupancy(bedOccupancy);
    composition.setNotifierRole(notifierRole);
    builder.setReportBedOccupancy(composition.build());

    Bundle bundle = builder.build();

    // verify
    assertThat(bundle.getIdentifier().getValue()).isEqualTo(notificationBundleId);
    List<Bundle.BundleEntryComponent> actualEntries = bundle.getEntry();
    Resource expectedComposition = actualEntries.get(0).getResource();
    assertThat(expectedComposition).isInstanceOf(Composition.class);
    Resource expectedNotifierRole = actualEntries.get(1).getResource();
    assertThat(expectedNotifierRole).isInstanceOf(PractitionerRole.class).isEqualTo(notifierRole);
    Resource expectedNotifierOrganization = actualEntries.get(2).getResource();
    assertThat(expectedNotifierOrganization)
        .isInstanceOf(Organization.class)
        .isEqualTo(notifierRole.getOrganization().getResource());
    Resource expectedQuestionnaireResponse = actualEntries.get(3).getResource();
    assertThat(expectedQuestionnaireResponse)
        .isInstanceOf(QuestionnaireResponse.class)
        .isEqualTo(bedOccupancy);
  }
}
