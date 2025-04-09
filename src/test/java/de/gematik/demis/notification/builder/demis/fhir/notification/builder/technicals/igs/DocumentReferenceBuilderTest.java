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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.DocumentReference;
import org.junit.jupiter.api.Test;

class DocumentReferenceBuilderTest {

  private static final String ATTACHMENT_CONTENT_TYPE = "application/fasta";
  private static final String HASH = "SOMEHASH";

  @Test
  void shouldBuildDocumentReference() {
    DocumentReferenceBuilder builder = configureBuilderWithTestData();

    DocumentReference documentReference = builder.build();

    assertThat(documentReference).isNotNull();
    assertThat(documentReference.getContent()).isNotNull();
    assertThat(documentReference.getContent()).hasSize(1);
    assertThat(documentReference.getContentFirstRep()).isNotNull();
    assertThat(documentReference.getContentFirstRep().getAttachment()).isNotNull();
    assertThat((ATTACHMENT_CONTENT_TYPE))
        .isEqualTo(documentReference.getContentFirstRep().getAttachment().getContentType());
    assertThat(documentReference.getContentFirstRep().getAttachment().getHashElement()).isNotNull();
    assertThat(
            (documentReference
                .getContentFirstRep()
                .getAttachment()
                .getHashElement()
                .getValueAsString()))
        .isEqualTo(HASH);
  }

  private DocumentReferenceBuilder configureBuilderWithTestData() {
    return DocumentReferenceBuilder.builder()
        .attachmentContentType(ATTACHMENT_CONTENT_TYPE)
        .hash(HASH)
        .initialize();
  }
}
