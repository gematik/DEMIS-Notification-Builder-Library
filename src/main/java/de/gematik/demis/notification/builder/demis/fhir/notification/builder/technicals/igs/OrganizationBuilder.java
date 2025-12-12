package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateCodeableConcept;
import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs.IgsBuilderUtils.generateIdentifier;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ORGANIZATION_TYPE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_BSNR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_LABORATORY_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER_FACILITY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_FACILITY;
import static org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.EMAIL;
import static org.hl7.fhir.r4.model.ContactPoint.ContactPointUse.WORK;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Organization;

/** Builder for the FHIR resource Organization in an IGS NotificationBundleSequence. */
@SuperBuilder
public class OrganizationBuilder extends AbstractIgsResourceBuilder<Organization> {

  private static final String CODING_CODE = "refLab";
  private static final String CODING_DISPLAY = "Einrichtung der Spezialdiagnostik";
  private OrganizationType organizationType;

  @Override
  public Optional<Organization> buildResource() {
    Organization organization = new Organization();
    organization.setId(UUID.randomUUID().toString());

    if (organizationType == OrganizationType.LABOR) {
      return buildLabOrg(organization);
    } else if (organizationType == OrganizationType.SUBMITTING) {
      return buildSubmitterOrg(organization);
    }
    return Optional.empty();
  }

  private Optional<Organization> buildLabOrg(Organization organization) {
    organization.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_NOTIFIER_FACILITY).initialize().build());
    organization.setIdentifier(
        List.of(
            generateIdentifier(NAMING_SYSTEM_LABORATORY_ID, data.getSequencingLabDemisLabId())));
    organization.setType(
        List.of(
            generateCodeableConcept(CODE_SYSTEM_ORGANIZATION_TYPE, CODING_CODE, CODING_DISPLAY)));
    organization.setName(data.getSequencingLabName());
    organization.setTelecom(
        List.of(
            ContactPointBuilder.builder()
                .system(EMAIL)
                .value(data.getSequencingLabEmail())
                .use(WORK)
                .initialize()
                .build()));
    organization.setAddress(
        List.of(
            AddressBuilder.builder()
                .line(data.getSequencingLabAddress())
                .postalCode(data.getSequencingLabPostalCode())
                .city(data.getSequencingLabCity())
                .federalState(data.getSequencingLabFederalState())
                .country(data.getSequencingLabCountry())
                .initialize()
                .build()));
    return Optional.of(organization);
  }

  private Optional<Organization> buildSubmitterOrg(Organization organization) {
    if (!hasSubmittingFacility()) {
      return Optional.empty();
    }
    validateSubmittingFacilityData();
    organization.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_SUBMITTING_FACILITY).initialize().build());
    organization.setIdentifier(
        List.of(generateIdentifier(NAMING_SYSTEM_BSNR, data.getPrimeDiagnosticLabDemisLabId())));
    organization.setName(data.getPrimeDiagnosticLabName());
    organization.setTelecom(
        List.of(
            ContactPointBuilder.builder()
                .system(EMAIL)
                .value(data.getPrimeDiagnosticLabEmail())
                .use(WORK)
                .initialize()
                .build()));
    organization.setAddress(
        List.of(
            AddressBuilder.builder()
                .line(data.getPrimeDiagnosticLabAddress())
                .postalCode(data.getPrimeDiagnosticLabPostalCode())
                .federalState(data.getPrimeDiagnosticLabFederalState())
                .city(data.getPrimeDiagnosticLabCity())
                .country(data.getPrimeDiagnosticLabCountry())
                .initialize()
                .build()));
    return Optional.of(organization);
  }

  private boolean hasSubmittingFacility() {
    return StringUtils.isNotBlank(data.getPrimeDiagnosticLabName())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabEmail())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabAddress())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabCity())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabPostalCode())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabCountry())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabDemisLabId())
        || StringUtils.isNotBlank(data.getPrimeDiagnosticLabFederalState());
  }

  private void validateSubmittingFacilityData() {
    if (StringUtils.isBlank(data.getPrimeDiagnosticLabName())
        || StringUtils.isBlank(data.getPrimeDiagnosticLabEmail())
        || StringUtils.isBlank(data.getPrimeDiagnosticLabAddress())
        || StringUtils.isBlank(data.getPrimeDiagnosticLabCity())
        || StringUtils.isBlank(data.getPrimeDiagnosticLabPostalCode())
        || StringUtils.isBlank(data.getPrimeDiagnosticLabCountry())) {
      throw new InvalidInputDataException(
          "Wenn das Primärlabor angegeben wurde, dann müssen die folgenden Felder alle "
              + "befüllt werden: PRIME_DIAGNOSTIC_LAB.NAME, PRIME_DIAGNOSTIC_LAB.EMAIL, PRIME_DIAGNOSTIC_LAB.ADDRESS, "
              + "PRIME_DIAGNOSTIC_LAB.CITY, PRIME_DIAGNOSTIC_LAB.POSTAL_CODE und PRIME_DIAGNOSTIC_LAB.COUNTRY");
    }
  }

  public enum OrganizationType {
    LABOR,
    SUBMITTING;
  }
}
