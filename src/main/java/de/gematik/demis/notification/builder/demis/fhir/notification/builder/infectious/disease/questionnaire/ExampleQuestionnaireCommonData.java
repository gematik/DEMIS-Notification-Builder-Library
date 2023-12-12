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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;

public class ExampleQuestionnaireCommonData {

  public static final String HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER =
      "https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer";

  private ExampleQuestionnaireCommonData() {
    throw new IllegalStateException("Utility class");
  }

  public static List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
      getCommonQuestionnaire(Organization organization, List<Resource> encounter) {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> responseItemComponents =
        new ArrayList<>();
    responseItemComponents.add(exampleForIsDead());
    responseItemComponents.add(exampleForMilitaryAffiliation());
    responseItemComponents.add(exampleForLabSpecimenReference(organization));
    responseItemComponents.add(exampleForEncounter(encounter));
    responseItemComponents.add(exampleForInfectProtectFacility(organization));
    responseItemComponents.add(exampleForPlaceExposure());
    responseItemComponents.add(exampleForOrganDonation());
    responseItemComponents.add(exampleForAdditionalInformation());

    return responseItemComponents;
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent exampleForIsDead() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().buildAnswerWithDateValue("2022-01-22");

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("deathDate").buildItemWithAnswer(answerForAnswerItem);

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("JA")
            .buildAnswerWithItem(answerItem);

    return new ItemDataBuilder().setLinkId("isDead").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForMilitaryAffiliation() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation")
            .setAnswerCodingCode("memberOfBundeswehr")
            .setAnswerCodingDisplay("Soldat/BW-Angehöriger")
            .buildAnswer();
    return new ItemDataBuilder().setLinkId("militaryAffiliation").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForLabSpecimenReference(Organization organization) {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerForAnswerItem =
        new AnswerDataBuilder().buildAnswerWithReferenceValue(organization);

    QuestionnaireResponse.QuestionnaireResponseItemComponent answerItem =
        new ItemDataBuilder().setLinkId("labSpecimenLab").buildItemWithAnswer(answerForAnswerItem);

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    return new ItemDataBuilder().setLinkId("labSpecimenTaken").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent exampleForEncounter(
      List<Resource> encounterList) {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(createHospitalizedGroups(encounterList));
    return new ItemDataBuilder().setLinkId("hospitalized").buildEntryItem(entryItemAnswer);
  }

  /**
   * Creates an item that wraps multiple hospitalizedGroup-items, each containing a single
   * hospitalizedEncounter-item
   *
   * @param encounterList fully declared encounters
   * @return items with encounter links, aka. hospitalizedGroup-items
   */
  private static List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
      createHospitalizedGroups(List<Resource> encounterList) {
    return encounterList.stream()
        .map(new AnswerDataBuilder()::buildAnswerWithReferenceValue)
        .map(new ItemDataBuilder().setLinkId("hospitalizedEncounter")::buildItemWithAnswer)
        .map(new ItemDataBuilder().setLinkId("hospitalizedGroup")::buildItemWithItem)
        .collect(Collectors.toList());
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForInfectProtectFacility(Organization organization) {
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
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    return new ItemDataBuilder().setLinkId("infectProtectFacility").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent exampleForPlaceExposure() {
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
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswerWithItem(answerItem);
    return new ItemDataBuilder().setLinkId("placeExposure").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent exampleForOrganDonation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .setAnswerCodingSystem(HTTPS_DEMIS_RKI_DE_FHIR_CODE_SYSTEM_YES_OR_NO_ANSWER)
            .setAnswerCodingCode("yes")
            .setAnswerCodingDisplay("Ja")
            .buildAnswer();
    return new ItemDataBuilder().setLinkId("organDonation").buildEntryItem(entryItemAnswer);
  }

  public static QuestionnaireResponse.QuestionnaireResponseItemComponent
      exampleForAdditionalInformation() {

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent entryItemAnswer =
        new AnswerDataBuilder()
            .buildAnswerWithStringValue(
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben");
    return new ItemDataBuilder().setLinkId("additionalInformation").buildEntryItem(entryItemAnswer);
  }
}
