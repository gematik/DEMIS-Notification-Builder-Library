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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire.QuestionnaireResponseBuilder.deepCopy;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_BUNDLE_DISEASE;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonNonNominalDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Provenance;
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

  @Nonnull
  public static Bundle createNonNominalExcerpt(@Nonnull final Bundle originalBundle) {
    final NotificationBundleDiseaseDataBuilder builder = new NotificationBundleDiseaseDataBuilder();
    final BundleBuilderContext ctx = BundleBuilderContext.from(originalBundle);
    final Patient notifiedPerson =
        NotifiedPersonNonNominalDataBuilder.createExcerptNotByNamePatient(ctx.subject());

    final Condition condition = DiseaseDataBuilder.deepCopy(ctx.condition(), notifiedPerson);
    final QuestionnaireResponse specificQuestionnaire =
        ctx.specificQuestionnaireResponse().isPresent()
            ? deepCopy(ctx.specificQuestionnaireResponse().get(), notifiedPerson)
            : null;

    final QuestionnaireResponse commonQuestionnaire =
        ctx.commonQuestionnaireResponse().isPresent()
            ? deepCopy(ctx.commonQuestionnaireResponse().get(), notifiedPerson)
            : null;

    final PractitionerRole notifierRole = PractitionerRoleBuilder.deepCopy73(ctx.notifier());

    final Composition composition =
        NotificationDiseaseDataBuilder.excerptCopy(
            ctx.composition(),
            condition,
            notifiedPerson,
            notifierRole,
            Optional.ofNullable(specificQuestionnaire),
            Optional.ofNullable(commonQuestionnaire));

    builder
        .setDisease(condition)
        .setSpecificInformation(specificQuestionnaire)
        .setCommonInformation(commonQuestionnaire)
        .setNotificationDisease(composition)
        .setNotifiedPerson(notifiedPerson)
        .setNotifierRole(notifierRole)
        .setHospitalizations(
            ctx.encounters().stream()
                .map(encounter -> EncounterDataBuilder.deepCopy(encounter, notifiedPerson))
                .toList())
        .setEncounterOrganizations(ctx.organizations())
        .setImmunizations(
            ctx.immunizations().stream()
                .map(
                    immunization -> ImmunizationDataBuilder.deepyCopy(immunization, notifiedPerson))
                .toList());

    originalBundle.getMeta().getTag().forEach(builder::addTag);

    ctx.provenance().map(Provenance::copy).ifPresent(builder::addAdditionalEntry);

    final Identifier newIdentifierForCopiedBundle =
        new Identifier()
            .setValue(Utils.generateUuidString())
            .setSystem(DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);

    final String originalBundleIdentifier = originalBundle.getIdentifier().getValue();
    final Coding referenceTagToOriginalBundle =
        new Coding(
            DemisConstants.RELATED_NOTIFICATION_CODING_SYSTEM,
            originalBundleIdentifier,
            "Relates to message with identifier: " + originalBundleIdentifier);

    return builder
        .setId(originalBundle.getId())
        .setProfileUrl(builder.getDefaultProfileUrl())
        .setIdentifier(newIdentifierForCopiedBundle)
        .setType(Bundle.BundleType.DOCUMENT)
        .setTimestamp(originalBundle.getTimestamp())
        .setLastUpdated(originalBundle.getMeta().getLastUpdated())
        .addTag(referenceTagToOriginalBundle)
        .build();
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
