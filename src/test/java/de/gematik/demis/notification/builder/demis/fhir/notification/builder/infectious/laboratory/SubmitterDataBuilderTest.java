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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubmitterDataBuilderTest {

  private SubmitterDataBuilder submitterDataBuilder;

  @BeforeEach
  void setUp() {
    submitterDataBuilder = new SubmitterDataBuilder();
  }

  @Test
  void shoudlSetAllDataForSubmittingFacility() {
    Address address = new Address();
    submitterDataBuilder.setSubmitterAddress(address);

    ContactPoint telecom = new ContactPoint();
    submitterDataBuilder.setSubmitterTelecom(telecom);

    submitterDataBuilder.setPractitionerType(PractitionerType.ORGANIZATION);

    submitterDataBuilder.setSubmittingRoleId("someId");
    submitterDataBuilder.setSubmittingFacilityName("someSubmittingFacilityName");

    PractitionerRole practitionerRole = submitterDataBuilder.buildExampleSubmitterFacilityData();

    assertThat(practitionerRole.getOrganization().getResource()).isNotNull();
    assertThat(practitionerRole.getPractitioner().getResource()).isNull();
    assertThat(practitionerRole.getId()).isEqualTo("someId");

    Organization resource = (Organization) practitionerRole.getOrganization().getResource();
    assertThat(resource.getAddress()).hasSize(1);
    assertThat(resource.getAddress().get(0)).isEqualTo(address);
    assertThat(resource.getTelecom()).hasSize(1);
    assertThat(resource.getTelecom().get(0)).isEqualTo(telecom);
    assertThat(resource.getName()).isEqualTo("someSubmittingFacilityName");
  }

  @Test
  void shoudlSetGivenOrganizationAsFacility() {
    Organization organization = new Organization();
    submitterDataBuilder.setSubmittingFacility(organization);

    PractitionerRole practitionerRole = submitterDataBuilder.buildExampleSubmitterFacilityData();

    assertThat(practitionerRole.getOrganization().getResource()).isNotNull();
    assertThat(practitionerRole.getPractitioner().getResource()).isNull();
    assertThat(practitionerRole.getOrganization().getResource()).isEqualTo(organization);
  }

  @Test
  void shouldSetAllDataForSubmitter() {
    Address address = new Address();
    submitterDataBuilder.setSubmitterAddress(address);

    ContactPoint telecom = new ContactPoint();
    submitterDataBuilder.setSubmitterTelecom(telecom);

    submitterDataBuilder.setPractitionerType(PractitionerType.PRACTITIONER);

    submitterDataBuilder.setSubmittingRoleId("someId");
    submitterDataBuilder.setSubmitterId("someSubmitterId");
    HumanName submittingPersonName = new HumanName();
    submitterDataBuilder.setSubmittingPersonName(submittingPersonName);

    PractitionerRole practitionerRole = submitterDataBuilder.buildExampleSubmitterFacilityData();

    assertThat(practitionerRole.getOrganization().getResource()).isNull();
    assertThat(practitionerRole.getPractitioner().getResource()).isNotNull();
    assertThat(practitionerRole.getId()).isEqualTo("someId");

    Practitioner resource = (Practitioner) practitionerRole.getPractitioner().getResource();
    assertThat(resource.getAddress()).hasSize(1);
    assertThat(resource.getAddress().get(0)).isEqualTo(address);
    assertThat(resource.getTelecom()).hasSize(1);
    assertThat(resource.getTelecom().get(0)).isEqualTo(telecom);
    assertThat(resource.getName()).hasSize(1);
    assertThat(resource.getName().get(0)).isEqualTo(submittingPersonName);
  }

  @Test
  void shoudlSetGivenPractitionerAsFacility() {
    Practitioner practitioner = new Practitioner();
    submitterDataBuilder.setSubmittingPerson(practitioner);

    PractitionerRole practitionerRole = submitterDataBuilder.buildExampleSubmitterFacilityData();

    assertThat(practitionerRole.getOrganization().getResource()).isNull();
    assertThat(practitionerRole.getPractitioner().getResource()).isNotNull();
    assertThat(practitionerRole.getPractitioner().getResource()).isEqualTo(practitioner);
  }
}
