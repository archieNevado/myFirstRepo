package com.coremedia.livecontext.search;

import com.coremedia.cae.security.CaeCsrfIgnoringRequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class CommerceSearchCsrfIgnoringRequestMatcher implements CaeCsrfIgnoringRequestMatcher {

  public static final String IGNORE_URI_PATTERN = CommerceSearchHandler.URI_PATTERN.replaceAll("\\{\\w+}", ".+");

  @Override
  public boolean matches(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      return false;
    }

    return pathInfo.matches(IGNORE_URI_PATTERN);
  }
}
