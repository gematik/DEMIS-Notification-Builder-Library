package de.gematik.demis.notification.builder.demis.fhir.notification.builder.receipt;

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

  private static final String RECEIPT_BUNDLE_PROFILE =
      "https://demis.rki.de/fhir/StructureDefinition/ReceiptBundle";
  private static final String NOTIFICATION_RECEIPT_PROFILE =
      "https://demis.rki.de/fhir/StructureDefinition/NotificationReceipt";
  private static final String RECEIVED_NOTIFICATION_EXTENSION =
      "https://demis.rki.de/fhir/StructureDefinition/ReceivedNotification";
  private static final String NOTIFICATION_ID_SYSTEM =
      "https://demis.rki.de/fhir/NamingSystem/NotificationId";
  private static final String REPORT_TYPE_CODE = "80563-0";
  private static final String REPORT_TYPE_DISPLAY = "Report";
  private static final String REPORT_TYPE_SYSTEM = "http://loinc.org";
  private static final String PDF_CONTENT_TYPE = "application/pdf";
  private static final String PDF_QUITTUNG_TITLE = "PDF Quittung";
  private static final String RECEIVER_TITLE = "Empfänger";

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
    receiptBundle.setMeta(new Meta().addProfile(RECEIPT_BUNDLE_PROFILE));
    receiptBundle.setType(Bundle.BundleType.COLLECTION);

    // Composition
    notificationReceiptComposition = new Composition();
    notificationReceiptComposition.setId(generateUuidString());
    notificationReceiptComposition.setTitle("Meldevorgangsquittung");

    notificationReceiptComposition.setMeta(new Meta().addProfile(NOTIFICATION_RECEIPT_PROFILE));

    Identifier value = new Identifier();
    value.setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    value.setValue(notificationBundleId);

    Extension extension = new Extension();
    extension.setUrl(RECEIVED_NOTIFICATION_EXTENSION);
    extension.setValue(value);
    notificationReceiptComposition.addExtension(extension);

    notificationReceiptComposition.setStatus(Composition.CompositionStatus.FINAL);

    notificationReceiptComposition.setType(
        new CodeableConcept(new Coding(REPORT_TYPE_SYSTEM, REPORT_TYPE_CODE, REPORT_TYPE_DISPLAY)));
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
        new Identifier().setSystem(NOTIFICATION_ID_SYSTEM).setValue(relatesToId));

    component.setTarget(relatesToTarget);
    return component;
  }

  private void addRkiOrgWhenSet() {
    if (rkiOrganization != null) {
      Composition.SectionComponent section = new Composition.SectionComponent();
      section.setTitle(RECEIVER_TITLE);
      section.setCode(new CodeableConcept().setText(RECEIVER_TITLE));
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
    pdfBinary.setContentType(PDF_CONTENT_TYPE);
    Composition.SectionComponent t1 = new Composition.SectionComponent();
    t1.setTitle(PDF_QUITTUNG_TITLE);
    t1.setCode(new CodeableConcept().setText(PDF_QUITTUNG_TITLE));
    Reference receiptBinaryRef = new Reference(pdfBinary);
    t1.addEntry(receiptBinaryRef);
    notificationReceiptComposition.addSection(t1);
    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(pdfBinary)
            .setFullUrl(DEMIS_RKI_DE_FHIR + "Binary/" + pdfBinary.getId()));
  }
}
