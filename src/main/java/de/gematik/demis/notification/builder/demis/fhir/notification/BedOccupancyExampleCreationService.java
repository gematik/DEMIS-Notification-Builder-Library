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

package de.gematik.demis.notification.builder.demis.fhir.notification;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.reports.ReportBundleDataBuilder;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;

public class BedOccupancyExampleCreationService {

  private FhirContext fhirContext = FhirContext.forR4();

  public Bundle createTestBedOccupancyBundle() {
    return new ReportBundleDataBuilder().buildExampleReportBundle();
  }

  public String createTestBedOccupancyBundleJson() {
    return fhirContext.newJsonParser().encodeResourceToString(createTestBedOccupancyBundle());
  }

  public String createTestBedOccupancyBundleXml() {
    return fhirContext.newXmlParser().encodeResourceToString(createTestBedOccupancyBundle());
  }

  public Parameters createStandardLaboratoryParameters() {
    Parameters.ParametersParameterComponent content =
        new Parameters.ParametersParameterComponent(new StringType("content"))
            .setResource(createTestBedOccupancyBundle());
    return new Parameters().addParameter(content);
  }

  public String createTestBedOccupancyParametersJson() {
    return fhirContext.newJsonParser().encodeResourceToString(createStandardLaboratoryParameters());
  }

  public String createTestBedOccupancyParametersXml() {
    return fhirContext.newXmlParser().encodeResourceToString(createStandardLaboratoryParameters());
  }
}
