package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Initializes the commerce connection for the requests site (if any).
 *
 * @see SiteHelper
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class CommerceConnectionFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceConnectionFilter.class);

  @Autowired
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Site site = SiteHelper.getSiteFromRequest(request);
    try {
      commerceConnectionInitializer.init(site);
    } catch (NoCommerceConnectionAvailable noCommerceConnectionAvailable) {
      LOG.debug("no commerce connection available for site {}", site);
    }
    try {
      chain.doFilter(request, response);
    } finally {
      Commerce.clearCurrent();
    }
  }

  @Override
  public void destroy() {

  }

}
