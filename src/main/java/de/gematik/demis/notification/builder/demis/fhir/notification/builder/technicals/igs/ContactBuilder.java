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
 * #L%
 */

import java.util.List;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.hl7.fhir.r4.model.StringType;

@Builder(buildMethodName = "initialize")
public class ContactBuilder {

  private NameUse contactNameUse;
  private String contactNameFamily;
  private String contactNameGiven;

  public OrganizationContactComponent build() {
    HumanName humanName = new HumanName();
    if (contactNameUse != null) {
      humanName.setUse(contactNameUse);
    }
    if (StringUtils.isNotBlank(contactNameFamily)) {
      humanName.setFamily(contactNameFamily);
    }
    if (StringUtils.isNotBlank(contactNameGiven)) {
      StringType stringType = new StringType();
      stringType.setValue(contactNameGiven);
      humanName.setGiven(List.of(stringType));
    }
    OrganizationContactComponent contact = new OrganizationContactComponent();
    contact.setName(humanName);
    return contact;
  }
}
