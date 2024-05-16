package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

/**
 * Selects the {@link SitemapSetup} for a {@link Site}
 */
public class SitemapSetupSelector {

  /**
   * The configurations for different sites.
   * <p>
   * Each site must specify its suitable sitemap configuration by a setting
   * "sitemapOrgConfiguration" whose value is one of the keys of this map.
   * Typically, there will be one entry for each web presence (e.g. corporate,
   * livecontext), and all the multi language sites will share the
   * configuration.
   */
  private final Map<String, SitemapSetup> sitemapConfigurations;
  private final SettingsService settingsService;
  private final SitemapHelper sitemapHelper;

  public SitemapSetupSelector(@NonNull Map<String, SitemapSetup> sitemapConfigurations,
                              @NonNull SettingsService settingsService,
                              @NonNull SitemapHelper sitemapHelper) {
    this.sitemapConfigurations = sitemapConfigurations;
    this.settingsService = settingsService;
    this.sitemapHelper = sitemapHelper;
  }

  @NonNull
  public SitemapSetup selectConfiguration(Site site) {
    String configKey = settingsService.setting(SitemapHelper.SITEMAP_ORG_CONFIGURATION_KEY, String.class, site);
    if (configKey==null) {
      throw new IllegalArgumentException("Site " + site + " is not configured for sitemap generation.  Must specify a configuration by setting \"" + SitemapHelper.SITEMAP_ORG_CONFIGURATION_KEY + "\".");
    }
    SitemapSetup sitemapConfiguration = sitemapConfigurations.get(configKey);
    if (sitemapConfiguration==null) {
      throw new IllegalStateException("No such sitemap configuration: " + configKey);
    }
    return sitemapConfiguration;
  }

  public boolean isSitemapEnabled(Site site) {
    return sitemapHelper.isSitemapEnabled(site);
  }

}
