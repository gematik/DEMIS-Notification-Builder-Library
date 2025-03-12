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
 * #L%
 */

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.AnswerDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.CommonInformationDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.ItemDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.SpecificInformationDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.DateTimeTypeBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.test.FhirJsonTestsUtil;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Builds a disease notification step-by-step and compares to content of a JSON file. */
class NotificationBundleDiseaseDataBuilderJsonFileTest {

  private static final String REFERENCE_BUNDLE_FILE =
      "src/test/resources/disease/disease-request.json";
  private static final String TIMESTAMP_TEXT = "2023-07-12T13:32:12+02:00";
  public static final String SNOMED_INFO_SCT = "http://snomed.info/sct";

  private static String expected;
  private static Date timestamp;
  private final NotificationBundleDiseaseDataBuilder bundle =
      new NotificationBundleDiseaseDataBuilder();

  @BeforeAll
  static void createExpectation() {
    try {
      expected = Files.readString(Path.of(REFERENCE_BUNDLE_FILE));
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to read reference JSON file: " + REFERENCE_BUNDLE_FILE, e);
    }
  }

  @BeforeAll
  static void createTimestamp() {
    timestamp = Date.from(ZonedDateTime.parse(TIMESTAMP_TEXT).toInstant());
  }

  @Test
  void shouldBuildExactlyMatchingRequest() {
    buildBundle();
    FhirJsonTestsUtil.assertEqualJson(
        this.bundle.build(), expected, "disease notification FHIR bundle");
  }

  /** Configures the builder, but does not actually trigger the build method. */
  private void buildBundle() {
    setMetadata();
    Patient notifiedPerson = setNotifiedPerson();
    Organization organization = setNotifier();
    setCondition(notifiedPerson);
    setCommonInfo(notifiedPerson, organization);
    setSpecificInfo(notifiedPerson);
    setComposition();
  }

  private void setComposition() {
    var composition =
        this.bundle
            .createComposition()
            .setId("3")
            .setIdentifierAsNotificationId("4")
            .setDate(new DateTimeType(timestamp))
            .build();
    this.bundle.setNotificationDisease(composition);
  }

  private void setMetadata() {
    this.bundle.setDefaults();
    this.bundle.setId("1");
    this.bundle.setIdentifierAsNotificationBundleId("2");
    this.bundle.setTimestamp(timestamp);
  }

  private Patient setNotifiedPerson() {
    var name =
        new HumanName()
            .setFamily("Betroffen")
            .addGiven("Bertha-Luise")
            .addGiven("Hanna")
            .addGiven("Karin")
            .setText("Bertha-Luise Hanna Karin Betroffen");
    var address =
        new AddressDataBuilder()
            .setStreet("Berthastraße 123")
            .setCity("Betroffenenstadt")
            .setPostalCode("abcde")
            .setCountry("20422")
            .build();
    var notifiedPerson =
        new NotifiedPersonDataBuilder()
            .setDefaults()
            .setId("5")
            .setGender(Enumerations.AdministrativeGender.FEMALE)
            .setBirthdate("09.06.1999")
            .setHumanName(name)
            .addAddress(address)
            .build();
    this.bundle.setNotifiedPerson(notifiedPerson);
    return notifiedPerson;
  }

  private Organization setNotifier() {
    var phone =
        new TelecomDataBuilder()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue("01234567")
            .build();
    var mail =
        new TelecomDataBuilder()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue("anna@ansprechpartner.de")
            .build();
    var address =
        new AddressDataBuilder()
            .setStreet("Krankenhausstraße 1")
            .setCity("Buchhorst")
            .setPostalCode("abcde")
            .setCountry("20422")
            .build();
    var organization =
        new OrganizationBuilder()
            .asNotifierFacility()
            .setOrganizationId("10")
            .setTypeSystem("https://demis.rki.de/fhir/CodeSystem/organizationType")
            .setTypeCode("hospital")
            .setTypeDisplay("Krankenhaus")
            .setFacilityName("TEST Organisation")
            .setTelecomList(Arrays.asList(phone, mail))
            .setAddress(address)
            .build();
    var notifier =
        new PractitionerRoleBuilder()
            .setId("6")
            .asNotifierRole()
            .withOrganization(organization)
            .build();
    this.bundle.setNotifierRole(notifier);
    return organization;
  }

  private void setCondition(Patient notifiedPerson) {
    var disease =
        new DiseaseDataBuilder()
            .setId("7")
            .setProfileUrlByDisease("cvdd")
            .setDisease(
                new Coding()
                    .setSystem(DemisConstants.CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY)
                    .setCode("cvdd")
                    .setDisplay("Coronavirus-Krankheit-2019 (COVID-19)"))
            .setVerificationStatus(
                new Coding()
                    .setSystem(DemisConstants.CODE_SYSTEM_CONDITION_VERIFICATION_STATUS)
                    .setCode("confirmed"))
            .setRecordedDate("2022-01-02")
            .setOnset("2022-01-01")
            .setNotifiedPerson(notifiedPerson)
            .addEvidence(new Coding().setSystem(SNOMED_INFO_SCT).setCode("67782005"))
            .addEvidence(new Coding().setSystem(SNOMED_INFO_SCT).setCode("213257006"))
            .addEvidence(new Coding().setSystem(SNOMED_INFO_SCT).setCode("371820004"))
            .build();
    this.bundle.setDisease(disease);
  }

  private void setCommonInfo(Patient notifiedPerson, Organization hospitalizationServiceProvider) {
    CommonInformationDataBuilder commonInfo = new CommonInformationDataBuilder();
    commonInfo.setDefaults();
    commonInfo.setId("8");
    commonInfo.setNotifiedPerson(notifiedPerson);
    setIsDead(commonInfo);
    setMilitaryAffiliation(commonInfo);
    setLabSpecimenTaken(commonInfo);
    commonInfo.addHospitalization(
        createOngoingHospitalization(notifiedPerson, hospitalizationServiceProvider));
    commonInfo.addHospitalization(
        createFinishedHospitalization(notifiedPerson, hospitalizationServiceProvider));
    setInfectProtectFacility(commonInfo);
    setPlaceExposure(commonInfo);
    commonInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("organDonation")
            .addAnswer(new AnswerDataBuilder().setValueCodingYes().build())
            .build());
    this.bundle.setCommonInformation(commonInfo.build());
  }

  private static void setPlaceExposure(CommonInformationDataBuilder commonInfo) {
    commonInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("placeExposure")
            .addAnswer(new AnswerDataBuilder().setValueCodingYes().build())
            .build());
  }

  private static void setInfectProtectFacility(CommonInformationDataBuilder commonInfo) {
    commonInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("infectProtectFacility")
            .addAnswer(new AnswerDataBuilder().setValueCodingYes().build())
            .build());
  }

  private static void setLabSpecimenTaken(CommonInformationDataBuilder commonInfo) {
    commonInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("labSpecimenTaken")
            .addAnswer(new AnswerDataBuilder().setValueCodingYes().build())
            .build());
  }

  private static void setMilitaryAffiliation(CommonInformationDataBuilder commonInfo) {
    var memberOfBundeswehr =
        new AnswerDataBuilder()
            .setValueCoding(
                new Coding()
                    .setSystem("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation")
                    .setCode("memberOfBundeswehr"))
            .build();
    commonInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("militaryAffiliation")
            .addAnswer(memberOfBundeswehr)
            .build());
  }

  private static void setIsDead(CommonInformationDataBuilder commonInfo) {
    var deathDate =
        new ItemDataBuilder()
            .setLinkId("deathDate")
            .addAnswer(new AnswerDataBuilder().setValueDate(new DateType("2022-01-22")).build())
            .build();
    var isDead =
        new ItemDataBuilder()
            .setLinkId("isDead")
            .addAnswer(new AnswerDataBuilder().setValueCodingYes().addItem(deathDate).build())
            .build();
    commonInfo.addItem(isDead);
  }

  private Encounter createOngoingHospitalization(
      Patient notifiedPerson, Organization serviceProvider) {
    var hospitalization =
        new EncounterDataBuilder()
            .setDefaults()
            .setId("11")
            .setStatus("in-progress")
            .setNotifiedPerson(notifiedPerson)
            .setPeriodStart("2022-01-05")
            .setServiceProvider(serviceProvider)
            .setClassification(
                new Coding(
                    "http://terminology.hl7.org/CodeSystem/v3-ActCode",
                    "IMP",
                    "inpatient encounter"))
            .setHospitalizationNote("Nach Verlegung auf Normalstation diagnostizierten wir Covid.")
            .build();
    this.bundle.addHospitalization(hospitalization);
    return hospitalization;
  }

  private Encounter createFinishedHospitalization(
      Patient notifiedPerson, Organization serviceProvider) {
    var hospitalization =
        new EncounterDataBuilder()
            .setDefaults()
            .setId("12")
            .setStatus("finished")
            .setNotifiedPerson(notifiedPerson)
            .setPeriodStart("2022-01-01")
            .setPeriodEnd("2022-01-05")
            .setServiceProvider(serviceProvider)
            .setClassification(
                new Coding(
                    "http://terminology.hl7.org/CodeSystem/v3-ActCode",
                    "IMP",
                    "inpatient encounter"))
            .setHospitalizationNote("Kritischer Zustand bei Aufnahme")
            .setServiceType(
                new Coding(
                    "https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType",
                    "3600",
                    "Intensivmedizin"))
            .build();
    this.bundle.addHospitalization(hospitalization);
    return hospitalization;
  }

  private void setSpecificInfo(Patient notifiedPerson) {
    SpecificInformationDataBuilder specificInfo = new SpecificInformationDataBuilder();
    specificInfo.setDefaults();
    specificInfo.setId("9");
    specificInfo.setProfileUrlByDisease("cvdd");
    specificInfo.setQuestionnaireUrlByDisease("cvdd");
    specificInfo.setNotifiedPerson(notifiedPerson);
    setInfectionSource(specificInfo);
    setInfectionEnvironmentSetting(specificInfo);
    addImmunization1(notifiedPerson, specificInfo);
    addImmunization2(notifiedPerson, specificInfo);
    addImmunization3(notifiedPerson, specificInfo);
    addImmunization4(notifiedPerson, specificInfo);
    this.bundle.setSpecificInformation(specificInfo.build());
  }

  private void addImmunization1(
      Patient notifiedPerson, SpecificInformationDataBuilder specificInfo) {
    var immunization =
        new ImmunizationDataBuilder()
            .setId("13")
            .setProfileUrl(DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD)
            .setStatus("completed")
            .setVaccineCode(
                new Coding(
                    "https://ec.europa.eu/health/documents/community-register/html/",
                    "EU/1/20/1528",
                    "Comirnaty"))
            .setNotifiedPerson(notifiedPerson)
            .setOccurrence(new DateTimeTypeBuilder().setText("2021-07").build())
            .build();
    this.bundle.addImmunization(immunization);
    specificInfo.addImmunization(immunization);
  }

  private void addImmunization2(
      Patient notifiedPerson, SpecificInformationDataBuilder specificInfo) {
    var immunization =
        new ImmunizationDataBuilder()
            .setId("14")
            .setProfileUrl(DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD)
            .setStatus("completed")
            .setVaccineCode(
                new Coding(
                    "https://ec.europa.eu/health/documents/community-register/html/",
                    "ASKU",
                    "nicht ermittelbar"))
            .setNotifiedPerson(notifiedPerson)
            .setOccurrence(new DateTimeTypeBuilder().setText("2021-11-30").build())
            .build();
    this.bundle.addImmunization(immunization);
    specificInfo.addImmunization(immunization);
  }

  private void addImmunization3(
      Patient notifiedPerson, SpecificInformationDataBuilder specificInfo) {
    var immunization =
        new ImmunizationDataBuilder()
            .setId("15")
            .setProfileUrl(DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD)
            .setStatus("completed")
            .setVaccineCode(
                new Coding(
                    "https://ec.europa.eu/health/documents/community-register/html/",
                    "otherVaccine",
                    "Anderer Impfstoff"))
            .setNotifiedPerson(notifiedPerson)
            .setOccurrence(new DateTimeTypeBuilder().setText("2021-12-25").build())
            .build();
    this.bundle.addImmunization(immunization);
    specificInfo.addImmunization(immunization);
  }

  private void addImmunization4(
      Patient notifiedPerson, SpecificInformationDataBuilder specificInfo) {
    var immunization =
        new ImmunizationDataBuilder()
            .setId("16")
            .setProfileUrl(DemisConstants.PROFILE_IMMUNIZATION_INFORMATION_CVDD)
            .setStatus("completed")
            .setVaccineCode(
                new Coding(
                    "https://ec.europa.eu/health/documents/community-register/html/",
                    "EU/1/20/1528",
                    "Comirnaty"))
            .setNotifiedPerson(notifiedPerson)
            .setOccurrence(new DateTimeTypeBuilder().setText("2021").build())
            .build();
    this.bundle.addImmunization(immunization);
    specificInfo.addImmunization(immunization);
  }

  private static void setInfectionSource(SpecificInformationDataBuilder specificInfo) {
    specificInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("infectionSource")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                            .setCode("NASK"))
                    .build())
            .build());
  }

  private static void setInfectionEnvironmentSetting(SpecificInformationDataBuilder specificInfo) {
    specificInfo.addItem(
        new ItemDataBuilder()
            .setLinkId("infectionEnvironmentSetting")
            .addAnswer(
                new AnswerDataBuilder()
                    .setValueCoding(
                        new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                            .setCode("NASK"))
                    .build())
            .build());
  }
}
