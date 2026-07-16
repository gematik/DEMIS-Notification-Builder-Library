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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import lombok.Data;
import lombok.Getter;
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
  private static final String REPORT_TYPE_SYSTEM = "http://loinc.org";
  private static final String PDF_CONTENT_TYPE = "application/pdf";
  private static final String PDF_QUITTUNG_TITLE = "PDF Quittung";
  private static final String RESPONSE_COMPOSITION_TITLE = "Meldevorgangsquittung";

  private static final String REPORT_TYPE_CODE = "80563-0";
  private static final String REPORT_TYPE_DISPLAY = "Report";
  private static final String REPORT_RECEIVER_TITLE = "Empfänger";

  private static final String NOTIFICATION_TYPE_CODE = "77999-1";
  private static final String NOTIFICATION_TYPE_DISPLAY = "Case notification comment";
  private static final String NOTIFICATION_RECEIVER_TITLE = "Zuständiges Gesundheitsamt";

  private Organization rkiOrganization;

  private Organization gaOrganization;

  private Organization custodianOrganization;

  private Organization demisOrg;

  private String notificationBundleId;

  private Binary pdfQuittung;

  private String relatesToId;

  public Bundle createReportReceiptBundle() {

    demisOrg = new DemisOrganizationBuilder().createDemisOrg();
    rkiOrganization = new RkiOrganizationBuilder().createRkiOrganization();

    return createReceiptBundle(ReceiptType.REPORT);
  }

  public Bundle createNotificationReceiptBundle() {

    demisOrg = new DemisOrganizationBuilder().createDemisOrg();

    return createReceiptBundle(ReceiptType.NOTIFICATION);
  }

  private Bundle createReceiptBundle() {
    return createReceiptBundle(ReceiptType.DEFAULT);
  }

  private Bundle createReceiptBundle(final ReceiptType receiptType) {
    Bundle receiptBundle = new Bundle();
    Composition notificationReceiptComposition = new Composition();
    //    Bundle specific
    setupBundle(receiptBundle);

    // Composition
    setupComposition(notificationReceiptComposition, receiptType);

    // add to Composition
    addNotificationBundleId(notificationReceiptComposition);
    addRelatesToTargetData(notificationReceiptComposition);
    addOrganizationReferences(notificationReceiptComposition, receiptType);

    // add to receiptBundle
    addCompositionEntry(receiptBundle, notificationReceiptComposition);
    addOrganizationEntries(receiptBundle, receiptType);

    if (pdfQuittung != null) {
      addPdf(notificationReceiptComposition, pdfQuittung, receiptBundle);
    }

    return receiptBundle;
  }

  private void setupBundle(final Bundle receiptBundle) {
    receiptBundle.setMeta(new Meta().addProfile(RECEIPT_BUNDLE_PROFILE));
    receiptBundle.setType(Bundle.BundleType.COLLECTION);
  }

  private void setupComposition(
      final Composition notificationReceiptComposition, final ReceiptType receiptType) {
    notificationReceiptComposition.setMeta(new Meta().addProfile(NOTIFICATION_RECEIPT_PROFILE));
    notificationReceiptComposition.setType(
        new CodeableConcept(
            new Coding(
                REPORT_TYPE_SYSTEM, receiptType.getTypeCode(), receiptType.getTypeDisplay())));

    notificationReceiptComposition.setId(Utils.generateUuidString());
    notificationReceiptComposition.setDate(Utils.getCurrentDate());

    notificationReceiptComposition.setStatus(Composition.CompositionStatus.FINAL);
    notificationReceiptComposition.setTitle(RESPONSE_COMPOSITION_TITLE);
  }

  private void addNotificationBundleId(final Composition notificationReceiptComposition) {
    Identifier value = new Identifier();
    value.setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    value.setValue(notificationBundleId);

    Extension extension = new Extension();
    extension.setUrl(RECEIVED_NOTIFICATION_EXTENSION);
    extension.setValue(value);
    notificationReceiptComposition.addExtension(extension);
  }

  private void addRelatesToTargetData(final Composition notificationReceiptComposition) {
    Composition.CompositionRelatesToComponent component =
        new Composition.CompositionRelatesToComponent();

    component.setCode(Composition.DocumentRelationshipType.APPENDS);

    Reference relatesToTarget = new Reference();

    relatesToTarget.setType("Composition");
    relatesToTarget.setIdentifier(
        new Identifier().setSystem(NOTIFICATION_ID_SYSTEM).setValue(relatesToId));

    component.setTarget(relatesToTarget);
    notificationReceiptComposition.addRelatesTo(component);
  }

  private void addOrganizationReferences(
      final Composition notificationReceiptComposition, final ReceiptType receiptType) {
    addDemisOrgWhenSet(notificationReceiptComposition);
    if (ReceiptType.REPORT.equals(receiptType)) {
      addOrgWhenSet(notificationReceiptComposition, rkiOrganization, REPORT_RECEIVER_TITLE);
    } else {
      addOrgWhenSet(notificationReceiptComposition, gaOrganization, NOTIFICATION_RECEIVER_TITLE);
      addCustodianOrgWhenSet(notificationReceiptComposition);
    }
  }

  private void addOrgWhenSet(
      final Composition notificationReceiptComposition,
      Organization organization,
      String receiverTitle) {
    if (organization != null) {
      Composition.SectionComponent section = new Composition.SectionComponent();
      section.setTitle(receiverTitle);
      section.setCode(new CodeableConcept().setText(receiverTitle));
      Reference receiverRef = new Reference(organization);
      section.addEntry(receiverRef);
      notificationReceiptComposition.addSection(section);
    }
  }

  private void addDemisOrgWhenSet(final Composition notificationReceiptComposition) {
    if (demisOrg != null) {
      Reference organizationReference = new Reference(demisOrg);
      notificationReceiptComposition.addAuthor(organizationReference);
    }
  }

  private void addCustodianOrgWhenSet(final Composition notificationReceiptComposition) {
    if (custodianOrganization != null) {
      Reference organizationReference = new Reference(custodianOrganization);
      notificationReceiptComposition.setCustodian(organizationReference);
    }
  }

  private void addOrganizationEntries(final Bundle receiptBundle, final ReceiptType receiptType) {
    addOrganizationEntry(receiptBundle, demisOrg);
    if (ReceiptType.REPORT.equals(receiptType)) {
      addOrganizationEntry(receiptBundle, rkiOrganization);
    } else {
      addOrganizationEntry(receiptBundle, gaOrganization);
      addOrganizationEntry(receiptBundle, custodianOrganization);
    }
  }

  private void addOrganizationEntry(final Bundle receiptBundle, final Organization organization) {
    if (organization != null) {
      receiptBundle.addEntry(
          new Bundle.BundleEntryComponent()
              .setResource(organization)
              .setFullUrl(DEMIS_RKI_DE_FHIR + "Organization/" + organization.getId()));
    }
  }

  private void addCompositionEntry(
      final Bundle receiptBundle, final Composition notificationReceiptComposition) {
    receiptBundle.addEntry(
        new Bundle.BundleEntryComponent()
            .setResource(notificationReceiptComposition)
            .setFullUrl(
                DEMIS_RKI_DE_FHIR + "Composition/" + notificationReceiptComposition.getId()));
  }

  private void addPdf(
      final Composition notificationReceiptComposition, Binary pdfBinary, Bundle receiptBundle) {
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

  @Getter
  public enum ReceiptType {
    REPORT(REPORT_TYPE_CODE, REPORT_TYPE_DISPLAY, REPORT_RECEIVER_TITLE),
    NOTIFICATION(NOTIFICATION_TYPE_CODE, NOTIFICATION_TYPE_DISPLAY, NOTIFICATION_RECEIVER_TITLE),
    DEFAULT(REPORT_TYPE_CODE, REPORT_TYPE_DISPLAY, REPORT_RECEIVER_TITLE);

    private final String typeCode;
    private final String typeDisplay;
    private final String receiverTitle;

    ReceiptType(String typeCode, String typeDisplay, String receiverTitle) {
      this.typeCode = typeCode;
      this.typeDisplay = typeDisplay;
      this.receiverTitle = receiverTitle;
    }
  }
}
