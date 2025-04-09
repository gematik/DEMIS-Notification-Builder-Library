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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
  private final List<Resource> organizations = new ArrayList<>();

  private Patient notifiedPerson;
  @Getter private PractitionerRole notifierRole;

  /** The FHIR bundle composition entry will be generated automatically if you don't set it. */
  private Composition notificationDisease;

  private Condition disease;
  private QuestionnaireResponse commonInformation;
  private QuestionnaireResponse specificInformation;

  private static String validSuffix(String disease) {
    if ((StringUtils.length(disease) != 4) || !StringUtils.isAlpha(disease)) {
      throw new IllegalArgumentException("Not a four letter disease identifier: " + disease);
    }
    return disease.toUpperCase();
  }

  /**
   * Create disease specific URL.
   *
   * @param url URL
   * @param disease disease category code like: <code>cvdd</code>
   * @return URL
   */
  public static String createDiseaseSpecificUrl(String url, String disease) {
    return url + validSuffix(disease);
  }

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

  public final NotificationBundleDiseaseDataBuilder addOrganization(Organization organization) {
    this.organizations.add(organization);
    return this;
  }

  public final NotificationBundleDiseaseDataBuilder setOrganizations(
      List<Organization> organizations) {
    this.organizations.clear();
    this.organizations.addAll(organizations);
    return this;
  }

  @Override
  protected void addEntries() {
    addComposition();
    addEntry(this.notifiedPerson);
    addEntry(this.disease);
    addEntryOfPractitionerRole(this.notifierRole);
    addQuestionnaireResponses();
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
    NotificationDiseaseDataBuilder composition = new NotificationDiseaseDataBuilder();
    composition.setDefaults();
    composition.setProfileUrlByDisease(getCategory());
    DateTimeType date = new DateTimeType();
    date.setValue(Utils.getCurrentDate(), InstantType.DEFAULT_PRECISION);
    composition.setDate(date);
    composition.setNotifiedPerson(this.notifiedPerson);
    composition.setNotifierRole(this.notifierRole);
    composition.setDisease(this.disease);
    composition.setCommonQuestionnaireResponse(this.commonInformation);
    if (this.specificInformation != null) {
      composition.setSpecificQuestionnaireResponse(this.specificInformation);
    }
    return composition;
  }

  private String getCategory() {
    if (this.disease.hasMeta()) {
      Meta meta = this.disease.getMeta();
      if (meta.hasProfile()) {
        final Optional<String> category =
            meta.getProfile().stream()
                .map(CanonicalType::getValue)
                .filter(p -> StringUtils.startsWith(p, CONDITION_PROFILE_PREFIX))
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
    addEncounters();
    addEntry(this.commonInformation);
  }

  private void addImmunizations() {
    addEntries(this.immunizations);
  }

  private void addEncounters() {
    addEntries(this.organizations);
    addEntries(this.hospitalizations);
  }
}
