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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
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
import org.junit.jupiter.api.Test;

class NonNominalBundleBuilderTest {

  @Test
  void shouldCreateBundleWithNonNominalUrl() {
    NonNominalBundleBuilder builder = new NonNominalBundleBuilder();
    builder.setDefaults();

    builder.setNotificationLaboratory(TestObjects.composition());
    builder.setLaboratoryReport(TestObjects.laboratoryReport());
    builder.setPathogenDetection(singletonList(TestObjects.pathogenDetection()));
    builder.setSpecimen(singletonList(TestObjects.specimen()));
    builder.setNotifierRole(TestObjects.notifier());
    builder.setSubmitterRole(TestObjects.submitter());
    builder.setNotifiedPerson(TestObjects.notifiedPerson());

    Bundle bundle = builder.build();

    assertThat(bundle.getMeta().getProfile()).hasSize(1);
    assertThat(bundle.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(
            "https://demis.rki.de/fhir/StructureDefinition/NotificationBundleLaboratoryNonNominal");
  }

  private Bundle createBundle() {
    try {
      final String source =
          Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal.json"));
      final IParser iParser = FhirContext.forR4().newJsonParser();
      iParser.setPrettyPrint(true);
      return (Bundle) iParser.parseResource(source);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldCopyMetaProfileUrl() {
    Bundle originalBundle = createBundle();

    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);

    assertThat(result.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL);
  }

  @Test
  void shouldCopyMetaTags() {
    Bundle originalBundle = createBundle();

    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);
    assertThat(result.getMeta().getTag().get(0).getSystem()).isEqualTo("system");
    assertThat(result.getMeta().getTag().get(0).getCode()).isEqualTo("code");
    assertThat(result.getMeta().getTag().get(0).getDisplay()).isEqualTo("display");
  }

  @Test
  void shouldCopyIdentifier() {
    Bundle originalBundle = createBundle();

    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);
    assertThat(result.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
    assertThat(result.getIdentifier().getValue()).isEqualTo("555-42-23-6");
  }

  @Test
  void shouldCopyType() {
    Bundle originalBundle = createBundle();
    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);
    assertThat(result.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
  }

  @Test
  void shouldCopyLastUpdated() {
    Bundle originalBundle = createBundle();
    Date lastUpdated = originalBundle.getMeta().getLastUpdated();

    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);
    assertThat(result.getMeta().getLastUpdated()).isEqualTo(lastUpdated);
  }

  @Test
  void shouldCopyTimestamp() {
    Bundle originalBundle = createBundle();
    Date timestamp = originalBundle.getTimestamp();

    final Bundle result = NonNominalBundleBuilder.deepCopy(originalBundle);
    assertThat(result.getTimestamp()).isEqualTo(timestamp);
  }

  @Test
  void shouldCopyEntries() {
    final Bundle original = getBundle();

    final Bundle result = NonNominalBundleBuilder.deepCopy(original);

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
    final Observation observation = TestObjects.pathogenDetection(specimen);
    final Patient notifiedPerson = TestObjects.notifiedPerson();
    notifiedPerson.setBirthDate(null);

    final Bundle original = getBundle();

    assertThatNoException()
        .isThrownBy(
            () -> {
              NonNominalBundleBuilder.deepCopy(original);
            });
  }

  @Test
  void readingFromJsonShouldOnlySwapNotifiedPerson() throws IOException {
    // GIVEN a bundle with NonNominal profiles and a notified person

    // THEN
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = NonNominalBundleBuilder.deepCopy(original);

    final String expected =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  @Test
  void readingFromJsonShouldNotModifyAnything() throws IOException {
    // GIVEN a bundle with NonNominal profiles and a notified person

    // THEN
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal-notbyname.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = NonNominalBundleBuilder.deepCopy(original);

    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-notbyname-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  private static Bundle getBundle() {
    final Specimen specimen = TestObjects.specimen();
    final Observation observation = TestObjects.pathogenDetection(specimen);
    final DiagnosticReport diagnosticReport = TestObjects.laboratoryReport(observation);
    final Patient patient = TestObjects.notifiedPerson();
    final PractitionerRole submitter = TestObjects.submitter();
    return new NonNominalBundleBuilder()
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
