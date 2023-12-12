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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DEMIS_RKI_DE_FHIR;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;

import lombok.Data;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;

/** This builder helps with the creation of receipt bundles. it will add all profile */
@Data
public class ReceiptBuilder {

  private Organization rkiOrganization;

  private Organization demisOrg;

  private Composition notificationReceiptComposition;

  private String notificationBundleId;

  private Binary pdfQuittung;

  private String relatesToId;

  public Bundle createReportReceiptBundle() {

    demisOrg = new DemisOrganizationBuilder().createDemisOrg();
    rkiOrganization = new RkiOrganizationBuilder().createRkiOrganization();

    return createReceiptBundle();
  }

  public Bundle createReceiptBundle() {
    Bundle receiptBundle = new Bundle();

    //    Bundle specific
    receiptBundle.setMeta(
        new Meta().addProfile("https://demis.rki.de/fhir/StructureDefinition/ReceiptBundle"));
    receiptBundle.setType(Bundle.BundleType.COLLECTION);

    // Composition
    notificationReceiptComposition = new Composition();
    notificationReceiptComposition.setId(generateUuidString());
    notificationReceiptComposition.setTitle("Meldevorgangsquittung");

    notificationReceiptComposition.setMeta(
        new Meta().addProfile("https://demis.rki.de/fhir/StructureDefinition/NotificationReceipt"));

    Identifier value = new Identifier();
    value.setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    value.setValue(notificationBundleId);

    Extension extension = new Extension();
    extension.setUrl("https://demis.rki.de/fhir/StructureDefinition/ReceivedNotification");
    extension.setValue(value);
    notificationReceiptComposition.addExtension(extension);

    notificationReceiptComposition.setStatus(Composition.CompositionStatus.FINAL);

    notificationReceiptComposition.setType(
        new CodeableConcept(new Coding("http://loinc.org", "80563-0", "Report")));
    notificationReceiptComposition.setDate(getCurrentDate());

    notificationReceiptComposition.addRelatesTo(createRelatesToTargetData());

    addDemisOrgWhenSet();
    addRkiOrgWhenSet();

    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(notificationReceiptComposition)
            .setFullUrl(
                DEMIS_RKI_DE_FHIR + "Composition/" + notificationReceiptComposition.getId()));
    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(demisOrg)
            .setFullUrl(DEMIS_RKI_DE_FHIR + "Organization/" + demisOrg.getId()));
    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(rkiOrganization)
            .setFullUrl(DEMIS_RKI_DE_FHIR + "Organization/" + rkiOrganization.getId()));

    if (pdfQuittung != null) {
      addPdf(pdfQuittung, receiptBundle);
    }

    return receiptBundle;
  }

  private Composition.CompositionRelatesToComponent createRelatesToTargetData() {
    Composition.CompositionRelatesToComponent component =
        new Composition.CompositionRelatesToComponent();

    component.setCode(Composition.DocumentRelationshipType.APPENDS);

    Reference relatesToTarget = new Reference();

    relatesToTarget.setType("Composition");
    relatesToTarget.setIdentifier(
        new Identifier()
            .setSystem("https://demis.rki.de/fhir/NamingSystem/NotificationId")
            .setValue(relatesToId));

    component.setTarget(relatesToTarget);
    return component;
  }

  private void addRkiOrgWhenSet() {
    if (rkiOrganization != null) {
      Composition.SectionComponent section = new Composition.SectionComponent();
      section.setTitle("Empfänger");
      section.setCode(new CodeableConcept().setText("Empfänger"));
      Reference receiverRef = new Reference(rkiOrganization);
      section.addEntry(receiverRef);
      notificationReceiptComposition.addSection(section);
    }
  }

  private void addDemisOrgWhenSet() {
    if (demisOrg != null) {
      Reference organizationReference = new Reference(demisOrg);
      notificationReceiptComposition.addAuthor(organizationReference);
    }
  }

  private void addPdf(Binary pdfBinary, Bundle receiptBundle) {

    pdfBinary.setId(generateUuidString());
    pdfBinary.setContentType("application/pdf");
    Composition.SectionComponent t1 = new Composition.SectionComponent();
    t1.setTitle("PDF Quittung");
    t1.setCode(new CodeableConcept().setText("PDF Quittung"));
    Reference receiptBinaryRef = new Reference(pdfBinary);
    t1.addEntry(receiptBinaryRef);
    notificationReceiptComposition.addSection(t1);
    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(pdfBinary)
            .setFullUrl(DEMIS_RKI_DE_FHIR + "Binary/" + pdfBinary.getId()));
  }
}
