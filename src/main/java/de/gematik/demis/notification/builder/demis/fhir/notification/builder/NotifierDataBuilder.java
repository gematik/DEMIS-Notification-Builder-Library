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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.ContactDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

/**
 * @deprecated please use PractitionerRoleBuilder, PractitionerBuilder and OrganizationBuilder
 */
@Setter
@Deprecated(since = "1.2.1")
public class NotifierDataBuilder {

  private static final String HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_ORGANIZATION_TYPE =
      "https://demis.rki.de/fhir/CodeSystem/organizationType";
  private String notifierFacilityName;
  private Address notifierAddress;
  private PractitionerRole notifierRole;
  private List<ContactPoint> notifierTelecomList;
  private String notifierId;
  private String notifierRoleId;
  private HumanName notifierName;
  private PractitionerType notifierType;
  private String notifierFacilityTypeSystem;
  private String notifierFacilityTypeCode;
  private String notifierFacilityTypeDisplay;
  private List<Organization.OrganizationContactComponent> notifierFacilityContactList;
  private List<Identifier> identifiers = new ArrayList<>();

  public PractitionerRole buildNotifierData() {
    notifierRole = new PractitionerRole();

    if (notifierType.equals(PractitionerType.PRACTITIONER)) {
      Practitioner notifier =
          new Practitioner()
              .addName(notifierName)
              .setTelecom(notifierTelecomList)
              .addAddress(notifierAddress);

      notifier.setId(notifierId);
      notifier.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFIER));
      notifier.setIdentifier(identifiers);

      Reference notifierReference = new Reference(notifier);
      notifierRole.setPractitioner(notifierReference);

    } else {
      Organization notifierFacility =
          new Organization()
              .setName(notifierFacilityName)
              .setTelecom(notifierTelecomList)
              .addAddress(notifierAddress);

      notifierFacility.setId(notifierId);
      notifierFacility.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFIER_FACILITY));

      Coding notifierFacilityCoding =
          new Coding(
              notifierFacilityTypeSystem, notifierFacilityTypeCode, notifierFacilityTypeDisplay);
      notifierFacility.addType(new CodeableConcept(notifierFacilityCoding));
      notifierFacility.setIdentifier(identifiers);

      if (notifierFacilityContactList != null) {
        notifierFacility.setContact(notifierFacilityContactList);
      }

      Reference notifierFacilityReference = new Reference(notifierFacility);
      notifierRole.setOrganization(notifierFacilityReference);
    }

    notifierRole.setId(notifierRoleId);
    notifierRole.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFIER_ROLE));

    return notifierRole;
  }

  public PractitionerRole buildNotifierDataForGateway() {
    notifierRoleId = requireNonNullElse(notifierRoleId, generateUuidString());
    notifierType = PractitionerType.ORGANIZATION;

    notifierId = generateUuidString();
    notifierFacilityTypeSystem =
        requireNonNullElse(
            notifierFacilityTypeSystem, HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_ORGANIZATION_TYPE);
    notifierFacilityTypeCode = requireNonNullElse(notifierFacilityTypeCode, "hospital");
    notifierFacilityTypeDisplay = requireNonNullElse(notifierFacilityTypeDisplay, "Krankenhaus");

    return buildNotifierData();
  }

  public PractitionerRole buildLaboratoryExampleNotifierData() {
    notifierRoleId = requireNonNullElse(notifierRoleId, generateUuidString());
    notifierAddress =
        requireNonNullElse(notifierAddress, new AddressDataBuilder().buildExampleAddress());
    notifierTelecomList =
        requireNonNullElse(
            notifierTelecomList, singletonList(new TelecomDataBuilder().buildPhoneContactPoint()));
    notifierName =
        requireNonNullElse(
            notifierName,
            new HumanNameDataBuilder().setUse(HumanName.NameUse.OFFICIAL).buildHumanName());
    notifierType = requireNonNullElse(notifierType, PractitionerType.PRACTITIONER);

    notifierId = requireNonNullElse(notifierId, generateUuidString());
    notifierFacilityTypeSystem =
        requireNonNullElse(
            notifierFacilityTypeSystem, HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_ORGANIZATION_TYPE);
    notifierFacilityTypeCode = requireNonNullElse(notifierFacilityTypeCode, "laboratory");
    notifierFacilityTypeDisplay =
        requireNonNullElse(notifierFacilityTypeDisplay, "Erregerdiagnostische Untersuchungsstelle");
    notifierFacilityName = requireNonNullElse(notifierFacilityName, "Primärlabor");

    return buildNotifierData();
  }

  public PractitionerRole buildLaboratoryExampleNotifierDataHospital() {
    notifierRoleId = requireNonNullElse(notifierRoleId, generateUuidString());
    notifierAddress =
        requireNonNullElse(notifierAddress, new AddressDataBuilder().buildExampleAddress());
    notifierTelecomList =
        requireNonNullElse(
            notifierTelecomList, singletonList(new TelecomDataBuilder().buildPhoneContactPoint()));
    notifierType = requireNonNullElse(notifierType, PractitionerType.ORGANIZATION);

    notifierId = requireNonNullElse(notifierId, generateUuidString());
    notifierFacilityTypeSystem =
        requireNonNullElse(
            notifierFacilityTypeSystem, HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_ORGANIZATION_TYPE);
    notifierFacilityTypeCode = requireNonNullElse(notifierFacilityTypeCode, "hospital");
    notifierFacilityTypeDisplay =
        requireNonNullElse(notifierFacilityTypeDisplay, "Krankenhaus St. Gematik");
    notifierFacilityName = requireNonNullElse(notifierFacilityName, "Klinik");

    return buildNotifierData();
  }

  public PractitionerRole buildReportExampleNotifierData() {
    notifierRoleId = requireNonNullElse(notifierRoleId, generateUuidString());
    notifierFacilityName =
        requireNonNullElse(notifierFacilityName, "Sankt Gertrauden-Krankenhaus GmbH");

    notifierAddress =
        requireNonNullElse(notifierAddress, new AddressDataBuilder().buildExampleAddress());
    notifierTelecomList =
        requireNonNullElse(
            notifierTelecomList, singletonList(new TelecomDataBuilder().buildPhoneContactPoint()));

    return buildNotifierDataForGateway();
  }

  public NotifierDataBuilder addNotifierTelecom(ContactPoint contactPoint) {
    if (notifierTelecomList == null) {
      notifierTelecomList = new ArrayList<>();
    }

    notifierTelecomList.add(contactPoint);

    return this;
  }

  public NotifierDataBuilder addNotifierFacilityContact(
      Organization.OrganizationContactComponent contact) {
    if (notifierFacilityContactList == null) {
      notifierFacilityContactList = new ArrayList<>();
    }
    notifierFacilityContactList.add(contact);
    return this;
  }

  public NotifierDataBuilder addNotifierFacilityContact(HumanName contact) {
    return addNotifierFacilityContact(new ContactDataBuilder().buildWithHumanName(contact));
  }

  public NotifierDataBuilder addIdentifier(Identifier identifier) {
    identifiers.add(identifier);
    return this;
  }

  public NotifierDataBuilder addNotifierId() {
    setNotifierRoleId(generateUuidString());
    return setNotifierId(generateUuidString());
  }
}
