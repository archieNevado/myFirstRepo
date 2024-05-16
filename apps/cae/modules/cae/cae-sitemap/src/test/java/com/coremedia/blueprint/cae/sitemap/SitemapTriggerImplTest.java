package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_INTERNAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Both methods are tested independently because generateSitemaps() has no result and it's hard to test.
 */
@ExtendWith(MockitoExtension.class)
class SitemapTriggerImplTest {

  @Mock
  private UrlPathFormattingHelper urlPathFormattingHelper;

  @Mock
  private SitesService sitesService;

  @Mock
  private SitemapSetupSelector sitemapSetupSelector;

  private SitemapTriggerImpl sitemapTrigger;

  @BeforeEach
  void setup() {
    sitemapTrigger = Mockito.spy(new SitemapTriggerImpl(sitemapSetupSelector, urlPathFormattingHelper, sitesService, 666));
  }

  @Test
  void testGenerateSitemaps() throws IOException {
    Set<Site> sites = new HashSet<>();
    Site enabledSite = createSite(true);
    Site disabledSite = createSite(false);

    sites.add(enabledSite);
    sites.add(disabledSite);
    when(sitesService.getSites()).thenReturn(sites);

    //trigger
    //just ignore generate sitemap because this logic will be tested in a separated test...
    doReturn("result").when(sitemapTrigger).generateSitemap(any(Site.class));

    sitemapTrigger.generateSitemaps();
    verify(sitemapTrigger, times(1)).generateSitemap(enabledSite);
    verify(sitemapTrigger, times(0)).generateSitemap(disabledSite);
  }

  @Test
  void testGenerateSitemap() throws IOException {
    //inputs
    Site site = mock(Site.class);
    String siteSegment = "testSegment";

    //expected results
    String expectedResult = "Sitemap has been written to /path/to/sitemap/siteId, Timestamp";

    //mocking
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    mockSegment(site, siteSegment);
    doReturn(httpClient).when(sitemapTrigger).createHttpClient();

    CloseableHttpResponse response = mockResponse(HttpServletResponse.SC_OK, expectedResult);
    HttpGetMatcher httpGetMatcher = new HttpGetMatcher("http://localhost:666/" + PREFIX_INTERNAL + "/" + siteSegment + "/sitemap-org");
    when(httpClient.execute(argThat(httpGetMatcher))).thenReturn(response);

    //action
    assertThat(sitemapTrigger.generateSitemap(site)).isEqualTo(expectedResult);
  }

  @Test
  void testGenerateSitemapFailed() throws IOException {
    //inputs
    Site site = mock(Site.class);
    String siteSegment = "testSegment";

    //mocking
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    mockSegment(site, siteSegment);
    doReturn(httpClient).when(sitemapTrigger).createHttpClient();

    CloseableHttpResponse response = mockResponse(HttpServletResponse.SC_BAD_REQUEST);
    HttpGetMatcher httpGetMatcher = new HttpGetMatcher("http://localhost:666/" + PREFIX_INTERNAL + "/" + siteSegment + "/sitemap-org");
    when(httpClient.execute(argThat(httpGetMatcher))).thenReturn(response);

    //action
    assertThatThrownBy(() -> sitemapTrigger.generateSitemap(site)).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void testGenerateSitemapNoSiteSegment() throws IOException {
    //inputs
    Site site = mock(Site.class);
    String siteSegment = null;

    //mocking
    mockSegment(site, siteSegment);

    //action
    assertThatThrownBy(() -> sitemapTrigger.generateSitemap(site)).isInstanceOf(IllegalArgumentException.class);
  }

  private CloseableHttpResponse mockResponse(int statusCode, String expectedResult) throws IOException {
    CloseableHttpResponse response = mockResponse(statusCode);
    when(response.getEntity().getContent()).thenReturn(new ByteArrayInputStream(expectedResult.getBytes()));
    return response;
  }

  private CloseableHttpResponse mockResponse(int statusCode) throws IOException {
    CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
    when(response.getStatusLine().getStatusCode()).thenReturn(statusCode);
    return response;
  }

  private void mockSegment(Site site, String siteSegment) {
    Content siteRootDocument = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(siteRootDocument);
    when(urlPathFormattingHelper.getVanityName(siteRootDocument)).thenReturn(siteSegment);
  }

  private Site createSite(boolean isEnabledForGeneration) {
    Site site = mock(Site.class);
    when(sitemapSetupSelector.isSitemapEnabled(site)).thenReturn(isEnabledForGeneration);
    return site;
  }

  private class HttpGetMatcher extends BaseMatcher<HttpGet> {

    private String expectedUrl;

    public HttpGetMatcher(String expectedUrl) {
      this.expectedUrl = expectedUrl;
    }

    @Override
    public boolean matches(Object item) {
      if (!(item instanceof HttpGet)) {
        return false;
      }

      HttpGet actual = (HttpGet) item;
      if(actual.getURI().toString().equals(expectedUrl)) {
        return true;
      }

      return false;
    }

    @Override
    public void describeTo(Description description) {
      //not implemented because its not a verify matcher, so this description is not needed
    }
  }
}
