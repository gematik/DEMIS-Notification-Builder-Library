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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_DISEASE;
import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.RECEPTION_TIME_STAMP_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Type;
import org.junit.jupiter.api.Test;

public final class NotificationDiseaseDataBuilderTest {

  public static NotificationDiseaseDataBuilder createCvddBuilder() {
    NotificationDiseaseDataBuilder builder = new NotificationDiseaseDataBuilder();
    builder.setDefaults();
    setCvddProfileUrl(builder);
    builder.setDate(new DateTimeType("2022-03-10"));
    return builder;
  }

  private static void setCvddProfileUrl(NotificationDiseaseDataBuilder builder) {
    builder.setProfileUrl(PROFILE_NOTIFICATION_DISEASE);
  }

  @Test
  void setDefaults_shouldSetValues() {
    Composition disease = new NotificationDiseaseDataBuilder().setDefaults().build();
    assertThat(disease.getId()).isNotEmpty();
    assertThat(disease.getIdentifier()).isNotNull();
    assertThat(disease.getIdentifier().getValue()).isNotEmpty();
    assertThat(disease.getTitle()).isEqualTo(DemisConstants.DISEASE_COMPOSITION_TITLE);
    assertThat(disease.getType()).isNotNull();
    assertThat(disease.getCategory()).isNotEmpty();
    Coding category = disease.getCategoryFirstRep().getCodingFirstRep();
    assertThat(category.getSystem()).isEqualTo(DemisConstants.CODE_SYSTEM_NOTIFICATION_TYPE);
    assertThat(category.getCode()).isEqualTo(DemisConstants.DISEASE_NOTIFICATION_TYPE_CODE);
    assertThat(disease.getStatus()).isSameAs(Composition.CompositionStatus.FINAL);
    assertThat(disease.getDate()).isNotNull();
  }

  @Test
  void setDefaults_shouldKeepValues() throws ParseException {
    String id = "init-id";
    String notificationId = "init-notification-id";
    String typeCode = "init-type-code";
    String title = "init-title";
    String categoryCode = "init-category-code";
    Composition.CompositionStatus status = Composition.CompositionStatus.AMENDED;
    Date date = new SimpleDateFormat("dd.MM.yyyy").parse("27.05.2009");
    Composition disease =
        new NotificationDiseaseDataBuilder()
            .setId(id)
            .setIdentifierAsNotificationId(notificationId)
            .setType(new Coding().setCode(typeCode))
            .setTitle(title)
            .setCategory(new Coding().setCode(categoryCode))
            .setStatus(status)
            .setDate(new DateTimeType(date))
            .setDefaults()
            .build();
    assertThat(disease.getId()).isEqualTo(id);
    assertThat(disease.getIdentifier().getValue()).isEqualTo(notificationId);
    assertThat(disease.getType().getCodingFirstRep().getCode()).isEqualTo(typeCode);
    assertThat(disease.getTitle()).isEqualTo(title);
    assertThat(disease.getCategoryFirstRep().getCodingFirstRep().getCode()).isEqualTo(categoryCode);
    assertThat(disease.getStatus()).isSameAs(status);
    assertThat(disease.getDate()).isEqualTo(date);
  }

  @Test
  void shouldCopyExtension() {
    // set up data
    Composition composition;
    PractitionerRole author;
    Patient subject;

    composition = new Composition();
    composition.setMeta(new Meta().addProfile("foobarProfile"));
    composition.setStatus(Composition.CompositionStatus.FINAL);

    author = new PractitionerRole();
    author.setId("author");

    subject = new Patient();
    subject.setId("subject");

    // relevant for test
    Type value = new DateTimeType("2022-02-19T00:00:00.000+01:00");
    composition.addExtension(new Extension(RECEPTION_TIME_STAMP_TYPE, value));

    Condition condition = new Condition();
    QuestionnaireResponse specificQuestionnaireResponse = new QuestionnaireResponse();
    Composition deepCopyComposition =
        NotificationDiseaseDataBuilder.deepCopy(
            composition, condition, subject, author, specificQuestionnaireResponse);

    assertThat(deepCopyComposition.getExtension())
        .extracting(Extension::getUrl, Extension::getValue)
        .contains(tuple(RECEPTION_TIME_STAMP_TYPE, value));
  }
}
