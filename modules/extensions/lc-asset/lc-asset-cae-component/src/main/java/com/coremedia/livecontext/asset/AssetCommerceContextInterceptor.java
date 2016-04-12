package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Initialize Context for asset urls (e.g. {@link ProductCatalogPictureHandler#IMAGE_URI_PATTERN})
 */
public class AssetCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  LiveContextSiteResolver liveContextSiteResolver;
  @Nullable
  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    String storeId = extractStoreId(normalizedPath);
    Locale locale = extractLocale(normalizedPath);

    return liveContextSiteResolver.findSiteFor(storeId, locale) ;
  }

  private String extractStoreId(String path) {
    return extractToken(path, 3);
  }


  private Locale extractLocale(String path) {
    String localeToken = extractToken(path, 4);
    return LocaleUtils.toLocale(localeToken);
  }

  private String extractToken(String path, int index) {
    String[] split = path.split("/");
    if (split.length != 7) {
      throw new IllegalArgumentException("Cannot handle path " + path);
    }
    return split[index];
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }
}
