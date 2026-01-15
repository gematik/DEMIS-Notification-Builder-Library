package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.QuestionnaireResponseBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.SequencedCollection;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Slf4j
public class NotificationBundleDiseaseNonNominalDataBuilder
    extends NotificationBundleDiseaseDataBuilder {

  @Override
  public NotificationDiseaseDataBuilder createComposition() {
    log.debug("Disease notification bundle builder generates composition.");
    NotificationDiseaseDataBuilder compositionBuilder =
        new NotificationDiseaseNonNominalDataBuilder();
    createCompositionHelper(compositionBuilder);
    return compositionBuilder;
  }

  @Nonnull
  public static Bundle deepCopy(@Nonnull final Bundle originalBundle) {
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle);
    // in case the subject references an organization as address we do copy the address entry, but
    // we then need to manually copy the organization as well
    final SequencedCollection<Address> addresses =
        NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(ctx.subject());
    final Patient notifiedPerson =
        NotifiedPersonNonNominalDataBuilder.deepCopy(ctx.subject(), addresses);

    final PractitionerRole notifierRole = PractitionerRoleBuilder.deepCopy73(ctx.notifier());
    final Condition condition = DiseaseDataBuilder.deepCopy(ctx.condition(), notifiedPerson);
    final QuestionnaireResponse specificQuestionnaire =
        ctx.specificQuestionnaireResponse().isPresent()
            ? QuestionnaireResponseBuilder.deepCopy(
                ctx.specificQuestionnaireResponse().get(), notifiedPerson)
            : null;
    final Composition composition =
        NotificationDiseaseDataBuilder.deepCopy(
            ctx.composition(), condition, notifiedPerson, notifierRole, specificQuestionnaire);

    final NotificationBundleDiseaseDataBuilder builder =
        new NotificationBundleDiseaseNonNominalDataBuilder()
            .setDisease(condition)
            .setSpecificInformation(specificQuestionnaire)
            .setNotificationDisease(composition)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifierRole);

    ctx.provenance().map(Provenance::copy).ifPresent(builder::addAdditionalEntry);

    // Ensure this is called before you call builder.build()!
    originalBundle.getMeta().getTag().forEach(builder::addTag);

    return builder
        .setId(originalBundle.getId())
        .setProfileUrl(builder.getDefaultProfileUrl())
        .setIdentifierAsNotificationBundleId(originalBundle.getIdentifier().getValue())
        .setType(Bundle.BundleType.DOCUMENT)
        .setTimestamp(originalBundle.getTimestamp())
        .setLastUpdated(originalBundle.getMeta().getLastUpdated())
        .build();
  }

  @Override
  public NotificationBundleDiseaseDataBuilder setCommonInformation(
      @CheckForNull final QuestionnaireResponse commonInformation) {
    throw new UnsupportedOperationException("Not supported for this builder");
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE_NON_NOMINAL;
  }
}
