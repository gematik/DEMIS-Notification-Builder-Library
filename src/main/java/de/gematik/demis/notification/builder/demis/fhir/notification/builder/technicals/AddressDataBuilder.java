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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

@Setter
public class AddressDataBuilder {

  public static final String CURRENT = "current";
  public static final String ORDINARY = "ordinary";
  public static final String PRIMARY = "primary";

  private String street;
  private String houseNumber;
  private String additionalInfo;
  private String city;
  private String postalCode;
  private String country;

  @Setter(AccessLevel.PRIVATE)
  private List<Extension> extensionList = new ArrayList<>();

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

  /**
   * @deprecated method. Use build() instead. Renaming to build() to be consistent with other
   *     builders.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public Address buildAddress() {
    return build();
  }

  public Address build() {
    final Address fhirAddress = new Address();
    if (isNotBlank(postalCode)) {
      fhirAddress.setPostalCode(postalCode);
    }
    if (isNotBlank(country)) {
      fhirAddress.setCountry(country);
    }

    if (isNotBlank(street)) {

      StringBuilder lineBuilder = new StringBuilder();
      lineBuilder.append(street);

      if (isNotBlank(houseNumber)) {
        lineBuilder.append(" ").append(houseNumber);
      }

      if (isNotBlank(additionalInfo)) {
        lineBuilder.append(" ").append(additionalInfo);
      }

      final StringType lineElement = fhirAddress.addLineElement();
      lineElement.setValue(lineBuilder.toString());
    }

    if (isNotBlank(city)) {
      fhirAddress.setCity(city);
    }

    extensionList.forEach(fhirAddress::addExtension);

    return fhirAddress;
  }

  public AddressDataBuilder withOrganizationReferenceExtension(Organization organization) {
    Extension organisationReference = new Extension();
    organisationReference.setUrl(
        "https://demis.rki.de/fhir/StructureDefinition/FacilityAddressNotifiedPerson");
    organisationReference.setValue(new Reference(organization));
    extensionList.add(organisationReference);
    return this;
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
}
