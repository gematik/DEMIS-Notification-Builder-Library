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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;

/**
 * Builder for a FHIR bundle containing a disease notification. Special feature: <code>
 * createComposition()</code> creates a composition based on the configured data.
 */
@Setter
@Slf4j
public class NotificationBundleDiseaseDataBuilder extends BundleDataBuilder {

  static final String CONDITION_PROFILE_PREFIX =
      "https://demis.rki.de/fhir/StructureDefinition/Disease";

  private final List<Resource> hospitalizations = new ArrayList<>();
  private final List<Resource> immunizations = new ArrayList<>();
  private final List<Resource> encounterOrganizations = new ArrayList<>();
  private final List<Resource> notifiedPersonFacilities = new ArrayList<>();

  private Patient notifiedPerson;
  @Getter private PractitionerRole notifierRole;

  /** The FHIR bundle composition entry will be generated automatically if you don't set it. */
  private Composition notificationDisease;

  private Condition disease;
  @CheckForNull private QuestionnaireResponse commonInformation;
  private QuestionnaireResponse specificInformation;

  public final NotificationBundleDiseaseDataBuilder addImmunization(Immunization immunization) {
    this.immunizations.add(immunization);
    return this;
  }

  @Override
  public NotificationBundleDiseaseDataBuilder setDefaults() {
    super.setDefaults();
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder setImmunizations(
      List<Immunization> immunizations) {
    this.immunizations.clear();
    this.immunizations.addAll(immunizations);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder addHospitalization(Encounter hospitalization) {
    this.hospitalizations.add(hospitalization);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder setHospitalizations(
      List<Encounter> hospitalizations) {
    this.hospitalizations.clear();
    this.hospitalizations.addAll(hospitalizations);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder addEncounterOrganization(
      Organization organization) {
    this.encounterOrganizations.add(organization);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder setEncounterOrganizations(
      List<Organization> encounterOrganizations) {
    this.encounterOrganizations.clear();
    this.encounterOrganizations.addAll(encounterOrganizations);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder addNotifiedPersonFacilities(
      Organization organization) {
    this.notifiedPersonFacilities.add(organization);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder setNotifiedPersonFacilities(
      List<Organization> organizations) {
    this.notifiedPersonFacilities.clear();
    this.notifiedPersonFacilities.addAll(organizations);
    return this;
  }

  @Override
  protected void addEntries() {
    addComposition();
    addNotifiedPerson();
    addEntry(this.disease);
    addEntryOfPractitionerRole(this.notifierRole);
    addQuestionnaireResponses();
  }

  private void addNotifiedPerson() {
    addEntry(this.notifiedPerson);
    addEntries(this.notifiedPersonFacilities);
  }

  @Override
  protected String getDefaultProfileUrl() {
    return PROFILE_NOTIFICATION_BUNDLE_DISEASE;
  }

  /**
   * Create composition based on configured data.
   *
   * @return composition builder
   */
  public NotificationDiseaseDataBuilder createComposition() {
    log.debug("Disease notification bundle builder generates composition.");
    NotificationDiseaseDataBuilder compositionBuilder = new NotificationDiseaseDataBuilder();
    createCompositionHelper(compositionBuilder);
    return compositionBuilder;
  }

  void createCompositionHelper(NotificationDiseaseDataBuilder compositionBuilder) {
    compositionBuilder.setDefaults();
    compositionBuilder.setProfileUrlByDisease(getCategory());
    DateTimeType date = new DateTimeType();
    date.setValue(Utils.getCurrentDate(), InstantType.DEFAULT_PRECISION);
    compositionBuilder.setDate(date);
    compositionBuilder.setNotifiedPerson(this.notifiedPerson);
    compositionBuilder.setNotifierRole(this.notifierRole);
    compositionBuilder.setDisease(this.disease);
    compositionBuilder.setCommonQuestionnaireResponse(this.commonInformation);
    if (this.specificInformation != null) {
      compositionBuilder.setSpecificQuestionnaireResponse(this.specificInformation);
    }
  }

  String getCategory() {
    if (this.disease.hasMeta()) {
      Meta meta = this.disease.getMeta();
      if (meta.hasProfile()) {
        final Optional<String> category =
            meta.getProfile().stream()
                .map(CanonicalType::getValue)
                .filter(p -> Strings.CS.startsWith(p, CONDITION_PROFILE_PREFIX))
                .map(p -> p.substring(p.length() - 4))
                .findFirst();
        if (category.isPresent()) {
          return category.get();
        }
      }
    }
    throw new IllegalArgumentException("disease FHIR condition resource has no profile URL");
  }

  private void addComposition() {
    if (this.notificationDisease != null) {
      addEntry(this.notificationDisease);
    }
  }

  private void addQuestionnaireResponses() {
    addCommonInformationQuestionnaireResponse();
    addInformationQuestionnaireResponse();
  }

  private void addInformationQuestionnaireResponse() {
    if (this.specificInformation != null) {
      addImmunizations();
      addEntry(this.specificInformation);
    }
  }

  private void addCommonInformationQuestionnaireResponse() {
    if (this.commonInformation != null) {
      addEncounters();
      addEntry(this.commonInformation);
    }
  }

  private void addImmunizations() {
    addEntries(this.immunizations);
  }

  private void addEncounters() {
    addEntries(this.encounterOrganizations);
    addEntries(this.hospitalizations);
  }
}
