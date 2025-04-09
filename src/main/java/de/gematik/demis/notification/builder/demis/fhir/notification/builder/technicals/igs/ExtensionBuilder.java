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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import lombok.Builder;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

@Builder(buildMethodName = "initialize")
public class ExtensionBuilder {

  private String url;
  private String valueString;
  private String dateTime;
  private Coding valueCoding;
  private Reference valueReference;

  public Extension build() {
    Extension extension = new Extension();
    extension.setUrl(url);
    if (valueString != null) {
      extension.setValue(new StringType(valueString));
    }
    if (valueCoding != null) {
      extension.setValue(valueCoding);
    }
    if (valueReference != null) {
      extension.setValue(valueReference);
    }
    if (dateTime != null) {
      extension.setValue(new DateTimeType(dateTime));
    }
    return extension;
  }
}
