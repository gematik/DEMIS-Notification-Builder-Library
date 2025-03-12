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
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SEQUENCING_PLATFORM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_SEQUENCING_DEVICE;
import static org.hl7.fhir.r4.model.Device.DeviceNameType.MODELNAME;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.experimental.SuperBuilder;
import org.hl7.fhir.r4.model.Device;

/** Builder for the entry Device/SequencingDevice in an IGS NotificationBundleSequence. */
@SuperBuilder
public class SequencingDeviceBuilder extends AbstractIgsResourceBuilder<Device> {

  /**
   * Builds the FHIR object representing the entry Device/SequencingDevice.
   *
   * @return The FHIR object representing the entry Device/SequencingDevice.
   */
  @Override
  public Optional<Device> buildResource() {
    Device device = new Device();

    device.setId(UUID.randomUUID().toString());

    device.setMeta(
        MetaBuilder.builder().metaProfile(PROFILE_SEQUENCING_DEVICE).initialize().build());

    Device.DeviceDeviceNameComponent deviceDeviceNameComponent =
        new Device.DeviceDeviceNameComponent();
    deviceDeviceNameComponent.setName(data.getSequencingInstrument());
    deviceDeviceNameComponent.setType(MODELNAME);
    device.setDeviceName(List.of(deviceDeviceNameComponent));

    device.setType(
        IgsBuilderUtils.generateCodeableConcept(
            CODE_SYSTEM_SEQUENCING_PLATFORM, data.getSequencingPlatform(), null));

    return Optional.of(device);
  }
}
