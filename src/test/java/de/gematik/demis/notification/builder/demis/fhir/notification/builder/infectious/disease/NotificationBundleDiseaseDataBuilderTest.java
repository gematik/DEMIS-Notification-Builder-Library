package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationBundleDiseaseDataBuilderTest {

  private NotificationBundleDiseaseDataBuilder builder;

  @BeforeEach
  void createMinimalBuilder() {
    this.builder = new NotificationBundleDiseaseDataBuilder();
    this.builder.setDefaults();
    this.builder.setNotifiedPerson(new PatientBuilder().setDefaults().build());
    this.builder.setDisease(new DiseaseDataBuilder().setDefaults().build());
    final QuestionnaireResponse commonInformation = new QuestionnaireResponse();
    commonInformation.setId("c1");
    this.builder.setCommonInformation(commonInformation);
    final QuestionnaireResponse specificInformation = new QuestionnaireResponse();
    specificInformation.setId("s1");
    this.builder.setSpecificInformation(specificInformation);
  }

  @Test
  void testImmunizationSetters() {
    Immunization i1 = new Immunization();
    i1.setId("i1");
    Immunization i2 = new Immunization();
    i2.setId("i2");
    assertThat(this.builder.addImmunization(i1)).isSameAs(this.builder);
    assertThat(this.builder.setImmunizations(Collections.singletonList(i2))).isSameAs(this.builder);
    List<Immunization> immunizations =
        builder.build().getEntry().stream()
            .map(Bundle.BundleEntryComponent::getResource)
            .filter(Immunization.class::isInstance)
            .map(Immunization.class::cast)
            .toList();
    assertThat(immunizations).containsExactly(i2);
  }

  @Test
  void testHospitalizationSetters() {
    Encounter e1 = new Encounter();
    e1.setId("e1");
    Encounter e2 = new Encounter();
    e2.setId("e2");
    assertThat(this.builder.addHospitalization(e1)).isSameAs(this.builder);
    assertThat(this.builder.setHospitalizations(Collections.singletonList(e2)))
        .isSameAs(this.builder);
    List<Encounter> hospitalizations =
        builder.build().getEntry().stream()
            .map(Bundle.BundleEntryComponent::getResource)
            .filter(Encounter.class::isInstance)
            .map(Encounter.class::cast)
            .toList();
    assertThat(hospitalizations).containsExactly(e2);
  }

  @Test
  void testOrganizationSetters() {
    Organization o1 = new Organization();
    o1.setId("1");
    Organization o2 = new Organization();
    o2.setId("2");
    assertThat(this.builder.addOrganization(o1)).isSameAs(this.builder);
    assertThat(this.builder.setOrganizations(Collections.singletonList(o2))).isSameAs(this.builder);
    List<Organization> organizations =
        builder.build().getEntry().stream()
            .map(Bundle.BundleEntryComponent::getResource)
            .filter(Organization.class::isInstance)
            .map(Organization.class::cast)
            .toList();
    assertThat(organizations).containsExactly(o2);
  }

  @Test
  void build_shouldHandleMissingSpecificQuestionnaireAndCreateComposition() {
    this.builder.setDefaults();
    Condition condition = new Condition();
    condition.setId("c1");
    condition
        .getMeta()
        .addProfile(NotificationBundleDiseaseDataBuilder.CONDITION_PROFILE_PREFIX + "CVDD");
    this.builder.setDisease(condition);
    this.builder.setSpecificInformation(null);
    Composition composition = this.builder.createComposition().build();
    assertThat(composition.getMeta().getProfile().getFirst().getValue())
        .as("composition profile URL contains category")
        .isEqualTo("https://demis.rki.de/fhir/StructureDefinition/NotificationDiseaseCVDD");
    assertThat(composition.getSection()).hasSize(2);
    this.builder.setNotificationDisease(composition);
    Bundle bundle = this.builder.build();
    assertThat(bundle).isNotNull();
  }
}
