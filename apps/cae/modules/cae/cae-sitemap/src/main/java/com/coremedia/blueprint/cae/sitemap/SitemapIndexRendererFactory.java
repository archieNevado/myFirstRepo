package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;

public final class SitemapIndexRendererFactory implements SitemapRendererFactory {

  private final String targetDirectory;
  private final UrlPrefixResolver urlPrefixResolver;
  private final ObjectProvider<SitemapHelper> sitemapHelperProvider;

  public SitemapIndexRendererFactory(@NonNull String targetDirectory,
                                     @NonNull UrlPrefixResolver urlPrefixResolver,
                                     @NonNull ObjectProvider<SitemapHelper> sitemapHelperProvider) {
    this.targetDirectory = targetDirectory;
    this.urlPrefixResolver = urlPrefixResolver;
    this.sitemapHelperProvider = sitemapHelperProvider;
  }

  // --- SitemapRendererFactory -------------------------------------

  @Override
  public SitemapRenderer createInstance() {
    SitemapIndexRenderer sitemapIndexRenderer = new SitemapIndexRenderer();
    sitemapIndexRenderer.setTargetDirectory(targetDirectory);
    sitemapIndexRenderer.setUrlPrefixResolver(urlPrefixResolver);
    sitemapIndexRenderer.setSitemapHelper(sitemapHelperProvider.getObject());
    return sitemapIndexRenderer;
  }

  @Override
  public String getContentType() {
    // The SitemapIndexRenderer does not return the actual sitemap index
    // but a success message, so the type is not xml but plain.
    return "text/plain";
  }

  @Override
  public boolean absoluteUrls() {
    return true;
  }
}
