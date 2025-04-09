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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

public class PractitionerRoleBuilder implements InitializableFhirObjectBuilder {

  private PractitionerType practitionerType = PractitionerType.EMPTY;

  @Setter private String id;
  private Resource practitionerOrFacility;
  private String profileUrl;

  /**
   * @return a copy of the given {@link PractitionerRole} and the contained resources (e.g. the
   *     organization)
   */
  public static PractitionerRole deepCopy(@Nonnull final PractitionerRole original) {
    return original.copy();
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
