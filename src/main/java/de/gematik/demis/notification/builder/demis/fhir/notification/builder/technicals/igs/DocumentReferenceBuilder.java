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

import lombok.Builder;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.DocumentReference;

/** Builder for the entry DocumentReference in IGS. */
@Builder(buildMethodName = "initialize")
public class DocumentReferenceBuilder {

  private String attachmentContentType;
  private String hash;

  /**
   * Generates the DocumentReference object.
   *
   * @return The DocumentReference object.
   */
  public DocumentReference build() {
    DocumentReference documentReference = new DocumentReference();
    DocumentReference.DocumentReferenceContentComponent content =
        new DocumentReference.DocumentReferenceContentComponent();
    Attachment attachment = new Attachment();
    attachment.setContentType(attachmentContentType);
    attachment.setHashElement(new Base64BinaryType(hash));
    content.setAttachment(attachment);
    documentReference.addContent(content);
    return documentReference;
  }
}
