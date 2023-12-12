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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;

/**
 * this class demonstrates how the builders for all the parts of a notification can be used to
 * create a valid (or invalid) notification
 */
class NotificationBundleLaboratoryDataBuilderTest {

  @Test
  void createCompleteBundleWithStandardData() {
    // given
    Patient notifiedPerson = new Patient();
    PractitionerRole submitterRole = new PractitionerRole();
    Practitioner submitter = new Practitioner();
    submitterRole.setPractitioner(new Reference(submitter));
    Specimen specimen = new Specimen();
    Observation onePathogenDetection = new Observation().setSpecimen(new Reference(specimen));
    List<Observation> pathogenDetectionList = Collections.singletonList(onePathogenDetection);
    DiagnosticReport laboratoryReport =
        new DiagnosticReport().addResult(new Reference(onePathogenDetection));
    PractitionerRole notifierRole = new PractitionerRole();
    Organization notifierFacility = new Organization();
    notifierRole.setOrganization(new Reference(notifierFacility));
    Composition notificationLaboratory = new Composition();

    // when
    NotificationBundleLaboratoryDataBuilder builder = new NotificationBundleLaboratoryDataBuilder();
    builder
        .setDefaults()
        .setNotifiedPerson(notifiedPerson)
        .setSubmitterRole(submitterRole)
        .setSpecimen(specimen)
        .setPathogenDetection(pathogenDetectionList)
        .setLaboratoryReport(laboratoryReport)
        // now from superclass
        .setNotifierRole(notifierRole)
        .setNotificationLaboratory(notificationLaboratory)
        .setBundleId("specificId")
        .setBundleIdentifier("specificIdentifier");
    Bundle bundle = builder.build();

    assertThat(bundle.getEntry())
        .extracting("resource")
        .containsExactlyInAnyOrder(
            notifiedPerson,
            submitterRole,
            submitter,
            notifierRole,
            notifierFacility,
            onePathogenDetection,
            specimen,
            laboratoryReport,
            notificationLaboratory);
    assertThat(bundle.getId()).isEqualTo("specificId");
    assertThat(bundle.getIdentifier().getValue()).isEqualTo("specificIdentifier");
    assertThat(bundle.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
    assertThat(bundle.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
    assertThat(bundle.getTimestamp()).isInstanceOf(Date.class);
  }

  /**
   * this test creates the same notification that the createStandardLaboratoryNotificationBundle
   * method from LaboratoryNotificationCreationService would create.
   */
  @Test
  void exampleStandardLaboratoryNotificationfromInputWithStandardBuilder() {

    NotificationBundleLaboratoryDataBuilder notificationBundleLaboratoryDataBuilder =
        new NotificationBundleLaboratoryDataBuilder();

    Patient notifiedPerson = new NotifiedPersonDataBuilder().buildExampleNotifiedPerson();
    notificationBundleLaboratoryDataBuilder.setNotifiedPerson(notifiedPerson);
    PractitionerRole submitterRole = new SubmitterDataBuilder().buildExampleSubmitterFacilityData();
    notificationBundleLaboratoryDataBuilder.setSubmitterRole(submitterRole);
    Specimen specimen =
        new SpecimenDataBuilder().buildExampleSpecimen(notifiedPerson, submitterRole);
    notificationBundleLaboratoryDataBuilder.setSpecimen(specimen);
    Observation pathogenDetection =
        new PathogenDetectionDataBuilder().buildPathogenDetection(notifiedPerson, specimen);
    notificationBundleLaboratoryDataBuilder.setPathogenDetection(
        Collections.singletonList(pathogenDetection));
    DiagnosticReport laboratoryReport =
        new LaboratoryReportDataBuilder()
            .buildExampleCVDPLaboratoryReport(notifiedPerson, pathogenDetection);
    notificationBundleLaboratoryDataBuilder.setLaboratoryReport(laboratoryReport);
    PractitionerRole notifierRole = new NotifierDataBuilder().buildLaboratoryExampleNotifierData();
    notificationBundleLaboratoryDataBuilder.setNotifierRole(notifierRole);

    Composition notificationLaboratory =
        new NotificationLaboratoryDataBuilder()
            .buildExampleComposition(notifiedPerson, notifierRole, laboratoryReport);
    notificationBundleLaboratoryDataBuilder.setNotificationLaboratory(notificationLaboratory);

    Bundle bundle = notificationBundleLaboratoryDataBuilder.buildExampleLaboratoryBundle();

    assertThat(bundle.getEntry()).hasSize(9);
    Composition actualNotificationLaboratory = (Composition) bundle.getEntry().get(0).getResource();
    assertThat(actualNotificationLaboratory).isEqualTo(notificationLaboratory);
    assertThat(actualNotificationLaboratory.getAuthor().get(0).getResource())
        .isEqualTo(notifierRole);
    assertThat(actualNotificationLaboratory.getSubject().getResource()).isEqualTo(notifiedPerson);
    assertThat(actualNotificationLaboratory.getSection().get(0).getEntry().get(0).getResource())
        .isEqualTo(laboratoryReport);
  }

  /**
   * this method shows how to set the name and the gender to other values than the standard value.
   * all other values stay equal to the standard values.
   */
  @Test
  void exampleWithNotStandardInputForNotifiedPerson() {

    NotificationBundleLaboratoryDataBuilder notificationBundleLaboratoryDataBuilder =
        new NotificationBundleLaboratoryDataBuilder();

    NotifiedPersonDataBuilder notifiedPersonDataBuilder = new NotifiedPersonDataBuilder();
    HumanName personName =
        new HumanNameDataBuilder()
            .setFamilyName("Lauterbach")
            .addGivenName("Nele")
            .buildHumanName();
    notifiedPersonDataBuilder.setHumanName(personName);
    notifiedPersonDataBuilder.setGender(Enumerations.AdministrativeGender.FEMALE);
    Patient notifiedPerson = notifiedPersonDataBuilder.buildExampleNotifiedPerson();
    notificationBundleLaboratoryDataBuilder.setNotifiedPerson(notifiedPerson);

    Bundle bundle = notificationBundleLaboratoryDataBuilder.buildExampleLaboratoryBundle();

    assertThat(bundle.getEntry()).hasSize(9);
    Composition notificationLaboratory = (Composition) bundle.getEntry().get(0).getResource();
    Patient resource = (Patient) notificationLaboratory.getSubject().getResource();
    assertThat(resource.getName().get(0)).isEqualTo(personName);
    assertThat(resource.getGender()).isEqualTo(Enumerations.AdministrativeGender.FEMALE);
  }
}
