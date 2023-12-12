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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

import static org.assertj.core.api.Assertions.assertThat;

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
    notifierRole = new PractitionerRole();
    laboratoryReport = new DiagnosticReport();
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
    assertThat(composition.getCategory()).hasSize(1);
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("http://loinc.org");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("11502-2");
    assertThat(composition.getCategoryFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("Laboratory report");

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

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

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

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

    assertThat(composition.getType().getCoding()).hasSize(1);
    assertThat(composition.getType().getCoding().get(0).getDisplay()).isEqualTo("typeDisplay");
    assertThat(composition.getType().getCoding().get(0).getCode()).isEqualTo("typeCode");
    assertThat(composition.getType().getCoding().get(0).getSystem()).isEqualTo("typeSystem");
  }

  @Test
  void shoudlSetCodeAndCategoryAsGiven() {

    notificationLaboratoryDataBuilder.setCodeAndCategoryCode("codeAndCategoryCode");
    notificationLaboratoryDataBuilder.setCodeAndCategorySystem("codeAndCategorySystem");
    notificationLaboratoryDataBuilder.setCodeAndCategoryDisplay("codeAndCategoryDisplay");

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

    assertThat(composition.getCategory()).hasSize(1);
    assertThat(composition.getCategory().get(0).getCoding()).hasSize(1);
    assertThat(composition.getCategory().get(0).getCoding().get(0).getCode())
        .isEqualTo("codeAndCategoryCode");
    assertThat(composition.getCategory().get(0).getCoding().get(0).getSystem())
        .isEqualTo("codeAndCategorySystem");
    assertThat(composition.getCategory().get(0).getCoding().get(0).getDisplay())
        .isEqualTo("codeAndCategoryDisplay");
  }

  @Test
  void shoudlSetIdentifierSystemAndValue() {

    notificationLaboratoryDataBuilder.setIdentifierSystem("identifierSystem");
    notificationLaboratoryDataBuilder.setIdentifierValue("identifierValue");

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

    assertThat(composition.getIdentifier().getSystem()).isEqualTo("identifierSystem");
    assertThat(composition.getIdentifier().getValue()).isEqualTo("identifierValue");
  }

  @Test
  void shoudlSetCompositionStatus() {

    notificationLaboratoryDataBuilder.setCompositionStatus(Composition.CompositionStatus.AMENDED);

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

    assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.AMENDED);
  }

  @Test
  void shoudlSetCompositionId() {

    notificationLaboratoryDataBuilder.setNotificationId("someId");

    Composition composition =
        notificationLaboratoryDataBuilder.buildExampleComposition(
            notifiedPerson, notifierRole, laboratoryReport);

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
}
