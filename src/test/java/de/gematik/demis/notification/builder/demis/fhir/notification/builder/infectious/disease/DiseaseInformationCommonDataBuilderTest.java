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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_YES_OR_NO_ANSWER;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.AnswerDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.DiseaseInformationCommonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.ItemDataBuilder;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiseaseInformationCommonDataBuilderTest {

  private Patient emptyPatient;
  private List<QuestionnaireResponse.QuestionnaireResponseItemComponent> list;
  private DiseaseInformationCommonDataBuilder builder;

  private static FhirContext fhirContext;

  @BeforeAll
  static void contextSetUp() {
    fhirContext = FhirContext.forR4();
  }

  @BeforeEach
  void setUp() {
    emptyPatient = new Patient();

    builder = new DiseaseInformationCommonDataBuilder();
    builder.setId("123456");
    builder.setStatus("completed");
    builder.setProfileUrl("profileUrl");
    builder.setQuestionnaireUrl("questionnaireUrl");

    list = new ArrayList<>();
  }

  @Test
  void shouldCreateQuestionnaireResponseWithDeathQuestionItem() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().buildAnswerWithDateValue("2022-01-22");

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("deathDate").buildItemWithAnswer(answerForAnswerItem);

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("JA")
            .buildAnswerWithItem(answerItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("isDead").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"isDead\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"JA\"},\"item\":[{\"linkId\":\"deathDate\",\"answer\":[{\"valueDate\":\"2022-01-22\"}]}]}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithMilitaryAffiliation() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation")
            .setAnswerCodingCode("memberOfBundeswehr")
            .setAnswerCodingDisplay("Soldat/BW-Angehöriger")
            .buildAnswer();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("militaryAffiliation").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"militaryAffiliation\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/militaryAffiliation\",\"code\":\"memberOfBundeswehr\",\"display\":\"Soldat/BW-Angehöriger\"}}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithLabSpecimenReference() {
    Organization organization = new Organization();
    organization.setId("123456");

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(organization);

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("deathDate").buildItemWithAnswer(answerForAnswerItem);

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("labSpecimenTaken").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"labSpecimenTaken\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"deathDate\",\"answer\":[{\"valueReference\":{\"reference\":\"Organization/123456\"}}]}]}]}]}";

    assertThat(s).isEqualTo(expected).contains("123456");
  }

  @Test
  void shouldCreateQuestionnaireResponseWithTwoEncounter() {
    Encounter encounter1 = new Encounter();
    encounter1.setId("123456");

    Encounter encounter2 = new Encounter();
    encounter2.setId("654321");

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem1 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(encounter1);
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem2 =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(encounter2);

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("hospitalizedGroup")
            .buildItemWithAnswer(asList(answerForAnswerItem1, answerForAnswerItem2));

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("hospitalized").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"hospitalized\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"hospitalizedGroup\",\"answer\":[{\"valueReference\":{\"reference\":\"Encounter/123456\"}},{\"valueReference\":{\"reference\":\"Encounter/654321\"}}]}]}]}]}";

    assertThat(s).isEqualTo(expected).contains("123456").contains("654321");
  }

  @Test
  void shouldCreateQuestionnaireResponseWithInfectProtectFacility() {

    Organization organization = new Organization();
    organization.setId("123456");

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer1 =
        new AnswerDataBuilder().buildAnswerWithDateValue("2021-12-01");
    QuestionnaireResponse.QuestionnaireResponseItemComponent item1 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityBegin").buildItemWithAnswer(answer1);
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer2 =
        new AnswerDataBuilder().buildAnswerWithDateValue("2022-01-05");
    QuestionnaireResponse.QuestionnaireResponseItemComponent item2 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityEnd").buildItemWithAnswer(answer2);
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer3 =
        new AnswerDataBuilder()
            .setAnswerCodingSystem("https://demis.rki.de/fhir/CodeSystem/organizationAssociation")
            .setAnswerCodingCode("employment")
            .setAnswerCodingDisplay("Tätigkeit")
            .buildAnswer();
    QuestionnaireResponse.QuestionnaireResponseItemComponent item3 =
        new ItemDataBuilder().setLinkId("infectProtectFacilityRole").buildItemWithAnswer(answer3);
    QuestionnaireResponse.QuestionnaireResponseItemComponent item4 =
        new ItemDataBuilder()
            .setLinkId("infectProtectFacilityOrganization")
            .buildItemWithAnswer(
                new AnswerDataBuilder().buildAnswerWithReferenceValue(organization));
    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("infectProtectFacilityGroup")
            .buildItemWithItem(asList(item1, item2, item3, item4));

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("infectProtectFacility").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"infectProtectFacility\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"infectProtectFacilityGroup\",\"item\":[{\"linkId\":\"infectProtectFacilityBegin\",\"answer\":[{\"valueDate\":\"2021-12-01\"}]},{\"linkId\":\"infectProtectFacilityEnd\",\"answer\":[{\"valueDate\":\"2022-01-05\"}]},{\"linkId\":\"infectProtectFacilityRole\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/organizationAssociation\",\"code\":\"employment\",\"display\":\"Tätigkeit\"}}]},{\"linkId\":\"infectProtectFacilityOrganization\",\"answer\":[{\"valueReference\":{\"reference\":\"Organization/123456\"}}]}]}]}]}]}";

    assertThat(s).isEqualTo(expected).contains("123456");
  }

  @Test
  void shouldCreateQuestionnaireResponseWithPlaceExposure() {

    QuestionnaireResponse.QuestionnaireResponseItemComponent item1 =
        new ItemDataBuilder()
            .setLinkId("placeExposureBegin")
            .buildItemWithAnswer(new AnswerDataBuilder().buildAnswerWithDateValue("2021-12-20"));
    QuestionnaireResponse.QuestionnaireResponseItemComponent item2 =
        new ItemDataBuilder()
            .setLinkId("placeExposureEnd")
            .buildItemWithAnswer(new AnswerDataBuilder().buildAnswerWithDateValue("2021-12-28"));
    QuestionnaireResponse.QuestionnaireResponseItemComponent item3 =
        new ItemDataBuilder()
            .setLinkId("placeExposureRegion")
            .buildItemWithAnswer(
                new AnswerDataBuilder()
                    .setAnswerCodingSystem("https://demis.rki.de/fhir/CodeSystem/geographicRegion")
                    .setAnswerCodingCode("21000316")
                    .setAnswerCodingDisplay("Libyen")
                    .buildAnswer());
    QuestionnaireResponse.QuestionnaireResponseItemComponent item4 =
        new ItemDataBuilder()
            .setLinkId("placeExposureHint")
            .buildItemWithAnswer(new AnswerDataBuilder().buildAnswerWithStringValue("Anmerkung"));
    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder()
            .setLinkId("placeExposureGroup")
            .buildItemWithItem(asList(item1, item2, item3, item4));
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("placeExposure").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"placeExposure\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"},\"item\":[{\"linkId\":\"placeExposureGroup\",\"item\":[{\"linkId\":\"placeExposureBegin\",\"answer\":[{\"valueDate\":\"2021-12-20\"}]},{\"linkId\":\"placeExposureEnd\",\"answer\":[{\"valueDate\":\"2021-12-28\"}]},{\"linkId\":\"placeExposureRegion\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/geographicRegion\",\"code\":\"21000316\",\"display\":\"Libyen\"}}]},{\"linkId\":\"placeExposureHint\",\"answer\":[{\"valueString\":\"Anmerkung\"}]}]}]}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithOrganDonation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswer();
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("organDonation").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"organDonation\",\"answer\":[{\"valueCoding\":{\"system\":\"https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer\",\"code\":\"yes\",\"display\":\"Ja\"}}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateQuestionnaireResponseWithAdditionalInformation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .buildAnswerWithStringValue(
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben");
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("additionalInformation").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder.buildDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"123456\",\"meta\":{\"profile\":[\"profileUrl\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"questionnaireUrl\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"additionalInformation\",\"answer\":[{\"valueString\":\"Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben\"}]}]}";

    assertThat(s).isEqualTo(expected);
  }

  @Test
  void shouldCreateExampleQuestionnaireResponse() {

    builder = new DiseaseInformationCommonDataBuilder();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .buildAnswerWithStringValue(
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben");
    QuestionnaireResponse.QuestionnaireResponseItemComponent entryItem =
        new ItemDataBuilder().setLinkId("additionalInformation").buildEntryItem(entryItemAnswer);
    list.add(entryItem);

    QuestionnaireResponse questionnaireResponse =
        builder
            .setId("25574ab2-fc80-4492-b999-6e04d6d57478")
            .buildExampleDiseaseInformationCommonData(emptyPatient, list);

    FhirParser parser = new FhirParser(fhirContext);
    String s = parser.encodeToJson(questionnaireResponse);

    String expected =
        "{\"resourceType\":\"QuestionnaireResponse\",\"id\":\"25574ab2-fc80-4492-b999-6e04d6d57478\",\"meta\":{\"profile\":[\"https://demis.rki.de/fhir/StructureDefinition/DiseaseInformationCommon\"]},\"contained\":[{\"resourceType\":\"Patient\",\"id\":\"1\"}],\"questionnaire\":\"https://demis.rki.de/fhir/Questionnaire/DiseaseQuestionsCommon\",\"status\":\"completed\",\"subject\":{\"reference\":\"#1\"},\"item\":[{\"linkId\":\"additionalInformation\",\"answer\":[{\"valueString\":\"Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben\"}]}]}";

    assertThat(s).isEqualTo(expected);
  }
}
