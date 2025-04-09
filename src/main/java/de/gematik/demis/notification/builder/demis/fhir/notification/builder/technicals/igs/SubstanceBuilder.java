package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.igs;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_SUBSTANCES;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_ADAPTER_SUBSTANCE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_PRIMER_SUBSTANCE;

import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Substance;

/** Builder for the entry Patient/NotificationId in an IGS NotificationBundleSequence. */
@SuperBuilder
public class SubstanceBuilder extends AbstractIgsResourceBuilder<Substance> {

  private Kind kind;

  /**
   * Builds the FHIR object representing the entry Patient/NotifiedPersonNotByName.
   *
   * @return The FHIR object representing the entry Patient/NotifiedPersonNotByName.
   */
  @Override
  public Optional<Substance> buildResource() {
    Substance substance = new Substance();

    switch (kind) {
      case PRIMER -> {
        if (StringUtils.isBlank(data.getPrimerSchemeSubstance())) {
          return Optional.empty();
        }
        substance.setDescription(data.getPrimerSchemeSubstance());
        substance.setCode(
            IgsBuilderUtils.generateCodeableConcept(
                CODE_SYSTEM_SEQUENCING_SUBSTANCES, "primer", "Primer-Scheme"));
        substance.setMeta(
            MetaBuilder.builder().metaProfile(PROFILE_PRIMER_SUBSTANCE).initialize().build());
      }
      case ADAPTER -> {
        if (StringUtils.isBlank(data.getAdapterSubstance())) {
          return Optional.empty();
        }
        substance.setDescription(data.getAdapterSubstance());
        substance.setCode(
            IgsBuilderUtils.generateCodeableConcept(
                CODE_SYSTEM_SEQUENCING_SUBSTANCES, "adapter", "Adapter"));
        substance.setMeta(
            MetaBuilder.builder().metaProfile(PROFILE_ADAPTER_SUBSTANCE).initialize().build());
      }
    }

    substance.setId(UUID.randomUUID().toString());

    return Optional.of(substance);
  }

  public enum Kind {
    PRIMER,
    ADAPTER
  }
}
