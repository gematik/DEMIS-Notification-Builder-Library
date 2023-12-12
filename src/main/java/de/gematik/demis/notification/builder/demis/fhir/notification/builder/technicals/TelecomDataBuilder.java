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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import static java.util.Objects.requireNonNullElse;

import lombok.Setter;
import org.hl7.fhir.r4.model.ContactPoint;

@Setter
public class TelecomDataBuilder {

  private String value;
  private ContactPoint.ContactPointSystem system;
  private ContactPoint.ContactPointUse use;

  public ContactPoint buildExampleContactPoint() {

    value = requireNonNullElse(value, "987654320");
    system = requireNonNullElse(system, ContactPoint.ContactPointSystem.FAX);
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.HOME);

    return buildContactPoint();
  }

  public ContactPoint buildContactPoint() {
    ContactPoint newContactPoint = new ContactPoint();
    if (value != null) {
      newContactPoint.setValue(value);
    }
    if (system != null) {
      newContactPoint.setSystem(system);
    }
    if (use != null) {
      newContactPoint.setUse(use);
    }
    return newContactPoint;
  }

  public ContactPoint buildPhoneContactPoint() {
    value = requireNonNullElse(value, "0123456789");
    system = ContactPoint.ContactPointSystem.PHONE;
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.WORK);
    return buildExampleContactPoint();
  }

  public ContactPoint buildEmailContactPoint() {
    value = requireNonNullElse(value, "some@email.com");
    system = ContactPoint.ContactPointSystem.EMAIL;
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.WORK);
    return buildExampleContactPoint();
  }
}
