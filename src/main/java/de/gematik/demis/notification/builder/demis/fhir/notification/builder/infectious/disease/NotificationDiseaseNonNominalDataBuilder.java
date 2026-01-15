package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_TYPE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_7_3_COMPOSITION_TITLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_DISEASE_NON_NOMINAL;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import org.hl7.fhir.r4.model.Coding;

public class NotificationDiseaseNonNominalDataBuilder extends NotificationDiseaseDataBuilder {

  @Override
  public NotificationDiseaseDataBuilder setProfileUrlByDisease(String disease) {
    setProfileUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(
            PROFILE_NOTIFICATION_DISEASE_NON_NOMINAL, disease));
    return this;
  }

  @Override
  public void setDefaultCategory() {
    setCategory(
        new Coding(
            CODE_SYSTEM_NOTIFICATION_TYPE,
            DemisConstants.DISEASE_NOTIFICATION_7_3_TYPE_CODE,
            DemisConstants.NOTIFICATION_7_3_COMPOSITION_TITLE));
  }

  @Override
  protected String getDefaultTitle() {
    return NOTIFICATION_7_3_COMPOSITION_TITLE;
  }
}
