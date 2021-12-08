package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SitemapHelperTest {
  private static final String SITE_ID = "abc123";
  private static final String URL_PREFIX = "https://acme.com";

  @Mock
  private SettingsService settingsService;

  @Mock
  private UrlPrefixResolver urlPrefixResolver;

  @Mock
  private Site site;

  private SitemapHelper testling;

  @BeforeEach
  public void setup() {
    when(site.getId()).thenReturn(SITE_ID);
    when(urlPrefixResolver.getUrlPrefix(SITE_ID, null, null)).thenReturn(URL_PREFIX);
    testling = new SitemapHelper(settingsService, urlPrefixResolver, "", "");
  }


  // --- tests ------------------------------------------------------

  @Test
  void testSitemapIndexUrl() {
    String indexUrl = testling.sitemapIndexUrl(site);
    assertOnlyOneSegment(indexUrl);
  }

  @Test
  void testSitemapIndexEntryUrl() {
    String indexUrl = testling.sitemapIndexEntryUrl(site, "sitemap1.xml.gz");
    assertOnlyOneSegment(indexUrl);
  }


  // --- internal ---------------------------------------------------

  /**
   * See com.coremedia.blueprint.cae.sitemap.SitemapHandler.URI_PATTERN_SITEMAP
   * for an explanation why our sitemap URLs must not have a path.
   */
  private void assertOnlyOneSegment(String indexUrl) {
    assertTrue(indexUrl.startsWith(URL_PREFIX+"/"));
    assertEquals(URL_PREFIX.length(), indexUrl.lastIndexOf('/'), "Sitemap index URL '" + indexUrl + "' has a path.");
  }
}
