package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import ca.uhn.fhir.context.FhirVersionEnum;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class BundleDataBuilderTest {

  private static void verifyProfileUrl(Bundle bundle, String profileUrl) {
    List<String> metaProfileValues =
        bundle.getMeta().getProfile().stream().map(CanonicalType::getValue).toList();
    assertThat(metaProfileValues).contains(profileUrl);
  }

  @DisplayName("build method should create object and add global needed entries")
  @Test
  void shouldBuildBundle() {

    String profileUrl = "test-profile-url";
    Composition notificationLaboratory = new Composition();
    PractitionerRole practitionerRole = new PractitionerRole();
    Practitioner practitioner = new Practitioner();
    practitionerRole.setPractitioner(new Reference(practitioner));

    BundleDataBuilder b =
        new TestBundleDataBuilder()
            .setPractitionerRole(practitionerRole)
            .setResource(notificationLaboratory);
    b.setProfileUrl(profileUrl);
    Bundle bundle = b.build();

    verifyProfileUrl(bundle, profileUrl);

    assertThat(bundle.getEntry())
        .extracting("resource")
        .containsExactly(notificationLaboratory, practitionerRole, practitioner);
  }

  // set standardvalues
  @DisplayName("all standard data should be set")
  @Test
  void shouldSetStandardData() {

    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      Date date =
          Date.from(
              LocalDateTime.of(2020, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
      utilities.when(Utils::getCurrentDate).thenReturn(date);

      TestBundleDataBuilder builder = new TestBundleDataBuilder();
      String defaultProfileUrl = "default-profile-url";
      builder.setDefaultProfileUrl(defaultProfileUrl);
      builder.setDefaults();
      Bundle bundle = builder.build();

      verifyProfileUrl(bundle, defaultProfileUrl);

      assertThat(bundle.getId()).as("Bundle ID").isEqualTo("1");
      Identifier identifier = bundle.getIdentifier();
      assertThat(identifier.getValue()).as("Notification bundle ID").isEqualTo("1");
      assertThat(identifier.getSystem())
          .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
      assertThat(bundle.getType()).as("bundle document type").isEqualTo(Bundle.BundleType.DOCUMENT);
      assertThat(bundle.getTimestamp()).isEqualTo(date);
    }
  }

  @DisplayName("practitioner used when set in role")
  @Test
  void shouldAddPractitionerWhenSet() {
    PractitionerRole practitionerRole = new PractitionerRole();
    Practitioner practitioner = new Practitioner();
    practitionerRole.setPractitioner(new Reference(practitioner));

    TestBundleDataBuilder builder = new TestBundleDataBuilder();
    builder.setPractitionerRole(practitionerRole);
    Bundle bundle = builder.build();

    List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
    assertThat(entries).extracting("resource").containsExactly(practitionerRole, practitioner);
  }

  @DisplayName("organization used when set in role")
  @Test
  void shouldAddOrganizationWhenSet() {
    PractitionerRole practitionerRole = new PractitionerRole();
    Organization organization = new Organization();
    practitionerRole.setOrganization(new Reference(organization));

    TestBundleDataBuilder builder = new TestBundleDataBuilder();
    builder.setPractitionerRole(practitionerRole);
    Bundle bundle = builder.build();

    List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
    assertThat(entries).extracting("resource").containsExactly(practitionerRole, organization);
  }

  @Test
  void throwsAnExceptionForIllegalArgument() {
    final TestBundleDataBuilder testBundleDataBuilder = new TestBundleDataBuilder();
    final IllegalArgumentException illegalArgumentException =
        catchThrowableOfType(
            IllegalArgumentException.class,
            () -> {
              testBundleDataBuilder.addAdditionalEntry(
                  new IBaseResource() {
                    @Override
                    public IBaseMetaType getMeta() {
                      return null;
                    }

                    @Override
                    public IIdType getIdElement() {
                      return null;
                    }

                    @Override
                    public IBaseResource setId(final String s) {
                      return null;
                    }

                    @Override
                    public IBaseResource setId(final IIdType iIdType) {
                      return null;
                    }

                    @Override
                    public FhirVersionEnum getStructureFhirVersionEnum() {
                      return null;
                    }

                    @Override
                    public boolean isEmpty() {
                      return false;
                    }

                    @Override
                    public boolean hasFormatComment() {
                      return false;
                    }

                    @Override
                    public List<String> getFormatCommentsPre() {
                      return List.of();
                    }

                    @Override
                    public List<String> getFormatCommentsPost() {
                      return List.of();
                    }

                    @Override
                    public Object getUserData(final String s) {
                      return null;
                    }

                    @Override
                    public void setUserData(final String s, final Object o) {}
                  });
            });
    assertThat(illegalArgumentException).isNotNull();
  }

  @DisplayName("BundleBuilder setter tests")
  @Nested
  class SetterTests {

    @DisplayName("setDefaults keeps values")
    @Test
    void setDefaults_shouldKeepValues() throws ParseException {
      String id = "init-id";
      String notificationBundleId = "init-notification-bundle-id";
      Date timestamp = new SimpleDateFormat("yyyy-MM-dd").parse("2024-06-07");
      String profileUrl = "init-profile-url";
      Bundle.BundleType type = Bundle.BundleType.MESSAGE;
      Bundle bundle =
          new TestBundleDataBuilder()
              .setId(id)
              .setIdentifierAsNotificationBundleId(notificationBundleId)
              .setTimestamp(timestamp)
              .setProfileUrl(profileUrl)
              .setType(type)
              .setDefaults()
              .build();
      assertThat(bundle.getId()).isEqualTo(id);
      assertThat(bundle.getIdentifier().getValue()).isEqualTo(notificationBundleId);
      assertThat(bundle.getTimestamp()).isEqualTo(timestamp);
      assertThat(bundle.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
      assertThat(bundle.getType()).isSameAs(type);
    }

    @DisplayName("notifier role setter")
    @Test
    void shouldSetNotifierRole() {
      TestBundleDataBuilder builder = new TestBundleDataBuilder();

      PractitionerRole notifierRole = new PractitionerRole();
      builder.setPractitionerRole(notifierRole);

      List<Bundle.BundleEntryComponent> entries = builder.build().getEntry();
      assertThat(entries).extracting("resource").containsOnly(notifierRole);
    }

    @DisplayName("notification laboratory (composition) setter")
    @Test
    void shouldSetNotificationLaboratory() {
      TestBundleDataBuilder builder = new TestBundleDataBuilder();

      Composition notificationLaboratory = new Composition();
      builder.setResource(notificationLaboratory);

      List<Bundle.BundleEntryComponent> entries = builder.build().getEntry();
      assertThat(entries).extracting("resource").containsOnly(notificationLaboratory);
    }
  }
}
