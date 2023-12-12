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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.Collections;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class OrganizationBuilderTest {

  @Test
  void shouldAddDefaultValues() {

    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");

      OrganizationBuilder organizationBuilder = new OrganizationBuilder();
      organizationBuilder.setDefaultData();

      Organization organization = organizationBuilder.build();
      assertThat(organization.getTypeFirstRep().getCodingFirstRep().getSystem())
          .isEqualTo("https://demis.rki.de/fhir/CodeSystem/organizationType");
      assertThat(organization.getId()).isEqualTo("1");
    }
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
        .containsExactlyInAnyOrder("https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR");
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
            "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR",
            "https://demis.rki.de/fhir/NamingSystem/DemisParticipantId");

    assertThat(organization.getIdentifier())
        .hasSize(2)
        .extracting("value")
        .containsExactlyInAnyOrder("bsnrValue", "participantId");
  }

  @Test
  void shouldCreateSubmittingFacility() {
    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
    organizationBuilder.asSubmittingFacility();
    Organization organization = organizationBuilder.build();

    assertThat(organization.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/SubmittingFacility");

    assertThat(organization.getIdentifier()).isEmpty();
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
}
