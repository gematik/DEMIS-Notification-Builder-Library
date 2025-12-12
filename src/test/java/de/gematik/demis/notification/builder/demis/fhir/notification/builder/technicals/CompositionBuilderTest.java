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

class CompositionBuilderTest {

  //  @Test
  //  @DisplayName("standard type coding test")
  //  void shouldSetTypeCodingToStandardValues() {
  //
  //    CompositionBuilder compositionBuilder = new CompositionBuilder();
  //
  //    compositionBuilder.setDefaultData();
  //
  //    assertThat(compositionBuilder.typeSystem).isEqualTo("http://loinc.org");
  //    assertThat(compositionBuilder.typeCode).isEqualTo("34782-3");
  //    assertThat(compositionBuilder.typeDisplay).isEqualTo("Infectious disease Note");
  //  }
  //
  //  @Test
  //  @DisplayName("special test for date")
  //  void shouldTestStandardDateBehavior() {
  //
  //    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
  //      utilities.when(Utils::generateUuidString).thenReturn("1");
  //      Date date =
  //          Date.from(
  //              LocalDateTime.of(2020, 1, 1, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
  //      utilities.when(Utils::getCurrentDate).thenReturn(date);
  //
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //      compositionBuilder.setDefaultData();
  //
  //      Composition composition = compositionBuilder.build();
  //
  //      assertThat(composition.getDate()).isEqualTo(date);
  //      assertThat(compositionBuilder.identifierValue).isEqualTo("1");
  //      assertThat(compositionBuilder.notificationId).isEqualTo("1");
  //    }
  //  }
  //
  //  @Test
  //  @DisplayName("builder should create composition and use setter")
  //  void shouldCreateComposition() {
  //    CompositionBuilder compositionBuilder = new CompositionBuilder();
  //    compositionBuilder.setTitle("testTitle");
  //    compositionBuilder.setNotificationId("notificationId");
  //    compositionBuilder.setCompositionStatus(Composition.CompositionStatus.FINAL);
  //    Patient patient = new Patient();
  //    compositionBuilder.setNotifiedPerson(patient);
  //    PractitionerRole practitionerRole = new PractitionerRole();
  //    compositionBuilder.setNotifierRole(practitionerRole);
  //    compositionBuilder.setCodeAndCategoryCode("catCode");
  //    compositionBuilder.setCodeAndCategorySystem("catSystem");
  //    compositionBuilder.setCodeAndCategoryDisplay("catDisplay");
  //    Date date = new Date();
  //    compositionBuilder.setDate(date);
  //
  //    Composition composition = compositionBuilder.build();
  //
  //    assertThat(composition).isNotNull();
  //    assertThat(composition.getTitle()).isEqualTo("testTitle");
  //    assertThat(composition.getId()).isEqualTo("notificationId");
  //    assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
  //    assertThat(composition.getSubject().getResource()).isEqualTo(patient);
  //    assertThat(composition.getAuthor()).extracting("resource").containsOnly(practitionerRole);
  //
  // assertThat(composition.getCategory().get(0).getCodingFirstRep().getCode()).isEqualTo("catCode");
  //    assertThat(composition.getCategory().get(0).getCodingFirstRep().getSystem())
  //        .isEqualTo("catSystem");
  //    assertThat(composition.getCategory().get(0).getCodingFirstRep().getDisplay())
  //        .isEqualTo("catDisplay");
  //    assertThat(composition.getDate()).isEqualTo(date);
  //  }
  //
  //  @Test
  //  @DisplayName("builder should set data from default method")
  //  void shouldCreateCompositionWithDefaultData() {
  //    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
  //      utilities.when(Utils::generateUuidString).thenReturn("1");
  //      Date date = new Date();
  //      utilities.when(Utils::getCurrentDate).thenReturn(date);
  //
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //      compositionBuilder.setDefaultData();
  //
  //      Composition composition = compositionBuilder.build();
  //
  //      assertThat(composition.getType().getCodingFirstRep().getSystem())
  //          .isEqualTo("http://loinc.org");
  //      assertThat(composition.getType().getCodingFirstRep().getCode()).isEqualTo("34782-3");
  //      assertThat(composition.getType().getCodingFirstRep().getDisplay())
  //          .isEqualTo("Infectious disease Note");
  //      assertThat(composition.getDate()).isEqualTo(date);
  //      assertThat(composition.getId()).isEqualTo("1");
  //      assertThat(compositionBuilder.identifierValue).isEqualTo("1");
  //    }
  //  }
  //
  //  @Nested
  //  @DisplayName("status convenient setter tests")
  //  class setterTest {
  //
  //    @Test
  //    @DisplayName("status to final")
  //    void shouldSetStatusToFinal() {
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //
  //      compositionBuilder.setStatusToFinal();
  //
  //      assertThat(compositionBuilder.compositionStatus)
  //          .isEqualTo(Composition.CompositionStatus.FINAL);
  //    }
  //
  //    @Test
  //    @DisplayName("status to preliminary")
  //    void shouldSetStatusToPreliminary() {
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //
  //      compositionBuilder.setStatusToPreliminary();
  //
  //      assertThat(compositionBuilder.compositionStatus)
  //          .isEqualTo(Composition.CompositionStatus.PRELIMINARY);
  //    }
  //
  //    @Test
  //    @DisplayName("status to amended")
  //    void shouldSetStatusToAmended() {
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //
  //      compositionBuilder.setStatusToAmended();
  //
  //      assertThat(compositionBuilder.compositionStatus)
  //          .isEqualTo(Composition.CompositionStatus.AMENDED);
  //    }
  //
  //    @Test
  //    @DisplayName("status to Error")
  //    void shouldSetStatusToEnterednerror() {
  //      CompositionBuilder compositionBuilder = new CompositionBuilder();
  //
  //      compositionBuilder.setStatusToEnteredinerror();
  //
  //      assertThat(compositionBuilder.compositionStatus)
  //          .isEqualTo(Composition.CompositionStatus.ENTEREDINERROR);
  //    }
  //  }
}
