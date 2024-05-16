package com.coremedia.blueprint.cae.sitemap;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Container for all beans needed to configure a SitemapGenerationController.
 * <p>
 * While this indirection looks unnecessarily complex for handlers with fix
 * configurations, it allows for an easy configuration switch
 * (e.g. request/site specific) in more generic handlers (namely the
 * {@link com.coremedia.blueprint.cae.sitemap.SitemapGenerationHandler}).
 */
public class SitemapSetup {
  private List<SitemapUrlGenerator> urlGenerators;
  private SitemapRendererFactory sitemapRendererFactory;

  private final CaeSitemapConfigurationProperties caeSitemapConfigurationProperties;

  public SitemapSetup(CaeSitemapConfigurationProperties caeSitemapConfigurationProperties) {
    this.caeSitemapConfigurationProperties = caeSitemapConfigurationProperties;
  }

  // --- configuration ----------------------------------------------

  public void setUrlGenerators(List<SitemapUrlGenerator> urlGenerators) {
    this.urlGenerators = urlGenerators;
  }

  public void setSitemapRendererFactory(SitemapRendererFactory sitemapRendererFactory) {
    this.sitemapRendererFactory = sitemapRendererFactory;
  }

  @PostConstruct
  void initialize() {
    if (sitemapRendererFactory == null) {
      throw new IllegalStateException("Required property not set: sitemapRendererFactory");
    }
    if (urlGenerators == null) {
      throw new IllegalStateException("Required property not set: urlGenerators");
    }
  }

  // --- features ---------------------------------------------------

  List<SitemapUrlGenerator> getUrlGenerators() {
    return urlGenerators;
  }

  SitemapRendererFactory getSitemapRendererFactory() {
    return sitemapRendererFactory;
  }

  String getProtocol() {
    return caeSitemapConfigurationProperties.getProtocol();
  }
}
