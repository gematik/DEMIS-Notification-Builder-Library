package de.gematik.demis.notification.builder.demis.fhir.notification.utils;

/*-
 * #%L
 * notification-builder-library
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

class ReferenceUtilsTest {

  @Test
  void thatNPEIsThrown() {
    final Patient resource = new Patient();
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              ReferenceUtils.internalReference(resource);
            });
  }

  @Test
  void canDealWithUrnUuid() {
    final Patient resource = new Patient();
    resource.setId("urn:uuid:20998fdc-0af8-4276-b099-5997534f1e5e");

    final Reference reference = ReferenceUtils.internalReference(resource);
    assertThat(reference.getReference()).isEqualTo("urn:uuid:20998fdc-0af8-4276-b099-5997534f1e5e");
    assertThat(reference.getResource()).isSameAs(resource);
  }

  @Test
  void convertIdToLocalId() {
    final Patient resource = new Patient();
    resource.setId("123");

    final Reference reference = ReferenceUtils.internalReference(resource);
    assertThat(reference.getReference()).isEqualTo("Patient/123");
    assertThat(reference.getResource()).isSameAs(resource);
  }

  @Test
  void thatValidRefReturnsResource_getResource() {
    final Patient patient = new Patient();
    patient.setId("123");
    final Reference reference = new Reference(patient);
    final Patient referencedPatient = (Patient) ReferenceUtils.getResource(reference);
    assertThat(referencedPatient.getId()).isEqualTo("123");
  }

  @Test
  void thatInvalidRefThrowsException_getResource() {
    final Reference reference = new Reference("Patient/invalid-ref");
    assertThatThrownBy(() -> ReferenceUtils.getResource(reference))
        .isInstanceOf(UnprocessableEntityException.class)
        .hasMessageContaining("Reference 'Patient/invalid-ref' is not resolvable");
  }

  @Test
  void thatEmptyRefReturnsNull_getResource() {
    final Reference reference = new Reference();
    assertThat(ReferenceUtils.getResource(reference)).isNull();
  }
}
