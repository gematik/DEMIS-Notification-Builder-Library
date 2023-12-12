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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ReceiptBuilderTest {

  @Test
  void shouldCreateReceiptBundle() {

    Bundle bundle = new ReceiptBuilder().setPdfQuittung(new Binary()).createReportReceiptBundle();

    assertThat(bundle).isNotNull();
    assertThat(bundle.getEntry()).hasSize(4);
    assertThat(bundle.getEntry().get(3).getResource()).isInstanceOf(Binary.class);
  }

  @Test
  void shouldCreateReceiptBundleWithoutPdf() {

    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    assertThat(bundle).isNotNull();
    assertThat(bundle.getEntry()).hasSize(3);
  }

  @Test
  void shouldCreateCompositionAsFirstEntryInBundle() {

    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    assertThat(bundle).isNotNull();
    assertThat(bundle.getEntryFirstRep().getResource()).isInstanceOf(Composition.class);
  }

  @Test
  void shouldSetCorrectSystemForReceiptBundleId() {

    Bundle bundle =
        new ReceiptBuilder().setNotificationBundleId("someId").createReportReceiptBundle();

    Composition resource = (Composition) bundle.getEntryFirstRep().getResource();
    Extension actual = resource.getExtension().get(0);
    Identifier value = (Identifier) actual.getValue();

    assertThat(value.getSystem())
        .isEqualTo("https://demis.rki.de/fhir/NamingSystem/NotificationBundleId");

    assertThat(value.getValue()).isEqualTo("someId");
  }

  @Test
  void shouldSetCodeConceptForTypeInComposition() {
    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    Composition composition = (Composition) bundle.getEntryFirstRep().getResource();
    Coding coding = composition.getType().getCodingFirstRep();
    assertThat(coding.getSystem()).isEqualTo("http://loinc.org");
    assertThat(coding.getCode()).isEqualTo("80563-0");
    assertThat(coding.getDisplay()).isEqualTo("Report");
  }

  @Test
  void shouldSetCorrectTitleInComposition() {
    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    Composition composition = (Composition) bundle.getEntryFirstRep().getResource();
    assertThat(composition.getTitle()).isEqualTo("Meldevorgangsquittung");
  }

  @Test
  void shouldAddAuthorOrganizationWithSpecificDemisInfo() {
    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    Composition composition = (Composition) bundle.getEntryFirstRep().getResource();

    List<Reference> authorList = composition.getAuthor();
    assertThat(authorList).hasSize(1);

    Organization organization = (Organization) authorList.get(0).getResource();

    assertThat(organization.getId()).isEqualTo("DEMIS");
    assertThat(organization.getName()).isEqualTo("DEMIS");

    List<Organization.OrganizationContactComponent> organizationContacts =
        organization.getContact();

    assertThat(organizationContacts).hasSize(1);

    Organization.OrganizationContactComponent contactComponent = organizationContacts.get(0);

    List<ContactPoint> telecomList = contactComponent.getTelecom();
    assertThat(telecomList).hasSize(1);

    ContactPoint contactPoint = telecomList.get(0);
    assertThat(contactPoint.getSystem()).isEqualTo(ContactPoint.ContactPointSystem.EMAIL);
    assertThat(contactPoint.getValue()).isEqualTo("demis@rki.de");

    assertThat(bundle.getEntry().get(1).getResource()).isEqualTo(organization);
  }

  @Test
  void shouldAddReceiverAsSectionComponentToComposition() {
    Bundle bundle = new ReceiptBuilder().createReportReceiptBundle();

    Composition composition = (Composition) bundle.getEntryFirstRep().getResource();
    Composition.SectionComponent sectionComponent = composition.getSectionFirstRep();

    assertThat(sectionComponent.getTitle()).isEqualTo("Empfänger");
    assertThat(sectionComponent.getCode().getText()).isEqualTo("Empfänger");

    Organization organization = (Organization) sectionComponent.getEntryFirstRep().getResource();
    assertThat(organization.getId()).isEqualTo("1.");

    List<Identifier> identifier = organization.getIdentifier();
    assertThat(identifier).hasSize(1);
    assertThat(identifier.get(0).getSystem())
        .isEqualTo("https://demis.rki.de/fhir/CodeSystem/reportingSite");
    assertThat(identifier.get(0).getValue()).isEqualTo("1.");

    assertThat(organization.getName()).isEqualTo("Robert Koch-Institut");

    assertThat(organization.getTelecom()).hasSize(1);
    assertThat(organization.getTelecomFirstRep().getSystem())
        .isEqualTo(ContactPoint.ContactPointSystem.EMAIL);
    assertThat(organization.getTelecomFirstRep().getValue()).isEqualTo("demis@rki.de");

    assertThat(organization.getAddress()).hasSize(1);
    assertThat(organization.getAddressFirstRep().getLine()).hasSize(1);
    assertThat(organization.getAddressFirstRep().getLine().get(0).getValue())
        .isEqualTo("Nordufer 20");
    assertThat(organization.getAddressFirstRep().getPostalCode()).isEqualTo("13353");
    assertThat(organization.getAddressFirstRep().getCity()).isEqualTo("Berlin");
    assertThat(organization.getAddressFirstRep().getCountry()).isEqualTo("20422");

    assertThat(bundle.getEntry().get(2).getResource()).isEqualTo(organization);
  }

  @Test
  void checkStringAfterParsing() throws IOException {

    try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class)) {
      utilities.when(Utils::generateUuidString).thenReturn("1");
      utilities
          .when(Utils::getCurrentDate)
          .thenReturn(
              Date.from(
                  LocalDateTime.of(2020, 1, 1, 0, 0, 0)
                      .atZone(ZoneId.systemDefault())
                      .toInstant()));

      Bundle bundle = new ReceiptBuilder().setRelatesToId("2").createReportReceiptBundle();
      String actualJson =
          FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

      String expectedJson =
          new String(
              getClass()
                  .getClassLoader()
                  .getResourceAsStream("ReportReceiptBundleExample.json")
                  .readAllBytes(),
              StandardCharsets.UTF_8);

      assertThat(actualJson).isEqualToIgnoringNewLines(expectedJson);
    }
  }
}
