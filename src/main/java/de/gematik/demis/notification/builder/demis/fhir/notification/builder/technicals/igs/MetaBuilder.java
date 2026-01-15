package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Setter;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Meta;

/** Builder for the Meta structure. */
@Setter
@Builder(buildMethodName = "initialize")
public class MetaBuilder {

  private String metaProfile;
  private Date lastUpdated;

  /**
   * Generates the Meta object.
   *
   * @return The Meta object.
   */
  public Meta build() {
    Meta meta = new Meta();
    CanonicalType canonicalType = new CanonicalType();
    canonicalType.setValue(metaProfile);
    meta.setProfile(List.of(canonicalType));
    if (lastUpdated != null) {
      meta.setLastUpdated(lastUpdated);
    }
    return meta;
  }
}
