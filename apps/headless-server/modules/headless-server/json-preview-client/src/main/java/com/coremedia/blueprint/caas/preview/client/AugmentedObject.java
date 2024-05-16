package com.coremedia.blueprint.caas.preview.client;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.Set;

@DefaultAnnotation(NonNull.class)
class AugmentedObject {

  private static final Set<String> AUGMENTED_CONTENTTYPE_NAMES = Set.of("CMExternalChannel", "CMExternalProduct");

  private final String externalId;
  private final String siteId;

  AugmentedObject(String externalId, String siteId) {
    this.externalId = externalId;
    this.siteId = siteId;
  }

  String getExternalId() {
    return externalId;
  }

  String getSiteId() {
    return siteId;
  }

  static Optional<AugmentedObject> of(String contentId, ContentRepository contentRepository, SitesService sitesService) {
    Content content = contentRepository.getContent(contentId);
    if (content == null) {
      return Optional.empty();
    }
    if (!AUGMENTED_CONTENTTYPE_NAMES.contains(content.getType().getName())) {
      return Optional.empty();
    }
    String externalId = content.getString("externalId");
    if (StringUtils.isBlank(externalId)) {
      return Optional.empty();
    }
    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site == null) {
      return Optional.empty();
    }
    return Optional.of(new AugmentedObject(externalId, site.getId()));
  }
}
