package com.coremedia.lc.studio.lib.rest;

/**
 * A REST service to resolve shop urls to matching target beans for pbe support on shop pages.
 */

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.rest.linking.LocationHeaderResourceFilter;
import com.google.common.collect.Iterables;
import com.sun.jersey.spi.container.ResourceFilters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/urlService")
@Named
public class PbeShopUrlTargetResolver {
  private static final Logger LOGGER = LoggerFactory.getLogger(PbeShopUrlTargetResolver.class);
  private static final String CATALOG_ID_QUERY_PARAM = "catalogId";
  private static final String STORE_ID_QUERY_PARAM = "storeId";
  private static final String LANG_ID_QUERY_PARAM = "langId";
  private static final String SHOP_URL_PBE_PARAM = "shopUrl";
  private static final String SITE_ID_PBE_PARAM = "siteId";

  @Inject
  private CommerceConnectionInitializer commerceConnectionInitializer;
  @Inject
  private SitesService sitesService;
  @Inject
  @Named("externalPageAugmentationService")
  private AugmentationService externalPageAugmentationService;


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @ResourceFilters(value = {LocationHeaderResourceFilter.class})
  public Object resolveUrlTargetBean(final Map<String, Object> rawJson) {
    String shopUrlStr = (String) rawJson.get(SHOP_URL_PBE_PARAM);
    String siteId = (String) rawJson.get(SITE_ID_PBE_PARAM);

    Object value = resolveUrl(shopUrlStr, siteId);
    LOGGER.info("resolved {} (site {}) to {}", shopUrlStr, siteId, value);
    return Collections.singletonMap("bean", value);
  }

  Object resolveUrl(@Nonnull String urlStr, @Nonnull String siteId) {
    URL shopUrl = getUrlFromString(urlStr);
    commerceConnectionInitializer.init(siteId);
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    if (currentConnection != null && shopUrl != null) {
      //get potential partNumber from urlStr
      String externalId = Iterables.getLast(Arrays.asList(shopUrl.getPath().split("/")), null);
      if (isSeoUrl(shopUrl) && !StringUtils.isBlank(externalId)) {
        //try to load category from partNumber
        Category category = getCategory(externalId);
        if (category != null) {
          return category;
        }
        // root document is implicitly augmented
        if(externalId.equalsIgnoreCase(currentConnection.getStoreContext().getStoreName())) {
          Site site = sitesService.getSite(siteId);
          if(null != site) {
            return site.getSiteRootDocument();
          }
        }
      }
      //lookup CMExternalPage for externalId
      Content externalPage = getExternalPage(externalId, siteId);
      if (externalPage != null) {
        return externalPage;
      }
    }
    return null;
  }

  private boolean isSeoUrl(URL url) {
    String query = url.getQuery();
    return !(query != null && (query.contains(CATALOG_ID_QUERY_PARAM) || query.contains(STORE_ID_QUERY_PARAM) || query.contains(LANG_ID_QUERY_PARAM)));
  }

  private URL getUrlFromString(String urlStr) {
    URL url = null;
    try {
      url = new URL(urlStr);
    } catch (MalformedURLException e) {
      LOGGER.info("URL not valid", e);
    }
    return url;
  }

  @Nullable
  private Category getCategory(@Nonnull String externalId) {
    try {
      return Commerce.getCurrentConnection().getCatalogService().findCategoryBySeoSegment(externalId);
    } catch (CommerceException e) {
      LOGGER.warn("cannot resolve category for seo segment", externalId);
    }
    return null;
  }

  private Content getExternalPage(String externalId, String siteId) {
    return externalPageAugmentationService.getContentByExternalId(externalId, sitesService.getSite(siteId));
  }

}
