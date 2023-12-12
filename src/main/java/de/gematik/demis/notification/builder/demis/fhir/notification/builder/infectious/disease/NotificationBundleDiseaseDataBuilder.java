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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.BundleUtils.addEntry;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.DiseaseInformationCVDDDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.DiseaseInformationCommonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.ExampleQuestionnaireCVVDData;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.ExampleQuestionnaireCommonData;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class NotificationBundleDiseaseDataBuilder {

  private String id;
  private String profileUrl;
  private String identifierString;
  private String identifierSystem;
  private String type;
  private Date timestamp;

  private Patient notifiedPerson;

  private PractitionerRole notifierRole;

  private Composition notificationDisease;

  private Condition disease;

  private QuestionnaireResponse diseaseInformationCommon;

  private QuestionnaireResponse diseaseInformationSpecific;

  private List<Resource> encounterList = Collections.emptyList();
  private List<Resource> immunizationList = Collections.emptyList();
  private List<Resource> encounterOrganizationList = Collections.emptyList();

  private List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
      questionnaireResponsesForCommonQuesitons = Collections.emptyList();
  private List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
      questionnaireResponsesForSpecificQuestions = Collections.emptyList();

  /**
   * @deprecated no example code data will be provided in the future. please create your own
   *     testdata in your project.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public Bundle buildExampleCVDDDiseaseBundle() {

    identifierString = requireNonNullElse(identifierSystem, generateUuidString());
    identifierSystem = requireNonNullElse(identifierSystem, NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);

    timestamp = new Date();
    type = requireNonNullElse(type, "document");

    notifiedPerson =
        requireNonNullElse(
            notifiedPerson, new NotifiedPersonDataBuilder().buildExampleNotifiedPerson());
    notifierRole =
        requireNonNullElse(
            notifierRole, new NotifierDataBuilder().buildLaboratoryExampleNotifierDataHospital());

    disease =
        requireNonNullElse(
            disease, new DiseaseDataBuilder().buildCVDDexampleDisease(notifiedPerson));

    if (encounterList.isEmpty()) {
      encounterList =
          Collections.singletonList(
              new EncounterDataBuilder()
                  .buildExampleHospitalization(
                      notifiedPerson, (Organization) notifierRole.getOrganization().getResource()));
    }

    diseaseInformationCommon =
        requireNonNullElse(
            diseaseInformationCommon,
            new DiseaseInformationCommonDataBuilder()
                .buildExampleDiseaseInformationCommonData(
                    notifiedPerson,
                    ExampleQuestionnaireCommonData.getCommonQuestionnaire(
                        (Organization) notifierRole.getOrganization().getResource(),
                        encounterList)));

    if (immunizationList.isEmpty()) {
      immunizationList =
          Collections.singletonList(
              new ImmunizationDataBuilder().buildExampleImmunization(notifiedPerson));
    }

    diseaseInformationSpecific =
        requireNonNullElse(
            diseaseInformationSpecific,
            new DiseaseInformationCVDDDataBuilder()
                .buildExampleCVDDInformationSpecificData(
                    notifiedPerson,
                    ExampleQuestionnaireCVVDData.getCVDDQuestionnaire(immunizationList)));

    notificationDisease =
        requireNonNullElse(
            notificationDisease,
            new NotificationDiseaseDataBuilder()
                .buildExampleCVDDNotificationDisease(
                    notifiedPerson,
                    notifierRole,
                    disease,
                    diseaseInformationCommon,
                    diseaseInformationSpecific));

    return buildDiseaseBundle();
  }

  public Bundle buildDiseaseBundle() {
    Bundle bundle = new Bundle();

    id = requireNonNullElse(id, generateUuidString());
    bundle.setId(id);
    profileUrl = requireNonNullElse(profileUrl, PROFILE_NOTIFICATION_BUNDLE_DISEASE);
    bundle.setMeta(new Meta().addProfile(profileUrl));

    Identifier identifier = new Identifier();
    if (isNotBlank(identifierString)) {
      identifier.setValue(identifierString);
    }
    if (isNotBlank(identifierSystem)) {
      identifier.setSystem(identifierSystem);
    }
    bundle.setIdentifier(identifier);

    if (isNotBlank(type)) {
      bundle.setType(Bundle.BundleType.valueOf(type.toUpperCase()));
    }

    if (timestamp != null) {
      bundle.setTimestamp(timestamp);
    }

    addEntry(bundle, notificationDisease);
    addEntry(bundle, notifiedPerson);
    addEntry(bundle, disease);
    addEntry(bundle, notifierRole);
    if (notifierRole.getOrganization() != null) {
      addEntry(bundle, (Organization) notifierRole.getOrganization().getResource());
    }
    addEntry(bundle, encounterList);
    addEntry(bundle, encounterOrganizationList);
    addEntry(bundle, diseaseInformationCommon);
    addEntry(bundle, immunizationList);
    addEntry(bundle, diseaseInformationSpecific);

    return bundle;
  }
}
