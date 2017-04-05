package com.coremedia.ecommerce.studio.rest.filter;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;

import static com.coremedia.ecommerce.studio.rest.filter.SiteFilter.extractSiteId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SiteFilterTest {

  private ServletResponse response = null;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private SiteFilter siteFilter;

  @Test
  public void siteIsNotSetOnNonMatchingPathInfo() throws Exception {
    MockHttpServletRequest request = buildRequest("/playbackcontext/store/2338907623496/");

    siteFilter.doFilter(request, response, filterChain);

    Site siteFromRequest = SiteHelper.getSiteFromRequest(request);

    assertThat(siteFromRequest).isNull();

    verify(sitesService, never()).getSite(anyString());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void siteIsNotSetOnUnknownSiteId() throws Exception {
    MockHttpServletRequest request = buildRequest("/livecontext/store/2338907623496/");

    siteFilter.doFilter(request, response, filterChain);

    Site siteFromRequest = SiteHelper.getSiteFromRequest(request);

    assertThat(siteFromRequest).isNull();

    verify(sitesService).getSite("2338907623496");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void siteIsSetOnKnownSiteId() throws Exception {
    MockHttpServletRequest request = buildRequest("/livecontext/store/2338907623496/");

    when(sitesService.getSite("2338907623496")).thenReturn(site);

    siteFilter.doFilter(request, response, filterChain);

    Site siteFromRequest = SiteHelper.getSiteFromRequest(request);

    assertThat(siteFromRequest).isEqualTo(site);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void checkRegexWithoutSiteIsNotAcceptable() throws Exception {
    String siteId = extractSiteId("/livecontext/store/");
    assertThat(siteId).isNull();
  }

  @Test
  public void checkRegexWithoutTrailingSlashIsAcceptable() throws Exception {
    String siteId = extractSiteId("/livecontext/store/2338907623496");
    assertThat(siteId).isEqualTo("2338907623496");
  }

  @Test
  public void checkRegexWithTrailingSlashIsAcceptable() throws Exception {
    String siteId = extractSiteId("/livecontext/store/2338907623496/");
    assertThat(siteId).isEqualTo("2338907623496");
  }

  @Test
  public void checkRegexAnythingElseBehindSiteIsAcceptable() throws Exception {
    String siteId = extractSiteId("/livecontext/store/2338907623496/abcd");
    assertThat(siteId).isEqualTo("2338907623496");
  }

  private MockHttpServletRequest buildRequest(String pathInfo) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo(pathInfo);
    return request;
  }
}