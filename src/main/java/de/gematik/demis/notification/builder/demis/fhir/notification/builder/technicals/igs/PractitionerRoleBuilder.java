package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PREFIX_ORGANIZATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_ROLE;

import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

/** Builder for a PractitionerRole entry in an IGS NotificationBundleSequence. */
@Slf4j
@SuperBuilder
public class PractitionerRoleBuilder extends AbstractIgsResourceBuilder<PractitionerRole> {

  private Optional<Organization> organizationReference;
  private PractitionerRoleProfile profile;

  @Override
  public Optional<PractitionerRole> buildResource() {
    if (organizationReference.isEmpty() || profile == null) {
      log.warn("Cannot build PractitionerRole without organization reference or profile");
      return Optional.empty();
    }
    PractitionerRole practitionerRole = new PractitionerRole();
    practitionerRole.setMeta(MetaBuilder.builder().metaProfile(profile.url).initialize().build());
    practitionerRole.setId(UUID.randomUUID().toString());

    Reference reference = new Reference();
    reference.setReference(PREFIX_ORGANIZATION.concat(organizationReference.get().getId()));
    practitionerRole.setOrganization(reference);

    return Optional.of(practitionerRole);
  }

  public enum PractitionerRoleProfile {
    NOTIFIER_ROLE(PROFILE_NOTIFIER_ROLE),
    SUBMITTER_ROLE(PROFILE_SUBMITTING_ROLE);

    private final String url;

    PractitionerRoleProfile(String url) {
      this.url = url;
    }
  }
}
