package com.coremedia.livecontext.preview;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdProvider;
import com.coremedia.id.IdScheme;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreviewCommerceContextInterceptorTest {

  private PreviewCommerceContextInterceptor interceptor;

  @Mock
  private LiveContextSiteResolver sitesResolver;
  @Mock
  private SitesService sitesService;
  @Mock
  private IdScheme idScheme;
  @Mock
  private Site abcSite;
  @Mock
  private Content content;

  private final IdProvider idProvider = new IdProvider();
  private final MockHttpServletRequest request = new MockHttpServletRequest();

  @BeforeEach
  void setup() {
    idProvider.setSchemes(List.of(idScheme));
    interceptor = new PreviewCommerceContextInterceptor(sitesService, idProvider);
    interceptor.setSiteResolver(sitesResolver);
  }

  @Test
  void testWithNumericContentId() {
    request.addParameter("id", "123");

    when(idScheme.parseId("coremedia:///cap/content/123")).thenReturn(content);
    when(content.getId()).thenReturn("coremedia:///cap/content/123");
    when(sitesResolver.findSiteForContentId(123)).thenReturn(abcSite);

    Optional<Site> site = interceptor.findSite(request, "ignoredPath");
    assertThat(site).contains(abcSite);

    verify(sitesResolver).findSiteForContentId(123);
  }

  @Test
  void testWithNormalizedContentId() {
    request.addParameter("id", "coremedia:///cap/content/123");

    when(idScheme.parseId("coremedia:///cap/content/123")).thenReturn(content);
    when(content.getId()).thenReturn("coremedia:///cap/content/123");
    when(sitesResolver.findSiteForContentId(123)).thenReturn(abcSite);

    Optional<Site> site = interceptor.findSite(request, "ignoredPath");
    assertThat(site).contains(abcSite);

    verify(sitesResolver).findSiteForContentId(123);
  }

  @Test
  void testWithSiteId() {
    when(sitesService.getSite("abc")).thenReturn(abcSite);
    request.addParameter("site", "abc");

    Optional<Site> site = interceptor.findSite(request, "ignoredPath");
    assertThat(site).contains(abcSite);
  }

  @Test
  void testWithElasticSocialId() {
    request.addParameter("id", "es:comment:539ae297e4b0971a9a345115");

    Optional<Site> site = interceptor.findSite(request, "ignoredPath");
    assertThat(site).isEmpty();

    verify(sitesResolver, never()).findSiteForContentId(anyInt());
  }
}
