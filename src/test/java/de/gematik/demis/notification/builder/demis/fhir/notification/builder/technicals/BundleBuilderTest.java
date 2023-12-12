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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleBuilder.addEntry;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class BundleBuilderTest {

  // addEntryTests
  @DisplayName("list of resources should be add to given bundle")
  @Test
  void shouldAddEntriesToBundle() {

    Bundle bundle = new Bundle();

    Resource composition = new Composition().setId("composition");
    Resource patient = new Patient().setId("patient");
    Resource practitioner = new Practitioner().setId("practitioner");

    List<Resource> listToAdd = asList(composition, patient, practitioner);

    addEntry(bundle, listToAdd);

    assertThat(bundle.getEntry()).extracting("resource").isEqualTo(listToAdd);
  }

  @DisplayName("resource should be add to given bundle")
  @Test
  void shouldAddEntryToBundle() {

    Bundle bundle = new Bundle();

    Resource composition = new Composition().setId("composition");

    addEntry(bundle, composition);

    assertThat(bundle.getEntry()).extracting("resource").containsExactly(composition);
  }

  // builder test
  @DisplayName("build method should create object and add global needed entries")
  @Test
  void shouldBuildBundle() {

    Composition notificationLaboratory = new Composition();
    PractitionerRole practitionerRole = new PractitionerRole();
    Practitioner practitioner = new Practitioner();
    practitionerRole.setPractitioner(new Reference(practitioner));

    BundleBuilder builder =
        new BundleBuilder()
            .setNotificationLaboratory(notificationLaboratory)
            .setNotifierRole(practitionerRole);

    Bundle bundle = builder.build();

    assertThat(bundle.getEntry())
        .extracting("resource")
        .containsExactly(notificationLaboratory, practitionerRole, practitioner);
  }

  // set standardvalues
  @DisplayName("all standard data should be set")
  @Test
  void shouldSetStandarData() {

    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      Date date =
          Date.from(
              LocalDateTime.of(2020, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
      utilities.when(Utils::getCurrentDate).thenReturn(date);

      BundleBuilder builder = new BundleBuilder();
      builder.setDefaultData();
      Bundle bundle = builder.build();

      assertThat(bundle.getId()).isEqualTo("1");
      assertThat(bundle.getIdentifier().getValue()).isEqualTo("1");
      assertThat(bundle.getIdentifier().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
      assertThat(bundle.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
      assertThat(bundle.getTimestamp()).isEqualTo(date);
    }
  }

  @DisplayName("BundleBuilder setter tests")
  @Nested
  class SetterTests {
    @DisplayName("notifier role setter")
    @Test
    void shouldSetNotifierRole() {
      BundleBuilder builder = new BundleBuilder();

      PractitionerRole notifierRole = new PractitionerRole();
      builder.setNotifierRole(notifierRole);

      assertThat(builder.notifierRole).isEqualTo(notifierRole);
      assertThat(builder.build().getEntry()).extracting("resource").containsOnly(notifierRole);
    }

    @DisplayName("notification laboratory (composition) setter")
    @Test
    void shouldSetNotificationLaboratory() {
      BundleBuilder builder = new BundleBuilder();

      Composition notificationLaboratory = new Composition();
      builder.setNotificationLaboratory(notificationLaboratory);

      assertThat(builder.notificationLaboratory).isEqualTo(notificationLaboratory);
      assertThat(builder.build().getEntry())
          .extracting("resource")
          .containsOnly(notificationLaboratory);
    }

    @DisplayName("notifier role setter")
    @Test
    void shouldSettNotifierRole() {
      BundleBuilder builder = new BundleBuilder();

      PractitionerRole notifierRole = new PractitionerRole();
      builder.setNotifierRole(notifierRole);

      assertThat(builder.notifierRole).isEqualTo(notifierRole);
      assertThat(builder.build().getEntry()).extracting("resource").containsOnly(notifierRole);
    }
  }

  @DisplayName("practitioner used when set in role")
  @Test
  void shouldAddPractitionerWhenSet() {
    PractitionerRole practitionerRole = new PractitionerRole();
    Practitioner practitioner = new Practitioner();
    practitionerRole.setPractitioner(new Reference(practitioner));

    Bundle bundle = new Bundle();
    BundleBuilder builder = new BundleBuilder();
    builder.addPractitionerRoleWithSubResource(bundle, practitionerRole);

    assertThat(bundle.getEntry()).extracting("resource").containsOnly(practitioner);
  }

  @DisplayName("organization used when set in role")
  @Test
  void shouldAddOrganizationWhenSet() {
    PractitionerRole practitionerRole = new PractitionerRole();
    Organization organization = new Organization();
    practitionerRole.setOrganization(new Reference(organization));

    Bundle bundle = new Bundle();
    BundleBuilder builder = new BundleBuilder();
    builder.addPractitionerRoleWithSubResource(bundle, practitionerRole);

    assertThat(bundle.getEntry()).extracting("resource").containsOnly(organization);
  }
}
