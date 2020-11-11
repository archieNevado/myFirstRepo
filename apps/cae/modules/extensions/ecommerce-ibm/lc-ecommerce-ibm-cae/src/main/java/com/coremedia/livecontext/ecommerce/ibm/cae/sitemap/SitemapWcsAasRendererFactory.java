package com.coremedia.livecontext.ecommerce.ibm.cae.sitemap;

import com.coremedia.blueprint.cae.sitemap.SitemapRenderer;
import com.coremedia.blueprint.cae.sitemap.SitemapRendererFactory;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class SitemapWcsAasRendererFactory implements SitemapRendererFactory {
  @Override
  public SitemapRenderer createInstance() {
    return new WcsAasCrawlSitemapRenderer();
  }

  @Override
  public String getContentType() {
    return "text/html";
  }

  @Override
  public boolean absoluteUrls() {
    return true;
  }
}
