package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

import static java.util.Objects.requireNonNullElse;

import lombok.Setter;
import org.hl7.fhir.r4.model.ContactPoint;

@Setter
public class TelecomDataBuilder implements FhirObjectBuilder {

  private ContactPoint.ContactPointSystem system;
  private ContactPoint.ContactPointUse use;
  private String value;

  public ContactPoint buildExampleContactPoint() {
    value = requireNonNullElse(value, "987654320");
    system = requireNonNullElse(system, ContactPoint.ContactPointSystem.PHONE);
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.WORK);
    return build();
  }

  public TelecomDataBuilder setEmail(String email) {
    setSystem(ContactPoint.ContactPointSystem.EMAIL);
    setValue(email);
    return this;
  }

  public TelecomDataBuilder setPhone(String phone) {
    setSystem(ContactPoint.ContactPointSystem.PHONE);
    setValue(phone);
    return this;
  }

  @Override
  public ContactPoint build() {
    return new ContactPoint().setValue(value).setSystem(system).setUse(use);
  }

  public ContactPoint buildPhoneContactPoint() {
    value = requireNonNullElse(value, "0123456789");
    system = ContactPoint.ContactPointSystem.PHONE;
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.WORK);
    return build();
  }

  public ContactPoint buildEmailContactPoint() {
    value = requireNonNullElse(value, "some@email.com");
    system = ContactPoint.ContactPointSystem.EMAIL;
    use = requireNonNullElse(use, ContactPoint.ContactPointUse.WORK);
    return build();
  }
}
