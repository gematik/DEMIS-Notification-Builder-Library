package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.CommonInformationDataBuilderTest;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.SpecificInformationDataBuilderTest;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;

class NotificationBundleDiseaseDataBuilderDynamicTest {

  private final NotificationBundleDiseaseDataBuilder builder =
      new NotificationBundleDiseaseDataBuilder();

  private Bundle bundle;

  @Test
  void testBuild() {
    this.builder.setDefaults();
    // resources
    Patient notifiedPerson = setNotifiedPerson();
    Condition disease = setDisease(notifiedPerson);
    PractitionerRole practitionerRole = setPractitionerRole();
    QuestionnaireResponse commonInfo = setCommonInfo(notifiedPerson);
    QuestionnaireResponse specificInfo = setSpecificInfo(notifiedPerson);
    // composition
    setComposition(notifiedPerson, practitionerRole, disease, commonInfo, specificInfo);
    // bundle verification
    this.bundle = this.builder.build();
    verify();
  }

  private void setComposition(
      Patient notifiedPerson,
      PractitionerRole practitionerRole,
      Condition disease,
      QuestionnaireResponse commonInfo,
      QuestionnaireResponse specificInfo) {
    NotificationDiseaseDataBuilder composition =
        NotificationDiseaseDataBuilderTest.createCvddBuilder();
    composition
        .setNotifiedPerson(notifiedPerson)
        .setNotifierRole(practitionerRole)
        .setDisease(disease)
        .setCommonQuestionnaireResponse(commonInfo)
        .setSpecificQuestionnaireResponse(specificInfo);
    this.builder.setNotificationDisease(composition.build());
  }

  private PractitionerRole setPractitionerRole() {
    HumanName name =
        new HumanNameDataBuilder().addGivenName("Paul").setFamilyName("Practitioneer").build();
    Practitioner practitioner =
        new PractitionerBuilder()
            .setPractitionerName(name)
            .setPractitionerId(Utils.generateUuidString())
            .asNotifier()
            .build();
    PractitionerRole role =
        new PractitionerRoleBuilder().asNotifierRole().withPractitioner(practitioner).build();
    this.builder.setNotifierRole(role);
    return role;
  }

  private Patient setNotifiedPerson() {
    HumanName name =
        new HumanNameDataBuilder().addGivenName("Markus").setFamilyName("Musterpatient").build();
    Patient notifiedPerson =
        new NotifiedPersonNominalDataBuilder().setDefault().setHumanName(name).build();
    this.builder.setNotifiedPerson(notifiedPerson);
    return notifiedPerson;
  }

  private Condition setDisease(Patient notifiedPerson) {
    Condition disease =
        DiseaseDataBuilderTest.createCvddBuilder().setNotifiedPerson(notifiedPerson).build();
    this.builder.setDisease(disease);
    return disease;
  }

  private QuestionnaireResponse setCommonInfo(Patient notifiedPerson) {
    QuestionnaireResponse commonInfo =
        CommonInformationDataBuilderTest.createExampleBuilder()
            .setNotifiedPerson(notifiedPerson)
            .build();
    this.builder.setCommonInformation(commonInfo);
    return commonInfo;
  }

  private QuestionnaireResponse setSpecificInfo(Patient notifiedPerson) {
    Immunization immunization = ImmunizationDataBuilderTest.createCvddBuilder().build();
    this.builder.addImmunization(immunization);
    QuestionnaireResponse specificInfo =
        SpecificInformationDataBuilderTest.createCvddBuilder(immunization)
            .setNotifiedPerson(notifiedPerson)
            .build();
    this.builder.setSpecificInformation(specificInfo);
    return specificInfo;
  }

  private static boolean hasProfileUrl(Resource resource, String profileUrl) {
    Meta meta = resource.getMeta();
    if (meta != null) {
      return meta.getProfile().stream().map(CanonicalType::getValue).anyMatch(profileUrl::equals);
    }
    return false;
  }

  private void verify() {
    assertThat(this.bundle).as("disease notification bundle").isNotNull();
    verifyBundleProfileUrl();
    verifyBundleId();
    verifyNotificationBundleId();
    verifyBundleEntries();
    verifyCommonQuestionnaireResponse();
    verifyCvddSpecificQuestionnaireResponse();
  }

  private void verifyBundleEntries() {
    List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
    String entryUrls =
        entries.stream()
            .map(Bundle.BundleEntryComponent::getFullUrl)
            .collect(Collectors.joining(", ", "[", "]"));
    assertThat(entries).as("bundle entries: " + entryUrls).hasSize(8);
    assertThat(entries.get(0).getFullUrl())
        .as("composition is first entry")
        .contains("Composition");
    assertThat(countEntries(DemisConstants.PROFILE_NOTIFICATION_DISEASE))
        .as("CVDD composition exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_NOTIFIED_PERSON))
        .as("notified person exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_NOTIFIER_ROLE))
        .as("notifier exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_DISEASE_CVDD))
        .as("CVDD condition exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON))
        .as("common questionnaire responses exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_DISEASE_INFORMATION_CVDD))
        .as("CVDD questionnaire responses exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_NOTIFIER))
        .as("notifying practitioner exists")
        .isEqualTo(1L);
    assertThat(countEntries(DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD))
        .as("CVDD immunization")
        .isEqualTo(1L);
  }

  private void verifyBundleId() {
    assertThat(this.bundle.getId()).as("bundle ID").isNotEmpty();
  }

  private void verifyBundleProfileUrl() {
    assertThat(hasProfileUrl(this.bundle, PROFILE_NOTIFICATION_BUNDLE_DISEASE))
        .as("disease notification bundle profile URL: " + PROFILE_NOTIFICATION_BUNDLE_DISEASE)
        .isTrue();
  }

  private void verifyNotificationBundleId() {
    Identifier notificationBundleId = this.bundle.getIdentifier();
    assertThat(notificationBundleId).as("notification bundle ID").isNotNull();
    assertThat(notificationBundleId.getSystem())
        .as("notification bundle ID system")
        .isEqualTo(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    assertThat(notificationBundleId.getValue()).as("notification bundle ID value").isNotEmpty();
  }

  private long countEntries(String profileUrl) {
    return this.bundle.getEntry().stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(e -> hasProfileUrl(e, profileUrl))
        .count();
  }

  private void verifyCommonQuestionnaireResponse() {
    Optional<Resource> resource = findEntry(DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON);
    assertThat(resource)
        .as("common information questionnaire response")
        .isPresent()
        .hasValueSatisfying(QuestionnaireResponse.class::isInstance);
    QuestionnaireResponse commonInfo = (QuestionnaireResponse) resource.get();
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items = commonInfo.getItem();
    assertThat(items).as("items").hasSize(1);
    QuestionnaireResponse.QuestionnaireResponseItemComponent item = items.get(0);
    assertThat(item.getLinkId()).as("isDead link ID").isEqualTo("isDead");
    assertThat(item.getItem()).as("sub-items").isEmpty();
    List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answers = item.getAnswer();
    assertThat(answers).as("answers").hasSize(1);
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer = answers.get(0);
    Type value = answer.getValue();
    assertThat(value).as("answer type").isInstanceOf(Coding.class);
    assertThat(((Coding) value).getDisplay()).as("answer coding display value").isEqualTo("Nein");
  }

  private Optional<Resource> findEntry(String profileUrl) {
    return this.bundle.getEntry().stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(r -> hasProfileUrl(r, profileUrl))
        .findFirst();
  }

  private void verifyCvddSpecificQuestionnaireResponse() {
    Optional<Resource> resource = findEntry(DemisConstants.PROFILE_DISEASE_INFORMATION_CVDD);
    assertThat(resource)
        .as("CVDD specific information questionnaire response")
        .isPresent()
        .hasValueSatisfying(QuestionnaireResponse.class::isInstance);
    QuestionnaireResponse cvddInfo = (QuestionnaireResponse) resource.get();
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items = cvddInfo.getItem();
    assertThat(items)
        .as(
            "items: "
                + items.stream()
                    .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getLinkId)
                    .collect(Collectors.joining(", ", "[", "]")))
        .hasSize(4);
    assertThat(
            items.stream()
                .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getLinkId)
                .filter("infectionRiskKind"::equals)
                .count())
        .as("number of infection risks")
        .isEqualTo(2L);
    QuestionnaireResponse.QuestionnaireResponseItemComponent lastItem = items.get(3);
    assertThat(lastItem.getLinkId())
        .as("fourth item is second infection risk")
        .isEqualTo("infectionRiskKind");
    assertThat(lastItem.getAnswer().get(0).getValueCoding().getCode())
        .as("second infection risk code")
        .isEqualTo("235856003");
  }
}
