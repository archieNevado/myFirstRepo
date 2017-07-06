package com.coremedia.livecontext.preview;

import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Add request attribute "hasPreviewToken" for the request with query parameter "previewToken"
 */
public class PreviewTokenMarkerFilter implements Filter {

  private static final String WC_P13N_TEST = "wc.p13n_test";
  public static final String WC_PREVIEW_MODE_ENABLED = "wc.preview.mode.enabled";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request.getParameterMap().containsKey(PreviewTokenAppendingLinkTransformer.QUERY_PARAMETER_PREVIEW_TOKEN) || isPreviewFragmentRequest(request)) {
      request.setAttribute(LiveContextPageHandlerBase.HAS_PREVIEW_TOKEN, true);
    }
    chain.doFilter(request, response);
  }

  private boolean isPreviewFragmentRequest(ServletRequest request) {
    if(request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String wcInPreviewMode = httpRequest.getHeader(WC_PREVIEW_MODE_ENABLED);
      String p13nTest = httpRequest.getHeader(WC_P13N_TEST);

      //p13nTest check is only for backwards compatibility. wcInPreviewMode means the wcs runs in preview mode.
      return "true".equals(p13nTest) || "true".equals(wcInPreviewMode);
    }
    return false;
  }

  @Override
  public void destroy() {
  }
}
