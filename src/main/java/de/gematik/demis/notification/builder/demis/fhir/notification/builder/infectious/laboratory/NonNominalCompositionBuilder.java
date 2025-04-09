package de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory;

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

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.PROFILE_NOTIFICATION_LABORATORY_NON_NOMINAL;

import java.util.List;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;

public class NonNominalCompositionBuilder extends NotificationLaboratoryDataBuilder {

  public static Composition deepCopy(
      @Nonnull final Composition original,
      @Nonnull final PractitionerRole author,
      @Nonnull final Patient subject,
      @Nonnull final DiagnosticReport diagnosticReport) {
    final NonNominalCompositionBuilder builder = new NonNominalCompositionBuilder();
    builder.setMetaUrl(original.getMeta().getProfile().getFirst().getValueAsString());
    builder.setNotificationId(original.getId());

    final Coding codingFirstRep = original.getType().getCodingFirstRep();
    builder.setTypeCode(codingFirstRep);

    final Coding category = original.getCategoryFirstRep().getCodingFirstRep();
    builder.setCategoryCode(category);

    builder.setIdentifierSystem(original.getIdentifier().getSystem());
    builder.setIdentifierValue(original.getIdentifier().getValue());

    builder.setCompositionStatus(original.getStatus());

    builder.setTitle(original.getTitle());
    builder.setDate(original.getDate());

    List<Composition.CompositionRelatesToComponent> relatesTo = original.getRelatesTo();
    if (!relatesTo.isEmpty()) {
      final Composition.CompositionRelatesToComponent relatesToElement = relatesTo.getFirst();
      final Reference targetReference = relatesToElement.getTargetReference();
      final Identifier identifier = targetReference.getIdentifier();

      builder.setRelatesToCode(relatesToElement.getCode());
      builder.setRelatesToReferenceType(targetReference.getType());
      builder.setRelatesToNotificationId(identifier.getValue());
    }

    builder.setNotifierRole(author);
    builder.setNotifiedPerson(subject);

    builder.setLaboratoryReport(diagnosticReport);
    final Composition.SectionComponent section = original.getSectionFirstRep();
    final Coding sectionCode = section.getCode().getCodingFirstRep();
    builder.setSectionCode(sectionCode);

    return builder.build();
  }

  @Override
  protected String getDefaultProfileUrl() {
    return PROFILE_NOTIFICATION_LABORATORY_NON_NOMINAL;
  }
}
