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
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LaboratoryExcerptBuilderTest {

  @Nested
  @DisplayName("§7.1 not by name creation tests")
  class BundleToExcerptNotifiedPersonNotByNameTests {

    private Bundle create71Bundle() {
      try {
        final String source =
            Files.readString(Path.of("src/test/resources/laboratory/71-cvdp-erstmeldung.json"));
        final IParser iParser = FhirContext.forR4().newJsonParser();
        iParser.setPrettyPrint(true);
        return (Bundle) iParser.parseResource(source);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Test
    void shouldCopyMetaProfileUrl() {
      Bundle originalBundle = create71Bundle();

      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
              originalBundle);

      assertThat(result.getMeta().getProfile().getFirst().getValue())
          .isEqualTo(DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY);
    }

    @Test
    void shouldCopyMetaTagsAndAddRelatedNotificationBundleTag() {
      Bundle originalBundle = create71Bundle();

      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
              originalBundle);
      assertThat(result.getMeta().getTag().getFirst().getSystem()).isEqualTo("system");
      assertThat(result.getMeta().getTag().getFirst().getCode()).isEqualTo("code");
      assertThat(result.getMeta().getTag().getFirst().getDisplay()).isEqualTo("display");

      assertThat(result.getMeta().getTag().get(1).getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/RelatedNotificationBundle");
      assertThat(result.getMeta().getTag().get(1).getCode())
          .isEqualTo("925ccab9-b22c-3a95-b209-2cd46cabfa78");
      assertThat(result.getMeta().getTag().get(1).getDisplay())
          .isEqualTo("Relates to message with identifier: 925ccab9-b22c-3a95-b209-2cd46cabfa78");
    }

    @Test
    void shouldCreateNewIdentifierAndUseIdentifierAsTag() {
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils.when(Utils::generateUuidString).thenReturn("555-42-23-6");
        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();
        Bundle originalBundle = create71Bundle();

        final Bundle result =
            LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
                originalBundle);
        assertThat(result.getIdentifier().getSystem())
            .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
        assertThat(result.getIdentifier().getValue()).isEqualTo("555-42-23-6");

        assertThat(result.getMeta().getTag().get(1).getCode())
            .isEqualTo("925ccab9-b22c-3a95-b209-2cd46cabfa78");
      }
    }

    @Test
    void shouldCopyType() {
      Bundle originalBundle = create71Bundle();
      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
              originalBundle);
      assertThat(result.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
    }

    @Test
    void shouldCopyLastUpdated() {
      Bundle originalBundle = create71Bundle();
      Date lastUpdated = originalBundle.getMeta().getLastUpdated();

      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
              originalBundle);
      assertThat(result.getMeta().getLastUpdated()).isEqualTo(lastUpdated);
    }

    @Test
    void shouldCopyTimestamp() {
      Bundle originalBundle = create71Bundle();
      Date timestamp = originalBundle.getTimestamp();

      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
              originalBundle);
      assertThat(result.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldCopyEntries() {
      final Bundle original = getBundle();

      final Bundle result =
          LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(original);

      assertThat(result.getEntry()).hasSize(original.getEntry().size());
      // Equality is implemented on referencial equality. Ensure that no entry is present in either
      // list,
      // which means: we have copied the entries by creating a new object.
      assertThat(result.getEntry()).doesNotContainAnyElementsOf(original.getEntry());
      assertThat(original.getEntry()).doesNotContainAnyElementsOf(result.getEntry());
    }

    @Test
    void thatCopyingWithMissingBirthdateWorks() {
      final Specimen specimen = TestObjects.specimen();
      TestObjects.pathogenDetection(specimen);
      final Patient notifiedPerson = TestObjects.notifiedPerson();
      notifiedPerson.setBirthDate(null);

      final Bundle original = getBundle();

      assertThatNoException()
          .isThrownBy(
              () ->
                  LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(
                      original));
    }

    @Test
    void shouldCreateNewBundleFollowingSpecifications() throws IOException {
      int[] idHelper = {50};
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils
            .when(Utils::generateUuidString)
            .thenAnswer(invocation -> Integer.toString(idHelper[0]++));

        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();

        String json =
            Files.readString(
                Path.of(
                    "src/test/resources/laboratory/LaboratoryNotificationTestcaseForNotByNameExcerpt.json"));
        IParser iParser = FhirContext.forR4().newJsonParser().setPrettyPrint(true);
        Bundle bundle = iParser.parseResource(Bundle.class, json);

        Bundle excerpt =
            LaboratoryExcerptBuilder.createExcerptNotifiedPersonNotByNameFromNominalBundle(bundle);
        String excerptJson = iParser.encodeResourceToString(excerpt);

        String expected =
            Files.readString(
                Path.of(
                    "src/test/resources/laboratory/LaboratoryNotificationTestcaseForNotByNameExcerptExpected.json"));

        assertThat(excerptJson).isEqualTo(expected);
      }
    }

    private static Bundle getBundle() {
      final Specimen specimen = TestObjects.specimen();
      final Observation observation = TestObjects.pathogenDetection(specimen);
      final DiagnosticReport diagnosticReport = TestObjects.laboratoryReport(observation);
      final Patient patient = TestObjects.notifiedPerson();
      final PractitionerRole submitter = TestObjects.submitter();
      return new NotificationBundleLaboratoryNonNominalDataBuilder()
          .setDefaults()
          .setNotifierRole(TestObjects.notifier())
          .setSubmitterRole(submitter)
          .setNotifiedPerson(patient)
          .setPathogenDetection(List.of(observation))
          .setSpecimen(List.of(specimen))
          .setLaboratoryReport(diagnosticReport)
          .setNotificationLaboratory(TestObjects.composition(diagnosticReport, patient, submitter))
          .build();
    }
  }

  @Nested
  class NonNominalBundleToExcerptNotifiedPersonAnonymousTests {
    @Test
    void shouldCreateNewBundleFollowingSpecifications() throws IOException {
      int[] idHelper = {50};
      try (MockedStatic<Utils> utils = Mockito.mockStatic(Utils.class)) {
        utils
            .when(Utils::generateUuidString)
            .thenAnswer(invocation -> Integer.toString(idHelper[0]++));

        utils.when(() -> Utils.getShortReferenceOrUrnUuid(any())).thenCallRealMethod();

        String json = Files.readString(Path.of("src/test/resources/laboratory/7_3/basic.json"));
        IParser iParser = FhirContext.forR4().newJsonParser().setPrettyPrint(true);
        Bundle bundle = iParser.parseResource(Bundle.class, json);

        Bundle excerpt =
            LaboratoryExcerptBuilder.createExcerptNotifiedPersonAnonymousFromNonNominalBundle(
                bundle);
        String excerptJson = iParser.encodeResourceToString(excerpt);

        String expected =
            Files.readString(Path.of("src/test/resources/laboratory/7_3/basic_expected.json"));

        assertThat(excerptJson).isEqualTo(expected);
      }
    }
  }
}
