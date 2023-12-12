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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;

@Setter
public class OrganizationBuilder {

  private String participantId;
  private String laboratoryId;
  private String identifier;

  @Setter(AccessLevel.PRIVATE)
  private List<Identifier> identifierList = new ArrayList<>();

  private boolean active = false;
  private String typeCode;
  private String typeSystem;
  private String typeDisplay;
  private List<ContactPoint> telecomList = new ArrayList<>();
  private Address address;
  private String facilityName;
  private String metaProfileUrl;
  private String bsnrValue;

  @Setter(AccessLevel.PRIVATE)
  private boolean isNotifierFacility = false;

  private String organizationId;

  public OrganizationBuilder asNotifierFacility() {
    metaProfileUrl = PROFILE_NOTIFIER_FACILITY;
    isNotifierFacility = true;
    return this;
  }

  public OrganizationBuilder asSubmittingFacility() {
    metaProfileUrl = PROFILE_SUBMITTING_FACILITY;
    isNotifierFacility = false;
    return this;
  }

  public OrganizationBuilder setDefaultData() {
    organizationId = generateUuidString();
    typeSystem = DemisConstants.CODE_SYSTEM_ORGANIZATION_TYPE;
    return this;
  }

  public Organization build() {
    Organization organization = new Organization();

    organization.setMeta(new Meta().addProfile(metaProfileUrl));

    if (isNotifierFacility) {
      addBsnr(organization);
      addParticipantId(organization);
      addLaboratoryId(organization);
    }
    addIdentifiers(organization);

    if (active) {
      organization.setActive(active);
    }

    Coding coding = new Coding(typeSystem, typeCode, typeDisplay);
    organization.addType(new CodeableConcept(coding));
    organization.setName(facilityName);
    organization.setTelecom(telecomList);
    organization.addAddress(address);
    organization.setId(organizationId);

    return organization;
  }

  private void addIdentifiers(Organization organization) {
    identifierList.forEach(organization::addIdentifier);
    if (identifier != null) {
      organization.addIdentifier(new Identifier().setValue(identifier));
    }
  }

  public OrganizationBuilder addIdentifier(Identifier identifier) {
    identifierList.add(identifier);
    return this;
  }

  private void addLaboratoryId(Organization organization) {
    if (laboratoryId != null) {
      Identifier participantIdentifier =
          new Identifier()
              .setValue(laboratoryId)
              .setSystem(DemisConstants.NAMING_SYSTEM_LABORATORY_ID);
      organization.addIdentifier(participantIdentifier);
    }
  }

  private void addParticipantId(Organization organization) {
    if (participantId != null) {
      Identifier participantIdentifier =
          new Identifier()
              .setValue(participantId)
              .setSystem(DemisConstants.NAMING_SYSTEM_PARTICIPANT_ID);
      organization.addIdentifier(participantIdentifier);
    }
  }

  private void addBsnr(Organization organization) {
    if (bsnrValue != null) {
      Identifier bsnrIdentifier =
          new Identifier().setSystem(DemisConstants.NAMING_SYSTEM_BSNR).setValue(bsnrValue);
      organization.addIdentifier(bsnrIdentifier);
    }
  }
}
