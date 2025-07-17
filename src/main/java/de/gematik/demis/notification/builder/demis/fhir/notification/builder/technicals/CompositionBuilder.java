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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_DISPLAY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.getCurrentDate;
import static org.hl7.fhir.r4.model.Composition.DocumentRelationshipType.APPENDS;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.ReferenceUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.CheckForNull;
import lombok.Getter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;

@Getter
public abstract class CompositionBuilder<T extends CompositionBuilder> {

  private String typeSystem;
  private String typeCode;
  private String typeDisplay;
  private String title;
  private String notificationId;
  @CheckForNull private Patient notifiedPerson;
  @CheckForNull private PractitionerRole notifierRole;
  private Composition.CompositionStatus compositionStatus;
  private List<Extension> extensions;

  /**
   * @deprecated the strict profiles for laboratory notifications will not allow the use of
   *     composition cateogry. so it can be removed from the builders
   */
  @Deprecated(forRemoval = true)
  private String codeAndCategorySystem;

  /**
   * @deprecated the strict profiles for laboratory notifications will not allow the use of
   *     composition cateogry. so it can be removed from the builders
   */
  @Deprecated(forRemoval = true)
  private String codeAndCategoryCode;

  /**
   * @deprecated the strict profiles for laboratory notifications will not allow the use of
   *     composition cateogry. so it can be removed from the builders
   */
  @Deprecated(forRemoval = true)
  private String codeAndCategoryDisplay;

  /**
   * @deprecated Use {@code dateTimeElement} instead. This field uses {@link java.util.Date}, which
   *     does not reliably preserve millisecond precision when serializing or deserializing FHIR
   *     resources. For full precision and to ensure the original timestamp (including milliseconds)
   *     is retained in FHIR JSON/XML, use the new {@link org.hl7.fhir.r4.model.DateTimeType} field
   *     and set it via {@code setDateElement()}.
   */
  @Deprecated(forRemoval = true)
  private Date date;

  /**
   * Stores the exact FHIR date/time value, including milliseconds, as a {@link DateTimeType}.
   * Always use this field and {@code setDateElement()} for setting or copying date/time values to
   * ensure full precision is maintained in FHIR resources.
   */
  private DateTimeType dateTimeElement;

  private String identifierSystem;
  private String identifierValue;
  private String metaUrl;
  private String relatesTo;
  private Composition.DocumentRelationshipType relatesToCode = APPENDS;
  private String relatesToReferenceType = "Composition";

  public T setRelatesToNotificationId(String relatesTo) {
    this.relatesTo = relatesTo;
    return (T) this;
  }

  public T setRelatesToCode(Composition.DocumentRelationshipType relatesToCode) {
    this.relatesToCode = relatesToCode;
    return (T) this;
  }

  public T setRelatesToReferenceType(String relatesToReferenceType) {
    this.relatesToReferenceType = relatesToReferenceType;
    return (T) this;
  }

  public T setMetaUrl(String metaUrl) {
    this.metaUrl = metaUrl;
    return (T) this;
  }

  public T setTypeSystem(String typeSystem) {
    this.typeSystem = typeSystem;
    return (T) this;
  }

  public T setTypeCode(String typeCode) {
    this.typeCode = typeCode;
    return (T) this;
  }

  public T setTypeDisplay(String typeDisplay) {
    this.typeDisplay = typeDisplay;
    return (T) this;
  }

  public T setTitle(String title) {
    this.title = title;
    return (T) this;
  }

  public T setNotificationId(String notificationId) {
    this.notificationId = notificationId;
    return (T) this;
  }

  public T setNotifiedPerson(Patient notifiedPerson) {
    this.notifiedPerson = notifiedPerson;
    return (T) this;
  }

  public T setNotifierRole(PractitionerRole notifierRole) {
    this.notifierRole = notifierRole;
    return (T) this;
  }

  public T setCompositionStatus(Composition.CompositionStatus compositionStatus) {
    this.compositionStatus = compositionStatus;
    return (T) this;
  }

  public T setCodeAndCategorySystem(String codeAndCategorySystem) {
    this.codeAndCategorySystem = codeAndCategorySystem;
    return (T) this;
  }

  public T setCodeAndCategoryCode(String codeAndCategoryCode) {
    this.codeAndCategoryCode = codeAndCategoryCode;
    return (T) this;
  }

  public T setCodeAndCategoryDisplay(String codeAndCategoryDisplay) {
    this.codeAndCategoryDisplay = codeAndCategoryDisplay;
    return (T) this;
  }

  /**
   * @deprecated Use {@link #setDateTimeType(DateTimeType)} instead. Setting the date using a {@link
   *     java.util.Date} does not guarantee preservation of millisecond precision when serializing
   *     or deserializing FHIR resources. To ensure the exact timestamp (including milliseconds) is
   *     retained in the FHIR output, use {@code setDateElement(DateTimeType)} with a {@link
   *     DateTimeType} value.
   */
  @Deprecated(forRemoval = true)
  public T setDate(Date date) {
    this.date = date;
    return (T) this;
  }

  public T setDateTimeType(DateTimeType dateTimeType) {
    this.dateTimeElement = dateTimeType;
    return (T) this;
  }

  public T setIdentifierSystem(String identifierSystem) {
    this.identifierSystem = identifierSystem;
    return (T) this;
  }

  public T setIdentifierValue(String identifierValue) {
    this.identifierValue = identifierValue;
    return (T) this;
  }

  public T addExtension(Extension extension) {
    if (extensions == null) {
      extensions = new ArrayList<>();
    }
    this.extensions.add(extension);
    return (T) this;
  }

  protected void setDefaultData() {
    // standard type code/system for laboratory notification
    typeSystem = NOTIFICATION_STANDARD_TYPE_SYSTEM;
    typeCode = NOTIFICATION_STANDARD_TYPE_CODE;
    typeDisplay = NOTIFICATION_STANDARD_TYPE_DISPLAY;
    date = getCurrentDate();
    identifierValue = generateUuidString();
    notificationId = generateUuidString();

    metaUrl = getDefaultProfileUrl();
  }

  protected Composition build() {
    Composition newComposition = new Composition();
    newComposition.setTitle(title);
    newComposition.setId(notificationId);
    newComposition.setStatus(compositionStatus);
    if (notifiedPerson != null) {
      newComposition.setSubject(ReferenceUtils.internalReference(notifiedPerson));
    }
    if (notifierRole != null) {
      newComposition.addAuthor(ReferenceUtils.internalReference(notifierRole));
    }
    newComposition.setMeta(new Meta().addProfile(metaUrl));

    if (codeAndCategorySystem != null && codeAndCategoryCode != null) {
      Coding codeAndCategroyCoding =
          new Coding(codeAndCategorySystem, codeAndCategoryCode, codeAndCategoryDisplay);
      CodeableConcept categoryCodeableConcept = new CodeableConcept(codeAndCategroyCoding);
      newComposition.addCategory(categoryCodeableConcept);
    }

    if (dateTimeElement != null) {
      newComposition.setDateElement(dateTimeElement);
    } else if (date != null) {
      newComposition.setDate(date);
    }

    Coding typeCoding = new Coding(typeSystem, typeCode, typeDisplay);
    CodeableConcept typeCodeableConcept = new CodeableConcept(typeCoding);
    newComposition.setType(typeCodeableConcept);

    newComposition.setExtension(extensions);

    return newComposition;
  }

  public T setStatusToFinal() {
    this.compositionStatus = Composition.CompositionStatus.FINAL;
    return (T) this;
  }

  public T setStatusToPreliminary() {
    this.compositionStatus = Composition.CompositionStatus.PRELIMINARY;
    return (T) this;
  }

  public T setStatusToAmended() {
    this.compositionStatus = Composition.CompositionStatus.AMENDED;
    return (T) this;
  }

  public T setStatusToEnteredinerror() {
    this.compositionStatus = Composition.CompositionStatus.ENTEREDINERROR;
    return (T) this;
  }

  protected abstract String getDefaultProfileUrl();
}
