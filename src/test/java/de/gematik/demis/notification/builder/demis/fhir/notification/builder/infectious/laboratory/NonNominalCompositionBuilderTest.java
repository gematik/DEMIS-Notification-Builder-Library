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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.RECEPTION_TIME_STAMP_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NonNominalCompositionBuilderTest {

  private Composition composition;
  private PractitionerRole author;
  private Patient subject;
  private DiagnosticReport diagnosticReport;

  @BeforeEach
  void setUp() {
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
    composition.setDateElement(new DateTimeType("2022-02-19T00:00:00.000+01:00"));

    NonNominalCompositionBuilder builder = new NonNominalCompositionBuilder();
    Composition deepCopyComposition =
        builder.deepCopy(composition, author, subject, diagnosticReport);

    assertThat(deepCopyComposition.getDateElement().getValueAsString())
        .isEqualTo("2022-02-19T00:00:00.000+01:00");
  }

  @Test
  void shouldCopyExtension() {
    Type value = new DateTimeType("2022-02-19T00:00:00.000+01:00");
    composition.addExtension(new Extension(RECEPTION_TIME_STAMP_TYPE, value));

    NonNominalCompositionBuilder builder = new NonNominalCompositionBuilder();
    Composition deepCopyComposition =
        builder.deepCopy(composition, author, subject, diagnosticReport);

    assertThat(deepCopyComposition.getExtension())
        .extracting(Extension::getUrl, Extension::getValue)
        .contains(tuple(RECEPTION_TIME_STAMP_TYPE, value));
  }
}
