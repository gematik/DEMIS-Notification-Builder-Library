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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ACT_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_HOSPITALIZATION_NOTE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.STRUCTURE_DEFINITION_HOSPITALIZATION_REGION;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.DateTimeTypeBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

@Setter
@EqualsAndHashCode
public class EncounterDataBuilder implements InitializableFhirObjectBuilder {

  private String id;
  private String profileUrl;

  private String hospitalizationNote;
  private Coding hospitalizationRegion;

  private String status;
  private Coding classification;
  private Coding serviceType;

  private DateTimeType periodStart;
  private DateTimeType periodEnd;
  private Coding reason;

  private Patient notifiedPerson;
  private Organization serviceProvider;

  private static Period getPeriod(Encounter encounter) {
    var period = encounter.getPeriod();
    if (period == null) {
      period = new Period();
      encounter.setPeriod(period);
    }
    return period;
  }

  @Override
  public Encounter build() {
    Encounter encounter = new Encounter();
    addId(encounter);
    addSubject(encounter);
    addServiceProvider(encounter);
    addClassCodingIfSet(encounter);
    addStatusIfSet(encounter);
    addProfileIfSet(encounter);
    addPeriodStartIfSet(encounter);
    addPeriodEndIfSet(encounter);
    addReasonIfSet(encounter);
    addHospitalizationNotIfSet(encounter);
    addHospitalizationRegionIfSet(encounter);
    addServiceTypeCodingIfSet(encounter);
    return encounter;
  }

  private void addReasonIfSet(Encounter encounter) {
    if (this.reason != null) {
      encounter.setReasonCode(List.of(new CodeableConcept().addCoding(this.reason)));
    }
  }

  /**
   * Sets:
   *
   * <ul>
   *   <li>ID
   *   <li>profile URL
   *   <li>classification as inpatient encounter
   * </ul>
   *
   * @return builder
   */
  @Override
  public EncounterDataBuilder setDefaults() {
    if (this.id == null) {
      setId(Utils.generateUuidString());
    }
    if (this.profileUrl == null) {
      setProfileUrl(DemisConstants.PROFILE_HOSPITALIZATION);
    }
    if (this.classification == null) {
      setClassification(new Coding().setSystem(CODE_SYSTEM_ACT_CODE).setCode("IMP"));
    }
    return this;
  }

  public EncounterDataBuilder setPeriodStart(String start) {
    setPeriodStart(new DateTimeTypeBuilder().setText(start).build());
    return this;
  }

  public EncounterDataBuilder setPeriodEnd(String end) {
    setPeriodEnd(new DateTimeTypeBuilder().setText(end).build());
    return this;
  }

  public EncounterDataBuilder setPeriodStart(DateTimeType start) {
    this.periodStart = start;
    return this;
  }

  public EncounterDataBuilder setPeriodEnd(DateTimeType end) {
    this.periodEnd = end;
    return this;
  }

  private void addSubject(Encounter encounter) {
    if (this.notifiedPerson != null) {
      encounter.setSubject(new Reference(this.notifiedPerson));
    }
  }

  private void addServiceProvider(Encounter encounter) {
    if (this.serviceProvider != null) {
      encounter.setServiceProvider(new Reference(this.serviceProvider));
    }
  }

  private void addId(Encounter encounter) {
    this.id = requireNonNullElse(this.id, generateUuidString());
    encounter.setId(this.id);
  }

  private void addServiceTypeCodingIfSet(Encounter encounter) {
    if (this.serviceType != null) {
      encounter.setServiceType(new CodeableConcept(this.serviceType));
    }
  }

  private void addHospitalizationRegionIfSet(Encounter encounter) {
    if (this.hospitalizationRegion != null) {
      Extension region = new Extension();
      region.setUrl(STRUCTURE_DEFINITION_HOSPITALIZATION_REGION);
      region.setValue(new CodeableConcept(this.hospitalizationRegion));
      encounter.addExtension(region);
    }
  }

  private void addHospitalizationNotIfSet(Encounter encounter) {
    if (isNotBlank(this.hospitalizationNote)) {
      encounter.addExtension(
          new Extension(STRUCTURE_DEFINITION_HOSPITALIZATION_NOTE)
              .setValue(new StringType(this.hospitalizationNote)));
    }
  }

  private void addPeriodStartIfSet(Encounter encounter) {
    if (this.periodStart != null) {
      getPeriod(encounter).setStartElement(this.periodStart);
    }
  }

  private void addPeriodEndIfSet(Encounter encounter) {
    if (this.periodEnd != null) {
      getPeriod(encounter).setEndElement(this.periodEnd);
    }
  }

  private void addProfileIfSet(Encounter encounter) {
    if (isNotBlank(this.profileUrl)) {
      encounter.setMeta(new Meta().addProfile(this.profileUrl));
    }
  }

  private void addStatusIfSet(Encounter encounter) {
    if (isNotBlank(this.status)) {
      encounter.setStatus(Encounter.EncounterStatus.fromCode(this.status.toLowerCase()));
    }
  }

  private void addClassCodingIfSet(Encounter encounter) {
    if (this.classification != null) {
      encounter.setClass_(this.classification);
    }
  }

  public static Encounter deepCopy(Encounter originalEncounter, Patient notifiedPerson) {
    EncounterDataBuilder encounterDataBuilder = new EncounterDataBuilder();

    encounterDataBuilder
        .setNotifiedPerson(notifiedPerson)
        .setId(Utils.getShortReferenceOrUrnUuid(originalEncounter))
        .setProfileUrl(originalEncounter.getMeta().getProfile().getFirst().getValue())
        .setStatus(originalEncounter.getStatus().toCode())
        .setClassification(originalEncounter.getClass_())
        .setServiceType(originalEncounter.getServiceType().getCodingFirstRep())
        .setPeriodStart(originalEncounter.getPeriod().getStartElement())
        .setPeriodEnd(originalEncounter.getPeriod().getEndElement());

    List<CodeableConcept> reasonCode = originalEncounter.getReasonCode();
    if (reasonCode != null && !reasonCode.isEmpty()) {
      encounterDataBuilder.setReason(reasonCode.getFirst().getCodingFirstRep());
    }

    Extension hospitalizationNote =
        originalEncounter.getExtensionByUrl(STRUCTURE_DEFINITION_HOSPITALIZATION_NOTE);
    if (hospitalizationNote != null) {
      encounterDataBuilder.setHospitalizationNote(hospitalizationNote.getValue().toString());
    }

    Extension hospitalizationRegionExtension =
        originalEncounter.getExtensionByUrl(STRUCTURE_DEFINITION_HOSPITALIZATION_REGION);
    if (hospitalizationRegionExtension != null) {
      CodeableConcept hospitalizationRegionCC =
          (CodeableConcept) hospitalizationRegionExtension.getValue();
      encounterDataBuilder.setHospitalizationRegion(hospitalizationRegionCC.getCodingFirstRep());
    }

    Encounter encounter = encounterDataBuilder.build();
    encounter.setServiceProvider(originalEncounter.getServiceProvider());
    return encounter;
  }
}
