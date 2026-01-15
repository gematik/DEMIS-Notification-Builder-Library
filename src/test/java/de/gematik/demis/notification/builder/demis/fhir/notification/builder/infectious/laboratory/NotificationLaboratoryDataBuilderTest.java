package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationLaboratoryDataBuilderTest {

  private static Patient notifiedPerson;
  private static PractitionerRole notifierRole;
  private static DiagnosticReport laboratoryReport;
  private NotificationLaboratoryDataBuilder notificationLaboratoryDataBuilder;

  @BeforeAll
  static void setUpAll() {

    notifiedPerson = new Patient();
    notifiedPerson.setId("1");
    notifierRole = new PractitionerRole();
    notifierRole.setId("2");
    laboratoryReport = new DiagnosticReport();
    laboratoryReport.setId("3");
  }

  @BeforeEach
  void setUp() {
    notificationLaboratoryDataBuilder = new NotificationLaboratoryDataBuilder();
  }

  @Test
  @DisplayName("build with setDefaultCall adds default values")
  void shouldSetDefaultValue() {

    NotificationLaboratoryDataBuilder builder = new NotificationLaboratoryDataBuilder();
    builder.setDefault();
    DiagnosticReport laboratoryReport1 = new DiagnosticReport();
    laboratoryReport1.setId("123");
    builder.setLaboratoryReport(laboratoryReport1);

    Composition composition = builder.build();

    // check data from superclass
    assertThat(composition.getType().getCodingFirstRep().getCode()).isEqualTo("34782-3");
    assertThat(composition.getType().getCodingFirstRep().getDisplay())
        .isEqualTo("Infectious disease Note");
    assertThat(composition.getType().getCodingFirstRep().getSystem()).isEqualTo("http://loinc.org");

    // check laboratory specific data
    assertThat(composition.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/NotificationLaboratory");
    assertThat(composition.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationId");
    assertThat(composition.getCategory()).isEmpty();
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getSystem()).isNull();
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode()).isNull();
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getDisplay()).isNull();

    assertThat(composition.getSection()).hasSize(1);
    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getSystem())
        .isEqualTo("http://loinc.org");
    assertThat(composition.getSectionFirstRep().getEntry()).hasSize(1);
    assertThat(composition.getSectionFirstRep().getEntryFirstRep().getResource())
        .isEqualTo(laboratoryReport1);

    // ids from default values
    assertThat(composition.getIdentifier().getValue()).isNotNull();
    assertThat(composition.getId()).isNotNull();
  }

  @Test
  void shouldSetMandatoryFieldsAsGiven() {
    notificationLaboratoryDataBuilder.setNotifiedPerson(notifiedPerson);
    notificationLaboratoryDataBuilder.setNotifierRole(notifierRole);
    notificationLaboratoryDataBuilder.setLaboratoryReport(laboratoryReport);

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getSubject().getResource()).isEqualTo(notifiedPerson);
    assertThat(composition.getAuthor()).hasSize(1);
    assertThat(composition.getAuthor().get(0).getResource()).isEqualTo(notifierRole);
    assertThat(composition.getSection()).hasSize(1);
    assertThat(composition.getSection().get(0).getEntry()).hasSize(1);
    assertThat(composition.getSection().get(0).getEntry().get(0).getResource())
        .isEqualTo(laboratoryReport);
  }

  @Test
  void shouldSetTypeAsGiven() {

    notificationLaboratoryDataBuilder.setTypeCode("typeCode");
    notificationLaboratoryDataBuilder.setTypeDisplay("typeDisplay");
    notificationLaboratoryDataBuilder.setTypeSystem("typeSystem");

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getType().getCoding()).hasSize(1);
    assertThat(composition.getType().getCoding().get(0).getDisplay()).isEqualTo("typeDisplay");
    assertThat(composition.getType().getCoding().get(0).getCode()).isEqualTo("typeCode");
    assertThat(composition.getType().getCoding().get(0).getSystem()).isEqualTo("typeSystem");
  }

  @Test
  void shoudlSetIdentifierSystemAndValue() {

    notificationLaboratoryDataBuilder.setIdentifierSystem("identifierSystem");
    notificationLaboratoryDataBuilder.setIdentifierValue("identifierValue");

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getIdentifier().getSystem()).isEqualTo("identifierSystem");
    assertThat(composition.getIdentifier().getValue()).isEqualTo("identifierValue");
  }

  @Test
  void shoudlSetCompositionStatus() {

    notificationLaboratoryDataBuilder.setCompositionStatus(Composition.CompositionStatus.AMENDED);

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.AMENDED);
  }

  @Test
  void shoudlSetCompositionId() {

    notificationLaboratoryDataBuilder.setNotificationId("someId");

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getId()).isEqualTo("someId");
  }

  @DisplayName("should set relatesTo values if given")
  @Test
  void shouldSetRelatesTo() {
    NotificationLaboratoryDataBuilder compositionBuilder = new NotificationLaboratoryDataBuilder();
    compositionBuilder.setRelatesToNotificationId("someIdentifier");
    Composition composition = compositionBuilder.build();

    assertThat(composition.getRelatesTo()).hasSize(1);
    assertThat(composition.getRelatesTo().get(0).getCode())
        .isEqualTo(Composition.DocumentRelationshipType.APPENDS);
    assertThat(composition.getRelatesTo().get(0).getTarget()).isNotNull();
    Reference reference = (Reference) composition.getRelatesTo().get(0).getTarget();
    assertThat(reference.getType()).isEqualTo("Composition");
    assertThat(reference.getIdentifier().getValue()).isEqualTo("someIdentifier");
    assertThat(reference.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationId");
  }

  @Test
  @DisplayName("should set section component system, code, and display")
  void shouldSetSectionComponentSystemCodeAndDisplay() {
    notificationLaboratoryDataBuilder.setSectionComponentSystem("system");
    notificationLaboratoryDataBuilder.setSectionComponentCode("code");
    notificationLaboratoryDataBuilder.setSectionComponentDisplay("display");
    notificationLaboratoryDataBuilder.setLaboratoryReport(TestObjects.laboratoryReport());

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getSystem())
        .isEqualTo("system");
    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getCode())
        .isEqualTo("code");
    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getDisplay())
        .isEqualTo("display");
  }

  @Test
  void thatSectionComponentCodingIsOnlySetWhenLaboratoryReportPresent() {
    notificationLaboratoryDataBuilder.setSectionComponentSystem("system");
    notificationLaboratoryDataBuilder.setSectionComponentCode("code");
    notificationLaboratoryDataBuilder.setSectionComponentDisplay("display");

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getSystem()).isNull();
    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getCode()).isNull();
    assertThat(composition.getSectionFirstRep().getCode().getCodingFirstRep().getDisplay())
        .isNull();
  }

  @Test
  @DisplayName("should set laboratory report reference in section component")
  void shouldSetLaboratoryReportReferenceInSectionComponent() {
    notificationLaboratoryDataBuilder.setLaboratoryReport(laboratoryReport);

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getSectionFirstRep().getEntryFirstRep().getResource())
        .isEqualTo(laboratoryReport);
  }

  @Test
  @DisplayName("should set default values when setDefault is called")
  void shouldSetDefaultValuesWhenSetDefaultIsCalled() {
    notificationLaboratoryDataBuilder.setDefault();

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationId");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getSystem()).isNull();
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode()).isNull();
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getDisplay()).isNull();
  }

  @Test
  @DisplayName("should set meta profile URL")
  void shouldSetMetaProfileUrl() {
    notificationLaboratoryDataBuilder.setDefault();

    Composition composition = notificationLaboratoryDataBuilder.build();

    assertThat(composition.getMeta().getProfile())
        .extracting("value")
        .contains("https://demis.rki.de/fhir/StructureDefinition/NotificationLaboratory");
  }
}
