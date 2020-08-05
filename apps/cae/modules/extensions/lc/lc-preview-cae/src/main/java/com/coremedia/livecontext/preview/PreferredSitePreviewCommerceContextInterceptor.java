package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Creates the store context for preview urls containing studioPreferredSite parameter.
 */
public class PreferredSitePreviewCommerceContextInterceptor extends AbstractCommerceContextInterceptor {
  private static final String STUDIO_PREFERRED_SITE_PARAM = "studioPreferredSite";
  private SitesService sitesService;
  private String queryParam = STUDIO_PREFERRED_SITE_PARAM;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String queryString = request.getQueryString();
    if (StringUtils.isEmpty(queryString) || !request.getParameterMap().containsKey(queryParam)) {
      return true;
    }
    return super.preHandle(request, response, handler);
  }

  @NonNull
  @Override
  protected Optional<Site> findSite(HttpServletRequest request, String normalizedPath) {
    Site site = null;

    String[] siteIds = request.getParameterMap().get(queryParam);
    if (siteIds != null && siteIds.length == 1) {
      site = sitesService.getSite(siteIds[0]);
    }

    return Optional.ofNullable(site);
  }

  @Required
  public void setSitesService(SitesService siteService) {
    this.sitesService = siteService;
  }

  public void setQueryParam(String queryParam) {
    this.queryParam = queryParam;
  }
}
