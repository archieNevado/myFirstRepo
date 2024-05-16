package com.coremedia.blueprint.assets.cae.handlers;

import com.coremedia.cae.security.CaeCsrfIgnoringRequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class DownloadPortalCsrfIgnoringRequestMatcher implements CaeCsrfIgnoringRequestMatcher {

  public static final String IGNORE_URI_PATTERN =
          DownloadPortalHandler.PATTERN_DOWNLOAD_COLLECTION_OVERVIEW.replaceAll("\\{\\w+}", ".+");

  @Override
  public boolean matches(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      return false;
    }

    return pathInfo.matches(IGNORE_URI_PATTERN);
  }
}
