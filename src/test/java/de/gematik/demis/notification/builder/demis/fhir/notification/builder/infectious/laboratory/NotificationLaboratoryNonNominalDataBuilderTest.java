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

import de.gematik.demis.notification.builder.demis.fhir.testUtils.TestObjects;
import java.util.Date;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class NotificationLaboratoryNonNominalDataBuilderTest {

  private final Patient notifiedPerson = TestObjects.notifiedPerson();
  private final PractitionerRole notifierRole = TestObjects.notifier();
  private final DiagnosticReport diagnosticReport = TestObjects.laboratoryReport();

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
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getMeta().getProfile().get(0).getValue())
        .isEqualTo("https://example.com/profile");
  }

  @Test
  void shouldCopyIdentifier() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getIdentifier().getSystem()).isEqualTo("system");
    assertThat(result.getIdentifier().getValue()).isEqualTo("value");
  }

  @Test
  void shouldCopyStatus() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
  }

  @Test
  void shouldCopyType() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getType().getCodingFirstRep().getSystem()).isEqualTo("typeSystem");
    assertThat(result.getType().getCodingFirstRep().getCode()).isEqualTo("typeCode");
    assertThat(result.getType().getCodingFirstRep().getDisplay()).isEqualTo("typeDisplay");
  }

  @Test
  void shouldCopyCategory() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getCategoryFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("categorySystem");
    assertThat(result.getCategoryFirstRep().getCodingFirstRep().getCode())
        .isEqualTo("categoryCode");
    assertThat(result.getCategoryFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("categoryDisplay");
  }

  @Test
  void shouldCopyDate() {
    Composition originalComposition = createComposition();
    Date date = originalComposition.getDate();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getDate()).isEqualTo(date);
  }

  @Test
  void shouldCopyTitle() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getTitle()).isEqualTo("Title");
  }

  @Test
  void shouldCopyRelatesTo() {
    Composition originalComposition = createComposition();

    final Composition result =
        NonNominalCompositionBuilder.deepCopy(
            originalComposition, notifierRole, notifiedPerson, diagnosticReport);
    assertThat(result.getRelatesToFirstRep().getCode())
        .isEqualTo(Composition.DocumentRelationshipType.APPENDS);
    assertThat(result.getRelatesToFirstRep().getTargetReference().getIdentifier().getValue())
        .isEqualTo("relatesToValue");
  }
}
