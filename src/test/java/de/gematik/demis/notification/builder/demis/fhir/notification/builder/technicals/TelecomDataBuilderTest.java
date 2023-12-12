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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.jupiter.api.Test;

class TelecomDataBuilderTest {

  @Test
  void shouldSetInputAsContactInfo() {

    ContactPoint contactPoint =
        new TelecomDataBuilder()
            .setSystem(ContactPoint.ContactPointSystem.PAGER)
            .setValue("+999999999")
            .setUse(ContactPoint.ContactPointUse.MOBILE)
            .buildExampleContactPoint();

    assertThat(contactPoint.getSystem()).isEqualTo(ContactPoint.ContactPointSystem.PAGER);
    assertThat(contactPoint.getValue()).isEqualTo("+999999999");
    assertThat(contactPoint.getUse()).isEqualTo(ContactPoint.ContactPointUse.MOBILE);
  }

  @Test
  void shouldUseStandardDataForPhone() {

    ContactPoint contactPoint = new TelecomDataBuilder().buildPhoneContactPoint();

    assertThat(contactPoint.getSystem()).isEqualTo(ContactPoint.ContactPointSystem.PHONE);
    assertThat(contactPoint.getValue()).isEqualTo("0123456789");
    assertThat(contactPoint.getUse()).isEqualTo(ContactPoint.ContactPointUse.WORK);
  }

  @Test
  void shouldUseStandardDataForEmail() {

    ContactPoint contactPoint = new TelecomDataBuilder().buildEmailContactPoint();

    assertThat(contactPoint.getSystem()).isEqualTo(ContactPoint.ContactPointSystem.EMAIL);
    assertThat(contactPoint.getValue()).isEqualTo("some@email.com");
    assertThat(contactPoint.getUse()).isEqualTo(ContactPoint.ContactPointUse.WORK);
  }
}
