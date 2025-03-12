package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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

import org.apache.commons.lang3.NotImplementedException;

public final class DemisConstants {

  public static final String DEMIS_RKI_DE_FHIR = "https://demis.rki.de/fhir/";
  public static final String DEMIS_RKI_IGS_DE_FHIR = "https://demis.rki.de/fhir/igs/";
  // profiles for laboratory notification (Labormeldung)
  public static final String PROFILE_NOTIFICATION_BUNDLE_LABORATORY =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleLaboratory";
  public static final String PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NON_NOMINAL =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleLaboratoryNonNominal";
  public static final String PROFILE_NOTIFICATION_BUNDLE_LABORATORY_ANONYMOUS =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleLaboratoryAnonymous";
  public static final String PROFILE_NOTIFICATION_BUNDLE_LABORATORY_NEGATIVE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleLaboratoryNegative";
  public static final String PROFILE_NOTIFICATION_BUNDLE_SEQUENCE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleSequence";
  public static final String PROFILE_IGS_NOTIFICATION_BUNDLE_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/NotificationBundleSequence";
  public static final String PROFILE_NOTIFICATION_LABORATORY =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationLaboratory";
  public static final String PROFILE_NOTIFICATION_LABORATORY_NEGATIVE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationNegative";
  public static final String PROFILE_NOTIFICATION_LABORATORY_ANONYMOUS =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationLaboratoryAnonymous";
  public static final String PROFILE_NOTIFICATION_LABORATORY_NON_NOMINAL =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationLaboratoryNonNominal";
  public static final String PROFILE_SUBMITTING_FACILITY =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/SubmittingFacility";
  public static final String PROFILE_SUBMITTING_PERSON =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/SubmittingPerson";
  public static final String PROFILE_SUBMITTING_ROLE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/SubmittingRole";
  public static final String PROFILE_LABORATORY_REPORT_CVDP =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/LaboratoryReportCVDP";
  public static final String PROFILE_PATHOGEN_DETECTION_CVDP =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/PathogenDetectionCVDP";
  public static final String PROFILE_SPECIMEN_CVDP =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/SpecimenCVDP";
  // profiles for disease notification (Arztmeldung)
  public static final String PROFILE_NOTIFICATION_BUNDLE_DISEASE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationBundleDisease";
  public static final String PROFILE_NOTIFICATION_DISEASE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotificationDiseaseCVDD";
  public static final String PROFILE_DISEASE_CVDD =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/DiseaseCVDD";
  public static final String PROFILE_HOSPITALIZATION =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/Hospitalization";
  public static final String PROFILE_IMMUNIZATION_INFORMATION_CVDD =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/ImmunizationInformationCVDD";
  public static final String PROFILE_DISEASE_INFORMATION_COMMON =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/DiseaseInformationCommon";
  public static final String PROFILE_DISEASE_INFORMATION_CVDD =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/DiseaseInformationCVDD";
  // profiles for laboratory and disease notification
  public static final String PROFILE_NOTIFIED_PERSON =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifiedPerson";
  public static final String PROFILE_NOTIFIED_PERSON_NOT_BY_NAME =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifiedPersonNotByName";
  public static final String PROFILE_NOTIFIED_PERSON_ANONYMOUS =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifiedPersonAnonymous";
  public static final String PROFILE_NOTIFIER_FACILITY =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifierFacility";
  public static final String PROFILE_NOTIFIED_PERSON_FACILITY =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifiedPersonFacility";
  public static final String PROFILE_NOTIFIER_ROLE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/NotifierRole";
  public static final String PROFILE_NOTIFIER = DEMIS_RKI_DE_FHIR + "StructureDefinition/Notifier";
  public static final String PROFILE_ORGANIZATION =
      "http://hl7.org/fhir/StructureDefinition/Organization";
  public static final String PROFILE_PROCESS_NOTIFICATION_SEQUENCE_REQUEST_PARAMETERS =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/ProcessNotificationSequenceRequestParameters";
  public static final String PROFILE_NOTIFICATION_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/NotificationSequence";

  public static final String PROFILE_LABORATORY_REPORT_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/LaboratoryReportSequence";
  public static final String PROFILE_SEQUENCING_DEVICE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequencingDevice";
  public static final String PROFILE_SPECIMEN_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SpecimenSequence";
  public static final String PROFILE_PATHOGEN_DETECTION_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/PathogenDetectionSequence";
  public static final String PROFILE_MOLECULAR_SEQUENCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/Sequence";
  public static final String PROFILE_ADAPTER_SUBSTANCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/AdapterSubstance";
  public static final String PROFILE_PRIMER_SUBSTANCE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/PrimerSubstance";
  // questionnaires
  public static final String QUESTIONNAIRE_DISEASE_QUESTIONS_COMMON =
      DEMIS_RKI_DE_FHIR + "Questionnaire/DiseaseQuestionsCommon";
  public static final String QUESTIONNAIRE_DISEASE_QUESTIONS_CVDD =
      DEMIS_RKI_DE_FHIR + "Questionnaire/DiseaseQuestionsCVDD";
  // extensions
  public static final String STRUCTURE_DEFINITION_ADDRESS_USE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/AddressUse";
  public static final String STRUCTURE_DEFINITION_FACILITY_ADDRESS_NOTIFIED_PERSON =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/FacilityAddressNotifiedPerson";
  public static final String STRUCTURE_DEFINITION_ADXP_STREET_NAME =
      "http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-streetName";
  public static final String STRUCTURE_DEFINITION_ADXP_HOUSE_NUMBER =
      "http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber";
  public static final String STRUCTURE_DEFINITION_HOSPITALIZATION_NOTE =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/HospitalizationNote";
  public static final String STRUCTURE_DEFINITION_HOSPITALIZATION_REGION =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/HospitalizationRegion";
  public static final String STRUCTURE_DEFINITION_SEQUENCE_AUTHOR =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequenceAuthor";
  public static final String STRUCTURE_DEFINITION_SEQUENCING_REASON =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequencingReason";
  public static final String STRUCTURE_DEFINITION_SEQUENCE_DOCUMENT =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequenceDocumentReference";
  public static final String STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_DATE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequenceUploadDate";
  public static final String STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_SUBMITTER =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequenceUploadSubmitter";
  public static final String STRUCTURE_DEFINITION_SEQUENCE_UPLOAD_STATUS =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/SequenceUploadStatus";
  public static final String STRUCTURE_DEFINITION_ISOLATE =
      DEMIS_RKI_IGS_DE_FHIR + "StructureDefinition/Isolate";
  public static final String EXTENSION_URL_PSEUDONYM =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/PseudonymRecordType";
  // naming systems
  public static final String NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID =
      DEMIS_RKI_DE_FHIR + "NamingSystem/NotificationBundleId";
  public static final String NAMING_SYSTEM_NOTIFICATION_ID =
      DEMIS_RKI_DE_FHIR + "NamingSystem/NotificationId";
  public static final String NAMING_SYSTEM_BSNR =
      "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";
  public static final String NAMING_SYSTEM_PARTICIPANT_ID =
      "https://demis.rki.de/fhir/NamingSystem/DemisParticipantId";
  public static final String NAMING_SYSTEM_LABORATORY_ID =
      "https://demis.rki.de/fhir/NamingSystem/DemisLaboratoryId";
  // code systems for laboratory notification
  public static final String CODE_SYSTEM_ADDRESS_USE = DEMIS_RKI_DE_FHIR + "CodeSystem/addressUse";
  public static final String CODE_SYSTEM_ORGANIZATION_TYPE =
      DEMIS_RKI_DE_FHIR + "CodeSystem/organizationType";
  public static final String CODE_SYSTEM_NOTIFICATION_CATEGORY =
      DEMIS_RKI_DE_FHIR + "CodeSystem/notificationCategory";
  public static final String CODE_SYSTEM_CONCLUSION_CODE =
      DEMIS_RKI_DE_FHIR + "CodeSystem/conclusionCode";
  public static final String CODE_SYSTEM_SEQUENCING_SUBSTANCES =
      DEMIS_RKI_IGS_DE_FHIR + "CodeSystem/sequencingSubstances";
  // code systems for disease notification
  public static final String CODE_SYSTEM_NOTIFICATION_TYPE =
      DEMIS_RKI_DE_FHIR + "CodeSystem/notificationType";
  public static final String CODE_SYSTEM_SECTION_CODE =
      DEMIS_RKI_DE_FHIR + "CodeSystem/sectionCode";
  public static final String CODE_SYSTEM_NOTIFICATION_DISEASE_CATEGORY =
      DEMIS_RKI_DE_FHIR + "CodeSystem/notificationDiseaseCategory";
  public static final String CODE_SYSTEM_HOSPITALIZATION_SERVICE_TYPE =
      DEMIS_RKI_DE_FHIR + "CodeSystem/hospitalizationServiceType";
  public static final String CODE_SYSTEM_VACCINE = DEMIS_RKI_DE_FHIR + "CodeSystem/vaccine";
  public static final String CODE_SYSTEM_MILITARY_AFFILIATION =
      DEMIS_RKI_DE_FHIR + "CodeSystem/militaryAffiliation";
  public static final String CODE_SYSTEM_YES_OR_NO_ANSWER =
      DEMIS_RKI_DE_FHIR + "CodeSystem/yesOrNoAnswer";
  public static final String CODE_SYSTEM_ORGANIZATION_ASSOCIATION =
      DEMIS_RKI_DE_FHIR + "CodeSystem/organizationAssociation";
  public static final String CODE_SYSTEM_GEOGRAPHIC_REGION =
      DEMIS_RKI_DE_FHIR + "CodeSystem/geographicRegion";
  public static final String CODE_SYSTEM_INFECTION_ENVIRONMENT_SETTING =
      DEMIS_RKI_DE_FHIR + "CodeSystem/infectionEnvironmentSetting";
  public static final String CODE_SYSTEM_SEQUENCING_PLATFORM =
      DEMIS_RKI_IGS_DE_FHIR + "CodeSystem/sequencingPlatform";
  // sequence notification
  public static final String CODE_SYSTEM_SEQUENCING_STRATEGY =
      DEMIS_RKI_IGS_DE_FHIR + "CodeSystem/sequencingStrategy";
  // general code systems
  public static final String CODE_SYSTEM_OBSERVATION_INTERPRETATION =
      "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation";
  public static final String CODE_SYSTEM_OBSERVATION_CATEGORY =
      "http://terminology.hl7.org/CodeSystem/observation-category";
  public static final String CODE_SYSTEM_CONDITION_VERIFICATION_STATUS =
      "http://terminology.hl7.org/CodeSystem/condition-ver-status";
  public static final String CODE_SYSTEM_ACT_CODE =
      "http://terminology.hl7.org/CodeSystem/v3-ActCode";
  public static final String CODE_SYSTEM_NULL_FLAVOR =
      "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";
  public static final String SYSTEM_LOINC = "http://loinc.org";
  public static final String SYSTEM_SNOMED = "http://snomed.info/sct";
  public static final String COMMUNITY_REGISTER =
      "https://ec.europa.eu/health/documents/community-register/html/";

  // Report Profiles
  public static final String PROFILE_REPORT_BUNDLE =
      "https://demis.rki.de/fhir/StructureDefinition/ReportBundle";
  public static final String PROFILE_REPORT_BED_OCCUPANDY =
      "https://demis.rki.de/fhir/StructureDefinition/ReportBedOccupancy";
  public static final String PROFILE_STATISTIC_INFORMATION_BED_OCCUPANCY =
      "https://demis.rki.de/fhir/StructureDefinition/StatisticInformationBedOccupancy";

  public static final String PROFILE_INEK_NAMING_SYSTEM =
      "https://demis.rki.de/fhir/NamingSystem/InekStandortId";

  public static final String NOTIFICATION_ID_SYSTEM =
      "https://demis.rki.de/fhir/NamingSystem/NotificationId";
  public static final String LOINC_ORG_SYSTEM = "http://loinc.org";

  public static final String NOTIFICATION_STANDARD_TYPE_CODE = "34782-3";
  public static final String NOTIFICATION_STANDARD_TYPE_DISPLAY = "Infectious disease Note";
  public static final String NOTIFICATION_STANDARD_TYPE_SYSTEM = LOINC_ORG_SYSTEM;

  public static final String LABORAOTRY_CATEGORY_CODE = "11502-2";
  public static final String LABORATORY_CATEGORY_DISPLAY = "Laboratory report";

  public static final String PATHOGEN_DETECTION_BASE_URL =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/PathogenDetection";

  public static final String LABORATORY_REPORT_BASE_URL =
      DEMIS_RKI_DE_FHIR + "StructureDefinition/LaboratoryReport";
  public static final String SPECIMEN_BASE_URL = DEMIS_RKI_DE_FHIR + "StructureDefinition/Specimen";
  public static final String ADDRESS_USE_URL = DEMIS_RKI_DE_FHIR + "StructureDefinition/AddressUse";

  public static final String CONCLUSION_CODE_SYSTEM =
      "https://demis.rki.de/fhir/CodeSystem/conclusionCode";

  public static final String REASON_FOR_TESTING_SYSTEM =
      "http://hl7.org/fhir/StructureDefinition/workflow-reasonCode";

  public static final String DISEASE_COMPOSITION_TITLE = "Meldung gemäß §6 Absatz 1, 2 IfSG";
  public static final String DISEASE_NOTIFICATION_TYPE_CODE = "6.1_2";
  public static final String DISEASE_NOTIFICATION_TYPE_DISPLAY =
      "Meldung gemäß IfSG §6 Absatz 1, 2";
  public static final String DISEASE_SECTION_SPECIFIC_CODE = "specificClinAndEpiInformation";
  public static final String DISEASE_SECTION_SPECIFIC_DISPLAY =
      "Meldetatbestandsspezifische klinische und epidemiologische Angaben";
  public static final String DISEASE_SECTION_COMMON_CODE = "generalClinAndEpiInformation";
  public static final String DISEASE_SECTION_COMMON_DISPLAY =
      "Meldetatbestandsübergreifende klinische und epidemiologische Angaben";

  public static final String PREFIX_PATIENT = "Patient/";
  public static final String PREFIX_ORGANIZATION = "Organization/";
  public static final String PREFIX_DIAGNOSTIC_REPORT = "DiagnosticReport/";
  public static final String PREFIX_PRACTITIONER_ROLE = "PractitionerRole/";
  public static final String PREFIX_SPECIMEN = "Specimen/";
  public static final String PREFIX_DEVICE = "Device/";
  public static final String PREFIX_MOLECULAR_SEQUENCE = "MolecularSequence/";
  public static final String PREFIX_OBSERVATION = "Observation/";
  public static final String PREFIX_DOCUMENT_REFERENCE = "DocumentReference/";
  public static final String PREFIX_SUBSTANCE = "Substance/";
  public static final String COMPOSITION_BASE = DEMIS_RKI_IGS_DE_FHIR + "Composition/";
  public static final String PRACTITIONER_ROLE_BASE =
      DEMIS_RKI_IGS_DE_FHIR + PREFIX_PRACTITIONER_ROLE;
  public static final String DIAGNOSTIC_REPORT_BASE =
      DEMIS_RKI_IGS_DE_FHIR + PREFIX_DIAGNOSTIC_REPORT;
  public static final String PARTICIPANT_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_PATIENT;
  public static final String ORGANIZATION_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_ORGANIZATION;
  public static final String SUBSTANCE_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_SUBSTANCE;
  public static final String ORGANIZATION_BASE_RELATIVE = PREFIX_ORGANIZATION;
  public static final String DEVICE_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_DEVICE;
  public static final String SPECIMEN_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_SPECIMEN;
  public static final String OBSERVATION_BASE = DEMIS_RKI_IGS_DE_FHIR + PREFIX_OBSERVATION;
  public static final String MOLECULAR_SEQUENCE_BASE =
      DEMIS_RKI_IGS_DE_FHIR + PREFIX_MOLECULAR_SEQUENCE;
  public static final String DOCUMENT_REFERENCE_BASE =
      DEMIS_RKI_IGS_DE_FHIR + PREFIX_DOCUMENT_REFERENCE;

  private DemisConstants() {
    throw new NotImplementedException("you shall not use this constructor");
  }
}
