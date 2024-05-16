package com.coremedia.blueprint.cae.filter;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteHelper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Determines the {@link Site} for a request and stores it in the {@link javax.servlet.ServletRequest}.
 */
public class SiteFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(SiteFilter.class);

  private SiteResolver siteResolver;

  public void setSiteResolver(SiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  @PostConstruct
  protected void initialize() {
    if (siteResolver == null) {
      throw new IllegalStateException("Required property not set: siteResolver");
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      String pathInfo = httpServletRequest.getPathInfo();
      try {
        if (Strings.isNullOrEmpty(pathInfo)) {
          LOG.info("Could not determine a site without a path info in request {}", request);
        } else {
          Site site = siteResolver.findSiteByPath(pathInfo);
          SiteHelper.setSiteToRequest(site, request);
        }
      } catch (Exception e) {
        LOG.warn("Could not determine the site for the request", e);
      }
    }
    chain.doFilter(request, response);
  }

}
