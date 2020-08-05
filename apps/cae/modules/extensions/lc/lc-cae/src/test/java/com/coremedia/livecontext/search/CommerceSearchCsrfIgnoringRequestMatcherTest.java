package com.coremedia.livecontext.search;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.assertj.core.api.Assertions.assertThat;

class CommerceSearchCsrfIgnoringRequestMatcherTest {

  private final RequestMatcher testling = new CommerceSearchCsrfIgnoringRequestMatcher();

  @ParameterizedTest
  @ValueSource(strings = {
          "/dynamic/calista/shopsearch",
          "/dynamic/whatever/shopsearch",
  })
  void matchesTrue(String pathInfo) {
    MockHttpServletRequest mockRequest = createMockHttpServletRequest(pathInfo);
    assertThat(testling.matches(mockRequest)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "/static/calista/shopsearch",
          "/dynamic//shopsearch",
          "/dynamic/calista/search",
          "/dynamic/calista/shop",
          "/dynamic/calista/",
          "/calista/shopsearch",
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
