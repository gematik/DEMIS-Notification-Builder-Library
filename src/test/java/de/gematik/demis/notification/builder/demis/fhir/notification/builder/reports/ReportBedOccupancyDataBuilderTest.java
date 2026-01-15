package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

class ReportBedOccupancyDataBuilderTest {

  private final ReportBedOccupancyDataBuilder builder = new ReportBedOccupancyDataBuilder();
  private final PractitionerRole notifierRole = new PractitionerRole();
  private final QuestionnaireResponse statisticInformationBedOccupancy =
      new QuestionnaireResponse();

  @Test
  void setDefaults_shouldSetValues() {
    builder.setDefaults();
    Composition composition = builder.build();
    Coding type = composition.getType().getCodingFirstRep();
    assertThat(type.getSystem()).as("type system").isEqualTo("http://loinc.org");
    assertThat(type.getCode()).as("type code").isEqualTo("80563-0");
    Coding category = composition.getCategoryFirstRep().getCodingFirstRep();
    assertThat(category.getSystem())
        .as("category system")
        .isEqualTo("https://demis.rki.de/fhir/CodeSystem/reportCategory");
    assertThat(category.getCode()).as("category code").isEqualTo("bedOccupancyReport");
    assertThat(composition.getTitle())
        .as("title")
        .isEqualTo("Bericht (Krankenhausbettenbelegungsstatistik)");
    assertThat(composition.getId()).as("ID").isNotNull();
    assertThat(composition.getDate()).as("date").isNotNull();
    assertThat(composition.getStatus()).as("status").isEqualTo(Composition.CompositionStatus.FINAL);
    assertThat(composition.getIdentifier()).as("notification ID").isNotNull();
  }

  @Test
  void setDefaults_shouldKeepValues() throws ParseException {
    String id = "init-id";
    String identifier = "init-identifier";
    String title = "init-title";
    String typeCode = "init-type-code";
    String categoryCode = "init-category-code";
    Date currentDate = new SimpleDateFormat("dd.MM.yyyy").parse("13.04.2011");
    Composition.CompositionStatus status = Composition.CompositionStatus.PRELIMINARY;
    Composition composition =
        new ReportBedOccupancyDataBuilder()
            .setId(id)
            .setIdentifierAsNotificationId(identifier)
            .setTitle(title)
            .setType(new Coding().setCode(typeCode))
            .setCategory(new Coding().setCode(categoryCode))
            .setCurrentDate(currentDate)
            .setStatusStandard(status)
            .setDefaults()
            .build();
    assertThat(composition.getId()).isEqualTo(id);
    assertThat(composition.getIdentifier().getValue()).isEqualTo(identifier);
    assertThat(composition.getTitle()).isEqualTo(title);
    assertThat(composition.getType().getCodingFirstRep().getCode()).isEqualTo(typeCode);
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo(categoryCode);
    assertThat(composition.getDate()).isEqualTo(currentDate);
  }

  @Test
  void shouldSetNotificationId() {
    builder.setIdentifierAsNotificationId("notificationId");
    Composition composition = builder.build();
    Identifier notificationId = composition.getIdentifier();
    assertThat(notificationId.getSystem())
        .as("notificationId system")
        .isEqualTo(DemisConstants.NOTIFICATION_ID_SYSTEM);
    assertThat(notificationId.getValue()).as("notificationId value").isEqualTo("notificationId");
  }

  @Test
  void shouldCreateCompositionWithGivenTypeData() {

    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setType(
        new Coding().setCode("typeCode").setSystem("typeSystem").setDisplay("typeDisplay"));

    Composition composition = builder.build();

    Coding code = composition.getType().getCodingFirstRep();
    assertThat(code.getCode()).isEqualTo("typeCode");
    assertThat(code.getSystem()).isEqualTo("typeSystem");
    assertThat(code.getDisplay()).isEqualTo("typeDisplay");
  }

  @Test
  void shouldCreateCompositionWithGivenCategoryData() {
    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setCategory(
        new Coding()
            .setCode("categoryCode")
            .setSystem("categorySystem")
            .setDisplay("categoryDisplay"));

    Composition composition = builder.build();

    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("categoryCode");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("categorySystem");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("categoryDisplay");
  }

  @Test
  void shouldCreateCompositionWithGivenSubjectData() {
    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setSubject(new Identifier().setSystem("subjectSystem").setValue("subjectValue"));

    Composition composition = builder.build();

    assertThat(composition.getSubject().getIdentifier().getSystem()).isEqualTo("subjectSystem");
    assertThat(composition.getSubject().getIdentifier().getValue()).isEqualTo("subjectValue");
  }

  @Test
  void shouldCreateCompositionWithGivenIdentifierData() {
    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setIdentifier(
        new Identifier().setSystem("identifierSystem").setValue("identifierValue"));

    Composition composition = builder.build();

    assertThat(composition.getIdentifier().getSystem()).isEqualTo("identifierSystem");
    assertThat(composition.getIdentifier().getValue()).isEqualTo("identifierValue");
  }

  @Test
  void shouldCreateCompositionWithGivenStatusData() {
    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setStatus("final");

    Composition composition = builder.build();

    assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
  }

  @Test
  void shouldCreateCompositionWithGivenTitleIdAndDateData() {
    builder.setNotifierRole(this.notifierRole);
    builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);

    builder.setTitle("title");
    builder.setId("id");
    Date date = new Date();
    builder.setCurrentDate(date);

    Composition composition = builder.build();

    assertThat(composition.getTitle()).isEqualTo("title");
    assertThat(composition.getId()).isEqualTo("id");
    assertThat(composition.getDate()).isEqualTo(date);
  }

  @Test
  void shouldSetNotifierRole() {
    builder.setNotifierRole(this.notifierRole);
    Composition composition = builder.build();
    assertThat(composition.getAuthor().getFirst().getResource())
        .as("notifier role")
        .isEqualTo(this.notifierRole);
  }

  @Test
  void shouldSetStatistics() {
    this.builder.setStatisticInformationBedOccupancy(this.statisticInformationBedOccupancy);
    Composition composition = builder.build();
    assertThat(composition.getSectionFirstRep().getEntryFirstRep().getResource())
        .as("bed occupancy statistics")
        .isEqualTo(this.statisticInformationBedOccupancy);
  }
}
