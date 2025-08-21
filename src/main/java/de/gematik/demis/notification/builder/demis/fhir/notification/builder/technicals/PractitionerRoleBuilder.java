package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getShortReferenceOrUrnUuid;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public class PractitionerRoleBuilder implements InitializableFhirObjectBuilder {

  private PractitionerType practitionerType = PractitionerType.EMPTY;

  @Setter private String id;
  private Resource practitionerOrFacility;
  private String profileUrl;

  /**
   * @return a copy of the given {@link PractitionerRole} and the contained resources (e.g. the
   *     organization)
   */
  @Nonnull
  public static PractitionerRole deepCopy(@Nonnull final PractitionerRole original) {
    PractitionerRole copy = new PractitionerRole();
    copy.setId(original.getId());
    copy.setMeta(original.getMeta());
    Practitioner referencedPractitioner = (Practitioner) original.getPractitioner().getResource();
    Organization referencedOrganization = (Organization) original.getOrganization().getResource();
    if (referencedPractitioner != null) {
      Practitioner copiedPractitioner = referencedPractitioner.copy();
      Reference newReference = new Reference(copiedPractitioner);
      newReference.setReference(getShortReferenceOrUrnUuid(copiedPractitioner));
      copy.setPractitioner(newReference);
    }
    if (referencedOrganization != null) {
      Organization copiedOrganization = referencedOrganization.copy();
      Reference value = new Reference(copiedOrganization);
      value.setReference(getShortReferenceOrUrnUuid(copiedOrganization));
      copy.setOrganization(value);
    }
    return copy;
  }

  /**
   * @return a copy of the given {@link PractitionerRole} and the contained resources (e.g. the
   *     organization) with 7.3 specific modifications
   */
  @Nonnull
  public static PractitionerRole deepCopy73(@Nonnull final PractitionerRole original) {
    final PractitionerRole copy = new PractitionerRole();
    copy.setId(original.getId());
    copy.setMeta(original.getMeta());
    if (original.getPractitioner().getResource()
        instanceof final Practitioner referencedPractitioner) {
      final Practitioner copiedPractitioner = referencedPractitioner.copy();
      final Reference newReference =
          new Reference(copiedPractitioner)
              .setReference(getShortReferenceOrUrnUuid(copiedPractitioner));
      copy.setPractitioner(newReference);
    }
    if (original.getOrganization().getResource()
        instanceof final Organization originalOrganization) {
      final Organization organization = removeNotifiedPersonFacilityProfile(originalOrganization);
      final Reference reference =
          new Reference(organization).setReference(getShortReferenceOrUrnUuid(organization));
      copy.setOrganization(reference);
    }
    return copy;
  }

  /**
   * @return a copy of the given Organization with the profile NotifiedPersonFacility removed (if
   *     available)
   */
  @Nonnull
  private static Organization removeNotifiedPersonFacilityProfile(
      @Nonnull final Organization organization) {
    final Organization result = organization.copy();
    final List<CanonicalType> allowedProfiles =
        Metas.profilesFrom(result).stream()
            .filter(Predicate.not(DemisConstants.PROFILE_NOTIFIED_PERSON_FACILITY::equals))
            .map(CanonicalType::new)
            .toList();
    result.getMeta().setProfile(allowedProfiles);
    return result;
  }

  @Override
  public PractitionerRoleBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    return this;
  }

  public PractitionerRoleBuilder asNotifierRole() {
    profileUrl = PROFILE_NOTIFIER_ROLE;
    return this;
  }

  public PractitionerRoleBuilder asSubmittingRole() {
    profileUrl = PROFILE_SUBMITTING_ROLE;
    return this;
  }

  public PractitionerRoleBuilder withPractitioner(Practitioner practitioner) {
    this.practitionerOrFacility = practitioner;
    practitionerType = PractitionerType.PRACTITIONER;
    return this;
  }

  public PractitionerRoleBuilder withOrganization(Organization organization) {
    this.practitionerOrFacility = organization;
    practitionerType = PractitionerType.ORGANIZATION;
    return this;
  }

  @Override
  public PractitionerRole build() {
    PractitionerRole practitionerRole = new PractitionerRole();
    practitionerRole.setId(Objects.requireNonNullElseGet(this.id, Utils::generateUuidString));
    practitionerRole.setMeta(new Meta().addProfile(profileUrl));
    switch (practitionerType) {
      case ORGANIZATION:
        practitionerRole.setOrganization(new Reference(practitionerOrFacility));
        break;
      case PRACTITIONER:
        practitionerRole.setPractitioner(new Reference(practitionerOrFacility));
        break;
      default:
        break;
    }
    return practitionerRole;
  }
}
