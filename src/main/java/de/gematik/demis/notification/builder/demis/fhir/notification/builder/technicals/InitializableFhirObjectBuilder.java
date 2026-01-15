package de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals;

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

/**
 * Builder that supports initializing of default values you only have to change if you want to
 * create apply very specific values like a pre-defined notification bundle ID or if you want to
 * create an example notification not matching the schema.
 */
public interface InitializableFhirObjectBuilder extends FhirObjectBuilder {

  /**
   * Sets default values like randomly created IDs, but also the specified, fixed FHIR profile URLs
   * for resources like encounter and common questionnaire response. Existing values will not be
   * changed.
   *
   * @return builder
   */
  InitializableFhirObjectBuilder setDefaults();
}
