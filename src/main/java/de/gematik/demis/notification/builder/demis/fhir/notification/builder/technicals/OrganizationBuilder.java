package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_ORGANIZATION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class OrganizationBuilder implements InitializableFhirObjectBuilder {

  private static final Set<String> SUBMITTING_FACILITIES =
      Set.of(PROFILE_SUBMITTING_FACILITY, PROFILE_NOTIFIED_PERSON_FACILITY);
  private String participantId;
  private String laboratoryId;
  private String identifier;

  @Setter(AccessLevel.PRIVATE)
  private List<Identifier> identifierList = new ArrayList<>();

  private boolean active = false;
  private Coding type;
  private List<Organization.OrganizationContactComponent> contacts = new ArrayList<>();
  private List<ContactPoint> telecomList = new ArrayList<>();
  private List<Address> address;
  private String facilityName;
  // Sequenced set guarantees iteration order, which can help stabilizing tests
  private final SequencedSet<String> metaProfileUrls = new LinkedHashSet<>();
  private String bsnrValue;

  @Setter(AccessLevel.PRIVATE)
  private boolean isNotifierFacility = false;

  private String organizationId;

  @Setter(AccessLevel.PRIVATE)
  private boolean isLaboratoryFacility;

  /**
   * Set default parameters. It's best to call this method before setting any values to avoid
   * overwriting your values.
   */
  @Override
  public OrganizationBuilder setDefaults() {
    if (this.organizationId == null) {
      this.organizationId = generateUuidString();
    }
    if (this.metaProfileUrls.isEmpty()) {
      this.metaProfileUrls.add(PROFILE_ORGANIZATION);
    }
    return this;
  }

  public OrganizationBuilder addTelecom(ContactPoint contactPoint) {
    telecomList.add(contactPoint);
    return this;
  }

  public OrganizationBuilder asNotifierFacility() {
    setMetaProfileUrl(PROFILE_NOTIFIER_FACILITY);
    isNotifierFacility = true;
    return this;
  }

  public OrganizationBuilder asSubmittingFacility() {
    setMetaProfileUrl(PROFILE_SUBMITTING_FACILITY);
    isNotifierFacility = false;
    return this;
  }

  public OrganizationBuilder asLaboratoryFacility() {
    setMetaProfileUrl(DemisConstants.PROFILE_LABORATORY_FACILITY);
    isLaboratoryFacility = true;
    isNotifierFacility = false;
    return this;
  }

  public OrganizationBuilder asInfectProtectFacility() {
    setMetaProfileUrl(DemisConstants.PROFILE_INFECT_PROTECT_FACILITY);
    isLaboratoryFacility = false;
    isNotifierFacility = false;
    return this;
  }

  /**
   * Add {@link DemisConstants#PROFILE_NOTIFIED_PERSON_FACILITY} to the existing meta.profileUrl.
   */
  public OrganizationBuilder addNotifiedPersonFacilityProfile() {
    addMetaProfileUrl(PROFILE_NOTIFIED_PERSON_FACILITY);
    return this;
  }

  public OrganizationBuilder addContact(Organization.OrganizationContactComponent contact) {
    this.contacts.add(contact);
    return this;
  }

  public OrganizationBuilder addContact(String prefix, String given, String family) {
    final var contact = new Organization.OrganizationContactComponent();
    contact.setName(HumanNameDataBuilder.with(prefix, given, family));
    return addContact(contact);
  }

  @Override
  public Organization build() {
    Organization organization = new Organization();

    addProfileUrls(organization);

    if (isNotifierFacility || isLaboratoryFacility) {
      addBsnr(organization);
      addParticipantId(organization);
      addLaboratoryId(organization);
    }
    addIdentifiers(organization);

    if (active) {
      organization.setActive(true);
    }

    addType(organization);
    organization.setName(facilityName);
    organization.setTelecom(telecomList);
    organization.setContact(contacts);
    organization.setAddress(address);
    organization.setId(organizationId);
    return organization;
  }

  public OrganizationBuilder setAddress(List<Address> address) {
    this.address = address;
    return this;
  }

  public OrganizationBuilder setAddress(Address address) {
    if (this.address == null) {
      this.address = new ArrayList<>();
    }
    this.address.add(address);
    return this;
  }

  /** Reset present URLs and set the given URL as only element */
  public OrganizationBuilder setMetaProfileUrl(final String url) {
    metaProfileUrls.clear();
    metaProfileUrls.add(url);
    return this;
  }

  private void addProfileUrls(final Organization organization) {
    final Meta meta = new Meta();
    metaProfileUrls.forEach(meta::addProfile);
    organization.setMeta(meta);
  }

  /** Add the given URL if it's not already present */
  public OrganizationBuilder addMetaProfileUrl(final String url) {
    if (this.metaProfileUrls.contains(url)) {
      return this;
    }

    this.metaProfileUrls.add(url);
    return this;
  }

  public OrganizationBuilder addIdentifier(Identifier identifier) {
    identifierList.add(identifier);
    return this;
  }

  /**
   * Set department name and contact person for the current organization.
   *
   * @throws IllegalStateException in case this method is called for an organization profile other
   *     than {@link DemisConstants#PROFILE_SUBMITTING_FACILITY}.
   */
  public OrganizationBuilder setSubmitterDetails(
      final HumanName contactPerson, final String departmentName) {
    assertSubmittProfile();
    final Organization.OrganizationContactComponent submitterDetails =
        new Organization.OrganizationContactComponent()
            .setName(contactPerson)
            .setAddress(new Address().addLine(departmentName));
    /*
     * We assume that:
     * 1. the code throughout our applications expects only one contact
     * 2. that one contact is carrying the department name and contact person details in fields "borrowed" from FHIR
     * 3. the code is calling {@link Organization#getContactFirstRep()} to retrieve the first contact
     *
     * Therefore, we need to place the details at the beginning of the list, just in case a caller is adding multiple contacts.
     * We could in theory throw an exception but that might break existing clients (e.g. when they conditionally add
     * multiple contacts).
     */
    contacts.addFirst(submitterDetails);
    return this;
  }

  private void assertSubmittProfile() {
    final boolean onlyValidProfiles = SUBMITTING_FACILITIES.containsAll(metaProfileUrls);
    if (!onlyValidProfiles) {
      throw new IllegalStateException(
          String.format(
              "Can't set submitter details for organizations other than '%s'",
              PROFILE_SUBMITTING_FACILITY));
    }
  }

  public OrganizationBuilder setTypeSystem(String system) {
    getOrCreateType().setSystem(system);
    return this;
  }

  public OrganizationBuilder setTypeDisplay(String display) {
    getOrCreateType().setDisplay(display);
    return this;
  }

  public OrganizationBuilder setTypeCode(String code) {
    getOrCreateType().setCode(code);
    return this;
  }

  private Coding getOrCreateType() {
    if (this.type == null) {
      this.type = new Coding();
      this.type.setSystem(DemisConstants.CODE_SYSTEM_ORGANIZATION_TYPE);
    }
    return this.type;
  }

  private void addType(Organization organization) {
    if (isLaboratoryFacility) {
      setTypeCode("laboratory");
    }
    if (this.type != null) {
      organization.addType(new CodeableConcept(this.type));
    }
  }

  private void addIdentifiers(Organization organization) {
    identifierList.forEach(organization::addIdentifier);
    if (identifier != null) {
      organization.addIdentifier(new Identifier().setValue(identifier));
    }
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
