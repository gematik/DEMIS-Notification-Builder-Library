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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.QuestionnaireResponseBuilder.deepCopy;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonAnonymousDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Slf4j
public final class DiseaseExcerptBuilder {
  private DiseaseExcerptBuilder() {}

  @Nonnull
  public static Bundle createExcerptNotifiedPersonNotByNameFromNominalBundle(
      @Nonnull final Bundle originalBundle) {
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle);
    final Patient notifiedPerson =
        NotifiedPersonNonNominalDataBuilder.createExcerptNotByNamePatient(ctx.subject());
    return DiseaseExcerptBuilder.createExcerpt(
        originalBundle, ctx, new NotificationBundleDiseaseDataBuilder(), notifiedPerson);
  }

  @Nonnull
  public static Bundle createExcerptNotifiedPersonAnonymousFromNonNominalBundle(
      @Nonnull final Bundle originalBundleNonNominal) {
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundleNonNominal);
    final List<Address> addresses =
        AddressDataBuilder.copyAddressesForExcerpt(
            List.copyOf(NotifiedPersonNonNominalDataBuilder.getAddressesToCopy(ctx.subject())));
    final Patient notifiedPerson =
        NotifiedPersonAnonymousDataBuilder.createAnonymousPatientForExcerpt(
            ctx.subject(), addresses);
    return DiseaseExcerptBuilder.createExcerpt(
        originalBundleNonNominal,
        ctx,
        new NotificationBundleDiseaseNonNominalDataBuilder(),
        notifiedPerson);
  }

  private static Bundle createExcerpt(
      @Nonnull final Bundle originalBundle,
      final BundleBuilderContext ctx,
      final NotificationBundleDiseaseDataBuilder builder,
      final Patient notifiedPerson) {

    final Condition condition = DiseaseDataBuilder.deepCopy(ctx.condition(), notifiedPerson);
    final QuestionnaireResponse specificQuestionnaire =
        ctx.specificQuestionnaireResponse().isPresent()
            ? deepCopy(ctx.specificQuestionnaireResponse().get(), notifiedPerson)
            : null;

    final QuestionnaireResponse commonQuestionnaire =
        ctx.commonQuestionnaireResponse().isPresent()
            ? deepCopy(ctx.commonQuestionnaireResponse().get(), notifiedPerson)
            : null;

    final PractitionerRole notifierRole = PractitionerRoleBuilder.deepCopy73(ctx.notifier());

    final Composition composition =
        NotificationDiseaseDataBuilder.excerptCopy(
            ctx.composition(),
            condition,
            notifiedPerson,
            notifierRole,
            Optional.ofNullable(specificQuestionnaire),
            Optional.ofNullable(commonQuestionnaire));

    builder
        .setDisease(condition)
        .setSpecificInformation(specificQuestionnaire)
        .setNotificationDisease(composition)
        .setNotifiedPerson(notifiedPerson)
        .setNotifierRole(notifierRole)
        .setHospitalizations(
            ctx.encounters().stream()
                .map(encounter -> EncounterDataBuilder.deepCopy(encounter, notifiedPerson))
                .toList())
        .setEncounterOrganizations(ctx.organizations())
        .setImmunizations(
            ctx.immunizations().stream()
                .map(
                    immunization -> ImmunizationDataBuilder.deepyCopy(immunization, notifiedPerson))
                .toList());
    try {
      builder.setCommonInformation(commonQuestionnaire);
    } catch (final Exception e) {
      log.debug("Could not set common information questionnaire response in excerpt bundle", e);
    }

    originalBundle.getMeta().getTag().forEach(builder::addTag);

    ctx.provenance().map(Provenance::copy).ifPresent(builder::addAdditionalEntry);

    final Identifier newIdentifierForCopiedBundle =
        new Identifier()
            .setValue(Utils.generateUuidString())
            .setSystem(DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);

    final String originalBundleIdentifier = originalBundle.getIdentifier().getValue();
    final Coding referenceTagToOriginalBundle =
        new Coding(
            DemisConstants.RELATED_NOTIFICATION_CODING_SYSTEM,
            originalBundleIdentifier,
            "Relates to message with identifier: " + originalBundleIdentifier);

    return builder
        .setId(originalBundle.getId())
        .setProfileUrl(builder.getDefaultProfileUrl())
        .setIdentifier(newIdentifierForCopiedBundle)
        .setType(Bundle.BundleType.DOCUMENT)
        .setTimestamp(originalBundle.getTimestamp())
        .setLastUpdated(originalBundle.getMeta().getLastUpdated())
        .addTag(referenceTagToOriginalBundle)
        .build();
  }
}
