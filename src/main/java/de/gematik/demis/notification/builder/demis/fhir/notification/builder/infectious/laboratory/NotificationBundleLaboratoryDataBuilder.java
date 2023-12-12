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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.NotifierDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class NotificationBundleLaboratoryDataBuilder
    extends BundleBuilder<NotificationBundleLaboratoryDataBuilder> {

  private Patient notifiedPerson;
  private PractitionerRole submitterRole;
  private Specimen specimen;
  private List<Observation> pathogenDetection = new ArrayList<>();
  private DiagnosticReport laboratoryReport;

  @Setter(AccessLevel.PRIVATE)
  private boolean isDefaultValues = false;

  public NotificationBundleLaboratoryDataBuilder setDefaults() {
    metaProfileUrl = DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY;
    setDefaultData();
    return this;
  }

  @Override
  public Bundle build() {
    Bundle newBundle = super.build();
    addEntry(newBundle, notifiedPerson);
    addEntry(newBundle, submitterRole);
    addPractitionerRoleWithSubResource(newBundle, submitterRole);
    addEntry(newBundle, specimen);
    addEntry(newBundle, pathogenDetection);
    addEntry(newBundle, laboratoryReport);
    newBundle.setMeta(new Meta().addProfile(metaProfileUrl));

    return newBundle;
  }

  /**
   * @deprecated will be removed. new structure for builder incoming.
   */
  @Deprecated(since = "1.2.1")
  public Bundle buildExampleLaboratoryBundle() {
    notifiedPerson =
        requireNonNullElse(
            notifiedPerson, new NotifiedPersonDataBuilder().buildExampleNotifiedPerson());
    notifierRole =
        requireNonNullElse(
            notifierRole, new NotifierDataBuilder().buildLaboratoryExampleNotifierData());
    submitterRole =
        requireNonNullElse(
            submitterRole, new SubmitterDataBuilder().buildExampleSubmitterFacilityData());
    specimen =
        requireNonNullElse(
            specimen,
            new SpecimenDataBuilder().buildExampleSpecimen(notifiedPerson, submitterRole));

    if (pathogenDetection.isEmpty()) {
      pathogenDetection.add(
          new PathogenDetectionDataBuilder()
              .buildExamplePathogenDetection(notifiedPerson, specimen));
    }
    laboratoryReport =
        requireNonNullElse(
            laboratoryReport,
            new LaboratoryReportDataBuilder()
                .buildExampleCVDPLaboratoryReport(notifiedPerson, pathogenDetection));

    if (notificationLaboratory == null) {
      NotificationLaboratoryDataBuilder notificationLaboratoryDataBuilder =
          new NotificationLaboratoryDataBuilder();
      notificationLaboratory =
          notificationLaboratoryDataBuilder.buildExampleComposition(
              notifiedPerson, notifierRole, laboratoryReport);
    }

    return buildLaboratoryBundle();
  }

  /**
   * @deprecated please use {@link #build()} instead.
   * @return
   */
  @Deprecated(since = "1.2.1")
  public Bundle buildLaboratoryBundle() {
    Bundle newBundle = new Bundle();

    newBundle.setMeta(new Meta().addProfile(DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY));

    setStandardDataForLaboratoryNotification(newBundle);

    addEntry(newBundle, notificationLaboratory);
    addEntry(newBundle, notifiedPerson);
    addEntry(newBundle, notifierRole);

    if (notifierRole.getPractitioner().getResource() != null) {
      addEntry(newBundle, notifierRole.getPractitioner().getResource());
    }
    if (notifierRole.getOrganization().getResource() != null) {
      addEntry(newBundle, notifierRole.getOrganization().getResource());
    }

    addEntry(newBundle, submitterRole);

    if (submitterRole.getOrganization().getResource() != null) {
      addEntry(newBundle, submitterRole.getOrganization().getResource());
    }
    if (submitterRole.getPractitioner().getResource() != null) {
      addEntry(newBundle, submitterRole.getPractitioner().getResource());
    }

    addEntry(newBundle, specimen);
    addEntry(newBundle, pathogenDetection);
    addEntry(newBundle, laboratoryReport);

    return newBundle;
  }

  private void setStandardDataForLaboratoryNotification(Bundle newBundle) {
    bundleId = requireNonNullElse(bundleId, generateUuidString());
    newBundle.setId(bundleId);
    Identifier identifier = new Identifier();
    identifier.setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID);
    identifier.setValue(generateUuidString());
    newBundle.setIdentifier(identifier);
    newBundle.setType(Bundle.BundleType.DOCUMENT);
    newBundle.setTimestamp(getCurrentDate());
  }
}
