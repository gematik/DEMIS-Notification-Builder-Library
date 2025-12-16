package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.disease.questionnaire;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.FhirObjectBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.UriType;

@Setter
public class AnswerDataBuilder implements FhirObjectBuilder {

  private final List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
      new ArrayList<>();
  // primitives
  private Boolean valueBoolean;
  private BigDecimal valueDecimal;
  private Integer valueInteger;
  private String valueString;
  // time
  private DateType valueDate;
  private TimeType valueTime;
  private DateTimeType valueDateTime;
  // objects
  private String valueUri;
  private Coding valueCoding;
  private Quantity valueQuantity;
  private Reference valueReference;

  @Override
  public QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent build() {
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    addValue(answer);
    answer.setItem(this.items);
    return answer;
  }

  public AnswerDataBuilder addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    this.items.add(item);
    return this;
  }

  public AnswerDataBuilder setItems(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    this.items.clear();
    this.items.addAll(items);
    return this;
  }

  public AnswerDataBuilder setValueReference(Reference reference) {
    this.valueReference = reference;
    return this;
  }

  public AnswerDataBuilder setValueReference(Resource resource) {
    return setValueReference(new Reference(resource));
  }

  public Coding setValueCoding() {
    this.valueCoding = new Coding();
    return this.valueCoding;
  }

  public Quantity setValueQuantity() {
    this.valueQuantity = new Quantity();
    return this.valueQuantity;
  }

  /**
   * Create and set a date time you can define by the native builder
   *
   * @return date time builder
   */
  public DateTimeType setValueDateTime() {
    this.valueDateTime = new DateTimeType();
    return this.valueDateTime;
  }

  @Deprecated(since = "9.0.7")
  public AnswerDataBuilder setValueCodingYes() {
    return setValueCoding(
        new Coding("https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer", "yes", "Ja"));
  }

  public AnswerDataBuilder setValueCodingYesSnomed() {
    return setValueCoding(
        new Coding("http://snomed.info/sct", "373066001", "Yes (qualifier value)"));
  }

  @Deprecated(since = "9.0.7")
  public AnswerDataBuilder setValueCodingNo() {

    return setValueCoding(
        new Coding("https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer", "no", "Nein"));
  }

  public AnswerDataBuilder setValueCodingNoSnomed() {
    return setValueCoding(
        new Coding("http://snomed.info/sct", "373067005", "No (qualifier value)"));
  }

  /**
   * Add value to answer, if a value is set. An answer can only hold a single value.
   *
   * @param answer answer
   */
  private void addValue(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    addPrimitiveValues(answer);
    addTimeValues(answer);
    addObjectValues(answer);
  }

  private void addTimeValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (this.valueDate != null) {
      answer.setValue(this.valueDate);
    }
    if (this.valueTime != null) {
      answer.setValue(this.valueTime);
    }
    if (this.valueDateTime != null) {
      answer.setValue(this.valueDateTime);
    }
  }

  private void addObjectValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (this.valueUri != null) {
      answer.setValue(new UriType(this.valueUri));
    }
    if (this.valueCoding != null) {
      answer.setValue(this.valueCoding);
    }
    if (this.valueQuantity != null) {
      answer.setValue(this.valueQuantity);
    }
    if (this.valueReference != null) {
      answer.setValue(this.valueReference);
    }
  }

  private void addPrimitiveValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (this.valueBoolean != null) {
      answer.setValue(new BooleanType(this.valueBoolean));
    }
    if (this.valueDecimal != null) {
      answer.setValue(new DecimalType(this.valueDecimal));
    }
    if (this.valueInteger != null) {
      answer.setValue(new IntegerType(this.valueInteger));
    }
    if (this.valueString != null) {
      answer.setValue(new StringType(this.valueString));
    }
  }
}
