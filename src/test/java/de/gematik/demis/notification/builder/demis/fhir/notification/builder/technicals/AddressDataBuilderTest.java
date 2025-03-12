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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressDataBuilderTest {

  @Test
  void shouldCreateAddressWithInput() {

    Address address =
        new AddressDataBuilder()
            .setStreet("someStreet")
            .setHouseNumber("15")
            .setCity("someCity")
            .setPostalCode("12345")
            .setCountry("6789")
            .build();

    assertThat(address.getLine()).hasSize(1);
    assertThat(address.getLine().get(0)).hasToString("someStreet 15");

    assertThat(address.getCity()).isEqualTo("someCity");

    assertThat(address.getPostalCode()).isEqualTo("12345");

    assertThat(address.getCountry()).isEqualTo("6789");
  }

  @Test
  void shouldCreateMinimalDEMISAccecptedAddressNotifiedPerson() {

    Address address = new AddressDataBuilder().setCity("someCity").setCountry("6789").build();

    assertThat(address.getCity()).isEqualTo("someCity");

    assertThat(address.getCountry()).isEqualTo("6789");
  }

  @DisplayName("add extension for organisation")
  @Test
  void shouldAddExtensionToAddress() {

    Organization orga = new Organization();

    Address address =
        new AddressDataBuilder()
            .setCity("someCity")
            .withOrganizationReferenceExtension(orga)
            .build();

    assertThat(address.getExtension())
        .extracting("value")
        .extracting("resource")
        .containsExactly(orga);
  }

  @DisplayName("add extension for AddressUse")
  @Test
  void shouldAddressUseExtension() {
    Address address = new AddressDataBuilder().withAddressUseExtension("use", "display").build();
    assertThat(address.getExtension()).hasSize(1);
    Type value = address.getExtension().get(0).getValue();
    assertThat(value).isInstanceOf(Coding.class);
    Coding code = (Coding) value;
    assertThat(code.getCode()).isEqualTo("use");
    assertThat(code.getDisplay()).isEqualTo("display");
    assertThat(code.getSystem()).isEqualTo("https://demis.rki.de/fhir/CodeSystem/addressUse");
    assertThat(address.getExtension())
        .extracting("url")
        .containsExactly("https://demis.rki.de/fhir/StructureDefinition/AddressUse");
  }

  @DisplayName("add random extension")
  @Test
  void shouldAddExtension() {
    Address address = new AddressDataBuilder().addExtension(new Extension()).build();
    assertThat(address.getExtension()).hasSize(1);
  }

  @Test
  void thatCopyPostalCodeHandlesMissingPostalCode() {
    final Address address = new Address().setPostalCode(null);
    assertThatNoException().isThrownBy(() -> AddressDataBuilder.copyOnlyPostalCode(address));
  }

  @Test
  void thatCopyPostalCodeHandlesVariousLengths() {
    final Address address = new Address().setPostalCode("12345");
    assertThat(AddressDataBuilder.copyOnlyPostalCode(address).getPostalCode()).isEqualTo("123");

    address.setPostalCode("123");
    assertThat(AddressDataBuilder.copyOnlyPostalCode(address).getPostalCode()).isEqualTo("123");

    address.setPostalCode("12");
    assertThat(AddressDataBuilder.copyOnlyPostalCode(address).getPostalCode()).isEqualTo("12");

    address.setPostalCode("");
    assertThat(AddressDataBuilder.copyOnlyPostalCode(address).getPostalCode()).isNull();
  }
}
