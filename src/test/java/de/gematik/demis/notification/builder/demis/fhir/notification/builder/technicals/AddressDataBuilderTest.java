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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import java.util.SequencedCollection;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.StringType;
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

  @Test
  void shouldSetAddressUseExtensionWithoutDisplay() {
    Address address = new AddressDataBuilder().withAddressUseExtension("use").build();
    assertThat(address.getExtension()).hasSize(1);
    Type value = address.getExtension().get(0).getValue();
    assertThat(value).isInstanceOf(Coding.class);
    Coding code = (Coding) value;
    assertThat(code.getCode()).isEqualTo("use");
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

  @Test
  void thatCopyRedactedAddressCopiesPostalCodeAndCountry() {
    final Address address =
        new Address()
            .setLine(List.of(new StringType("street"), new StringType("nr")))
            .setCity("City 2")
            .setState("State 2")
            .setPostalCode("21345")
            .setCountry("DE");
    final SequencedCollection<Address> result =
        AddressDataBuilder.copyOfRedactedAddress(List.of(address));
    assertThat(result).hasSize(1);

    final Address redactedAddress = result.getFirst();
    assertThat(redactedAddress.getPostalCode()).isEqualTo("213");
    assertThat(redactedAddress.getCountry()).isEqualTo("DE");
    assertThat(redactedAddress.getCity()).isNull();
    assertThat(redactedAddress.getState()).isNull();
    assertThat(redactedAddress.getLine()).isEmpty();
  }

  @Test
  void thatCopyRedactedAddressCanHandleAbsentCountry() {
    final Address address =
        new Address()
            .setLine(List.of(new StringType("street")))
            .setCity("City")
            .setState("State")
            .setPostalCode("12345");

    final SequencedCollection<Address> result =
        AddressDataBuilder.copyOfRedactedAddress(List.of(address));
    assertThat(result).hasSize(1);

    final Address redactedAddress = result.getFirst();
    assertThat(redactedAddress.getPostalCode()).isEqualTo("123");
    assertThat(redactedAddress.getCountry()).isNull();
    assertThat(redactedAddress.getCity()).isNull();
    assertThat(redactedAddress.getState()).isNull();
    assertThat(redactedAddress.getLine()).isEmpty();
  }

  @Test
  void thatCopyRedactedAddressReturnsEmptyIfResultWouldBeEmpty() {
    // GIVEN an address without a field required by the redacted address
    final Address address =
        new Address().setLine(List.of(new StringType("street"))).setCity("City").setState("State");

    // WHEN we copy
    final SequencedCollection<Address> result =
        AddressDataBuilder.copyOfRedactedAddress(List.of(address));

    // THEN no empty address is created
    assertThat(result).isEmpty();
  }

  @Test
  void thatCopyRedactedAddressCopiesExtensions() {
    final List<Extension> extensions = List.of(new Extension("anything"));
    final Address address = new Address().setCountry("DE");
    address.setExtension(extensions);

    final SequencedCollection<Address> result =
        AddressDataBuilder.copyOfRedactedAddress(List.of(address));

    assertThat(result).hasSize(1);
    // Extension isn't properly implementing the equals method, so we fallback to null
    assertThat(result.getFirst().getExtensionByUrl("anything")).isNotNull();
  }
}
