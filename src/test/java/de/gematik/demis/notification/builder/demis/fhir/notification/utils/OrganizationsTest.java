package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class OrganizationsTest {

  @Test
  void thatItCanExtractAnOrganizationFromAnExtension() {
    // GIVEN an organisation referenced by an extension
    final Organization referencedByAddress = new Organization();
    final Extension extension = new Extension().setValue(new Reference(referencedByAddress));

    // AND an address using that extension
    final Address address = new Address();
    address.setExtension(List.of(extension, new Extension()));

    // WHEN
    final List<Organization> organizations = Organizations.fromExtension(address);
    assertThat(organizations).containsExactly(referencedByAddress);
  }

  @Test
  void thatExtensionsReferencingOtherModelsAreIgnored() {
    // GIVEN an organisation referenced by an extension
    final Organization referencedByAddress = new Organization();
    final Extension extension = new Extension().setValue(new Reference(referencedByAddress));

    // AND an extension referencing some other model
    final Extension referenceToPatient = new Extension();
    referenceToPatient.setValue(new Reference(new Patient()));

    // AND an address using that extension
    final Address address = new Address();
    address.setExtension(List.of(extension, referenceToPatient));

    // WHEN
    final List<Organization> organizations = Organizations.fromExtension(address);
    assertThat(organizations).containsExactly(referencedByAddress);
  }
}
