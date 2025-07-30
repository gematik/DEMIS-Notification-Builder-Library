package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_NOTIFICATION_TYPE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.CODE_SYSTEM_SECTION_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DISEASE_COMPOSITION_TITLE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DISEASE_NOTIFICATION_TYPE_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.DISEASE_NOTIFICATION_TYPE_DISPLAY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_ID;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_CODE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_DISPLAY;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NOTIFICATION_STANDARD_TYPE_SYSTEM;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.RECEPTION_TIME_STAMP_TYPE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils.generateUuidString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.InitializableFhirObjectBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Metas;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;

/** Composition builder of disease notification */
@Setter
public class NotificationDiseaseDataBuilder implements InitializableFhirObjectBuilder {

  private static final String PROFILE_URL =
      "https://demis.rki.de/fhir/StructureDefinition/NotificationDisease";

  private String id;
  private String profileUrl;
  private String status;
  private Coding type;
  private String title;
  private DateTimeType date;
  private Identifier identifier;
  private Coding category;
  private List<Extension> extensions;

  private Patient notifiedPerson;
  private PractitionerRole notifierRole;
  private Condition disease;
  @CheckForNull private QuestionnaireResponse commonQuestionnaireResponse;
  private QuestionnaireResponse specificQuestionnaireResponse;

  @Nonnull
  public static Composition deepCopy(
      @Nonnull final Composition original,
      @Nonnull final Condition condition,
      @Nonnull final Patient notifiedPerson,
      @Nonnull final PractitionerRole notifier,
      @Nonnull final QuestionnaireResponse specificQuestionnaireResponse) {
    final NotificationDiseaseDataBuilder builder =
        new NotificationDiseaseDataBuilder()
            .setCategory(original.getCategoryFirstRep().getCodingFirstRep().copy())
            .setDate(original.getDateElement().copy())
            .setId(original.getId())
            .setIdentifier(original.getIdentifier().copy())
            .setStatus(original.getStatus())
            .setTitle(original.getTitle())
            .setType(original.getType().getCodingFirstRep().copy());

    final Optional<String> profile = Metas.profilesFrom(original).stream().findFirst();
    profile.ifPresent(builder::setProfileUrl);

    Extension extensionByUrl = original.getExtensionByUrl(RECEPTION_TIME_STAMP_TYPE);
    if (extensionByUrl != null) {
      builder.addExtension(
          new Extension().setUrl(extensionByUrl.getUrl()).setValue(extensionByUrl.getValue()));
    }

    final Composition intermediate =
        builder
            .setDisease(condition)
            .setNotifiedPerson(notifiedPerson)
            .setNotifierRole(notifier)
            .setSpecificQuestionnaireResponse(specificQuestionnaireResponse)
            .build();
    final List<Composition.CompositionRelatesToComponent> relatesTo =
        original.getRelatesTo().stream()
            .map(Composition.CompositionRelatesToComponent::copy)
            .toList();
    return intermediate.setRelatesTo(relatesTo);
  }

  @Override
  public Composition build() {
    Composition composition = new Composition();
    addId(composition);
    addTitle(composition);
    addDate(composition);
    addIdentifier(composition);
    addMetaProfile(composition);
    addStatus(composition);
    addType(composition);
    addCategory(composition);
    addSubject(composition);
    addNotifierRole(composition);
    addDiseaseSections(composition);

    composition.setExtension(extensions);

    return composition;
  }

  /**
   * Set default values:
   *
   * <ul>
   *   <li>title
   *   <li>document type
   *   <li>document category
   *   <li>ID
   *   <li>notification ID
   *   <li>status
   *   <li>date
   * </ul>
   */
  @Override
  public NotificationDiseaseDataBuilder setDefaults() {
    if (this.id == null) {
      setId(generateUuidString());
    }
    if (this.title == null) {
      setTitle(getDefaultTitle());
    }
    if (this.type == null) {
      setType(
          new Coding(
              NOTIFICATION_STANDARD_TYPE_SYSTEM,
              NOTIFICATION_STANDARD_TYPE_CODE,
              NOTIFICATION_STANDARD_TYPE_DISPLAY));
    }
    if (this.category == null) {
      setDefaultCategory();
    }
    if (this.identifier == null) {
      setIdentifierAsNotificationId(generateUuidString());
    }
    if (this.status == null) {
      setStatus("final");
    }
    if (this.date == null) {
      setDate(DateTimeType.now());
    }
    return this;
  }

  protected String getDefaultTitle() {
    return DISEASE_COMPOSITION_TITLE;
  }

  public void setDefaultCategory() {
    setCategory(
        new Coding(
            CODE_SYSTEM_NOTIFICATION_TYPE,
            DISEASE_NOTIFICATION_TYPE_CODE,
            DISEASE_NOTIFICATION_TYPE_DISPLAY));
  }

  public NotificationDiseaseDataBuilder setStatus(Composition.CompositionStatus status) {
    this.status = status.name();
    return this;
  }

  public NotificationDiseaseDataBuilder setStatus(String status) {
    this.status = status;
    return this;
  }

  /**
   * Set identifier with notification ID value
   *
   * @param notificationId notification ID
   * @return builder
   */
  public NotificationDiseaseDataBuilder setIdentifierAsNotificationId(String notificationId) {
    this.identifier =
        new Identifier().setSystem(NAMING_SYSTEM_NOTIFICATION_ID).setValue(notificationId);
    return this;
  }

  public NotificationDiseaseDataBuilder setProfileUrlByDisease(String disease) {
    setProfileUrl(
        NotificationBundleDiseaseDataBuilder.createDiseaseSpecificUrl(PROFILE_URL, disease));
    return this;
  }

  private void addDiseaseSections(Composition composition) {
    addDiseaseSection(this.disease, composition);
    if (this.commonQuestionnaireResponse != null) {
      addCommonInformationSection(this.commonQuestionnaireResponse, composition);
    }
    if (this.specificQuestionnaireResponse != null) {
      addSpecificInformationSection(this.specificQuestionnaireResponse, composition);
    }
  }

  private void addSubject(Composition composition) {
    if (this.notifiedPerson != null) {
      composition.setSubject(new Reference(this.notifiedPerson));
    }
  }

  private void addNotifierRole(Composition composition) {
    if (this.notifierRole != null) {
      composition.addAuthor(new Reference(this.notifierRole));
    }
  }

  private void addId(Composition composition) {
    composition.setId(this.id);
  }

  private void addCategory(Composition composition) {
    if (this.category != null) {
      composition.addCategory(new CodeableConcept(this.category));
    }
  }

  private void addIdentifier(Composition composition) {
    if (this.identifier != null) {
      composition.setIdentifier(this.identifier);
    }
  }

  private void addDate(Composition composition) {
    if (date != null) {
      composition.setDate(date.getValue());
    }
  }

  private void addTitle(Composition composition) {
    if (isNotBlank(title)) {
      composition.setTitle(title);
    }
  }

  private void addCommonInformationSection(
      @Nonnull QuestionnaireResponse commonQuestionnaireResponse,
      @Nonnull Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("Meldetatbestandsübergreifende klinische und epidemiologische Angaben")
            .setCode(
                new CodeableConcept(
                    new Coding(
                        CODE_SYSTEM_SECTION_CODE,
                        DemisConstants.DISEASE_SECTION_COMMON_CODE,
                        DemisConstants.DISEASE_SECTION_COMMON_DISPLAY)))
            .addEntry(new Reference(commonQuestionnaireResponse)));
  }

  private void addSpecificInformationSection(
      QuestionnaireResponse specificQuestionnaireResponse, Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("Meldetatbestandsspezifische klinische und epidemiologische Angaben")
            .setCode(
                new CodeableConcept(
                    new Coding(
                        CODE_SYSTEM_SECTION_CODE,
                        DemisConstants.DISEASE_SECTION_SPECIFIC_CODE,
                        DemisConstants.DISEASE_SECTION_SPECIFIC_DISPLAY)))
            .addEntry(new Reference(specificQuestionnaireResponse)));
  }

  private void addDiseaseSection(Condition disease, Composition composition) {
    composition.addSection(
        new Composition.SectionComponent()
            .setTitle("disease")
            .setCode(
                new CodeableConcept(new Coding(CODE_SYSTEM_SECTION_CODE, "diagnosis", "Diagnose")))
            .addEntry(new Reference(disease)));
  }

  private void addType(Composition composition) {
    if (this.type != null) {
      composition.setType(new CodeableConcept(this.type));
    }
  }

  private void addStatus(Composition composition) {
    if (isNotBlank(status)) {
      composition.setStatus(Composition.CompositionStatus.valueOf(status.toUpperCase()));
    }
  }

  private void addMetaProfile(Composition composition) {
    if (isNotBlank(profileUrl)) {
      composition.setMeta(new Meta().addProfile(profileUrl));
    }
  }

  public NotificationDiseaseDataBuilder addExtension(Extension extension) {
    if (this.extensions == null) {
      extensions = new ArrayList<>();
    }
    this.extensions.add(extension);
    return this;
  }
}
