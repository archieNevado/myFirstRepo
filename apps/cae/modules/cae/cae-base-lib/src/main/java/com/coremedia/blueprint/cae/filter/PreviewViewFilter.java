package com.coremedia.blueprint.cae.filter;

import com.coremedia.blueprint.base.links.UriConstants;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Reject requests for preview specific views on Live CAEs.
 * <p>
 * A view is preview specific iff it contains "Preview".
 */
public class PreviewViewFilter extends OncePerRequestFilter implements InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(PreviewViewFilter.class);
  private static final String PREVIEW = "Preview";

  private final boolean isLive;

  public PreviewViewFilter(boolean isLive) {
    this.isLive = isLive;
  }

// --- configuration ----------------------------------------------

  @Override
  public void afterPropertiesSet() {
    if (isLive) {
      LOG.info("Activated PreviewViewFilter for this Live CAE.  Requests for preview views will be denied.");
    }
  }


  // --- Filter ---------------------------------------------------------------

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String requestView = request.getParameter(UriConstants.RequestParameters.VIEW_PARAMETER);
    boolean accept = !isLive || isLiveView(requestView);
    if (accept) {
      filterChain.doFilter(request, response);
    } else {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
  }


  // --- internal ---------------------------------------------------

  @VisibleForTesting
  boolean isLiveView(String view) {
    return view==null || !view.contains(PREVIEW);
  }

}
