package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

/**
 * @deprecated please use PractitionerRoleBuilder, PractitionerBuilder and OrganizationBuilder
 */
@Setter
@Deprecated(since = "1.2.1")
public class SubmitterDataBuilder {

  private de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType
      practitionerType;
  private String submittingFacilityName;
  private Address submitterAddress;
  private Practitioner submittingPerson;
  private Organization submittingFacility;
  private ContactPoint submitterTelecom;
  private String submittingRoleId;
  private String submitterId;
  private HumanName submittingPersonName;
  private List<ContactPoint> submitterTelecomList = new ArrayList<>();
  private List<Organization.OrganizationContactComponent> submitterFacilityContactList =
      new ArrayList<>();
  private List<Identifier> identifiers = new ArrayList<>();

  private String typeCode;
  private String typeDisplay;
  private String typeSystem;

  public PractitionerRole buildSubmitterData() {
    PractitionerRole submittingRole = new PractitionerRole();

    if (practitionerType.equals(PractitionerType.ORGANIZATION)) {
      processOrganizationData(submittingRole);
    } else {
      processPractitionerData(submittingRole);
    }

    Meta meta = new Meta().addProfile(DemisConstants.PROFILE_SUBMITTING_ROLE);
    submittingRole.setMeta(meta);

    submittingRole.setId(submittingRoleId);

    return submittingRole;
  }

  public PractitionerRole buildExampleSubmitterFacilityData() {

    submitterAddress =
        requireNonNullElse(submitterAddress, new AddressDataBuilder().buildExampleAddress());
    submitterTelecom =
        requireNonNullElse(submitterTelecom, new TelecomDataBuilder().buildPhoneContactPoint());
    practitionerType = requireNonNullElse(practitionerType, PractitionerType.ORGANIZATION);
    submitterId = requireNonNullElse(submitterId, generateUuidString());

    submittingFacilityName =
        requireNonNullElse(submittingFacilityName, "Musterorganisation Einsender");

    submittingRoleId = requireNonNullElse(submittingRoleId, generateUuidString());

    return buildSubmitterData();
  }

  public PractitionerRole buildExampleSubmitterData() {

    submitterAddress =
        requireNonNullElse(submitterAddress, new AddressDataBuilder().buildExampleAddress());
    submitterTelecom =
        requireNonNullElse(submitterTelecom, new TelecomDataBuilder().buildPhoneContactPoint());
    practitionerType = requireNonNullElse(practitionerType, PractitionerType.PRACTITIONER);
    submitterId = requireNonNullElse(submitterId, generateUuidString());
    submittingPersonName =
        requireNonNullElse(submittingPersonName, new HumanNameDataBuilder().buildHumanName());

    submittingRoleId = requireNonNullElse(submittingRoleId, generateUuidString());
    return buildSubmitterData();
  }

  private void processPractitionerData(PractitionerRole submittingRole) {
    if (submittingPerson == null) {
      submittingPerson = new Practitioner();

      submittingPerson.setMeta(new Meta().addProfile(DemisConstants.PROFILE_SUBMITTING_PERSON));
      submittingPerson.setId(submitterId);
      submittingPerson.addName(submittingPersonName);
      submittingPerson.setTelecom(submitterTelecomList);
      submittingPerson.addTelecom(submitterTelecom);
      submittingPerson.addAddress(submitterAddress);
      submittingPerson.setIdentifier(identifiers);
    }
    submittingRole.setPractitioner(new Reference(submittingPerson));
  }

  private void processOrganizationData(PractitionerRole submittingRole) {
    if (submittingFacility == null) {
      submittingFacility = new Organization();

      submittingFacility.setName(submittingFacilityName);
      submittingFacility.addAddress(submitterAddress);
      submittingFacility.setMeta(new Meta().addProfile(DemisConstants.PROFILE_SUBMITTING_FACILITY));
      submittingFacility.setId(submitterId);
      submittingFacility.setTelecom(submitterTelecomList);
      submittingFacility.addTelecom(submitterTelecom);
      submittingFacility.setIdentifier(identifiers);
      submittingFacility.addType(
          new CodeableConcept(new Coding(typeSystem, typeCode, typeDisplay)));
    }
    submittingRole.setOrganization(new Reference(submittingFacility));
  }

  public void setSubmittingFacility(Organization submittingFacility) {
    this.submittingFacility = submittingFacility;
    this.practitionerType = PractitionerType.ORGANIZATION;
    this.submittingPerson = null;
  }

  public void setSubmittingPerson(Practitioner submittingPerson) {
    this.submittingPerson = submittingPerson;
    this.practitionerType = PractitionerType.PRACTITIONER;
    this.submittingFacility = null;
  }

  public SubmitterDataBuilder addIdentifier(Identifier identifier) {
    identifiers.add(identifier);
    return this;
  }

  public SubmitterDataBuilder addContactPoint(Organization.OrganizationContactComponent contact) {
    submitterFacilityContactList.add(contact);
    return this;
  }

  public SubmitterDataBuilder addSubmitterTelecom(ContactPoint contactPoint) {
    submitterTelecomList.add(contactPoint);
    return this;
  }

  public SubmitterDataBuilder addSubmitterId() {
    setSubmittingRoleId(generateUuidString());
    return setSubmitterId(generateUuidString());
  }
}
