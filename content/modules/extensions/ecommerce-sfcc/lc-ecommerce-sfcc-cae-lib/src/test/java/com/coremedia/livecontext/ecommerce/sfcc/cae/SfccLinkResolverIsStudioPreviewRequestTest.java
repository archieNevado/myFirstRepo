package com.coremedia.livecontext.ecommerce.sfcc.cae;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SfccLinkResolverIsStudioPreviewRequestTest {

  @ParameterizedTest
  @CsvSource({
          "false, false, false, false",
          "true , false, false, true ",
          "false, true , false, true ",
          "false, false, true , true ",
          "true , true , false, true ",
          "false, true , true , true ",
          "true , true , true , true ",
  })
  void isStudioPreviewRequest(boolean isStudioPreview, boolean isP13nTest, boolean isPreview, boolean expected) {
    HttpServletRequest request = buildRequest(isStudioPreview, isP13nTest, isPreview);

    assertThat(SfccLinkResolver.isStudioPreviewRequest(request)).isEqualTo(expected);
  }

  private static HttpServletRequest buildRequest(boolean isStudioPreview, boolean isP13nTest, boolean isPreview) {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getAttribute("isStudioPreview")).thenReturn(String.valueOf(isStudioPreview));
    when(request.getParameter("p13n_test")).thenReturn(String.valueOf(isP13nTest));
    when(request.getParameter("preview")).thenReturn(String.valueOf(isPreview));

    return request;
  }
}
