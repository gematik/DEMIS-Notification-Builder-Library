/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_ROLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public class PractitionerRoleBuilder {

  private PractitionerType practitionerType = PractitionerType.EMPTY;

  private Resource practitionerOrFacility;
  private String metaProfileUrl;

  public PractitionerRoleBuilder asNotifierRole() {
    metaProfileUrl = PROFILE_NOTIFIER_ROLE;
    return this;
  }

  public PractitionerRoleBuilder asSubmittingRole() {
    metaProfileUrl = PROFILE_SUBMITTING_ROLE;
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

  public PractitionerRole build() {
    PractitionerRole practitionerRole = new PractitionerRole();

    practitionerRole.setMeta(new Meta().addProfile(metaProfileUrl));

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

    practitionerRole.setId(generateUuidString());

    return practitionerRole;
  }
}
