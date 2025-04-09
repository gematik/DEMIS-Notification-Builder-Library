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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.junit.jupiter.api.Test;

class PractitionerRoleBuilderTest {

  @Test
  void shouldCreateNotifierRole() {
    PractitionerRole practitionerRole = new PractitionerRoleBuilder().asNotifierRole().build();

    assertThat(practitionerRole.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/NotifierRole");
  }

  @Test
  void shouldCreateSubmittingRole() {
    PractitionerRole practitionerRole = new PractitionerRoleBuilder().asSubmittingRole().build();

    assertThat(practitionerRole.getMeta().getProfile())
        .hasSize(1)
        .extracting("myStringValue")
        .containsOnly("https://demis.rki.de/fhir/StructureDefinition/SubmittingRole");
  }

  @Test
  void shouldCreateWithOrganization() {
    Organization organization = new Organization();
    PractitionerRole practitionerRole =
        new PractitionerRoleBuilder().withOrganization(organization).build();

    assertThat(practitionerRole.getOrganization().getResource()).isEqualTo(organization);
  }

  @Test
  void shouldCreateWithPractitioner() {
    Practitioner practitioner = new Practitioner();
    PractitionerRole practitionerRole =
        new PractitionerRoleBuilder().withPractitioner(practitioner).build();

    assertThat(practitionerRole.getPractitioner().getResource()).isEqualTo(practitioner);
  }

  @Test
  void setDefaults_shouldSetValues() {
    PractitionerRole practitionerRole = new PractitionerRoleBuilder().setDefaults().build();
    assertThat(practitionerRole.getId()).isNotNull();
  }

  @Test
  void setDefaults_shouldKeepValues() {
    String id = "init-id";
    PractitionerRole practitionerRole =
        new PractitionerRoleBuilder().setId(id).setDefaults().build();
    assertThat(practitionerRole.getId()).isEqualTo(id);
  }
}
