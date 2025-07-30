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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
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
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.Test;

class NotificationBundleLaboratoryNonNominalDataBuilderTest {

  @Test
  void shouldCreateBundleWithNonNominalUrl() {
    NotificationBundleLaboratoryNonNominalDataBuilder builder =
        new NotificationBundleLaboratoryNonNominalDataBuilder();
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

    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);

    assertThat(result.getMeta().getProfile().getFirst().getValue())
        .isEqualTo(DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL);
  }

  @Test
  void shouldCopyMetaTags() {
    Bundle originalBundle = createBundle();

    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);
    assertThat(result.getMeta().getTag().get(0).getSystem()).isEqualTo("system");
    assertThat(result.getMeta().getTag().get(0).getCode()).isEqualTo("code");
    assertThat(result.getMeta().getTag().get(0).getDisplay()).isEqualTo("display");
  }

  @Test
  void shouldCopyIdentifier() {
    Bundle originalBundle = createBundle();

    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);
    assertThat(result.getIdentifier().getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");
    assertThat(result.getIdentifier().getValue()).isEqualTo("555-42-23-6");
  }

  @Test
  void shouldCopyType() {
    Bundle originalBundle = createBundle();
    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);
    assertThat(result.getType()).isEqualTo(Bundle.BundleType.DOCUMENT);
  }

  @Test
  void shouldCopyLastUpdated() {
    Bundle originalBundle = createBundle();
    Date lastUpdated = originalBundle.getMeta().getLastUpdated();

    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);
    assertThat(result.getMeta().getLastUpdated()).isEqualTo(lastUpdated);
  }

  @Test
  void shouldCopyTimestamp() {
    Bundle originalBundle = createBundle();
    Date timestamp = originalBundle.getTimestamp();

    final Bundle result =
        NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);
    assertThat(result.getTimestamp()).isEqualTo(timestamp);
  }

  @Test
  void shouldCopyEntries() {
    final Bundle original = getBundle();

    final Bundle result = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

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
              NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);
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
    final Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

    final String expected =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  @Test
  void thatProcessingWithUrnUuidWorks() throws IOException {
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal-urn_uuid.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-urn_uuid-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  @Test
  void thatProcessingWithUrnUuidAndRegularIdsMixedWorks() throws IOException {
    // GIVEN a bundle that makes use of urn:uuid: and regular ids (e.g. Composition/...)
    final String source =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-urn_uuid-mixed.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-urn_uuid-mixed-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  @Test
  void thatProcessingResourcesWithoutIdWorks() throws IOException {
    // A test to verify that parsing a bundle without ids, will add the fullurl as id to the
    // resource so we can
    // process it
    //
    // GIVEN a bundle that has no id fields on the resources
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-nonnominal-no-ids.json"));
    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Bundle original = (Bundle) iParser.parseResource(source);
    final Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

    // NOTE: it's okay if this test breaks should you change either the input or the output. Just
    // make sure you
    // adjust accordingly.
    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-urn_uuid-expected.json"));

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
    final Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(original);

    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-nonnominal-notbyname-expected.json"));

    final String result = iParser.encodeResourceToString(copy);
    assertThat(result).isEqualToIgnoringNewLines(expected);
  }

  @Test
  void shouldSetPractitionerReferenceWithoutUrnUuidWhenParametersNotificationisReceived()
      throws IOException {
    final String source =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-laboratory-parameters-notification.json"));

    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Parameters original = (Parameters) iParser.parseResource(source);
    Bundle originalBundle = (Bundle) original.getParameter().getFirst().getResource();

    Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);

    assertThat(copy.getEntryFirstRep().getFullUrl())
        .isEqualTo("https://demis.rki.de/fhir/Composition/6ed88a19-70e9-3292-8272-b45740eb82b6");
    Resource resource = copy.getEntry().get(2).getResource();
    assertThat(resource).isInstanceOf(PractitionerRole.class);
    PractitionerRole practitionerRole = (PractitionerRole) resource;
    assertThat(practitionerRole.getOrganization().getReference())
        .hasToString("Organization/f66f699c-4ca5-3c54-9405-d2ef4305791e");

    Resource resource2 = copy.getEntry().get(4).getResource();
    assertThat(resource2).isInstanceOf(PractitionerRole.class);
    PractitionerRole practitionerRole2 = (PractitionerRole) resource2;
    assertThat(practitionerRole2.getOrganization().getReference())
        .hasToString("Organization/e9dddc3f-2110-38ad-9f1a-479f2a656074");

    final String expected =
        Files.readString(
            Path.of(
                "src/test/resources/laboratory/73-laboratory-parameters-notification-copy.json"));

    IParser iParser1 = FhirContext.forR4Cached().newJsonParser();
    iParser1.setPrettyPrint(true);
    String actual = iParser1.encodeResourceToString(copy);
    assertThat(actual).isEqualToIgnoringWhitespace(expected);
  }

  @Test
  void shouldCopyNotifiedPersonFacility() throws IOException {
    final String source =
        Files.readString(Path.of("src/test/resources/laboratory/73-other-facility-case.json"));

    final IParser iParser = FhirContext.forR4().newJsonParser();
    iParser.setPrettyPrint(true);

    final Parameters original = (Parameters) iParser.parseResource(source);
    Bundle originalBundle = (Bundle) original.getParameter().getFirst().getResource();

    Bundle copy = NotificationBundleLaboratoryNonNominalDataBuilder.deepCopy(originalBundle);

    final String expected =
        Files.readString(
            Path.of("src/test/resources/laboratory/73-other-facility-case-expected.json"));

    IParser iParser1 = FhirContext.forR4Cached().newJsonParser();
    iParser1.setPrettyPrint(true);
    String actual = iParser1.encodeResourceToString(copy);
    assertThat(actual).isEqualToIgnoringWhitespace(expected);
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
