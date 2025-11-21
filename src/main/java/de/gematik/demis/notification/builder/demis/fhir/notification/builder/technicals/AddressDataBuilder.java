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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_ADDRESS_USE;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.truncate;

import com.google.common.base.Strings;
import de.gematik.demis.notification.builder.demis.fhir.notification.types.AddressUse;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;

@Setter
public class AddressDataBuilder implements FhirObjectBuilder {

  public static final String CURRENT = "current";
  public static final String ORDINARY = "ordinary";
  public static final String PRIMARY = "primary";

  /** street or line information */
  private String street;

  private String houseNumber;
  private String additionalInfo;
  private String city;
  private String postalCode;
  private String country;

  @Setter(AccessLevel.PRIVATE)
  private List<Extension> extensionList = new ArrayList<>();

  public static void copyExtensions(final Address source, final Address target) {
    target.setExtension(source.getExtension().stream().map(Extension::copy).toList());
  }

  public static String shortenPostalCode(final Address originalAddress) {
    return truncate(originalAddress.getPostalCode(), 3);
  }

  public static List<Address> copyAllAddressesForExcerpt(List<Address> addresses) {
    return addresses.stream().map(AddressDataBuilder::copyAddressWithPostalCodeCountryUse).toList();
  }

  public static Address copyAddressWithPostalCodeCountryUse(final Address original) {
    AddressDataBuilder builder =
        new AddressDataBuilder()
            .setPostalCode(shortenPostalCode(original))
            .setCountry(original.getCountry())
            .withAddressUseExtension(original.getExtensionByUrl(CODE_SYSTEM_ADDRESS_USE));

    return builder.build();
  }

  /**
   * Create a new {@link Address} and only copy the first three letters of the postal code. See
   * {@link AddressDataBuilder#copyExtensions(Address, Address)} on how to copy extensions.
   */
  public static Address copyOnlyPostalCode(final Address original) {
    final Address result = new Address();
    final String postalCode = original.getPostalCode();
    if (!Strings.isNullOrEmpty(postalCode)) {
      result.setPostalCode(postalCode.substring(0, Math.min(postalCode.length(), 3)));
    }

    return result;
  }

  /**
   * Copy the given addresses so that they are redacted. Redacted means: all address types are
   * copied, but only 3 letters of the postal code remain.
   */
  public static SequencedCollection<Address> copyOfRedactedAddress(
      final Collection<Address> originals) {

    final ArrayList<Address> result = new ArrayList<>(originals.size());
    for (final Address original : originals) {
      if (original.hasPostalCode() || original.hasCountry()) {
        final Address redactedCopy = AddressDataBuilder.copyOnlyPostalCode(original);
        redactedCopy.setCountry(original.getCountry());
        AddressDataBuilder.copyExtensions(original, redactedCopy);
        result.add(redactedCopy);
      } else if (isReferencingOrganization(original)) {
        // make sure to FIRST test that there is no data to redact and then add addresses
        // referencing organizations
        final Address target = new Address();
        AddressDataBuilder.copyExtensions(original, target);
        result.add(target);
      }
    }

    return result;
  }

  /**
   * @return true if any extension on the address references an organization
   */
  public static boolean isReferencingOrganization(@Nonnull final Address original) {
    return original.getExtension().stream()
        .map(Extension::getValue)
        .anyMatch(Utils.hasFhirType("Reference"));
  }

  /**
   * Standard builder method. All fields can be set to other input. Standard address: Dorfstraße 11
   * 21481 Buchhorst 20422
   *
   * @return hl7 address ressource to be used in address fields of other ressources
   */
  public Address buildExampleAddress() {
    street = requireNonNullElse(street, "Dorfstraße 11");
    city = requireNonNullElse(city, "Buchhorst");
    postalCode = requireNonNullElse(postalCode, "21481");
    country = requireNonNullElse(country, "20422");
    return build();
  }

  @Override
  public Address build() {
    final Address fhirAddress = new Address();
    addPostalCode(fhirAddress);
    addCountry(fhirAddress);
    addLine(fhirAddress);
    addCity(fhirAddress);
    extensionList.forEach(fhirAddress::addExtension);
    return fhirAddress;
  }

  @Nonnull
  public AddressDataBuilder withOrganizationReferenceExtension(@Nonnull Organization organization) {
    final Extension organisationReference =
        new Extension()
            .setUrl(DemisConstants.STRUCTURE_DEFINITION_FACILITY_ADDRESS_NOTIFIED_PERSON)
            .setValue(new Reference(organization));
    extensionList.add(organisationReference);
    return this;
  }

  public AddressDataBuilder withAddressUseExtension(@Nonnull final Extension addressUseExtension) {
    extensionList.add(addressUseExtension);
    return this;
  }

  public AddressDataBuilder withAddressUseExtension(@Nonnull final AddressUse addressUse) {
    extensionList.add(addressUse.asExtension());
    return this;
  }

  public AddressDataBuilder withAddressUseExtension(String addressUseCode) {
    return withAddressUseExtension(addressUseCode, null);
  }

  public AddressDataBuilder withAddressUseExtension(
      String addressUseCode, String addressUseDisplay) {
    Extension addressUseExtension = new Extension();
    addressUseExtension.setUrl("https://demis.rki.de/fhir/StructureDefinition/AddressUse");
    String addressUseSystem = "https://demis.rki.de/fhir/CodeSystem/addressUse";
    Coding addressUseExtensionCode =
        new Coding(addressUseSystem, addressUseCode, addressUseDisplay);
    addressUseExtension.setValue(addressUseExtensionCode);
    extensionList.add(addressUseExtension);
    return this;
  }

  public AddressDataBuilder addExtension(Extension extension) {
    extensionList.add(extension);
    return this;
  }

  private void addPostalCode(Address fhirAddress) {
    if (isNotBlank(postalCode)) {
      fhirAddress.setPostalCode(postalCode);
    }
  }

  private void addCountry(Address fhirAddress) {
    if (isNotBlank(country)) {
      fhirAddress.setCountry(country);
    }
  }

  private void addCity(Address fhirAddress) {
    if (isNotBlank(city)) {
      fhirAddress.setCity(city);
    }
  }

  private void addLine(Address fhirAddress) {
    if (isNotBlank(street)) {
      StringBuilder lineBuilder = new StringBuilder();
      lineBuilder.append(street);
      if (isNotBlank(houseNumber)) {
        lineBuilder.append(" ").append(houseNumber);
      }
      if (isNotBlank(additionalInfo)) {
        lineBuilder.append(" ").append(additionalInfo);
      }
      fhirAddress.addLineElement().setValue(lineBuilder.toString());
    }
  }
}
