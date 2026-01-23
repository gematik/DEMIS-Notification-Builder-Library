package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder.yearMonthToString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_GENDER;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.EXTENSION_URL_PSEUDONYM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFIED_PERSON_NOT_BY_NAME;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PatientBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.types.AddressUse;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Patients;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Type;

@Setter
public class NotifiedPersonNonNominalDataBuilder {

  private String id;
  private DateType birthdate;
  private Enumerations.AdministrativeGender gender;
  private Extension genderExtension;
  private List<Address> address = new ArrayList<>();
  private DateTimeType deceased;
  private List<Extension> extensions;

  @Setter(AccessLevel.PRIVATE)
  private Type pseudonym;

  /**
   * Copy all required properties and resources attached to the given {@link Patient}.
   *
   * <p><strong>Note on handling addresses</strong>
   *
   * <p>Addresses are simply redacted using {@link
   * AddressDataBuilder#copyOfRedactedAddress(Collection)}. The caller MUST pass only addresses that
   * should be copied. No considerations are made for specific AddressUse extensions.
   *
   * @param addressesToCopy Addresses to copy to the new Patient resource. Existing addresses on the
   *     Patient are ignored.
   */
  @Nonnull
  public static Patient deepCopy(
      @Nonnull final Patient patientToCopy,
      @Nonnull final SequencedCollection<Address> addressesToCopy) {
    final NotifiedPersonNonNominalDataBuilder builder = new NotifiedPersonNonNominalDataBuilder();

    final SequencedCollection<Address> addresses =
        AddressDataBuilder.copyOfRedactedAddress(addressesToCopy);
    builder.setAddress(List.copyOf(addresses));

    Patients.copyBirthdateShortened(patientToCopy, builder::setBirthdate);

    builder.setGender(patientToCopy.getGender());
    final IIdType idElement = patientToCopy.getIdElement();
    builder.setId(idElement.getIdPart());
    final Extension extensionByUrl = patientToCopy.getExtensionByUrl(EXTENSION_URL_PSEUDONYM);
    final Type pseudonym = extensionByUrl == null ? null : extensionByUrl.getValue();
    builder.setPseudonym(pseudonym);

    return builder.build();
  }

  @Nonnull
  public static Patient createExcerptNotByNamePatient(@Nonnull final Patient patientToCopy) {
    final NotifiedPersonNonNominalDataBuilder builder =
        new NotifiedPersonNonNominalDataBuilder()
            .setId(Utils.generateUuidString())
            .setGender(patientToCopy.getGender())
            .setGenderExtension(
                patientToCopy.getGenderElement().getExtensionByUrl(EXTENSION_URL_GENDER))
            .setDeceased(patientToCopy.getDeceasedDateTimeType())
            .setAddress(AddressDataBuilder.copyAddressesForExcerpt(patientToCopy.getAddress()))
            .addExtension(patientToCopy.getExtensionByUrl(EXTENSION_URL_PSEUDONYM));

    Patients.copyBirthdateShortened(patientToCopy, builder::setBirthdate);

    return builder.build();
  }

  /** Return the addresses to copy. This method will remove forbidden AddressUse extensions. */
  @Nonnull
  public static SequencedCollection<Address> getAddressesToCopy(
      @Nonnull final Patient patientToCopy) {
    /*
     Look at two types of addresses. Addresses that have
     1. only the AddressUse::current extension
     2. multiple extensions, at least one is AddressUse::current
     3. no AddressUse::current extension
     Ensure the iteration order stays intact, we do the same when redacting
    */

    final ArrayList<Address> result = new ArrayList<>();

    // We are about to modify Address objects here, to avoid changing data for the caller we do copy
    // the information first.
    final List<Address> safeAddresses =
        patientToCopy.getAddress().stream().map(Address::copy).toList();

    for (final Address address : safeAddresses) {
      final List<Extension> desiredExtensions =
          address.getExtension().stream()
              // Remove extensions that reference an Organization with NotifiedPersonFacility
              // profile
              .filter(
                  extension -> {
                    final boolean hasProfile =
                        DemisConstants.STRUCTURE_DEFINITION_FACILITY_ADDRESS_NOTIFIED_PERSON.equals(
                            extension.getUrl());
                    if (hasProfile
                        && extension.getValue() instanceof Reference ref
                        && ref.getResource() instanceof Resource resource) {
                      return !Metas.hasProfile(DemisConstants.PROFILE_NOTIFIED_PERSON_FACILITY)
                          .test(resource);
                    }
                    return true;
                  })
              // Remove AddressUse.CURRENT
              .filter(Predicate.not(AddressUse.CURRENT.matchesExtension()))
              .toList();
      /*
       Only keep the address if we have an AddressUse extension, otherwise we might keep a current
       address around that doesn't have any useful extension (e.g. because someone added a random
       extension that we don't filter)
      */
      final boolean hasAddressUseExtension =
          desiredExtensions.stream()
              .anyMatch(
                  extension ->
                      DemisConstants.STRUCTURE_DEFINITION_ADDRESS_USE.equals(extension.getUrl()));
      if (hasAddressUseExtension) {
        address.setExtension(desiredExtensions);
        result.add(address);
      }
    }

    return result;
  }

  public Patient build() {
    return new PatientBuilder()
        .setProfileUrl(PROFILE_NOTIFIED_PERSON_NOT_BY_NAME)
        .setId(id)
        .setBirthdate(birthdate)
        .setGender(gender)
        .setGenderExtension(genderExtension)
        .setAddress(address)
        .setPseudonym(pseudonym)
        .setLastUpdated(Utils.getCurrentDate())
        .setDeceased(deceased)
        .setExtensions(extensions)
        .build();
  }

  public NotifiedPersonNonNominalDataBuilder setBirthdate(DateType birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public NotifiedPersonNonNominalDataBuilder setBirthdate(Year year) {
    this.birthdate = new DateType();
    this.birthdate.setValueAsString(year.toString());
    this.birthdate.setPrecision(TemporalPrecisionEnum.YEAR);
    return this;
  }

  public NotifiedPersonNonNominalDataBuilder setBirthdate(YearMonth yearMonth) {
    this.birthdate = new DateType();
    this.birthdate.setValueAsString(yearMonthToString(yearMonth));
    this.birthdate.setPrecision(TemporalPrecisionEnum.MONTH);
    return this;
  }

  /**
   * <b>NOTE:</b> You probably want to use {@link
   * NotifiedPersonNonNominalDataBuilder#setBirthdate(YearMonth)} which won't generate exceptions.
   *
   * <p>Attempt to parse a birthdate from the given string.
   *
   * @param birthdate in the format yyyy or yyyy-MM
   * @throws IllegalArgumentException If the date is in a format other than yyyy-MM or yyyy.
   */
  public NotifiedPersonNonNominalDataBuilder setBirthdate(String birthdate) {
    this.birthdate = new DateType();
    TemporalPrecisionEnum precision;
    if (birthdate == null) {
      return this;
    }

    if (birthdate.length() == 4) {
      precision = TemporalPrecisionEnum.YEAR;
      this.birthdate.setValueAsString(birthdate);
    } else if (birthdate.length() == 7) {
      precision = TemporalPrecisionEnum.MONTH;
      this.birthdate.setValueAsString(birthdate + "-01");
    } else {
      throw new IllegalArgumentException("Birthdate must be in the format yyyy or yyyy-MM");
    }
    this.birthdate.setPrecision(precision);
    return this;
  }

  public NotifiedPersonNonNominalDataBuilder addExtension(Extension extension) {
    if (this.extensions == null) {
      this.extensions = new ArrayList<>();
    }
    this.extensions.add(extension);
    return this;
  }
}
