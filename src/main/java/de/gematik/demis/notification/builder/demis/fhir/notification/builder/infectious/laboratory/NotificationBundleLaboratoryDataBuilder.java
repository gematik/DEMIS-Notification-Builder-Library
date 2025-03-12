package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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
 * #L%
 */

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.BundleDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Specimen;

/**
 * Builder for a FHIR bundle containing a laboratory notification. Special feature: <code>
 * createComposition()</code> creates a composition based on the configured data.
 */
@Setter
@Slf4j
public class NotificationBundleLaboratoryDataBuilder extends BundleDataBuilder {

  private Patient notifiedPerson;
  private PractitionerRole notifierRole;
  private PractitionerRole submitterRole;
  private Collection<Specimen> specimen = new ArrayList<>();
  private Collection<Observation> pathogenDetection = new ArrayList<>();
  private DiagnosticReport laboratoryReport;
  private Composition notificationLaboratory;

  @Override
  protected void addEntries() {
    addComposition();
    addNotifiedPerson();
    addNotifier();
    addSubmitter();
    addSpecimen();
    addPathogenDetection();
    addLaboratoryReport();
  }

  @Override
  public NotificationBundleLaboratoryDataBuilder setDefaults() {
    super.setDefaults();
    return this;
  }

  /**
   * Create composition with configured data
   *
   * @return composition builder
   */
  public NotificationLaboratoryDataBuilder createComposition() {
    log.debug("Laboratory notification bundle builder generates composition.");
    NotificationLaboratoryDataBuilder composition = new NotificationLaboratoryDataBuilder();
    composition.setDefault();
    composition.setLaboratoryReport(this.laboratoryReport);
    composition.setNotifierRole(this.notifierRole);
    composition.setNotifiedPerson(this.notifiedPerson);
    return composition;
  }

  @Override
  protected String getDefaultProfileUrl() {
    return DemisConstants.PROFILE_NOTIFICATION_BUNDLE_LABORATORY;
  }

  private void addSpecimen() {
    specimen.forEach(this::addEntry);
  }

  private void addLaboratoryReport() {
    addEntry(laboratoryReport);
  }

  private void addPathogenDetection() {
    addEntries(List.copyOf(pathogenDetection));
  }

  private void addSubmitter() {
    addEntryOfPractitionerRole(this.submitterRole);
  }

  private void addNotifiedPerson() {
    addEntry(notifiedPerson);
  }

  private void addNotifier() {
    addEntryOfPractitionerRole(notifierRole);
  }

  private void addComposition() {
    if (this.notificationLaboratory != null) {
      addEntry(this.notificationLaboratory);
    }
  }
}
