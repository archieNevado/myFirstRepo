package com.coremedia.ecommerce.studio.rest.filter;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inject site into request context.
 * <p>
 * This should make the commerce connection (injected by a follow-up filter) available in Studio.
 */
public class SiteFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(SiteFilter.class);

  private static final Pattern SITE_ID_URL_PATTERN = Pattern.compile("/livecontext/.+?/(.+?)((/.*)|$)");

  @Inject
  @SuppressWarnings("squid:S3306") //squid:S3306 Constructor injection should be used instead of field injection
  private SitesService sitesService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // not needed
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    Site site = getSite(request);

    if (site != null) {
      SiteHelper.setSiteToRequest(site, request);
    }

    chain.doFilter(request, response);
  }

  @Nullable
  private Site getSite(@Nonnull ServletRequest request) {
    String pathInfo = getPathInfo(request);

    if (pathInfo == null) {
      return null;
    }

    String siteId = extractSiteId(pathInfo);

    if (siteId == null) {
      return null;
    }

    return findSiteById(siteId);
  }

  @Nullable
  private static String getPathInfo(@Nonnull ServletRequest request) {
    if (!(request instanceof HttpServletRequest)) {
      return null;
    }

    return ((HttpServletRequest) request).getPathInfo();
  }

  @Nullable
  @VisibleForTesting
  static String extractSiteId(@Nonnull CharSequence pathInfo) {
    Matcher matcher = SITE_ID_URL_PATTERN.matcher(pathInfo);

    if (!matcher.matches()) {
      return null;
    }

    String siteId = matcher.group(1);

    if (siteId == null) {
      LOG.debug("Unable to extract site ID from URL path info '{}'.", pathInfo);
    }

    return siteId;
  }

  @Nullable
  private Site findSiteById(@Nonnull String siteId) {
    Site site = sitesService.getSite(siteId);

    if (site == null) {
      LOG.debug("Unknown site ID '{}'.", siteId);
      return null;
    }

    return site;
  }

  @Override
  public void destroy() {
    // not needed
  }
}
