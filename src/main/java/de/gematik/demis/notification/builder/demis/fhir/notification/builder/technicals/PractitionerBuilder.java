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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SUBMITTING_PERSON;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;

@Setter
public class PractitionerBuilder implements InitializableFhirObjectBuilder {

  public static final String SALUTATION_SYSTEM =
      "https://demis.rki.de/fhir/StructureDefinition/Salutation";
  private Address address;
  private List<ContactPoint> telecomList = new ArrayList<>();
  private String practitionerId;
  private HumanName practitionerName;
  private String salutationCode;
  private String salutationDisplay;

  private boolean active;

  @Setter(AccessLevel.PRIVATE)
  private boolean isNotifierPrac = false;

  private String metaProfileUrl;
  private List<Identifier> identifier;

  /**
   * sets all relevant notifier data
   *
   * @return builder
   */
  public PractitionerBuilder asNotifier() {
    this.metaProfileUrl = PROFILE_NOTIFIER;
    this.isNotifierPrac = true;
    return this;
  }

  /**
   * sets all relevant submitter data
   *
   * @return builder
   */
  public PractitionerBuilder asSubmittingPerson() {
    this.metaProfileUrl = PROFILE_SUBMITTING_PERSON;
    this.isNotifierPrac = false;
    return this;
  }

  @Override
  public PractitionerBuilder setDefaults() {
    if (this.practitionerId == null) {
      this.practitionerId = generateUuidString();
    }
    return this;
  }

  @Override
  public Practitioner build() {
    Practitioner practitioner = new Practitioner();
    practitioner.setId(Objects.requireNonNullElse(this.practitionerId, generateUuidString()));
    practitioner.addName(practitionerName);
    practitioner.setTelecom(telecomList);
    practitioner.addAddress(address);
    practitioner.setActive(active);
    practitioner.setIdentifier(identifier);
    if (isNotifierPrac && salutationCode != null) {
      Extension t = new Extension(SALUTATION_SYSTEM);
      t.setValue(new Coding(SALUTATION_SYSTEM, salutationCode, salutationDisplay));
      practitioner.addExtension(t);
    }
    practitioner.setMeta(new Meta().addProfile(metaProfileUrl));
    return practitioner;
  }

  public PractitionerBuilder addContactPoint(ContactPoint contactPoint) {
    this.telecomList.add(contactPoint);
    return this;
  }
}
