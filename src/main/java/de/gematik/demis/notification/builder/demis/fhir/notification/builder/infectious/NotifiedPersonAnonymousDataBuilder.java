package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_ANONYMOUS;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.SequencedCollection;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Patient;

@Setter
public class NotifiedPersonAnonymousDataBuilder extends NotifiedPersonNominalDataBuilder {

  /**
   * Create a copy of the given Patient. In case there is more information, only the relevant data
   * for NotifiedPersonAnonymous is extracted.
   */
  @Nonnull
  public static Patient deepCopy(@Nonnull final Patient original) {
    final NotifiedPersonAnonymousDataBuilder builder = new NotifiedPersonAnonymousDataBuilder();

    final SequencedCollection<Address> copiedAddresses =
        AddressDataBuilder.copyOfRedactedAddress(original.getAddress());
    builder.setAddress(copiedAddresses.stream().toList());
    builder.setGender(original.getGender());
    final IIdType idElement = original.getIdElement();
    builder.setId(idElement.getIdPart());

    return builder.build();
  }

  @Override
  public NotifiedPersonAnonymousDataBuilder setDefault() {
    if (this.getId() == null) {
      setId(Utils.generateUuidString());
    }

    setProfileUrl(PROFILE_NOTIFIED_PERSON_ANONYMOUS);
    return this;
  }
}
