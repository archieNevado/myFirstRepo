package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.invoke.MethodHandles.lookup;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Some sitemap features needed by various classes.
 */
public class SitemapHelper {
  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  static final String SITEMAP_ORG = "sitemap-org";
  static final String SITEMAP_ORG_CONFIGURATION_KEY = "sitemapOrgConfiguration";
  static final String FILE_PREFIX = "sitemap";
  static final String SITEMAP_INDEX_FILENAME = FILE_PREFIX + "_index.xml";

  private final SettingsService settingsService;
  private final UrlPrefixResolver urlPrefixResolver;
  private final String baseUri;
  private final String protocol;

  public SitemapHelper(@NonNull SettingsService settingsService,
                       @NonNull UrlPrefixResolver urlPrefixResolver,
                       @NonNull String baseUri,
                       @NonNull String protocol) {
    this.settingsService = settingsService;
    this.urlPrefixResolver = urlPrefixResolver;
    this.baseUri = baseUri;
    this.protocol = protocol;
  }

  public boolean isSitemapEnabled(Site site) {
    boolean want = settingsService.setting(SITEMAP_ORG_CONFIGURATION_KEY, String.class, site) != null;
    boolean can = urlPrefixResolver.getUrlPrefix(site.getId(), null, null) != null;
    if (want && !can) {
      LOG.warn("Site {} is sitemap-enabled but has no URL prefix. Sitemap generation would fail.", site);
    }
    return want && can;
  }

  public String sitemapIndexUrl(Site site) {
    StringBuilder stringBuilder = buildSitemapUrlPrefix(site);
    stringBuilder.append(SITEMAP_INDEX_FILENAME);
    return stringBuilder.toString();
  }

  public String sitemapIndexEntryUrl(Site site, String sitemapFilename) {
    StringBuilder stringBuilder = buildSitemapUrlPrefix(site);
    stringBuilder.append(sitemapFilename);
    return stringBuilder.toString();
  }

  protected StringBuilder buildSitemapUrlPrefix(Site site) {
    String urlPrefix = urlPrefixResolver.getUrlPrefix(site.getId(), null, null);
    if (urlPrefix == null) {
      throw new IllegalStateException("Cannot determine URL prefix for site " + site.getId());
    }

    if (!isBlank(protocol)) {
      UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(urlPrefix);
      uriComponentsBuilder.scheme(protocol);
      urlPrefix = uriComponentsBuilder.build().toUriString();
    }

    StringBuilder sb = new StringBuilder(urlPrefix);
    if (!isBlank(baseUri)) {
      sb.append(baseUri);
    }
    sb.append(SitemapHandler.SERVICE_SITEMAP_PREFIX);
    sb.append(site.getId());
    sb.append("-");
    return sb;
  }
}
