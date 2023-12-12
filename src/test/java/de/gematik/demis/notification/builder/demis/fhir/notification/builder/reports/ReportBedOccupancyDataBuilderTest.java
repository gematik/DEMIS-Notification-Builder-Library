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
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportBedOccupancyDataBuilderTest {

  private PractitionerRole notifierRole;
  private QuestionnaireResponse statisticInformationBedOccupancy;

  @BeforeEach
  void setUp() {
    notifierRole = new PractitionerRole();
    statisticInformationBedOccupancy = new QuestionnaireResponse();
  }

  @Test
  void shouldCreateCompositionWithGivenTypeData() {

    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setTypeCode("typeCode");
    reportBundleDataBuilder.setTypeSystem("typeSystem");
    reportBundleDataBuilder.setTypeDisplay("typeDisplay");

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getType().getCodingFirstRep().getCode()).isEqualTo("typeCode");
    assertThat(composition.getType().getCodingFirstRep().getSystem()).isEqualTo("typeSystem");
    assertThat(composition.getType().getCodingFirstRep().getDisplay()).isEqualTo("typeDisplay");
  }

  @Test
  void shouldCreateCompositionWithGivenCategoryData() {
    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setCategoryCode("categoryCode");
    reportBundleDataBuilder.setCategorySystem("categorySystem");
    reportBundleDataBuilder.setCategoryDisplay("categoryDisplay");

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("categoryCode");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("categorySystem");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("categoryDisplay");
  }

  @Test
  void shouldCreateCompositionWithGivenSubjectData() {
    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setSubjectSystem("subjectSystem");
    reportBundleDataBuilder.setSubjectValue("subjectValue");

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getSubject().getIdentifier().getSystem()).isEqualTo("subjectSystem");
    assertThat(composition.getSubject().getIdentifier().getValue()).isEqualTo("subjectValue");
  }

  @Test
  void shouldCreateCompositionWithGivenIdentifierData() {
    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setIdentifierSystem("identifierSystem");
    reportBundleDataBuilder.setIdentifierValue("identifierValue");

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getIdentifier().getSystem()).isEqualTo("identifierSystem");
    assertThat(composition.getIdentifier().getValue()).isEqualTo("identifierValue");
  }

  @Test
  void shouldCreateCompositionWithGivenStatusData() {
    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setStatus("final");

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
  }

  @Test
  void shouldCreateCompositionWithGivenTitleIdAndDateData() {
    ReportBedOccupancyDataBuilder reportBundleDataBuilder = new ReportBedOccupancyDataBuilder();

    reportBundleDataBuilder.setTitle("title");
    reportBundleDataBuilder.setId("id");
    Date date = new Date();
    reportBundleDataBuilder.setCurrentDate(date);

    Composition composition =
        reportBundleDataBuilder.buildReportBedOccupancy(
            notifierRole, statisticInformationBedOccupancy);

    assertThat(composition.getTitle()).isEqualTo("title");
    assertThat(composition.getId()).isEqualTo("id");
    assertThat(composition.getDate()).isEqualTo(date);
  }
}
