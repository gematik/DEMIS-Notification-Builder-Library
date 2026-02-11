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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class OrganizationBuilderTest {

  @Test
  void shouldAddDefaultValues() {
    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");

      OrganizationBuilder organizationBuilder = new OrganizationBuilder();
      organizationBuilder.setDefaults();
      Organization organization = organizationBuilder.build();

      // verify
      assertThat(organization.getId()).isEqualTo("1");
      Meta meta = organization.getMeta();
      assertThat(meta).isNotNull();
      List<CanonicalType> profiles = meta.getProfile();
      assertThat(profiles).hasSize(1);
      assertThat(profiles.getFirst().getValue())
          .as("default organization meta profile")
          .isEqualTo(DemisConstants.PROFILE_ORGANIZATION);
    }
  }

  @Test
  void setDefaultValues_shouldKeepValues() {
    String organizationId = "init-id";
    String profileUrl = "init-profile";
    Organization organization =
        new OrganizationBuilder()
            .setOrganizationId(organizationId)
            .setMetaProfileUrl(profileUrl)
            .setDefaults()
            .build();
    assertThat(organization.getId())
        .as("organization ID before setDefaults()")
        .isEqualTo(organizationId);
    assertThat(organization.getMeta().getProfile().getFirst().getValue())
        .as("organization profile URL before setDefaults()")
        .isEqualTo(profileUrl);
  }

  @Test
  void shouldCreateNotifierFacility() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asNotifierFacility();
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/NotifierFacility");

    assertThat(organization.getIdentifier()).isEmpty();
  }

  @Test
  void shouldCreateNotifierFacilityWithBSNR() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asNotifierFacility();
    organizationBuilder.setBsnrValue("bsnr");
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/NotifierFacility");

    assertThat(organization.getIdentifier())
        .hasSize(1)
        .extracting("system")
        .containsExactlyInAnyOrder(DemisConstants.NAMING_SYSTEM_BSNR);
  }

  @Test
  void shouldCreateLaboratoryFacilityWithBSNR() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asLaboratoryFacility();
    organizationBuilder.setBsnrValue("bsnr");
    organizationBuilder.setTypeCode("bad-type-code-to-test-automatic-correction");
    Organization laboratory = organizationBuilder.build();

    assertThat(laboratory.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly(DemisConstants.PROFILE_LABORATORY_FACILITY);

    assertThat(laboratory.getIdentifier())
        .hasSize(1)
        .extracting("system")
        .containsExactlyInAnyOrder(DemisConstants.NAMING_SYSTEM_BSNR);

    final List<CodeableConcept> types = laboratory.getType();
    assertThat(types).hasSize(1);
    final Coding type = types.getFirst().getCodingFirstRep();
    assertThat(type.getSystem()).isEqualTo(DemisConstants.CODE_SYSTEM_ORGANIZATION_TYPE);
    assertThat(type.getCode()).isEqualTo("laboratory");
  }

  @Test
  void shouldCreateNotifierFacilityWithParticipantId() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asNotifierFacility();
    organizationBuilder.setParticipantId("participantId");
    organizationBuilder.setBsnrValue("bsnrValue");
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/NotifierFacility");

    assertThat(organization.getIdentifier())
        .hasSize(2)
        .extracting("system")
        .containsExactlyInAnyOrder(
            DemisConstants.NAMING_SYSTEM_BSNR, DemisConstants.NAMING_SYSTEM_PARTICIPANT_ID);

    assertThat(organization.getIdentifier())
        .hasSize(2)
        .extracting("value")
        .containsExactlyInAnyOrder("bsnrValue", "participantId");
  }

  @Test
  void shouldCreateLaboratoryFacilityWithParticipantId() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asLaboratoryFacility();
    organizationBuilder.setParticipantId("participantId");
    organizationBuilder.setBsnrValue("bsnrValue");
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly(DemisConstants.PROFILE_LABORATORY_FACILITY);

    assertThat(organization.getIdentifier())
        .hasSize(2)
        .extracting("system")
        .containsExactlyInAnyOrder(
            DemisConstants.NAMING_SYSTEM_BSNR, DemisConstants.NAMING_SYSTEM_PARTICIPANT_ID);

    assertThat(organization.getIdentifier())
        .hasSize(2)
        .extracting("value")
        .containsExactlyInAnyOrder("bsnrValue", "participantId");
  }

  @Test
  void shouldCreateInfectProtectFacility() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asInfectProtectFacility();
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly(DemisConstants.PROFILE_INFECT_PROTECT_FACILITY);

    assertThat(organization.getIdentifier()).isEmpty();
  }

  @Test
  void shouldCreateSubmittingFacility() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    HumanName contactPerson = HumanNameDataBuilder.with("Mrs", "Contact", "Person");
    Organization organization =
        organizationBuilder
            .asSubmittingFacility()
            .addContact("Mr", "Firstname", "Lastname")
            .setSubmitterDetails(contactPerson, "departmentName")
            .build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/SubmittingFacility");

    assertThat(organization.getIdentifier()).isEmpty();

    Organization.OrganizationContactComponent contactFirstRep = organization.getContactFirstRep();
    // StringType doesn't implement equals properly, so we use this ugly hack
    assertThat(contactFirstRep.getAddress().getLine().getFirst()).hasToString("departmentName");
    assertThat(contactFirstRep.getName()).isEqualTo(contactPerson);
  }

  @Test
  void shouldCreateSubmittingFacilityWithIdentifier() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asSubmittingFacility();
    organizationBuilder.setIdentifier("identifier");
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/SubmittingFacility");

    assertThat(organization.getIdentifier()).hasSize(1).extracting("system").containsOnlyNulls();
    assertThat(organization.getIdentifier()).extracting("value").containsOnly("identifier");
  }

  @Test
  void shouldRejectSubmitterDetailsUnlessIsSubmittingFacility() {
    // GIVEN a new builder is created for a notifier facility
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asNotifierFacility();
    HumanName humanName = HumanNameDataBuilder.with("mr", "first", "last");

    // AND the user attempts to set submitter details
    assertThrows(
        IllegalStateException.class,
        () -> organizationBuilder.setSubmitterDetails(humanName, "department"));
    // THEN an exception is thrown
  }

  @Test
  void shouldCreateOrganizationWithGivenData() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.setActive(true);
    organizationBuilder.setTypeCode("typeCode");
    organizationBuilder.setTypeDisplay("typeDisplay");
    organizationBuilder.setTypeSystem("typeSystem");
    organizationBuilder.setFacilityName("facilityName");
    ContactPoint contactPoint = new ContactPoint();
    organizationBuilder.setTelecomList(Collections.singletonList(contactPoint));
    Address address = new Address();
    organizationBuilder.setAddress(address);
    Organization organization = organizationBuilder.build();

    assertThat(organization.getActive()).isTrue();
    assertThat(organization.getTypeFirstRep().getCodingFirstRep().getCode()).isEqualTo("typeCode");
    assertThat(organization.getTypeFirstRep().getCodingFirstRep().getDisplay())
        .isEqualTo("typeDisplay");
    assertThat(organization.getTypeFirstRep().getCodingFirstRep().getSystem())
        .isEqualTo("typeSystem");
    assertThat(organization.getName()).isEqualTo("facilityName");
    assertThat(organization.getTelecom()).containsOnly(contactPoint);
    assertThat(organization.getAddress()).containsOnly(address);
  }

  @Test
  void addTelecom_shouldAddContactPoint() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    ContactPoint contactPoint = new ContactPoint();
    organizationBuilder.addTelecom(contactPoint);
    Organization organization = organizationBuilder.build();
    assertThat(organization.getTelecom()).containsOnly(contactPoint);
  }

  @Test
  void canSetMultipleProfileUrls() {
    final Organization orga =
        new OrganizationBuilder().asSubmittingFacility().addNotifiedPersonFacilityProfile().build();
    final List<String> actual =
        orga.getMeta().getProfile().stream().map(PrimitiveType::getValueAsString).toList();
    assertThat(actual)
        .containsExactly(
            DemisConstants.PROFILE_SUBMITTING_FACILITY,
            DemisConstants.PROFILE_NOTIFIED_PERSON_FACILITY);
  }

  /** This test confirms that the implementation works as expected for users of 2.5.10 and before */
  @Test
  void thatSetMetaProfileUrlOverwritesExistingData() {
    final String expectedUrl = "anything";
    final Organization orga =
        new OrganizationBuilder()
            .asSubmittingFacility()
            .addNotifiedPersonFacilityProfile()
            .setMetaProfileUrl(expectedUrl)
            .build();
    final List<String> actual =
        orga.getMeta().getProfile().stream().map(PrimitiveType::getValueAsString).toList();
    assertThat(actual).containsExactly(expectedUrl);
  }

  @Test
  void shouldAddASingleAddress() {
    Address address = new Address();

    OrganizationBuilder organizationBuilder = new OrganizationBuilder().setAddress(address);

    Organization organization = organizationBuilder.build();

    assertThat(organization.getAddress()).containsExactly(address);
  }
}
