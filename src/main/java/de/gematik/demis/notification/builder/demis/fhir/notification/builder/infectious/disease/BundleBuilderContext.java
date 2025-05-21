package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Compositions;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Conditions;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Patients;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerRoles;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Provenances;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.QuestionnaireResponses;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

/** Provide a simplified layer to access resources in a Bundle. This is the disease flavor. */
record BundleBuilderContext(
    @Nonnull Composition composition,
    @Nonnull Patient subject,
    @Nonnull PractitionerRole notifier,
    @Nonnull Condition condition,
    @Nonnull QuestionnaireResponse specificQuestionnaire,
    @Nonnull Optional<Provenance> provenance) {

  /**
   * @throws IllegalArgumentException if one of the required resources can't be extracted from the
   *     Bundle
   */
  @Nonnull
  public static BundleBuilderContext from(@Nonnull final Bundle bundle) {
    final Composition composition =
        Compositions.from(bundle)
            .orElseThrow(illegalArgumentException("Can't find Composition in Bundle"));
    final Patient patient =
        Patients.subjectFrom(bundle)
            .orElseThrow(illegalArgumentException("Can't find Subject in Bundle"));
    final PractitionerRole notifier =
        PractitionerRoles.authorFrom(composition)
            .orElseThrow(illegalArgumentException("Can't find Author in Bundle"));
    final Condition condition =
        Conditions.from(composition)
            .orElseThrow(illegalArgumentException("Can't find Condition in Bundle"));
    final QuestionnaireResponse specificQuestionnaire =
        QuestionnaireResponses.specificFrom(composition)
            .orElseThrow(
                illegalArgumentException("Can't find specific QuestionnaireResponse in Bundle"));
    final Optional<Provenance> provenance = Provenances.from(bundle.getEntry());
    return new BundleBuilderContext(
        composition, patient, notifier, condition, specificQuestionnaire, provenance);
  }

  private static Supplier<IllegalArgumentException> illegalArgumentException(final String s) {
    return () -> new IllegalArgumentException(s);
  }
}
