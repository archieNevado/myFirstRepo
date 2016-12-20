package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * A REST service to resolve shop URLs to matching target beans for PBE support on shop pages.
 */
@Named
class PbeShopUrlTargetResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PbeShopUrlTargetResolver.class);

  private static final String CATALOG_ID_QUERY_PARAM = "catalogId";
  private static final String STORE_ID_QUERY_PARAM = "storeId";
  private static final String LANG_ID_QUERY_PARAM = "langId";

  @Inject
  private SitesService sitesService;

  @Inject
  @Named("externalPageAugmentationService")
  private AugmentationService externalPageAugmentationService;

  @Nullable
  Object resolveUrl(@Nonnull String urlStr, @Nonnull String siteId) {
    URL shopUrl = getUrlFromString(urlStr);
    if (shopUrl == null) {
      return null;
    }

    CommerceConnection commerceConnection = Commerce.getCurrentConnection();
    if (commerceConnection == null) {
      return null;
    }

    // get potential partNumber from urlStr
    List<String> pathSegments = Arrays.asList(shopUrl.getPath().split("/"));
    String externalId = Iterables.getLast(pathSegments, null);

    if (isSeoUrl(shopUrl) && !StringUtils.isBlank(externalId)) {
      // try to load category from partNumber
      Category category = getCategory(commerceConnection, externalId);
      if (category != null) {
        return category;
      }

      // root document is implicitly augmented
      if (externalId.equalsIgnoreCase(commerceConnection.getStoreContext().getStoreName())) {
        Site site = sitesService.getSite(siteId);
        if (null != site) {
          return site.getSiteRootDocument();
        }
      }
    }

    // lookup CMExternalPage for externalId
    return getExternalPage(externalId, siteId);
  }

  private boolean isSeoUrl(@Nonnull URL url) {
    String query = url.getQuery();
    return !(query != null && (query.contains(CATALOG_ID_QUERY_PARAM) || query.contains(STORE_ID_QUERY_PARAM) || query.contains(LANG_ID_QUERY_PARAM)));
  }

  @Nullable
  private URL getUrlFromString(@Nonnull String urlStr) {
    try {
      return new URL(urlStr);
    } catch (MalformedURLException e) {
      LOGGER.info("URL not valid", e);
      return null;
    }
  }

  @Nullable
  private Category getCategory(@Nonnull CommerceConnection connection, @Nonnull String externalId) {
    try {
      return connection.getCatalogService().findCategoryBySeoSegment(externalId);
    } catch (CommerceException e) {
      LOGGER.warn("Cannot resolve category for SEO segment (external ID: {}).", externalId);
      return null;
    }
  }

  @Nullable
  private Content getExternalPage(@Nullable String externalId, @Nonnull String siteId) {
    return externalPageAugmentationService.getContentByExternalId(externalId, sitesService.getSite(siteId));
  }
}
