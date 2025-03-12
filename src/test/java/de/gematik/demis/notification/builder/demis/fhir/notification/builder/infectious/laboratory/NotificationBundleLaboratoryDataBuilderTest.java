package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

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

    // when
    NotificationBundleLaboratoryDataBuilder builder = new NotificationBundleLaboratoryDataBuilder();
    builder
        .setDefaults()
        .setId("specificId")
        .setIdentifierAsNotificationBundleId("specificIdentifier");

    Composition composition = new NotificationLaboratoryDataBuilder().build();

    builder
        .setNotifiedPerson(notifiedPerson)
        .setSubmitterRole(submitterRole)
        .setSpecimen(List.of(specimen))
        .setPathogenDetection(pathogenDetectionList)
        .setLaboratoryReport(laboratoryReport)
        .setNotifierRole(notifierRole)
        .setNotificationLaboratory(composition);
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
            composition);
    assertThat(bundle.getId()).isEqualTo("specificId");
    assertThat(bundle.getIdentifier().getValue()).isEqualTo("specificIdentifier");
    assertThat(bundle.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
    assertThat(bundle.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
    assertThat(bundle.getTimestamp()).isInstanceOf(Date.class);
  }

  @Test
  void thatAdditionalEntriesAreAppendedToTheEnd() {
    // GIVEN a bundle
    final Patient notifiedPerson = new Patient();
    final Specimen specimen = new Specimen();
    final Observation onePathogenDetection = new Observation().setSpecimen(new Reference(specimen));
    final DiagnosticReport laboratoryReport =
        new DiagnosticReport().addResult(new Reference(onePathogenDetection));
    final PractitionerRole notifierRole = new PractitionerRole();
    final Organization notifierFacility = new Organization();
    notifierRole.setOrganization(new Reference(notifierFacility));

    final Organization additionalEntry = new Organization();

    // WHEN I use defaults
    final NotificationBundleLaboratoryDataBuilder builder =
        new NotificationBundleLaboratoryDataBuilder();
    builder
        .setDefaults()
        .setId("specificId")
        .setIdentifierAsNotificationBundleId("specificIdentifier");

    final Composition composition = new NotificationLaboratoryDataBuilder().build();

    // AND I begin by adding additional entries
    builder.addAdditionalEntry(additionalEntry);
    // AND then set the remaining information
    builder
        .setNotificationLaboratory(composition)
        .setSpecimen(List.of(specimen))
        .setLaboratoryReport(laboratoryReport)
        .setNotifierRole(notifierRole)
        .setNotifiedPerson(notifiedPerson);
    Bundle bundle = builder.build();

    // THEN a specific order is established
    assertThat(bundle.getEntry())
        .extracting("resource")
        .containsExactly(
            composition,
            notifiedPerson,
            notifierRole,
            notifierFacility,
            specimen,
            laboratoryReport,
            additionalEntry);
  }
}
