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

package de.gematik.demis.notification.builder.demis.fhir.examples;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.*;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.HumanNameDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.TelecomDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.PractitionerType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;

class LaboratoryNotificationExamplesTest {

  FhirContext fhirContext = FhirContext.forR4();
  FhirParser fhirParser = new FhirParser(fhirContext);

  @Test
  void shouldCreateACustomLaboratoyINVPNotification() {

    /*
     * ********************Data input that has to be mapped*********************
     */

    // notified person data
    String familyName = "Völler";
    String givenName = "Rudi";
    String prefix = "Dr. futbol";
    Enumerations.AdministrativeGender gender = Enumerations.AdministrativeGender.MALE;
    Enumerations.AdministrativeGender genderAlternative =
        Enumerations.AdministrativeGender.fromCode("male");

    String telephoneNumber = "+49 123456789";
    ContactPoint.ContactPointSystem phone = ContactPoint.ContactPointSystem.PHONE;
    ContactPoint.ContactPointSystem phoneAlternative =
        ContactPoint.ContactPointSystem.fromCode("phone");
    ContactPoint.ContactPointUse phoneUse = ContactPoint.ContactPointUse.HOME;
    ContactPoint.ContactPointUse phoneUseAlternative =
        ContactPoint.ContactPointUse.fromCode("home");

    String emailAddress = "rudi.voeller@email.org";
    ContactPoint.ContactPointSystem email = ContactPoint.ContactPointSystem.EMAIL;
    ContactPoint.ContactPointSystem emailAlternative =
        ContactPoint.ContactPointSystem.fromCode("email");
    ContactPoint.ContactPointUse emailUse = ContactPoint.ContactPointUse.HOME;
    ContactPoint.ContactPointUse emailUseAlternative =
        ContactPoint.ContactPointUse.fromCode("home");

    Date birthdate =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    String street = "Hauptstraße";
    String country = "20422";
    String postalCode = "21481";
    String city = "Berlin";
    String houseNumber = "1";
    String notifiedPersonAddressUseCode = "current";
    String notifiedPersonAddressUseSystem = "https://demis.rki.de/fhir/CodeSystem/addressUse";
    String notifiedPersonAddressUseDisplay = "Derzeitiger Aufenthaltsort";
    String notifiedPersonAddressExtensionUrl =
        "https://demis.rki.de/fhir/StructureDefinition/AddressUse";

    // notifier
    PractitionerType notifierOrgType = PractitionerType.ORGANIZATION;

    String notifierIdentifiertExampleSystem1 =
        "https://demis.rki.de/fhir/NamingSystem/DemisLaboratoryId";
    String notifierIdentifierExampleValue1 = "13589";
    String notifierIdentifiertExampleSystem2 = "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";
    String notifierIdentifierExampleValue2 = "98765430";

    String notifierTypeCode = "laboratory";
    String notifierTypeDisplay = "Erregerdiagnostische Untersuchungsstelle";
    String notifierTypeSystem = "https://demis.rki.de/fhir/CodeSystem/organizationType";

    String notiferPhoneValue = "+49 987654321";
    ContactPoint.ContactPointSystem notifierPhoneSystem =
        ContactPoint.ContactPointSystem.fromCode("phone");
    ContactPoint.ContactPointUse notifierPhoneUse = ContactPoint.ContactPointUse.fromCode("work");

    String notifierHouseNumber = "2";
    String notifierStreet = "Laborstraße";
    String notifierCity = "München";
    String notifierPostalCode = "80331";
    String notifierCountry = "20422";

    String notifierName = "Primärlabor der gematik GmbH";

    // submitter

    String submitterHouseNumber = "2";
    String submitterStreet = "Laborstraße";
    String submitterCity = "München";
    String submitterPostalCode = "80331";
    String submitterCountry = "20422";

    String identifierBSNR = "135896780";
    String identifierBSNRSystem = "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";
    String submitterName = "Einsender Praxis";
    String submitterEmailValue = "some.email@address.de";
    ContactPoint.ContactPointUse submitterEmailUse = ContactPoint.ContactPointUse.fromCode("work");
    ContactPoint.ContactPointSystem submitterEmailSystem =
        ContactPoint.ContactPointSystem.fromCode("email");

    // specimen
    Specimen.SpecimenStatus specimenStatus = Specimen.SpecimenStatus.fromCode("available");
    String specimenTypeCode = "309164002";
    String specimenTypeSystem = "http://snomed.info/sct";
    String specimentTypeDisplay = "Upper respiratory swab sample (specimen)";
    Date receivedTime = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    String specimenProfileUrl = "https://demis.rki.de/fhir/StructureDefinition/SpecimenINVP";

    // pathogen detection
    Observation.ObservationStatus pathogenDetectionStatus =
        Observation.ObservationStatus.fromCode("final");
    String categoryCode = "laboratory";
    String categorySystem = "http://terminology.hl7.org/CodeSystem/observation-category";
    String pathogenDetectionValueCode = "99623-1";
    String pathogenDetectionValueDisplay =
        "Influenza virus A N1 RNA [Presence] in Specimen by NAA with probe detection";
    String pathogenDetectionValueSystem = "http://loinc.org";
    Type value = new StringType("500");
    Type valueAlternative = new CodeableConcept(new Coding("system", "code", "display"));
    Type valueAlternative2 = new Quantity(500L);
    String interpretionCode = "POS";
    String interpretationSystem =
        "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation";
    String methodCode = "121276004";
    String methodSystem = "http://snomed.info/sct";
    String methodDisplay = "Antigen assay (procedure)";
    String pathogenDetectionProfileUrl =
        "https://demis.rki.de/fhir/StructureDefinition/PathogenDetectionINVP";
    // diagnostic report
    DiagnosticReport.DiagnosticReportStatus diagnosticReportStatus =
        DiagnosticReport.DiagnosticReportStatus.fromCode("final");
    String laboratoryCode = "invp";
    String laboratoryCodeSystem = "https://demis.rki.de/fhir/CodeSystem/notificationCategory";
    String laboratoryCodeDisplay = "Influenzavirus; Meldepflicht nur für den direkten Nachweis";
    Date now = Date.from(Instant.now());
    String laboratoryReportProfileUrl =
        "https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportINVP";

    // composition
    Composition.CompositionStatus compositionStatus =
        Composition.CompositionStatus.fromCode("final");
    String notificationLaboratoryTypeCode = "34782-3";
    String notificationLaboratoryTypeDisplay = "Infectious disease Note";
    String notificationLaboratoryTypeSystem = "http://loinc.org";
    String notificationLaboratoryCategorySystem = "http://loinc.org";
    String notificationLaboratoryCategoryCode = "11502-2";
    String notificationLaboratoryCategoryDisplay = "Laboratory report";
    String notificationLaboratorySectionCompomentySystem = "http://loinc.org";
    String notificationLaboratorySectionCompomentyCode = "11502-2";
    String notificationLaboratorySectionCompomentyDisplay = "Laboratory report";

    /*-------------------------------------------------------------------------------------
     * now starts the mapping
     */

    // NotifiedPerson
    NotifiedPersonDataBuilder notifiedPersonDataBuilder = new NotifiedPersonDataBuilder();
    // name
    HumanNameDataBuilder humanNameDataBuilder = new HumanNameDataBuilder();
    humanNameDataBuilder.setFamilyName(familyName);
    humanNameDataBuilder.addGivenName(givenName);
    humanNameDataBuilder.addPrefix(prefix);
    HumanName humanName = humanNameDataBuilder.buildHumanName();
    notifiedPersonDataBuilder.setHumanName(humanName);
    // gender
    notifiedPersonDataBuilder.setGender(gender);
    // telecom
    TelecomDataBuilder telefonNumberExampleBuilder = new TelecomDataBuilder();
    telefonNumberExampleBuilder.setValue(telephoneNumber);
    telefonNumberExampleBuilder.setSystem(phone);
    telefonNumberExampleBuilder.setUse(phoneUse);
    ContactPoint telephoneNumberCP = telefonNumberExampleBuilder.buildContactPoint();
    notifiedPersonDataBuilder.addTelecom(telephoneNumberCP);

    TelecomDataBuilder emailAddressExampleBuilder = new TelecomDataBuilder();
    emailAddressExampleBuilder.setValue(emailAddress);
    emailAddressExampleBuilder.setSystem(email);
    emailAddressExampleBuilder.setUse(emailUse);
    ContactPoint emailCP = emailAddressExampleBuilder.buildContactPoint();
    notifiedPersonDataBuilder.addTelecom(emailCP);

    notifiedPersonDataBuilder.setTelecom(
        asList(telephoneNumberCP, emailCP)); // alternative version of adding telecom data

    // birthdate (opt)
    notifiedPersonDataBuilder.setBirthdate(birthdate);

    // address
    AddressDataBuilder addressDataBuilder = new AddressDataBuilder();
    addressDataBuilder.setStreet(street);
    addressDataBuilder.setCountry(country);
    addressDataBuilder.setPostalCode(postalCode);
    addressDataBuilder.setCity(city);
    addressDataBuilder.setHouseNumber(houseNumber);
    Address address =
        addressDataBuilder
            .withAddressUseExtension(notifiedPersonAddressUseCode, notifiedPersonAddressUseDisplay)
            .build();
    notifiedPersonDataBuilder.addAddress(address);

    Patient notifiedPerson = notifiedPersonDataBuilder.buildNotifiedPerson();

    // Notifier
    NotifierDataBuilder notifierDataBuilder = new NotifierDataBuilder();
    notifierDataBuilder.setNotifierType(notifierOrgType);

    notifierDataBuilder.addIdentifier(
        new Identifier()
            .setSystem(notifierIdentifiertExampleSystem1)
            .setValue(notifierIdentifierExampleValue1));
    notifierDataBuilder.addIdentifier(
        new Identifier()
            .setValue(notifierIdentifierExampleValue2)
            .setSystem(notifierIdentifiertExampleSystem2));
    notifierDataBuilder.setNotifierFacilityTypeCode(notifierTypeCode);
    notifierDataBuilder.setNotifierFacilityTypeDisplay(notifierTypeDisplay);
    notifierDataBuilder.setNotifierFacilityTypeSystem(notifierTypeSystem);

    notifierDataBuilder.setNotifierFacilityName(notifierName);

    TelecomDataBuilder telecomDataBuilder = new TelecomDataBuilder();
    telecomDataBuilder.setValue(notiferPhoneValue);
    telecomDataBuilder.setSystem(notifierPhoneSystem);
    telecomDataBuilder.setUse(notifierPhoneUse);

    notifierDataBuilder.addNotifierId();

    ContactPoint phoneCP = telecomDataBuilder.buildContactPoint();
    notifierDataBuilder.addNotifierTelecom(phoneCP);

    AddressDataBuilder notifierAddressData = new AddressDataBuilder();
    notifierAddressData.setHouseNumber(notifierHouseNumber);
    notifierAddressData.setStreet(notifierStreet);
    notifierAddressData.setCity(notifierCity);
    notifierAddressData.setPostalCode(notifierPostalCode);
    notifierAddressData.setCountry(notifierCountry);
    Address notifierAddress = notifierAddressData.build();
    notifierDataBuilder.setNotifierAddress(notifierAddress);

    PractitionerRole notifierRole = notifierDataBuilder.buildNotifierData();

    // Submitter
    SubmitterDataBuilder submitterDataBuilder = new SubmitterDataBuilder();
    submitterDataBuilder.setPractitionerType(PractitionerType.ORGANIZATION);
    submitterDataBuilder.addIdentifier(
        new Identifier().setValue(identifierBSNR).setSystem(identifierBSNRSystem));
    submitterDataBuilder.setSubmittingFacilityName(submitterName);
    submitterDataBuilder.addSubmitterTelecom(
        new TelecomDataBuilder()
            .setValue(submitterEmailValue)
            .setUse(submitterEmailUse)
            .setSystem(submitterEmailSystem)
            .buildContactPoint());
    AddressDataBuilder submitterAddressBuilder = new AddressDataBuilder();
    submitterAddressBuilder.setHouseNumber(submitterHouseNumber);
    submitterAddressBuilder.setStreet(submitterStreet);
    submitterAddressBuilder.setCity(submitterCity);
    submitterAddressBuilder.setPostalCode(submitterPostalCode);
    submitterAddressBuilder.setCountry(submitterCountry);

    Address submitterAddress = submitterAddressBuilder.build();
    submitterDataBuilder.setSubmitterAddress(submitterAddress);

    submitterDataBuilder.addSubmitterId();

    PractitionerRole submitterRole = submitterDataBuilder.buildSubmitterData();

    // Specimen
    SpecimenDataBuilder specimenDataBuilder = new SpecimenDataBuilder();
    specimenDataBuilder.setSpecimenStatus(specimenStatus);
    specimenDataBuilder.setTypeCode(specimenTypeCode);
    specimenDataBuilder.setTypeSystem(specimenTypeSystem);
    specimenDataBuilder.setTypeDisplay(specimentTypeDisplay);
    specimenDataBuilder.setReceivedTime(receivedTime);

    specimenDataBuilder.setMetaProfileUrl(specimenProfileUrl);
    specimenDataBuilder.addSpecimenId();

    Specimen specimen = specimenDataBuilder.buildSpecimen(notifiedPerson, submitterRole);

    // PathogenDetection
    PathogenDetectionDataBuilder pathogenDetectionDataBuilder = new PathogenDetectionDataBuilder();
    pathogenDetectionDataBuilder.setStatus(pathogenDetectionStatus);
    pathogenDetectionDataBuilder.setCategoryCode(categoryCode);
    pathogenDetectionDataBuilder.setCategorySystem(categorySystem);
    pathogenDetectionDataBuilder.setObservationCodeCode(pathogenDetectionValueCode);
    pathogenDetectionDataBuilder.setObservationCodeDisplay(pathogenDetectionValueDisplay);
    pathogenDetectionDataBuilder.setObservationCodeSystem(pathogenDetectionValueSystem);
    pathogenDetectionDataBuilder.setValue(value);
    pathogenDetectionDataBuilder.setInterpretationCode(interpretionCode);
    pathogenDetectionDataBuilder.setInterpretationSystem(interpretationSystem);
    pathogenDetectionDataBuilder.setMethodCode(methodCode);
    pathogenDetectionDataBuilder.setMethodSystem(methodSystem);
    pathogenDetectionDataBuilder.setMethodDisplay(methodDisplay);

    pathogenDetectionDataBuilder.setProfileUrl(pathogenDetectionProfileUrl);
    pathogenDetectionDataBuilder.addId();

    Observation pathogenDetection =
        pathogenDetectionDataBuilder.buildPathogenDetection(notifiedPerson, specimen);

    // Laboratory Report

    LaboratoryReportDataBuilder laboratoryReportDataBuilder = new LaboratoryReportDataBuilder();

    laboratoryReportDataBuilder.setStatus(diagnosticReportStatus);
    laboratoryReportDataBuilder.setCodeCode(laboratoryCode);
    laboratoryReportDataBuilder.setCodeSystem(laboratoryCodeSystem);
    laboratoryReportDataBuilder.setCodeDisplay(laboratoryCodeDisplay);
    laboratoryReportDataBuilder.setIssued(now);
    laboratoryReportDataBuilder.setConclusionCodeToNachgewiesen();
    laboratoryReportDataBuilder.addLaboratoryId();

    laboratoryReportDataBuilder.setMetaProfileUrl(laboratoryReportProfileUrl);

    DiagnosticReport laboratoryReport =
        laboratoryReportDataBuilder.buildLaboratoryReport(notifiedPerson, pathogenDetection);

    // NotificationLaboratory

    NotificationLaboratoryDataBuilder notificationLaboratoryDataBuilder =
        new NotificationLaboratoryDataBuilder();
    notificationLaboratoryDataBuilder.setCompositionStatus(compositionStatus);
    notificationLaboratoryDataBuilder.setTypeCode(notificationLaboratoryTypeCode);
    notificationLaboratoryDataBuilder.setTypeDisplay(notificationLaboratoryTypeDisplay);
    notificationLaboratoryDataBuilder.setTypeSystem(notificationLaboratoryTypeSystem);

    notificationLaboratoryDataBuilder.setCodeAndCategoryCode(notificationLaboratoryCategoryCode);
    notificationLaboratoryDataBuilder.setCodeAndCategoryDisplay(
        notificationLaboratoryCategoryDisplay);
    notificationLaboratoryDataBuilder.setCodeAndCategorySystem(
        notificationLaboratoryCategorySystem);

    notificationLaboratoryDataBuilder.setDate(now);
    notificationLaboratoryDataBuilder.setTitle("Erregernachweismeldung");

    notificationLaboratoryDataBuilder.setSectionComponentSystem(
        notificationLaboratorySectionCompomentySystem);
    notificationLaboratoryDataBuilder.setSectionComponentCode(
        notificationLaboratorySectionCompomentyCode);
    notificationLaboratoryDataBuilder.setSectionComponentDisplay(
        notificationLaboratorySectionCompomentyDisplay);

    notificationLaboratoryDataBuilder.addIdentifier();
    notificationLaboratoryDataBuilder.addNotificationLaboratoryId();

    Composition notificationLaboratory =
        notificationLaboratoryDataBuilder.buildNotificationLaboratory(
            notifiedPerson, notifierRole, laboratoryReport);

    // NotifcationBundleLaboratory
    NotificationBundleLaboratoryDataBuilder notificationBundleLaboratoryDataBuilder =
        new NotificationBundleLaboratoryDataBuilder()
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifierRole)
            .setSubmitterRole(submitterRole)
            .setLaboratoryReport(laboratoryReport)
            .setPathogenDetection(Collections.singletonList(pathogenDetection))
            .setSpecimen(specimen)
            .setNotificationLaboratory(notificationLaboratory);

    Bundle bundle = notificationBundleLaboratoryDataBuilder.buildLaboratoryBundle();

    String laboratoryNotificationAsString = fhirParser.encodeToJson(bundle);
    assertThat(bundle).isNotNull();
    assertThat(laboratoryNotificationAsString).isNotEmpty().contains("{");
  }

  @Test
  void shouldCreateACustomLaboratoyCVDPNotification() {

    /*
     * ********************Data input that has to be mapped*********************
     */

    // notified person data
    String familyName = "Völler";
    String givenName = "Rudi";
    String prefix = "Dr. futbol";
    Enumerations.AdministrativeGender gender = Enumerations.AdministrativeGender.MALE;

    String telephoneNumber = "+49 123456789";
    ContactPoint.ContactPointSystem phone = ContactPoint.ContactPointSystem.PHONE;
    ContactPoint.ContactPointSystem phoneAlternative =
        ContactPoint.ContactPointSystem.fromCode("phone");
    ContactPoint.ContactPointUse phoneUse = ContactPoint.ContactPointUse.HOME;

    String emailAddress = "rudi.voeller@email.org";
    ContactPoint.ContactPointSystem email = ContactPoint.ContactPointSystem.EMAIL;
    ContactPoint.ContactPointSystem emailAlternative =
        ContactPoint.ContactPointSystem.fromCode("email");
    ContactPoint.ContactPointUse emailUse = ContactPoint.ContactPointUse.HOME;

    Date birthdate =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    String street = "Hauptstraße";
    String country = "20422";
    String postalCode = "21481";
    String city = "Berlin";
    String houseNumber = "1";
    String notifiedPersonAddressUseCode = "current";
    String notifiedPersonAddressUseDisplay = "Derzeitiger Aufenthaltsort";

    // notifier
    PractitionerType notifierOrgType = PractitionerType.ORGANIZATION;

    String notifierIdentifiertExampleSystem1 =
        "https://demis.rki.de/fhir/NamingSystem/DemisLaboratoryId";
    String notifierIdentifierExampleValue1 = "13589";
    String notifierIdentifiertExampleSystem2 = "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";
    String notifierIdentifierExampleValue2 = "98765430";

    String notifierTypeCode = "laboratory";
    String notifierTypeDisplay = "Erregerdiagnostische Untersuchungsstelle";
    String notifierTypeSystem = "https://demis.rki.de/fhir/CodeSystem/organizationType";

    String notiferPhoneValue = "+49 987654321";
    ContactPoint.ContactPointSystem notifierPhoneSystem =
        ContactPoint.ContactPointSystem.fromCode("phone");
    ContactPoint.ContactPointUse notifierPhoneUse = ContactPoint.ContactPointUse.fromCode("work");

    String notifierHouseNumber = "2";
    String notifierStreet = "Laborstraße";
    String notifierCity = "München";
    String notifierPostalCode = "80331";
    String notifierCountry = "20422";

    String notifierName = "Primärlabor der gematik GmbH";

    // submitter

    String submitterHouseNumber = "2";
    String submitterStreet = "Laborstraße";
    String submitterCity = "München";
    String submitterPostalCode = "80331";
    String submitterCountry = "20422";

    String identifierBSNR = "135896780";
    String identifierBSNRSystem = "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";
    String submitterName = "Einsender Praxis";
    String submitterEmailValue = "some.email@address.de";
    ContactPoint.ContactPointUse submitterEmailUse = ContactPoint.ContactPointUse.fromCode("work");
    ContactPoint.ContactPointSystem submitterEmailSystem =
        ContactPoint.ContactPointSystem.fromCode("email");
    String submitterTypeCode = "hospital";
    String submitterTypeDisplay = "Krankenhaus";

    // specimen
    Specimen.SpecimenStatus specimenStatus = Specimen.SpecimenStatus.fromCode("available");
    String specimenTypeCode = "309164002";
    String specimentTypeDisplay = "Upper respiratory swab sample (specimen)";
    Date receivedTime = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    String specimenProfileUrl = "https://demis.rki.de/fhir/StructureDefinition/SpecimenCVDP";

    // pathogen detection
    Observation.ObservationStatus pathogenDetectionStatus =
        Observation.ObservationStatus.fromCode("final");
    String pathogenDetectionValueCode = "94660-8";
    String pathogenDetectionValueDisplay =
        "SARS-CoV-2 (COVID-19)-RNA [Nachweis] in Serum oder Plasma mit Nukleinsäureamplifikation mit Sondendetektion";
    Type value = new StringType("500");
    String interpretionCode = "POS";
    String methodCode = "121276004";
    String methodDisplay = "Antigen assay (procedure)";
    String pathogenDetectionProfileUrl =
        "https://demis.rki.de/fhir/StructureDefinition/PathogenDetectionCVDP";
    // diagnostic report
    DiagnosticReport.DiagnosticReportStatus diagnosticReportStatus =
        DiagnosticReport.DiagnosticReportStatus.fromCode("final");
    String laboratoryCode = "cvdp";
    String laboratoryCodeDisplay = "SARS-CoV-2";
    Date now = Date.from(Instant.now());
    String laboratoryReportProfileUrl =
        "https://demis.rki.de/fhir/StructureDefinition/LaboratoryReportCVDP";

    // composition
    Composition.CompositionStatus compositionStatus =
        Composition.CompositionStatus.fromCode("final");
    String notificationLaboratorySectionCompomentySystem = "http://loinc.org";
    String notificationLaboratorySectionCompomentyCode = "11502-2";
    String notificationLaboratorySectionCompomentyDisplay = "Laboratory report";

    /*-------------------------------------------------------------------------------------
     * now starts the mapping
     */

    // NotifiedPerson
    NotifiedPersonDataBuilder notifiedPersonDataBuilder = new NotifiedPersonDataBuilder();
    // name
    HumanNameDataBuilder humanNameDataBuilder = new HumanNameDataBuilder();
    humanNameDataBuilder.setFamilyName(familyName);
    humanNameDataBuilder.addGivenName(givenName);
    humanNameDataBuilder.addPrefix(prefix);
    HumanName humanName = humanNameDataBuilder.buildHumanName();
    notifiedPersonDataBuilder.setHumanName(humanName);
    // gender
    notifiedPersonDataBuilder.setGender(gender);
    // telecom
    TelecomDataBuilder telefonNumberExampleBuilder = new TelecomDataBuilder();
    telefonNumberExampleBuilder.setValue(telephoneNumber);
    telefonNumberExampleBuilder.setSystem(phone);
    telefonNumberExampleBuilder.setUse(phoneUse);
    ContactPoint telephoneNumberCP = telefonNumberExampleBuilder.buildContactPoint();
    notifiedPersonDataBuilder.addTelecom(telephoneNumberCP);

    TelecomDataBuilder emailAddressExampleBuilder = new TelecomDataBuilder();
    emailAddressExampleBuilder.setValue(emailAddress);
    emailAddressExampleBuilder.setSystem(email);
    emailAddressExampleBuilder.setUse(emailUse);
    ContactPoint emailCP = emailAddressExampleBuilder.buildContactPoint();
    notifiedPersonDataBuilder.addTelecom(emailCP);

    notifiedPersonDataBuilder.setTelecom(
        asList(telephoneNumberCP, emailCP)); // alternative version of adding telecom data

    // birthdate (opt)
    notifiedPersonDataBuilder.setBirthdate(birthdate);

    // address
    AddressDataBuilder addressDataBuilder = new AddressDataBuilder();
    addressDataBuilder.setStreet(street);
    addressDataBuilder.setCountry(country);
    addressDataBuilder.setPostalCode(postalCode);
    addressDataBuilder.setCity(city);
    addressDataBuilder.setHouseNumber(houseNumber);
    Address address =
        addressDataBuilder
            .withAddressUseExtension(notifiedPersonAddressUseCode, notifiedPersonAddressUseDisplay)
            .build();
    notifiedPersonDataBuilder.addAddress(address);

    Patient notifiedPerson = notifiedPersonDataBuilder.buildNotifiedPerson();

    // Notifier
    NotifierDataBuilder notifierDataBuilder = new NotifierDataBuilder();
    notifierDataBuilder.setNotifierType(notifierOrgType);

    notifierDataBuilder.addIdentifier(
        new Identifier()
            .setSystem(notifierIdentifiertExampleSystem1)
            .setValue(notifierIdentifierExampleValue1));
    notifierDataBuilder.addIdentifier(
        new Identifier()
            .setValue(notifierIdentifierExampleValue2)
            .setSystem(notifierIdentifiertExampleSystem2));
    notifierDataBuilder.setNotifierFacilityTypeCode(notifierTypeCode);
    notifierDataBuilder.setNotifierFacilityTypeDisplay(notifierTypeDisplay);
    notifierDataBuilder.setNotifierFacilityTypeSystem(notifierTypeSystem);

    notifierDataBuilder.setNotifierFacilityName(notifierName);

    TelecomDataBuilder telecomDataBuilder = new TelecomDataBuilder();
    telecomDataBuilder.setValue(notiferPhoneValue);
    telecomDataBuilder.setSystem(notifierPhoneSystem);
    telecomDataBuilder.setUse(notifierPhoneUse);

    notifierDataBuilder.addNotifierId();

    ContactPoint phoneCP = telecomDataBuilder.buildContactPoint();
    notifierDataBuilder.addNotifierTelecom(phoneCP);

    AddressDataBuilder notifierAddressData = new AddressDataBuilder();
    notifierAddressData.setHouseNumber(notifierHouseNumber);
    notifierAddressData.setStreet(notifierStreet);
    notifierAddressData.setCity(notifierCity);
    notifierAddressData.setPostalCode(notifierPostalCode);
    notifierAddressData.setCountry(notifierCountry);
    Address notifierAddress = notifierAddressData.build();
    notifierDataBuilder.setNotifierAddress(notifierAddress);

    PractitionerRole notifierRole = notifierDataBuilder.buildNotifierData();

    // Submitter
    Identifier bsnrIdentifier =
        new Identifier().setValue(identifierBSNR).setSystem(identifierBSNRSystem);
    ContactPoint submitterTelecom =
        new TelecomDataBuilder()
            .setValue(submitterEmailValue)
            .setUse(submitterEmailUse)
            .setSystem(submitterEmailSystem)
            .buildContactPoint();

    Address submitterAddress =
        new AddressDataBuilder()
            .setHouseNumber(submitterHouseNumber)
            .setStreet(submitterStreet)
            .setCity(submitterCity)
            .setPostalCode(submitterPostalCode)
            .setCountry(submitterCountry)
            .build();

    Organization submitterFacility =
        new OrganizationBuilder()
            .asSubmittingFacility()
            .setDefaultData()
            .setFacilityName(submitterName)
            .setTelecomList(Collections.singletonList(submitterTelecom))
            .setAddress(submitterAddress)
            .addIdentifier(bsnrIdentifier)
            .setTypeCode(submitterTypeCode)
            .setTypeDisplay(submitterTypeDisplay)
            .build();

    PractitionerRole submitterRole =
        new PractitionerRoleBuilder()
            .asSubmittingRole()
            .withOrganization(submitterFacility)
            .build();

    // Specimen
    SpecimenDataBuilder specimenDataBuilder =
        new SpecimenDataBuilder()
            .setDefaultData()
            .setSpecimenStatus(specimenStatus)
            .setTypeCode(specimenTypeCode)
            .setTypeDisplay(specimentTypeDisplay)
            .setReceivedTime(receivedTime)
            .setNotifiedPerson(notifiedPerson)
            .setSubmittingRole(submitterRole)
            .setMetaProfileUrl(specimenProfileUrl);

    Specimen specimen = specimenDataBuilder.build();

    // PathogenDetection
    PathogenDetectionDataBuilder pathogenDetectionDataBuilder =
        new PathogenDetectionDataBuilder()
            .setDefaultData()
            .setStatus(pathogenDetectionStatus)
            .setObservationCodeCode(pathogenDetectionValueCode)
            .setObservationCodeDisplay(pathogenDetectionValueDisplay)
            .setValue(value)
            .setInterpretationCode(interpretionCode)
            .setMethodCode(methodCode)
            .setMethodDisplay(methodDisplay)
            .setNotifiedPerson(notifiedPerson)
            .setSpecimen(specimen)
            .setProfileUrl(pathogenDetectionProfileUrl);

    Observation pathogenDetection = pathogenDetectionDataBuilder.build();

    // Laboratory Report
    LaboratoryReportDataBuilder laboratoryReportDataBuilder =
        new LaboratoryReportDataBuilder()
            .setDefaultData()
            .setStatus(diagnosticReportStatus)
            .setCodeCode(laboratoryCode)
            .setCodeDisplay(laboratoryCodeDisplay)
            .setIssued(now)
            .setConclusionCodeStatusToDetected()
            .setNotifiedPerson(notifiedPerson)
            .setPathogenDetections(Collections.singletonList(pathogenDetection))
            .setMetaProfileUrl(laboratoryReportProfileUrl);

    DiagnosticReport laboratoryReport = laboratoryReportDataBuilder.build();

    // NotificationLaboratory

    NotificationLaboratoryDataBuilder notificationLaboratoryDataBuilder =
        new NotificationLaboratoryDataBuilder()
            .setDefault()
            .setCompositionStatus(compositionStatus)
            .setTitle("Erregernachweismeldung")
            .setSectionComponentSystem(notificationLaboratorySectionCompomentySystem)
            .setSectionComponentCode(notificationLaboratorySectionCompomentyCode)
            .setSectionComponentDisplay(notificationLaboratorySectionCompomentyDisplay)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifierRole)
            .setLaboratoryReport(laboratoryReport);

    Composition notificationLaboratory = notificationLaboratoryDataBuilder.build();

    // NotifcationBundleLaboratory
    NotificationBundleLaboratoryDataBuilder notificationBundleLaboratoryDataBuilder =
        new NotificationBundleLaboratoryDataBuilder()
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifierRole)
            .setSubmitterRole(submitterRole)
            .setLaboratoryReport(laboratoryReport)
            .setPathogenDetection(Collections.singletonList(pathogenDetection))
            .setSpecimen(specimen)
            .setNotificationLaboratory(notificationLaboratory);

    Bundle bundle = notificationBundleLaboratoryDataBuilder.buildLaboratoryBundle();

    String laboratoryNotificationAsString = fhirParser.encodeToJson(bundle);
    assertThat(bundle).isNotNull();
    assertThat(laboratoryNotificationAsString).isNotEmpty().contains("{");
  }
}
