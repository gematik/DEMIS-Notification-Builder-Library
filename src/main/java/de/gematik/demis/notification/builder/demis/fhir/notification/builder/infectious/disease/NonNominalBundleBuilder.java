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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNotByNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.SpecificInformationDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Organizations;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

/** A builder for NonNominal disease notifications */
public class NonNominalBundleBuilder extends NotificationBundleDiseaseDataBuilder {
  @Nonnull
  public static Bundle deepCopy(@Nonnull final Bundle original) {
    final BundleBuilderContext ctx = BundleBuilderContext.from(original);
    // in case the subject references an organization as address we do copy the address entry, but
    // we then need to manually copy the organization as well
    final Patient notifiedPerson = NotifiedPersonNotByNameDataBuilder.deepCopy(ctx.subject());
    final List<Address> addressWithOrganization =
        notifiedPerson.getAddress().stream()
            .filter(AddressDataBuilder::isReferencingOrganization)
            .toList();
    final List<Organization> referencedOrganizations =
        addressWithOrganization.stream()
            .map(Organizations::fromExtension)
            .flatMap(Collection::stream)
            .toList();

    final PractitionerRole notifierRole = PractitionerRoleBuilder.deepCopy(ctx.notifier());
    final Condition condition = DiseaseDataBuilder.deepCopy(ctx.condition(), notifiedPerson);
    final QuestionnaireResponse specificQuestionnaire =
        SpecificInformationDataBuilder.deepCopy(ctx.specificQuestionnaire(), notifiedPerson);
    final Composition composition =
        NotificationDiseaseDataBuilder.deepCopy(
            ctx.composition(), condition, notifiedPerson, notifierRole, specificQuestionnaire);

    final NotificationBundleDiseaseDataBuilder builder =
        new NonNominalBundleBuilder()
            .setDisease(condition)
            .setSpecificInformation(specificQuestionnaire)
            .setNotificationDisease(composition)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifierRole);

    /*
     Currently the NotificationBundleDiseaseDataBuilder is only adding values set using builder.setOrganizations
     if a common questionnaire is set. This currently does not happen for NonNominal flavours, so we add it
     manually.
    */
    referencedOrganizations.forEach(builder::addAdditionalEntry);
    final Optional<Provenance> provenance = ctx.provenance().map(Provenance::copy);
    provenance.ifPresent(builder::addAdditionalEntry);

    // Note: these setters do not return a NotificationBundleDiseaseDataBuilder and make it harder
    // to chain all calls together
    Bundle bundle =
        builder
            .setProfileUrl(builder.getDefaultProfileUrl())
            .setIdentifierAsNotificationBundleId(original.getIdentifier().getValue())
            .setType(Bundle.BundleType.DOCUMENT)
            .setTimestamp(original.getTimestamp())
            .setLastUpdated(original.getMeta().getLastUpdated())
            .build();

    original.getMeta().getTag().forEach(builder::addTag);

    return bundle;
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
