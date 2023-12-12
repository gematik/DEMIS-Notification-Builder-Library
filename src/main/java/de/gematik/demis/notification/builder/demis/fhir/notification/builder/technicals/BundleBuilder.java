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

package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static java.util.Objects.requireNonNullElse;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;

public class BundleBuilder<T extends BundleBuilder<T>> {

  protected PractitionerRole notifierRole;
  protected Composition notificationLaboratory;
  protected String bundleId;
  protected String bundleIdentifier;
  protected String identifierSystem;
  protected String metaProfileUrl;

  public static void addEntry(Bundle bundle, List<? extends Resource> resources) {
    resources.forEach(resource -> addEntry(bundle, resource));
  }

  public static void addEntry(Bundle bundle, IBaseResource resource) {
    if (resource instanceof Resource) {
      String fullUrl = requireNonNullElse(ReferenceUtils.getFullUrl((Resource) resource), null);
      bundle.addEntry().setResource((Resource) resource).setFullUrl(fullUrl);
    }
  }

  public Bundle build() {
    Bundle newBundle = new Bundle();

    addEntry(newBundle, notificationLaboratory);
    addEntry(newBundle, notifierRole);
    addPractitionerRoleWithSubResource(newBundle, notifierRole);

    newBundle.setId(bundleId);

    Identifier identifier = new Identifier();
    identifier.setSystem(identifierSystem);
    bundleIdentifier = requireNonNullElse(bundleIdentifier, generateUuidString());
    identifier.setValue(bundleIdentifier);

    newBundle.setIdentifier(identifier);
    newBundle.setType(Bundle.BundleType.DOCUMENT);
    newBundle.setTimestamp(getCurrentDate());

    return newBundle;
  }

  public T setNotifierRole(PractitionerRole notifierRole) {
    this.notifierRole = notifierRole;
    return (T) this;
  }

  public T setNotificationLaboratory(Composition notificationLaboratory) {
    this.notificationLaboratory = notificationLaboratory;
    return (T) this;
  }

  public T setBundleId(String bundleId) {
    this.bundleId = bundleId;
    return (T) this;
  }

  public T setBundleIdentifier(String bundleIdentifier) {
    this.bundleIdentifier = bundleIdentifier;
    return (T) this;
  }

  protected void setDefaultData() {
    bundleId = requireNonNullElse(bundleId, generateUuidString());
    identifierSystem = NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;
  }

  protected void addPractitionerRoleWithSubResource(
      Bundle newBundle, PractitionerRole practitionerRole) {
    if (practitionerRole != null) {
      if (practitionerRole.getPractitioner().getResource() != null) {
        addEntry(newBundle, practitionerRole.getPractitioner().getResource());
      }
      if (practitionerRole.getOrganization().getResource() != null) {
        addEntry(newBundle, practitionerRole.getOrganization().getResource());
      }
    }
  }
}
