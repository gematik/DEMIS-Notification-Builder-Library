package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_YES_OR_NO_ANSWER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_DISEASE_INFORMATION_COMMON;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.QUESTIONNAIRE_DISEASE_QUESTIONS_COMMON;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CommonInformationDataBuilderTest {

  private static final FhirContext FHIR_CONTEXT = FhirContext.forR4Cached();

  private final Patient emptyPatient = new Patient();
  private CommonInformationDataBuilder builder;

  public static CommonInformationDataBuilder createExampleBuilder() {
    CommonInformationDataBuilder common = new CommonInformationDataBuilder();
    common.setDefaults();
    addIsDead(common);
    return common;
  }

  private static void addIsDead(CommonInformationDataBuilder common) {
    Coding coding = new Coding("https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer", "no", "Nein");
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new AnswerDataBuilder().setValueCoding(coding).build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent isDead =
        new ItemDataBuilder().setLinkId("isDead").addAnswer(answer).build();
    common.addItem(isDead);
  }

  @BeforeEach
  void createBuilder() {
    builder = new CommonInformationDataBuilder();
    builder.setId("123456");
    builder.setStatus("completed");
    builder.setProfileUrl("profileUrl");
    builder.setQuestionnaireUrl("questionnaireUrl");
  }

  @Test
  void shouldSetDefault() {
    this.builder = new CommonInformationDataBuilder();
    this.builder.setDefaults();
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse response = this.builder.build();
    assertThat(response.getQuestionnaire())
        .as("questionnaire URL")
        .isEqualTo(QUESTIONNAIRE_DISEASE_QUESTIONS_COMMON);
    assertThat(response.getMeta().hasProfile(PROFILE_DISEASE_INFORMATION_COMMON))
        .as("profile URL")
        .isTrue();
    assertThat(response.getStatus())
        .as("status")
        .isSameAs(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
  }

  @Test
  void setDefaults_shouldKeepValues() {
    this.builder = new CommonInformationDataBuilder();
    String profileUrl = "init-profile-url";
    String questionnaireUrl = "init-questionnaire-url";
    var status = QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS;
    this.builder
        .setProfileUrl(profileUrl)
        .setQuestionnaireUrl(questionnaireUrl)
        .setStandardStatus(status);
    this.builder.setDefaults();
    QuestionnaireResponse response = this.builder.build();
    assertThat(response.getMeta().getProfile().getFirst().getValue()).isEqualTo(profileUrl);
    assertThat(response.getQuestionnaire()).isEqualTo(questionnaireUrl);
    assertThat(response.getStatus()).isSameAs(status);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithDeathQuestionItem() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().setValueDate(new DateType("2022-01-22")).build();

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("deathDate").addAnswer(answerForAnswerItem).build();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(new Coding(CODE_SYSTEM_YES_OR_NO_ANSWER, "yes", "JA"))
            .addItem(answerItem)
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("isDead").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = this.builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"linkId\":\"isDead\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"JA\"},\"item\":[{\"linkId\":\"deathDate\",\"answer\":[{\"valueDate\":\"2022-01-22\"}]}]}]}";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithMilitaryAffiliation() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(
                new Coding(
                    "https://demis.rki.de/fhir/CodeSystem/militaryAffiliation",
                    "memberOfBundeswehr",
                    "Soldat/BW-Angehöriger"))
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("militaryAffiliation").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"linkId\":\"militaryAffiliation\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/militaryAffiliation\",\"code\":\"memberOfBundeswehr\",\"display\":\"Soldat/BW-Angehöriger\"}}";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithLabSpecimenReference() {
    Organization organization = new Organization();
    organization.setId("123456");

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().setValueReference(organization).build();

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("deathDate").addAnswer(answerForAnswerItem).build();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(new Coding(CODE_SYSTEM_YES_OR_NO_ANSWER, "yes", "Ja"))
            .addItem(answerItem)
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("labSpecimenTaken").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "\"item\":[{\"linkId\":\"labSpecimenTaken\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"deathDate\",\"answer\":[{\"valueReference\":{\"reference\":\"Organization/123456\"}}]}]}]}]";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithTwoEncounterItems() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem1 =
        new AnswerDataBuilder().setValueReference(new Encounter().setId("123456")).build();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem2 =
        new AnswerDataBuilder().setValueReference(new Encounter().setId("654321")).build();

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("hospitalizedGroup")
            .addAnswer(answerForAnswerItem1)
            .addAnswer(answerForAnswerItem2)
            .build();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(new Coding(CODE_SYSTEM_YES_OR_NO_ANSWER, "yes", "Ja"))
            .addItem(answerItem)
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("hospitalized").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "\"item\":[{\"linkId\":\"hospitalized\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"hospitalizedGroup\",\"answer\":[{\"valueReference\":{\"reference\":\"Encounter/123456\"}},{\"valueReference\":{\"reference\":\"Encounter/654321\"}}]}]}]}]";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithTwoEncounters() {
    Encounter encounter1 = new Encounter();
    encounter1.setId("123456");
    Encounter encounter2 = new Encounter();
    encounter2.setId("654321");
    this.builder.addHospitalization(encounter1);
    this.builder.addHospitalization(encounter2);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "\"item\":[{\"linkId\":\"hospitalized\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"hospitalizedGroup\",\"item\":[{\"linkId\":\"hospitalizedEncounter\",\"answer\":[{\"valueReference\":{\"reference\":\"Encounter/123456\"}}]}]},{\"linkId\":\"hospitalizedGroup\",\"item\":[{\"linkId\":\"hospitalizedEncounter\",\"answer\":[{\"valueReference\":{\"reference\":\"Encounter/654321\"}}]}]}]}]}]";
    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithInfectProtectFacility() {

    Organization organization = new Organization();
    organization.setId("123456");

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer1 =
        new AnswerDataBuilder().setValueDate(new DateType("2021-12-01")).build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item1 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityBegin").addAnswer(answer1).build();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer2 =
        new AnswerDataBuilder().setValueDate(new DateType("2022-01-05")).build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item2 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityEnd").addAnswer(answer2).build();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer3 =
        new AnswerDataBuilder()
            .setValueCoding(
                new Coding(
                    "https://demis.rki.de/fhir/CodeSystem/organizationAssociation",
                    "employment",
                    "Tätigkeit"))
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item3 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityRole").addAnswer(answer3).build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item4 =
        new ItemDataBuilder()
            .setLinkId("infectProtectFacilityOrganization")
            .addAnswer(new AnswerDataBuilder().setValueReference(organization).build())
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("infectProtectFacilityGroup")
            .setItems(asList(item1, item2, item3, item4))
            .build();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(
                new Coding()
                    .setSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
                    .setCode("yes")
                    .setDisplay("Ja"))
            .addItem(answerItem)
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("infectProtectFacility").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "\"item\":[{\"linkId\":\"infectProtectFacility\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"infectProtectFacilityGroup\",\"item\":[{\"linkId\":\"infectProtectFacilityBegin\",\"answer\":[{\"valueDate\":\"2021-12-01\"}]},{\"linkId\":\"infectProtectFacilityEnd\",\"answer\":[{\"valueDate\":\"2022-01-05\"}]},{\"linkId\":\"infectProtectFacilityRole\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/organizationAssociation\",\"code\":\"employment\",\"display\":\"Tätigkeit\"}}]},{\"linkId\":\"infectProtectFacilityOrganization\",\"answer\":[{\"valueReference\":{\"reference\":\"Organization/123456\"}}]}]}]}]}]";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithPlaceExposure() {

    QuestionnaireResponse.QuestionnaireResponseItemComponent item1 =
        new ItemDataBuilder()
            .setLinkId("placeExposureBegin")
            .addAnswer(new AnswerDataBuilder().setValueDate(new DateType("2021-12-20")).build())
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item2 =
        new ItemDataBuilder()
            .setLinkId("placeExposureEnd")
            .addAnswer(new AnswerDataBuilder().setValueDate(new DateType("2021-12-28")).build())
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item3 =
        new ItemDataBuilder()
            .setLinkId("placeExposureRegion")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding(
                            "https://demis.rki.de/fhir/CodeSystem/geographicRegion",
                            "21000316",
                            "Libyen"))
                    .build())
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item4 =
        new ItemDataBuilder()
            .setLinkId("placeExposureHint")
            .addAnswer(new AnswerDataBuilder().setValueString("Anmerkung").build())
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("placeExposureGroup")
            .setItems(asList(item1, item2, item3, item4))
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(new Coding(CODE_SYSTEM_YES_OR_NO_ANSWER, "yes", "Ja"))
            .addItem(answerItem)
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("placeExposure").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "\"item\":[{\"linkId\":\"placeExposure\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"placeExposureGroup\",\"item\":[{\"linkId\":\"placeExposureBegin\",\"answer\":[{\"valueDate\":\"2021-12-20\"}]},{\"linkId\":\"placeExposureEnd\",\"answer\":[{\"valueDate\":\"2021-12-28\"}]},{\"linkId\":\"placeExposureRegion\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/geographicRegion\",\"code\":\"21000316\",\"display\":\"Libyen\"}}]},{\"linkId\":\"placeExposureHint\",\"answer\":[{\"valueString\":\"Anmerkung\"}]}]}]}]}]";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithOrganDonation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueCoding(new Coding(CODE_SYSTEM_YES_OR_NO_ANSWER, "yes", "Ja"))
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("organDonation").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"linkId\":\"organDonation\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"}}";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithAdditionalInformation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueString(
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben")
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("additionalInformation").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse = builder.build();

    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"linkId\":\"additionalInformation\",\"answer\":[{\"valueString\":\"Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben\"}]}";

    assertThat(s).contains(expected);
  }

  @Test
  void shouldCreateExampleQuestionnaireResponse() {
    this.builder = new CommonInformationDataBuilder();
    this.builder.setDefaults();
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setValueString(
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben")
            .build();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("additionalInformation").addAnswer(entryItemAnswer).build();
    this.builder.addItem(entryItem);
    this.builder.setNotifiedPerson(this.emptyPatient);
    QuestionnaireResponse questionnaireResponse =
        builder.setId("25574ab2-fc80-4492-b999-6e04d6d57478").build();
    FhirParser parser = new FhirParser(FHIR_CONTEXT);
    String s = parser.encodeToJson(questionnaireResponse);
    String expected =
        "{\"linkId\":\"additionalInformation\",\"answer\":[{\"valueString\":\"Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben\"}]}";
    assertThat(s).contains(expected);
  }
}
