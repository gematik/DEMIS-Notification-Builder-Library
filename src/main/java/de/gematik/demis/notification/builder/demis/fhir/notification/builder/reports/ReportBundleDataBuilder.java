package de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

@Setter
@Slf4j
public class ReportBundleDataBuilder extends BundleDataBuilder {

  private Composition reportBedOccupancy;
  private PractitionerRole notifierRole;
  private QuestionnaireResponse statisticInformationBedOccupancy;

  @Override
  public ReportBundleDataBuilder setDefaults() {
    super.setDefaults();
    return this;
  }

  @Override
  protected void addEntries() {
    if (this.reportBedOccupancy != null) {
      addEntry(this.reportBedOccupancy);
    }
    if (this.notifierRole != null) {
      addEntryOfPractitionerRole(this.notifierRole);
    }
    if (this.statisticInformationBedOccupancy != null) {
      addEntry(this.statisticInformationBedOccupancy);
    }
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_REPORT_BUNDLE;
  }
}
