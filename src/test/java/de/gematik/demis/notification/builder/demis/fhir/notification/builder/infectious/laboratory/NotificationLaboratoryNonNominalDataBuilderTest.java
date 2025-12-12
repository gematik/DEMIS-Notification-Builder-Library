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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.RECEPTION_TIME_STAMP_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.util.Date;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;

class NotificationLaboratoryNonNominalDataBuilderTest {

  private Patient notifiedPerson = TestObjects.notifiedPerson();
  private PractitionerRole notifierRole = TestObjects.notifier();
  private DiagnosticReport diagnosticReport = TestObjects.laboratoryReport();
  private Composition composition;
  private PractitionerRole author;
  private Patient subject;

  private void setup() {
    notifiedPerson = TestObjects.notifiedPerson();
    notifierRole = TestObjects.notifier();
    diagnosticReport = TestObjects.laboratoryReport();
  }

  private Composition createComposition() {
    Composition composition = new Composition();
    composition.setMeta(new Meta().addProfile("https://example.com/profile"));
    composition.setIdentifier(new Identifier().setSystem("system").setValue("value"));
    composition.setStatus(Composition.CompositionStatus.FINAL);
    composition.setType(
        new CodeableConcept().addCoding(new Coding("typeSystem", "typeCode", "typeDisplay")));
    composition.addCategory(
        new CodeableConcept()
            .addCoding(new Coding("categorySystem", "categoryCode", "categoryDisplay")));
    composition.setDate(new Date());
    composition.setTitle("Title");
    composition
        .addRelatesTo()
        .setCode(Composition.DocumentRelationshipType.APPENDS)
        .setTarget(new Reference().setIdentifier(new Identifier().setValue("relatesToValue")));
    return composition;
  }

  @Test
  void shouldCopyMetaProfileUrl() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://example.com/profile");
  }

  @Test
  void shouldCopyIdentifier() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getIdentifier().getSystem()).isEqualTo("system");
    assertThat(result.getIdentifier().getValue()).isEqualTo("value");
  }

  @Test
  void shouldCopyStatus() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
  }

  @Test
  void shouldCopyType() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getType().getCodingFirstRep().getSystem()).isEqualTo("typeSystem");
    assertThat(result.getType().getCodingFirstRep().getCode()).isEqualTo("typeCode");
    assertThat(result.getType().getCodingFirstRep().getDisplay()).isEqualTo("typeDisplay");
  }

  @Test
  void shouldCopyDate() {
    setup();
    Composition originalComposition = createComposition();
    Date date = originalComposition.getDate();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getDate()).isEqualTo(date);
  }

  @Test
  void shouldCopyTitle() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getTitle()).isEqualTo("Title");
  }

  @Test
  void shouldCopyRelatesTo() {
    setup();
    Composition originalComposition = createComposition();

    final Composition result =
        NotificationLaboratoryNonNominalDataBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getRelatesToFirstRep().getCode())
        .isEqualTo(Composition.DocumentRelationshipType.APPENDS);
    assertThat(result.getRelatesToFirstRep().getTargetReference().getIdentifier().getValue())
        .isEqualTo("relatesToValue");
  }

  void setUp2() {
    composition = new Composition();
    composition.setMeta(new Meta().addProfile("foobarProfile"));

    author = new PractitionerRole();
    author.setId("author");

    subject = new Patient();
    subject.setId("subject");

    diagnosticReport = new DiagnosticReport();
    diagnosticReport.setId("diagnosticReport");
  }

  @Test
  void shouldCopyMilisecondsFromOriginialComposition() {
    setUp2();
    composition.setDateElement(new DateTimeType("2022-02-19T00:00:00.000+01:00"));

    NotificationLaboratoryNonNominalDataBuilder builder =
        new NotificationLaboratoryNonNominalDataBuilder();
    Composition deepCopyComposition =
        builder.deepCopy(composition, author, subject, diagnosticReport);

    assertThat(deepCopyComposition.getDateElement().getValueAsString())
        .isEqualTo("2022-02-19T00:00:00.000+01:00");
  }

  @Test
  void shouldCopyExtension() {
    setUp2();
    Type value = new DateTimeType("2022-02-19T00:00:00.000+01:00");
    composition.addExtension(new Extension(RECEPTION_TIME_STAMP_TYPE, value));

    NotificationLaboratoryNonNominalDataBuilder builder =
        new NotificationLaboratoryNonNominalDataBuilder();
    Composition deepCopyComposition =
        builder.deepCopy(composition, author, subject, diagnosticReport);

    assertThat(deepCopyComposition.getExtension())
        .extracting(Extension::getUrl, Extension::getValue)
        .contains(tuple(RECEPTION_TIME_STAMP_TYPE, value));
  }
}
