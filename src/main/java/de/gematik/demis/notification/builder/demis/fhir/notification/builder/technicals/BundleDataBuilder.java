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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants.NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID;

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import de.gematik.demis.notification.builder.demis.fhir.notification.utils.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;

@Setter
@Slf4j
public abstract class BundleDataBuilder implements InitializableFhirObjectBuilder {

  private final List<Bundle.BundleEntryComponent> entries = new ArrayList<>();
  private final List<Bundle.BundleEntryComponent> additionalEntries = new ArrayList<>();

  /** bundle ID */
  private String id;

  /** preferably: notification bundle ID */
  private Identifier identifier;

  /** profile URLS for meta information */
  private String profileUrl;

  /** tags for meta information */
  private List<Coding> tagList = new ArrayList<>();

  private Date lastUpdated;
  private Date timestamp;
  private Bundle.BundleType type;

  /**
   * Create a full URL for a resource. Used for entries in a bundle.
   *
   * @param resource resource
   * @return full URL, like: <code>
   * <a href="https://demis.rki.de/fhir/Organization/9d6c3dcd-cba3-4fcc-9eae-6b7b19e60148">...</a>
   *     </code> or <code>
   *     urn:uuid:45e4eee9-bf7f-472f-bdd3-3d8731b71c9f</code>
   */
  public static String createFullUrl(Resource resource) {
    /*
     * Creating resources using FHIR parsers will create IdElements. Our own library doesn't do that. We first opt to look
     * at the IdType created by FHIR due to the finer access control on id elements. If that fails we fallback to look at
     * the resource for the necessary information.
     *
     * Only our builders create FHIR resources without setting the resource type. A resource obtained through other
     * means (e.g. using an object parsed from JSON/XML) will return fullUrls for resource.getId(). This will
     * lead to broken fullUrls -> e.g. https://demis.../ResourceType/ResourceType/Id.
     *
     * Changing all builders seems to risky as we don't really know who or what is relying on the ids being created.
     * However, we prefer to reference the IdType now, as that also works well when interacting with other code, e.g.
     * to create correct references.
     */
    final IdType idElement = resource.getIdElement();
    if (idElement.getValue().startsWith("urn:uuid")) {
      return idElement.getValue();
    }

    String resourceType = idElement.getResourceType();
    if (!idElement.hasResourceType() || Objects.equals(resourceType, "null")) {
      resourceType = resource.getResourceType().toString();
    }

    final String resourceId = Objects.requireNonNullElse(idElement.getIdPart(), resource.getId());
    return DemisConstants.DEMIS_RKI_DE_FHIR + resourceType + "/" + resourceId;
  }

  @Override
  public BundleDataBuilder setDefaults() {
    if (this.id == null) {
      setId(Utils.generateUuidString());
    }
    if (this.identifier == null) {
      setIdentifierAsNotificationBundleId(Utils.generateUuidString());
    }
    if (this.timestamp == null) {
      setTimestamp(Utils.getCurrentDate());
    }
    if (this.profileUrl == null) {
      setProfileUrl(getDefaultProfileUrl());
    }
    if (this.type == null) {
      setType(Bundle.BundleType.DOCUMENT);
    }
    return this;
  }

  @Override
  public final Bundle build() {
    addEntries();
    Bundle bundle = new Bundle();
    applyParameters(bundle);
    return bundle;
  }

  /** Add an entry that will be appended at the end of the Bundle when building. */
  public BundleDataBuilder addAdditionalEntry(IBaseResource resource) {
    if (resource instanceof Resource res) {
      this.additionalEntries.add(
          new Bundle.BundleEntryComponent().setFullUrl(createFullUrl(res)).setResource(res));
    } else {
      throw new IllegalArgumentException(
          "Unable to add entry of given class: " + resource.getClass().getName());
    }
    return this;
  }

  /**
   * Do NOT make this method public. If entries are added in the wrong order, they will cause
   * validation to break. We don't want to expect our users to know this.
   *
   * @param resource bundle entry resource
   * @return builder
   */
  protected BundleDataBuilder addEntry(IBaseResource resource) {
    if (resource == null) {
      return this;
    }
    if (resource instanceof Resource res) {
      this.entries.add(
          new Bundle.BundleEntryComponent().setFullUrl(createFullUrl(res)).setResource(res));
    } else {
      throw new IllegalArgumentException(
          "Unable to add entry of given class: " + resource.getClass().getName());
    }
    return this;
  }

  /**
   * Add a practitioner role as bundle entry and add the practitioner or organization as sub
   * resource bundle entry.
   *
   * @param practitionerRole practitioner role
   */
  protected void addEntryOfPractitionerRole(PractitionerRole practitionerRole) {
    if (practitionerRole != null) {
      addEntry(practitionerRole);
      if (practitionerRole.hasPractitioner()) {
        addEntry(practitionerRole.getPractitioner().getResource());
      }
      if (practitionerRole.hasOrganization()) {
        addEntry(practitionerRole.getOrganization().getResource());
      }
    }
  }

  /**
   * Do NOT make this method public. See {@link BundleDataBuilder#addEntry(IBaseResource)} for more
   * details.
   *
   * @param resources resources to create bundle entries for
   * @return builder
   */
  protected BundleDataBuilder addEntries(List<? extends IBaseResource> resources) {
    resources.forEach(this::addEntry);
    return this;
  }

  /**
   * Sets identifier as notification bundle ID
   *
   * @param notificationBundleId notification bundle ID
   * @return builder
   */
  public BundleDataBuilder setIdentifierAsNotificationBundleId(String notificationBundleId) {
    setIdentifier(
        new Identifier()
            .setSystem(NAMING_SYSTEM_NOTIFICATION_BUNDLE_ID)
            .setValue(notificationBundleId));
    return this;
  }

  /**
   * Process your domain objects to create the bundle entries you need by calling: <code>
   * void addEntry(Resource)</code>
   */
  protected abstract void addEntries();

  protected abstract String getDefaultProfileUrl();

  /**
   * Set FHIR document type
   *
   * @param type FHIR document type
   * @return builder
   */
  public BundleDataBuilder setTypeAsText(String type) {
    setType(Bundle.BundleType.fromCode(type));
    return this;
  }

  private void applyParameters(Bundle bundle) {
    bundle.setId(this.id);
    bundle.setIdentifier(this.identifier);
    bundle.setTimestamp(this.timestamp);
    bundle.setType(this.type);
    setMeta(bundle);
    bundle.setEntry(this.entries);
    additionalEntries.forEach(bundle::addEntry);
  }

  private void setMeta(Bundle bundle) {
    Meta meta = new Meta();
    if (StringUtils.isNotBlank(this.profileUrl)) {
      meta.addProfile(this.profileUrl);
    }

    meta.setTag(tagList);
    bundle.setMeta(meta.setLastUpdated(this.lastUpdated));
  }

  public BundleDataBuilder addTag(Coding coding) {
    this.tagList.add(coding);
    return this;
  }

  private static String validSuffix(String disease) {
    if ((StringUtils.length(disease) != 4) || !StringUtils.isAlpha(disease)) {
      throw new IllegalArgumentException("Not a four letter disease identifier: " + disease);
    }
    return disease.toUpperCase();
  }

  /**
   * Create disease specific URL.
   *
   * @param url URL
   * @param disease disease category code like: <code>cvdd</code>
   * @return URL
   */
  public static String createDiseaseSpecificUrl(String url, String disease) {
    return url + validSuffix(disease);
  }
}
