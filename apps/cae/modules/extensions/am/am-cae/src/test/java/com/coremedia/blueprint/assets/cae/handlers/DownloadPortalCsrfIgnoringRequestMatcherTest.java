package com.coremedia.blueprint.assets.cae.handlers;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.assertj.core.api.Assertions.assertThat;

class DownloadPortalCsrfIgnoringRequestMatcherTest {

  private final RequestMatcher testling = new DownloadPortalCsrfIgnoringRequestMatcher();

  @ParameterizedTest
  @ValueSource(strings = {
          "/dynamic/fragment/corporate/asset-download-portal/download-collection-overview/42",
          "/dynamic/fragment/whatever/asset-download-portal/download-collection-overview/37",
  })
  void matchesTrue(String pathInfo) {
    MockHttpServletRequest mockRequest = createMockHttpServletRequest(pathInfo);
    assertThat(testling.matches(mockRequest)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "/static/fragment/corporate/asset-download-portal/download-collection-overview/42",
          "/dynamic/foo/corporate/asset-download-portal/download-collection-overview/42",
          "/dynamic/fragment//asset-download-portal/download-collection-overview/42",
          "/dynamic/fragment/corporate/download-portal/download-collection-overview/42",
          "/dynamic/fragment/corporate/asset-download-portal/download-collection/42",
          "/dynamic/fragment/corporate/asset-download-portal/download-collection-overview/",
  })
  void matchesFalse(String pathInfo) {
    MockHttpServletRequest mockRequest = createMockHttpServletRequest(pathInfo);
    assertThat(testling.matches(mockRequest)).isFalse();
  }

  @Test
  void matchesFalseWithNull() {
    MockHttpServletRequest mockRequest = createMockHttpServletRequest(null);
    assertThat(testling.matches(mockRequest)).isFalse();
  }

  @SuppressWarnings("DuplicateStringLiteralInspection")
  private static MockHttpServletRequest createMockHttpServletRequest(@Nullable String pathInfo) {
    String requestURI = "/blueprint/servlet" + StringUtils.defaultString(pathInfo);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest(HttpMethod.GET.name(), requestURI);
    mockRequest.setPathInfo(pathInfo);
    return mockRequest;
  }
}
