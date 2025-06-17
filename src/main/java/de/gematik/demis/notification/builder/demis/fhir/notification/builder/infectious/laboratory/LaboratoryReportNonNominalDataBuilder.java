package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.LABORATORY_REPORT_BASE_URL;

import lombok.Setter;

/**
 * Builder for LaboratoryReport resources intended for non-nominal (non-identifiable) notifications.
 *
 * <p>Currently, this builder does not introduce any functional differences compared to
 * LaboratoryReportDataBuilder. Both builders generate LaboratoryReport resources with the same
 * profile URL schema: "LaboratoryReport" + pathogen code (e.g. "LaboratoryReportCVDP" for
 * SARS-CoV-2).
 *
 * <p>The explicit separation into two builders is intentional, providing flexibility for future
 * requirements. Should significant differences for non-nominal reports arise, this structure allows
 * for isolated changes without breaking existing interfaces or implementations.
 */
@Setter
public class LaboratoryReportNonNominalDataBuilder extends LaboratoryReportDataBuilder {

  @Override
  protected String getDefaultProfileUrl() {
    /*
     * Note: The profile URL for LaboratoryReportNonNominal remains identical to the nominal variant.
     * This ensures consistent FHIR validation and processing, regardless of notification type.
     * If future requirements demand a different profile URL, adjustments can be made here.
     */
    return LABORATORY_REPORT_BASE_URL;
  }
}
