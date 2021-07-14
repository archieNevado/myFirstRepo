package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceRefAdapterCmsOnly {
  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final SitesService sitesService;
  private final CommerceSettingsHelper commerceSettingsHelper;

  public CommerceRefAdapterCmsOnly(SitesService sitesService, CommerceSettingsHelper commerceSettingsHelper) {
    this.sitesService = sitesService;
    this.commerceSettingsHelper = commerceSettingsHelper;
  }

  @SuppressWarnings("unused")
  // it is being used by within commerce-reference-schema.graphql as @fetch(from: "@commerceRefAdapter.getCommerceRef(#this)")
  @Nullable
  public CommerceRef getCommerceRef(Content content, String externalReferencePropertyName) {
    LOG.debug("Loading commerce reference data from settings for content with id {}", content.getId());
    String commerceIdStr = content.getString(externalReferencePropertyName);

    CommerceId commerceId = CommerceIdParserHelper.parseCommerceId(commerceIdStr).orElse(null);
    if (commerceId == null || commerceId.getExternalId().isEmpty()) {
      LOG.debug("externalId is null for {}", content.getId());
      return null;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.debug("no site for {} {}", content.getId(), commerceIdStr);
      return null;
    }

    // commerce connection needed for catalog alias resolution
    if (!DEFAULT_CATALOG_ALIAS.equals(commerceId.getCatalogAlias())) {
      String message = String.format("Commerce id with catalog alias cannot be resolved in cms only mode. CommerceId: %s", commerceIdStr);
      LOG.debug(message);
      throw new IllegalStateException(message);
    }

    return getCommerceRef(commerceId, List.of(), site,
            commerceSettingsHelper.getStoreId(site), CatalogId.of(commerceSettingsHelper.getCatalogId(site)));
  }

  private CommerceRef getCommerceRef(CommerceId commerceId, List<String> breadcrumb, Site site, String storeId, CatalogId catalogId) {
    return CommerceRefFactory.from(
            commerceId,
            catalogId,
            storeId,
            commerceSettingsHelper.getLocale(site),
            site.getId(),
            breadcrumb);
  }
}
