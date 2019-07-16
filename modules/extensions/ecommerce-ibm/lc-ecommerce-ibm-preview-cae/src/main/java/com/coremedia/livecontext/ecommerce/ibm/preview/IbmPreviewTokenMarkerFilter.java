package com.coremedia.livecontext.ecommerce.ibm.preview;

import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Add request attribute {@link LiveContextPageHandlerBase#HAS_PREVIEW_TOKEN} for the request if
 * {@link PreviewTokenAppendingLinkTransformer#QUERY_PARAMETER_PREVIEW_TOKEN} was added by ibm commerce preview.
 */
public class IbmPreviewTokenMarkerFilter  implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request.getParameterMap().containsKey(PreviewTokenAppendingLinkTransformer.QUERY_PARAMETER_PREVIEW_TOKEN)) {
      request.setAttribute(LiveContextPageHandlerBase.HAS_PREVIEW_TOKEN, true);
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }
}