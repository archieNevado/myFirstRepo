package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.multisite.Site;
import edu.umd.cs.findbugs.annotations.NonNull;

public class ContentBasedSitemapSetupFactory implements SitemapSetupFactory {
  private final SitemapSetupSelector sitemapSetupSelector;

  public ContentBasedSitemapSetupFactory(@NonNull SitemapSetupSelector sitemapSetupSelector) {
    this.sitemapSetupSelector = sitemapSetupSelector;
  }

  @Override
  public SitemapSetup createSitemapSetup(Site site) {
    if (!sitemapSetupSelector.isSitemapEnabled(site)) {
      throw new IllegalArgumentException("Site " + site + " is not configured for sitemap generation.  Must specify a configuration by setting \"" + SitemapHelper.SITEMAP_ORG_CONFIGURATION_KEY + "\".");
    }
    return sitemapSetupSelector.selectConfiguration(site);
  }
}
