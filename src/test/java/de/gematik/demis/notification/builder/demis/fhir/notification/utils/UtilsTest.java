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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UtilsTest {

  @Test
  void throwsExceptionWhenResourceOrIdIsInvalid() {
    var patient = new Patient();
    assertThatThrownBy(() -> Utils.getShortReferenceOrUrnUuid(patient))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Resource does not have a valid id");
  }

  @ParameterizedTest
  @MethodSource("provideValidResourcesWithUrnUuid")
  void returnsUrnUuidWhenResourceIdStartsWithUrnUuid(IBaseResource resource, String expected) {
    assertThat(Utils.getShortReferenceOrUrnUuid(resource)).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("provideValidResourcesWithNonUrnUuid")
  void returnsShortReferenceWhenResourceIdDoesNotStartWithUrnUuid(
      IBaseResource resource, String expected) {
    assertThat(Utils.getShortReferenceOrUrnUuid(resource)).isEqualTo(expected);
  }

  @Test
  void throwExeptionWhenResourceIsNull() {
    assertThatThrownBy(() -> Utils.getShortReferenceOrUrnUuid(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Resource must not be null");
  }

  @Test
  void throwExeptionWhenPartOfResourceIsNull() {
    IBaseResource mockedResource = mock(IBaseResource.class);
    when(mockedResource.getIdElement()).thenReturn(null);
    // check for resource.getIdElement() == null
    assertThatThrownBy(() -> Utils.getShortReferenceOrUrnUuid(mockedResource))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Resource does not have a valid id");

    IdType id = mock(IdType.class);
    when(mockedResource.getIdElement()).thenReturn(id);
    // check for resource.getIdElement().getValue() == null
    assertThatThrownBy(() -> Utils.getShortReferenceOrUrnUuid(mockedResource))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Resource does not have a valid id");

    when(id.getValue()).thenReturn("foobar");
    // check for resource.getIdElement().getIdPart() == null
    assertThatThrownBy(() -> Utils.getShortReferenceOrUrnUuid(mockedResource))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Resource does not have a valid id");
  }

  private static Stream<Arguments> provideValidResourcesWithUrnUuid() {
    return Stream.of(Arguments.of(mockResourceWithId("urn:uuid:1234"), "urn:uuid:1234"));
  }

  private static Stream<Arguments> provideValidResourcesWithNonUrnUuid() {
    return Stream.of(Arguments.of(mockResourceWithId("1234", "Patient"), "Patient/1234"));
  }

  private static IBaseResource mockResourceWithId(String id) {
    return mockResourceWithId(id, "ResourceType");
  }

  private static IBaseResource mockResourceWithId(String id, String type) {
    IBaseResource resource = new Patient();
    resource.setId(id);
    return resource;
  }
}
